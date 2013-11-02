package jannovar.io;

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
import jannovar.exception.ChromosomeScaffoldException;
import jannovar.exception.VCFParseException;
import jannovar.genotype.GenotypeFactoryA;
import jannovar.genotype.SingleGenotypeFactory;
import jannovar.genotype.MultipleGenotypeFactory;

/**
 * Parses a VCF file and extracts Variants from each of the variant lines of
 * the VCF file. We read and count all of the lines that are
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
 * The classes relies on the abstract factory pattern to create appropriate
 * {@link jannovar.genotype.GenotypeCall GenotypeCall}
 * objects depending on whether we have a single-sample or multiple-sample VCF file. Note that
 * these objects contain data on variant quality (GQ) and read depth (DP).
 * @author Peter Robinson 
 * @version 0.21 (1 November, 2013)
 */
public class VCFReader {
    /** Complete path of the VCF file being parsed */
    private String file_path=null;
    /** (Plain) basename of the VCF file being parsed. */
    private String base_filename = null;
    /** Very first line of VCF file, must be of the form {@code ##fileformat=VCFv4.1}. */
    private String firstVCFLine=null;
    /** All of the lines in the original VCF header (excluding those that have been
     * stored in {@link #formatLines}, {@link #infoLines} or {@link #contigLines}*/
    private ArrayList<String> vcf_header=null;
    /** All of the FORMAT lines fromt he VCF header */
    private ArrayList<String> formatLines=null;
    /** All of the INFO lines from the VCF header */
    private ArrayList<String> infoLines=null;
    /** All of the contig lines from the VCF header, e.g., 
	{@code ##contig=<ID=chr22,length=51304566,assembly=hg19>}. */
    private ArrayList<String> contigLines=null;
   
    /** List of all variants parsed from this VCF file */
    private ArrayList<VCFLine> variant_list=null;
    /** Short messages describing any errors encountered in parsing the VCF file,
	useful for output messages. */ 
    private ArrayList<String> errorList=null;
    /** Set of all of the chromosomes that could not be parsed correctly, usually
	scaffolds such as chr11_random.*/
    private HashSet<String> unparsableChromosomes=null;
    /** The total number of variants located in chromosome scaffolds other than the
	canonical 1-22, X,Y,M. */
    private int n_unparsable_chromosome_scaffold_variants;
    
   
    /** The total number of lines with variants.*/
    private int total_number_of_variants;
    
    /** Factory object to create {@link jannovar.genotype.GenotypeCall GenotypeCall} objects. Note that
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

    
    /** List of samples on this VCF file. */
    private ArrayList<String> sample_name_list = null;
    /** List of lines that could not be successfully parsed.
    * This list can be used for user output.
    */
    private ArrayList<String> unparsable_line_list=null;

     /** @return The total number of variants of any kind parsed from the VCF file*/
    public int get_total_number_of_variants() { return this.total_number_of_variants;}

    /**
     * @return the total number of samples represented in this VCF file 
     */
    public int getNumberOfSamples() { return this.sample_name_list.size(); }

    public String getVCFFileName() { return this.base_filename; }
    

    /**
     * The constructor initializes various ArrayLists etc. After calling the constructors,
     * users can call parse_file(String path) to 
     */
    public VCFReader() {
	this.variant_list = new ArrayList<VCFLine>();
	this.vcf_header= new ArrayList<String>();
	this.formatLines=new ArrayList<String>();
	this.infoLines=new ArrayList<String>();
	this.contigLines=new ArrayList<String>();
	this.unparsable_line_list = new ArrayList<String>();
	this.sample_name_list = new  ArrayList<String>();
	this.total_number_of_variants = 0;
	this.unparsableChromosomes = new HashSet<String>();
	this.errorList = new ArrayList<String>();
	this.n_unparsable_chromosome_scaffold_variants=0;
    }

    /**
     * This function is intended to be used to retrieve the original
     * variant lines of the VCF file.
     * <p>
     * The VCFReader first parses the VCF file variant lines into
     * {@link jannovar.io.VCFLine VCFLine} objects. These objects
     * store the String from the original VCF line, and thus it is
     * possible to output both the original VCF line as well as the
     * data that was parsed from it. Assuming that everything works
     * correctly, however, client code should be using the 
     * {@link jannovar.exome.Variant Variant} class for the individual
     * variants. This class has a number of functions for examining and
     * sorting the variants. It does not sotre the actual String from the
     * original VCF line.
     * @return A list of {@link jannovar.io.VCFLine VCFLine} objects representing 
     * all lines of the VCF file.
     */
    public ArrayList<VCFLine> getVCFLineList() { return this.variant_list;} 


    /**
     * @return a list of {@link jannovar.exome.Variant Variant} objects extracted from the VCF file. 
     */
    public ArrayList<Variant> getVariantList() { 
	ArrayList<Variant> vars = new ArrayList<Variant>();
	for (VCFLine line : this.variant_list) {
	    Variant v = new Variant(line.get_chromosome(),
				    line.get_position(),
				    line.get_reference_sequence(),
				    line.get_alternate_sequence(),
				    line.getGenotype());
	    vars.add(v);
	}
	return vars;
    } 

    /**
     * Transform a VCF variant line into a 
     * {@link jannovar.exome.Variant Variant} object.
     * @param line a line of a VCF file that represents a variant
     * @return the corresponding {@link jannovar.exome.Variant Variant} object.
     */
    public Variant VCFline2Variant(VCFLine line) {
	Variant v = new Variant(line.get_chromosome(),
				    line.get_position(),
				    line.get_reference_sequence(),
				    line.get_alternate_sequence(),
				    line.getGenotype());
	return v;
    }


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
    public ArrayList<String> get_vcf_header() { 
	ArrayList<String> lst = new ArrayList<String>();
	lst.add(this.firstVCFLine);
	lst.addAll(this.formatLines);
	lst.addAll(this.infoLines);
	lst.addAll(this.contigLines);
	lst.addAll(this.vcf_header); 
	return lst;
    }

   

    /**
     * This line is added to the output of a VCF file annotated by Jannovar and describes the new field
     * for the INFO section entitled EFFECT, which decribes the effects of variants 
     * (splicing,missense,stoploss, etc).
     */
    private static final String infoEFFECT="##INFO=<ID=EFFECT,Number=1,Type=String,Description=\""+
	"variant effect (UTR5,UTR3,intronic,splicing,missense,stoploss,stopgain,"+
	"frameshift-insertion,frameshift-deletion,non-frameshift-deletion,"+
	"non-frameshift-insertion,synonymous)\">";

    /**
     * This line is added to the output of a VCF file annotated by Jannovar and describes the new field
     * for the INFO section entitled HGVS, which provides the HGVS encoded variant corresponding to the
     * chromosomal variant in the original VCF file.
     */
    private static final String infoHGVS="##INFO=<ID=HGVS,Number=1,Type=String,Description=\"HGVS Nomenclature\">";

    /**
     * The parsing process stores a list with each of the header lines of the original VCF file.
     * This function returns those lines but adds two additional lines to provide information
     * about the annotations added to the output VCF file.
     * @return List of lines of the original VCF file.
     */
    public ArrayList<String> getAnnotatedVCFHeader() { 
	ArrayList<String> lst = new ArrayList<String>();
	lst.add(this.firstVCFLine);
	lst.addAll(this.formatLines);
	lst.addAll(this.infoLines);
	lst.add(infoEFFECT);
	lst.add(infoHGVS);
	lst.addAll(this.contigLines);
	lst.addAll(this.vcf_header); 
	return lst;
    }

    /**
     * This function returns status messages representing the results of parsing.
     * The messages can represent errors, if any occured. There is always at least one
     * message indicating the total number of variants encountered during parsing. The
     * messages are intended to be use for HTML output or logs etc.
     * @return list of any errors encountered during VCF parsing, or  null to indicate no error.
     */
    public ArrayList<String> get_html_message() { 
	ArrayList<String> msg = new ArrayList<String>();
	if (this.base_filename != null)
	    msg.add(String.format("VCF file: %s (number of variants: %d)",base_filename,this.total_number_of_variants));
	else
	     msg.add(String.format("Number of variants in VCF file: %d",this.total_number_of_variants));
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
     * Parse a VCF file that has been put into a BufferedReader by
     * client code (one possible use: a tomcat server). The result is the same
     * as if we passed the file path to the method {@link #parseFile} but
     * is useful in cases where we have a BufferedReader but not a file on disk.
     */
    public void parseStringStream(BufferedReader VCFfileContents) throws VCFParseException {
	try{
	    inputVCFStream(VCFfileContents);
	} catch (IOException e) {
	    String err =
	      String.format("[VCFReader:parseStringStream]: %s",e.toString());
	    throw new VCFParseException(err);
	 }
    }


    /**
     * This method parses the entire VCF file by creating a Stream from the
     * file path passed to it and calling the method {@link #inputVCFStream}.
     * @param VCFfilePath complete path to a VCF file.
     */
     public void parseFile(String VCFfilePath) throws VCFParseException {
	 this.file_path = VCFfilePath;
	 File file = new File(this.file_path);
	 this.base_filename = file.getName();
	 try{
	     FileInputStream fstream = new FileInputStream(this.file_path);
	     DataInputStream in = new DataInputStream(fstream);
	     BufferedReader br = new BufferedReader(new InputStreamReader(in));
	     inputVCFStream(br);
	 } catch (IOException e) {
	    String err = String.format("[VCFReader:parseFile]: %s",e.toString());
	    throw new VCFParseException(err);
	 }
     }

    /**
     * Parse the entire VCF file. It places all header lines into the arraylist 
     * {@link #vcf_header} and the remaining lines are parsed into
     * Variant objects.  This class could be improved by storing various
     * data elements/explanations explicitly.
     * @param br An open handle to a VCF file.
     */
    private void inputVCFStream(BufferedReader br)
	throws IOException, VCFParseException
    {	
	String line;
	int linecount=0;
	int snvcount=0;
	// The first line of a VCF file should include the VCF version number
	// e.g., ##fileformat=VCFv4.0
	line = br.readLine();
	if (line == null) {
	    String err =
		String.format("Error: First line of VCF file (%s) was null",
				this.file_path);
	    throw new VCFParseException(err);
	}
	if (!line.startsWith("##fileformat=VCF")) {
	    String err = "Error: First line of VCF file did not start with format:" + line;
	    throw new VCFParseException(err);
	} else {
	    this.firstVCFLine = line;
	}
	String version = line.substring(16).trim();
		
	while ((line = br.readLine()) != null)   {
	    if (line.isEmpty()) continue;
	    if (line.startsWith("##")) {
		if (line.startsWith("##FORMAT")) {
		    this.formatLines.add(line);
		} else if (line.startsWith("##INFO")) {
		    this.infoLines.add(line);
		} else if (line.startsWith("##contig") || line.startsWith("##CONTIG")) {
		    this.contigLines.add(line);
		} else {
		    vcf_header.add(line); 
		}
		continue; 
	    } else if (line.startsWith("#CHROM")) {
		/* The CHROM line is the last line of the header and
		  includes  the FORMAT AND sample names. */
		try {
		    parse_chrom_line(line); 
		    vcf_header.add(line); 
		} catch (VCFParseException e) {
		    String s = String.format("Error parsing #CHROM line: %s",e.toString());
		    throw new VCFParseException(s);
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
	/* This tells VCFLine whether to expect single-sample or multiple-sample.*/
	VCFLine.setGenotypeFactory(genofactory);
	/* Here is where we begin to parse the variant lines! */
	while ((line = br.readLine()) != null)   {
	    if (line.isEmpty()) continue;
	    VCFLine ln = null;
	    try {
		ln = new VCFLine(line);
	    } catch (ChromosomeScaffoldException cse) {
		this.unparsableChromosomes.add(cse.getMessage());
		this.n_unparsable_chromosome_scaffold_variants++;
		continue;
	    } catch (VCFParseException e) {
		/* Note: Do not propagate these exceptions further, but
		  * merely record what happened. */ 
		this.unparsable_line_list.add(e + ": " + line);      
		System.err.println("Warning: Skipping unparsable line: \n\t" + line);
		System.err.println("Exception: "+e.toString());
		continue;
	    }
	    this.total_number_of_variants++;    
	    variant_list.add(ln);
	} // while
	if (this.unparsableChromosomes.size()>0) {
	    recordBadChromosomeParses();
	}
    }
	

    /**
     * This function gets called when there was difficulty in 
     * parsing the chromosomes of some variants, e.g., GL000192.1.
     * We add a list of the chromosomes to messages, this can be used
     * to produce error messages for user output.
     */
    private void recordBadChromosomeParses()
    {
	if (n_unparsable_chromosome_scaffold_variants == 0) 
	    return;
	Iterator<String> it = this.unparsableChromosomes.iterator();
	boolean first=true;
	StringBuffer sb = new StringBuffer();
	sb.append(n_unparsable_chromosome_scaffold_variants + " variants were identified from the following chromosome scaffolds: ");
	while (it.hasNext()) {
	    String s = it.next();
	    if (first) {
		sb.append(s);
		first=false;
	    } else {
		sb.append(", " + s);
	    }
	}
	this.errorList.add(sb.toString());
    }


    /**
     * The #CHROM line is the last line of the header of a VCF file, and it contains
     * seven required fields followed by one or more sample names.
     * <PRE>
     * #CHROM	POS	ID	REF	ALT	QUAL	FILTER	INFO	FORMAT	name1  name2 ...
     * </PRE>
     * We will use the number of sample names to determine which subclass of the abstract
     * factory {@link jannovar.genotype.GenotypeFactoryA GenotypeFactoryA} to instantiate.
     */
    public void parse_chrom_line(String line) throws VCFParseException
    {
	String A[] = line.split("\t");
	/* First check that obligatory format is correct */
	if (! A[0].equals("#CHROM") ) {
	    throw new VCFParseException("[parse_chrom_line]: Malformed #CHROM field in #CHROM line: " + line);
	}
	if (! A[1].equals("POS") ) {
	    throw new VCFParseException("[parse_chrom_line]: Malformed POS field in #CHROM line:" + line);
	}
	if (! A[2].equals("ID") ) {
	    throw new VCFParseException("[parse_chrom_line]: Malformed ID field in #CHROM line:" + line);
	}
	if (! A[3].equals("REF") ) {
	    throw new VCFParseException("[parse_chrom_line]: Malformed REF field in #CHROM line:" + line);
	}
	if (! A[4].equals("ALT") ) {
	    throw new VCFParseException("[parse_chrom_line]: Malformed ALT field in #CHROM line:" + line);
	}
	if (! A[5].equals("QUAL") ) {
	    throw new VCFParseException("[parse_chrom_line]: Malformed QUAL field in #CHROM line:" + line);
	}
	if (! A[6].equals("FILTER") ) {
	    throw new VCFParseException("[parse_chrom_line]: Malformed FILTER field in #CHROM line:" + line);
	}
	if (! A[7].equals("INFO") ) {
	    throw new VCFParseException("[parse_chrom_line]: Malformed INFO field in #CHROM line:" + line);
	}
	if (! A[8].equals("FORMAT") ) {
	    String s = String.format("[parse_chrom_line]: Malformed FORMAT field in #CHROM line: %s",line);
	    throw new VCFParseException(s);
	}
	if (A.length<10) {
	    String s = String.format("Error: Did not find sufficient number fields in the #CHROM line" +
				     " (need to be at least 10, but found %d): %s",A.length,line);
	    throw new VCFParseException(s);
	}
	/* Note that if we get here, the sample names must begin in field 9 */
	for (int i=9; i< A.length; ++i) {
	    sample_name_list.add(A[i]);
	}

    } 
}
/* eof */