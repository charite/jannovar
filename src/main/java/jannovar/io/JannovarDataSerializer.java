package jannovar.io;

import jannovar.exception.SerializationException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Manager for serializing and deserializing {@link JannovarData} objects.
 * 
 * @author Peter N Robinson <peter.robinson@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class JannovarDataSerializer {

	/**
	 * Serialize a {@link JannovarData} object to a file.
	 * 
	 * @param filename
	 *            path to the file
	 * @param data
	 *            the {@link JannovarData} object to serialize
	 * @throws SerializationException
	 *             on problems with the serialization
	 */
	public static void serializeKnownGeneList(String filename, JannovarData data) throws SerializationException {
		if (data == null || data.transcriptInfos.isEmpty() || data.referenceDict.contigID.isEmpty())
			throw new SerializationException("Attempting to serialize empty data set");

		// This is waiting for Java 7 to be improved. Also see: http://stackoverflow.com/questions/4092914
		String error = null;
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(filename);
			oos = new ObjectOutputStream(fos);
			oos.writeObject(data);
		} catch (IOException i) {
			error = String.format("Could not serialize data file list: %s", i.toString());
		} finally {
			try {
				oos.close();
				fos.close();
			} catch (IOException e) {
				// swallow, nothing we can do
			}
			if (error != null)
				throw new SerializationException(error);
		}
	}

	/**
	 * Deserialize a {@link JannovarData} object from a file.
	 * 
	 * @param filename
	 *            path to the file
	 * @return {@link JannovarData} object
	 * @throws SerializationException
	 *             on problems with the deserialization
	 */
	public static JannovarData deserializeKnownGeneList(String filename) throws SerializationException {
		JannovarData result = null;

		// This is also waiting for Java 7 to be cleaned up, see above.
		String error = null;
		FileInputStream fileIn = null;
		ObjectInputStream in = null;
		try {
			fileIn = new FileInputStream(filename);
			in = new ObjectInputStream(fileIn);
			result = (JannovarData) in.readObject();
		} catch (IOException i) {
			error = String.format("Could not deserialize data list: %s", i.toString());
		} catch (ClassNotFoundException c) {
			error = String.format("Could not deserialized class definition: %s", c.toString());
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				// swallow, nothing we can do
			}
			try {
				fileIn.close();
			} catch (IOException e) {
				// swallow, nothing we can do
			}
			if (error != null)
				throw new SerializationException(error);
		}

		return result;
	}
}
