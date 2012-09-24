package exomizer.reference;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException; 
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * This class takes a variant position that is located within an exon and provides a corresponding annotation.
 * The code is based on the perl script {@code annotate_variation.pl} from Annovar.
 * The logic of this program is based loosely on the logic of Annovar. In brief,
 * <OL>
 * <LI>Read in all FASTA files for UCSC knownGenes
 * <LI>For each variant to be annotated, we expect to get the following information: my ($refcdsstart, $refvarstart, 
 $refvarend, $refstrand, $index, $exonpos, $nextline) = @{$refseqvar->{$seqid}->[$i]};
 * </OL>
 * @author Peter N Robinson
 * @version 0.01 (Sept. 22, 2012)
 */
public class ExonicVariantAnnotator {
    /** Map of genetic code. Keys are codons and values are the corresponding amino acid (one-letter code) */
    private HashMap<String,String> codon1=null;
    /** Map of genetic code. Keys are codons and values are the corresponding amino acid (three-letter code) */
    private HashMap<String,String> codon3=null;
    /** Map of IUPAC ambiguity codes.  */
    private HashMap<String,String> iupac=null;
    /** Sequences of known gene mRNAs. Key, a UCSC identified such as uc010nxr.1; value: A cDNA sequence in
	lower case letters, such as "cttgccgtcag..." */
    private HashMap<String,String> fasta=null;

    
    public ExonicVariantAnnotator(String fastapath) {
	initializeMaps();


    }


   


    /**
     * Initializes a set of maps that represent the gene code with various aminoacid codes. 
     * Also initializes map of IUPAC codes.
     */
    private void initializeMaps() {
	codon1 = new HashMap<String,String>();
	codon3 = new HashMap<String,String>();
	iupac  = new HashMap<String,String>();
	
	codon1.put("AAA","K");
	codon1.put("AAC","N");
	codon1.put("AAG","K");
	codon1.put("AAT","N");
	codon1.put("ACA","T");
	codon1.put("ACC","T");
	codon1.put("ACG","T");
	codon1.put("ACT","T");
	codon1.put("AGA","R");
	codon1.put("AGC","S");
	codon1.put("AGG","R");
	codon1.put("AGT","S");
	codon1.put("ATA","I");
	codon1.put("ATC","I");
	codon1.put("ATG","M");
	codon1.put("ATT","I");
	codon1.put("CAA","Q");
	codon1.put("CAC","H");
	codon1.put("CAG","Q");
	codon1.put("CAT","H");
	codon1.put("CCA","P");
	codon1.put("CCC","P");
	codon1.put("CCG","P");
	codon1.put("CCT","P");
	codon1.put("CGA","R");
	codon1.put("CGC","R");
	codon1.put("CGG","R");
	codon1.put("CGT","R");
	codon1.put("CTA","L");
	codon1.put("CTC","L");
	codon1.put("CTG","L");
	codon1.put("CTT","L");
	codon1.put("GAA","E");
	codon1.put("GAC","D");
	codon1.put("GAG","E");
	codon1.put("GAT","D");
	codon1.put("GCA","A");
	codon1.put("GCC","A");
	codon1.put("GCG","A");
	codon1.put("GCT","A");
	codon1.put("GGA","G");
	codon1.put("GGC","G");
	codon1.put("GGG","G");
	codon1.put("GGT","G");
	codon1.put("GTA","V");
	codon1.put("GTC","V");
	codon1.put("GTG","V");
	codon1.put("GTT","V");
	codon1.put("TAA","*");
	codon1.put("TAC","Y");
	codon1.put("TAG","*");
	codon1.put("TAT","Y");
	codon1.put("TCA","S");
	codon1.put("TCC","S");
	codon1.put("TCG","S");
	codon1.put("TCT","S");
	codon1.put("TGA","*");
	codon1.put("TGC","C");
	codon1.put("TGG","W");
	codon1.put("TGT","C");
	codon1.put("TTA","L");
	codon1.put("TTC","F");
	codon1.put("TTG","L");
	codon1.put("TTT","F");

	codon3.put("AAA","Lys");
	codon3.put("AAC","Asn");
	codon3.put("AAG","Lys");
	codon3.put("AAT","Asn");
	codon3.put("ACA","Thr");
	codon3.put("ACC","Thr");
	codon3.put("ACG","Thr");
	codon3.put("ACT","Thr");
	codon3.put("AGA","Arg");
	codon3.put("AGC","Ser");
	codon3.put("AGG","Arg");
	codon3.put("AGT","Ser");
	codon3.put("ATA","Ile");
	codon3.put("ATC","Ile");
	codon3.put("ATG","Met");
	codon3.put("ATT","Ile");
	codon3.put("CAA","Gln");
	codon3.put("CAC","His");
	codon3.put("CAG","Gln");
	codon3.put("CAT","His");
	codon3.put("CCA","Pro");
	codon3.put("CCC","Pro");
	codon3.put("CCG","Pro");
	codon3.put("CCT","Pro");
	codon3.put("CGA","Arg");
	codon3.put("CGC","Arg");
	codon3.put("CGG","Arg");
	codon3.put("CGT","Arg");
	codon3.put("CTA","Leu");
	codon3.put("CTC","Leu");
	codon3.put("CTG","Leu");
	codon3.put("CTT","Leu");
	codon3.put("GAA","Glu");
	codon3.put("GAC","Asp");
	codon3.put("GAG","Glu");
	codon3.put("GAT","Asp");
	codon3.put("GCA","Ala");
	codon3.put("GCC","Ala");
	codon3.put("GCG","Ala");
	codon3.put("GCT","Ala");
	codon3.put("GGA","Gly");
	codon3.put("GGC","Gly");
	codon3.put("GGG","Gly");
	codon3.put("GGT","Gly");
	codon3.put("GTA","Val");
	codon3.put("GTC","Val");
	codon3.put("GTG","Val");
	codon3.put("GTT","Val");
	codon3.put("TAA","*");
	codon3.put("TAC","Tyr");
	codon3.put("TAG","*");
	codon3.put("TAT","Tyr");
	codon3.put("TCA","Ser");
	codon3.put("TCC","Ser");
	codon3.put("TCG","Ser");
	codon3.put("TCT","Ser");
	codon3.put("TGA","*");
	codon3.put("TGC","Cys");
	codon3.put("TGG","Trp");
	codon3.put("TGT","Cys");
	codon3.put("TTA","Leu");
	codon3.put("TTC","Phe");
	codon3.put("TTG","Leu");
	codon3.put("TTT","Phe");
	
	iupac.put("-","-");
	iupac.put(".","-");
	iupac.put("A","AA");
	iupac.put("B","CGT");
	iupac.put("C","CC");
	iupac.put("D","AGT");
	iupac.put("G","GG");
	iupac.put("H","ACT");
	iupac.put("K","GT");
	iupac.put("M","AC");
	iupac.put("N","ACGT");
	iupac.put("R","AG");
	iupac.put("S","GC");
	iupac.put("T","TT");
	iupac.put("V","ACG");
	iupac.put("W","AT");
	iupac.put("Y","CT");
     
    }
    

}



