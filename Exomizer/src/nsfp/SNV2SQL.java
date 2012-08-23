package nsfp;


import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
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
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/** Logging */
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import nsfp.snv.SNV;

/**
 * This class expects to be get a list of SNV objects that basically correspond
 * to lines in a VCF file. It then asks the SQL database nsfpalizer for the
 * dbNSFP-derived annotations. See README file for how to setup the postgreSQL
 * database.
 * @author peter.robinson@charite.de
 * @date 19.08.2012
 */

public class SNV2SQL {
    static Logger log = Logger.getLogger(SNV2SQL.class.getName());
    
    /** List of NSFP objects, one for each missense/substitution variant. */
    private ArrayList<NSFP> nsfp_hits=null;
    /** Total number of candidate variants tested. We use this for debugging. */
    private int n_vars_tested=0;
    private boolean use_pathogenicity_filter = false;
    private boolean use_thousand_genomes_filter=false;
    private float thousand_genomes_threshold=1.0f;
    private boolean use_quality_filter=false;
    private int quality_threshold = 0;
    
    private Connection connection=null;

    private PreparedStatement findGeneStatement = null;
    private PreparedStatement findESPStatement = null;
    private StringBuilder message=null;
    private int no_data_for_snv=0;
    

    public SNV2SQL() {
	nsfp_hits = new ArrayList<NSFP>();
	openDatabaseConnection();
	this.message = new StringBuilder();
    }

     /**
     * Connect to mysql database and store connection in handle
     * this.connect. Prepare the query statements.*/
    public void openDatabaseConnection() {
	String URL = "jdbc:postgresql://localhost/nsfpalizer";
        String username = "nsfp";
        String password = "vcfanalysis";
        
	String query = String.format("SELECT genename, uniprot_id, ensembl_geneid,aapos,sift,"+
				     "polyphen,mut_taster,phyloP,ThGenomes_AC,ThGenomes_AF " +
				     "FROM gene, variant " +
				     "WHERE chromosome = ? "+
				     "AND position = ? " +
				     "AND ref = ? " +
				     "AND alt = ? "+
				     "AND gene.gene_id = variant.gene_id");

        try {
            this.connection = 
		DriverManager.getConnection (URL, username,password);
	    this.findGeneStatement = connection.prepareStatement(query);
        } catch (SQLException e) {
            System.err.println("problems for database connection: "+URL);
	    System.err.println(e);
        }

	String espQuery = "SELECT minor, major, frequency  " +
	    " FROM esp "+
	    "WHERE chromosome = ? "+
	    "AND position = ? "+
	    "AND ref = ? " +
	    "AND alt = ? ";
	try {
	    this.findESPStatement = connection.prepareStatement(espQuery);
        } catch (SQLException e) {
            System.err.println("problems for preparing ESP query: "+ espQuery);
	    System.err.println(e);
        }
    }

    public String get_HTML_filtering_message() { return this.message.toString(); }

    public ArrayList<NSFP> get_nsfp_hits() { return nsfp_hits; }

    /** Cause this parse to use a threshold value for the SNVs. If 
	a mutation is above the threshold, it will not be reported. */
    public void set_pathogenicity_filter(){
	this.use_pathogenicity_filter = true;
    }

    /**
     * Use a frequency filter for thousand genomes and take only those
     * variants with a lower frequency. */
    public void set_thousand_genomes_filter(float threshold) {
	this.use_thousand_genomes_filter=true;
	this.thousand_genomes_threshold=threshold;
    }
    /**
     * This is a genotype quality threshold from the GQ field of the VCF files.
     */
    public void set_quality_threshold(int th) {
	this.use_quality_filter=true;
	this.quality_threshold = th;
    }
    /**
     * This is the main function to compare the dbNSFP data for a list
     * of SNVs extracted from the VCF file.
     * @param snv a list of all SNVs extracted from the user input data (a VCF file).
     */
    public void getDataForListOfSNVs(ArrayList<SNV> snv_list) {
	Collections.sort(snv_list);
	//System.out.println("Total number of SNVs in input file: " + snv_list.size());
	log.trace("Total number of SNVs in input file: " + snv_list.size());
	message.append("<UL><LI>Total SNVs before filtering " + snv_list.size() + "</LI>");

	Iterator<SNV> it = snv_list.iterator();
	/* 1) Filter out bad quality reads if desired by user */
	if (this.use_quality_filter) {
	    while (it.hasNext()) {
		SNV s = it.next();
		if (! s.passes_genotype_quality_threshold(quality_threshold))
		    it.remove();
		else if (s.is_homozygous_ref())
		    it.remove(); /* Not a mutation, probably an indication of bad quality, check this. */
	    }
	    message.append("<LI>Total SNVs after quality filtering " + snv_list.size() + "</LI>");
	}


	/* 2) Filter on ESP frequency */
	if (use_thousand_genomes_filter) {
	    it = snv_list.iterator();
	    while (it.hasNext()) {
		SNV s = it.next();
		retrieve_ESP_data(s);
		if ( ! s.is_rarer_than_ESP_threshold(thousand_genomes_threshold) )
		    it.remove();
	    }
	    message.append("<LI>Total SNVs after ESP-frequency filtering " + snv_list.size() + "</LI>");
	}

	

	it = snv_list.iterator();
	while (it.hasNext()) {
	    SNV s = it.next();
	   
	    NSFP nsfp = null;
	    if (s.is_non_SNV_pathogenic()) {
		/** This SNV cannot be evaluated by NSFP because it is not
		    a substitution, so just add it.
		TODO  Add frequency data for nonsense mutations with 5000 Exomes.*/
		 nsfp = NSFP.get_NSFP_for_non_substition_variant(s);
		 if (nsfp == null) continue; // not a pathogenic class, e.g., intergenic.
	    } else {		
		nsfp = retrieve_data_for_SNV(s);
		if (nsfp == null) continue;
		if (use_pathogenicity_filter && ! nsfp.is_predicted_pathogenic() ) {
		    nsfp = null;
		    continue; // This variant is not predicted pathogenic.
		}
		if (use_thousand_genomes_filter && ! nsfp.is_rarer_than_threshold(thousand_genomes_threshold) ) {
		    nsfp = null;
		    continue;
		}
	    }
	    if (nsfp == null) continue; // null is flag for not passing threshold.
	   
	    this.nsfp_hits.add(nsfp);
	}
	message.append("<LI>Total SNVs after pathogenicity filtering " + nsfp_hits.size() + "</LI></UL>");
	//System.out.println("Total vars tested: " + n_vars_tested);
	log.trace("Total vars tested: " + n_vars_tested);
    }


    public void retrieve_ESP_data(SNV snv) {
	int chrom = snv.get_chromosome();
	int position = snv.get_position();
	char ref = snv.ref_as_char();
	char alt = snv.var_as_char();
	ResultSet rs = null;
	try {
	    this.findESPStatement.setInt(1,chrom);
	    this.findESPStatement.setInt(2,position);
	    this.findESPStatement.setString(4,Character.toString(ref));
	    this.findESPStatement.setString(3,Character.toString(alt));
	    //System.out.println("Query = " + findESPStatement);
	    rs = findESPStatement.executeQuery();
	    while ( rs.next() ) {
		int minor = rs.getInt(1);
		int major = rs.getInt(2);
		float freq = rs.getFloat(3);
		//System.out.println("Got minor=" + minor + " major = " + major + " freq = " +freq);
		//System.exit(1);
		snv.set_esp_minor(minor);
		snv.set_esp_major(major);
		snv.set_esp_frequency(freq);
	    }
	} catch(SQLException e) {
	    System.err.println("Error executing prepared ESP query");
	    System.err.println(e);

	}
    }


    /**
     * This function queries the database to get dbNSFP information
     * for a single nucleotide SNV.
     */
    public NSFP retrieve_data_for_SNV(SNV snv) {
	int chrom = snv.get_chromosome();
	int position = snv.get_position();
	char ref = snv.ref_as_char();
	char alt = snv.var_as_char();
	ResultSet rs = null;
	NSFP nsfp = null;


	try {
	    this.findGeneStatement.setInt(1,chrom);
	    this.findGeneStatement.setInt(2,position);
	    this.findGeneStatement.setString(3,Character.toString(ref));
	    this.findGeneStatement.setString(4,Character.toString(alt));
	    
	    rs = findGeneStatement.executeQuery();

	    //ResultSetMetaData rsmd = rs.getMetaData();
	    //int numberOfColumns = rsmd.getColumnCount();
	    //System.out.println("n columns = " + numberOfColumns);
	    //System.out.println("chrom = " + chrom + " pos = " + position);
	    while ( rs.next() ) {
                String  genename = rs.getString(1);
		String uniprot_id = rs.getString(2);
		String ensembl_geneid = rs.getString(3);
		int aapos = rs.getInt(4);
		float sift = rs.getFloat(5);
		float polyphen = rs.getFloat(6);
		float mut_taster = rs.getFloat(7);
		float phylop = rs.getFloat(8);
		int ThGenomes_AC = rs.getInt(9);
		float ThGenomes_AF = rs.getFloat(10);
		//System.out.println("Retrieve data for SNV at pos: " + snv.get_position());
		nsfp = new NSFP(snv, chrom,position,Character.toString(ref),Character.toString(alt));
		nsfp.set_genename(genename);
		nsfp.set_uniprot_id(uniprot_id);
		nsfp.set_ensembl_geneid(ensembl_geneid);
		nsfp.set_aapos(aapos);
		nsfp.set_sift(sift);
		nsfp.set_polyphen_HVAR(polyphen);
		nsfp.set_mut_taster(mut_taster);
		nsfp.set_phylo_p(phylop);
		nsfp.set_ThGenomes_AC(ThGenomes_AC);
		nsfp.set_ThGenomes_AF(ThGenomes_AF);
	    }
	    rs.close();
	} catch(SQLException e) {
	    System.err.println("Error execting prepared query");
	    System.err.println(e);
	}
	if (nsfp == null) {
	    this.no_data_for_snv++;
	    /** Could not get data, should not happen. TODO check this */
	}
	return nsfp;
    }




    



}