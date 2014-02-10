package jannovar.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.ArrayList;

import jannovar.exception.PedParseException;
import jannovar.pedigree.Person;
import jannovar.pedigree.Pedigree;

/**
 * A class to parse standard Pedfiles (pedigree files) as known from linkage.
 * In particular, we expect a pre-makeped format file
 * <ul>
 * <li> Column 1: Pedigree identifier (The identifier can be a number)
 * <li> Column 2: Individual's ID (or a character string)
 * <li> Column 3: The individual's father (If the person is a founder, just put a 0 in each column)
 * <li> Column 4: The individual's mother (If the person is a founder, just put a 0 in each column)
 * <li> Column 5: Sex (1 = Male, 2 = Female, Unknown sex is not permitted)
 * <li> Column 6+: Genetic data (Disease and marker phenotypes)
 * </ul>
 * Note that in our format, we only use 6 columns, and the sixth column contains the
 * disease phenotype data,
 * <ul>
 * <li> 2 means affected
 * <li> 1 means unaffected
 * <li> 0 means unknown
 * </ul>
 * @author Peter Robinson
 * @version 0.04 (2 June, 2013)
 */
public class PedFileParser {
    /**
     * Complete path to the PED file
     */
    private String pedfile_path=null;
    /**
     * The plain (base) name of the PED file.
     */
    private String base_filename=null;
    /**
     * Pedigree object representing the family described in the PED file. This
     * object is created and returned by {@link #parseFile} if parsing is successful.
     */
    private Pedigree pedigree = null;

    public PedFileParser(){
	/* no-op */
    }
    
    
    public Pedigree parseFile(String PEDfilePath) throws PedParseException {
	 this.pedfile_path = PEDfilePath;
	 File file = new File(this.pedfile_path);
	 this.base_filename = file.getName();
	 try{
	     FileInputStream fstream = new FileInputStream(this.pedfile_path);
	     DataInputStream in = new DataInputStream(fstream);
	     BufferedReader br = new BufferedReader(new InputStreamReader(in));
	     inputPedFileStream(br);
	     br.close();
	 } catch (IOException e) {
	    String err = String.format("[PedFileParser:parseFile]: %s",e.toString());
	    throw new PedParseException(err);
	 }
	 return this.pedigree;
    }
    
    
    /**
     * Parse a PED file that has been put into a BufferedReader by
     * client code (one possible use: a tomcat server). The result is the same
     * as if we passed the file path to the method {@link #parseFile} but
     * is useful in cases where we have a BufferedReader but not a file on disk.
     * @param PEDfileContents reader object
     * @return {@link Pedigree} object
     * @throws jannovar.exception.PedParseException
     */
    public Pedigree parseStream(BufferedReader PEDfileContents) throws PedParseException {
	try{
	    inputPedFileStream(PEDfileContents);
	} catch (IOException e) {
	    String err =
	      String.format("[PedFileParser:parseStringStream]: %s",e.toString());
	    throw new PedParseException(err);
	 }
	return this.pedigree;
    }
    
    /**
     * Parse the entire PED file. A typical line looks like
     * <pre>
     * ped1 son1 father mother 1 2
     * </pre>
     * @param br An open handle to a PED file.
     */
    private void inputPedFileStream(BufferedReader br)
	throws IOException, PedParseException
    {	
	String line;
	int linecount=0;
	boolean firstline = true; /* flag for the first line of the ped file. */
	ArrayList<Person> personList = new ArrayList<Person>();
	String famID =null;
	while (( line = br.readLine()) != null){
            if (line.isEmpty())
                continue; /* Should not happen, but skip silently */
            String A[] = line.split("\\s+");
        
            if (A.length < 6){
                throw new PedParseException("Error: ped file line with less than 6 fields: "+ line);
            }
            Person per = parsePerson(A);
            if (firstline) {
                famID = per.getFamilyID();
	    }
	    personList.add(per);
	}
	this.pedigree = new Pedigree(personList,famID);
    }

    /**
     * Parse a Person object from a ped file line that has been split into an Array of Strings
     * that should have (at least) six fields. Ignore anything more than the first six fields.
     * See the documentation for this class for the structure of the ped file lines.
     * @param A list of fields of a single ped file line
     * @return {@link Person} object
     * @throws jannovar.exception.PedParseException
     */
    public Person parsePerson(String [] A) throws PedParseException
    {
        String famID = A[0];
        String individualID = A[1];
        String fatherID = A[2].equals("0") ? null : A[2];
        String motherID = A[3].equals("0") ? null : A[3];
        String sex = A[4];
        String disease = A[5];
        return new Person(famID,individualID,fatherID,motherID,sex,disease);        
    }

}