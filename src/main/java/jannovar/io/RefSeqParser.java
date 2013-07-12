package jannovar.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException; 
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import jannovar.common.Constants;
import jannovar.exception.JannovarException;
import jannovar.exception.KGParseException;
import jannovar.reference.TranscriptModel;


/**
 * This class coordinates the downloading and parsing of the RefSeq
 * transcript definition files from the UCSC database (hg19). 
 * It is possible to parse directly from the gzip file without decompressing them, or the start from the
 * decompressed files. The class checks of the files exist and if they have the suffix "gz".
 * @see <a href="http://hgdownload.cse.ucsc.edu/goldenPath/hg19/database/">UCSC hg19 database downloads</a>
 * @author Peter N Robinson
 * @version 0.02 (10 July, 2013)
 */
public class RefSeqParser extends TranscriptDataParser implements Constants  {
     /** Number of tab-separated fields in then UCSC refFlat.txt file (build hg19). */
    public static final int NFIELDS=11;
    /** Base URI for UCSC hg19 build annotation files */
    private static final String hg19base = "http://hgdownload.soe.ucsc.edu/goldenPath/hg19/database/";
    /** Name of refFlat.txt file */
    private static final String refFlat = "refFlat.txt";


    public RefSeqParser(String path) {
	super(path);
    }




    /**
     * The refflat file has the following fields.
     * <ol>
     * <li>geneName: e.g., ANAPC13, Name of gene as it appears in genome browser.
     * <li>name: e.g., NM_001242374, Name of gene (refseq accession number)
     * <li>chrom: e.g., chr3, Reference sequence chromosome or scaffold
     * <li>strand: e.g., -,+ or - for strand
     * <li>txStart: e.g., 134196545, Transcription start position
     * <li>txEnd: e.g.,	134204865, Transcription end position
     * <li>cdsStart: e.g., 134197431, Coding region start
     * <li>cdsEnd: e.g., 134201746, Coding region end
     * <li>exonCount: e.g.,3, Number of exons
     * <li>exonStarts: e.g., 134196545,134201647,134204161, Exon start positions
     * <li>exonEnds: e.g., 134197557,134201773,13420486
     * </ol>
     * This is the basis for the TranscriptModel
    */
    public void parseRefFlatFile() {


    }


    public void downloadFiles() throws JannovarException {
	makeDirectoryIfNotExist();
	String refFlatCompressed = String.format("%s.gz",this.refFlat);
	//String knownGeneMrna = String.format("%s.gz",Constants.knownGeneMrna);
	//String kgXref = String.format("%s.gz",Constants.kgXref);
	//String known2locus = String.format("%s.gz",Constants.known2locus);
	download_file(this.hg19base, refFlatCompressed);
	


    }




     /**
     * The constructor parses a single line of the refseqFlat.txt file. 
   
    * <ol>
     * <li>geneName: e.g., ANAPC13, Name of gene as it appears in genome browser.
     * <li>name: e.g., NM_001242374, Name of gene (refseq accession number)
     * <li>chrom: e.g., chr3, Reference sequence chromosome or scaffold
     * <li>strand: e.g., -,+ or - for strand
     * <li>txStart: e.g., 134196545, Transcription start position
     * <li>txEnd: e.g.,	134204865, Transcription end position
     * <li>cdsStart: e.g., 134197431, Coding region start
     * <li>cdsEnd: e.g., 134201746, Coding region end
     * <li>exonCount: e.g.,3, Number of exons
     * <li>exonStarts: e.g., 134196545,134201647,134204161, Exon start positions
     * <li>exonEnds: e.g., 134197557,134201773,13420486
     * </ol>
     * The function additionalls parses the start and end of the exons. 
     * Note that in the UCSC database, positions are represented using
     * half-open, zero-based coordinates. That is, if start is 2 and end is 7, then the first nucleotide is at
     * position 3 (one-based) and the last nucleotide is at positon 7 (one-based). For now, we are switching
     * the coordinates to fully-closed one based by incrementing all start positions by one. This is the way
     * coordinates are typically represented in VCF files and is the way coordinate calculations are done
     * in annovar. At a later date, it may be worthwhile to switch to the UCSC-way of half-open zero based coordinates.
     * @param line A single line of the UCSC refFlat.txt file
     */
    public TranscriptModel parseTranscriptModelFromLine(String line) throws KGParseException  {
	TranscriptModel model = TranscriptModel.createTranscriptModel();
	String A[] = line.split("\t");
	if (A.length != NFIELDS) {
	    String error = String.format("Malformed line in UCSC knownGene.txt file:\n%s\nExpected %d fields but there were %d",
					 line,NFIELDS,A.length);
	    throw new KGParseException(error);
	}
	/* Field 0 has the gene symbol, e.g., ANAPC13. */
	model.setGeneSymbol(A[0]);
	/* Field 1 has the accession number, e.g., NM_001242374 */
	model.setAccessionNumber(A[1]);
	byte chromosome;
	try {
	    if (A[2].equals("chrX"))  chromosome = X_CHROMOSOME;     // 23
	    else if (A[2].equals("chrY")) chromosome = Y_CHROMOSOME; // 24
	    else if (A[2].equals("chrM")) chromosome = M_CHROMOSOME;  // 25
	    else {
		String tmp = A[2].substring(3); // remove leading "chr"
		chromosome = Byte.parseByte(tmp);
	    }
	} catch (NumberFormatException e) {  // scaffolds such as chrUn_gl000243 cause Exception to be thrown.
	    throw new KGParseException("Could not parse chromosome field: " + A[1]);
	}
	model.setChromosome(chromosome);
	char strand = A[3].charAt(0);
	if (strand != '+' && strand != '-') {
	    throw new KGParseException("Malformed strand: " + A[2]);
	}
	model.setStrand(strand);
	int txStart,txEnd,cdsStart,cdsEnd;
	try {
	    txStart = Integer.parseInt(A[4]) + 1; // +1 to convert to one-based fully closed numbering
	} catch (NumberFormatException e) {
	    throw new KGParseException("Could not parse txStart:" + A[4]);
	}
	model.setTranscriptionStart(txStart);
	try {
	    txEnd = Integer.parseInt(A[5]);
	} catch (NumberFormatException e) {
	    throw new KGParseException("Could not parse txEnd:" + A[5]);
	}
	model.setTranscriptionEnd(txEnd);
	try {
	    cdsStart = Integer.parseInt(A[6]) + 1;// +1 to convert to one-based fully closed numbering
	} catch (NumberFormatException e) {
	    throw new KGParseException("Could not parse cdsStart:" + A[6]);
	}
	model.setCdsStart(cdsStart);
	try {
	    cdsEnd = Integer.parseInt(A[7]);
	} catch (NumberFormatException e) {
	    throw new KGParseException("Could not parse cdsEnd:" + A[7]);
	}
	model.setCdsEnd(cdsEnd);
	byte exonCount;
	try {
	    exonCount = Byte.parseByte(A[8]);
	}catch (NumberFormatException e) {
	    throw new KGParseException("Could not parse exonCount:" + A[8]);
	}
	model.setExonCount(exonCount);
	/* Now parse the exon ends and starts */
	int[] exonStarts= new int[exonCount] ;
	/** End positions of each of the exons of this transcript */
	int[] exonEnds= new int[exonCount];
	String starts = A[9];
	String ends   = A[10];
	String B[] = starts.split(",");
	if (B.length != exonCount) {
	    String error = String.format("[RefSeqParser] Malformed exonStarts list: found %d but I expected %d exons",
					 B.length,exonCount);
	    error = String.format("%s. This should never happen, the refFlat.txt file may be corrupted", error);
	    throw new KGParseException(error); 
	}
	for (int i=0;i<exonCount;++i) {
	    try {
		exonStarts[i] = Integer.parseInt(B[i]) + 1; // Change 0-based to 1-based numbering
	    } catch (NumberFormatException e) {
		String error = String.format("[UCSCKGParser] Malformed exon start at position %d of line %s", i, starts);
		error = String.format("%s. This should never happen, the knownGene.txt file may be corrupted", error);
		throw new KGParseException(error);
	    }
	}
	// Now do the ends.
	B = ends.split(",");
	for (int i=0;i<exonCount;++i) {
	    try {
		exonEnds[i] = Integer.parseInt(B[i]);
	    } catch (NumberFormatException e) {
		String error = String.format("[UCSCKGParser] Malformed exon end at position %d of line %s", i, ends);
		error = String.format("%s. This should never happen, the knownGene.txt file may be corrupted", error);
		throw new KGParseException(error);
	    }
	}
	model.setExonStartsAndEnds(exonStarts, exonEnds);
	model.initialize();

	return model;
    }
   


}