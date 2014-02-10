/**
 * 
 */
package jannovar.io;

import jannovar.common.Constants;
import jannovar.exception.FileDownloadException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * This class is responsible for downloading the data files from UCSC, Ensembl, RefSeq.
 * @version 0.2 (2014-01-02)
 * @author mjaeger
 * 
 */
public class TranscriptDataDownloader implements Constants {
	/** Path of directory to which the files will be downloaded. */
	private String directory_path;

	/**
	 * This constructor sets the location of the directory into which the transcript 
	 * annotation data will be downloaded.<br>
	 * Creates the folder if it still not exists.
	 * @param dirPath
	 *            Location of download directory.
	 */
	public TranscriptDataDownloader(String dirPath) {
		// add trailing slash.
		if(!dirPath.endsWith(System.getProperty("file.separator")))
			dirPath += System.getProperty("file.separator");
		this.directory_path = dirPath;
	}

	/**
	 * Returns the path to the download directory were the transcript annotation files are stored.
	 * @return The path to the download directory.
	 */
	public String getDownloadDirectory() {
		return this.directory_path;
	}

	/**
	 * Construct the object and also set proxy properties for http connection.
     * @param dirpath directory path
     * @param proxyHost proxy host (e.g. http://proxy.company.com)
     * @param port proxy port (e.g. 8080)
	 */
	public TranscriptDataDownloader(String dirpath, String proxyHost, String port) {
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
	 * This function first checks if the download directory already exists. If
	 * not, it tries to create the directory. Then the
	 * download method is called.
	 * @param source an integer constant (see {@link jannovar.common.Constants Constants})
	 * that indicates whether to download UCSC, Ensembl, or RefSeq.
     * @param r use {@link jannovar.common.Constants Release}
	 * @throws FileDownloadException
	 */
	public void downloadTranscriptFiles(int source, Release r) throws FileDownloadException {
		makeDirectoryIfNotExist();
		switch (source) {
		case UCSC:
		    downloadUCSCfiles(r);
		    break;
		case ENSEMBL:
		    downloadEnsemblFiles(r);
		    break;
		case REFSEQ:
		    downloadRefseqFiles(r);
		    break;
		default:
		    throw new FileDownloadException("Unknown source of transcript annotation.");
		}
	}

	/**
	 * Downloads the ensembl transcript data files (if a file already exists, 
	 * it emits a warning message and skips it).
     * @param r {@link Release} version
     * @throws jannovar.exception.FileDownloadException
	 */
	public void downloadRefseqFiles(Release r) throws FileDownloadException{
		
		String gff_base;
		String rna_base;
		String gff_name;
		String rna_name = refseq_rna;
		switch(r){
		case MM9:
			gff_base	= String.format("%s%s%s", REFSEQ_FTP_BASE_MOUSE,REFSEQ_MM9,REFSEQ_GFF_BASE);
			rna_base	= String.format("%s%s%s", REFSEQ_FTP_BASE_MOUSE,REFSEQ_MM9,REFSEQ_FASTA_BASE);
			gff_name	= refseq_gff_mm9;
			break;
		case MM10:
			gff_base	= String.format("%s%s%s", REFSEQ_FTP_BASE_MOUSE,REFSEQ_MM10,REFSEQ_GFF_BASE);
			rna_base	= String.format("%s%s%s", REFSEQ_FTP_BASE_MOUSE,REFSEQ_MM10,REFSEQ_FASTA_BASE);
			gff_name	= refseq_gff_mm10;
			break;
		case HG18:
			gff_base	= String.format("%s%s%s", REFSEQ_FTP_BASE,REFSEQ_HG18,REFSEQ_GFF_BASE);
			rna_base	= String.format("%s%s%s", REFSEQ_FTP_BASE,REFSEQ_HG18,REFSEQ_FASTA_BASE);
			gff_name	= refseq_gff_hg18;
			break;
		case HG38:
			gff_base	= String.format("%s%s%s", REFSEQ_FTP_BASE,REFSEQ_HG38,REFSEQ_GFF_BASE);
			rna_base	= String.format("%s%s%s", REFSEQ_FTP_BASE,REFSEQ_HG38,REFSEQ_FASTA_BASE);
			gff_name	= refseq_gff_hg38;
			break;
		case HG19:			
		default:
			gff_base	= String.format("%s%s%s", REFSEQ_FTP_BASE,REFSEQ_HG19,REFSEQ_GFF_BASE);
			rna_base	= String.format("%s%s%s", REFSEQ_FTP_BASE,REFSEQ_HG19,REFSEQ_FASTA_BASE);
			gff_name	= refseq_gff_hg19;
		}
		download_file(gff_base, gff_name);
		download_file(rna_base, rna_name);
	}

	/**
	 * Downloads the ensembl transcript data files (if a file already exists, 
	 * it emits a warning message and skips it).
     * @param r {@link Release} version
     * @throws jannovar.exception.FileDownloadException
	 */
	public void downloadEnsemblFiles(Release r) throws FileDownloadException{
		String gtf_base;
		String cdna_base;
		String ncrna_base;
		String gtf_name;
		String cdna_name;
		String ncrna_name;
		switch (r) {
		case MM9:
			gtf_base	= String.format("%s%s%s", ENSEMBL_FTP_BASE_MM9,ENSEMBL_GTF_BASE,ENSEMBL_MOUSE_BASE);
			cdna_base	= String.format("%s%s%s%s", ENSEMBL_FTP_BASE_MM9,ENSEMBL_FASTA_BASE,ENSEMBL_MOUSE_BASE,"cdna/");
			ncrna_base	= String.format("%s%s%s%s", ENSEMBL_FTP_BASE_MM9,ENSEMBL_FASTA_BASE,ENSEMBL_MOUSE_BASE,"ncrna/");
			gtf_name	= String.format("%s%s", ensembl_mm9, ensembl_gtf);
			cdna_name	= String.format("%s%s", ensembl_mm9, ensembl_cdna);
			ncrna_name	= String.format("%s%s", ensembl_mm9, ensembl_ncrna);
			break;
		case MM10:
			gtf_base	= String.format("%s%s%s", ENSEMBL_FTP_BASE_MM10,ENSEMBL_GTF_BASE,ENSEMBL_MOUSE_BASE);
			cdna_base	= String.format("%s%s%s%s", ENSEMBL_FTP_BASE_MM10,ENSEMBL_FASTA_BASE,ENSEMBL_MOUSE_BASE,"cdna/");
			ncrna_base	= String.format("%s%s%s%s", ENSEMBL_FTP_BASE_MM10,ENSEMBL_FASTA_BASE,ENSEMBL_MOUSE_BASE,"ncrna/");
			gtf_name	= String.format("%s%s", ensembl_mm10, ensembl_gtf);
			cdna_name	= String.format("%s%s", ensembl_mm10, ensembl_cdna);
			ncrna_name	= String.format("%s%s", ensembl_mm10, ensembl_ncrna);
			break;		
		case HG18:
			gtf_base	= String.format("%s%s%s", ENSEMBL_FTP_BASE_HG18,ENSEMBL_GTF_BASE,ENSEMBL_HUMAN_BASE);
			cdna_base	= String.format("%s%s%s%s", ENSEMBL_FTP_BASE_HG18,ENSEMBL_FASTA_BASE,ENSEMBL_HUMAN_BASE,"cdna/");
			ncrna_base	= String.format("%s%s%s%s", ENSEMBL_FTP_BASE_HG18,ENSEMBL_FASTA_BASE,ENSEMBL_HUMAN_BASE,"ncrna/");
			gtf_name	= String.format("%s%s", ensembl_hg18, ensembl_gtf);
			cdna_name	= String.format("%s%s", ensembl_hg18, ensembl_cdna);
			ncrna_name	= String.format("%s%s", ensembl_hg18, ensembl_ncrna);
			break;
		case HG19:
		default:
			gtf_base	= String.format("%s%s%s", ENSEMBL_FTP_BASE_HG19,ENSEMBL_GTF_BASE,ENSEMBL_HUMAN_BASE);
			cdna_base	= String.format("%s%s%s%s", ENSEMBL_FTP_BASE_HG19,ENSEMBL_FASTA_BASE,ENSEMBL_HUMAN_BASE,"cdna/");
			ncrna_base	= String.format("%s%s%s%s", ENSEMBL_FTP_BASE_HG19,ENSEMBL_FASTA_BASE,ENSEMBL_HUMAN_BASE,"ncrna/");
			gtf_name	= String.format("%s%s", ensembl_hg19, ensembl_gtf);
			cdna_name	= String.format("%s%s", ensembl_hg19, ensembl_cdna);
			ncrna_name	= String.format("%s%s", ensembl_hg19, ensembl_ncrna);
			break;
		}
		download_file(ncrna_base, ncrna_name);
		download_file(cdna_base, cdna_name);
		download_file(gtf_base, gtf_name);
	}

	/**
	 * Downloads the four
	 * required files from the UCSC genome browser (if a file already exists, it
	 * emits a warning message and skips it).
     * @param r {@link Release} version
     * @throws jannovar.exception.FileDownloadException
	 */
	public void downloadUCSCfiles(Release r) throws FileDownloadException {
		String knownGene = String.format("%s.gz", Constants.knownGene);
		String knownGeneMrna = String.format("%s.gz", Constants.knownGeneMrna);
		String kgXref = String.format("%s.gz", Constants.kgXref);
		String known2locus = String.format("%s.gz", Constants.known2locus);
		
		String ucsc_ftp_base;
		switch (r) {
		case MM9:
			ucsc_ftp_base	= UCSC_FTP_BASE_MM9;
			break;
		case MM10:
			ucsc_ftp_base	= UCSC_FTP_BASE_MM10;
			break;
		case HG18:
			ucsc_ftp_base	= UCSC_FTP_BASE_HG18;
			break;
		case HG19:
		default:
			ucsc_ftp_base	= UCSC_FTP_BASE_HG19;
			break;
		}
		download_file(ucsc_ftp_base, knownGene);
		download_file(ucsc_ftp_base, knownGeneMrna);
		download_file(ucsc_ftp_base, kgXref);
		download_file(ucsc_ftp_base, known2locus);
	}

    /**
     * This function creates a new directory to store the downloaded UCSC files.
     * If the directory already exists, it just emits a warning and does
     * nothing.
     */
    private void makeDirectoryIfNotExist() {
	File directory = new File(this.directory_path);
	/* first make data directory. This is the top directory that the subdirectories
	   hg18, hg19, mm9, mm10 etc go into.*/
//	File data = new File("data");
//	if (! data.exists())
//	    data.mkdirs();
	if (directory.exists()) {
	    System.out.println(String.format("[INFO] Cowardly refusing to create "
					     + "directory \"%s\" since it already exists", this.directory_path));
	} else {
	    directory.mkdirs();
	}
    }

	/**
	 * This method downloads a file to the specified local file path. If the
	 * file already exists, it emits a warning message and does nothing.
     * @param baseURL URL to remote directory
     * @param fname remote file name
     * @return <code>true</code> if the file was downloaded successfully 
     * @throws jannovar.exception.FileDownloadException
	 */
	public boolean download_file(String baseURL, String fname) throws FileDownloadException {
	    
	    String urlstring = baseURL + fname;
	    String local_file_path = this.directory_path + fname;
	    File f = new File(local_file_path);
	    if (f.exists()) {
		System.out.println(String.format("[INFO] Timorously refusing to download " 
						 + "file \"%s\" since it already exists locally", fname));
		return false;
		
	    }
	    System.out.println("[INFO] Downloading: \"" + fname + "\"");
	    //System.out.println("File " + local_file_path);
	    //System.out.println("proxy: " + System.getProperty("http.proxyHost"));
	    //System.out.println("port: " + System.getProperty("http.proxyPort"));
	    int threshold = 0;
	    int block = 250000;
	    try {
		URL url = new URL(urlstring);
		URLConnection urlc = url.openConnection();
		InputStream reader = urlc.getInputStream();
		FileOutputStream writer = new FileOutputStream(local_file_path);
		byte[] buffer = new byte[153600];
		int totalBytesRead = 0;
		int bytesRead = 0;
		int size = urlc.getContentLength();
		if (size >= 0)
		    block = size / 20;
		System.out.println("0%       50%      100%");
		while ((bytesRead = reader.read(buffer)) > 0) {
		    writer.write(buffer, 0, bytesRead);
		    buffer = new byte[153600];
		    totalBytesRead += bytesRead;
		    if (totalBytesRead > threshold) {
			System.err.print("=");
			threshold += block;
			// block += 250000; /* reduce number of progress bars for
			// big files. */
		    }
		}
		System.out.println();
		System.out.println("[INFO] Done. " + (new Integer(totalBytesRead).toString()) + "(" + size + ") bytes read.");
		writer.close();
		reader.close();
	    } catch (MalformedURLException e) {
		String err = String.format("Could not interpret url: \"%s\"\n%s", urlstring, e.toString());
		throw new FileDownloadException(err);
	    } catch (IOException e) {
		String err = String.format("IO Exception reading from URL: \"%s\"\n%s", urlstring, e.toString());
		throw new FileDownloadException(err);
	    }
	    
	    return true;
	}
}
/* eof */
