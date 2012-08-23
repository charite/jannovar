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
import java.io.InputStreamReader;
import java.io.IOException; 

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.Iterator;

import nsfp.io.*;


/**
 * This program is intended to parse the NSFP program files and to
 * create a sump file that can be imported into the database
 * It has been tested with postgreSQL, but probably should work
 * with very minor changes for MySQL.
 * To set up the user and database in postgres
 * postgres=# create user nsfp with password 'vcfanalysis';
 * postgres=# create database nsfpalizer;
 * To import the dumps into the database
 * psql -h localhost nsfpalizer -U postgres -W (rpw)
 * copy gene from '/home/peter/SVN/apps/NGSanalysis/NSFPalizer/gene-dump.pg' WITH DELIMITER '|';
 * copy variant from '/home/peter/SVN/apps/NGSanalysis/NSFPalizer/variant-dump.pg' WITH DELIMITER '|';
 * @author peter.robinson@charite.de
 */
public class NSFP2SQL {
    static Logger log = Logger.getLogger(NSFP2SQL.class.getName());
    /** Path to directory with files from dbNFSP project, 
	which should contain one file for each chromosome */
    private String nsfp_dir = null;
    /** path to each individual dbNFSP file, one for each chromosome. */
    private ArrayList<String> chromosome_file_paths=null;

    /** Handle to mysql database */
    private Connection connect = null;
    private Statement statement = null;
    

    public static void main(String argv[]) {
	log.setLevel(Level.TRACE);
	FileAppender fa = new FileAppender();
	fa.setName("FileLogger");
	fa.setFile("nsfp2sql.log");
	fa.setLayout(new PatternLayout("[%t] [%d{HH:mm:ss.SSS}] %-5p %c %x - %m%n"));
	fa.setThreshold(Level.TRACE);
	fa.setAppend(false);
	fa.activateOptions();
	Logger.getRootLogger().addAppender(fa);
	log.trace("Starting executation of NSFP2SQL");
	System.out.println("NSFP 2 SQL");
	
	NSFP2SQL n2q = new NSFP2SQL(argv);
	n2q.openDatabaseConnection();
	n2q.createTables();
	n2q.parseChromosomes();
    }
    
    public NSFP2SQL(String argv[]){
	parseCommandLineArguments(argv);
    }


    /**
     * Create the two main tables of the nsfpalizer SQL database
     * Note that this program will not add data to the databases
     * because it is very much faster to import the data into the
     * database as a "dump", using postgresql COPY command. Therefore,
     * the function is the only one in the program that uses the
     * jbdc interface to do something with the database.
     */
    public void createTables() {
	Statement statement = null;
	String drop = "DROP TABLE IF EXISTS gene";
	log.trace("Dropping previous version of gene");
	try {
	    statement = connect.createStatement();
	    statement.execute(drop);
	}catch (SQLException e) {
            System.err.println("problems dropping table gene");
	    System.err.println(e);
	    System.exit(1);
        }
	    
	String create = "CREATE TABLE gene ("+
	    "gene_id SERIAL,"+
            "genename VARCHAR(64),"+
            "uniprot_acc VARCHAR(64),"+
            "uniprot_id VARCHAR(64),"+
	    "cds_strand CHAR,"+
	    "ensembl_geneid VARCHAR(255),"+
	    "ensembl_transcript_id VARCHAR(255),"+
	    "PRIMARY KEY(gene_id));";
	log.trace(create);
	try {
	    statement = connect.createStatement();
	    statement.execute(create);
	}catch (SQLException e) {
            System.err.println("problems creating gene table");
	    System.err.println(e);
	    System.exit(1);
        }
	
	String drop2 = "DROP TABLE IF EXISTS variant";
	log.trace("Dropping previous version of variant");
	try {
	    statement = connect.createStatement();
	    statement.execute(drop2);
	}catch (SQLException e) {
            System.err.println("problems dropping variant table");
	    System.err.println(e);
	    System.exit(1);
        }
	
	String create2 = "CREATE TABLE variant ("+
	    "chromosome SMALLINT,"+
	    "position   INT,"+
	    "ref        CHAR(1),"+
	    "alt        CHAR(1),"+
	    "aaref      CHAR(1),"+
	    "aaalt      CHAR(1),"+
	    "uniprot_aapos INT,"+
	    "aapos          INT,"+
	    "sift          FLOAT,"+
	    "polyphen      FLOAT,"+
	    "mut_taster    FLOAT,"+
	    "phyloP       FLOAT," +
      	    "ThGenomes_AC  SMALLINT," +
	    "ThGenomes_AF     FLOAT," +
	    "gene_id       INT, " +  /* The foreign key */
	    "PRIMARY KEY(position,chromosome,ref,alt))";
    	try {
	    statement = connect.createStatement();
	    statement.execute(create2);
	}catch (SQLException e) {
            System.err.println("problems creating variant table");
	    System.err.println(e);
	    System.exit(1);
        }
	log.trace(create2);
	


    }


    /**
     * Use NSFP2SQLDumpParser to parse all of the chromosome files.
     * This will create a dump file that can be imported into 
     * postgresql (see README for instructions).
     */
    public void parseChromosomes() {
	NSFP2SQLDumpParser parser = new NSFP2SQLDumpParser();
	get_list_of_chromosome_files();
	Iterator<String> it = chromosome_file_paths.iterator();
	while (it.hasNext()) {
	    String fname = it.next();
	    parser.input_chromosome(fname);
	}
	parser.cleanup_filehandles();
    }


    /**
     * Connect to mysql database and store connection in handle
     * this.connect. 
     For postgres
     postgres-# createuser -P nsfp
     postgres-# createdb -O nsfp nsfpalizer
    */
    public void openDatabaseConnection() {
	String URL = "jdbc:postgresql://localhost/nsfpalizer";
        String username = "nsfp";
        String password = "vcfanalysis";
        try {
            this.connect = DriverManager.getConnection (
                URL,
                username,
                password);
        } catch (SQLException e) {
            System.err.println("problems connecting to "+URL);
	    System.err.println(e);
	    System.exit(1);
        }
    }



     /**
     * Get a list of all dbNSFP Annotation files from the
     * annotation_directory. A String with the path of each
     * valid chromosome file is put into the array  list
     * chromosome_file_paths.
     */
    public void get_list_of_chromosome_files()
    {
	if (nsfp_dir != null)
	    System.out.println("Getting list of dbNSFP files from directory: " + nsfp_dir);
	else {
	    System.out.println("Error: dbNFSP path was not initialized");
	    System.err.println("Error: dbNFSP path was not initialized");
	    System.exit(1);
	}
	chromosome_file_paths = new ArrayList<String>();
	
	File folder = new File(nsfp_dir);
	if (folder == null || ! folder.isDirectory()) {
	    System.err.println("Error: Could not initialize folder at path " + nsfp_dir);
	    System.exit(1);
	}
	File[] listOfFiles = folder.listFiles();
	System.out.println("There were " + listOfFiles.length + " files in all in the dbNSFP folder");
	this.chromosome_file_paths= new ArrayList<String>();
	
	for (int i = 0; i < listOfFiles.length; i++) {
	    if (listOfFiles[i].isFile()) {
		String f = listOfFiles[i].getName();
		int ind = f.indexOf("chr"); // Just extract chromosome files
		if (ind < 0) continue;
		String path = nsfp_dir + "/" + f;
		this.chromosome_file_paths.add(path);
	    }	
	}
	if (chromosome_file_paths.size() != 24) {
	    System.out.println("Did not get all 24 chromosome files fo NSFP");
	    System.out.println("Got a total of " + chromosome_file_paths.size() + 
			       " chromosome files.");
	    System.exit(1);
	}
	
    }



    /**
     * Parse the command line.
     * @param args
     */
    private void parseCommandLineArguments(String[] args)
    {
	try
	{
	    Options options = new Options();
	    options.addOption(new Option("h","help",false,"Shows this help"));
	    options.addOption(new Option("n","nfsp",true,"Directory containing NFSP annotation files. Required"));
	   
	    Parser parser = new GnuParser();
	    CommandLine cmd = parser.parse(options,args);
	    if (cmd.hasOption("h"))
	    {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("NSFPalizer", options);
		System.exit(0);
	    }

	    this.nsfp_dir = getRequiredOptionValue(cmd,'n');
	   
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