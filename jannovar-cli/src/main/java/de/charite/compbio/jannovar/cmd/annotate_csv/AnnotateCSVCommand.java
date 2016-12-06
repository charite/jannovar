package de.charite.compbio.jannovar.cmd.annotate_csv;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.annotation.AllAnnotationListTextGenerator;
import de.charite.compbio.jannovar.annotation.AnnotationException;
import de.charite.compbio.jannovar.annotation.BestAnnotationListTextGenerator;
import de.charite.compbio.jannovar.annotation.VariantAnnotations;
import de.charite.compbio.jannovar.annotation.VariantAnnotationsTextGenerator;
import de.charite.compbio.jannovar.annotation.VariantAnnotator;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationCommand;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Allows the annotation of a CVF file.
 *
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public class AnnotateCSVCommand extends JannovarAnnotationCommand {

	/** Configuration */
	private JannovarAnnotateCSVOptions options;

	/**
	 * @param argv
	 * @param args
	 * @throws CommandLineParsingException
	 */
	public AnnotateCSVCommand(String argv[], Namespace args) throws CommandLineParsingException {
		this.options = new JannovarAnnotateCSVOptions();
		this.options.setFromArgs(args);
	}

	/**
	 * This function will simply annotate a csv file wehere positions are set
	 *
	 *
	 * @param options
	 *            configuration for the command
	 * @throws AnnotationException
	 *             on problems in the annotation process
	 */
	@Override
	public void run() throws JannovarException {
		System.err.println("Options");
		System.err.println(options.toString());

		System.err.println("Deserializing transcripts...");
		deserializeTranscriptDefinitionFile(options.getDatabaseFilePath());

		final VariantAnnotator annotator = new VariantAnnotator(refDict, chromosomeMap, new AnnotationBuilderOptions());

		try {
			Reader in = new FileReader(options.getCsv());
			final Appendable out = System.out;
			CSVParser parser = options.getFormat().parse(in);

			final CSVPrinter printer = options.getFormat().print(out);

			if (options.isHeader()) {
				List<String> header = new ArrayList<>(parser.getHeaderMap().size() + 2);
				for (Map.Entry<String, Integer> entry : parser.getHeaderMap().entrySet()) {
					header.add(entry.getValue(), entry.getKey());
				}
				header.add(parser.getHeaderMap().size(), "HGVS");
				header.add(parser.getHeaderMap().size()+1, "FunctionalClass");

				printer.printRecord(header);
			}

			for (CSVRecord record : parser) {

				// Parse the chromosomal change string into a GenomeChange object.
				String chromosomalChange = getChromosomalChange(record);
				final GenomeVariant genomeChange = parseGenomeChange(chromosomalChange);

				// Construct VariantAnnotator for building the variant annotations.
				VariantAnnotations annoList = null;
				try {
					annoList = annotator.buildAnnotations(genomeChange);
				} catch (Exception e) {
					System.err.println(String.format("[ERROR] Could not annotate variant %s!", chromosomalChange));
					e.printStackTrace(System.err);
					continue;
				}

				for (String string : record) {
					printer.print(string);
				}
				VariantAnnotationsTextGenerator textGenerator;
				if (options.isShowAll())
					textGenerator = new AllAnnotationListTextGenerator(annoList, 0, 1);
				else
					textGenerator = new BestAnnotationListTextGenerator(annoList, 0, 1);

				printer.print(textGenerator.buildHGVSText(options.isUseThreeLetterAminoAcidCode()
						? AminoAcidCode.THREE_LETTER : AminoAcidCode.ONE_LETTER));
				printer.print(annoList.getHighestImpactEffect());
				printer.println();
			}
			parser.close();
			printer.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			throw new JannovarException(e1.getMessage());
		}

	}

	private String getChromosomalChange(CSVRecord record) {
		return record.get(options.getChr()) + ":" + record.get(options.getPos()) + record.get(options.getRef())
				+ ">" + record.get(options.getAlt());
	}

	private GenomeVariant parseGenomeChange(String changeStr) throws JannovarException {
		Pattern pat = Pattern.compile("(chr[0-9MXY]+):([0-9]+)([ACGTN]*)>([ACGTN]*)");
		Matcher match = pat.matcher(changeStr);

		if (!match.matches()) {
			System.err.println("[ERROR] Input string for the chromosomal change " + changeStr
					+ " does not fit the regular expression ... :(");
			System.exit(3);
		}

		int chr = refDict.getContigNameToID().get(match.group(1));
		int pos = Integer.parseInt(match.group(2));
		String ref = match.group(3);
		String alt = match.group(4);

		return new GenomeVariant(new GenomePosition(refDict, Strand.FWD, chr, pos, PositionType.ONE_BASED), ref, alt);
	}

}
