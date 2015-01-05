package jannovar.cmd.annotate_pos;

import jannovar.JannovarException;
import jannovar.JannovarOptions;
import jannovar.annotation.AnnotationException;
import jannovar.annotation.AnnotationList;
import jannovar.annotation.VariantAnnotator;
import jannovar.cmd.CommandLineParsingException;
import jannovar.cmd.HelpRequestedException;
import jannovar.cmd.JannovarAnnotationCommand;
import jannovar.reference.GenomeChange;
import jannovar.reference.GenomePosition;
import jannovar.reference.PositionType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.ParseException;

/**
 * Allows the annotation of a single position.
 *
 * @author Marten Jaeger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
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
		deserializeTranscriptDefinitionFile();

		// Parse the chromosomal change string into a GenomeChange object.
		System.out.println("input: " + options.chromosomalChange);
		final GenomeChange genomeChange = parseGenomeChange(options.chromosomalChange);

		// Construct VariantAnnotator for building the variant annotations.
		final VariantAnnotator annotator = new VariantAnnotator(refDict, chromosomeMap);
		final AnnotationList annoList = annotator.buildAnnotationList(genomeChange);
		if (annoList == null) {
			String e = String.format("No annotations found for variant %s", options.chromosomalChange);
			throw new AnnotationException(e);
		}

		// Obtain first or all functional annotation(s) and effect(s).
		final String annotation;
		final String effect;
		if (options.showAll) {
			annotation = annoList.getAllTranscriptAnnotations();
			effect = annoList.getAllTranscriptVariantEffects();
		} else {
			annotation = annoList.getSingleTranscriptAnnotation();
			effect = annoList.getVariantType().toString();
		}

		System.out.println(String.format("EFFECT=%s;HGVS=%s", effect, annotation));
	}

	private GenomeChange parseGenomeChange(String changeStr) throws JannovarException {
		Pattern pat = Pattern.compile("(chr[0-9MXY]+):([0-9]+)([ACGTN])>([ACGTN])");
		Matcher match = pat.matcher(options.chromosomalChange);

		if (!match.matches() | match.groupCount() != 4) {
			System.err
			.println("[ERROR] Input string for the chromosomal change does not fit the regular expression ... :(");
			System.exit(3);
		}

		int chr = refDict.contigID.get(match.group(1));
		int pos = Integer.parseInt(match.group(2));
		String ref = match.group(3);
		String alt = match.group(4);

		return new GenomeChange(new GenomePosition(refDict, '+', chr, pos, PositionType.ONE_BASED), ref, alt);
	}

	@Override
	protected JannovarOptions parseCommandLine(String[] argv) throws CommandLineParsingException,
	HelpRequestedException {
		AnnotatePositionCommandLineParser parser = new AnnotatePositionCommandLineParser();
		try {
			return parser.parse(argv);
		} catch (ParseException e) {
			throw new CommandLineParsingException(e.getMessage());
		}
	}

}
