package de.charite.compbio.jannovar.mendel.filter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.data.Chromosome;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.intervals.Interval;
import de.charite.compbio.jannovar.impl.intervals.IntervalArray;
import de.charite.compbio.jannovar.mendel.IncompatiblePedigreeException;
import de.charite.compbio.jannovar.mendel.SubModeOfInheritance;
import de.charite.compbio.jannovar.mendel.bridge.CannotAnnotateMendelianInheritance;
import de.charite.compbio.jannovar.mendel.bridge.MendelVCFHeaderExtender;
import de.charite.compbio.jannovar.mendel.bridge.VariantContextMendelianAnnotator;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFHeader;

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
	/** Provider for contig name to number conversion */
	private final ContigInfoProvider contigInfoProvider;
	/** Whether or not to interpret genotype-wise filters */
	private final boolean interpretGenotypeFilters;
	/** Whether or not to interpret variant-wise filters */
	private final boolean interpretVariantFilters;

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
	 * @param interpretFilters
	 *            whether or not to interpret genotype- or variant-wise filters
	 */
	public GeneWiseMendelianAnnotationProcessor(Pedigree pedigree, JannovarData jannovarData,
			Consumer<VariantContext> sink, boolean interpretFilters) {
		this.pedigree = pedigree;
		this.jannovarData = jannovarData;
		this.sink = sink;

		this.interpretGenotypeFilters = interpretFilters;
		this.interpretVariantFilters = interpretFilters;

		this.geneList = buildGeneList(this.jannovarData);
		this.annotator = new VariantContextMendelianAnnotator(this.pedigree, interpretGenotypeFilters,
				interpretVariantFilters);

		this.contigInfoProvider = new ContigInfoProvider();
	}

	@Override
	public void put(VariantContext vc) throws VariantContextFilterException {
		LOGGER.trace("Putting variant {} into inheritance filter", new Object[] { vc });

		// Map contig name to number of yet unknown
		if (contigInfoProvider.getCurrentContig() == null
				|| !contigInfoProvider.getCurrentContig().equals(vc.getContig())) {
			contigInfoProvider.registerContig(vc.getContig());
		} else {
			// Contig already known, check that variants are sorted by contig
			if (!contigInfoProvider.isContigKnown(vc.getContig()))
				throw new UncheckedJannovarException("Variants are not sorted by chromome, seeing contig "
						+ vc.getContig() + " the second time with contig " + contigInfoProvider.getCurrentContig()
						+ " before the second time");
		}

		// Resolve contig that we work on, trigger start of new contig if necessary
		final ReferenceDictionary refDict = jannovarData.getRefDict();
		// The contig name may not be known to the
		Optional<Integer> contigID = Optional.ofNullable(refDict.getContigNameToID().get(vc.getContig()));
		Optional<IntervalArray<Gene>> iTree = contigID.map(x -> geneList.getGeneIntervalTree().get(x));
		// Unknown contig or contig with annotation, simply write out
		if (!iTree.isPresent()) {
			LOGGER.trace("Unknown contig or contig without annotation in " + vc.getContig()
					+ ", flushing current contig and writing out.");
			markDoneGenes(-1, -1);
			sink.accept(vc);
			return;
		}

		// Consider this variant for each affected gene
		Optional<GenomeInterval> changeInterval = contigID
				.map(x -> new GenomeInterval(refDict, Strand.FWD, x, vc.getStart() - 1, vc.getEnd()));
		Optional<IntervalArray<Gene>.QueryResult> qr = Optional.empty();
		if (changeInterval.isPresent()) {
			if (changeInterval.get().length() == 0)
				qr = iTree.map(x -> x.findOverlappingWithPoint(changeInterval.get().getBeginPos()));
			else
				qr = iTree.map(x -> x.findOverlappingWithInterval(changeInterval.get().getBeginPos(),
						changeInterval.get().getEndPos()));
		}

		if (qr.isPresent()) {
			if (qr.get().getEntries().isEmpty()) {
				putVariantForGene(vc, null);
			} else {
				for (Gene gene : qr.get().getEntries())
					if (isGeneAffectedByChange(gene, vc))
						putVariantForGene(vc, gene);
			}
		}

		// Write out all variants left of variant. If contig ID not known then write out everything currently in cache
		if (contigID.isPresent())
			markDoneGenes(contigID.get(), vc.getStart() - 1);
		else
			markDoneGenes(-1, -1);
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
	 * Appropriately extend {@link VCFHeader}
	 */
	public void extendHeader(VCFHeader vcfHeader, String prefix) {
		new MendelVCFHeaderExtender().extendHeader(vcfHeader, prefix);
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
	 *
	 * @throws UncheckedJannovarException
	 *             on problems with annotation of the variant
	 */
	private void putVariantForGene(VariantContext vc, Gene gene) {
		LOGGER.trace("Assigning variant {} to gene {}", new Object[] { vc, gene });
		// Register VariantContext as active
		activeVariants.computeIfAbsent(vc, x -> new VariantContextCounter(x, 0));

		if (gene == null) {
			// Compute modes of inheritance on its own, don't assign to any gene, just marked as active
			try {
				annotator.annotateRecord(vc);
			} catch (CannotAnnotateMendelianInheritance e) {
				throw new UncheckedJannovarException("Problem with mendelian variant annotation in variant context", e);
			}
			return;
		}

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

		if (doneGenes.isEmpty()) {
			processedGene(null);
		} else {
			for (Gene gene : doneGenes) {
				processedGene(gene);
				activeGenes.remove(gene);
			}
		}

		if (!doneGenes.isEmpty() && activeGenes.isEmpty() && !activeVariants.isEmpty())
			throw new RuntimeException("All genes inactive, there should be no active variant");
	}

	/**
	 * Builds genotype call lists for variants in currently active genes, checks for compatibility, and in case of
	 * compatibility, marks variants in <code>gene</code> as compatible.
	 * 
	 * @throws VariantContextFilterException
	 *             in case of problems with processing the variant
	 * @throws CannotAnnotateMendelianInheritance
	 *             in case of problems with Mendelian inheritance annotation
	 */
	private void checkVariantsForGene(Gene gene)
			throws VariantContextFilterException, CannotAnnotateMendelianInheritance {
		// Compute compatible modes for all variants in the gene
		final ArrayList<VariantContext> variantsForGene = activeGenes.get(gene);
		ImmutableMap<SubModeOfInheritance, ImmutableList<VariantContext>> compatibleMap = annotator
				.computeCompatibleInheritanceSubModes(variantsForGene);
		// Annotate the variants with new compatible modes
		for (Entry<SubModeOfInheritance, ImmutableList<VariantContext>> e : compatibleMap.entrySet()) {
			for (VariantContext vc : e.getValue()) {
				activeVariants.get(vc).addCompatibleMode(e.getKey());
			}
		}
	}

	/**
	 * Decrease counter for all variants located in <code>gene</code>.
	 *
	 * @param gene
	 *            the {@link Gene} to mark the variants for, <code>null</code> to trigger processing variants without
	 *            marking a gene as processed
	 */
	private void processedGene(Gene gene) throws VariantContextFilterException {
		try {
			if (gene != null)
				checkVariantsForGene(gene);
		} catch (CannotAnnotateMendelianInheritance e) {
			if (e.getCause().getClass().equals(IncompatiblePedigreeException.class))
				throw new VariantContextFilterException(
						"Cannot annotate Mendelian inheritance, pedigree is incompatible to genotypes", e);
			else
				throw new VariantContextFilterException("Problem with annotating variant for Mendelian inheritance", e);
		}

		if (gene != null)
			LOGGER.trace("Gene done {}", new Object[] { gene.getName() });
		else
			LOGGER.trace("Marking variants as done without any gene");

		// Decrease count of variants that lie in gene (that is now ignored)
		for (VariantContextCounter var : activeVariants.values()) {
			if (gene != null && isGeneAffectedByChange(gene, var.getVariantContext())) {
				LOGGER.trace("Gene {} done for variant {}", new Object[] { gene.getName(),
						var.getVariantContext().getContig() + ":" + var.getVariantContext().getStart() });
				var.decrement();
			}
		}

		// Comparator for comparing two VariantContextCounter objects
		Comparator<VariantContextCounter> cmp = new Comparator<VariantContextCounter>() {
			@Override
			public int compare(VariantContextCounter lhs, VariantContextCounter rhs) {
				final int idxLhs = contigInfoProvider.getContigNoForName(lhs.getVariantContext().getContig());
				final int idxRhs = contigInfoProvider.getContigNoForName(rhs.getVariantContext().getContig());
				if (idxLhs != idxRhs)
					return (idxLhs - idxRhs);
				else
					return (lhs.getVariantContext().getStart() - rhs.getVariantContext().getStart());
			}
		};

		// Get leftmost variant that is not processed
		Optional<VariantContextCounter> leftmost = activeVariants.values().stream().filter(x -> x.getCounter() != 0)
				.min(cmp);
		List<VariantContextCounter> done = activeVariants.values().stream()
				.filter(x -> (!leftmost.isPresent() || cmp.compare(x, leftmost.get()) < 0))
				.collect(Collectors.toList());

		// Sort done by coordinate
		Collections.sort(done, cmp);

		// Remove completed variants and write out if passing
		for (VariantContextCounter var : done) {
			activeVariants.remove(var.getVariantContext());

			ArrayList<String> modes = new ArrayList<>();
			modes.addAll(var.getCompatibleModes().stream().map(m -> m.toModeOfInheritance().getAbbreviation())
					.filter(m -> m != null).collect(Collectors.toList()));
			ArrayList<String> arSubModes = new ArrayList<>();
			arSubModes.addAll(var.getCompatibleModes().stream().filter(m -> m.isRecessive())
					.map(m -> m.getAbbreviation()).filter(m -> m != null).collect(Collectors.toList()));

			if (modes.isEmpty()) {
				sink.accept(var.getVariantContext());
			} else {
				VariantContextBuilder vcBuilder = new VariantContextBuilder(var.getVariantContext());
				if (!modes.isEmpty())
					vcBuilder.attribute(MendelVCFHeaderExtender.key(), modes);
				if (!arSubModes.isEmpty())
					vcBuilder.attribute(MendelVCFHeaderExtender.keySub(), arSubModes);
				sink.accept(vcBuilder.make());
			}
		}

		if (gene != null) {
			LOGGER.trace("Gene {} is inactive now", new Object[] { gene.getName() });
			// Mark gene as done
			activeGenes.remove(gene);
		}
	}

	/**
	 * Handle mapping between contig name and number
	 */
	private class ContigInfoProvider {

		private static final int UNKNOWN = -1;

		/** Name of currently processed contig */
		private String currentContig = null;

		/** Integer for order of next contig */
		private int nextContigNo = 0;

		/** Mapping from already seen contigs to integer */
		private Map<String, Integer> contigNameToNo = new HashMap<>();

		/** @return number for given contig name */
		int getContigNoForName(String name) {
			if (contigNameToNo.containsKey(name))
				return contigNameToNo.get(name);
			else
				return UNKNOWN;
		}

		/** @return whether the contig has been seen before */
		boolean isContigKnown(String name) {
			return getContigNoForName(name) != UNKNOWN;
		}

		/**
		 * Register new contig, returning ID of it
		 * 
		 * Also update {@link currentContig} to <code>name</code>
		 */
		int registerContig(String name) {
			if (isContigKnown(name))
				throw new RuntimeException("Seeing contig " + name
						+ " a second time (with other contig name in between). Is your file sorted?");
			contigNameToNo.put(name, nextContigNo);
			currentContig = name;
			return nextContigNo++;
		}

		/**
		 * Return name of current contig
		 */
		String getCurrentContig() {
			return currentContig;
		}
	}

}
