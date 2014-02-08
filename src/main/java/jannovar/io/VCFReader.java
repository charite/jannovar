package jannovar.io;

import java.io.File;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.IOException; 
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import jannovar.exome.Variant;
import jannovar.exception.ChromosomeScaffoldException;
import jannovar.exception.JannovarException;
import jannovar.exception.VCFParseException;
import jannovar.genotype.GenotypeFactoryA;
import jannovar.genotype.SingleGenotypeFactory;
import jannovar.genotype.MultipleGenotypeFactory;

/**
 * Parses a VCF file and extracts Variants from each of the variant lines of
 * the VCF file. We read and count all of the lines that are
 * represented in the VCF file. 
 * <P>
 * There are two ways of using this class: (i) input the entire VCF file to
 * create an arraylist of Variants represent all of the variants in the VCF file. This is
 * convenient and should not cause problems with exome files but is noticably slower for genome
 * sequences; (ii) input first the VCF header and initialize an iterator that returns one
 * VCF Line at a time; this is flexible and has a small memory footprint.
 * <P>
 * Client code for option (i) should look something like the following:
 * <pre>
 * String vcfpath="/some/path/sample.vcf";
 * VCFReader parser = new VCFReader();
 * parser.parseFile(vcfpath);
 * ArrayList<Variant> varlist = parser.getVariantList();
 * </pre>
 * Note that if the VCF file has already been read into a Buffer (this is the case for
 * the versions of the Exomiser that work as HTTP servers), substitute the following commands
 * <pre>
 * BufferedReader VCFfileContents = getBufferedReaderFromSomewhere();
 * VCFReader parser = new VCFReader();
 * parser.parseStringStream(VCFfileContents);
 * ArrayList<Variant> varlist = parser.getVariantList();
 * <P>
 * Client code for option (ii) should look something like the following (note that
 * the function that initializes the iterator (<code>parser.iterator()</code>) will
 * ensure that the VCF header has ben input even if client code does not call
 * <code> parser.inputVCFheader()</code>, but it seems clearer to show it
 * explicitly.
 * <pre>
 * VCFReader parser = new VCFReader(this.VCFfilePath);
 * parser.inputVCFheader();
 * Iterator<Variant> it = parser.getVariantIterator();
 * while(it.hasNext()){
 *   Variant v = it.next();
 *   // do something with v
 * }
 * </pre>
 * Note that if you want to print out an annotated VCF file, you can use the 
 * following function to get a list of lines representing the VCF header with two
 * additional INFO lines explaining the new annotation fields (see {@link #infoEFFECT}
 * and {@link #infoHGVS}):
 * <pre>
 * ArrayList<String> lst = parser.getAnnotatedVCFHeader();
 * </pre>
 * Note that to write out annotated VCF lines (inclöuding the original line and the new
 * annotation), you need to use the VCFLineIterator:
 * <pre>
 * Iterator<VCFLine> iter = parser.getVCFLineIterator();
 * while(iter.hasNext()){
 *   VCFLine line = iter.next();
 *   Variant v = line.toVariant();
 *   // output as you like, see Jannovar.java for example.
 * }
 * </pre>
 * Both ways of using this class will result in identical lists of variants being parsed,
 * with the first delivering a complete list of Variants all at once with potential 
 * disadvantages in memory consuption for large VCF files (e.g., genome sequencing), and the
 * second delivering the Variants as an iterator, with good performance on any size VCF file.
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
 * @author Peter Robinson, Marten Jäger
 * @version 0.28 (26 January, 2014)
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
    private ArrayList<Variant> variantList=null;
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
    /** flag indicating whether we have already read the VCF Header. */
    private boolean vcfHeaderIsInitialized;
     /** @return The total number of variants of any kind parsed from the VCF file*/
    public int get_total_number_of_variants() { return this.total_number_of_variants;}

    /**
     * @return the total number of samples represented in this VCF file 
     */
    public int getNumberOfSamples() { return this.sample_name_list.size(); }
    /** @return the base file name of the VCF file being analyzed.  */
    public String getVCFFileName() { return this.base_filename; }
    /** The {@link Reader} for the {@link InputStream}. **/
    private BufferedReader in;
    /** A flag that indicates whether we are using a file handle (BufferedReader)
	from somewhere else (e.g., a tomcat server). */
    private final boolean useExternalBufferedReader;
    
    
    /**
     * The constructor initializes the file handle but does not
     * read anything. The constructor can be used if client code
     * wants to get all Variants at once or if it wants to get a 
     * Iterator or Variants.
     * @param vcfPath Complete path to the VCF file.
     * @throws jannovar.exception.VCFParseException
     */
    public VCFReader(String vcfPath) throws VCFParseException {
	this.file_path = vcfPath;
	File file = new File(this.file_path);
	this.base_filename = file.getName();
	try{
		if(file.getName().endsWith(".gz"))
			this.in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
		else
			this.in = new BufferedReader(new FileReader(this.file_path));
	} catch (IOException e) {
	    String err = String.format("[VCFReader]: %s",e.toString());
	    throw new VCFParseException(err);
	}
	this.useExternalBufferedReader=false;
	init();
    }

    /**
     * The constructor copies an external file handle but does not
     * read anything. It sets the variable
     * {@link #useExternalBufferedReader} to true, which will
     * cause the function {@link #parseFile} to use the 
     * external file handle rather than trying to
     * open a new file handle. 
     * The constructor can be used if client code
     * wants to get all Variants at once or if it wants to get a 
     * Iterator or Variants.
     * @param br the {@link Reader}
     */
    public VCFReader(BufferedReader br) {
	this.in = br;
	this.useExternalBufferedReader=true;
	init();
    }


    /**
     * Initialize all of the variables used for holding
     * the results of parsing.
     */
    private void init() {
	this.variantList = new ArrayList<Variant>();
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
	this.vcfHeaderIsInitialized = false;
    }
    


    /**
     * This class first ensures that the VCF header has been read, and that
     * there is a file pointer that points to the first variant
     * line of the VCF file. Once that has been taken care of, it
     * returns a iterator over variants.
     * @return {@link Iterator} of {@link Variant}s
     * @throws jannovar.exception.JannovarException 
     */
    public Iterator<Variant> getVariantIterator() throws JannovarException {
	if (this.in == null ) {
	    String s = "[VCFReader.java] Could not initialize the Variant Iterator";
	    throw new JannovarException(s);
	}
	if (! this.vcfHeaderIsInitialized) {
	    inputVCFheader();
	} 
	// When we get here, the #CHROM line (the last line of the header)
	// has just been read..
	return new VariantIterator(this.in);
    }

     /**
     * This class first ensures that the VCF header has been read, and that
     * there is a file pointer that points to the first variant
     * line of the VCF file. Once that has been taken care of, it
     * returns a iterator over variants.
     * @return {@link Iterator} of {@link Variant}s
     * @throws jannovar.exception.JannovarException
     */
    public Iterator<VCFLine> getVCFLineIterator() throws JannovarException {
	if (this.in == null ) {
	    String s = "[VCFReader.java] Could not initialize the VCFLine Iterator";
	    throw new JannovarException(s);
	}
	if (! this.vcfHeaderIsInitialized) {
	    inputVCFheader();
	} 
	// When we get here, the #CHROM line (the last line of the header)
	// has just been read..
	return new VCFLineIterator(this.in);
    }


    /**
     * This inner class implements an iterator over all of the
     * variants of the VCF file.
     */
    class VariantIterator implements Iterator<Variant> {
	/** The next line to be input */
	String nextline = null;
	/** The VCFLine object corresponding to {@link #nextline}*/
	VCFLine vcfline = null;
	/** file handle, referenced from the outer class. */
	private final BufferedReader in;
	/** The constructor advances the iterator to the first 
	    Variant line of the VCF file and thereby initializes
	    the variable {@link #vcfline}.*/
	VariantIterator(BufferedReader handle)   {
	    this.in = handle;
	    try {
		moveToNextVCFLine();
	    } catch (IOException e) {
		String s = String.format("[VCFIterator] IOException while initializing iterator: %s",e.getMessage());
		throw new RuntimeException(s); // cannot recover from this kind of error.
	    }
	}
	@Override public boolean hasNext() {
	    return (this.vcfline != null);
	}

	@Override public Variant next(){
	    Variant v = this.vcfline.toVariant();
	    try {
		moveToNextVCFLine();
	    } catch (IOException e) {
		String s = String.format("[VCFIterator] IOException: %s",e.getMessage());
		throw new RuntimeException(s);
	    }
	    return v;
	}

	@Override public void remove() {
	    throw new UnsupportedOperationException();
	}

	/**
	 * This function will keep trying to get the next VCFLine
	 * object until the file has no more lines. If there is some problem
	 * creating the VCFLine object, it will record the error
	 * message and go on to the next line. At the end, it will close
	 * the file handle.
	 */
	private void moveToNextVCFLine() throws IOException {
	    while ((this.nextline = this.in.readLine()) != null) {
		try {
		    this.vcfline = new VCFLine(nextline);
		} catch (VCFParseException e) {
		    VCFReader.this.unparsable_line_list.add(e + ": " + nextline);
		}
		if (this.vcfline != null)
		    return;
	    }
	    this.vcfline=null;
	    in.close();
	}
    }
    /* end of inner class VariantIterator */

    /** This class implements an iterator over
     * VCFLine objects.
     */
    class VCFLineIterator implements Iterator<VCFLine> {
	/** The next line to be input */
	String nextline = null;
	/** The VCFLine object corresponding to {@link #nextline}*/
	VCFLine vcfline = null;
	/** file handle, referenced from the outer class. */
	private final BufferedReader in;
	/** The constructor advances the iterator to the first 
	    Variant line of the VCF file and thereby initializes
	    the variable {@link #vcfline}.*/
	VCFLineIterator(BufferedReader handle)   {
	    this.in = handle;
	    try {
		moveToNextVCFLine();
	    } catch (IOException e) {
		String s = String.format("[VCFLineIterator] IOException while initializing iterator: %s",e.getMessage());
		throw new RuntimeException(s); // cannot recover from this kind of error.
	    }
	}
	@Override public boolean hasNext() {
	    return (this.vcfline != null);
	}

	@Override public VCFLine next(){
	    VCFLine previous = this.vcfline;
	    try {
		moveToNextVCFLine();
	    } catch (IOException e) {
		String s = String.format("[VCFIterator] IOException: %s",e.getMessage());
		throw new RuntimeException(s);
	    }
	    return previous;
	}

	@Override public void remove() {
	    throw new UnsupportedOperationException();
	}

	/**
	 * This function will keep trying to get the next VCFLine
	 * object until the file has no more lines. If there is some problem
	 * creating the VCFLine object, it will record the error
	 * message and go on to the next line. At the end, it will close
	 * the file handle.
	 */
	private void moveToNextVCFLine() throws IOException {
	    while ((this.nextline = this.in.readLine()) != null) {
		try {
		    this.vcfline = new VCFLine(nextline);
		    if (this.vcfline != null) {
			return;
		    }
		} catch (ChromosomeScaffoldException e) {
		    /* The variant was mapped to a non-standard chromosome scaffold. */
		    unparsableChromosomes.add(e.getMessage());
		    n_unparsable_chromosome_scaffold_variants++;
		} catch (VCFParseException e) {
		    VCFReader.this.unparsable_line_list.add(e + ": " + nextline);
		    System.err.println("Warning: Skipping unparsable line: \n\t" + nextline);
		    System.err.println("Exception: " + e.toString());
		}
	    }
	    // if we get here, we have finished reading the entire file.
	    // set the variable vcfline to null and close the handle.
	    this.vcfline=null;
	    in.close();
	}
    }
    /* end of inner class VCFLineIterator */

    /**
     * @return a list of {@link jannovar.exome.Variant Variant} objects extracted from the VCF file. 
     */
    public ArrayList<Variant> getVariantList() { 
	return this.variantList;
    } 

    /**
     * Transform a VCF variant line into a 
     * {@link jannovar.exome.Variant Variant} object.
     * @param line a line of a VCF file that represents a variant
     * @return the corresponding {@link jannovar.exome.Variant Variant} object.
     */
    public Variant VCFline2Variant(VCFLine line) {
	Variant v = line.toVariant();
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
	"startloss,duplication,frameshift-insertion,frameshift-deletion,non-frameshift-deletion,"+
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
	if (!this.errorList.isEmpty()) {
	    msg.add("Errors encountered while parsing VCF file:");
	    msg.addAll(this.errorList);
	}
	if (!this.unparsable_line_list.isEmpty()) {
	    msg.add("Could not parse the following lines:");
	    msg.addAll(this.unparsable_line_list);
	}
	return msg;
    }

   
    /**
     * This method parses the entire VCF file by creating a Stream from the
     * file path passed to it and calling the method {@link #inputVCFStream}.
     * If the constructor for external BufferedReaders was used, the function
     * will use the external BufferedReader filehandle.
     * @throws jannovar.exception.VCFParseException
     */
     public void parseFile() throws VCFParseException {
	 try{
	     if (! this.useExternalBufferedReader) {
		 this.in = new BufferedReader(new FileReader(this.file_path));
	     }
	     if (! this.vcfHeaderIsInitialized)
		 inputVCFheader();
	     inputVCFStream();
	     if (in != null)
		 in.close();
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
     */
    private void inputVCFStream() throws IOException, VCFParseException {
     	String line;
	VCFLine vcfline;
	
	while ((line = in.readLine())!= null) {
	    try {
		vcfline =  new VCFLine(line);
	    }  catch (ChromosomeScaffoldException cse) {
		this.unparsableChromosomes.add(cse.getMessage());
		this.n_unparsable_chromosome_scaffold_variants++;
		continue;
	    } catch (VCFParseException e) {
		/*
		 * Note: Do not propagate these exceptions further, but merely
		 * record what happened.
		 */
		this.unparsable_line_list.add(e + ": " + line);
		System.err.println("Warning: Skipping unparsable line: \n\t" + line);
		System.err.println("Exception: " + e.toString());
		continue;
	    }
	    Variant v = vcfline.toVariant();
	    this.variantList.add(v);
	    this.total_number_of_variants++;
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
	sb.append(n_unparsable_chromosome_scaffold_variants).append(" variants were identified from the following chromosome scaffolds: ");
	while (it.hasNext()) {
	    String s = it.next();
	    if (first) {
		sb.append(s);
		first=false;
	    } else {
		sb.append(", ").append(s);
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
     * @param line column name line of the VCF file
     * @throws jannovar.exception.VCFParseException
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
    
    /**
     * Processes the header of the VCF file. Checks the first line, 
     * determines the number of samples in the VCF file and sets the {@link GenotypeFactoryA}. 
     * If everything goes well, this function sets the variable
     * {@link #vcfHeaderIsInitialized} to true.
     * @throws VCFParseException
     */
    public void inputVCFheader() throws VCFParseException {
   	String line;
    	// The first line of a VCF file should include the VCF version number
    	// e.g., ##fileformat=VCFv4.0
	try {
	    line = in.readLine();
	    if (line == null) {
		String err =
		    String.format("Error: First line of VCF file (%s) was null",
				  this.file_path);
		throw new VCFParseException(err);
	    }
	    if (! line.startsWith("##fileformat=VCF")) {
		String err = "Error: First line of VCF file did not start with format:" + line;
		throw new VCFParseException(err);
	    } else {
		this.firstVCFLine = line;
	    }
	    String version = line.substring(16).trim();
	    while ((line = in.readLine()) != null)   {
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
		} else if (line.startsWith("#CHROM")) {
		    /* The CHROM line is the last line of the header and
		       includes  the FORMAT AND sample names. */
		    parse_chrom_line(line); 
		    vcf_header.add(line); 
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
	} catch (IOException e) {
	    String s = String.format("[VCFReader.java] Error while reading VCF header: %s",
				     e.getMessage());
	    throw new VCFParseException(s);
	}
	/* This tells VCFLine whether to expect single-sample or multiple-sample.*/
	VCFLine.setGenotypeFactory(genofactory);
	this.vcfHeaderIsInitialized=true;
    }
    
    /**
     * Print some diagnostic warning messages to STDERR. For instance,
     * print out the number of variants mapped to non-standard chromosome scaffolds
     * and other parse errors.
     */
    public void printWarnings() {
	if (this.n_unparsable_chromosome_scaffold_variants>0) {
	    StringBuilder sb = new StringBuilder();
	    boolean notfirst=false;
	    for (String s : this.unparsableChromosomes) {
		if (notfirst)
		    sb.append("; ");
		else
		    notfirst=true;
		sb.append(s);
	    }
	    System.err.println(String.format("[WARN] %d variants found (and skipped) on chromosome scaffolds: %s",
					     this.n_unparsable_chromosome_scaffold_variants, sb.toString()));
	}
	for (String s: this.errorList) {
	    System.err.println("[WARN] " + s);
	}
    }
    
}
/* eof */