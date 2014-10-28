package jannovar;

import jannovar.common.Constants.Release;

/**
 * Configuration for the Jannovar annotation process.
 *
 * @author holtgrem
 */
public class JannovarOptions {

	/**
	 * Flag to indicate that Jannovar should serialize the UCSC data. This flag is set to true automatically if the user
	 * enters --create-ucsc (then, the four files are downloaded and subsequently serialized). If the user enters the
	 * flag {@code -U path}, then Jannovar interprets path as the location of a directory that already contains the UCSC
	 * files (either compressed or uncompressed), and sets this flag to true to perform serialization and then to exit.
	 * The name of the serialized file that gets created is "ucsc.ser" (this cannot be changed from the command line,
	 * see {@link #UCSCserializationFileName}).
	 */
	public boolean performSerialization = false;

	/**
	 * Name of file with serialized UCSC data. This should be the complete path to the file, and will be used for
	 * annotating VCF files.
	 */
	public String serializedFile = null;

	/** Path to a VCF file waiting to be annotated. */
	public String VCFfilePath = null;

	/** An FTP proxy for downloading the UCSC files from behind a firewall. */
	public String proxy = null;

	/** An FTP proxy port for downloading the UCSC files from behind a firewall. */
	public String proxyPort = null;

	/**
	 * Flag indicating whether to output annotations in Jannovar format (default: false).
	 */
	public boolean jannovarFormat = false;

	/**
	 * Flag indication whether the annotations for all affected transcripts should be reported.
	 */
	public boolean showAll = false;

	/**
	 * genome release for the download and the creation of the serialized transcript model file
	 */
	public Release genomeRelease = Release.HG19;

	/** Output folder for the annotated VCF files (default: current folder) */
	public String outVCFfolder = null;

	public JannovarOptions() {
	}
}
