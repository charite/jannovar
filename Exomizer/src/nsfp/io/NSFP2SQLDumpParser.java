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
 * @author peter.robinson@charite.de
 * @date 10.08.2012
 */

public class NSFP2SQLDumpParser implements NSFP_Constants {
     static Logger log = Logger.getLogger(NSFP2SQLparser.class.getName());
  
   
    /** Keep list of genes we have already seen (no need to have multiple SQL table rows).
     Key: genesymbol, value: AUTOINCREMENT ID in the gene table.*/
    private HashMap<String,Integer> seen_genes;
    /** This variable will contain values such as A3238732G that represent the current SNV. 
	It will be used to deal with doubled lines for the same variant */
    private String current_var=null;
   

    FileWriter fstream_gene =null;
    BufferedWriter out_gene =null;
    FileWriter fstream_variant =null;
    BufferedWriter out_variant =null;
    private int auto_increment = 0;
    /**
     * Some of the lines in dbNSFP are repeated if there is more than one way to parse a variant in chromosomal notation.
     * For our database, we will just take the variant that has the highest predicted pathogenicity (based on SIFT for now).
     * Once we get to the last comparable entry, we will take one of the lines in this map to write to file. */
    private HashMap<Float,String> current_variant_list=null;
   
   
    

    public NSFP2SQLDumpParser() {
	seen_genes = new HashMap<String,Integer>();
	String fname_gene = "gene-dump.pg";
	String fname_variant = "variant-dump.pg";
	try {
	     this.fstream_gene = new FileWriter( fname_gene);
	     this.out_gene  = new BufferedWriter(fstream_gene);
	     this.fstream_variant = new FileWriter(fname_variant);
	     this.out_variant  = new BufferedWriter(fstream_variant);
	}catch (IOException e) {
	    System.err.println("Could not initialize file handles for output.");
	    System.err.println(e);
	    System.exit(1);
	} 
    }

    /** This function needs to be called after we have written all the data to the file handles to 
	ensure that the data is actually written to disk before the program terminates. */
    public void cleanup_filehandles() {
	try {
	    this.out_gene.close();
	    this.out_variant.close();
	} catch (IOException e) {
	    System.err.println("Could not close file handles.");
	    System.err.println(e);
	    System.exit(1);
	} 
    }

 /**
     * Add a line to the database table.
     * @return the automatically generated primary key of the item added to the table.
     */
    private int add_gene(String genename,String uniprot_acc,String uniprot_id,char strand,
			  String ensembl_gene_id,String ensembl_transcript_id)
    {
	this.auto_increment++; /* This is the unique key for the gene rows */
	try {
	    this.out_gene.write(auto_increment + "|" + genename + "|" + uniprot_acc + "|" +
				uniprot_id + "|"+ strand + "|" + ensembl_gene_id + "|" + 
				ensembl_transcript_id + "\n");
	} catch (IOException e) {
	    System.err.println("Could not write to gene dump file for gene " + genename);
	    System.err.println(e);
	    System.exit(1);
	}  
	this.seen_genes.put(genename,auto_increment);
	return auto_increment;
    }


   

    /** Many entries in dbNFSP are lists of transcripts separated by ";"
	For simplicity, we will just take the first such entry.
	TODO: Maby refactor. */
    private String first_entry(String s) {
	int i = s.indexOf(";");
	if (i>0)
	    s = s.substring(0,i);
	return s;
    }
    /** Some entries in dbNSFP are either nonnegative ints or "." .
     If the latter, then return -1 (NOPARSE; a flag) */
    private int get_int_value(String s) {
	if (s.equals(".")) 
	    return NOPARSE;
	Integer ret_value;
	int i = s.indexOf(";");
	if (i>0)
	    s = s.substring(0,i);
	try {
	    ret_value = Integer.parseInt(s);
	} catch (NumberFormatException e) {
	    System.err.println("Could not parse integer value: \"" + s + "\"");
	    return NOPARSE;
	}
	return ret_value.intValue();
    }

     /** Some entries in dbNSFP are either nonnegative floats or "." .
     If the latter, then return -1f (NOPARSE_FLOAT; a flag) */
    private float get_float_value(String s) {
	if (s.equals("."))
	    return NOPARSE_FLOAT;
	int i = s.indexOf(";");
	if (i>0)
	    s = s.substring(0,i);
	Float ret_value;
	try {
	    ret_value = Float.parseFloat(s);
	} catch (NumberFormatException e) {
	    System.err.println("Could not parse Float value: \"" + s + "\"");
	    return NOPARSE_FLOAT;
	}
	return ret_value.floatValue();
    }


    /**
     * This method calls the prepared SQL statement to insert a row of data. */
    public void insert_variant(int chromosome, int position,char ref, char alt, 
			       char aaref, char aaalt, int uniprot_aapos, int aapos, 
			       float sift, float polyphen2_HVAR, float mut_taster,
			       float phyloP,int ThGenom_AC, 
			       float ThGenom_AF, int gene_id_key) {
	     
	
	StringBuilder sb = new StringBuilder();
	String insert_variant = 
	    chromosome + "|" + position + "|" + ref + "|" + alt + "|" + 
	    aaref + "|" + aaalt + "|" + uniprot_aapos +  "|" + aapos + "|" +
	    sift + "|" +  polyphen2_HVAR + "|" + mut_taster  + "|" + 
	    phyloP + "|" + ThGenom_AC + "|" + ThGenom_AF  + "|" + gene_id_key;
	String cu_var = ref + Integer.toString(position) + alt;
	
	if (this.current_var == null) {
	    /* 1) Exectued the very first time. We put the variant into the HashMap after initializing it */
	     this.current_var = cu_var;
	     current_variant_list = new HashMap<Float,String>();
	     current_variant_list.put(sift,insert_variant);
	} else	if ( cu_var.equals(this.current_var) ) {
	    /* We have seen another version of this variant. Add it to the list. */
	    current_variant_list.put(sift,insert_variant);
	} else {
	    /* 3) We are starting with a new variant. We now want to write the most pathogenic version 
	       of the previous variant to file. Then we need to reset the HashMap and state variables. */
	    // Find most pathogenic version of the current variant.
	    float best_score = -100f;
	    String best_line = null;
	    for (Float f : current_variant_list.keySet()) {
   		if (f > best_score) {
		    best_score = f;
		    best_line = current_variant_list.get(f);
		}
	    }
	    // Write this version to file
	    if (best_line != null) {
    		try {
		    out_variant.write(insert_variant + "\n");
		} catch (IOException e) {
		    System.err.println("problems writing into variant sump");
		    System.err.println("Offending line: " + insert_variant);
		    System.err.println(e);
		    log.trace("problems inserting into variant table");
		    log.trace("Offending line: " + insert_variant);
		    log.trace(e);
		} 
	    }
	    // Now reset variables
	    this.current_variant_list.clear();
	    this.current_var = cu_var;
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
    public void input_chromosome(String path) {
	System.out.println("******************************");
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
		
		String A[] = line.split("\t");
		if (A.length < 49) {
		    System.out.println("Malformed line with only " + A.length 
				       + "fields (expecting 49");
		    System.exit(1);
		}
		String genename = first_entry(A[7]);
		if (cc % 2000 == 0) {
		    System.out.println("line " + cc + " (" + genename + ")");
		}
		
		int gene_id_key = -1;
		if (! seen_genes.containsKey(genename) ) {
		    String uniprot_acc = first_entry(A[UNIPROT_ACC]);
		    String uniprot_id = first_entry(A[UNIPROT_ID]);
		    String cds_strand = A[12];
		    char strand;
		    if (cds_strand.charAt(0) == '+') strand = '+';
		    else if (cds_strand.charAt(0) == '-') strand = '-';
		    else strand = '?';
		    
		    String ensembl_gene_id = first_entry(A[ENSEMBL_GENE_ID]);
		    String ensembl_transcript_id = first_entry(A[ENSEMBL_TRANSCRIPT_ID]);
		    gene_id_key = add_gene(genename,uniprot_acc,uniprot_id,strand,
					   ensembl_gene_id,ensembl_transcript_id);
		} else {
		    gene_id_key = seen_genes.get(genename);
		}
		String chr = A[0];
		Integer c;
		if (chr.equals("X"))
		    c = new Integer(23);
		else if (chr.equals("Y"))
		    c = new Integer(24);
		else if (chr.equals("M"))
		    c = new Integer(25);
		else
		    c = Integer.parseInt(A[0]);
		Integer pos = Integer.parseInt(A[POS]);
		char ref = A[REF].charAt(0);
		char alt = A[ALT].charAt(0);
		char aaref = A[AAREF].charAt(0);
		char aaalt = A[AAALT].charAt(0);
		int uniprot_aapos = get_int_value(A[UNIPROT_AAPOS]);
		int aapos = get_int_value(A[AAPOS]);
		float sift =get_float_value(A[SIFT_SCORE]);
		float polyphen2_HVAR = get_float_value(A[POLYPHEN2_HVAR_SCORE]);
		float mut_taster = get_float_value(A[MUTATION_TASTER_SCORE]);
		float phyloP = get_float_value(A[PHYLO_P]);
	
		int ThGen_AC = get_int_value(A[TG_1000Gp1_AC]);
		float ThGen_AF = get_float_value(A[TG_1000Gp1_AF]);
	     
		 
		insert_variant(c, pos, ref, alt, aaref, aaalt, uniprot_aapos, aapos,
			       sift, polyphen2_HVAR,  mut_taster, 
			       phyloP, ThGen_AC,ThGen_AF, gene_id_key);
	     
	     

    
	     
	   
	   
	      long endTime = System.currentTimeMillis();
	      long duration = endTime - startTime;
	      long seconds = duration / 1000;
	      log.trace("Time to input chromosome: " + seconds);

      
	 }

	 in.close();
	 // Flush the write file handles.
	 out_gene.flush();
	 out_variant.flush();
    } catch (IOException e){
	System.err.println("Error: " + e.getMessage());
    }    
}



   





   


   
}