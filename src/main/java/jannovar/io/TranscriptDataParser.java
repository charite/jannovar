package jannovar.io;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException; 
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.zip.GZIPInputStream;

import jannovar.exception.KGParseException;
import jannovar.reference.TranscriptModel;

/**
 * Base class for parsers of UCSC data (KnownGene and RefSeq).
 * @author Peter N Robinson
 * @version 0.01 (10 July, 2013)
 */
public class TranscriptDataParser {
    

    /** Path of directory in which the transcript definition files live (
	currently, either the UCSC knownGene files, the refSeq files, or
	the ENSEMBL files). */
    protected String directory_path;

    /** Map of all known genes. Note that the key is the UCSC id, e.g., uc0001234.3, and the
     * value is the corresponding TranscriptModel object
     * @see jannovar.reference.TranscriptModel
     */
    protected HashMap<String,TranscriptModel> knownGeneMap=null;
    

    /**
     * The constructor initializes the Map of 
     * {@link jannovar.reference.TranscriptModel TranscriptModel} objects
     * and sets the directory path for the downloaded files that will
     * be parsed by one of the subclasses.
     * @param path Location of a directory that must contain the files
     * that will be downloaded
     */
    public TranscriptDataParser(String path) {
	this.knownGeneMap = new HashMap<String,TranscriptModel>();
	if ( path.endsWith("/")) {
	    this.directory_path = path;
	} else {
	    this.directory_path = path + "/"; // add trailing slash.
	}
    }

     /**
     * Construct the object and also set proxy properties for http connection.
     * @param dirpath Location of a directory that must contain the files that will be downloaded
     * @param proxyHost PROXY host
     * @param port PROXY port
     */
    public TranscriptDataParser(String dirpath, String proxyHost, String port) {
	this(dirpath);
	if (proxyHost == null)
	    return; /* Do not set proxy if proxyHost is null. */
	System.setProperty("proxySet","true");
	if(proxyHost.startsWith("http://"))
	    proxyHost = proxyHost.substring(7);
	System.setProperty("http.proxyHost", proxyHost);
	System.setProperty("http.proxyPort", port);
    }



    

    /**
     * @return a reference to the {@link #knownGeneMap knownGeneMap}, which contains info and sequences on all genes.
     */
    public HashMap<String,TranscriptModel> getKnownGeneMap() {
	return this.knownGeneMap;
    }

    
    /**
     * @return a List of all {@link jannovar.reference.TranscriptModel TranscriptModel} objects.
     */
    public ArrayList<TranscriptModel> getKnownGeneList() {
	ArrayList<TranscriptModel> lst =
	    new ArrayList<TranscriptModel>(this.knownGeneMap.values());
	return lst;
    }


    /**
     * Open a file handle from a compressed (gzip) or uncompressed file 
     * @param path Path to the file to be opened
     * @param isGzip true if the file is compressed (gzip).
     * @return Corresponding BufferedReader file handle.
     * @throws java.io.IOException
     */
    protected BufferedReader getBufferedReaderFromFilePath(String path, boolean isGzip) 
	throws IOException
    {
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
     * This function creates a new directory to store the downloaded
     * files. If the directory already exists, it just emits a
     * warning and does nothing.
     */
    protected void makeDirectoryIfNotExist() {
	File directory = new File(this.directory_path);
	if (directory.exists()) {
	    System.err.println(String.format("Cowardly refusing to create "+
					     "directory \"%s\" since it already exists",
					     this.directory_path));
	} else {
	    directory.mkdir();
	}
    }

    
    /**
     * This method downloads a file to the specified local file path.
     * If the file already exists, it emits a warning message and does nothing.
     * @param baseURL remote directory path
     * @param fname remote file name
     * @return <code>true</code> if file was downloaded successfully
     * @throws jannovar.exception.KGParseException
     */
    public boolean download_file(String baseURL, String fname ) throws KGParseException {

	String urlstring = baseURL + fname;
	String local_file_path = this.directory_path + fname;
	File f = new File(local_file_path);
	if (f.exists()) {
	    System.out.println(String.format("[INFO] Timorously refusing to download "+
					     "file \"%s\" since it already exists",
					     local_file_path));
	    return false;
	}
	System.out.println("[INFO] Downloading: \"" + urlstring + "\"");
	int threshold = 0;
	int block = 250000;
	try{
	    URL url = new URL(urlstring);
	    URLConnection urlc = url.openConnection();
	    InputStream reader = urlc.getInputStream();
	    FileOutputStream writer = new FileOutputStream(local_file_path);
	    byte[] buffer = new byte[153600];
	    int totalBytesRead = 0;
	    int bytesRead;
	    int size = urlc.getContentLength();
	    if(size >= 0)
	    	block = size / 20;
	    System.out.println("0%       50%      100%");
	    while ((bytesRead = reader.read(buffer)) > 0){ 
		writer.write(buffer, 0, bytesRead);
		buffer = new byte[153600];
		totalBytesRead += bytesRead;
		if (totalBytesRead > threshold) {
		    System.out.print("=");
		    threshold += block; 
		}
	    }
	    System.out.println();
	    System.out.println("[INFO] Done. " + (new Integer(totalBytesRead).toString())+"("+size + ") bytes read.");
	    writer.close();
	    reader.close();
	} catch (MalformedURLException e){
	    String err = String.format("Could not interpret url: \"%s\"\n%s",
				     urlstring,e.toString());
	    throw new KGParseException(err);
	}
	catch (IOException e){
	    String err = String.format("IO Exception reading from URL: \"%s\"\n%s",
				      urlstring,e.toString());
	    throw new KGParseException(err);
	}
	return true;
    }


}