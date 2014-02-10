package jannovar.io;

import java.io.IOException;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import jannovar.exception.JannovarException;
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
     * Serializes the contents of an ArrayList of 
     * {@link jannovar.reference.TranscriptModel TranscriptModel} objects. 
     * @param filename Name and path of the file to serialize to
     * @param kgList A list of {@link jannovar.reference.TranscriptModel TranscriptModel} objects to be serialized
     * @throws jannovar.exception.JannovarException
     */
    public void serializeKnownGeneList(String filename, ArrayList<TranscriptModel> kgList) throws JannovarException {
	if (kgList == null || kgList.isEmpty()) {
	    throw new JannovarException("Error: attempt to serialize empty knownGene list");
	}
	try {
	    FileOutputStream fos = new FileOutputStream(filename);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(kgList);
            oos.close();
	} catch (IOException i) {
	    String error = String.format("Could not serialize knownGene list: %s",i.toString());
	    throw new JannovarException(error);
	}
    }
    
    
    /**
     * Deserializes an ArrayList of 
     * {@link jannovar.reference.TranscriptModel TranscriptModel}
     * objects that were 
     * originally created by parsing the four UCSC known gene files.
     * @param filename name of serialized file
     * @return list of {@link TranscriptModel}s
     * @throws jannovar.exception.JannovarException
     */
    @SuppressWarnings (value="unchecked")
    public ArrayList<TranscriptModel> deserializeKnownGeneList(String filename) throws JannovarException  {
	ArrayList<TranscriptModel> kgList = null;
	try{
	    FileInputStream fileIn = new FileInputStream(filename);
	    ObjectInputStream in = new ObjectInputStream(fileIn);
	    kgList  = (ArrayList<TranscriptModel>) in.readObject();
	    in.close();
	    fileIn.close();
	} catch(IOException i) {
	    String error = String.format("[SerializationManager] i/o error: Could not deserialize knownGene list: %s",i.toString());
	    throw new JannovarException(error);
	} catch(ClassNotFoundException c) {
	    String error = String.format("[SerializationManager] Could not serialized class definition: %s",c.toString());
	    throw new JannovarException(error);
	}
	return kgList;
    }




}