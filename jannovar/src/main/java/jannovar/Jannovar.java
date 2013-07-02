package jannovar;

/** Command line functions from apache */
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.Writer;


import java.util.ArrayList;
import java.util.HashMap;

import jannovar.annotation.Annotation;
import jannovar.annotation.AnnotationList;
import jannovar.exception.AnnotationException;
import jannovar.exception.IntervalTreeException;
import jannovar.exception.JannovarException;
import jannovar.exception.KGParseException;
import jannovar.exception.VCFParseException;
import jannovar.exome.Variant;
import jannovar.interval.Interval;
import jannovar.interval.IntervalTree;
import jannovar.io.SerializationManager;
import jannovar.io.UCSCDownloader;
import jannovar.io.UCSCKGParser;
import jannovar.io.VCFLine;
import jannovar.io.VCFReader;
import jannovar.reference.Chromosome;
import jannovar.reference.TranscriptModel;


/**
 * This is the driver class for a program called Jannovar. It has two purposes
 * <OL>
 * <LI>Take the UCSC files knownGene.txt, kgXref.txt, knownGeneMrna.txt, and knownToLocusLink.txt,
 * and to create corresponding {@link jannovar.reference.TranscriptModel TranscriptModel} objects and to 
 * serialize them. The resulting serialized file can be used both by this program itself (see next item)
 * or by the main Exomizer program to annotated VCF file.
 * <LI>Using the serialized file of {@link jannovar.reference.TranscriptModel TranscriptModel} objects  (see above item)
 * annotate a VCF file using annovar-type program logic. Note that this functionality is also
 * used by the main Exomizer program and thus this program can be used as a stand-alone annotator ("Jannovar")
 * or as a way of testing the code for the Exomizer.
 * </OL>
 * <P>
 * To run the "Jannotator" exectuable:
 * <P>
 * {@code java -Xms1G -Xmx1G -jar Jannotator.jar -V xyz.vcf -D $SERIAL}
 * <P>
 * This will annotate a VCF file. The results of jannovar annotation are shown in the form
 * <PRE>
 * Annotation {original VCF line}
 * </PRE>
 * <P>
 * Just a reminder, to set up annovar to do this, use the following commands.
 * <PRE>
 *   perl annotate_variation.pl --downdb knownGene --buildver hg19 humandb/
 * </PRE>
 * then, to annotate a VCF file called BLA.vcf, we first need to convert it to Annovar input
 * format and run the main annovar program as follows.
 * <PRE>
 * $ perl convert2annovar.pl BLA.vcf -format vcf4 > BLA.av
 * $ perl annotate_variation.pl -buildver hg19 --geneanno BLA.av --dbtype knowngene humandb/
 * </PRE>
 * This will create two files with all variants and a special file with exonic variants.
 * <p>
 * There are three ways of using this program.
 * <ol>
 * <li>To create a serialized version of the UCSC gene definition data. In this case, the command-line
 * flag <b>- S</b> is provide as is the path to the four UCSC files. Then, {@code anno.serialize()} is true
 * and a file <b>ucsc.ser</b> is created.
 * <li>To deserialize the serialized data (<b>ucsc.ser</b>). In this case, the flag <b>- D</b> must be used.
 * <li>To simply read in the UCSC data without creating a serialized file.
 * </ol>
 * Whichever of the three versions is chosen, the user may additionally pass the path to a VCF file using the <b>-v</b> flag.
 * If so, then this file will be annotated using the UCSC data, and a new version of the file will be written to a file called
 * test.vcf.jannovar (assuming the original file was named test.vcf).
 * The
 * @author Peter N Robinson
 * @version 0.22 (2 July, 2013)
 */
public class Jannovar {
    /** Path to the UCSC file knownGene.txt */
    private String ucscPath = null;
    /** Path to the UCSC file kgXref.txt */
    private String ucscXrefPath=null;
    /** Path to the UCSC file knownGeneMrna.txt */
    private String ucscKGMrnaPath=null;
    /** Path to UCSC file knownToLocusLink.txt file. This file has cross refs between the 
	ucsc knownGene ids and Entrez gene ids (the previous name of Entrez gene was 
	locus link). */
    private String ucscKnown2LocusPath=null;
    /**
     * Flag to indicate that Jannovar should download known gene definitions files from the
     * UCSC server.
     */
    private boolean downloadUCSC;
    /**
     * Location of directory into which Jannovar will download the UCSC files (default: "ucsc").
     */
    private String downloadDirectory="ucsc/";
   
    /** List of all lines from knownGene.txt file from UCSC */
    private ArrayList<TranscriptModel> knownGenesList=null;
    /** Map of Chromosomes */
    private HashMap<Byte,Chromosome> chromosomeMap=null;
    /** List of variants from input file to be analysed. */
    private ArrayList<Variant> variantList=null;
    /** Flag indicating whether a serialization of the ICSC data should be written to file. */
    private String UCSCserializationFileName=null;
    /** Name of file with serialized UCSC data. */
    private String serializedFile=null;
    /** Path to a VCF file waiting to be annotated. */
    private String VCFfilePath=null;
    /**
     * Flag indicating whether to output annotations in Jannovar format (default: false).
     */
    private boolean jannovarFormat;

    public static void main(String argv[]) {
	
	Jannovar anno = new Jannovar(argv);

	if (anno.downloadUCSC()) {
	    anno.downloadUCSCfiles();
	    return;
	}
	
	if (anno.serialize()) {
	    try{
		anno.readUCSCKnownGenesFile();	
	    } catch (IntervalTreeException e) {
		System.out.println("Could not construct interval tree: " + e.toString());
		System.exit(1);
	    } 
	    try {
		anno.serializeUCSCdata();
	    } catch (JannovarException je) {
		System.out.println("Could not serialize UCSC data: " + je.toString());
		System.exit(1);
	    }
	    return;
	} else if (anno.deserialize()) {
	    try {
		anno.deserializeUCSCdata();
	    } catch (JannovarException je) {
		System.out.println("Could not deserialize UCSC data: " + je.toString());
		System.exit(1);
	    }
	} else if (anno.ucscFilesAvailable()) {
	     try{
		anno.readUCSCKnownGenesFile();	
	    } catch (IntervalTreeException e) {
		System.out.println("Could not construct interval tree: " + e.toString());
		System.exit(1);
	    }
	} else {
	    usage();
	    System.exit(1);
	}
	/* When we get here, the program has deserialized data and put it into the
	   Chromosome objects. We can now start to annotate variants. */
	if (anno.hasVCFfile()) {
	    anno.annotateVCF(); 
	} else {
	    System.out.println("No VCF file found");
	}
    }

    /** The constructor parses the command-line arguments. */
    public Jannovar(String argv[]){
	parseCommandLineArguments(argv);
    }


    /**
     * @return true if user wants to download UCSC files
     */
    public boolean downloadUCSC() {
	return this.downloadUCSC;
    }

    /**
     * This function creates a
     * {@link jannovar.io.UCSCDownloader UCSCDownloader} object in order to
     * download the four required UCSC files.
     */
    public void downloadUCSCfiles() {
	UCSCDownloader downloader = new UCSCDownloader(this.downloadDirectory);
	downloader.downloadUCSCfiles();


    }


    /**
     * @return true if we should serialize the UCSC data. */
    public boolean serialize() {
	return this.UCSCserializationFileName != null;
    }
    /**
     * @return true if we should deserialize a file with UCSC data to perform analysis
     */
    public boolean deserialize() {
	return this.serializedFile != null;
    }

    /**
     * @return true if we should annotate a VCF file
     */
    public boolean hasVCFfile() {
	return this.VCFfilePath != null;
    }

    /**
     * This function can be used to know whether we have all the UCSC files
     * we need to either serialize the 
     * {@link jannovar.reference.TranscriptModel TranscriptModel} objects
     * or to proceed directly to the analysis without serialization.
     * @return true if the user has given paths for the UCSC files.
     */
    public boolean ucscFilesAvailable() {
	if ( this.ucscPath !=null &&
	     this.ucscXrefPath != null &&
	     this.ucscKGMrnaPath != null &&
	     this.ucscKnown2LocusPath != null)
	    return true;
	else
	    return false;
    }
   

  

    /**
     * Annotate a single line of a VCF file, and output the line together with the new
     * INFO fields representing the annotations.
     * @param line an object representing the original VCF line 
     * @param v the Variant object that was parsed from the line
     * @param out A file handle to write to.
     */
    private void annotateVCFLine(VCFLine line, Variant v, Writer out) throws IOException,AnnotationException
    {
	byte chr =  v.getChromosomeAsByte();
	int pos = v.get_position();
	String ref = v.get_ref();
	String alt = v.get_alt();
	Chromosome c = chromosomeMap.get(chr);
	if (c==null) {
	    String e = String.format("[Jannovar] Could not identify chromosome \"%d\"", chr );
	    throw new AnnotationException(e);	
	} 
	AnnotationList anno = c.getAnnotationList(pos,ref,alt);
	if (anno==null) {
	    String e = String.format("[Jannovar] No annotations found for variant %s", v.toString());
	    throw new AnnotationException(e);	
	}
	String annotation = anno.getSingleTranscriptAnnotation();
	String effect = anno.getVariantType().toString();
	String A[] = line.getOriginalVCFLine().split("\t");
	for (int i=0;i<7;++i)
	    out.write(A[i] + "\t");
	/* Now add the stuff to the INFO line */
	String INFO = String.format("EFFECT=%s;HGVS=%s;%s",effect,annotation,A[7]);
	out.write(INFO);
	for (int i=8;i<A.length;++i)
	     out.write(A[i] + "\t");
	out.write("\n");
    }

    /**
     * This function outputs a single line in Jannovar format.
     * @param n The current number (one for each variant in the VCF file)
     * @param v The current variant with one or more annotations
     * @param out File handle to write Jannovar file.
     */
    private void outputJannovarLine(int n,Variant v, Writer out) throws IOException,AnnotationException
    {
	byte chr =  v.getChromosomeAsByte();
	String chrStr = v.get_chromosome_as_string();
	int pos = v.get_position();
	String ref = v.get_ref();
	String alt = v.get_alt();
	String gtype = v.getGenotypeAsString();
	float qual = v.get_variant_quality();
	Chromosome c = chromosomeMap.get(chr);
	if (c==null) {
	    String e = String.format("[Jannovar] Could not identify chromosome \"%d\"", chr );
	    throw new AnnotationException(e);	
	} 
	AnnotationList anno = c.getAnnotationList(pos,ref,alt);
	if (anno==null) {
	    String e = String.format("[Jannovar] No annotations found for variant %s", v.toString());
	    throw new AnnotationException(e);	
	}
	
	String effect = anno.getVariantType().toString();
	ArrayList<Annotation> lst = anno.getAnnotationList();
	for (Annotation a : lst) {
	    String annt = a.getVariantAnnotation();
	    String sym = a.getGeneSymbol();
	    String s = String.format("%d\t%s\t%s\t%s\t%s\t%d\t%s\t%s\t%s\t%.1f",
				     n,effect,sym,annt,chrStr,pos,ref,alt,gtype,qual);
	    out.write(s + "\n");
	}
    }


    private void outputAnnotatedVCF(VCFReader parser) 
    {
	this.variantList = parser.getVariantList();
	ArrayList<VCFLine> lineList = parser.getVCFLineList();
	File f = new File(this.VCFfilePath);
	String outname = f.getName() + ".jannovar";
	try {
	    FileWriter fstream = new FileWriter(outname);
	    BufferedWriter out = new BufferedWriter(fstream);
	    /** Write the header of the new VCF file */
	    ArrayList<String> lst = parser.getAnnotatedVCFHeader();
	    for (String s: lst) {
		out.write(s + "\n");
	    }
	    /** Now write each of the variants. */
	    for (VCFLine  line : lineList) {
		Variant v = parser.VCFline2Variant(line);
		try{
		    annotateVCFLine(line,v,out);
		} catch (AnnotationException e) {
		    System.out.println("[Jannovar] Warning: Annotation error: " + e.toString());
		}
	    }
	    out.close();
	}catch (IOException e){
	    System.out.println("[Jannovar] Error writing annotated VCF file");
	    System.out.println("[Jannovar] " + e.toString());
	    System.exit(1);
	}
    }


    private void outputJannovarFormatFile(VCFReader parser) 
    {
	this.variantList = parser.getVariantList();
	File f = new File(this.VCFfilePath);
	String outname = f.getName() + ".jannovar";
		try {
	    FileWriter fstream = new FileWriter(outname);
	    BufferedWriter out = new BufferedWriter(fstream);
	    /**  Output each of the variants. */
	    int n=0;
	    for (Variant v : variantList) {
		n++;
		try{
		    outputJannovarLine(n,v,out);
		} catch (AnnotationException e) {
		    System.out.println("[Jannovar] Warning: Annotation error: " + e.toString());
		}
	    }
	    out.close();
	}catch (IOException e){
	    System.out.println("[Jannovar] Error writing annotated VCF file");
	    System.out.println("[Jannovar] " + e.toString());
	    System.exit(1);
	}
    }



    /**
     * This function inputs a VCF file, and prints the annotated version thereof
     * to a file (name of the original file with the suffix .jannovar).
     */
    public void annotateVCF() {
	VCFReader parser = new VCFReader();
	VCFLine.setStoreVCFLines();
	try{
	    parser.parseFile(this.VCFfilePath);
	} catch (VCFParseException e) {
	    System.err.println("Unable to parse VCF file");
	    System.err.println(e.toString());
	    System.exit(1);
	}
	if (this.jannovarFormat) {
	    outputJannovarFormatFile(parser);
	} else {
	    outputAnnotatedVCF(parser);
	}

	
    }




     /**
     * Inputs the KnownGenes data from UCSC files, convert the
     * resulting {@link jannovar.reference.TranscriptModel TranscriptModel}
     * objects to {@link jannovar.interval.Interval Interval} objects, and
     * store these in a serialized file.
     */
    public void serializeUCSCdata() throws JannovarException {
	ArrayList<TranscriptModel> kgList = inputUCSCDataFromFile();
	SerializationManager manager = new SerializationManager();
	manager.serializeKnownGeneList(this.UCSCserializationFileName, kgList);
    }


     public void deserializeUCSCdata() throws JannovarException {
	ArrayList<TranscriptModel> kgList=null;
	SerializationManager manager = new SerializationManager();
	kgList = manager.deserializeKnownGeneList(this.serializedFile);
	this.chromosomeMap = Chromosome.constructChromosomeMapWithIntervalTree(kgList);
     }  





    /**
     * Input the four UCSC files for the KnownGene data
     * @return a list of all TranscriptModel objects from the KnownGene data.
     */
    private ArrayList<TranscriptModel> inputUCSCDataFromFile() {
	UCSCKGParser parser = new UCSCKGParser(this.ucscPath,this.ucscXrefPath,
					       this.ucscKGMrnaPath,this.ucscKnown2LocusPath);
	try{
	    parser.parseUCSCFiles();
	} catch (Exception e) {
	    System.out.println("Unable to input data from the UCSC files");
	    e.printStackTrace();
	    System.exit(1);
	}
	return parser.getKnownGeneList();
    }
    

    /**
     * This menction uses {@link jannovar.io.UCSCKGParser UCSCKGParser}
     * to parse the four UCSC KnownGenes files that are needed to create
     *  {@link jannovar.reference.TranscriptModel TranscriptModel} objects
     * for all genes in the transcriptome.
     *
     */
    public void readUCSCKnownGenesFile() throws IntervalTreeException {
	ArrayList<TranscriptModel> kgList = inputUCSCDataFromFile();
	this.chromosomeMap = Chromosome.constructChromosomeMapWithIntervalTree(kgList);
    }
    
    /**
     * A simple printout of the chromosome map for debugging purposes.
     */
    public void debugShowChromosomeMap() {
	for (Byte c: chromosomeMap.keySet()) {
	    Chromosome chromo = chromosomeMap.get(c);
	    System.out.println("Chrom. " + c + ": " + chromo.getNumberOfGenes()  + " genes");
	}	
    }


    
    /**
     * Parse the command line. The important options are -n: path to the directory with the NSFP files,
     * and -C a flag indicating that we want the program to delete the current table in the postgres
     * database and to create an empty table (using JDBC connection).
     * @param args Copy of the command line arguments.
     */
    private void parseCommandLineArguments(String[] args)
    {
	try
	{
	    Options options = new Options();
	    options.addOption(new Option("h","help",false,"Shows this help"));
	    options.addOption(new Option("U","nfsp",true,"Path to UCSC knownGene file. Required"));
	    options.addOption(new Option("X","xref",true,"Path to UCSC kgXref file. Required"));
	    options.addOption(new Option("M","mrna",true,"Path to UCSC knownGenes mrna file. Required"));
	    options.addOption(new Option("S","serialize",true,"Serialize"));
	    options.addOption(new Option("A","nfsp",true,"Path to Annovar input file. Required"));
	    options.addOption(new Option("D","deserialize",true,"Path to serialized file with UCSC data"));
	    options.addOption(new Option("V","vcf",true,"Path to VCF file"));
	    options.addOption(new Option("L","locus",true,"Path to ucsc file KnownToLocusLink.txt"));
	    options.addOption(new Option("J","janno",false,"Output Jannovar format"));
	    options.addOption(new  Option(null,"download-ucsc",false,"Download UCSC KnownGene data"));

	    Parser parser = new GnuParser();
	    CommandLine cmd = parser.parse(options,args);
	    if (cmd.hasOption("h") || args.length==0)
	    {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar Jannovar.jar [-options]", options);
		usage();
		System.exit(0);
	    }
	   
	    if (cmd.hasOption("J")) {
		this.jannovarFormat = true; 
	    } else {
		this.jannovarFormat = false;
	    }

	    if (cmd.hasOption("download-ucsc")) {
		this.downloadUCSC = true;
	    } else {
		this.downloadUCSC = false;
	    }
	    
	    
	    if (cmd.hasOption("S")) {
		this.ucscPath = getRequiredOptionValue(cmd,'U');
		this.ucscXrefPath = getRequiredOptionValue(cmd,'X');
		this.ucscKGMrnaPath = getRequiredOptionValue(cmd,'M');
		this.UCSCserializationFileName=cmd.getOptionValue('S');
		this.ucscKnown2LocusPath=cmd.getOptionValue('L');
	    }

	    if (cmd.hasOption('D')) {
		this.serializedFile = cmd.getOptionValue('D');
	    }

	    if (cmd.hasOption("V"))
		this.VCFfilePath =  cmd.getOptionValue("V");
	
	   
	} catch (ParseException pe)
	{
	    System.err.println("Error parsing command line options");
	    System.err.println(pe.getMessage());
	    System.exit(1);
	}
    }

    /**
     * This function is used to ensure that certain options are passed to the 
     * program before we start execution.
     *
     * @param cmd An apache CommandLine object that stores the command line arguments
     * @param name Name of the argument that must be present
     * @return Value of the required option as a String.
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


    private static void usage() {
	System.out.println("***   Jannovar: Usage     ****");
	System.out.println("Jannovar can be used to serialize UCSC KnownGenes data:");
	System.out.println("** $ java -jar Jannovar.jar -S f -U p1 -X p2 -M p3 -L p4");
	System.out.println("** Here,f is the desired file name of the serialized file, and p1-p4 are paths to the UCSC files");
	System.out.println("Jannovar can be used to annotate VCF files with the serialized UCSC file (or the original UCSC files):");
	System.out.println("** $ java -jar Jannovar -D ucsc.ser -V vcfPath");
	System.out.println("** Here, ucsc.ser is the name of the serialized UCSC data, and vcfPath is the path to the VCF exome file");


    }


}
