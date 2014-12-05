package jannovar.io;

import jannovar.reference.TranscriptModel;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

/**
 * Base class for parsers of UCSC data (KnownGene and RefSeq).
 *
 * @author Peter N Robinson
 * @version 0.01 (10 July, 2013)
 */
public class TranscriptDataParser {

	/**
	 * Path of directory in which the transcript definition files live ( currently, either the UCSC knownGene files, the
	 * refSeq files, or the ENSEMBL files).
	 */
	protected String basePath;

	/**
	 * Map of all known genes. Note that the key is the UCSC id, e.g., uc0001234.3, and the value is the corresponding
	 * TranscriptModel object
	 *
	 * @see jannovar.reference.TranscriptModel
	 */
	protected HashMap<String, TranscriptModel> knownGeneMap = null;

	/**
	 * The constructor initializes the Map of {@link jannovar.reference.TranscriptModel TranscriptModel} objects and
	 * sets the directory path for the downloaded files that will be parsed by one of the subclasses.
	 *
	 * @param path
	 *            Location of a directory that must contain the files that will be downloaded
	 */
	public TranscriptDataParser(String path) {
		this.knownGeneMap = new HashMap<String, TranscriptModel>();
		if (path.endsWith("/")) {
			this.basePath = path;
		} else {
			this.basePath = path + "/"; // add trailing slash.
		}
	}

	/**
	 * Construct the object and also set proxy properties for http connection.
	 *
	 * @param dirpath
	 *            Location of a directory that must contain the files that will be downloaded
	 * @param proxyHost
	 *            PROXY host
	 * @param port
	 *            PROXY port
	 */
	public TranscriptDataParser(String dirpath, String proxyHost, String port) {
		this(dirpath);
		if (proxyHost == null)
			return; /* Do not set proxy if proxyHost is null. */
		System.setProperty("proxySet", "true");
		if (proxyHost.startsWith("http://"))
			proxyHost = proxyHost.substring(7);
		System.setProperty("http.proxyHost", proxyHost);
		System.setProperty("http.proxyPort", port);
	}

	/**
	 * @return a reference to the {@link #knownGeneMap knownGeneMap}, which contains info and sequences on all genes.
	 */
	public HashMap<String, TranscriptModel> getKnownGeneMap() {
		return this.knownGeneMap;
	}

	/**
	 * @return a List of all {@link jannovar.reference.TranscriptModel TranscriptModel} objects.
	 */
	public ArrayList<TranscriptModel> getKnownGeneList() {
		ArrayList<TranscriptModel> lst = new ArrayList<TranscriptModel>(this.knownGeneMap.values());
		return lst;
	}

	/**
	 * Open a file handle from a compressed (gzip) or uncompressed file
	 *
	 * @param path
	 *            Path to the file to be opened
	 * @param isGzip
	 *            true if the file is compressed (gzip).
	 * @return Corresponding BufferedReader file handle.
	 * @throws java.io.IOException
	 */
	protected BufferedReader getBufferedReaderFromFilePath(String path, boolean isGzip) throws IOException {
		FileInputStream fin = new FileInputStream(path);
		BufferedReader br;
		if (isGzip) {
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(fin)));
		} else {
			DataInputStream in = new DataInputStream(fin);
			br = new BufferedReader(new InputStreamReader(in));
		}
		return br;
	}

	/**
	 * This function creates a new directory to store the downloaded files. If the directory already exists, it just
	 * emits a warning and does nothing.
	 */
	protected void makeDirectoryIfNotExist() {
		File directory = new File(this.basePath);
		if (directory.exists()) {
			System.err.println(String.format("Cowardly refusing to create " + "directory \"%s\" since it already exists", this.basePath));
		} else {
			directory.mkdir();
		}
	}

}