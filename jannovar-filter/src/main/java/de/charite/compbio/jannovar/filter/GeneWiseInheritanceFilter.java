package de.charite.compbio.jannovar.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;

import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.intervals.Interval;
import de.charite.compbio.jannovar.impl.intervals.IntervalArray;
import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityChecker;
import de.charite.compbio.jannovar.pedigree.compatibilitychecker.InheritanceCompatibilityCheckerException;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextComparator;

/**
 * A {@link VariantContext} filter that collects variants for each genes and then checks for compatibility.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
/**
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 *
 */
public class GeneWiseInheritanceFilter implements VariantContextFilter {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(GeneWiseInheritanceFilter.class);

	/** Deserialized Jannovar data */
	private final JannovarData jannovarDB;
	/** List of genes, indexed by interval tree */
	private final GeneList geneList;
	/** Next filter. */
	private final VariantContextFilter next;
	/** Compatibility checker for genotype call lists and {@link #pedigree}. */
	private final InheritanceCompatibilityChecker checker;

	/** Stores the gene/variants relation. */
	HashMap<Gene, List<VariantContext>> variantToGeneMap = new HashMap<Gene, List<VariantContext>>();
	/** All variants that matches the inheritance will be stored here */
	Set<VariantContext> passedVariants;

	/** Initialize */
	public GeneWiseInheritanceFilter(Pedigree pedigree, JannovarData jannovarDB,
			ImmutableSet<ModeOfInheritance> modeOfInheritances, VariantContextFilter next) {
		this.jannovarDB = jannovarDB;
		this.geneList = buildGeneList(jannovarDB);
		this.next = next;
		this.checker = new InheritanceCompatibilityChecker.Builder().pedigree(pedigree).addModes(modeOfInheritances)
				.build();
		// TODO(mschubach) what about chr1 and 1?
		List<String> contigs = new ArrayList<String>();
		for (Chromosome chr : jannovarDB.getChromosomes().values()) {
			contigs.add(chr.getChromosomeName());
			contigs.add("chr"+chr.getChromosomeName());
		}
		this.passedVariants = Sets.newTreeSet(new VariantContextComparator(contigs));
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
		for (Chromosome chrom : jannovarDB.getChromosomes().values())
			for (Interval<TranscriptModel> itv : chrom.getTMIntervalTree().getIntervals()) {
				TranscriptModel tm = itv.getValue();
				if (!geneMap.containsKey(tm.getGeneSymbol()))
					geneMap.put(tm.getGeneSymbol(), new GeneBuilder(jannovarDB.getRefDict(), tm.getGeneSymbol()));
				geneMap.get(tm.getGeneSymbol()).addTranscriptModel(tm);
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
	public void put(FlaggedVariant fv) throws FilterException {

		VariantContext vc = fv.getVC();
		LOGGER.trace("Putting variant {} into inheritance filter", new Object[] { vc });

		final ReferenceDictionary refDict = jannovarDB.getRefDict();
		// TODO(holtgrew): for now, we simply ignore variants on contigs unknown to us, this has to be fixed
		if (!refDict.getContigNameToID().containsKey(vc.getContig()))
			return;
		final int contigID = refDict.getContigNameToID().get(vc.getContig());

		IntervalArray<Gene> iTree = geneList.getGeneIntervalTree().get(contigID);

		final String ref = vc.getReference().getBaseString();
		final String alt = vc.getAlternateAllele(0).getBaseString();
		final int pos = vc.getStart();
		final GenomeVariant change = new GenomeVariant(
				new GenomePosition(jannovarDB.getRefDict(), Strand.FWD, contigID, pos, PositionType.ONE_BASED), ref,
				alt);

		// query the gene interval tree for overlapping genes
		final GenomeInterval changeInterval = change.getGenomeInterval();
		IntervalArray<Gene>.QueryResult qr;
		if (changeInterval.length() == 0)
			qr = iTree.findOverlappingWithPoint(changeInterval.getBeginPos());
		else
			qr = iTree.findOverlappingWithInterval(changeInterval.getBeginPos(), changeInterval.getEndPos());

		for (Gene gene : qr.getEntries())
			if (isGeneAffectedByChange(gene, change))
				putVariantForGene(vc, gene);
	}

	/**
	 * Adds a variant to a gene
	 * 
	 * @param vc
	 *            The variant
	 * @param gene
	 *            The gene where the variant should be associated.
	 */
	private void putVariantForGene(VariantContext vc, Gene gene) {
		if (!variantToGeneMap.containsKey(gene)) {
			variantToGeneMap.put(gene, new ArrayList<VariantContext>());
		}
		variantToGeneMap.get(gene).add(vc);
	}

	/**
	 * Called when done with processing.
	 *
	 * See {@link VariantContextFilter#finish} for more details.
	 */
	@Override
	public void finish() throws FilterException {
		for (Map.Entry<Gene, List<VariantContext>> entry : variantToGeneMap.entrySet()) {
			checkVariantsForGene(entry.getKey());
		}
		for (VariantContext vc : passedVariants) {
			LOGGER.trace("Variant remains {} ", new Object[] { vc });
			if (next != null) {
				FlaggedVariant fvc = new FlaggedVariant(vc);
				next.put(fvc);
			}
		}

		// there should be no more active variants or genes
		if (passedVariants.isEmpty())
			throw new RuntimeException("all variants should be inactive now");
		if (next != null)
			next.finish();
	}

	/**
	 * Adds the gioven varaints to the {@link #passedVariants}.
	 * 
	 * @param variants
	 *            List of {@link VariantContext} that are arred to the {@link #passedVariants} tree
	 */
	private void addCompatibleVariants(List<VariantContext> variants) {

		for (VariantContext vc : variants) {
			passedVariants.add(vc);
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
	private static boolean isGeneAffectedByChange(Gene gene, GenomeVariant change) {
		final GenomeInterval changeInterval = change.getGenomeInterval();
		if (changeInterval.length() == 0 && gene.getRegion().contains(changeInterval.getGenomeBeginPos())
				&& gene.getRegion().contains(changeInterval.getGenomeBeginPos().shifted(-1)))
			return false;
		else if (changeInterval.length() != 0 && gene.getRegion().overlapsWith(changeInterval))
			return true;
		return false;
	}

	/**
	 * Builds genotype call lists for variants in currently active genes, checks for compatibility, and in case of
	 * compatibility, marks variants in <code>gene</code> as compatible.
	 * 
	 * @param gene
	 * @throws FilterException
	 */
	private void checkVariantsForGene(Gene gene) throws FilterException  {
		LOGGER.trace("Check inheritance and marking variants in gene {} ", new Object[] { gene });
		List<VariantContext> filteredOutput;
		try {
			filteredOutput = this.checker.getCompatibleWith(variantToGeneMap.get(gene));
			addCompatibleVariants(filteredOutput);
		} catch (InheritanceCompatibilityCheckerException e) {
			throw new FilterException("Problem in mode of inheritance filter.", e);
		}
		
	}

}
