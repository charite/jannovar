package de.charite.compbio.jannovar.cmd;

import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Configuration for the annotation commands
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
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
				+ databaseFilePath + ", isReportProgress()=" + isReportProgress() + ", getHttpProxy()=" + getHttpProxy()
				+ ", getHttpsProxy()=" + getHttpsProxy() + ", getFtpProxy()=" + getFtpProxy() + "]";
	}

}
