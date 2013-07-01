package jannovar.io;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * This class downloads the four files we need to
 * generate TranscriptModel obejcts from the UCSC server.
 * @version 0.01 (1 July, 2013)
 * @author Peter Robinsin
 */
public class UCSCDownloader {
	
    /** Path of direrctory to which the files will be downloaded. */
    private String directory_path;
    /** Base URI for UCSC hg19 build annotation files */
    private String hg19base = "http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/";

    /** Name of the knownGenes file. */
    private String knownGene = "knownGene.txt.gz";
    /** Name of the UCSC knownGenes mRNA file. */
    private String knownGeneMrna = "knownGeneMrna.txt.gz";
    /** Name of the UCSC knownGenes Xref file. */
    private String kgXref = "kgXref.txt.gz";
    /** Name of the UCSC knownGenes Xref file. */
    private String known2locus = "knownToLocusLink.txt.gz";
    
    public UCSCDownloader(String dirpath) {
	if (! dirpath.endsWith("/")) {
	    dirpath = dirpath + "/"; // add trailing slash.
	}
	this.directory_path = dirpath;	
    }

    
    
    /**
     * Construct the object and also set proxy properties for http connection.
     */
    public UCSCDownloader(String dirpath, String proxyHost, String port) {
	UCSCDownloader(dirpath);
	System.setProperty("http.proxyHost", proxyHost);
	System.setPropery("http.proxyPort", port);
    }


    /**
     * This method downloads a file to the specified local file path
     */
    public boolean download_file(String urlstring, String local_file_path) {
	try{
	    URL url = new URL(urlstring);
	    url.openConnection();
	    InputStream reader = url.openStream();
	    FileOutputStream writer = new FileOutputStream(local_file_path);
	    byte[] buffer = new byte[153600];
	    int totalBytesRead = 0;
	    int bytesRead = 0;
	    while ((bytesRead = reader.read(buffer)) > 0){ 
		writer.write(buffer, 0, bytesRead);
		buffer = new byte[153600];
		totalBytesRead += bytesRead;
	    }
	    System.out.println("Done. " + (new Integer(totalBytesRead).toString()) + " bytes read.");
	    writer.close();
	    reader.close();
	} catch (MalformedURLException e){
	    System.out.println("Could not interpret url: " + urlstring);
	    e.printStackTrace();
	    System.exit(1);
	}
	catch (IOException e){
	    System.out.println("IO Exception reading from URL: " + urlstring);
	    e.printStackTrace();
	    System.exit(1);
	}
	
	return true;
    }