package de.charite.compbio.jannovar.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.charite.compbio.jannovar.impl.util.StringUtil;

// NOTE(holtgrem): Part of the public interface of the Jannovar library.

/**
 * Manager for serializing and deserializing {@link JannovarData} objects.
 *
 * @author <a href="mailto:peter.robinson@charite.de">Peter N Robinson</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public final class JannovarDataSerializer {

	/** the logger object to use */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	/** path to file to serialize to or deserialize from */
	private final String filename;

	/**
	 * Initialize the (de)serializer with the path to the file to load/save.
	 *
	 * @param filename
	 *            path to the file to deserialize from or serialize to
	 */
	public JannovarDataSerializer(String filename) {
		this.filename = filename;
	}

	/**
	 * Serialize a {@link JannovarData} object to a file.
	 *
	 * @param data
	 *            the {@link JannovarData} object to serialize
	 * @throws SerializationException
	 *             on problems with the serialization
	 */
	public void save(JannovarData data) throws SerializationException {
		logger.info(StringUtil.concatenate("Serializing JannovarData to ", filename));
		final long startTime = System.nanoTime();

		if (data == null || data.getRefDict().getContigNameToID().isEmpty())
			throw new SerializationException("Attempting to serialize empty data set");

		// This is waiting for Java 7 to be improved. Also see:
		// http://stackoverflow.com/questions/4092914
		String error = null;
		FileOutputStream fos = null;
		GZIPOutputStream gzos = null;
		ObjectOutputStream oos = null;
		try {
			fos = new FileOutputStream(filename);
			gzos = new GZIPOutputStream(fos);
			oos = new ObjectOutputStream(gzos);
			oos.writeObject(data);
		} catch (IOException i) {
			error = String.format("Could not serialize data file list: %s", i.toString());
		} finally {
			if (oos != null)
				try {
					oos.close();
					fos.close();
				} catch (IOException e) {
					// swallow, nothing we can do
				}
			if (fos != null)
				try {
					fos.close();
				} catch (IOException e) {
					// swallow, nothing we can do
				}
			if (error != null)
				throw new SerializationException(error);
		}

		logger.info(String.format("Serialization took %.2f sec.",
				(System.nanoTime() - startTime) / 1000.0 / 1000.0 / 1000.0));
	}

	/**
	 * Deserialize a {@link JannovarData} object from a file.
	 *
	 * @return {@link JannovarData} object yielded by deserialization
	 * @throws SerializationException
	 *             on problems with the deserialization
	 */
	public JannovarData load() throws SerializationException {
		logger.info(StringUtil.concatenate("Deserializing JannovarData from ", filename));
		final long startTime = System.nanoTime();

		JannovarData result = null;

		// This is also waiting for Java 7 to be cleaned up, see above.
		String error = null;
		FileInputStream fileIn = null;
		GZIPInputStream gzIn = null;
		ObjectInputStream in = null;
		try {
			fileIn = new FileInputStream(filename);
			gzIn = new GZIPInputStream(fileIn);
			in = new ObjectInputStream(gzIn);
			result = (JannovarData) in.readObject();
		} catch (IOException i) {
			error = String.format("Could not deserialize data list: %s", i.toString());
		} catch (ClassNotFoundException c) {
			error = String.format("Could not deserialized class definition: %s", c.toString());
		} finally {
			try {
				if (in != null)
					in.close();
			} catch (IOException e) {
				// swallow, nothing we can do
			}
			try {
				if (fileIn != null)
					fileIn.close();
			} catch (IOException e) {
				// swallow, nothing we can do
			}
			if (error != null)
				throw new SerializationException(error);
		}

		logger.info(String.format("Deserialization took %.2f sec.",
				(System.nanoTime() - startTime) / 1000.0 / 1000.0 / 1000.0));
		return result;
	}
}
