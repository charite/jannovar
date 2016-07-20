package de.charite.compbio.jannovar.mendel.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.intervals.Interval;
import de.charite.compbio.jannovar.impl.intervals.IntervalArray;
import de.charite.compbio.jannovar.mendel.ModeOfInheritance;
import de.charite.compbio.jannovar.mendel.bridge.CannotateAnnotateMendelianInheritance;
import de.charite.compbio.jannovar.mendel.bridge.MendelVCFHeaderExtender;
import de.charite.compbio.jannovar.mendel.bridge.VariantContextMendelianAnnotator;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

/**
 * Process {@link VariantContext} objects and annotate them with mendelian inheritance compatibility
 * 
 * The variants put into the processor must be clustered by contig name and sorted by begin position
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GeneWiseMendelianAnnotationProcessor implements VariantContextProcessor {

	/** The logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(GeneWiseMendelianAnnotationProcessor.class);

	/** The {@link Pedigree} to use for the computation */
	private final Pedigree pedigree;
	/** The {@link VariantContextMendelianAnnotator} to use for mendelian compatibility annotation */
	private final VariantContextMendelianAnnotator annotator;
	/** The {@link JannovarData} to use for extracting the genes from */
	private JannovarData jannovarData;
	/** Next step in pipeline after processing of {@link VariantContext} is complete */
	private final Consumer<VariantContext> sink;

	/** Currently active genes and variants assigned to them. */
	HashMap<Gene, ArrayList<VariantContext>> activeGenes = new HashMap<>();
	/** Currently buffered variants. */
	HashMap<VariantContext, VariantContextCounter> activeVariants = new HashMap<>();

	/** List of genes, indexed by interval tree */
	private final GeneList geneList;

	/**
	 * Construct processor with the path to the PED file to use
	 * 
	 * @param pedigree
	 *            the {@link Pedigree} object to use
	 * @param jannovarData
	 *            {@link JannovarData} object to use for getting the genes from
	 * @param sink
	 *            location to write the {@link VariantContext} to
	 */
	public GeneWiseMendelianAnnotationProcessor(Pedigree pedigree, JannovarData jannovarData,
			Consumer<VariantContext> sink) {
		this.pedigree = pedigree;
		this.jannovarData = jannovarData;
		this.sink = sink;

		this.geneList = buildGeneList(this.jannovarData);
		this.annotator = new VariantContextMendelianAnnotator(this.pedigree);
	}

	@Override
	public void put(VariantContext vc) throws VariantContextFilterException {
		LOGGER.trace("Putting variant {} into inheritance filter", new Object[] { vc });

		// Resolve contig that we work on, trigger start of new contig if necessary
		final ReferenceDictionary refDict = jannovarData.getRefDict();
		if (!refDict.getContigNameToID().containsKey(vc.getContig())) {
			LOGGER.trace("Unknown contig in " + vc.getContig() + ", flushing current contig and writing out.");
			markDoneGenes(-1, -1);
			sink.accept(vc);
			return;
		}
		final int contigID = refDict.getContigNameToID().get(vc.getContig());
		IntervalArray<Gene> iTree = geneList.getGeneIntervalTree().get(contigID);

		// Consider this variant for each affected gene
		GenomeInterval changeInterval = new GenomeInterval(refDict, Strand.FWD, contigID, vc.getStart() - 1,
				vc.getEnd());
		final IntervalArray<Gene>.QueryResult qr;
		if (changeInterval.length() == 0)
			qr = iTree.findOverlappingWithPoint(changeInterval.getBeginPos());
		else
			qr = iTree.findOverlappingWithInterval(changeInterval.getBeginPos(), changeInterval.getEndPos());

		for (Gene gene : qr.getEntries())
			if (isGeneAffectedByChange(gene, vc))
				putVariantForGene(vc, gene);

		// Write out all variants left of variant
		markDoneGenes(contigID, vc.getStart() - 1);
	}

	/**
	 * @return <code>true</code> if <code>gene</code> is affected by <code>variantContext</code>
	 */
	private boolean isGeneAffectedByChange(Gene gene, VariantContext vc) {
		final ReferenceDictionary refDict = jannovarData.getRefDict();
		final int contigID = refDict.getContigNameToID().get(vc.getContig());
		final GenomeInterval changeInterval = new GenomeInterval(refDict, Strand.FWD, contigID, vc.getStart() - 1,
				vc.getEnd());

		if (changeInterval.length() == 0 && gene.getRegion().contains(changeInterval.getGenomeBeginPos())
				&& gene.getRegion().contains(changeInterval.getGenomeBeginPos().shifted(-1)))
			return false;
		else if (changeInterval.length() != 0 && gene.getRegion().overlapsWith(changeInterval))
			return true;
		else
			return false;
	}

	@Override
	public void close() {
		LOGGER.trace("Closing mendelian annotation processor");
		markDoneGenes(-1, -1);

		// There should be no more active variants or genes
		if (!activeVariants.isEmpty())
			throw new VariantContextFilterException("All variants should be inactive now");
		if (!activeGenes.isEmpty())
			throw new VariantContextFilterException("All genes should be inactive now");
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
	 * Register {@link FlaggedVariant} as active for the given gene.
	 */
	private void putVariantForGene(VariantContext vc, Gene gene) {
		LOGGER.trace("Assigning variant {} to gene {}", new Object[] { vc, gene });
		// Register VariantContext as active
		activeVariants.computeIfAbsent(vc, x -> new VariantContextCounter(x, 0));
		activeVariants.get(vc).increment();
		// Register VariantContext for gene
		activeGenes.computeIfAbsent(gene, x -> new ArrayList<>());
		activeGenes.get(gene).add(vc);
	}

	/**
	 * Mark genes left of <code>(contigID, pos)</code> as done.
	 *
	 * @param contigID
	 *            numeric contig ID, as taken from {@link JannovarDB#refDict} from {@link #jannovarDB}.
	 * @param pos
	 *            zero-based position on the given contig
	 * @throws VariantContextFilterException
	 *             on problems with filtration
	 */
	private void markDoneGenes(int contigID, int pos) throws VariantContextFilterException {
		ArrayList<Gene> doneGenes = new ArrayList<Gene>();
		for (Entry<Gene, ArrayList<VariantContext>> entry : activeGenes.entrySet()) {
			Gene gene = entry.getKey();
			if (gene.getRegion().getChr() != contigID)
				doneGenes.add(gene);
			else if (gene.getRegion().getEndPos() <= pos)
				doneGenes.add(gene);
		}

		for (Gene gene : doneGenes) {
			processedGene(gene);
			activeGenes.remove(gene);
		}
	}

	/**
	 * Builds genotype call lists for variants in currently active genes, checks for compatibility, and in case of
	 * compatibility, marks variants in <code>gene</code> as compatible.
	 * 
	 * @throws VariantContextFilterException
	 *             in case of problems with processing the variant
	 * @throws CannotateAnnotateMendelianInheritance
	 *             in case of problems with Mendelian inheritance annotation
	 */
	private void checkVariantsForGene(Gene gene)
			throws VariantContextFilterException, CannotateAnnotateMendelianInheritance {
		// Compute compatible modes for all variants in the gene
		final ArrayList<VariantContext> variantsForGene = activeGenes.get(gene);
		ImmutableMap<ModeOfInheritance, ImmutableList<VariantContext>> compatibleMap = annotator
				.computeCompatibleInheritanceModes(variantsForGene);
		// Annotate the variants with new compatible modes
		for (Entry<ModeOfInheritance, ImmutableList<VariantContext>> e : compatibleMap.entrySet()) {
			for (VariantContext vc : e.getValue()) {
				activeVariants.get(vc).addCompatibleMode(e.getKey());
			}
		}
	}

	/**
	 * Decrease counter for all variants located in <code>gene</code>.
	 *
	 * @param gene
	 *            the {@link Gene} to mark the variants for
	 */
	private void processedGene(Gene gene) throws VariantContextFilterException {
		try {
			checkVariantsForGene(gene);
		} catch (CannotateAnnotateMendelianInheritance e) {
			throw new VariantContextFilterException("Problem with annotating variant for Mendelian inheritance", e);
		}

		LOGGER.trace("Gene done {}", new Object[] { gene.getName() });

		// decrease count of variants that lie in gene (that is now ignored)
		ArrayList<VariantContextCounter> done = new ArrayList<VariantContextCounter>();
		for (VariantContextCounter var : activeVariants.values()) {
			if (isGeneAffectedByChange(gene, var.getVariantContext())) {
				LOGGER.trace("Gene {} done for variant {}", new Object[] { gene.getName(),
						var.getVariantContext().getContig() + ":" + var.getVariantContext().getStart() });
				var.decrement();
			}
			if (var.getCounter() == 0)
				done.add(var);
		}

		// Sort done by coordinate
		Collections.sort(done, new Comparator<VariantContextCounter>() {
			@Override
			public int compare(VariantContextCounter lhs, VariantContextCounter rhs) {
				return (lhs.getVariantContext().getStart() - rhs.getVariantContext().getStart());
			}
		});

		// Remove completed variants and write out if passing
		for (VariantContextCounter var : done) {
			activeVariants.remove(var.getVariantContext());

			ArrayList<String> modes = new ArrayList<>();
			modes.addAll(var.getCompatibleModes().stream().map(m -> m.getAbbreviation()).filter(m -> m != null)
					.collect(Collectors.toList()));

			if (modes.isEmpty()) {
				sink.accept(var.getVariantContext());
			} else {
				VariantContextBuilder vcBuilder = new VariantContextBuilder(var.getVariantContext());
				vcBuilder.attribute(MendelVCFHeaderExtender.key(), modes);
				sink.accept(vcBuilder.make());
			}
		}

		LOGGER.trace("Gene {} is inactive now", new Object[] { gene.getName() });

		// Mark gene as done
		activeGenes.remove(gene);
	}

}
