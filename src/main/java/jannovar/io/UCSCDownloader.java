package jannovar.io;

import jannovar.common.Constants;
import jannovar.exception.KGParseException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class downloads the four files we need to generate TranscriptModel objects from the UCSC server.
 *
 * @version 0.02 (10 July, 2013)
 * @author Peter Robinson
 */
public final class UCSCDownloader implements Constants {

	/** Path of directory to which the files will be downloaded. */
	private String downloadDirectory;
	/** Base URI for UCSC hg19 build annotation files */
	private String hg19URLBase = "http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/";

	/**
	 * This constructor sets the location of the directory into which the UCSC data will be downloaded.
	 *
	 * @param dirpath
	 *            Location of download directory.
	 */
	public UCSCDownloader(String dirpath) {
		if (!dirpath.endsWith("/")) {
			dirpath = dirpath + "/"; // add trailing slash.
		}
		this.downloadDirectory = dirpath;
	}

	/** @return path to the download directory */
	public String getDownloadDirectory() {
		return this.downloadDirectory;
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
	public UCSCDownloader(String dirpath, String proxyHost, String port) {
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
	 * This function first checks if the download directory already exists. If not, it tries to create the directory. It
	 * then tries to download the four required files from the UCSC genome browser (if a file already exists, it emits a
	 * warning message and skips it).
	 *
	 * @throws jannovar.exception.KGParseException
	 */
	public void downloadUCSCfiles() throws KGParseException {
		makeDirectoryIfNotExist();
		String knownGene = String.format("%s.gz", Constants.knownGene);
		String knownGeneMrna = String.format("%s.gz", Constants.knownGeneMrna);
		String kgXref = String.format("%s.gz", Constants.kgXref);
		String known2locus = String.format("%s.gz", Constants.known2locus);
		downloadFile(this.hg19URLBase, knownGene);
		downloadFile(this.hg19URLBase, knownGeneMrna);
		downloadFile(this.hg19URLBase, kgXref);
		downloadFile(this.hg19URLBase, known2locus);
	}

	/**
	 * This function creates a new directory to store the downloaded UCSC files. If the directory already exists, it
	 * just emits a warning and does nothing.
	 */
	private void makeDirectoryIfNotExist() {
		File directory = new File(this.downloadDirectory);
		if (directory.exists()) {
			System.err.println(String.format("Cowardly refusing to create " + "directory \"%s\" since it already exists", this.downloadDirectory));
		} else {
			directory.mkdir();
		}
	}

	/**
	 * This method downloads a file to the specified local file path. If the file already exists, it emits a warning
	 * message and does nothing.
	 *
	 * @param baseURL
	 *            remote directory path
	 * @param fname
	 *            remote file name
	 * @return <code>true</code> if file was downloaded successfully
	 * @throws jannovar.exception.KGParseException
	 */
	public boolean downloadFile(String baseURL, String fname) throws KGParseException {

		String urlstring = baseURL + fname;
		String local_file_path = this.downloadDirectory + fname;
		File f = new File(local_file_path);
		if (f.exists()) {
			System.err.println(String.format("[INFO] Timorously refusing to download " + "file \"%s\" since it already exists", local_file_path));
			return false;

		}
		System.err.println("[INFO] Downloading: \"" + urlstring + "\"");

		// The error handling can be improved with Java 7.
		String err = null;
		InputStream reader = null;
		FileOutputStream writer = null;

		int threshold = 0;
		int block = 250000;
		try {
			URL url = new URL(urlstring);
			URLConnection urlc = url.openConnection();
			reader = urlc.getInputStream();
			writer = new FileOutputStream(local_file_path);
			byte[] buffer = new byte[153600];
			int totalBytesRead = 0;
			int bytesRead = 0;
			int size = urlc.getContentLength();
			if (size >= 0)
				block = size / 20;
			System.err.println("0%       50%      100%");
			while ((bytesRead = reader.read(buffer)) > 0) {
				writer.write(buffer, 0, bytesRead);
				buffer = new byte[153600];
				totalBytesRead += bytesRead;
				if (totalBytesRead > threshold) {
					System.err.print("=");
					threshold += block;
				}
			}
			System.err.println();
			System.err.println("[INFO] Done. " + (new Integer(totalBytesRead).toString()) + "(" + size + ") bytes read.");
		} catch (MalformedURLException e) {
			err = String.format("Could not interpret url: \"%s\"\n%s", urlstring, e.toString());
		} catch (IOException e) {
			err = String.format("IO Exception reading from URL: \"%s\"\n%s", urlstring, e.toString());
		} finally {
			try {
				if (writer != null)
					writer.close();
			} catch (IOException e) {
				// swallow, nothing we can do
			}
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				// swallow, nothing we can do
			}
		}
		if (err != null)
			throw new KGParseException(err);

		return true;
	}
}
