package jannovar.cmd.annotate_pos;

import jannovar.JannovarOptions;
import jannovar.annotation.AnnotationList;
import jannovar.annotation.VariantAnnotator;
import jannovar.cmd.JannovarAnnotationCommand;
import jannovar.exception.AnnotationException;
import jannovar.exception.CommandLineParsingException;
import jannovar.exception.HelpRequestedException;
import jannovar.exception.JannovarException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.cli.ParseException;

/**
 * Allows the annotation of a single position.
 *
 * Although public, this class is not meant to be part of the public Jannovar intervace. It can be changed or removed at
 * any point.
 *
 * @author Marten Jaeger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class AnnotatePositionCommand extends JannovarAnnotationCommand {

	public AnnotatePositionCommand(String argv[]) throws CommandLineParsingException, HelpRequestedException {
		super(argv);
	}

	/**
	 * THis function will simply annotate given chromosomal position with HGVS compliant output e.g. chr1:909238G>C -->
	 * PLEKHN1:NM_032129.2:c.1460G>C,p.(Arg487Pro)
	 *
	 * @throws AnnotationException
	 */
	@Override
	public void run() throws JannovarException {
		deserializeTranscriptDefinitionFile();

		System.err.println("input: " + options.chromosomalChange);
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

		VariantAnnotator annotator = new VariantAnnotator(refDict, chromosomeMap);
		AnnotationList anno = annotator.getAnnotationList(chr, pos, ref, alt);
		if (anno == null) {
			String e = String.format("No annotations found for variant %s", options.chromosomalChange);
			throw new AnnotationException(e);
		}
		String annotation;
		String effect;
		if (options.showAll) {
			annotation = anno.getAllTranscriptAnnotations();
			effect = anno.getAllTranscriptVariantEffects();
		} else {
			annotation = anno.getSingleTranscriptAnnotation();
			effect = anno.getVariantType().toString();
		}

		System.out.println(String.format("EFFECT=%s;HGVS=%s", effect, annotation));
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
