package jannovar.io;

/** Logging */
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException; 
import java.io.FileNotFoundException;

import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;


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
 * @see <a href="http://hgdownload.cse.ucsc.edu/goldenPath/hg19/database/">UCSC hg19 database downloads</a>
 * <P>
 * This class now additionally parses the ucsc {@code KnownToLocusLink.txt} file, which contains cross
 * references from the ucsc IDs to the corresponding Entrez Gene ids (earlier known as Locus Link):
 * <PRE>
 * uc010eve.3      3805
 * uc002qug.4      3805
 * uc010evf.3      3805
 * ...
 * </PRE>
 * @author Peter N Robinson
 * @version 0.06 (21 May, 2013)
 */
public class UCSCKGParser {
    /** A logger from Apache's log4j that records key statistics from program execution. */
    static Logger log = Logger.getLogger(UCSCKGParser.class.getName());
    /** Path to the knownGene.txt file from UCSC */
    private String kgPath=null;
    
    /** Map of all known genes. Note that the key is the UCSC id, e.g., uc0001234.3, and the
     * value is the corresponding TranscriptModel object
     * @see jannovar.reference.TranscriptModel
    */
    private HashMap<String,TranscriptModel> knownGeneMap=null;


    /**
     * Set up and check the existence of the file knownGene.txt
     * @param ucscPath path to the UCSC file knownGene.txt.
     */
    public UCSCKGParser(String ucscPath) {
	this.kgPath = ucscPath;
	log.info("Set kg path to " + kgPath);
	this.knownGeneMap = new HashMap<String,TranscriptModel>();
    }


	
    /**
     * @return a reference to the {@link #knownGeneMap knownGeneMap}, which contains info and sequences on all genes.
     */
    public HashMap<String,TranscriptModel> getKnownGeneMap() {
	return this.knownGeneMap;
    }
    

    /**
     * Serializes the contents of the HashMap {@link #knownGeneMap knownGeneMap} 
     * @param filename Name and path of the file to serialize to
     */
    public void serializeKnownGeneMap(String filename) {
	if (this.knownGeneMap==null || this.knownGeneMap.size()==0) {
	    System.err.println("Error: attempt to serialize empty knownGeneMap");
	    return;
	}
	try {
	    FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(this.knownGeneMap);
            oos.close();
	} catch (IOException i) {
          i.printStackTrace();
	  System.err.println("Could not serialize knownGeneMap");
	  System.exit(1);
	}
    }


    /**
     * Deserializes the HashMap {@link #knownGeneMap knownGeneMap} that was 
     * originally created by parsing the four UCSC known gene files.
     * @param filename name of serialized file
     */
    @SuppressWarnings (value="unchecked")
    public void deserializeKnownGeneMap(String filename) {
	try
         {
            FileInputStream fileIn =
                          new FileInputStream(filename);
            ObjectInputStream in = new ObjectInputStream(fileIn);
	   
            this.knownGeneMap  = (HashMap<String,TranscriptModel>) in.readObject();
            in.close();
            fileIn.close();
        }catch(IOException i) {
            i.printStackTrace();
	    System.err.println("Could not deserialize knownGeneMap");
	    System.exit(1);
           
        }catch(ClassNotFoundException c) {
            System.out.println("Could not find HashMap<String,TranscriptModel> class.");
            c.printStackTrace();
            System.exit(1);
        }
    }



    /** 
     * Parses the UCSC knownGene.txt file.
     */
    public void parseFile() {
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
		    TranscriptModel kg = new TranscriptModel(line);
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
	    System.out.println("Could not find file: " + this.kgPath);
	    System.out.println(fnfe);
	    System.exit(1);
	} catch (IOException e) {
	    System.out.println("Exception while parsing UCSC KnownGene file at \"" + this.kgPath + "\"");
	    System.err.println(e);
	    System.exit(1);
	}
	log.info("Read a total of " + linecount + " lines from " + this.kgPath);
	log.info("Parsed total of " + this.knownGeneMap.size() + " knownGene transcripts.");
	log.info("There were " + exceptionCount + " exceptions thrown for unparsable lines.");
    }



    /**
     * Parses the ucsc KnownToLocusLink.txt file, which contains cross references from
     * ucsc KnownGene ids to Entrez Gene ids. The function than adds an Entrez gene
     * id to the corresponing {@link jannovar.reference.TranscriptModel TranscriptModel}
     * objects.
     * @param path The full path to the KnownToLocusLink.txt file
     */
    public void  readKnown2Locus(String path) {
	try{
	    FileInputStream fstream = new FileInputStream(path);
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
	    log.info(msg);
	} catch (FileNotFoundException fnfe) {
	    System.out.println("Could not find knownToLocusLink file: " + path);
	    System.out.println(fnfe);
	    System.exit(1);
	} catch (IOException e) {
	    System.out.println("Exception while parsing UCSC KnownToLocusfile at \"" + path + "\"");
	    System.err.println(e);
	    System.exit(1);
	}
	   
    }



    /**
     * Input FASTA sequences from the UCSC hg19 file {@code knownGeneMrna.txt}
     * Note that the UCSC sequences are all in lower case, but we convert them
     * here to all upper case letters to simplify processing in other places of this program.
     * The sequences are then added to the corresponding {@link jannovar.reference.TranscriptModel TranscriptModel}
     * objects.
     * @param fastapath full path to knownGeneMrna.txt file.
     */
    public void readFASTAsequences(String fastapath) {
	
	try{
	    FileInputStream fstream = new FileInputStream(fastapath);
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
	    System.out.println("Could not find file: " + fastapath);
	    System.out.println(fnfe);
	    System.exit(1);
	} catch (IOException e) {
	    System.out.println("Exception while parsing UCSC KnownGene FASTA file at \"" + fastapath + "\"");
	    System.err.println(e);
	    System.exit(1);
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
      * @param xrefpath full path to kgXref.txt file.
     */
    public void readKGxRefFile(String xrefpath) throws KGParseException {
	try{
	    FileInputStream fstream = new FileInputStream(xrefpath);
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
	    String err = String.format("Could not find file: %s\n%s",xrefpath,fnfe.toString());
	    throw new KGParseException(err);
	} catch (IOException e) {
	    String err = String.format("Exception while parsing UCSC KnownGene xref file at \"%s\"\n%s",
				       xrefpath,e.toString());
	    throw new KGParseException(err);
	}
    }
}



