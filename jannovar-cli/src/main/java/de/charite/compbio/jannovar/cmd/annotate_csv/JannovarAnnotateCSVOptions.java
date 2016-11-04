package de.charite.compbio.jannovar.cmd.annotate_csv;

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
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public class JannovarAnnotateCSVOptions extends JannovarAnnotationOptions {

	/** List of Strings with genomic changes to parse */
	private String csv;
	private int chr;
	private int pos;
	private int ref;
	private int alt;

	/**
	 * Setup {@link ArgumentParser}
	 * 
	 * @param subParsers
	 *            {@link Subparsers} to setup
	 */
	public static void setupParser(Subparsers subParsers) {
		BiFunction<String[], Namespace, AnnotateCSVCommand> handler = (argv, args) -> {
			try {
				return new AnnotateCSVCommand(argv, args);
			} catch (CommandLineParsingException e) {
				throw new UncheckedJannovarException("Could not parse command line", e);
			}
		};

		Subparser subParser = subParsers.addParser("annotate-csv", true).help("a csv file").setDefault("cmd", handler);
		subParser.description("Perform annotation of genomic changes given on the command line");
		subParser.addArgument("-d", "--database").help("Path to database .ser file").required(true);
		subParser.addArgument("-i", "--input").help("CSV file").required(true);
		subParser.addArgument("-c", "--chr").type(Integer.class).help("Column of chr").required(true);
		subParser.addArgument("-p", "--pos").type(Integer.class).help("Column of pos").required(true);
		subParser.addArgument("-r", "--ref").type(Integer.class).help("Column of ref").required(true);
		subParser.addArgument("-a", "--alt").type(Integer.class).help("Column of alt").required(true);

		ArgumentGroup optionalGroup = subParser.addArgumentGroup("Optional Arguments");
		optionalGroup.addArgument("--show-all").help("Show all effects").setDefault(false);
		optionalGroup.addArgument("--no-3-prime-shifting").help("Disable shifting towards 3' of transcript")
				.dest("3_prime_shifting").setDefault(true).action(Arguments.storeFalse());
		optionalGroup.addArgument("--3-letter-amino-acids").help("Enable usage of 3 letter amino acid codes")
				.setDefault(false).action(Arguments.storeTrue());

		subParser.epilog(
				"Example: java -jar Jannovar.jar annotate-csv -d hg19_refseq.ser -c 1 -p 2 -r 3 -r 4 -i input.csv");

		JannovarBaseOptions.setupParser(subParser);
	}

	@Override
	public void setFromArgs(Namespace args) throws CommandLineParsingException {
		super.setFromArgs(args);

		csv = args.getString("input");
		chr = args.getInt("chr")-1;
		pos = args.getInt("pos")-1;
		ref = args.getInt("ref")-1;
		alt = args.getInt("alt")-1;
	}

	public int getChr() {
		return chr;
	}

	public int getAlt() {
		return alt;
	}

	public int getRef() {
		return ref;
	}

	public String getCsv() {
		return csv;
	}

	public int getPos() {
		return pos;
	}

	@Override
	public String toString() {
		return "JannovarAnnotatePosOptions [csv=" + csv + ", isUseThreeLetterAminoAcidCode()="
				+ isUseThreeLetterAminoAcidCode() + ", isNt3PrimeShifting()=" + isNt3PrimeShifting()
				+ ", getDatabaseFilePath()=" + getDatabaseFilePath() + ", isReportProgress()=" + isReportProgress()
				+ ", getHttpProxy()=" + getHttpProxy() + ", getHttpsProxy()=" + getHttpsProxy() + ", getFtpProxy()="
				+ getFtpProxy() + "]";
	}

}
