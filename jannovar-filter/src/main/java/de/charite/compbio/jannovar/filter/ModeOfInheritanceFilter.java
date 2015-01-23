package de.charite.compbio.jannovar.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.annotation.AnnotationList;
import de.charite.compbio.jannovar.impl.intervals.Interval;
import de.charite.compbio.jannovar.impl.intervals.IntervalArray;
import de.charite.compbio.jannovar.impl.intervals.IntervalEndExtractor;
import de.charite.compbio.jannovar.io.Chromosome;
import de.charite.compbio.jannovar.io.JannovarData;
import de.charite.compbio.jannovar.io.ReferenceDictionary;
import de.charite.compbio.jannovar.pedigree.CompatibilityCheckerException;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.GenotypeList;
import de.charite.compbio.jannovar.pedigree.GenotypeListBuilder;
import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.PedigreeDiseaseCompatibilityDecorator;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.reference.GenomeChange;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * A {@link VariantContext} filter that only includes variants that are compatible to a given {@link ModeOfInheritance}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class ModeOfInheritanceFilter implements VariantContextFilter {

	/** Deserialized Jannovar data */
	private final JannovarData jannovarDB;
	/** Pedigree to use for the compatibility check. */
	private final Pedigree pedigree;
	/** The mode of inheritance to filter for */
	private final ModeOfInheritance modeOfInheritance;
	/** List of genes, indexed by interval tree */
	private final GeneList geneList;
	/** Names of {@link pedigree#members}. */
	private final ImmutableList<String> personNames;
	/** Next filter. */
	private final VariantContextFilter next;
	/** Currently active genes and variants assigned to them. */
	HashMap<Gene, GenotypeListBuilder> activeGenes = new HashMap<Gene, GenotypeListBuilder>();
	/** Currently buffered variants. */
	HashMap<FlaggedVariant, FlaggedVariantCounter> activeVariants = new HashMap<FlaggedVariant, FlaggedVariantCounter>();

	/** Initialize */
	public ModeOfInheritanceFilter(Pedigree pedigree, JannovarData jannovarDB, ModeOfInheritance modeOfInheritance,
			VariantContextFilter next) {
		this.pedigree = pedigree;
		this.jannovarDB = jannovarDB;
		this.modeOfInheritance = modeOfInheritance;
		this.geneList = buildGeneList();
		this.next = next;

		ImmutableList.Builder<String> namesBuilder = new ImmutableList.Builder<String>();
		for (Person p : pedigree.members)
			namesBuilder.add(p.name);
		this.personNames = namesBuilder.build();
	}

	private GeneList buildGeneList() {
		// create one GeneBuilder for each gene, collect all transcripts for the gene
		HashMap<String, GeneBuilder> geneMap = new HashMap<String, GeneBuilder>();
		for (Chromosome chrom : jannovarDB.chromosomes.values())
			for (Interval<TranscriptModel> itv : chrom.tmIntervalTree.intervals) {
				TranscriptModel tm = itv.value;
				if (!geneMap.containsKey(tm.geneSymbol))
					geneMap.put(tm.geneSymbol, new GeneBuilder(jannovarDB.refDict, tm.geneSymbol));
				geneMap.get(tm.geneSymbol).addTranscriptModel(tm);
			}

		// construct GeneList from geneMap
		ImmutableList.Builder<Gene> builder = new ImmutableList.Builder<Gene>();
		for (GeneBuilder gene : geneMap.values())
			builder.add(gene.build());
		return new GeneList(builder.build());
	}

	@Override
	public void put(FlaggedVariant vc) throws FilterException {
		final ReferenceDictionary refDict = jannovarDB.refDict;
		// TODO(holtgrew): for now, we simply ignore variants on contigs unknown to us, this has to be fixed
		if (!refDict.contigID.containsKey(vc.vc.getChr()))
			return;
		final int contigID = refDict.contigID.get(vc.vc.getChr());
		IntervalArray<Gene> iTree = geneList.gIntervalTree.get(contigID);

		// Get shortcuts to ref, alt, and position. Note that this is "uncorrected" data, common prefixes etc. are
		// stripped when constructing the GenomeChange.
		ArrayList<AnnotationList> annoLists = new ArrayList<AnnotationList>();
		final int altCount = vc.vc.getAlternateAlleles().size();
		for (int alleleID = 0; alleleID < altCount; ++alleleID) {
			final String ref = vc.vc.getReference().getBaseString();
			final String alt = vc.vc.getAlternateAllele(alleleID).getBaseString();
			final int pos = vc.vc.getStart();
			// Construct GenomeChange from this and strip common prefixes.
			final GenomeChange change = new GenomeChange(new GenomePosition(refDict, '+', contigID, pos,
					PositionType.ONE_BASED), ref, alt);

			// Query the gene interval tree for overlapping genes.
			final GenomeInterval changeInterval = change.getGenomeInterval();
			IntervalArray<Gene>.QueryResult qr;
			if (changeInterval.length() == 0)
				qr = iTree.findOverlappingWithPoint(changeInterval.beginPos);
			else
				qr = iTree.findOverlappingWithInterval(changeInterval.beginPos, changeInterval.endPos);

			for (Gene gene : qr.entries)
				putVariantForGene(vc, gene);
		}

		// write out all variants left of variant
		markDoneGenes(contigID, vc.vc.getStart() - 1);
	}

	private void markDoneGenes(int contigID, int pos) throws FilterException {
		ArrayList<Gene> doneGenes = new ArrayList<Gene>();
		for (Map.Entry<Gene, GenotypeListBuilder> entry : activeGenes.entrySet()) {
			Gene gene = entry.getKey();
			if (gene.region.chr != contigID)
				doneGenes.add(gene);
			else if (gene.region.endPos <= pos)
				doneGenes.add(gene);
		}

		for (Gene gene : doneGenes) {
			activeGenes.remove(gene);
			processedGene(gene);
		}
	}

	/**
	 * Register {@link FlaggedVariant} as active for the given gene.
	 */
	private void putVariantForGene(FlaggedVariant vc, Gene gene) {
		// register variant as active
		if (!activeVariants.containsKey(vc))
			activeVariants.put(vc, new FlaggedVariantCounter(vc, 1));
		else
			activeVariants.get(vc).count += 1;

		// create new GenotypeListBuilder for the gene if necessary
		if (!activeGenes.containsKey(gene))
			activeGenes.put(gene, new GenotypeListBuilder(gene.name, gene.region, personNames));

		// register Genotypes for vc
		putGenotypes(vc, gene);
	}

	private void putGenotypes(FlaggedVariant fv, Gene gene) {
		final VariantContext vc = fv.vc;
		for (int i = 0; i < vc.getAlternateAlleles().size(); ++i) {
			Allele currAlt = vc.getAlternateAllele(i);

			ImmutableList.Builder<Genotype> builder = new ImmutableList.Builder<Genotype>();
			for (int pID = 0; pID < personNames.size(); ++pID) {
				htsjdk.variant.variantcontext.Genotype gt = vc.getGenotype(personNames.get(pID));
				if (gt.getAlleles().size() != 2)
					throw new RuntimeException("Unexpected allele count: " + gt.getAlleles().size());

				// we consider everything non-ALT (for current alternative allele) to be REF
				final boolean isRef0 = !gt.getAllele(0).getBaseString().equals(currAlt.getBaseString());
				final boolean isRef1 = !gt.getAllele(0).getBaseString().equals(currAlt.getBaseString());

				// TODO(holtgrem): Handle case of symbolic alleles
				if (gt.getAllele(0).isNoCall() || gt.getAllele(1).isNoCall())
					builder.add(Genotype.NOT_OBSERVED);
				else if (isRef0 && isRef1)
					builder.add(Genotype.HOMOZYGOUS_REF);
				else if (!isRef0 && !isRef1)
					builder.add(Genotype.HOMOZYGOUS_ALT);
				else
					builder.add(Genotype.HETEROZYGOUS);
			}
			activeGenes.get(gene).addGenotypes(builder.build());
		}
	}

	@Override
	public void finish() throws FilterException {
		// perform a final round of tests on all currently active genes
		PedigreeDiseaseCompatibilityDecorator checker = new PedigreeDiseaseCompatibilityDecorator(pedigree);
		ArrayList<Gene> doneGenes = new ArrayList<Gene>();
		for (Map.Entry<Gene, GenotypeListBuilder> entry : activeGenes.entrySet()) {
			GenotypeList lst = entry.getValue().build();
			try {
				if (checker.isCompatibleWith(lst, modeOfInheritance))
					markVariantsInGeneAsCompatible(entry.getKey());
				doneGenes.add(entry.getKey());
			} catch (CompatibilityCheckerException e) {
				throw new FilterException("Problem in mode of inheritance filter: " + e.getMessage());
			}
		}
		// mark gene as done
		for (Gene gene : doneGenes)
			processedGene(gene);

		// there should be no more active variants or genes
		if (activeVariants.isEmpty())
			throw new RuntimeException("all variants should be inactive now");
		if (activeGenes.isEmpty())
			throw new RuntimeException("all genes should be inactive now");
	}

	/**
	 * Mark currently buffered variants that are located in <code>gene</code> as "included".
	 *
	 * Called when we found out that the variants in <code>gene</code> are compatible with {@link #modeOfInheritance}.
	 *
	 * @param gene
	 *            the {@link Gene} to mark the variants for
	 */
	private void markVariantsInGeneAsCompatible(Gene gene) {
		for (FlaggedVariantCounter var : activeVariants.values()) {
			GenomePosition varPos = new GenomePosition(jannovarDB.refDict, '+',
					jannovarDB.refDict.contigID.get(var.var.vc.getChr()), var.var.vc.getStart() - 1);
			if (gene.region.contains(varPos))
				var.var.setIncluded(true);
		}
	}

	/**
	 * Decrease counter for all variants located in <code>gene</code>.
	 *
	 * @param gene
	 *            the {@link Gene} to mark the variants for
	 */
	void processedGene(Gene gene) throws FilterException {
		// decrease count of variants that lie in gene (that is now ignored)
		ArrayList<FlaggedVariantCounter> done = new ArrayList<FlaggedVariantCounter>();
		for (FlaggedVariantCounter var : activeVariants.values()) {
			GenomePosition varPos = new GenomePosition(jannovarDB.refDict, '+',
					jannovarDB.refDict.contigID.get(var.var.vc.getChr()), var.var.vc.getStart() - 1);
			if (gene.region.contains(varPos)) { // lies in gene
				var.count -= 1;
				if (var.count == 0)
					done.add(var);
			}
		}

		// sort done by coordinate
		Collections.sort(done, new Comparator<FlaggedVariantCounter>() {
			@Override
			public int compare(FlaggedVariantCounter lhs, FlaggedVariantCounter rhs) {
				return (lhs.var.vc.getStart() - rhs.var.vc.getStart());
			}
		});

		// remove done variants and write out if passing
		for (FlaggedVariantCounter var : done) {
			activeVariants.remove(var);
			if (var.var.isIncluded())
				next.put(var.var);
		}

		// mark gene as done
		activeGenes.remove(gene);
	}

	/**
	 * Store {@link FlaggedVariant} and a counter.
	 */
	private static class FlaggedVariantCounter {
		public final FlaggedVariant var;
		public int count;

		FlaggedVariantCounter(FlaggedVariant var, int count) {
			this.var = var;
			this.count = count;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((var == null) ? 0 : var.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			FlaggedVariantCounter other = (FlaggedVariantCounter) obj;
			if (var == null) {
				if (other.var != null)
					return false;
			} else if (!var.equals(other.var))
				return false;
			return true;
		}

	}

	/**
	 * Simple representation of a gene.
	 *
	 * Genes are identified by their name, {@link #equals} and {@link #hashCode} only consider the field {@link #name}!
	 */
	private static class Gene {
		public final String name;
		public final ImmutableList<TranscriptModel> transcripts;
		public final ReferenceDictionary refDict;
		public final GenomeInterval region;

		public Gene(ReferenceDictionary refDict, String name, ImmutableList<TranscriptModel> transcripts) {
			this.refDict = refDict;
			this.name = name;
			this.transcripts = transcripts;
			this.region = buildGeneRegion();
		}

		/**
		 * @return {@link GenomeInterval} of this gene (smallest begin and largest end position of all transcripts).
		 */
		private GenomeInterval buildGeneRegion() {
			if (transcripts.isEmpty())
				return null;

			GenomeInterval region = transcripts.get(0).txRegion.withStrand('+');
			for (TranscriptModel tm : transcripts)
				region = mergeRegions(region, tm.txRegion);
			return region;
		}

		/**
		 * @return {@link GenomeInterval} from the smaller begin to the larger end position of <code>lhs</code> and
		 *         <code>rhs</code>.
		 */
		private GenomeInterval mergeRegions(GenomeInterval lhs, GenomeInterval rhs) {
			lhs = lhs.withStrand('+');
			rhs = rhs.withStrand('+');
			return new GenomeInterval(lhs.getGenomeBeginPos().refDict, '+', lhs.getGenomeBeginPos().chr, Math.min(
					lhs.beginPos, rhs.beginPos), Math.max(lhs.endPos, rhs.endPos));
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((name == null) ? 0 : name.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Gene other = (Gene) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name)) {
				return false;
			}
			return true;
		}
	}

	/**
	 * Builder for {@link Gene}.
	 */
	private static class GeneBuilder {
		private final ReferenceDictionary refDict;
		private String name = null;
		private final ImmutableList.Builder<TranscriptModel> builder = new ImmutableList.Builder<TranscriptModel>();

		public GeneBuilder(ReferenceDictionary refDict, String name) {
			this.refDict = refDict;
			this.name = name;
		}

		public void addTranscriptModel(TranscriptModel tm) {
			builder.add(tm);
		}

		public Gene build() {
			return new Gene(refDict, name, builder.build());
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}

	/**
	 * Extraction of interval ends of {@link Gene} objects.
	 */
	private static class GeneIntervalEndExtractor implements IntervalEndExtractor<Gene> {
		@Override
		public int getBegin(Gene gene) {
			return gene.region.beginPos;
		}

		@Override
		public int getEnd(Gene gene) {
			return gene.region.endPos;
		}
	}

	/**
	 * List of genes, accessible through an interval tree.
	 */
	private static class GeneList {
		/** overall gene list */
		public final ImmutableList<Gene> genes;
		/** map from numeric chromosome id to interval tree of genes */
		public final ImmutableMap<Integer, IntervalArray<Gene>> gIntervalTree;

		public GeneList(ImmutableList<Gene> genes) {
			this.genes = genes;
			this.gIntervalTree = buildIntervalTree();
		}

		private ImmutableMap<Integer, IntervalArray<Gene>> buildIntervalTree() {
			HashMap<Integer, ArrayList<Gene>> chrToGene = new HashMap<Integer, ArrayList<Gene>>();
			for (Gene gene : genes) {
				if (!chrToGene.containsKey(gene.region.chr))
					chrToGene.put(gene.region.chr, new ArrayList<Gene>());
				chrToGene.get(gene.region.chr).add(gene);
			}

			ImmutableMap.Builder<Integer, IntervalArray<Gene>> builder = new ImmutableMap.Builder<Integer, IntervalArray<Gene>>();
			for (Map.Entry<Integer, ArrayList<Gene>> entry : chrToGene.entrySet())
				builder.put(entry.getKey(), new IntervalArray<Gene>(entry.getValue(), new GeneIntervalEndExtractor()));
			return builder.build();
		}
	}

}
