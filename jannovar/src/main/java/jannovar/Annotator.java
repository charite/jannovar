package jannovar;

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

import java.util.ArrayList;
import java.util.HashMap;

/* serialization */
import java.io.ObjectInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import jannovar.io.UCSCKGParser;
import jannovar.io.AnnovarParser;
import jannovar.reference.KnownGene;
import jannovar.reference.Chromosome;
import jannovar.reference.Annotation;
import jannovar.exome.Variant;
import jannovar.exception.AnnotationException;
import jannovar.exception.KGParseException;
import jannovar.io.VCFReader;

/**
 * This is the driver class for a program called Annotator. It has two purposes
 * <OL>
 * <LI>Take the UCSC files knownGene.txt, kgXref.txt, knownGeneMrna.txt, and knownToLocusLink.txt,
 * and to create corresponding {@link jannovar.reference.KnownGene KnownGene} objects and to 
 * serialize them. The resulting serialized file can be used both by this program itself (see next item)
 * or by the main Exomizer program to annotated VCF file.
 * <LI>Using the serialized file of {@link jannovar.reference.KnownGene KnownGene} objects  (see above item)
 * annotate a VCF file using annovar-type program logic. Note that this functionality is also
 * used by the main Exomizer program and thus this program can be used as a stand-alone annotator ("Jannovar")
 * or as a way of testing the code for the Jannovar.
 * </OL>
 * <P>
 * To run the "Jannovar" exectuable:
 * <P>
 * {@code java -Xms1G -Xmx1G -jar Annotator.jar -V xyz.vcf -D $SERIAL}
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
 * @author Peter N Robinson
 * @version 0.07 (December 18, 2012)
 */
public class Annotator {
    /** A logger from Apache's log4j that records key statistics from program execution. */
    static Logger log = Logger.getLogger(Annotator.class.getName());
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
    /** Path to annovar input file with variants to be tested. */
    private String annovarFilePath=null;
    /** List of all lines from knownGene.txt file from UCSC */
    private ArrayList<KnownGene> knownGenesList=null;
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

    public static void main(String argv[]) {
	log.setLevel(Level.TRACE);
	FileAppender fa = new FileAppender();
	fa.setName("FileLogger");
	fa.setFile("annotator.log");
	fa.setLayout(new PatternLayout("[%t] [%d{HH:mm:ss.SSS}] %-5p %c %x - %m%n"));
	fa.setThreshold(Level.TRACE);
	fa.setAppend(false);
	fa.activateOptions();
	Logger.getRootLogger().addAppender(fa);
	log.trace("Starting executation of Annotator");

	
	Annotator anno = new Annotator(argv);

	if (anno.serialize()) {
	    anno.readUCSCKnownGenesFile();	
	    anno.serializeUCSCdata();
	    return;
	} else if (anno.deserialize()) {
	    anno.deserializeUCSCdata();
	} else {
	    System.err.println("Error: Need to first run program to serialize UCSC data");
	    System.err.println("Then, run analysis using serialized data");
	    System.exit(1);
	}
	/* When we get here, the program has deserialized data and put it into the
	   Chromosome objects. We can now start to annotate variants. */
	if (anno.hasVCFfile()) {
	    anno.annotateVCF();
	   
	} else if (anno.hasAnnovarfile()) {
	    anno.inputAnnovarFile();
	    anno.annotateVariants();
	}
    }

    /** The constructor parses the command-line arguments. */
    public Annotator(String argv[]){
	parseCommandLineArguments(argv);
    }



    /**
     * Gets a list of {@link jannovar.exome.Variant} objects from 
     * a file in Annovar format. 
     * @see jannovar.io.AnnovarParser AnnovarParser
     */
    public void inputAnnovarFile() {
	AnnovarParser parser = new AnnovarParser(this.annovarFilePath);
	this.variantList = parser.getVariantList();
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
     * @return true if we should annotate a VCF file
     */
    public boolean hasAnnovarfile() {
	return this.annovarFilePath != null;
    }

    /**
     * Main method for performing annovar-type analysis.
     * This method decides what chromosome the variant is 
     * located on and passes the remaining data to the
     * corresponding {@link jannovar.reference.Chromosome Chromosome}
     * object for further analysis.
     */
    public void annotateVariants() {
	Chromosome c = null;
	int i=0;
	for (Variant v : variantList) {
	    //System.out.println(v);
	    byte chr =  v.getChromosomeAsByte();
	    int pos = v.get_position();
	    String ref = v.get_ref();
	    String alt = v.get_alt();
	    c = chromosomeMap.get(chr); // TODO change chromosome in Variant to byte?
	    if (c==null) {
		System.err.println("[Annotator.java:anotateVariants()] Could not identify chromosome \"" + chr + "\"");
		debugShowChromosomeMap();		
	    } else {
		//System.out.println("***********   Annotation List  ******************");
		try {
		    Annotation anno = c.getAnnotation(pos,ref,alt);
		    if (anno==null) {
			System.out.println("No annotations foud for variant " + v);
			continue;
		    }
		    i++;
		    //System.out.println(i + ")\t \"" + v.getChromosomalVariant() + "\"");
		    System.out.print("Line " + i + ": \t\"" + anno.getVariantAnnotation() +
				       "\" (Type:" + anno.getVariantTypeAsString() + ")");
		    System.out.println("\t" + v.getVCFline());
		   
		} catch (AnnotationException ae) {
		    System.err.println("§§§§§§§§§§§§§§§ Annotation Exception §§§§§§§§§§§§§§§§§§§§§§§§§");
		    System.err.println("Variant: \"" + v.getChromosomalVariant() + "\"");
		    ae.printStackTrace();
		    System.err.println("§§§§§§§§§§§§§§§ ------------------- §§§§§§§§§§§§§§§§§§§§§§§§§");
		    System.exit(1);
		} catch (Exception e) {
		    System.err.println("Really bad exception ");
		    System.err.println(v);
		    e.printStackTrace();
		    System.exit(1);
		}
		
	    }
	}
    }

    public void annotateVCF() {
	VCFReader parser = new VCFReader(this.VCFfilePath);
	this.variantList = parser.getVariantList();
	annotateVariants();
    }




     /**
     * Inputs the list of known genes from the UCSC file called knownGene.txt, and uses it
     * to write a serialized version of the data.
     */
    public void serializeUCSCdata() {
	UCSCKGParser parser = new UCSCKGParser(this.ucscPath);
	try{
	    parser.parseFile();
	    parser.readFASTAsequences(this.ucscKGMrnaPath);
	    parser.readKGxRefFile(this.ucscXrefPath);
	    parser.readKnown2Locus(this.ucscKnown2LocusPath);
	    parser.serializeKnownGeneMap(this.UCSCserializationFileName);
	} catch (KGParseException e) {
	    System.out.println("Failed to serialize UCSC Data");
	    System.out.println(e);
	    System.exit(1);
	}
	
    }

     /**
     * Inputs the list of known genes from the serialized data file. The serialized file 
     * was originally  created by parsing the three UCSC known gene files.
     */
    @SuppressWarnings (value="unchecked")
    public void deserializeUCSCdata() {
	HashMap<String,KnownGene> kgMap=null;
	try {
	     FileInputStream fileIn =
		 new FileInputStream(this.serializedFile);
	     ObjectInputStream in = new ObjectInputStream(fileIn);
	     kgMap = (HashMap<String,KnownGene>) in.readObject();
            in.close();
            fileIn.close();
	}catch(IOException i) {
            i.printStackTrace();
	    System.err.println("Could not deserialize knownGeneMap");
	    System.exit(1);
           
        }catch(ClassNotFoundException c) {
            System.out.println("Could not find HashMap<String,KnownGene> class.");
            c.printStackTrace();
            System.exit(1);
        }
	//System.out.println("Done deserialization, size of map is " + kgMap.size());
	this.chromosomeMap = new HashMap<Byte,Chromosome> ();
	// System.out.println("Number of KGs is " + kgMap.size());
	for (KnownGene kgl : kgMap.values()) {
	    byte chrom = kgl.getChromosome();
	    //System.out.println("Chromosome is " + chrom);
	    if (! chromosomeMap.containsKey(chrom)) {
		Chromosome chr = new Chromosome(chrom);
		//System.out.println("Adding chromosome for " + chrom);
		chromosomeMap.put(chrom,chr);
	    }
	    Chromosome c = chromosomeMap.get(chrom);
	    c.addGene(kgl);	
	}
    }


    /**
     * Input the list of known genes from the UCSC file called knownGene.txt, and uses it
     * to construct Chromosome objects. These objects represent the Chromosomes and the
     * genes they contain.
     */
    public void readUCSCKnownGenesFile()
    {
	UCSCKGParser parser = new UCSCKGParser(this.ucscPath);
	try {
	    parser.parseFile();
	    parser.readFASTAsequences(this.ucscKGMrnaPath);
	    parser.readKGxRefFile(this.ucscXrefPath);
	} catch (KGParseException e) {
	    System.out.println(e);
	    System.exit(1);
	}
	HashMap<String,KnownGene> kgMap = parser.getKnownGeneMap();
	this.chromosomeMap = new HashMap<Byte,Chromosome> ();
	System.out.println("Adding KGs to Chromosomes");
	System.out.println("Number of KGs is " + kgMap.size());
	for (KnownGene kgl : kgMap.values()) {
	    byte chrom = kgl.getChromosome();
	    //System.out.println("Chromosome is " + chrom);
	    if (! chromosomeMap.containsKey(chrom)) {
		Chromosome chr = new Chromosome(chrom);
		//System.out.println("Adding chromosome for " + chrom);
		chromosomeMap.put(chrom,chr);
	    }
	    Chromosome c = chromosomeMap.get(chrom);
	    c.addGene(kgl);	
	}
	
    }
    
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

	    Parser parser = new GnuParser();
	    CommandLine cmd = parser.parse(options,args);
	    if (cmd.hasOption("h"))
	    {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("Annotator", options);
		System.exit(0);
	    }
	   
	    
	    
	    if (cmd.hasOption("S")) {
		this.ucscPath = getRequiredOptionValue(cmd,'U');
		this.ucscXrefPath = getRequiredOptionValue(cmd,'X');
		this.ucscKGMrnaPath = getRequiredOptionValue(cmd,'M');
		this.UCSCserializationFileName=cmd.getOptionValue('S');
		this.ucscKnown2LocusPath=cmd.getOptionValue('L');
		return;
	    }
	    this.serializedFile = getRequiredOptionValue(cmd, 'D');
	    if (cmd.hasOption("A"))
		this.annovarFilePath =  cmd.getOptionValue("A");
	    else if (cmd.hasOption("V"))
		this.VCFfilePath =  cmd.getOptionValue("V");
	    else {
		System.err.println("Error: Need to pass either annovar file or VCF file!");
		System.exit(1);
	    }
	   
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



}
