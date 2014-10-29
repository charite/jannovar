package jannovar;

import jannovar.common.Constants.Release;

/**
 * Configuration for the Jannovar annotation process.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter Robinson <peter.robinson@charite.de>
 */
public class JannovarOptions {
	/**
	 * Flag to indicate that Jannovar should download known gene definitions files from the UCSC server.
	 */
	public boolean createUCSC = false;

	/**
	 * Flag to indicate Jannovar should download transcript definition files for RefSeq.
	 */
	public boolean createRefseq = false;

	/**
	 * Flag to indicate Jannovar should download transcript definition files for Ensembl.
	 */
	public boolean createEnsembl = false;

	/**
	 * Flag indicating if the RefSeq serialized outputfile should only contain curated entries.
	 */
	public boolean onlyCuratedRefSeq = false;

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
	 * Location of a directory which will be used as download directory with subfolders (by genome release e.g.
	 * hg19,mm9) in whichthe files defining the transcript models will be stored. (the files may or may not be
	 * compressed with gzip). The same variable is also used to indicate the output location of the serialized file. The
	 * default value is "data/hg19/"
	 */
	public String dirPath = null;

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

	/** chromosomal position an NA change (e.g. chr1:12345C>A) */
	public String chromosomalChange = null;

	/**
	 * genome release for the download and the creation of the serialized transcript model file
	 */
	public Release genomeRelease = Release.HG19;

	/** Output folder for the annotated VCF files (default: current folder) */
	public String outVCFfolder = null;

	/** @return true if we should annotate a VCF file */
	public boolean hasVCFfile() {
		return VCFfilePath != null;
	}

	/**
	 * @return true if we should deserialize a file with transcript model data to perform analysis
	 */
	public boolean deserialize() {
		return serializedFile != null;
	}

	public JannovarOptions() {
	}
}
