package nsfp;

/** Command line functions from apache */
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;

/** Logging */
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.IOException; 

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;

import nsfp.io.*;
import nsfp.snv.*;

public class NSFPalizer {
    static Logger log = Logger.getLogger(NSFPalizer.class.getName());
    /** Path to log configuration file (default is in same directory) */
    private static String log_configuration_path=null;

    /** Path to directory with files from dbNFSP project, which should contain one file for each chromosome */
    private String nfsp_directory = null;
    /** Input file for annover format */
    static String annovar_file=null;
    /** A string representing a comma-separated list of variables that users can pass
     in from the command line or galaxy.*/
    static String flags =null;
    /** Should we exclude variants that do not pass the filter threshold? */
    static boolean use_filter=false;
    /** Input VCF file */
    static String vcf_file=null;
    /** Store lines of header of VCF file, in case we want to print them out again. */
    private ArrayList<String> header=null;
    /** File to which we will write results, name can be changed via command line. */
    private String outfile="nsfp.out";
    /** List of SNVs parsed from VCF or Annovar and that have passed user-indicated filter */
    private ArrayList<SNV> snv_list=null;
    /** List of NSFP hits for the SNVs. Each NSFP contains annotations for the SNVs. */
    private ArrayList<NSFP> nsfp_hits=null;
    /** Should the output be provided as an HTML table for Galaxy? */
    private boolean html_output=false;
    /** HTML summary of parsing the VCF file */
    private String vcf_HTML_message = null;
    private String filtering_HTML_message = null;
    private String inheritance_HTML_message = null;
   
    /** Maximum count in thousand genomes to allow a mutation
	to pass the filter. If set to zero, no filter is used. */
    int thousand_genomes_max_AC=0;

    private boolean use_pathogenicity_filter=false;
    private boolean use_autosomal_recessive_filter=false;
    private boolean use_autosomal_dominant_filter=false;
    private boolean use_x_chromosomal_filter=false;
    private boolean use_any_inheritance_filter=false;
    private boolean use_thousand_genomes_filter=false;
    private float thousand_genomes_filter=1.0f;
    private boolean use_quality_filter=false;
    private int quality_threshold = 0;

    public boolean use_recessive_filter()  { return this.use_autosomal_recessive_filter; }
    public boolean use_dominant_filter()  { return this.use_autosomal_dominant_filter; }
    public boolean use_X_filter()  { return this.use_x_chromosomal_filter; }
    public boolean use_quality_filter()  { return this.use_quality_filter; }
   
    

    /** This will be used to store messages and explanations for the HTML output */
    private StringBuilder message_paragraph = null;

    public boolean outputHTML() { return html_output; }


    public static void main(String argv[]) {
	FileAppender fa = new FileAppender();
	fa.setName("FileLogger");
	fa.setFile("nsfpalizer.log");
	fa.setLayout(new PatternLayout("[%t] [%d{HH:mm:ss.SSS}] %-5p %c %x - %m%n"));
	fa.setThreshold(Level.TRACE);
	fa.setAppend(false);
	fa.activateOptions();
	Logger.getRootLogger().addAppender(fa);

	log.trace("NSFPalizer: starting execution");
	long startTime = System.currentTimeMillis();
	log.trace("time in ms: " + startTime);
	
	NSFPalizer nfsp = new NSFPalizer(argv);
	if (vcf_file != null) {
	    nfsp.parseVCFFile(vcf_file);
	}  else {
	    System.out.println("Error: Need to indicate VCF file!");
	    return;
	}
	
	nfsp.compare_SNVs_with_NFSP_SQL();
	long endTime = System.currentTimeMillis();
	long duration = endTime - startTime;
	long seconds = duration / 1000;


	if (nfsp.use_recessive_filter()  || nfsp.use_dominant_filter() ||  nfsp.use_X_filter()) {
	    nfsp.filter_for_inheritance();
	} 

	log.trace("NSFPalizer: finishing  execution ms=" + endTime);
	log.trace("Time taken: " + seconds + " s");

	nfsp.outputHTMLforGalaxy();
	   
    }

    /** 
     * The constructor sets a few class variables based on command line arguments.
     * @param argv an array with the command line arguments
     */
    public NSFPalizer(String argv[]) {
	parseCommandLineArguments(argv);
	log.trace("Out file: " + outfile);
	if (outfile == null) {
	    System.err.println("Outfile not indicated, terminating program execution...");
	    System.exit(1);
	}
	message_paragraph = new StringBuilder();
	
	set_filter_options();

    }




   
    /**
     * Apply an AD, AR, or X inheritance filter (that has been set via command line)
     * This has the effect of removing NSFP hits from the ArrayList nsfp_hits that do
     * not conform to the expected inheritance pattern. 
     */
    public void  filter_for_inheritance() {
	HashMap<String,Gene> gene_map = new HashMap<String,Gene>();
	StringBuilder sb = new StringBuilder();
	Iterator<NSFP> it = this.nsfp_hits.iterator();
	while (it.hasNext()) {
	    NSFP nsfp = it.next();
	    String name = nsfp.get_name();
	    if (gene_map.containsKey(name)) {
		Gene g = gene_map.get(name);
		g.add_nsfp(nsfp);
	    } else {
		Gene g = new Gene(nsfp);
		gene_map.put(name, g);
	    }
	}
	/* When we get here, all NSFP objects have been assigned to Gene objects. 
	 Here, we remove all NSFP objects and then put objects back that conform
	to the inheritance pattern. */
	sb.append("<P>Before inheritance filtering: " + this.nsfp_hits.size() + " candidates.");
	if (this.use_autosomal_recessive_filter) {
	    sb.append(" Using an autosomal recessive filter (homozygous or compound heterozygous mutations in a gene), there were ");
	} else if (this.use_autosomal_dominant_filter) {
	    sb.append(" Using an autosomal dominant filter (>=1heterozygous mutations in a gene), there were ");
	} else if (this.use_x_chromosomal_filter) {
	    sb.append(" Using an X chromosomal filter (any mutations in a gene on X chromosome), there were ");
	}
	this.nsfp_hits.clear();
	int n_genes=0;
	for (Gene g: gene_map.values()) {
	    if (this.use_autosomal_recessive_filter) {
		if (g.is_consistent_with_recessive()) {
		    n_genes++;
		    ArrayList<NSFP> list = g.get_NFSP_list();
		    for (NSFP n : list) {
			nsfp_hits.add(n);
		    }
		}
	    } else if (this.use_autosomal_dominant_filter) {
		if (g.is_consistent_with_dominant()) {
		    n_genes++;
		    ArrayList<NSFP> list = g.get_NFSP_list();
		    for (NSFP n : list) {
			nsfp_hits.add(n);
		    }
		}
	    } else if (this.use_x_chromosomal_filter) {
		if (g.is_consistent_with_X()) {
		    n_genes++;
		    ArrayList<NSFP> list = g.get_NFSP_list();
		    for (NSFP n : list) {
			nsfp_hits.add(n);
		    }
		}
	    } else {
		message_paragraph.append("<P>Error in inheritance filter, did not find correct flag, please report! \n");
	    }
	}
	sb.append(nsfp_hits.size() + " candidate variants in " + n_genes + " genes</P>");
	this.inheritance_HTML_message = sb.toString();
    }

   

    /**
     * The user can pass a comma-separated list of options from the command
     * line that will determine the behaviour of this program. This method
     * parses them and sets various filter flags. */
    private void set_filter_options() {
	if (flags != null) {
	    message_paragraph.append("<P>Filtering methods used: \n");
	} else {
	    message_paragraph.append("<P>No filtering applied to the data</P>");
	    return;
	}
	String A[] = flags.split(",");
	
	message_paragraph.append("<UL>\n");
	for (String a : A) {
	    a = a.trim();
	    if (a.equals("path")) {
		use_pathogenicity_filter = true;
		message_paragraph.append("<LI>Pathogenicity: At least one of SIFT, Polyphen2 (HVAR), "+
					 "or Mutation Taster predicts damaging effect</LI>\n");
	    }
	    else if (a.equals("AR")) {
		use_autosomal_recessive_filter = true;
		message_paragraph.append("<LI>Autosomal recessive filter: Show variants in genes with homozygous or "+
					 "compound heterozygous candidate mutations</LI>");
	    } else if (a.equals("AD")) {
		use_autosomal_dominant_filter = true;
		message_paragraph.append("<LI>Autosomal dominant filter: Show variants in genes with heterozygous "+
					 "candidate mutations (Not intended to be used as de novo filter!)</LI>");
	    } else if (a.equals("X")) {
		use_x_chromosomal_filter = true;
		message_paragraph.append("<LI>X chromosomal filter: Show all candidates on the X chromosome</LI>");
	    } else if (a.startsWith("TG=")) {
		use_thousand_genomes_filter = true;
		try {
		    a = a.substring(3);
		    thousand_genomes_filter = Float.parseFloat(a);
		} catch (NumberFormatException e) {
		    thousand_genomes_filter = 1.0f; // Bad parse
		    message_paragraph.append("<LI>Warning: Could not parse thousand genomes string " + a);
		    message_paragraph.append("Please try again. For instance \"TG=0.05\" for a 5% filter</LI>");
		}
		message_paragraph.append("<LI>Thousand Genomes Filter: Only show variants with a frequency of less than ");
		message_paragraph.append(String.format("%.1f%%",100*thousand_genomes_filter));
		message_paragraph.append("</LI>");
	    } else if (a.startsWith("Q=")) {
		use_quality_filter=true;
		try {
		    a = a.substring(2);
		    quality_threshold = Integer.parseInt(a);
		} catch (NumberFormatException e) {
		    use_quality_filter=false;
		     message_paragraph.append("<LI>Warning: Could not parse Quality filter string " + a);
		     message_paragraph.append("Please try again. For instance \"Q=20\" for a minimum quality"+
					      " filter of genotype qulaity >=20.</LI>");
		}
		 message_paragraph.append("<LI>Genotype quality filter. Do not show variants with quality less than " + 
					  a + ".</L>");
	    }
		    
	}
	message_paragraph.append("</UL>\n");
	int n_inheritance_filters=0;
	if (use_autosomal_recessive_filter)
	    n_inheritance_filters++;
	if (use_autosomal_dominant_filter)
	    n_inheritance_filters++;
	if (use_x_chromosomal_filter)
	    n_inheritance_filters++;
	if (n_inheritance_filters>0) {
	    message_paragraph.append("<P>Warning: Multiple inheritance filters were set. Not a valid analysis!</P>");
	}
	if (use_pathogenicity_filter) {
	    message_paragraph.append("<P>Pathogenicity predictions are based on the dbNSFP-normalized values for ");
	    message_paragraph.append("<OL><LI>Mutation Taster: for now &gt;0.95 assumed pathogenic, prediction categories not shown</LI>"); 
	    message_paragraph.append("<LI>Polyphen2 (HVAR): \"D\" (&gt; 0.956,probably damaging), \"P\": [0.447-0.955], "+
				     "possibly damaging, and \"B\", &lt;0.447, benign.</LI>");
	    message_paragraph.append("<LI>SIFT: \"D\"&lt;0.05, damaging and \"T\"&gt;=0.05, tolerated</LI></OL>");
	}
    }

    /**
     * Input the VCF file using the VCFReader class. The method will initialize the snv_list,
     * which contains one item for each variant in the VCF file, as well as the header, 
     * which contains a list of the header lines of the VCF file that will be used for 
     * printing the output filtered VCF.
     * @param file path to a VCF file.
     */
    public void parseVCFFile(String file) {
	VCFReader parser = new VCFReader(file);
	this.snv_list = parser.get_snv_list();
	this.header = parser.get_vcf_header();
	this.vcf_HTML_message = parser.get_html_message();

    }

    

    /**
     * This function passes all of the SNVs representing
     * missense substitutions from the VCF file and passes
     * them to the SNV2SQL object, which will query the MySQL database.*/
    public void compare_SNVs_with_NFSP_SQL()
    {
	log.trace("Opening connection to SQL database");
	SNV2SQL s2s = new SNV2SQL();
	if (use_pathogenicity_filter) {
	    s2s.set_pathogenicity_filter();
	}
	if (use_thousand_genomes_filter) {
	    s2s.set_thousand_genomes_filter(this.thousand_genomes_filter);
	}
	if (use_quality_filter()) {
	    s2s.set_quality_threshold(this.quality_threshold);
	}

	s2s.getDataForListOfSNVs(this.snv_list);
	this.nsfp_hits = s2s.get_nsfp_hits();
	this.filtering_HTML_message = s2s.get_HTML_filtering_message();
    }



    public void outputLatexTable() {

	String fname = "mylatextable.tex";
	LatexTableWriter ltw = new LatexTableWriter(fname);
	ltw.writefile(this.nsfp_hits);
     
    }

    public void output_filtered_VCF() {
	String outVCF=null;
	if (outfile.endsWith(".txt"))
	    outVCF= this.outfile.replace(".txt",".vcf");
	else if (outfile.endsWith(".out"))
	    outVCF= this.outfile.replace(".out",".vcf");
	else if (outfile.endsWith(".tab"))
	    outVCF= this.outfile.replace(".tab",".vcf");
	else
	    outVCF = this.outfile + ".vcf";
	VCFWriter writer = new VCFWriter(outVCF);
	writer.set_header(header);
	writer.writefile(this.nsfp_hits);

    }
    


    public void output_NFPS_results() {
	System.out.println("Output results to file \"" + outfile + "\"");
	try{
 
	    FileWriter fstream = new FileWriter(this.outfile);
	    BufferedWriter out = new BufferedWriter(fstream);
	    if (this.nsfp_hits == null) {
		System.err.println("Error: NSFP Hits not initialized correctly!");
		return;
	    }
	    Iterator<NSFP> it = this.nsfp_hits.iterator();
	    while (it.hasNext()) {
		NSFP n = it.next();
		out.write(n.get_complete_nsfp() +"\n");
		out.write("*********************************************\n\n");
	    }
	    out.close();
	}catch (IOException e){
	    System.err.println("Error: " + e.getMessage());
	}
    }



    public void outputHTMLforGalaxy() {
	String fname = "nsfpalizer.html";
	if (this.outfile != null)
	    fname = this.outfile;
	//System.out.println("Writing " + fname);
	try{
 	    FileWriter fstream = new FileWriter(fname);
	    BufferedWriter out = new BufferedWriter(fstream);
	    if (this.nsfp_hits == null) {
		out.write("<P>Error: NSFP Hits not initialized correctly!</P>");
		out.close();
		return;
	    }
	    title_and_introductory_paragraph(out);
	    if (message_paragraph.length()>0)
		out.write( message_paragraph + "\n");
	    if (this.vcf_HTML_message != null) {
		out.write(vcf_HTML_message);
	    } 
	    if (this.filtering_HTML_message != null) {
		out.write(filtering_HTML_message + "\n");
	    }
	    if (this.inheritance_HTML_message != null) {
		out.write(inheritance_HTML_message + "\n");
	    }
	    NSFP2HTMLTable table = new NSFP2HTMLTable(NSFP.GENOMIC_VAR,NSFP.VARTYPE_IDX,NSFP.GENENAME,
						      NSFP.SIFT_WITH_PRED,NSFP.POLYPHEN_WITH_PRED,
						      NSFP.MUTATION_TASTER_SCORE,NSFP.GENOTYPE_QUALITY,
						      NSFP.THOUSAND_GENOMES_AF_AC);
	  
	    out.write(table.table_header());
	    Iterator<NSFP> it = this.nsfp_hits.iterator();
	    //System.out.println("About to output hits to HTML, size = " + this.nsfp_hits.size());
	    while (it.hasNext()) {
		NSFP n = it.next();
		//System.out.println("Got n = " + n);
		//if (n.passes_SIFT_threshold(this.SIFT_threshold)) {
		    out.write(table.table_row(n) + "\n");
		    //}
	    }
	    out.write(table.table_footer() + "\n");
	    out.close();
	}catch (IOException e){
	    System.err.println("Error: " + e.getMessage());
	}


    }
    
    public void title_and_introductory_paragraph(BufferedWriter out) throws IOException
    {
	out.write("<H1>The Exomizer: Annotate and Filter Variants</H1>\n");
	out.write("<P>This is a java jdbc program that uses data from <a href=\"http://www.ncbi.nlm.nih.gov/pubmed/21520341\">dbNSFP</a>");
	out.write(" and other sources and filters to visualize and annotate VCF data. Apply flags from the command line or ");
	out.write(" Galaxy to control behaviour: path (pathogenicity filter), AR, AR, or X for inheritance filters, ");
	out.write(" TG=0.05 (or similar) for Thousand Genomes frequency filter, ");
	out.write(" and Q=30 for minimum genotype quality of 30 (or similar).</P>");
	out.write("<P>Brought to you by AG Robinson, be sure to visit our <a href=\"http://compbio.charite.de\">homepage</a>.</P>");
	    


    }



 /**
     * Parse the command line.
     * 
     * @param args
     */
    private void parseCommandLineArguments(String[] args)
    {
	try
	{
	    Options options = new Options();
	    options.addOption(new Option("h","help",false,"Shows this help"));
	    options.addOption(new Option("t","html",false,"output as HTML table for Galaxy"));
	    options.addOption(new Option("m","mutation",true,"Path to Annovar file with mutations to be analyzed."));
	    options.addOption(new Option("v","vcf",true,"Path to VCF file with mutations to be analyzed."));
	    options.addOption(new Option("o","outfile",true,"name of out file (default: \"nsfp.out\")"));
	    options.addOption(new Option("f","flags",true,"pass various flags for filtering"));
	    options.addOption(new Option("l","log",true,"Configuration file for logger"));
	  
	   

	    Parser parser = new GnuParser();
	    CommandLine cmd = parser.parse(options,args);
	    if (cmd.hasOption("h"))
	    {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Exomizer (formerly NSFPalizer)", options);
		System.exit(0);
	    }
	    if (cmd.hasOption("t"))
		{
		    this.html_output=true;
		}

	    //this.nfsp_directory = getRequiredOptionValue(cmd,'n');
	     if (cmd.hasOption("m")) {
		 annovar_file = getRequiredOptionValue(cmd,'m');
	     }
	    if (cmd.hasOption("o")) {
		this.outfile = cmd.getOptionValue("o");  
	    }
	    if (cmd.hasOption("v")) {
		vcf_file = cmd.getOptionValue('v');  
	    }
	    if (cmd.hasOption("f")) {
		flags = cmd.getOptionValue('f');
	    }
	    if (cmd.hasOption("l")) {
		
		log_configuration_path=cmd.getOptionValue("l");
	    }
	} catch (ParseException pe)
	{
	    System.err.println("Error parsing command line options");
	    System.err.println(pe.getMessage());
	    System.exit(1);
	}
    }

  /**
     * The required argument stuff of the jakarta cli didn't work
     * as expected, so we have to do this manually. If the specified
     * argument is not found an appropriate error message is written
     * the program exited.
     *
     * @param cmd
     * @param name
     * @return
     */
    private static String getRequiredOptionValue(CommandLine cmd, char name)
    {
	String val = cmd.getOptionValue(name);
	if (val == null)
	    {
		System.err.println("Aborting because the required argument \"-" 
		+ name + "\" wasn't specified! Use the -h for more help.");
		System.exit(-1);
	    }
	return val;
    }


}