package jannovar.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException; 

import java.util.ArrayList;

import jannovar.exome.Variant;
import jannovar.exception.VCFParseException;

/**
 * This class reads input files in Annovar format, which we are using to debug and 
 * check the correctness of this program. Such files can be created from VCF files
 * using the annovar script: </BR>
 * {@code perl convert2annovar.pl  -includeinfo  -format vcf4 variantfile > variant.avinput}
 * <P>
 * The format of the avinput file is </BR>
 * </BR>
 * {@code 1	67478546	67478546	G	A	comments: rs11209026 (R381Q), a SNP in IL23R associated with Crohn's disease}
 * <P>
 * The meaning of the fields is
 * <UL>
 * <LI>chromosome (without "chr")
 * <LI>ref start position
 * <LI>ref end position
 * <LI>ref sequence
 * <LI>alt sequence
 * <LI>Optional comment
 * </UL>
 * <P>
 * We will create {@link exomizer.exome.Variant Variant} objects from these lines, 
 * although most of the fields of the variant (which is intended to
 * represent a Variant as well as metadata from a VCF file) will be empty.
 * <P>
 * Note that now that the Exomizer is running well, this class should probably no longer be
 * needed, but it is maintained should the need for debugging arise.
 * @author Peter N Robinson
 * @version 0.04 (April 16, 2013)
 */

public class AnnovarParser {
    /** Path to annovar input file with variants to be tested. */
    private String annovarFilePath=null;
    /** List of variants from the input annovar file. */
    private ArrayList<Variant> variantList=null;

    public AnnovarParser(String path) {
	this.annovarFilePath = path;
	variantList = new ArrayList<Variant>();
	parseFile();
    }

    public ArrayList<Variant> getVariantList() { return this.variantList; }


    public void parseFile() {
	try{     
	    FileInputStream fstream = new FileInputStream(this.annovarFilePath);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String line;
	 
	    while ((line = br.readLine()) != null)   {
		//System.out.println(line);
		if (line.startsWith("#"))
		    continue; // Skip comments.
		String A[] = line.split("\t");
		String chr = A[0];
		int pos=-1,endpos=-1;
		if (A.length < 5) continue; // probably empty line
		try{
		    pos = Integer.parseInt(A[1].trim());
		    endpos = Integer.parseInt(A[2].trim());
		} catch (NumberFormatException e) {
		    System.err.println("Number format exception for annovar file:\n\t" + line);
		    System.err.println("Field 1: \""+ A[1] + "\"   Field 2: \"" + A[2] + "\"");
		    System.err.println(e);
		    System.exit(1);
		}
		String ref = A[3];
		String alt = A[4];
		if (ref.equals("-") && alt.equals("-")) {
		    /* Both are empty allele: malformed! */
		    System.err.println("Malformed annovar line, ref and alt are \"-\": " + line);
		    continue;
		}
		if (ref.matches("[^ACGT0\\-]")) {
		    System.err.println("Malformed annovar line, non-standard nucleotide code: " + line);
		    /* Non standard nucleotide code */
		    continue;
		}
		if (alt.matches("[^ACGT0\\-]")) {
		    System.err.println("Malformed annovar line, non-standard nucleotide code: " + line);
		    /* Non standard nucleotide code */
		    continue;
		}
		if (pos > endpos) {
		    System.err.println("Malformed annovar line, bad start/end position:" + line);
		    continue;
		}
		if (! ref.equals("0") && endpos - pos + 1 != ref.length()
		    ||
		    ref.equals("-") && pos != endpos) {
		    System.err.println("Malformed annovar line, length mismatch for insertion: \n\t\"" + line+"\"");
		    System.err.println("Chr = " + chr + "\npos="+pos + "\nref="+ ref + "\nalt=" + alt);
		    System.exit(1);
		}
		
		try {	
		    Variant v = new Variant(chr, pos, ref, alt); 
		    this.variantList.add(v);
		} catch (VCFParseException ve) {
		    System.out.println(ve.toString());
		    continue;
		}
	    }
	     in.close();
	} catch (IOException e){
	    System.err.println("Error reading annovar input file: " + e.getMessage());
	}    

    }


}