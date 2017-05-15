package de.charite.compbio.jannovar.cmd.annotate_vcf;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.Jannovar;
import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationCommand;
import de.charite.compbio.jannovar.cmd.annotate_vcf.JannovarAnnotateVCFOptions.BedAnnotationOptions;
import de.charite.compbio.jannovar.filter.facade.PedigreeFilterAnnotator;
import de.charite.compbio.jannovar.filter.facade.PedigreeFilterHeaderExtender;
import de.charite.compbio.jannovar.filter.facade.PedigreeFilterOptions;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterAnnotator;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterHeaderExtender;
import de.charite.compbio.jannovar.filter.facade.ThresholdFilterOptions;
import de.charite.compbio.jannovar.htsjdk.VariantContextAnnotator;
import de.charite.compbio.jannovar.htsjdk.VariantContextWriterConstructionHelper;
import de.charite.compbio.jannovar.htsjdk.VariantEffectHeaderExtender;
import de.charite.compbio.jannovar.mendel.IncompatiblePedigreeException;
import de.charite.compbio.jannovar.mendel.bridge.MendelVCFHeaderExtender;
import de.charite.compbio.jannovar.mendel.filter.ConsumerProcessor;
import de.charite.compbio.jannovar.mendel.filter.CoordinateSortingChecker;
import de.charite.compbio.jannovar.mendel.filter.GeneWiseMendelianAnnotationProcessor;
import de.charite.compbio.jannovar.mendel.filter.VariantContextFilterException;
import de.charite.compbio.jannovar.mendel.filter.VariantContextProcessor;
import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.PedFileContents;
import de.charite.compbio.jannovar.pedigree.PedFileReader;
import de.charite.compbio.jannovar.pedigree.PedParseException;
import de.charite.compbio.jannovar.pedigree.PedPerson;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Person;
import de.charite.compbio.jannovar.pedigree.Sex;
import de.charite.compbio.jannovar.progress.GenomeRegionListFactoryFromSAMSequenceDictionary;
import de.charite.compbio.jannovar.progress.ProgressReporter;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.facade.DBVariantContextAnnotator;
import de.charite.compbio.jannovar.vardbs.facade.DBVariantContextAnnotatorFactory;
import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.samtools.util.Interval;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.VCFContigHeaderLine;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLine;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Run annotation steps (read in VCF, write out VCF or Jannovar file format).
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public class AnnotateVCFCommand extends JannovarAnnotationCommand {

	/** Raw command line arguments */
	private String[] argv = null;

	/** Progress reporting */
	private ProgressReporter progressReporter = null;

	/** Configuration */
	private JannovarAnnotateVCFOptions options;

	public AnnotateVCFCommand(String[] argv, Namespace args) throws CommandLineParsingException {
		this.argv = argv;
		this.options = new JannovarAnnotateVCFOptions();
		this.options.setFromArgs(args);
	}

	/**
	 * This function inputs a VCF file, and prints the annotated version thereof to a file (name of
	 * the original file with the suffix .de.charite.compbio.jannovar).
	 *
	 * @throws JannovarException
	 *             on problems with the annotation
	 */
	@Override
	public void run() throws JannovarException {
		System.err.println("Options");
		System.err.println(options.toString());

		System.err.println("Deserializing transcripts...");
		deserializeTranscriptDefinitionFile(options.getDatabaseFilePath());

		final String vcfPath = options.getPathInputVCF();

		// whether or not to require availability of an index
		final boolean useInterval = (options.getInterval() != null
				&& !options.getInterval().equals(""));

		try (VCFFileReader vcfReader = new VCFFileReader(new File(vcfPath), useInterval)) {
			if (this.options.getVerbosity() >= 1) {
				final SAMSequenceDictionary seqDict = VCFFileReader
						.getSequenceDictionary(new File(vcfPath));
				if (seqDict != null) {
					final GenomeRegionListFactoryFromSAMSequenceDictionary factory = new GenomeRegionListFactoryFromSAMSequenceDictionary();
					this.progressReporter = new ProgressReporter(factory.construct(seqDict), 60);
					this.progressReporter.printHeader();
					this.progressReporter.start();
				} else {
					System.err.println(
							"Progress reporting does not work because VCF file is missing the contig "
									+ "lines in the header.");
				}
			}

			VCFHeader vcfHeader = vcfReader.getFileHeader();

			System.err.println("Annotating VCF...");
			final long startTime = System.nanoTime();

			// Jump to interval if given, otherwise start at beginning
			CloseableIterator<VariantContext> iter;
			if (useInterval) {
				Interval itv = RegionParser.parse(options.getInterval());
				int end = 1000 * 1000 * 1000; // "some large number"
				for (VCFContigHeaderLine line : vcfHeader.getContigLines()) {
					if (line.getID().equals(itv.getContig()))
						end = line.getSAMSequenceRecord().getSequenceLength();
				}
				if (itv.getStart() == 0 && itv.getEnd() == 0)
					itv = new Interval(itv.getContig(), 1, end);
				iter = vcfReader.query(itv.getContig(), itv.getStart(), itv.getEnd());
				System.err.println("Will read interval " + itv.toString());
			} else {
				System.err.println("Will read full input file");
				iter = vcfReader.iterator();
			}

			// Obtain Java 8 stream from iterator
			Stream<VariantContext> stream = iter.stream();

			// If configured, annotate using dbSNP VCF file (extend header to
			// use for writing out)
			if (options.pathVCFDBSNP != null) {
				DBAnnotationOptions dbSNPOptions = DBAnnotationOptions.createDefaults();
				dbSNPOptions.setIdentifierPrefix(options.prefixDBSNP);
				DBVariantContextAnnotator dbSNPAnno = new DBVariantContextAnnotatorFactory()
						.constructDBSNP(options.pathVCFDBSNP, options.pathFASTARef, dbSNPOptions);
				dbSNPAnno.extendHeader(vcfHeader);
				stream = stream.map(dbSNPAnno::annotateVariantContext);
			}

			// If configured, annotate using ExAC VCF file (extend header to use
			// for writing out)
			if (options.pathVCFExac != null) {
				DBAnnotationOptions exacOptions = DBAnnotationOptions.createDefaults();
				exacOptions.setIdentifierPrefix(options.prefixExac);
				DBVariantContextAnnotator exacAnno = new DBVariantContextAnnotatorFactory()
						.constructExac(options.pathVCFExac, options.pathFASTARef, exacOptions);
				exacAnno.extendHeader(vcfHeader);
				stream = stream.map(exacAnno::annotateVariantContext);
			}

			// If configured, annotate using gnomAD exomes VCF file (extend
			// header to use for
			// writing out)
			if (options.pathVCFGnomadExomes != null) {
				DBAnnotationOptions gnomadOptions = DBAnnotationOptions.createDefaults();
				gnomadOptions.setIdentifierPrefix(options.prefixGnomadExomes);
				DBVariantContextAnnotator gnomadExomesAnno = new DBVariantContextAnnotatorFactory()
						.constructGnomad(options.pathVCFGnomadExomes, options.pathFASTARef,
								gnomadOptions);
				gnomadExomesAnno.extendHeader(vcfHeader);
				stream = stream.map(gnomadExomesAnno::annotateVariantContext);
			}

			// If configured, annotate using gnomAD genomes VCF file (extend
			// header to use for
			// writing out)
			if (options.pathVCFGnomadGenomes != null) {
				DBAnnotationOptions gnomadOptions = DBAnnotationOptions.createDefaults();
				gnomadOptions.setIdentifierPrefix(options.prefixGnomadGenomes);
				DBVariantContextAnnotator gnomadGenomesAnno = new DBVariantContextAnnotatorFactory()
						.constructGnomad(options.pathVCFGnomadGenomes, options.pathFASTARef,
								gnomadOptions);
				gnomadGenomesAnno.extendHeader(vcfHeader);
				stream = stream.map(gnomadGenomesAnno::annotateVariantContext);
			}

			// If configured, annotate using UK10K VCF file (extend header to
			// use for writing out)
			if (options.pathVCFUK10K != null) {
				DBAnnotationOptions exacOptions = DBAnnotationOptions.createDefaults();
				exacOptions.setIdentifierPrefix(options.prefixUK10K);
				DBVariantContextAnnotator uk10kAnno = new DBVariantContextAnnotatorFactory()
						.constructUK10K(options.pathVCFUK10K, options.pathFASTARef, exacOptions);
				uk10kAnno.extendHeader(vcfHeader);
				stream = stream.map(uk10kAnno::annotateVariantContext);
			}

			// If configured, annotate using ClinVar VCF file (extend header to
			// use for writing out)
			if (options.pathClinVar != null) {
				DBAnnotationOptions clinVarOptions = DBAnnotationOptions.createDefaults();
				clinVarOptions.setIdentifierPrefix(options.prefixClinVar);
				DBVariantContextAnnotator clinvarAnno = new DBVariantContextAnnotatorFactory()
						.constructClinVar(options.pathClinVar, options.pathFASTARef,
								clinVarOptions);
				clinvarAnno.extendHeader(vcfHeader);
				stream = stream.map(clinvarAnno::annotateVariantContext);
			}

			// If configured, annotate using COSMIC VCF file (extend header to
			// use for writing out)
			if (options.pathCosmic != null) {
				DBAnnotationOptions cosmicOptions = DBAnnotationOptions.createDefaults();
				cosmicOptions.setIdentifierPrefix(options.prefixCosmic);
				DBVariantContextAnnotator cosmicAnno = new DBVariantContextAnnotatorFactory()
						.constructCosmic(options.pathCosmic, options.pathFASTARef, cosmicOptions);
				cosmicAnno.extendHeader(vcfHeader);
				stream = stream.map(cosmicAnno::annotateVariantContext);
			}

			// If configured, use threshold-based annotation (extend header to
			// use for writing out)
			if (options.useThresholdFilters) {
				// Build options object for threshold filter
				ThresholdFilterOptions thresholdFilterOptions = new ThresholdFilterOptions(
						options.getThreshFiltMinGtCovHet(), options.getThreshFiltMinGtCovHomAlt(),
						options.getThreshFiltMaxCov(), options.getThreshFiltMinGtGq(),
						options.getThreshFiltMinGtAafHet(), options.getThreshFiltMaxGtAafHet(),
						options.getThreshFiltMinGtAafHomAlt(),
						options.getThreshFiltMaxGtAafHomRef(), options.getPrefixExac(),
						options.getPrefixDBSNP(), options.getPrefixGnomadGenomes(),
						options.getPrefixGnomadExomes(),
						options.getThreshFiltMaxAlleleFrequencyAd(),
						options.getThreshFiltMaxAlleleFrequencyAr());
				// Add headers
				new ThresholdFilterHeaderExtender(thresholdFilterOptions).addHeaders(vcfHeader);
				// Build list of affecteds; take from pedigree file if given.
				// Otherwise, assume one single individual is always affected and otherwise warn
				// about missing pedigree.
				ArrayList<String> affecteds = new ArrayList<>();
				if (options.pathPedFile == null) {
					if (vcfHeader.getNGenotypeSamples() == 1) {
						System.err.println(
								"INFO: No pedigree file given and single individual. Assuming it is affected for the threshold filter");
					} else {
						System.err.println(
								"WARNING: no pedigree file given. Threshold filter will not annotate FILTER field, only genotype FT");
					}
				} else {
					Pedigree pedigree;
					try {
						pedigree = loadPedigree(vcfHeader);
					} catch (IOException e) {
						System.err.println("Problem loading pedigree from " + options.pathPedFile);
						System.err.println(e.getMessage());
						System.err.println("\n");
						e.printStackTrace(System.err);
						return;
					}
					for (Person person : pedigree.getMembers()) {
						if (person.isAffected())
							affecteds.add(person.getName());
					}
					if (affecteds.isEmpty()) {
						System.err.println(
								"WARNING: no affected individual in pedigree. Threshold filter will not modify FILTER field, "
										+ "only genotype FT");
					}
				}
				ThresholdFilterAnnotator thresholdFilterAnno = new ThresholdFilterAnnotator(
						thresholdFilterOptions, affecteds);
				stream = stream.map(thresholdFilterAnno::annotateVariantContext);

				// When configured to use advanced pedigree filters (must come
				// after threshold-based filtration)
				if (options.useAdvancedPedigreeFilters) {
					// Build options object from configuration and extend headers
					PedigreeFilterOptions pedFilterOptions = new PedigreeFilterOptions(
							options.getThreshDeNovoParentAd2());
					new PedigreeFilterHeaderExtender(pedFilterOptions).addHeaders(vcfHeader);

					// Load pedigree
					Pedigree pedigree;
					try {
						pedigree = loadPedigree(vcfHeader);
					} catch (IOException e) {
						System.err.println("Problem loading pedigree from " + options.pathPedFile);
						System.err.println(e.getMessage());
						System.err.println("\n");
						e.printStackTrace(System.err);
						return;
					}

					// Construct annotator and register with pipeline
					PedigreeFilterAnnotator pedFilterAnnotator = new PedigreeFilterAnnotator(
							pedFilterOptions, pedigree);
					stream = stream.map(pedFilterAnnotator::annotateVariantContext);
				}
				
				// Annotate from BED files
				List<BedFileAnnotator> annotators = new ArrayList<>();
				for (BedAnnotationOptions bedAnnotationOptions : options.getBedAnnotationOptions()) {
					BedFileAnnotator annotator = new BedFileAnnotator(bedAnnotationOptions);
					annotators.add(annotator);
					annotator.extendHeader(vcfHeader);
					stream = stream.map(annotator::annotateVariantContext);
				}
			}

			// Add step for annotating with variant effect
			VariantEffectHeaderExtender extender = new VariantEffectHeaderExtender();
			extender.addHeaders(vcfHeader);
			VariantContextAnnotator annotator = new VariantContextAnnotator(refDict, chromosomeMap,
					new VariantContextAnnotator.Options(!options.isShowAll(),
							options.isEscapeAnnField(), options.isNt3PrimeShifting(),
							options.isOffTargetFilterEnabled(),
							options.isOffTargetFilterUtrIsOffTarget(),
							options.isOffTargetFilterIntronicSpliceIsOffTarget()));
			stream = stream.map(annotator::annotateVariantContext);

			// Extend header with INHERITANCE filter
			if (options.pathPedFile != null || options.annotateAsSingletonPedigree) {
				System.err.println("Extending header with INHERITANCE...");
				new MendelVCFHeaderExtender().extendHeader(vcfHeader, "");
			}

			// Create VCF output writer
			ImmutableList<VCFHeaderLine> jvHeaderLines = ImmutableList.of(
					new VCFHeaderLine("jannovarVersion", Jannovar.getVersion()),
					new VCFHeaderLine("jannovarCommand", Joiner.on(' ').join(argv)));

			// Construct VariantContextWriter and start annotationg pipeline
			try (VariantContextWriter vcfWriter = VariantContextWriterConstructionHelper
					.openVariantContextWriter(vcfHeader, options.getPathOutputVCF(), jvHeaderLines);
					VariantContextProcessor sink = buildMendelianProcessors(vcfWriter, vcfHeader)) {
				// Make current VC available to progress printer
				if (this.progressReporter != null)
					stream = stream.peek(vc -> this.progressReporter.setCurrentVC(vc));

				stream.forEachOrdered(sink::put);
			} catch (IOException e) {
				throw new JannovarException("Problem opening file", e);
			}

			System.err.println("Wrote annotations to \"" + options.getPathOutputVCF() + "\"");
			final long endTime = System.nanoTime();
			System.err.println(String.format("Annotation and writing took %.2f sec.",
					(endTime - startTime) / 1000.0 / 1000.0 / 1000.0));
		} catch (IncompatiblePedigreeException e) {
			if (options.pathPedFile != null)
				System.err.println("VCF file " + vcfPath + " is not compatible to pedigree file "
						+ options.pathPedFile);
			else
				System.err.println("VCF file " + vcfPath
						+ " is not compatible with singleton pedigree annotation (do you have exactly one sample in VCF file?)");
			System.err.println(e.getMessage());
			System.err.println("\n");
			e.printStackTrace(System.err);
		} catch (VariantContextFilterException e) {
			System.err.println("There was a problem annotating the VCF file");
			System.err.println("The error message was as follows.  The stack trace below the error "
					+ "message can help the developers debug the problem.\n");
			System.err.println(e.getMessage());
			System.err.println("\n");
			e.printStackTrace(System.err);
			return;
		}

		if (progressReporter != null)
			progressReporter.done();
	}

	/**
	 * Load pedigree from file given in configuration or construct singleton pedigree
	 * 
	 * @param vcfHeader
	 *            {@link VCFHeader}, for checking compatibility and getting sample name in case of
	 *            singleton pedigree construction
	 * @throws PedParseException
	 *             in the case of problems with parsing pedigrees
	 */
	private Pedigree loadPedigree(VCFHeader vcfHeader)
			throws PedParseException, IOException, IncompatiblePedigreeException {
		if (options.pathPedFile != null) {
			final PedFileReader pedReader = new PedFileReader(new File(options.pathPedFile));
			final PedFileContents pedContents = pedReader.read();
			return new Pedigree(pedContents, pedContents.getIndividuals().get(0).getPedigree());
		} else {
			if (vcfHeader.getSampleNamesInOrder().size() != 1)
				throw new IncompatiblePedigreeException(
						"VCF file does not have exactly one sample but required for singleton pedigree construction");
			final String sampleName = vcfHeader.getSampleNamesInOrder().get(0);
			final PedPerson pedPerson = new PedPerson(sampleName, sampleName, "0", "0", Sex.UNKNOWN,
					Disease.AFFECTED);
			final PedFileContents pedContents = new PedFileContents(ImmutableList.of(),
					ImmutableList.of(pedPerson));
			return new Pedigree(pedContents, pedContents.getIndividuals().get(0).getPedigree());
		}
	}

	/**
	 * Construct the mendelian inheritance annotation processors
	 * 
	 * @param writer
	 *            the place to put put the VariantContext to after filtration
	 * @param vcfHeader
	 *            {@link VCFHeader}, for checking compatibility and getting sample name in case of
	 *            singleton pedigree construction
	 * @throws IOException
	 *             in case of problems with opening the pedigree file
	 * @throws PedParseException
	 *             in the case of problems with parsing pedigrees
	 * @throws IncompatiblePedigreeException
	 *             If the pedigree is incompatible with the VCF file
	 */
	private VariantContextProcessor buildMendelianProcessors(VariantContextWriter writer,
			VCFHeader vcfHeader)
			throws PedParseException, IOException, IncompatiblePedigreeException {
		if (options.pathPedFile != null || options.annotateAsSingletonPedigree) {
			final Pedigree pedigree = loadPedigree(vcfHeader);
			checkPedigreeCompatibility(pedigree, vcfHeader);
			final GeneWiseMendelianAnnotationProcessor mendelProcessor = new GeneWiseMendelianAnnotationProcessor(
					pedigree, jannovarData, vc -> writer.add(vc),
					options.isInheritanceAnnoUseFilters());
			return new CoordinateSortingChecker(mendelProcessor);
		} else {
			return new ConsumerProcessor(vc -> writer.add(vc));
		}
	}

	/**
	 * Check pedigree for compatibility
	 * 
	 * @param pedigree
	 *            {@link Pedigree} to check for compatibility
	 * @param vcfHeader
	 *            {@link VCFHeader} to check for compatibility
	 * @throws IncompatiblePedigreeException
	 *             if the VCF file is not compatible with the pedigree
	 */
	private void checkPedigreeCompatibility(Pedigree pedigree, VCFHeader vcfHeader)
			throws IncompatiblePedigreeException {
		List<String> missing = vcfHeader.getGenotypeSamples().stream()
				.filter(x -> !pedigree.getNames().contains(x)).collect(Collectors.toList());
		if (!missing.isEmpty())
			throw new IncompatiblePedigreeException(
					"The VCF file has the following sample names not present in Pedigree: "
							+ Joiner.on(", ").join(missing));
	}

}
