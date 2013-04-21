package jannovar.io;

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
import java.util.HashSet;
import java.util.Iterator;

import jannovar.exome.Variant;
import jannovar.exception.VCFParseException;

import jannovar.io.GenotypeFactoryA;
import jannovar.io.SingleGenotypeFactory;
import jannovar.io.MultipleGenotypeFactory;

/**
 * Parses a VCF file and extracts Variants for analysis
 * by the Jannovar. We read and count all of the lines that are
 * represented in the VCF file. 
 * <P>
 * We note that this parser demands that there be a FORMAT field and at least one sample id. 
 * Although this is not required in general for VCF files, any VCF file being used for 
 * exome analysis needs to have these fields. Here is the description from the VCF format
 * description at http://www.1000genomes.org:
 * <P>
 * If genotype information is present, then the same types of data must be present for all samples. 
 * First a FORMAT field is given specifying the data types and order. This is followed by one 
 * field per sample, with the colon-separated data in this field corresponding to the types 
 * specified in the format. The first sub-field must always be the genotype (GT).
 * <P>
 * The classes relies on the abstract factory pattern to create appropriate {@link jannovar.exome.GenotypeI GenotypeI}
 * objects depending on whether we have a single-sample or multiple-sample VCF file.
 * @author Peter Robinson 
 * @version 0.07 (16 April, 2013)
 */
public class VCFReader {
    static Logger log = Logger.getLogger(VCFReader.class.getName());
    /** Complete path of the VCF file being parsed */
    private String file_path=null;
    /** (Plain) basename of the VCF file being parsed. */
    private String base_filename = null;
    /** All of the lines in the original VCF header */
    private ArrayList<String> vcf_header=null;
    /** Set of all of the chromosomes that could not be parsed correctly, usually
	scaffolds such as chr11_random.*/
    private HashSet<String> badChrom=null;

    /** List of all single nucleotide polymorphisms from this VCF file */
    private ArrayList<Variant> variant_list=null;
    /** Short messages describing any errors encountered in parsing the VCF file, useful for HTML output. */ 
    private ArrayList<String> errorList=null;
   
    /** The total number of lines, including variants that are potentially pathogenic (missense, nonsense, splice, indel),
     as well as those that are probably not (intergenic, synonymous).*/
    private int total_number_of_variants;
    
    /** Factory object to create {@link jannovar.exome.GenotypeI GenotypeI} objects. Note that
     * this is an abstract class that will be instantiated depending on the information in the
     * #CHROM line of the VCF file (especially, whether there is a single sample or multiple
     * samples).
     */
    private GenotypeFactoryA genofactory=null;

    /** List of codes for FORMAT field in VCF */
    /** Genotype field  */
    public static final int FORMAT_GENOTYPE = 1;
    /* Genotype Quality field for FORMAT*/
    public static final int FORMAT_GENOTYPE_QUALITY = 2;
    /* Likelihoods for RR,RA,AA genotypes (R=ref,A=alt)  for FORMAT*/
    public static final int FORMAT_LIKELIHOODS = 3;
    /** # high-quality bases for FORMAT */
    public static final int FORMAT_HIGH_QUALITY_BASES = 4;
    /** Phred-scaled strand bias P-value  (FORMAT)*/
    public static final int FORMAT_PHRED_STRAND_BIAS = 5;
    /** List of Phred-scaled genotype likelihoods (FORMAT)*/
    public static final int FORMAT_PHRED_GENOTYPE_LIKELIHOODS = 6; 

    
    /** List of samples on this VCF file. Key: index, Value: name */
    private ArrayList<String> sample_name_list = null;
    /** List of lines that could not be successfully parsed. This list can be used for the HTML output.*/
    private ArrayList<String> unparsable_line_list=null;

     /** @return The total number of variants of any kind parsed from the VCF file*/
    public int get_total_number_of_variants() { return this.total_number_of_variants;}
    

    /**
     * @param path Path to a VCF file. 
     */
    public VCFReader(String path) {
	log.trace("Parsing VCF file: " + path);
	file_path = path;
	File file = new File(path);
	this.base_filename = file.getName();
	this.variant_list = new ArrayList<Variant>();
	this.vcf_header= new ArrayList<String>();
	this.unparsable_line_list = new ArrayList<String>();
	this.sample_name_list = new  ArrayList<String>();
	this.total_number_of_variants = 0;
	this.badChrom = new HashSet<String>();
	this.errorList = new ArrayList<String>();
	parse_file();
    }

    /**
     * In general, this class should extract one variant per variant line of the VCF file.
     * @return a list of variants extracted from the VCF file. 
     */
    public ArrayList<Variant> getVariantList() { return this.variant_list;} 

    /**
     * @return List of sample names
     */
    public ArrayList<String> getSampleNames(){ return this.sample_name_list; }

    /**
     * @return A list of VCF lines that could not be parsed correctly.
     */
    public ArrayList<String> getListOfUnparsableLines() { return this.unparsable_line_list; }
    
    /**
     * The parsing process stores a list with each of the header lines of the original VCF file.
     * @return List of lines of the original VCF file.
     */
    public ArrayList<String> get_vcf_header() { return vcf_header; }

    /**
     * @return list of any errors encountered during VCF parsing, or  null to indicate no error.
     */
    public ArrayList<String> get_html_message() { 
	ArrayList<String> msg = new ArrayList<String>();
	msg.add(String.format("VCF file: %s (number of variants: %d)",base_filename,this.total_number_of_variants));
	if (this.errorList.size() != 0) {
	    msg.add("Errors encountered while parsing VCF file:");
	    msg.addAll(this.errorList);
	}
	if (this.unparsable_line_list.size()!=0) {
	    msg.add("Could not parse the following lines:");
	    msg.addAll(this.unparsable_line_list);
	}
	return msg;
    }

    /**
     * This method parses the entire VCF file.
     * It places all header lines into the arraylist "header" and the remaining lines are parsed into
     * Variant objects.
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
		vcf_header.add(line);
	    }
	    String version = line.substring(16).trim();
	    log.trace("VCF Format: " + version);
	    
	    while ((line = br.readLine()) != null)   {
		if (line.isEmpty()) continue;
		if (line.startsWith("##")) {
		    vcf_header.add(line); 
		    continue; 
		} else if (line.startsWith("#CHROM")) {
		    try {
			parse_chrom_line(line); // Line with FORMAT and Sample names. 
			vcf_header.add(line); 
		    } catch (VCFParseException e) {
			String s = String.format("Error parsing #CHROM line: %s",e.toString());
			this.errorList.add(s);
		    }
		    /* Note that a side effect of the function parse_chrom_line
		       is to add sample names to sample_name_map. We can now instantiate the
		       genotype factory depending on whether there is more than one sample.
		    */
		    int n = this.sample_name_list.size();
		    if (n == 1) {
			this.genofactory = new SingleGenotypeFactory();
		    } else {
			this.genofactory = new MultipleGenotypeFactory();
		    } 
		    break; /* The #CHROM line is the last line of the header */ 
		}
	    }
	    VCFLine.setGenotypeFactory(genofactory);
	    while ((line = br.readLine()) != null)   {
		if (line.isEmpty()) continue;
		VCFLine ln = null;
		try {
		    ln = new VCFLine(line);
		} catch (VCFParseException e) {
		    this.unparsable_line_list.add(e + ": " + line);      
		    System.err.println("Warning: Skipping unparsable line: \n\t" + line);
		    System.err.println("Exception: "+e.toString());
		    continue;
		}
		this.total_number_of_variants++;
		
		try {
		    Variant var = ln.extractVariant();
		   
		    if (var.genotype_not_initialized()) {
			System.err.println("Error: Did not initialize genotype of SNV object");
			ln.dump_VCF_line_for_debug();
			System.out.println("Variant has genotype:" + var.get_genotype_as_string());
			System.exit(1);
		    }
		    var.setVCFline(line);
		    if (var.is_homozygous_ref())
			continue; /* SKIP Non-Variants, this does not make sense in WES setting
				     TODO: Move this into a separate filter */
		    variant_list.add(var);
		} catch (NumberFormatException e) {
		    //Exceptions may occur for variants such as 11_gl000202_random
		    // For now, just skip this. Refactor later
		    continue;
		} catch (VCFParseException ve) {
		    /** Record all chromosomes that could not be parsed,
			usually scaffolds such as 11_gl000202_random
		    */
		    String bad = ve.getBadChromosome();
		    this.badChrom.add(bad);
		    continue;
		}
	    } // while
	    in.close();
	} catch (IOException e) {
	    System.err.println(e);
	    System.exit(1);
	}
	if (this.badChrom.size()>0) {
	    recordBadChromosomeParses();
	}
    }
	

    /**
     * This function gets called when there was difficulty in 
     * parsing the chromosomes of some variants, e.g., GL000192.1.
     * We add a list of the chromosomes to messages, this will appear
     * in the HTML output.
     */
    private void  recordBadChromosomeParses()
    {
	Iterator<String> it = this.badChrom.iterator();
	StringBuilder sb = new StringBuilder();
	sb.append("<ul>\n");
	while (it.hasNext()) {
	    String s = it.next();
	    String t = String.format("Could not parse variant(s) mapped to chromosome: %s",s);
	    this.errorList.add(t);
	}
    }


    /**
     * The #CHROM line is the last line of the header of a VCF file, and it contains
     * seven required fields followed by one or more sample names.
     * <PRE>
     * #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	name1  name2 ...
     * </PRE>
     * We will use the number of sample names to determine which subclass of the abstract
     * factory {@link jannovar.io.GenotypeFactoryA GenotypeFactoryA} to instantiate.
     */
    public void parse_chrom_line(String line) throws VCFParseException
    {
	String A[] = line.split("\t");
	/* First check that obligatory format is correct */
	int n_errors = 0;

	this.errorList.add("VCF File: " + this.base_filename + ".");
	if (! A[0].equals("#CHROM") ) {
	    this.errorList.add("Error: Did not find #CHROM column in right place but instead:" + A[0] );
	    n_errors++;
	}
	if (! A[1].equals("POS") ) {
	    this.errorList.add("Error: Did not find POS column in right place but instead:" + A[1]);
	    n_errors++;
	}
	if (! A[2].equals("ID") ) {
	    this.errorList.add("<P>Error: Did not find ID column in right place but instead:" + A[2]);
	    n_errors++;
	}
	if (! A[3].equals("REF") ) {
	    this.errorList.add("<P>Error: Did not find REF column in right place but instead:" + A[3]);
	    n_errors++;
	}
	if (! A[4].equals("ALT") ) {
	    this.errorList.add("<P>Error: Did not find ALT column in right place but instead:" + A[4]);
	    n_errors++;
	}
	if (! A[5].equals("QUAL") ) {
	    this.errorList.add("<P>Error: Did not find QUAL column in right place but instead:" + A[5]);
	    n_errors++;
	}
	if (! A[6].equals("FILTER") ) {
	    this.errorList.add("<P>Error: Did not find FILTER column in right place but instead:" + A[6]);
	    n_errors++;
	}
	if (! A[7].equals("INFO") ) {
	    this.errorList.add("<P>Error: Did not find INFO column in right place but instead:" + A[7]);
	    n_errors++;
	}
	if (! A[8].equals("FORMAT") ) {
	    String s = String.format("Error: Did not find FORMAT in field 9 of the #CHROM line, but instead: \"%s\"",A[8]);
	    s = String.format("%s. There were a total of %d errors parsing this line: \"%s\"",s,n_errors+1,line);
	    throw new VCFParseException(s);
	}
	if (A.length<10) {
	    String s = String.format("Error: Did not find sufficient number fields int he #CHROM line" +
				     " (need to be at least 10, but found %d): %s",A.length,line);
	    throw new VCFParseException(s);
	}
	/* Note that if we get here, the sample names begin in field 9 */
	for (int i=9; i< A.length; ++i) {
	    sample_name_list.add(A[i]);
	}

    } 
}
/* eof */