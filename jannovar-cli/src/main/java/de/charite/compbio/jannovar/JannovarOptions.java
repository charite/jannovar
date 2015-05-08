package de.charite.compbio.jannovar;

import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;

import com.google.common.collect.ImmutableList;

/**
 * Configuration for the Jannovar program.
 *
 * This class contains the configuration for all Jannovar commands, even though most are not used by some commands. For
 * example, the proxy setting is only used when downloading data.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public final class JannovarOptions {

	/** the version of the program */
	public static final String JANNOVAR_VERSION = "0.14";

	/** the selected command */
	public Command command = null;

	/** whether to print progress bars to stderr or not. */
	public boolean printProgressBars = false;

	// TODO(holtgrew): Verbosity level should go to jannovar-cli/
	/** verbosity level */
	public int verbosity = 1;

	/** paths to INI files ot use for parsing */
	public ImmutableList<String> dataSourceFiles = null;

	/** data source name to use for downloading and parsing */
	public ImmutableList<String> dataSourceNames = null;

	/** directory to use for the downloads and the serialized file */
	public String downloadPath = "data";

	/** proxy for HTTP */
	public URL httpProxy = null;

	/** proxy for HTTPS */
	public URL httpsProxy = null;

	/** proxy for FTP */
	public URL ftpProxy = null;

	// Configuration for the annotate command

	/** path to a VCF file to be annotated */
	public ArrayList<String> vcfFilePaths = new ArrayList<String>();

	/** path to the file with the serialized data */
	public String dataFile = null;

	/** whether to write the result in the Jannovar format */
	public boolean jannovarFormat = false;

	/** whether to report the annotations for all affected transcripts for each alternative allele */
	public boolean showAll = false;

	/** path to output folder for the annotated VCF files (default is parallel to input file) */
	public String outVCFFolder = null;

	/** whether or not to write out the old Jannovar annotation VCF tags */
	public boolean writeJannovarInfoFields = false;

	/** whether or not to write out the VCF annotation standard fields */
	public boolean writeVCFAnnotationStandardInfoFields = true;

	/** whether or not to escape characters like <code>'='</code> in the <code>ANN</code> VCF field. */
	public boolean escapeAnnField = true;

	/** whether or not to shift variants towards the 3' end of the transcript */
	public boolean nt3PrimeShifting = true;

	/** infix added to output file name before .vcf/.vcf.gz/.bcf output */
	public String outputInfix = ".jv";

	// TODO(holtgrem): enable and use this!
	/** path to output VCF file path (overrides generation of file name from input file name) */
	public String outVCFFile = null;

	// Configuration for the annotate-position command

	/** chromosomal position and a change, e.g. "chr1:12345C>A" */
	public ArrayList<String> chromosomalChanges = new ArrayList<String>();

	/**
	 * The command that is to be executed.
	 */
	public enum Command {
		DOWNLOAD, ANNOTATE_VCF, ANNOTATE_POSITION, DB_LIST
	}

	/**
	 * Print option values to stderr.
	 */
	public void print(PrintStream out) {
		out.println("verbosity: " + verbosity);
		if (command == Command.DOWNLOAD) {
			out.println("dataSourceFiles: " + dataSourceFiles);
			out.println("dataSourceNames: " + dataSourceNames);
			out.println("downloadPath" + downloadPath);
			out.println("HTTP proxy: " + httpProxy);
			out.println("HTTPS proxy: " + httpsProxy);
			out.println("FTP proxy: " + ftpProxy);
		} else if (command == Command.ANNOTATE_VCF || command == Command.ANNOTATE_POSITION) {
			out.println("dataFile: " + dataFile);
			out.println("vcfFilePaths: " + vcfFilePaths);
			out.println("chromosomalChanges: " + chromosomalChanges);
			out.println("showAll: " + showAll);
			out.println("jannovarFormat: " + jannovarFormat);
			out.println("writeJannovarInfoFields: " + writeJannovarInfoFields);
			out.println("writeVCFAnnotationStandardInfoFields: " + writeVCFAnnotationStandardInfoFields);
			out.println("output infix: " + outputInfix);
		} else if (command == Command.DB_LIST) {
			out.println("dataSourceFiles: " + dataSourceFiles);
		}
	}

}
