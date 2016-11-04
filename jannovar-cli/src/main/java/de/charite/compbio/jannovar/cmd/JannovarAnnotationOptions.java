package de.charite.compbio.jannovar.cmd;

import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * Configuration for the annotation commands
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public class JannovarAnnotationOptions extends JannovarBaseOptions {

	/** Whether to use 3 letter amino acid code instead of 1 letter */
	private boolean useThreeLetterAminoAcidCode = false;

	/** Whether or not to shift variants towards the 3' end of the transcript */
	private boolean nt3PrimeShifting = false;

	/** Show all effects */
	private boolean showAll = false;

	/** Path to database file */
	private String databaseFilePath = null;
	
	/**
	 * Setup {@link ArgumentParser}
	 * 
	 * @param subParsers
	 *            {@link Subparsers} to setup
	 */
	public static void setupParser(ArgumentParser subParser) {
		ArgumentGroup optionalGroup = subParser.addArgumentGroup("Optional Arguments");
		optionalGroup.addArgument("--show-all").help("Show all effects").setDefault(false).action(Arguments.storeTrue());
		optionalGroup.addArgument("--no-3-prime-shifting").help("Disable shifting towards 3' of transcript")
				.dest("3_prime_shifting").setDefault(true).action(Arguments.storeFalse());
		optionalGroup.addArgument("--3-letter-amino-acids").help("Enable usage of 3 letter amino acid codes")
				.setDefault(false).action(Arguments.storeTrue());
		
		JannovarBaseOptions.setupParser(subParser);
	}
	
	

	@Override
	public void setFromArgs(Namespace args) throws CommandLineParsingException {
		super.setFromArgs(args);

		useThreeLetterAminoAcidCode = args.getBoolean("3_letter_amino_acids");
		nt3PrimeShifting = !args.getBoolean("3_prime_shifting");
		databaseFilePath = args.getString("database");
		showAll = args.getBoolean("show_all");
	}

	public boolean isUseThreeLetterAminoAcidCode() {
		return useThreeLetterAminoAcidCode;
	}

	public void setUseThreeLetterAminoAcidCode(boolean useThreeLetterAminoAcidCode) {
		this.useThreeLetterAminoAcidCode = useThreeLetterAminoAcidCode;
	}

	public boolean isNt3PrimeShifting() {
		return nt3PrimeShifting;
	}

	public void setNt3PrimeShifting(boolean nt3PrimeShifting) {
		this.nt3PrimeShifting = nt3PrimeShifting;
	}

	public String getDatabaseFilePath() {
		return databaseFilePath;
	}

	public void setDatabaseFilePath(String databaseFilePath) {
		this.databaseFilePath = databaseFilePath;
	}

	public boolean isShowAll() {
		return showAll;
	}

	public void setShowAll(boolean showAll) {
		this.showAll = showAll;
	}

	@Override
	public String toString() {
		return "JannovarAnnotationOptions [useThreeLetterAminoAcidCode=" + useThreeLetterAminoAcidCode
				+ ", nt3PrimeShifting=" + nt3PrimeShifting + ", showAll=" + showAll + ", databaseFilePath="
				+ databaseFilePath + ", toString()=" + super.toString() + "]";
	}

}
