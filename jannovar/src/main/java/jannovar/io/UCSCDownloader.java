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
    private String hg19base = "ftp://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/";

    /** Name of the knownGenes file. */
    private String knownGene = "knownGene.txt.gz";
    /** Name of the UCSC knownGenes mRNA file. */
    private String knownGeneMrna = "knownGeneMrna.txt.gz";
    /** Name of the UCSC knownGenes Xref file. */
    private String kgXref = "kgXref.txt.gz";
    /** Name of the UCSC knownGenes Xref file. */
    private String known2locus = "knownToLocusLink.txt.gz";
    
    /**
     * This constructor sets the locationof the directory into 
     * which the UCSC data will be downloaded.
     * @param dirpath Location of download directory.
     */
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
	this(dirpath);
	if (proxyHost == null)
	    return; /* Do not set proxy if proxyHost is null. */
	System.setProperty("proxySet","true");
	System.setProperty("http.proxyHost", proxyHost);
	System.setProperty("http.proxyPort", port);
    }


     /**
     * This function first checks if the download directory already exists. If not,
     * it tries to create the directory. It then tries to download the four required
     * files from the UCSC genome browser (if a file already exists, it emits a
     * warning message and skips it).
     */
    public void downloadUCSCfiles() {
	makeDirectoryIfNotExist();
	download_file(this.hg19base, this.knownGene);
	download_file(this.hg19base, this.knownGeneMrna);
	download_file(this.hg19base, this.kgXref);
	download_file(this.hg19base, this.known2locus);
	

    }


    /**
     * This function creates a new directory to store the downloaded
     * UCSC files. If the directory already exists, it just emits a
     * warning and does nothing.
     */
    private void makeDirectoryIfNotExist() {
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
     */
    public boolean download_file(String baseURL, String fname ) {

	String urlstring = baseURL + fname;
	String local_file_path = this.directory_path + fname;
	File f = new File(local_file_path);
	if (f.exists()) {
	    System.err.println(String.format("Timorously refusing to download "+
					     "file \"%s\" since it already exists",
					     local_file_path));
	    return false;

	}
	System.err.println("Downloading: \"" + urlstring + "\"");
	//System.out.println("File " + local_file_path);
	//System.out.println("proxy: " +  System.getProperty("http.proxyHost"));
	//System.out.println("port: " +  System.getProperty("http.proxyPort"));
	int threshold = 0;
	int block = 250000;
	try{
	    URL url = new URL(urlstring);
	    
	    InputStream reader = url.openConnection().getInputStream();
	    FileOutputStream writer = new FileOutputStream(local_file_path);
	    byte[] buffer = new byte[153600];
	    int totalBytesRead = 0;
	    int bytesRead = 0;
	    while ((bytesRead = reader.read(buffer)) > 0){ 
		writer.write(buffer, 0, bytesRead);
		buffer = new byte[153600];
		totalBytesRead += bytesRead;
		if (totalBytesRead > threshold) {
		    System.err.print("=");
		    threshold += block;
		}
	    }
	    System.err.println();
	    System.err.println("Done. " + (new Integer(totalBytesRead).toString()) + " bytes read.");
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
}
/* end of file */