package nsfp.io;

/** Logging */
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException; 

import java.util.ArrayList;
import java.util.HashMap;

import nsfp.snv.SNV;


/**
 * VCFReader.java
 * Purpose: Parse a VCF file and extract SNVs for analysis
 * by dbNSFP
 * author: peter.robinson@charite.de
 * @date 9.08.2012
 */
public class VCFReader {
    static Logger log = Logger.getLogger(VCFReader.class.getName());
    private String file_path=null;
    private String base_filename = null;
    /** All of the lines in the original VCF header */
    private ArrayList<String> header=null;

    /** List of all single nucleotide polymorphisms from this VCF file */
    private ArrayList<SNV> snv_list=null;

    private StringBuilder message_string = null;
    /** What is index in this VCF file where the sample(s) start? */
    private  int begin_sample_index;


    /** List of codes for FORMAT field in VCF */
    public static final int FORMAT_GENOTYPE = 1; /* Genotype  */
    public static final int FORMAT_GENOTYPE_QUALITY = 2; /* Genotype Quality */
    public static final int FORMAT_LIKELIHOODS = 3; /* Likelihoods for RR,RA,AA genotypes (R=ref,A=alt) */
    public static final int FORMAT_HIGH_QUALITY_BASES = 4; /*# high-quality bases */
    public static final int FORMAT_PHRED_STRAND_BIAS = 5; /* Phred-scaled strand bias P-value */
    public static final int FORMAT_PHRED_GENOTYPE_LIKELIHOODS = 6; /*List of Phred-scaled genotype likelihoods */

    /** Does this VCF file have a format column? */
    private boolean has_format = false;
    /** List of samples on this VCF file. Key: index, Value: name */
    private HashMap<Integer,String> sample_name_map = null;



    public VCFReader(String path) {
	log.trace("Parsing VCF file: " + path);
	file_path = path;
	File file = new File(path);
	this.base_filename = file.getName();
	snv_list = new ArrayList<SNV>();
	header= new ArrayList<String>();
	message_string = new StringBuilder();
	sample_name_map = new  HashMap<Integer,String>();
	parse_file();
    }

    public ArrayList<SNV> get_snv_list() { return this.snv_list;} 

    public ArrayList<String> get_vcf_header() { return header; }

    public String get_html_message() { return this.message_string.toString(); }

    /**
     * This method parses the entire VCF file.
     * It places all header lines into the arraylist "header" and the remaining lines are parsed into
     * SNVs. For now, the SNVs are provided with a copy of the original VCF line in their field called
     * anno. This needs to be made nicer and more flexible.
     */
    private void parse_file() {
	try{
	    FileInputStream fstream = new FileInputStream(this.file_path);
	    DataInputStream in = new DataInputStream(fstream);
	    BufferedReader br = new BufferedReader(new InputStreamReader(in));
	    String line;
	    int linecount=0;
	    int snvcount=0;
	    // The first line of a VCF file should include the VCF version number
	    // e.g., ##fileformat=VCFv4.0
	    line = br.readLine();
	    if (line == null) {
		System.err.println("Error: First line of VCF file was not read (null pointer)");
		System.err.println("File: " + this.file_path);
		System.exit(1);
	    }
	    if (!line.startsWith("##fileformat=VCF")) {
		System.err.println("Error: First line of VCF file did not start with format:" + line);
		System.exit(1);
	    } else {
		header.add(line);
	    }
	    String version = line.substring(16).trim();
	    log.trace("VCF Format: " + version);
	    // System.out.println("VCF Format: " + version);
	    

	    while ((line = br.readLine()) != null)   {
		if (line.isEmpty()) continue;
		if (line.startsWith("##")) { header.add(line); continue; }
		if (line.startsWith("#CHROM")) {
		    parse_chrom_line(line); // Line with FORMAT and Sample names. 
		    // has_format and begin_sample_index are set in the chrom line function above
		    // they are the same for every line
		    // therefore, set them as static (class-level) variables.
		    VCFLine.set_has_format(this.has_format); // Does this VCF file have a format field?
		    VCFLine.set_sample_begin_index(this.begin_sample_index); // Where to start to look for sample data
		    header.add(line); 
		    continue; 
		}
		VCFLine ln = new VCFLine(line);
		SNV snv=null;
		try {
		    snv = new SNV(ln.get_chromosome_as_string(), ln.get_position(),
				  ln.get_reference_sequence(),ln.get_alternate_sequence());
		} catch (NumberFormatException e) {
		    //Exceptions may occur for variants such as 11_gl000202_random
		    // For now, just skip this. Refactor later
		    continue;
		}
		if (ln.is_homozygous_alt()) 
		    snv.set_homozygous_alt();
		else if (ln.is_heterozygous()) 
		    snv.set_heterozygous();
		else if (ln.is_homozygous_ref()) {
		    snv.set_homozygous_ref();
		}
		int variant_type = ln.get_variant_type_as_int();
		snv.set_variant_type(variant_type);
		String gname = ln.get_genename();
		String refseq = ln.get_refseq();
		String cdsmut = ln.get_cds_mutation(); 
		String aamut = ln.get_aa_mutation();
		int gt_qual = ln.get_genotype_quality();
		snv.set_genename(gname);
		snv.set_refseq_mrna(refseq);
		snv.set_cds_mutation(cdsmut);
		snv.set_aa_mutation(aamut);
		snv.set_genotype_quality(gt_qual);
		if (snv.genotype_not_initialized()) {
		    System.err.println("Error: Did not initialize genotype of SNV object");
		    ln. dump_VCF_line_for_debug();
		    System.out.println("SNV has genotype:" + snv. get_genotype_as_string());
		    System.exit(1);
		}
		/* Just add class predicted to be pathogenic such as nonsense */
		if (ln.is_predicted_non_missense_path()) {
		    snv.set_predicted_non_missense_path();
		    this.snv_list.add(snv);
		} else if (ln.is_SNV()) {
		    /* Here, add missense, we do not yet know if they are predicted pathogenic,
		       we will ask the database. */
		    this.snv_list.add(snv);
		}
		/* Skip other SNVs. */
		linecount++;
	    }
	} catch (IOException e){
	    System.err.println("Error: " + e.getMessage());
	}
    }



    public void parse_chrom_line(String line)
    {
	String A[] = line.split("\t");
	/* First check that obligatory format is correct */
	int n_errors = 0;

	message_string.append("<P>VCF File: " + this.base_filename + ".</P>");
	if (! A[0].equals("#CHROM") ) {
	    message_string.append("<P>Error: Did not find #CHROM column in right place but instead:" + A[0] + "</P>");
	    n_errors++;
	}
	if (! A[1].equals("POS") ) {
	    message_string.append("<P>Error: Did not find POS column in right place but instead:" + A[1] + "</P>");
	    n_errors++;
	}
	if (! A[2].equals("ID") ) {
	    message_string.append("<P>Error: Did not find ID column in right place but instead:" + A[2] + "</P>");
	    n_errors++;
	}
	if (! A[3].equals("REF") ) {
	    message_string.append("<P>Error: Did not find REF column in right place but instead:" + A[3] + "</P>");
	    n_errors++;
	}
	if (! A[4].equals("ALT") ) {
	    message_string.append("<P>Error: Did not find ALT column in right place but instead:" + A[4] + "</P>");
	    n_errors++;
	}
	if (! A[5].equals("QUAL") ) {
	    message_string.append("<P>Error: Did not find QUAL column in right place but instead:" + A[5] + "</P>");
	    n_errors++;
	}
	if (! A[6].equals("FILTER") ) {
	    message_string.append("<P>Error: Did not find FILTER column in right place but instead:" + A[6] + "</P>");
	    n_errors++;
	}
	if (! A[7].equals("INFO") ) {
	    message_string.append("<P>Error: Did not find INFO column in right place but instead:" + A[7] + "</P>");
	    n_errors++;
	}
	if (A[8].equals("FORMAT") ) {
	    this.has_format = true;
	    begin_sample_index = 9;
	} else {
	    begin_sample_index = 8;
	}

	message_string.append("<P>Samples:</P><UL>\n");
	for (int i=begin_sample_index; i< A.length; ++i) {
	    message_string.append("<LI>" + A[i] + "</LI>\n");
	    sample_name_map.put(i,A[i]);
	}
	message_string.append("</UL>\n");
    }
    
   
   
   

  

    
    


}