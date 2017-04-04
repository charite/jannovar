package de.charite.compbio.jannovar.cmd.statistics;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationCommand;
import de.charite.compbio.jannovar.htsjdk.InvalidCoordinatesException;
import de.charite.compbio.jannovar.htsjdk.VariantContextAnnotator;
import de.charite.compbio.jannovar.stats.facade.StatisticsCollector;
import de.charite.compbio.jannovar.stats.facade.StatisticsWriter;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Jannovar command for gathering statistics about variant effect distribution etc.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GatherStatisticsCommand extends JannovarAnnotationCommand {

	/** Configuration */
	private JannovarGatherStatisticsOptions options;

	public GatherStatisticsCommand(String argv[], Namespace args) throws CommandLineParsingException {
		this.options = new JannovarGatherStatisticsOptions();
		this.options.setFromArgs(args);
	}

	/**
	 * Compute the statistics
	 */
	@Override
	public void run() throws JannovarException {
		System.err.println("Options");
		System.err.println(options.toString());

		System.err.println("Deserializing transcripts...");
		deserializeTranscriptDefinitionFile(options.getDatabaseFilePath());
		final boolean isUtrOffTarget = false;
		final boolean isIntronicSpliceOffTarget = false;
		VariantContextAnnotator annotator = new VariantContextAnnotator(refDict, chromosomeMap,
				new VariantContextAnnotator.Options(false, false, false, false, isUtrOffTarget,
						isIntronicSpliceOffTarget));

		Map<String, Integer> errorMsgs = new TreeMap<>();

		String prevChrom = null;
		
		System.err.println("Opening VCF file...");
		final String vcfPath = options.getPathInputVCF();
		try (VCFFileReader vcfReader = new VCFFileReader(new File(vcfPath), false)) {
			System.err.println("Gathering statistics...");
			final long startTime = System.nanoTime();
			StatisticsCollector statsCollector = new StatisticsCollector(
					vcfReader.getFileHeader().getSampleNamesInOrder());

			for (VariantContext vc : vcfReader) {
				if (!vc.getContig().equals(prevChrom)) {
					prevChrom = vc.getContig();
					System.err.println("Starting on contig " + prevChrom);
				}
				
				try {
					statsCollector.put(vc, annotator.buildAnnotations(vc));
				} catch (InvalidCoordinatesException e) {
					errorMsgs.putIfAbsent(e.getMessage(), 0);
					errorMsgs.put(e.getMessage(), errorMsgs.get(e.getMessage()) + 1);
				}
			}

			System.err.println("Writing out statistics...");
			try (StatisticsWriter writer = new StatisticsWriter(statsCollector,
					new File(options.getPathOutputReport()))) {
				writer.writeStatistics();
			} catch (FileNotFoundException e) {
				System.err.println("Could not open report output file");
				e.printStackTrace();
			} catch (Exception e1) {
				System.err.println("Unhandled exception");
				e1.printStackTrace();
			}

			System.err.println("The following error messages occured");
			for (Entry<String, Integer> e : errorMsgs.entrySet())
				System.err.println(e.getValue() + " times: " + e.getKey());

			System.err.println("Wrote report to \"" + options.getPathOutputReport() + "\".");
			final long endTime = System.nanoTime();
			System.err.println(String.format("Annotation, statistics gathering and writing took %.2f sec.",
					(endTime - startTime) / 1000.0 / 1000.0 / 1000.0));
		}
	}

}
