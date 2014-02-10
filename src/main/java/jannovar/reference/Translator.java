package jannovar.reference;

import java.util.HashMap;

import jannovar.exception.AnnotationException;

/**
 * This class helps to translate DNA sequences.
 * @author Peter N Robinson
 * @version 0.04 (Dec. 12, 2012)
 */
public class Translator {
    /** Map of genetic code. Keys are codons and values are the corresponding amino acid (one-letter code) */
    private HashMap<String,String> codon1=null;
    /** Map of genetic code. Keys are codons and values are the corresponding amino acid (three-letter code) */
    private HashMap<String,String> codon3=null;
    /** Map of IUPAC ambiguity codes.  */
    private HashMap<String,String> iupac=null;
//    /** Sequences of known gene mRNAs. Key, a UCSC identified such as uc010nxr.1; value: A cDNA sequence in
//	lower case letters, such as "cttgccgtcag..." */
//    private HashMap<String,String> fasta=null;
    
    private static Translator translator=null;

    
    private Translator() {
	initializeMaps();
    }
    
    /** Factory method to get reference to Translator.
     * @return  {@link Translator} singleton
     */
    static public Translator getTranslator() {
	if (Translator.translator == null) {
	    Translator.translator = new Translator();
	}
	return Translator.translator;
    }

    /**
     * Translates a DNA sequence. Assume the sequence is upper 
     * case with no ambiguous bases.
     * <P>
     * Currently, there is no need to translate more than a single codon. However,
     * some portions of the code are trying to translate DNA that is not a multiple
     * of 3 nt long (from indel code). Therefore, we will translate as much as possible here.
     * This may need refactoring in the future. (TODO).
     * @param dnaseq A DNA sequence that is to be translated
     * @return corresonding aminoacid sequence
     * @throws jannovar.exception.AnnotationException
     */
    public String translateDNA(String dnaseq) throws AnnotationException {
	StringBuilder aminoAcidSeq = new StringBuilder();
	int len = dnaseq.length();
	if (! (len%3 == 0) ) {
	    len = len - (len%3);
	    /* this forces len to be a multiple of 3. */
	    //String err = String.format("Attempt to translate sequence [%s] with length %d (should be 3n)",dnaseq,len);
	    //throw new AnnotationException(err);
	}
	for (int i=0; i<len; i += 3) {
	    String nt3 = dnaseq.substring(i,i+3);
	    String aa = this.codon1.get(nt3);
	    if (aa == null) {
		/*
		  String err = String.format("Could not find translation for codon:\"%s\" in sequence:\"%s\"", 
					   nt3, dnaseq);
					   throw new AnnotationException(err);
		*/
		break; /* stop translation */
	    }
	    aminoAcidSeq.append(aa);
	}
	return aminoAcidSeq.toString();
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



