package de.charite.compbio.jannovar.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.impl.intervals.Interval;
import de.charite.compbio.jannovar.impl.intervals.IntervalArray;
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
 * A {@link VariantContext} filter that collects variants for each genes and then checks for compatibility.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class GeneWiseInheritanceFilter implements VariantContextFilter {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(GeneWiseInheritanceFilter.class);

	/** Deserialized Jannovar data */
	private final JannovarData jannovarDB;
	/** The mode of inheritance to filter for */
	private final ModeOfInheritance modeOfInheritance;
	/** List of genes, indexed by interval tree */
	private final GeneList geneList;
	/** Names of {@link pedigree#members}. */
	private final ImmutableList<String> personNames;
	/** Next filter. */
	private final VariantContextFilter next;
	/** Compatibility checker for genotype call lists and {@link #pedigree}. */
	private final PedigreeDiseaseCompatibilityDecorator checker;

	/** Currently active genes and variants assigned to them. */
	HashMap<Gene, GenotypeListBuilder> activeGenes = new HashMap<Gene, GenotypeListBuilder>();
	/** Currently buffered variants. */
	HashMap<FlaggedVariant, FlaggedVariantCounter> activeVariants = new HashMap<FlaggedVariant, FlaggedVariantCounter>();

	/** Initialize */
	public GeneWiseInheritanceFilter(Pedigree pedigree, JannovarData jannovarDB, ModeOfInheritance modeOfInheritance,
			VariantContextFilter next) {
		this.jannovarDB = jannovarDB;
		this.modeOfInheritance = modeOfInheritance;
		this.geneList = buildGeneList(jannovarDB);
		this.next = next;
		this.checker = new PedigreeDiseaseCompatibilityDecorator(pedigree);

		ImmutableList.Builder<String> namesBuilder = new ImmutableList.Builder<String>();
		for (Person p : pedigree.members)
			namesBuilder.add(p.name);
		this.personNames = namesBuilder.build();
	}

	/**
	 * Build {@link GeneList} to be used in the filter.
	 *
	 * @param jannovarDB
	 *            data base to use for building gene list
	 * @return list of genes, built from <code>jannovarDB</code>.
	 */
	private static GeneList buildGeneList(JannovarData jannovarDB) {
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

	/**
	 * Main entry function for filter, see {@link VariantContextFilter#put} for more information.
	 */
	@Override
	public void put(FlaggedVariant vc) throws FilterException {
		LOGGER.trace("Putting variant {} into inheritance filter", new Object[] { vc.vc });

		final ReferenceDictionary refDict = jannovarDB.refDict;
		// TODO(holtgrew): for now, we simply ignore variants on contigs unknown to us, this has to be fixed
		if (!refDict.contigID.containsKey(vc.vc.getChr()))
			return;
		final int contigID = refDict.contigID.get(vc.vc.getChr());
		IntervalArray<Gene> iTree = geneList.gIntervalTree.get(contigID);

		// consider each alternative allele of the variant
		for (int alleleID = 0; alleleID < vc.vc.getAlternateAlleles().size(); ++alleleID) {
			final GenomeChange change = getGenomeChangeFromAltAllele(vc.vc, alleleID);

			// query the gene interval tree for overlapping genes
			final GenomeInterval changeInterval = change.getGenomeInterval();
			IntervalArray<Gene>.QueryResult qr;
			if (changeInterval.length() == 0)
				qr = iTree.findOverlappingWithPoint(changeInterval.beginPos);
			else
				qr = iTree.findOverlappingWithInterval(changeInterval.beginPos, changeInterval.endPos);

			for (Gene gene : qr.entries)
				if (isGeneAffectedByChange(gene, change))
					putVariantForGene(vc, gene);
		}

		// write out all variants left of variant
		markDoneGenes(contigID, vc.vc.getStart() - 1);
	}

	/**
	 * Register {@link FlaggedVariant} as active for the given gene.
	 */
	private void putVariantForGene(FlaggedVariant vc, Gene gene) {
		LOGGER.trace("Assigning variant {} to gene {}", new Object[] { vc.vc, gene });
		// register variant as active
		if (!activeVariants.containsKey(vc))
			activeVariants.put(vc, new FlaggedVariantCounter(vc, 1));
		else
			activeVariants.get(vc).count += 1;

		// create new GenotypeListBuilder for the gene if necessary
		if (!activeGenes.containsKey(gene))
			activeGenes.put(gene, new GenotypeListBuilder(gene.name, personNames));

		// register Genotypes for vc
		putGenotypes(vc, gene);
	}

	/**
	 * Construct {@link GenomeChange} from one allele in a {@link VariantContext}.
	 */
	private GenomeChange getGenomeChangeFromAltAllele(VariantContext vc, int alleleID) {
		final int contigID = jannovarDB.refDict.contigID.get(vc.getChr());
		final String ref = vc.getReference().getBaseString();
		final String alt = vc.getAlternateAllele(alleleID).getBaseString();
		final int pos = vc.getStart();
		return new GenomeChange(new GenomePosition(jannovarDB.refDict, '+', contigID, pos, PositionType.ONE_BASED),
				ref, alt);
	}

	/**
	 * Mark genes left of <code>(contigID, pos)</code> as done.
	 *
	 * @param contigID
	 *            numeric contig ID, as taken from {@link JannovarDB#refDict} from {@link #jannovarDB}.
	 * @param pos
	 *            zero-based position on the given contig
	 * @throws FilterException
	 *             on problems with filtration
	 */
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
			processedGene(gene);
			activeGenes.remove(gene);
		}
	}

	private void putGenotypes(FlaggedVariant fv, Gene gene) {
		final VariantContext vc = fv.vc;
		for (int i = 0; i < vc.getAlternateAlleles().size(); ++i) {
			Allele currAlt = vc.getAlternateAllele(i);

			ImmutableList.Builder<Genotype> builder = new ImmutableList.Builder<Genotype>();
			for (int pID = 0; pID < personNames.size(); ++pID) {
				htsjdk.variant.variantcontext.Genotype gt = vc.getGenotype(personNames.get(pID));
				if (gt.getAlleles().size() > 2)
					throw new RuntimeException("Unexpected allele count: " + gt.getAlleles().size());

				// we consider everything non-ALT (for current alternative allele) to be REF
				final int idx0 = 0;
				final boolean isRef0 = !gt.getAllele(idx0).getBaseString().equals(currAlt.getBaseString());
				final int idx1 = (gt.getAlleles().size() == 2) ? 1 : 0;
				final boolean isRef1 = !gt.getAllele(idx1).getBaseString().equals(currAlt.getBaseString());

				// TODO(holtgrem): Handle case of symbolic alleles and write through?
				if (gt.getAllele(idx0).isNoCall() || gt.getAllele(idx1).isNoCall())
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

	/**
	 * Called when done with processing.
	 *
	 * See {@link VariantContextFilter#finish} for more details.
	 */
	@Override
	public void finish() throws FilterException {
		// perform a final round of tests on all currently active genes
		ArrayList<Gene> doneGenes = new ArrayList<Gene>();
		for (Map.Entry<Gene, GenotypeListBuilder> entry : activeGenes.entrySet()) {
			checkVariantsForGene(entry.getKey());
			doneGenes.add(entry.getKey());
		}
		// mark gene as done
		for (Gene gene : doneGenes)
			processedGene(gene);

		for (FlaggedVariantCounter fvc : activeVariants.values())
			LOGGER.trace("Variant remains {} with count {}", new Object[] { fvc.var.vc, fvc.count });

		// there should be no more active variants or genes
		if (!activeVariants.isEmpty())
			throw new RuntimeException("all variants should be inactive now");
		if (!activeGenes.isEmpty())
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
		LOGGER.trace("Marking variants in {} as compatible", new Object[] { gene });
		for (FlaggedVariantCounter var : activeVariants.values()) {
			for (int alleleID = 0; alleleID < var.var.vc.getAlternateAlleles().size(); ++alleleID) {
				if (isGeneAffectedByChange(gene, getGenomeChangeFromAltAllele(var.var.vc, alleleID))) {
					LOGGER.trace("Including variant {}", new Object[] { var.var.vc });
					var.var.setIncluded(true);
				}
			}
		}
	}

	/**
	 * Utility function to test whether a {@link Gene} is affected by a {@link GenomeChage}.
	 *
	 * @param gene
	 *            to use for the check
	 * @param change
	 *            to use for the check
	 * @return <code>true</code> if <code>gene</code> is affected by <code>change</code>
	 */
	private static boolean isGeneAffectedByChange(Gene gene, GenomeChange change) {
		final GenomeInterval changeInterval = change.getGenomeInterval();
		if (changeInterval.length() == 0 && gene.region.contains(changeInterval.getGenomeBeginPos())
				&& gene.region.contains(changeInterval.getGenomeBeginPos().shifted(-1)))
			return false;
		else if (changeInterval.length() != 0 && gene.region.overlapsWith(changeInterval))
			return true;
		return false;
	}

	/**
	 * Builds genotype call lists for variants in currently active genes, checks for compatibility, and in case of
	 * compatibility, marks variants in <code>gene</code> as compatible.
	 */
	private void checkVariantsForGene(Gene gene) throws FilterException {
		// check gene for compatibility and mark variants as compatible if so
		GenotypeList lst = activeGenes.get(gene).build();
		try {
			boolean isXChromosomal = (gene.refDict.contigID.get("chrX") != null && gene.refDict.contigID.get("chrX")
					.intValue() == gene.region.chr);
			if (checker.isCompatibleWith(lst, modeOfInheritance, isXChromosomal))
				markVariantsInGeneAsCompatible(gene);
		} catch (CompatibilityCheckerException e) {
			throw new FilterException("Problem in mode of inheritance filter.", e);
		}
	}

	/**
	 * Decrease counter for all variants located in <code>gene</code>.
	 *
	 * @param gene
	 *            the {@link Gene} to mark the variants for
	 */
	private void processedGene(Gene gene) throws FilterException {
		checkVariantsForGene(gene);

		LOGGER.trace("Gene done {}", new Object[] { gene });

		// decrease count of variants that lie in gene (that is now ignored)
		ArrayList<FlaggedVariantCounter> done = new ArrayList<FlaggedVariantCounter>();
		for (FlaggedVariantCounter var : activeVariants.values()) {
			int sum = 0;
			for (int alleleID = 0; alleleID < var.var.vc.getAlternateAlleles().size(); ++alleleID) {
				if (isGeneAffectedByChange(gene, getGenomeChangeFromAltAllele(var.var.vc, alleleID))) {
					LOGGER.trace("Gene {} done for variant {}", new Object[] { var.var.vc, gene });
					sum += 1;
				}
			}
			if (sum > 0) {
				var.count -= sum;
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
			activeVariants.remove(var.var);
			if (var.var.isIncluded()) {
				LOGGER.trace("Keeping variant {}", new Object[] { var.var.vc });
				next.put(var.var);
			} else {
				LOGGER.trace("Removing variant {}", new Object[] { var.var.vc });
			}
		}

		LOGGER.trace("Gene {} is inactive now", new Object[] { gene });

		// mark gene as done
		activeGenes.remove(gene);
	}

}
