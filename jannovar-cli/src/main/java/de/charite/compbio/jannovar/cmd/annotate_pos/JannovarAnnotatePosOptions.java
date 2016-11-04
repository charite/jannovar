package de.charite.compbio.jannovar.cmd.annotate_pos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationOptions;
import de.charite.compbio.jannovar.cmd.JannovarBaseOptions;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * Options for the <tt>annotate-pos</tt> comman
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class JannovarAnnotatePosOptions extends JannovarAnnotationOptions {

	/** List of Strings with genomic changes to parse */
	private List<String> genomicChanges = new ArrayList<>();

	/**
	 * Setup {@link ArgumentParser}
	 * 
	 * @param subParsers
	 *            {@link Subparsers} to setup
	 */
	public static void setupParser(Subparsers subParsers) {
		BiFunction<String[], Namespace, AnnotatePositionCommand> handler = (argv, args) -> {
			try {
				return new AnnotatePositionCommand(argv, args);
			} catch (CommandLineParsingException e) {
				throw new UncheckedJannovarException("Could not parse command line", e);
			}
		};

		Subparser subParser = subParsers.addParser("annotate-pos", true)
				.help("annotate genomic changes given on the command line").setDefault("cmd", handler);
		subParser.description("Perform annotation of genomic changes given on the command line");
		subParser.addArgument("-d", "--database").help("Path to database .ser file").required(true);
		subParser.addArgument("-c", "--genomic-change").help("Genomic change to annotate, you can give multiple ones")
				.action(Arguments.append()).required(true);

		ArgumentGroup optionalGroup = subParser.addArgumentGroup("Optional Arguments");
		optionalGroup.addArgument("--show-all").help("Show all effects").setDefault(false);
		optionalGroup.addArgument("--no-3-prime-shifting").help("Disable shifting towards 3' of transcript")
				.dest("3_prime_shifting").setDefault(true).action(Arguments.storeFalse());
		optionalGroup.addArgument("--3-letter-amino-acids").help("Enable usage of 3 letter amino acid codes")
				.setDefault(false).action(Arguments.storeTrue());
		
		subParser.epilog("Example: java -jar Jannovar.jar annotate-pos -d hg19_refseq.ser -c 'chr1:12345C>A'");

		JannovarBaseOptions.setupParser(subParser);
	}

	@Override
	public void setFromArgs(Namespace args) throws CommandLineParsingException {
		super.setFromArgs(args);

		genomicChanges = args.getList("genomic_change");
	}

	public List<String> getGenomicChanges() {
		return genomicChanges;
	}

	public void setGenomicChanges(List<String> genomicChanges) {
		this.genomicChanges = genomicChanges;
	}

	@Override
	public String toString() {
		return "JannovarAnnotatePosOptions [genomicChanges=" + genomicChanges + ", isUseThreeLetterAminoAcidCode()="
				+ isUseThreeLetterAminoAcidCode() + ", isNt3PrimeShifting()=" + isNt3PrimeShifting()
				+ ", getDatabaseFilePath()=" + getDatabaseFilePath() + ", isReportProgress()=" + isReportProgress()
				+ ", getHttpProxy()=" + getHttpProxy() + ", getHttpsProxy()=" + getHttpsProxy() + ", getFtpProxy()="
				+ getFtpProxy() + "]";
	}

}
