package nsfp;


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

import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;



/** Logging */
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import nsfp.NSFP;
import nsfp.io.NSFP_Constants;

/**
 * Parse information from the NSFP chromosome files.
 * Place results of parsing into postgreSQL database.
 * Note that for some SNVs, there are multiple lines in the dbSNFP file.
 * For our analysis, we will take the line etc with the most pathogenic score,
 * that is, the worst case. We will mark those lines in our database with a flag
 * to indicate that more information is available in dbNFSP original files.

#base(NCBI.37) 
0) chr:pos   e.g., 11:193113
1) rsID      e.g., rs146566434
2) dbSNPVersion   e.g.,   dbSNP_134
3) Alleles    e.g., A/G
4) EuropeanAmericanAlleleCount e.g., A=3/G=8515
5) AfricanAmericanAlleleCount  e.g., A=134/G=4142  
6) AllAlleleCount  e.g.,  A=137/G=12657
7) MAFinPercent(EA/AA/All)    e.g., 0.0352/3.1338/1.0708
8) EuropeanAmericanGenotypeCount   e.g.,  AA=0/AG=3/GG=4256
9) AfricanAmericanGenotypeCount  e.g., AA=0/AG=134/GG=2004
10) AllGenotypeCount    e.g., AA=0/AG=137/GG=6260 
11) AvgSampleReadDepth e.g.,   76
12) Genes   e.g., SCGB1C1
13) GeneAccession  e.g., NM_145651.2
14) FunctionGVS   e.g., missense
15) AminoAcidChange    e.g., HIS,ARG
16) ProteinPos   e.g., 6/96
17) cDNAPos  e.g., 18
18) ConservationScorePhastCons  e.g.,  0.2
19) ConservationScoreGERP  e.g.,  -1.2
20) GranthamScore    e.g., NA
21) Polyphen  e.g., unknown
22) RefBaseNCBI37 e.g.,  C
23) ChimpAllele  e.g.,  C
24) ClinicalInfo  e.g., no
25) FilterStatus  e.g., PASS
26) OnIlluminaHumanExomeChip  e.g., no
27) GwasPubMedInfo   e.g.,  unknown


 * @author peter.robinson@charite.de
 * @date 19.08.2012
 */

public class ESP2SQLDumpParser implements NSFP_Constants {
     static Logger log = Logger.getLogger(ESP2SQLDumpParser.class.getName());
  
    static final int NUMBER_OF_FIELDS = 28;
   

    FileWriter fstream =null;
    BufferedWriter out =null;
   
    /** There are a small number of duplicate lines, apparently a mistake, use this to catch them */
    private HashSet<String> seen_vars;


    public ESP2SQLDumpParser() {

	String fname = "esp-dump.pg";

	try {
	     this.fstream = new FileWriter( fname);
	     this.out  = new BufferedWriter(fstream);
	} catch (IOException e) {
	    System.err.println("Could not initialize file handles for ESP output.");
	    System.err.println(e);
	    System.exit(1);
	} 
	this.seen_vars = new HashSet<String>();
    }

    /** This function needs to be called after we have written all the data to the file handles to 
	ensure that the data is actually written to disk before the program terminates. */
    public void cleanup_filehandles() {
	try {
	    this.out.close();
	} catch (IOException e) {
	    System.err.println("Could not close file handles.");
	    System.err.println(e);
	    System.exit(1);
	} 
    }

 

   




    /**
     * This method writes a dump line for one row of data. */
    public void  input_var(String chromosome,String position,String rs,String alleles,String counts) {
	/* Just to save some space, record non rs SNVs as "." */
	if (rs.equals("none")) rs = ".";
	/* Use numeric coding for X chromosome */
	if (chromosome.equals("X"))
	    chromosome = "23";
	

	String ref=".";
	String alt=".";
	if (alleles.length() == 3 && alleles.charAt(1) == '/') {
	    ref = alleles.substring(0,1);
	    alt = alleles.substring(2);
	}
	String A[] = counts.split("/");
	if (A.length != 2) {
	    System.out.println("Bad parse for counts: " + counts);
	    //System.exit(1);
	}
	if (A[0].charAt(1) != '=') {
	    System.out.println("Bad parse for counts: " + counts);
	    System.exit(1);
	}
	if (A[1].charAt(1) != '=') {
	    System.out.println("Bad parse for counts: " + counts);
	    System.exit(1);
	}
	String minor = A[0].substring(2);
	String major = A[1].substring(2);
	
	String freq = "0.0";
	try{
	    Integer x = Integer.parseInt(minor);
	    Integer y = Integer.parseInt(major);
	    float f = (float)x/(float)y;
	    freq = String.format("%.6f",f);
	} catch (NumberFormatException e) {
	    System.out.println("Bad parse for counts: " + counts +" major=" + major + ", minor=" + minor);
	    System.exit(1);
	}
	
	StringBuilder sb = new StringBuilder();
	String insert_variant = 
	    chromosome + "|" + position + "|" + ref + "|" + alt + "|" + 
	    minor + "|" + major + "|" + freq;

	

	// Write this version to file
	
	try {
	    out.write(insert_variant + "\n");
	} catch (IOException e) {
	    System.err.println("problems writing into ESP dump");
	    System.err.println("Offending line: " + insert_variant);
	    System.err.println(e);
	    log.trace("problems inserting into ESP dump");
	    log.trace("Offending line: " + insert_variant);
	    log.trace(e);
	} 
    }
	  



    /**
     * Get annotations for each of the variants in the VCG file for one chromosome.
     * Strategy is not to just split and examine good candidate lines.
     * It is probably quicker to compre strings rather than to transform
     * 	every string (position on chromosome) into an Integer, but this
     *	can be tested later.
     *@param c An integer representing the chromosome to be tested
     *@param a list of the snvs from the VCF file that are on this chromosome.
    */
    public void input_ESP_File(String path) {
	System.out.println("******************************");
	this.seen_vars.clear();
	long startTime = System.currentTimeMillis();

	int count=0;
	int varcount=0;
	
	System.out.println("Opening file: " + path);
	log.trace("Input file: " + path);
	
	try{     
	    FileInputStream fstream = new FileInputStream(path);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String line;
	    int cc = 0;
	    int good=0;
	    int bad = 0;
	    while ((line = br.readLine()) != null)   {
		if (line.startsWith("#")) continue; // comment.
		cc++;
		String A[] = line.split("\\s"); // split on whitespace
		//System.out.println(A[0] + "--" + A.length);
		if (A.length != NUMBER_OF_FIELDS) {
		    System.out.println("Length of line was " + A.length);
		    System.out.println("But i was expecting " + NUMBER_OF_FIELDS + " fields");
		    System.exit(1);
		}
		String var = A[0];
		String []B = var.split(":");
		if (B.length != 2) {
		    System.out.println("Bad parse for variant : " + var);
		    System.out.println("Did not contain two fields split by \":\"");
		    System.exit(1);
		}
		String chrom = B[0];
		String pos = B[1];
		String rs_id = A[1];
		String alleles = A[3];
		String allele_count=A[6];
		String hash = pos + chrom + alleles;
		if (seen_vars.contains(hash))
		    continue;
		else 
		    seen_vars.add(hash);

		//System.out.println(chrom + "-" + pos+ "\t" + rs_id + "\"\t" + alleles + "\tcount: " + allele_count);
		input_var(chrom,pos,rs_id,alleles,allele_count);
		

		
	    }
	
	    
	     

    
	     
	   
	   
	      long endTime = System.currentTimeMillis();
	      long duration = endTime - startTime;
	      long seconds = duration / 1000;
	      log.trace("Time to input chromosome: " + seconds);

      
	

	 in.close();
	 // Flush the write file handles.
	
    } catch (IOException e){
	System.err.println("Error: " + e.getMessage());
    }    
}



   





   


   
}