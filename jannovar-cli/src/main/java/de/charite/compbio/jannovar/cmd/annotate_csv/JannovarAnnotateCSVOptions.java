package de.charite.compbio.jannovar.cmd.annotate_csv;

import java.util.function.BiFunction;

import org.apache.commons.csv.CSVFormat;

import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationOptions;
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
	private CSVFormat format;
	private boolean header;
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

		Subparser subParser = subParsers.addParser("annotate-csv", true).help("Annotate a csv file").setDefault("cmd",
				handler);
		subParser.description("Perform annotation of genomic changes given on the command line");
		ArgumentGroup requiredGroup = subParser.addArgumentGroup("Required arguments");
		requiredGroup.addArgument("-d", "--database").help("Path to database .ser file").required(true);
		requiredGroup.addArgument("-i", "--input").help("CSV file").required(true);
		requiredGroup.addArgument("-c", "--chr").type(Integer.class).help("Column of chr (1 based)").required(true);
		requiredGroup.addArgument("-p", "--pos").type(Integer.class).help("Column of pos (1 based)").required(true);
		requiredGroup.addArgument("-r", "--ref").type(Integer.class).help("Column of ref (1 based)").required(true);
		requiredGroup.addArgument("-a", "--alt").type(Integer.class).help("Column of alt (1 based)").required(true);
		ArgumentGroup optionalGroup = subParser.addArgumentGroup("Additional CSV arguments (optional)");
		optionalGroup.addArgument("-t", "--type").type(CSVFormat.Predefined.class)
				.choices(CSVFormat.Predefined.Default, CSVFormat.Predefined.TDF, CSVFormat.Predefined.RFC4180,
						CSVFormat.Predefined.Excel, CSVFormat.Predefined.MySQL)
				.help("Type of csv file. ").setDefault(CSVFormat.Predefined.Default);
		optionalGroup.addArgument("--header").help("Set if the file contains a header. ").setDefault(false)
				.action(Arguments.storeTrue());

		subParser.epilog(
				"Example: java -jar Jannovar.jar annotate-csv -d hg19_refseq.ser -c 1 -p 2 -r 3 -r 4 -t TDF --header -i input.csv");

		JannovarAnnotationOptions.setupParser(subParser);
	}

	@Override
	public void setFromArgs(Namespace args) throws CommandLineParsingException {
		super.setFromArgs(args);

		csv = args.getString("input");
		format = ((CSVFormat.Predefined) args.get("type")).getFormat();
		chr = args.getInt("chr") - 1;
		pos = args.getInt("pos") - 1;
		ref = args.getInt("ref") - 1;
		alt = args.getInt("alt") - 1;
		header = args.getBoolean("header");
		if ( header) 
			format = format.withFirstRecordAsHeader().withSkipHeaderRecord();

	}

	/**
	 * @return the set position of the chr in the CSV file (0 based)
	 */
	public int getChr() {
		return chr;
	}

	/**
	 * @return the set position of the alternative in the CSV file (0 based)
	 */
	public int getAlt() {
		return alt;
	}

	/**
	 * @return the set position of the reference in the CSV file (0 based)
	 */
	public int getRef() {
		return ref;
	}

	/**
	 * @return the set path to the csv file
	 */
	public String getCsv() {
		return csv;
	}

	/**
	 * @return the set position of the chromosomal position in the CSV file (0 based)
	 */
	public int getPos() {
		return pos;
	}

	/**
	 * @return the set csv format.
	 */
	public CSVFormat getFormat() {
		return format;
	}
	
	/**
	 * @return if the files contains a header
	 */
	public boolean isHeader() {
		return header;
	}
	

	@Override
	public String toString() {
		return "JannovarAnnotateCSVOptions [csv=" + csv + ", format=" + format + ", chr=" + chr + ", pos=" + pos
				+ ", ref=" + ref + ", alt=" + alt + ", header?=" + header + ", toString()=" + super.toString() + "]";
	}

}
