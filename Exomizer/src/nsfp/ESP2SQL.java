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


/** Logging */
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.FileAppender;
import org.apache.log4j.PatternLayout;


/** Command line functions from apache */
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;


/**
 * This class transforms the ESP files into an postgreSQL dump
 * @author peter.robinson@charite.de
 * @date 19.08.2012
 */

public class ESP2SQL  {
    static Logger log = Logger.getLogger(ESP2SQL.class.getName());
    /** Directory with all the ESP files :*/
    private String esp_dir=null;
    private ArrayList<String> esp_file_paths = null;
    
     /** Handle to mysql database */
    private Connection connection = null;
    private Statement statement = null;


     public static void main(String argv[]) {
	log.setLevel(Level.TRACE);
	FileAppender fa = new FileAppender();
	fa.setName("FileLogger");
	fa.setFile("esp2sql.log");
	fa.setLayout(new PatternLayout("[%t] [%d{HH:mm:ss.SSS}] %-5p %c %x - %m%n"));
	fa.setThreshold(Level.TRACE);
	fa.setAppend(false);
	fa.activateOptions();
	Logger.getRootLogger().addAppender(fa);
	log.trace("Starting executation of ESP2SQL");
	System.out.println("ESP 2 SQL");
	
	ESP2SQL e2q = new ESP2SQL(argv);
	e2q.openDatabaseConnection();
	e2q.createTables();
	e2q.parseESPFiles();
	
    }

     public ESP2SQL(String argv[]){
	parseCommandLineArguments(argv);
	
    }


    /**
     * Connect to postgreSQL database and store connection in handle
     * this.connection. Prepare the query statements.*/
    public void openDatabaseConnection() {
	String URL = "jdbc:postgresql://localhost/nsfpalizer";
        String username = "nsfp";
        String password = "vcfanalysis";
        
        try {
            this.connection = 
		DriverManager.getConnection (URL, username, password);
        } catch (SQLException e) {
            System.err.println("problems for database connection: "+URL);
	    System.err.println(e);
        }
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

	String drop = "DROP TABLE IF EXISTS esp";
	log.trace("Dropping previous version of esp table");
	try {
	    statement = connection.createStatement();
	    statement.execute(drop);
	}catch (SQLException e) {
            System.err.println("problems dropping table esp");
	    System.err.println(e);
	    System.exit(1);
        }

	String create = "CREATE TABLE esp ("+
	    "chromosome   INT,"+
	    "position     INT,"+
	    "ref          CHAR(1),"+
	    "alt          CHAR(1),"+
	    "minor        SMALLINT,"+
	    "major        SMALLINT,"+
	    "frequency    FLOAT,"+
	    "PRIMARY KEY(position,chromosome,ref,alt))";
	  
	log.trace(create);
	try {
	    statement = connection.createStatement();
	    statement.execute(create);
	}catch (SQLException e) {
            System.err.println("problems creating gene table");
	    System.err.println(e);
	    System.exit(1);
        }
    }


    /**
     * Use ESP2SQLDumpParser to parse all of the 23 files.
     * There is one file each for chr. 1-22 and chr X.
     * This will create a dump file that can be imported into 
     * postgresql (see README for instructions).
     */
    public void parseESPFiles() {
	ESP2SQLDumpParser parser = new ESP2SQLDumpParser();
	get_list_of_ESP_files();
	Iterator<String> it = esp_file_paths.iterator();
	while (it.hasNext()) {
	    String fname = it.next();
	    parser.input_ESP_File(fname);
	}
	parser.cleanup_filehandles();
    }


     /**
     * Get a list of all ESP Annotation files from the
     * annotation_directory. A String with the path of each
     * valid ESP file is put into the array  list
     * esp_file_paths. The files have names such as
     * ESP6500.chr14.snps.txt 
     * so that we can find them by searching on "chr".
     */
    public void get_list_of_ESP_files()
    {
	if (esp_dir != null)
	    System.out.println("Getting list of ESSP files from directory: " + esp_dir);
	else {
	    System.out.println("Error: ESP path was not initialized");
	    System.exit(1);
	}
	esp_file_paths = new ArrayList<String>();
	
	File folder = new File(esp_dir);
	if (folder == null || ! folder.isDirectory()) {
	    System.err.println("Error: Could not initialize folder at path " + esp_dir);
	    System.exit(1);
	}
	File[] listOfFiles = folder.listFiles();
	System.out.println("There were " + listOfFiles.length + " files in all in the ESP folder");

	for (int i = 0; i < listOfFiles.length; i++) {
	    if (listOfFiles[i].isFile()) {
		String f = listOfFiles[i].getName();
		int ind = f.indexOf("chr"); // Just extract ESP files
		if (ind < 0) continue;
		String path = esp_dir + "/" + f;
		this.esp_file_paths.add(path);
	    }	
	}
	if (esp_file_paths.size() != 23) {
	    System.out.println("Did not get all 23 chromosome files from ESP");
	    System.out.println("Got a total of " + esp_file_paths.size() + 
			       "  files.");
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
	    options.addOption(new Option("e","esp",true,"Directory containing ESP annotation files. Required"));
	   
	    Parser parser = new GnuParser();
	    CommandLine cmd = parser.parse(options,args);
	    if (cmd.hasOption("h"))
	    {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("ESP2SQL", options);
		System.exit(0);
	    }

	    this.esp_dir = getRequiredOptionValue(cmd,'e');
	   
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