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

import jannovar.common.Constants;
import jannovar.exception.KGParseException;
import jannovar.reference.TranscriptModel;

/**
 * Parses the knownGene.txt file from the UCSC database. 
 * This file is tab-separated and has the following fields:
 * <OL>
 * <LI>  `name`   e.g., uc001irt.4. This is a UCSC knownGene identifier. We will use the kgXref table to convert to gene symbol etc.
 * <LI>  `chrom`  e.g., chr10
 * <LI>  `strand` e.g., +
 * <LI>  `txStart` e.g., 24497719
 * <LI>  `txEnd` e.g.,  24836772
 * <LI>  `cdsStart` e.g., 24498122
 * <LI>  `cdsEnd`  e.g., 24835253
 * <LI>  `exonCount` e.g., 17
*  <LI>   `exonStarts` e.g., 24497719,24508554,24669797,.... (total of 17 ints for this example)
 * <LI>  `exonEnds` e.g., 	24498192,24508838,24669996, .... (total of 17 ints for this example)
 * <LI>  `proteinID` e.g., NP_001091971
 * <LI>  `alignID` e.g.,  uc001irt.4 (Note: We do not need this field for our app).
 * </OL>
 * <P>
 * Note that this file is a MySQL dump file used at the UCSC database. We will use this program to create a 
 * serialized java object that can quickly be input to the Jannovar program. This is probably more efficient
 * than storing everything in the postgreSQL database because we will almost always need to get information
 * for half or more of the known genes, and thus it is quicker to initialize the object from a serialization.
  * <P>
 * This class additionally parses the ucsc {@code KnownToLocusLink.txt} file, which contains cross
 * references from the ucsc IDs to the corresponding Entrez Gene ids (earlier known as Locus Link):
 * <PRE>
 * uc010eve.3      3805
 * uc002qug.4      3805
 * uc010evf.3      3805
 * ...
 * </PRE>
 * This class parses the UCSC knownGenes files to create a list of 
 * {@link jannovar.reference.TranscriptModel TranscriptModel} objects.
 * @see <a href="http://hgdownload.cse.ucsc.edu/goldenPath/hg19/database/">UCSC hg19 database downloads</a>
 * @author Peter N Robinson
 * @version 0.12 (4 June, 2013)
 */
public class UCSCKGParser implements  Constants{
    /** Number of tab-separated fields in then UCSC knownGene.txt file (build hg19). */
    public static final int NFIELDS=12;
    /** Path to the knownGene.txt file from UCSC */
    private String kgPath=null;
    /** Path to the UCSC file kgXref.txt */
    private String ucscXrefPath=null;
    /** Path to the UCSC file knownGeneMrna.txt */
    private String ucscKGMrnaPath=null;
    /** Path to UCSC file knownToLocusLink.txt file. This file has cross refs between the 
	ucsc knownGene ids and Entrez gene ids (the previous name of Entrez gene was 
	locus link). */
    private String ucscKnown2LocusPath=null;
    
    /** Map of all known genes. Note that the key is the UCSC id, e.g., uc0001234.3, and the
     * value is the corresponding TranscriptModel object
     * @see jannovar.reference.TranscriptModel
    */
    private HashMap<String,TranscriptModel> knownGeneMap=null;


    /**
     * Set up and check the existence of the file knownGene.txt
     * @param ucscPath path to the UCSC file knownGene.txt.
     * @param XrefPath path to the UCSC file kgXref.txt
     * @param mRNApath path to the UCSC file knownGeneMrna.txt
     * @param locusPath path to the UCSC file knownToLocusLink.txt
     */
    public UCSCKGParser(String ucscPath, String XrefPath,String mRNApath,String locusPath) {
	this.kgPath = ucscPath;
	this.ucscXrefPath = XrefPath;
	this.ucscKGMrnaPath = mRNApath;
	this.ucscKnown2LocusPath = locusPath;
	this.knownGeneMap = new HashMap<String,TranscriptModel>();
    }

    /**
     * This function causes all four UCSC files to be parsed. This results in the 
     * construction of {@link #knownGeneMap}.
     */
    public void parseUCSCFiles() {
	try {
	    parseKnownGeneFile();
	    readFASTAsequences();
	    readKGxRefFile();
	    readKnown2Locus();
	} catch (KGParseException kge) {
	    System.out.println("UCSCKGParser.java: Error with file input");
	    System.out.println(kge.toString());
	    System.exit(1);
	}
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
     * The constructor parses a single line of the knownGene.txt file. 
     * <P>
     * The fields of the file are tab separated and have the following structure:
     * <UL>
     * <LI> 0: name (UCSC known gene id, e.g., "uc021olp.1"	
     * <LI> 1: chromosome, e.g., "chr1"
     * <LI> 2: strand, e.g., "-"
     * <LI> 3: transcription start, e.g., "38674705"
     * <LI> 4: transcription end, e.g., "38680439"
     * <LI> 5: CDS start, e.g., "38677458"
     * <LI> 6: CDS end, e.g., "38678111"
     * <LI> 7: exon count, e.g., "4"
     * <LI> 8: exonstarts, e.g., "38674705,38677405,38677769,38680388,"
     * <LI> 9: exonends, e.g., "38676494,38677494,38678123,38680439,"
     * <LI> 10: name, again (?), e.g., "uc021olp.1"
     * </UL>
     * The function additionalls parses the start and end of the exons. 
     * Note that in the UCSC database, positions are represented using
     * half-open, zero-based coordinates. That is, if start is 2 and end is 7, then the first nucleotide is at
     * position 3 (one-based) and the last nucleotide is at positon 7 (one-based). For now, we are switching
     * the coordinates to fully-closed one based by incrementing all start positions by one. This is the way
     * coordinates are typically represented in VCF files and is the way coordinate calculations are done
     * in annovar. At a later date, it may be worthwhile to switch to the UCSC-way of half-open zero based coordinates.
     * @param line A single line of the UCSC knownGene.txt file
     */
    public TranscriptModel parseTranscriptModelFromLine(String line) throws KGParseException  {
	TranscriptModel model = TranscriptModel.createTranscriptModel();
	String A[] = line.split("\t");
	if (A.length != NFIELDS) {
	    String error = String.format("Malformed line in UCSC knownGene.txt file:\n%s\nExpected %d fields but there were %d",
					 line,NFIELDS,A.length);
	    throw new KGParseException(error);
	}
	/* Field 0 has the accession number, e.g., uc010nxr.1. */
	model.setAccessionNumber(A[0]);
	byte chromosome;
	try {
	    if (A[1].equals("chrX"))  chromosome = X_CHROMOSOME;     // 23
	    else if (A[1].equals("chrY")) chromosome = Y_CHROMOSOME; // 24
	    else if (A[1].equals("chrM")) chromosome = M_CHROMOSOME;  // 25
	    else {
		String tmp = A[1].substring(3); // remove leading "chr"
		chromosome = Byte.parseByte(tmp);
	    }
	} catch (NumberFormatException e) {  // scaffolds such as chrUn_gl000243 cause Exception to be thrown.
	    throw new KGParseException("Could not parse chromosome field: " + A[1]);
	}
	model.setChromosome(chromosome);
	char strand = A[2].charAt(0);
	if (strand != '+' && strand != '-') {
	    throw new KGParseException("Malformed strand: " + A[2]);
	}
	model.setStrand(strand);
	int txStart,txEnd,cdsStart,cdsEnd;
	try {
	    txStart = Integer.parseInt(A[3]) + 1; // +1 to convert to one-based fully closed numbering
	} catch (NumberFormatException e) {
	    throw new KGParseException("Could not parse txStart:" + A[3]);
	}
	model.setTranscriptionStart(txStart);
	try {
	    txEnd = Integer.parseInt(A[4]);
	} catch (NumberFormatException e) {
	    throw new KGParseException("Could not parse txEnd:" + A[4]);
	}
	model.setTranscriptionEnd(txEnd);
	try {
	    cdsStart = Integer.parseInt(A[5]) + 1;// +1 to convert to one-based fully closed numbering
	} catch (NumberFormatException e) {
	    throw new KGParseException("Could not parse cdsStart:" + A[5]);
	}
	model.setCdsStart(cdsStart);
	try {
	    cdsEnd = Integer.parseInt(A[6]);
	} catch (NumberFormatException e) {
	    throw new KGParseException("Could not parse cdsEnd:" + A[6]);
	}
	model.setCdsEnd(cdsEnd);
	byte exonCount;
	try {
	    exonCount = Byte.parseByte(A[7]);
	}catch (NumberFormatException e) {
	    throw new KGParseException("Could not parse exonCount:" + A[7]);
	}
	model.setExonCount(exonCount);
	/* Now parse the exon ends and starts */
	int[] exonStarts= new int[exonCount] ;
	/** End positions of each of the exons of this transcript */
	int[] exonEnds= new int[exonCount];
	String starts = A[8];
	String ends   = A[9];
	String B[] = starts.split(",");
	if (B.length != exonCount) {
	    String error = String.format("[UCSCKGParser] Malformed exonStarts list: found %d but I expected %d exons",
					 B.length,exonCount);
	    error = String.format("%s. This should never happen, the knownGene.txt file may be corrupted", error);
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
   


    /** 
     * Parses the UCSC knownGene.txt file.
     */
    public void parseKnownGeneFile() throws KGParseException {
	int linecount=0;
	int exceptionCount=0;
	try{
	    FileInputStream fstream = new FileInputStream(this.kgPath);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String line;
	   
	    while ((line = br.readLine()) != null)   {
		linecount++;
		//System.out.println(line);
		try {
		    TranscriptModel kg = parseTranscriptModelFromLine(line);
		    String id = kg.getKnownGeneID();
		    this.knownGeneMap.put(id,kg);	   
		} catch (KGParseException e) {
		    //System.out.println("Exception parsing KnownGene.txt: " + e.toString());
		    exceptionCount++;
		}
	    }
	    System.out.println(String.format("lines: %d, exceptions: %d",linecount,exceptionCount));
	    System.out.println("Size of knownGeneMap: " + knownGeneMap.size());
	} catch (FileNotFoundException fnfe) {
	    String s = String.format("Could not find KnownGene.txt file: %s\n%s", 
				     this.kgPath, fnfe.toString());
	    throw new KGParseException(s);
	} catch (IOException e) {
	    String s = String.format("Exception while parsing UCSC KnownGene file at \"%s\"\n%s",
				     this.kgPath,e.toString());
	    throw new KGParseException(s);
	}
    }



    /**
     * Parses the ucsc KnownToLocusLink.txt file, which contains cross references from
     * ucsc KnownGene ids to Entrez Gene ids. The function than adds an Entrez gene
     * id to the corresponing {@link jannovar.reference.TranscriptModel TranscriptModel}
     * objects.
     */
    private void  readKnown2Locus() throws KGParseException {
	try{
	    FileInputStream fstream = new FileInputStream(this.ucscKnown2LocusPath);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String line;
	   
	    int foundID=0;
	    int notFoundID=0;
	   
	    while ((line = br.readLine()) != null)   {
		String A[] = line.split("\t");
		if (A.length != 2) {
		    System.err.println("Bad format for UCSC KnownToLocusLink.txt file:\n" + line);
		    System.err.println("Got " + A.length + " fields instead of the expected 2");
		    System.err.println("Fix problem in UCSC file before continuing");
		    System.exit(1);
		}
		String id = A[0];
		Integer entrez = Integer.parseInt(A[1]);
		TranscriptModel kg = this.knownGeneMap.get(id);
		if (kg == null) {
		    /** Note: many of these sequences seem to be for genes on scaffolds, e.g., chrUn_gl000243 */
		    //System.err.println("Error, could not find FASTA sequence for known gene \"" + id + "\"");
		    notFoundID++;
		    continue;
		    //System.exit(1);
		}
		foundID++;
		kg.setEntrezGeneID(entrez);
	    }
	    String msg = String.format("Done parsing knownToLocusLink. Got ids for %d knownGenes. Missed in %d",
				       foundID,notFoundID);
	    System.out.println(msg);
	} catch (FileNotFoundException fnfe) {
	    String s = String.format("Exception while parsing UCSC  knownToLocusLink file at \"%s\"\n%s",
				     this.ucscKnown2LocusPath,fnfe.toString());
	    throw new KGParseException(s);
	} catch (IOException e) {
	    String s = String.format("Exception while parsing UCSC KnownToLocusfile at \"%s\"\n%s",
				     this.ucscKnown2LocusPath,e.toString() );
	    throw new KGParseException(s);
	}
	   
    }



    /**
     * Input FASTA sequences from the UCSC hg19 file {@code knownGeneMrna.txt}
     * Note that the UCSC sequences are all in lower case, but we convert them
     * here to all upper case letters to simplify processing in other places of this program.
     * The sequences are then added to the corresponding {@link jannovar.reference.TranscriptModel TranscriptModel}
     * objects.
     */
    private void readFASTAsequences() throws KGParseException {
	
	try{
	    FileInputStream fstream = new FileInputStream(this.ucscKGMrnaPath);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String line;
	    int kgWithNoSequence=0;
	    int foundSequence=0;
	   
	    while ((line = br.readLine()) != null)   {
		String A[] = line.split("\t");
		if (A.length != 2) {
		    System.err.println("Bad format for UCSC KnownGeneMrna.txt file:\n" + line);
		    System.err.println("Got " + A.length + " fields instead of the expected 2");
		    System.err.println("Fix problem in UCSC file before continueing");
		    System.exit(1);
		}
		
		String id = A[0];
		String seq = A[1].toUpperCase();
		TranscriptModel kg = this.knownGeneMap.get(id);
		if (kg == null) {
		    /** Note: many of these sequences seem to be for genes on scaffolds, e.g., chrUn_gl000243 */
		    //System.err.println("Error, could not find FASTA sequence for known gene \"" + id + "\"");
		    kgWithNoSequence++;
		    continue;
		    //System.exit(1);
		}
		foundSequence++;
		kg.setSequence(seq);
	    }
	    in.close();
	    System.out.println(String.format("Found sequences for %d KGs, did not find sequence for %d",foundSequence,kgWithNoSequence));
	} catch (FileNotFoundException fnfe) {
	    String s = String.format("Could not find file: %s\n%s",this.ucscKGMrnaPath, fnfe.toString());
	    throw new KGParseException(s);
	} catch (IOException ioe) {
	    String s = String.format("Exception while parsing UCSC KnownGene FASTA file at \"%s\"\n%s",
				     this.ucscKGMrnaPath,ioe.toString());
	   throw new KGParseException(s);
	}
    }
    
    
     /**
      * Input xref information for the known genes. We are especially interested in information
      * corresponding to $name2 in Annovar (this is almost always a geneSymbol)
      * The sequences are then added to the corresponding {@link jannovar.reference.TranscriptModel TranscriptModel}
      * objects.
      * <P>
      * According to the Annovar documentation, some genes were given names that are prefixed with "Em:",
      * which should be removed due to the presence of ":" in exonic variant annotation. I do not find this
      * in the current version of kgXref.txt,
      * <P>
      * annovar parses the 5th field of this file (4th in zero-based numbering). For many of the entries, this
      * field contains the gene symbol, and this is used as $name2. 
      * <P>
      * Note that some of the fields are empty, which can cause a problem for Java's split function, which then
      * conflates neighboring fields. Therefore, we instead just count the number of tab signs to get to the 5th
      * field. Annovar does not use any of the other information in this file, we will do the same for now. 
      * <P>
      * uc001aca.2      NM_198317       Q6TDP4  KLH17_HUMAN     KLHL17  NM_198317       NP_938073       Homo sapiens kelch-like 17 (Drosophila) (KLHL17), mRNA. 
      * <P>
      * The structure of the file is 
      * <UL>
      * <LI> 0: UCSC knownGene id, e.g., "uc001aca.2" (this is the key used to match entries to the knownGene.txt file)
      * <LI> 1: Accession number (refseq if availabl), e.g., "NM_198317"
      * <LI> 2: Uniprot accession number, e.g.,  "Q6TDP4"
      * <LI> 3: UCSC stable id, e.g., "KLH17_HUMAN"
      * <LI> 4: Gene symbol, e.g., "KLH17"
      * <LI> 5: (?) Additional mRNA accession
      * <LI> 6: (?) Protein accession number
      * <LI> 7: Description
      * </UL>
      */
    public void readKGxRefFile() throws KGParseException {
	try{
	    FileInputStream fstream = new FileInputStream(this.ucscXrefPath);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String line;
	    int kgWithNoXref=0;
	    int kgWithXref=0;
	    
	    while ((line = br.readLine()) != null)   {
		if (line.startsWith("#"))
		    continue; /* Skip comment line */
		String A[] = line.split("\t");
		if (A.length < 8) {
		    String err = String.format("Error, malformed ucsc xref line: %s\nExpected 8 fields but got %d",
					       line, A.length);
		    throw new KGParseException(err);
		}
		String id = A[0];
		String geneSymbol = A[4];
		TranscriptModel kg = this.knownGeneMap.get(id);
		if (kg == null) {
		    /** Note: many of these sequences seem to be for genes on scaffolds, e.g., chrUn_gl000243 */
		    //System.err.println("Error, could not find xref sequence for known gene \"" + id + "\"");
		    kgWithNoXref++;
		    continue;
		    //System.exit(1);
		}
		kgWithXref++;
		kg.setGeneSymbol(geneSymbol);
		//System.out.println("x: \"" + geneSymbol + "\"");
	    } 
	    in.close();
	    System.out.println(String.format("Found kg for %d genes, missed it for %d",kgWithXref,kgWithNoXref));
	} catch (FileNotFoundException fnfe) {
	    String err = String.format("Could not find file: %s\n%s",this.ucscXrefPath,fnfe.toString());
	    throw new KGParseException(err);
	} catch (IOException e) {
	    String err = String.format("Exception while parsing UCSC KnownGene xref file at \"%s\"\n%s",
				       this.ucscXrefPath,e.toString());
	    throw new KGParseException(err);
	}
    }
}



