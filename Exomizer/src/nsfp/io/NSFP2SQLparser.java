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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/** Logging */
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


import nsfp.NSFP;
import nsfp.io.NSFP_Constants;

/**
 * Parse information from the NSFP chromosome files.
 * Place results of parsing into mySQL database.
 * Note that for some SNVs, there are multiple lines in the dbSNFP file.
 * For our analysis, we will take the line etc with the most pathogenic score,
 * that is, the worst case. We will mark those lines in our database with a flag
 * to indicate that more information is available in dbNFSP original files.
 * @author peter.robinson@charite.de
 * @date 10.08.2012
 */

public class NSFP2SQLparser implements NSFP_Constants {
     static Logger log = Logger.getLogger(NSFP2SQLparser.class.getName());
    Connection connection=null;
    PreparedStatement insert_variant=null;
    PreparedStatement update_variant=null;
    /** Keep list of genes we have already seen (no need to have multiple SQL table rows).
     Key: genesymbol, value: AUTOINCREMENT ID in the gene table.*/
    private HashMap<String,Integer> seen_genes;
    /** This variable will contain values such as A3238732G that represent the current SNV. 
	It will be used to deal with doubled lines for the same variant */
    private String current_var=null;
    private float current_sift;
   
   
    

    public NSFP2SQLparser(Connection con) {
	this.connection = con;
	seen_genes = new HashMap<String,Integer>();
	initialize_prepared_statements();
    }


    /**
     * THe insert queries are used lots, store them as a prepared statement to improve performance. */
    private void initialize_prepared_statements() {
	try {
	    insert_variant = 
	    connection.prepareStatement( "INSERT INTO variant (chromosome,position,ref,alt,aaref,aaalt,uniprot_aapos,"+
					 "aapos,sift,polyphen,mut_taster,ThGenomes_AC,gene_id) "+
					 "VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?)" );
	} catch (SQLException e) {
	    System.err.println("Problems initializing insert_variant statement");
	    System.err.println(e);
	    System.exit(1);
	}
	// Now prepare the update statement
	try{
	    this.update_variant =
		connection.prepareStatement(" UPDATE variant SET aaref = ?, aaalt = ?, uniprot_aapos = ?, "+
					    " aapos = ?, sift = ?, polyphen = ?, mut_taster = ?, ThGenomes_AC = ?, gene_id = ? "+
					    " WHERE chromosome = ? AND position = ? AND ref = ? AND alt = ?");

	    } catch (SQLException e) {
	    System.err.println("Problems initializing update_variant statement");
	    System.err.println(e);
	    System.exit(1);
	}

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
    public void insert_new_variant(int chromosome, int position,char ref, char alt, char aaref, char aaalt, int uniprot_aapos,
				   int aapos, float sift, float polyphen2_HVAR, float mut_taster,int ThGen_AC, int gene_id_key) {
	  	      
	      try {
		   insert_variant.setInt(1,chromosome);
		   insert_variant.setInt(2,position);
		   insert_variant.setString(3,Character.toString(ref));
		   insert_variant.setString(4,Character.toString(alt));
		   insert_variant.setString(5,Character.toString(aaref));
		   insert_variant.setString(6,Character.toString(aaalt));
		   insert_variant.setInt(7,uniprot_aapos);
		   insert_variant.setInt(8,aapos);
		   insert_variant.setFloat(9,sift);
		   insert_variant.setFloat(10,polyphen2_HVAR);
		   insert_variant.setFloat(11,mut_taster);
		   insert_variant.setInt(12,ThGen_AC);
		   insert_variant.setInt(13,gene_id_key);
		   insert_variant.execute();

	      } catch (SQLException e) {
		  System.err.println("problems inserting into variant table");
		  System.err.println("Offending line: " + insert_variant);
		  System.err.println(e);
		  log.trace("problems inserting into variant table");
		  log.trace("Offending line: " + insert_variant);
		
		 
		  log.trace(e);
	      }

    }

    /**
     * This method calls the prepared SQL statement to insert a row of data. */
    public void update_variant(int chromosome, int position,char ref, char alt, char aaref, char aaalt, int uniprot_aapos,
				   int aapos, float sift, float polyphen2_HVAR, float mut_taster,int ThGen_AC, int gene_id_key) {
	  	      
	      try {
		   update_variant.setString(1,Character.toString(aaref));
		   update_variant.setString(2,Character.toString(aaalt));
		   update_variant.setInt(3,uniprot_aapos);
		   update_variant.setInt(4,aapos);
		   update_variant.setFloat(5,sift);
		   update_variant.setFloat(6,polyphen2_HVAR);
		   update_variant.setFloat(7,mut_taster);
		   update_variant.setInt(8,ThGen_AC);
		   update_variant.setInt(9,gene_id_key);
		   update_variant.setInt(10,chromosome);
		   update_variant.setInt(11,position);
		   update_variant.setString(12,Character.toString(ref));
		   update_variant.setString(13,Character.toString(alt));
		   update_variant.executeUpdate();
		   //connection.commit();
		  

	      } catch (SQLException e) {
		  System.err.println("problems updateing variant table");
		  System.err.println("Offending line: " + update_variant);
		  System.err.println(e);
		  log.trace("problems updating variant table");
		  log.trace("Offending line: " + update_variant);
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
    public void input_chromosome(String path) {

	
	System.out.println("******************************");
	long startTime = System.currentTimeMillis();


	int count=0;
	int varcount=0;
	


	 System.out.println("Opening file: " + path);
	 log.trace("Input file: " + path);

	
	 // First is the first field and the following tab.
	 // sanity check. All lines should begin like this.
	 
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
		  gene_id_key = add_gene(genename,uniprot_acc,uniprot_id,strand,ensembl_gene_id,ensembl_transcript_id);
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
	      int ThGen_AC = get_int_value(A[TG_1000Gp1_AC]);

	      String current = A[REF] + A[POS] + A[ALT];
	      if ( ! current.equals(current_var) ) {
		  // not a duplicate line
		  insert_new_variant(c, pos, ref, alt, aaref, aaalt, uniprot_aapos, aapos,sift, polyphen2_HVAR,  mut_taster, ThGen_AC,gene_id_key);
	      } else if (sift < current_sift) {
		  update_variant(c, pos, ref, alt, aaref, aaalt, uniprot_aapos, aapos,sift, polyphen2_HVAR,  mut_taster, ThGen_AC,gene_id_key);

	      }
	      this.current_var = current;
	      this.current_sift = sift;

    
	     
	   
	   
	      long endTime = System.currentTimeMillis();
	      long duration = endTime - startTime;
	      long seconds = duration / 1000;
	      log.trace("Time to input chromosome: " + seconds);

      
	 }

	 in.close();
    } catch (IOException e){
	System.err.println("Error: " + e.getMessage());
    }    
}



   





    /**
     * Add a line to the database table.
     * @return the automatically generated primary key of the item added to the table.
     */
    private int add_gene(String genename,String uniprot_acc,String uniprot_id,char strand,
			  String ensembl_gene_id,String ensembl_transcript_id)
    {
	String insert=null;
	// The the id, which is automatically generated
	int autoIncKeyFromFunc = -1;
	try {
	    Statement st = connection.createStatement();
	    insert = String.format("INSERT INTO gene (genename,uniprot_acc,uniprot_id,"+
				   "cds_strand,ensembl_geneid,ensembl_transcript_id) " +
				   " VALUES(\'%s\',\'%s\',\'%s\',\'%c\',\'%s\',\'%s\') ",
				   genename,uniprot_acc,uniprot_id,strand, 
				   ensembl_gene_id,ensembl_transcript_id);
	    st.executeUpdate(insert,Statement.RETURN_GENERATED_KEYS);



	 
	    ResultSet rs = null;
	    rs = st.getGeneratedKeys();
	    if (rs.next()) {
		autoIncKeyFromFunc = rs.getInt(1);
		System.out.println("Got key = " + autoIncKeyFromFunc);
	    } else {
		System.err.println("Not able to retrieve AUTO INCREMENT id while inserting gene " + genename);
		System.exit(1);
	    }
	    rs.close();
	} catch (SQLException e) {
            System.err.println("problems inserting into table gene");
	    System.err.println("Offending line: " + insert);
	    System.err.println(e);
	    System.exit(1);
	}
	this.seen_genes.put(genename,autoIncKeyFromFunc);
	//System.out.println("Id = " +  autoIncKeyFromFunc + " gene" + genename);
	return autoIncKeyFromFunc;
    }


   
}