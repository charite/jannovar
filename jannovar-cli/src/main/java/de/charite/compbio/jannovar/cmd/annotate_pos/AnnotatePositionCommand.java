package de.charite.compbio.jannovar.cmd.annotate_pos;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.ParseException;

import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.JannovarOptions;
import de.charite.compbio.jannovar.annotation.AllAnnotationListTextGenerator;
import de.charite.compbio.jannovar.annotation.AnnotationException;
import de.charite.compbio.jannovar.annotation.BestAnnotationListTextGenerator;
import de.charite.compbio.jannovar.annotation.VariantAnnotations;
import de.charite.compbio.jannovar.annotation.VariantAnnotationsTextGenerator;
import de.charite.compbio.jannovar.annotation.VariantAnnotator;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.HelpRequestedException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationCommand;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;

/**
 * Allows the annotation of a single position.
 *
 * @author <a href="mailto:marten.jaeger@charite.de">Marten Jaeger</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public class AnnotatePositionCommand extends JannovarAnnotationCommand {

	public AnnotatePositionCommand(String argv[]) throws CommandLineParsingException, HelpRequestedException {
		super(argv);
	}

	/**
	 * This function will simply annotate given chromosomal position with HGVS compliant output.
	 *
	 * For example, the change <tt>chr1:909238G&gt;C</tt> could be converted to
	 * <tt>PLEKHN1:NM_032129.2:c.1460G&gt;C,p.(Arg487Pro)</tt>.
	 *
	 * @throws AnnotationException
	 *             on problems in the annotation process
	 */
	@Override
	public void run() throws JannovarException {
		System.err.println("Options");
		options.print(System.err);

		System.err.println("Deserializing transcripts...");
		deserializeTranscriptDefinitionFile();

		final VariantAnnotator annotator = new VariantAnnotator(refDict, chromosomeMap, new AnnotationBuilderOptions());
		System.out.println("#change\teffect\thgvs_annotation");
		for (String chromosomalChange : options.chromosomalChanges) {
			// Parse the chromosomal change string into a GenomeChange object.
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

			// Obtain first or all functional annotation(s) and effect(s).
			final String annotation;
			final String effect;
			VariantAnnotationsTextGenerator textGenerator;
			if (options.showAll)
				textGenerator = new AllAnnotationListTextGenerator(annoList, 0, 1);
			else
				textGenerator = new BestAnnotationListTextGenerator(annoList, 0, 1);
			annotation = textGenerator.buildHGVSText(
					options.useThreeLetterAminoAcidCode ? AminoAcidCode.THREE_LETTER : AminoAcidCode.ONE_LETTER);
			effect = textGenerator.buildEffectText();

			System.out.println(String.format("%s\t%s\t%s", chromosomalChange.toString(), effect, annotation));
		}
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

	@Override
	protected JannovarOptions parseCommandLine(String[] argv)
			throws CommandLineParsingException, HelpRequestedException {
		AnnotatePositionCommandLineParser parser = new AnnotatePositionCommandLineParser();
		try {
			return parser.parse(argv);
		} catch (ParseException e) {
			throw new CommandLineParsingException("Problem with command line parsing.", e);
		}
	}

}
