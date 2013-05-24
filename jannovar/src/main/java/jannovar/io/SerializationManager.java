package jannovar.io;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import jannovar.reference.TranscriptModel;


/**
 * This class bundles function and code needed to serialize objects
 * and write them to file and to deserialized the objects and recreate
 * the Java objects. It is mainly intended to be used to serialized
 * a list of {@link jannovar.interval.IntervalTree IntervalTree} objects
 * that contain {@link jannovar.reference.TranscriptModel TranscriptModel}
 * model objects representing individual transcripts.
 * @author Peter Robinson
 * @version 0.02 (23 May, 2013)
 */


public class SerializationManager {

    /** Default constructor */
    public SerializationManager() {
    }


    /**
     * Serializes the contents of the HashMap {@link #knownGeneMap knownGeneMap} 
     * @param filename Name and path of the file to serialize to
     * @param kgList A list of {@link jannovar.reference.TranscriptModel TranscriptModel} objects to be serialized
     */
    public void serializeKnownGeneList(String filename, ArrayList<TranscriptModel> kgList) {
	if (kgList ==null || kgList.size()==0) {
	    System.err.println("Error: attempt to serialize empty knownGene list");
	    return;
	}
	try {
	    FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(kgList);
            oos.close();
	} catch (IOException i) {
	    i.printStackTrace();
	    System.err.println("Could not serialize knownGene list");
	    System.exit(1);
	}
    }
    
    
    /**
     * Deserializes the HashMap {@link #knownGeneMap knownGeneMap} that was 
     * originally created by parsing the four UCSC known gene files.
     * @param filename name of serialized file
     */
    @SuppressWarnings (value="unchecked")
    public ArrayList<TranscriptModel> deserializeKnownGeneList(String filename) {
	ArrayList<TranscriptModel> kgList = null;
	try{
	    FileInputStream fileIn = new FileInputStream(filename);
	    ObjectInputStream in = new ObjectInputStream(fileIn);
	    kgList  = (ArrayList<TranscriptModel>) in.readObject();
	    in.close();
	    fileIn.close();
	} catch(IOException i) {
            i.printStackTrace();
	    System.err.println("Could not deserialize knownGeneMap");
	    System.exit(1);
	} catch(ClassNotFoundException c) {
            System.out.println("Could not find HashMap<String,TranscriptModel> class.");
            c.printStackTrace();
            System.exit(1);
        }
	return kgList;
    }


      /**
     * Inputs the list of known genes from the serialized data file. The serialized file 
     * was originally  created by parsing the three UCSC known gene files.
   
    @SuppressWarnings (value="unchecked")
    public void deserializeUCSCdata() {
	HashMap<String,TranscriptModel> kgMap=null;
	try {
	     FileInputStream fileIn =
		 new FileInputStream(this.serializedFile);
	     ObjectInputStream in = new ObjectInputStream(fileIn);
	     kgMap = (HashMap<String,TranscriptModel>) in.readObject();
            in.close();
            fileIn.close();
	}catch(IOException i) {
            i.printStackTrace();
	    System.err.println("Could not deserialize knownGeneMap");
	    System.exit(1);
           
        }catch(ClassNotFoundException c) {
            System.out.println("Could not find HashMap<String,TranscriptModel> class.");
            c.printStackTrace();
            System.exit(1);
        }
	//System.out.println("Done deserialization, size of map is " + kgMap.size());
	this.chromosomeMap = new HashMap<Byte,Chromosome> ();
	// System.out.println("Number of KGs is " + kgMap.size());
	for (TranscriptModel kgl : kgMap.values()) {
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
    }  */




}