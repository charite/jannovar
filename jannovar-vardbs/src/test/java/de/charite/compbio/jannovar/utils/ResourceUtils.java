package de.charite.compbio.jannovar.utils;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * Helper class with static methods for handling resources in tests
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ResourceUtils {

	/**
	 * Helper function for reading resources into memory
	 */
	public static String readResource(String path) {
		StringWriter writer = new StringWriter();
		try {
			IOUtils.copy(ResourceUtils.class.getResourceAsStream(path), writer, "UTF-8");
		} catch (IOException e) {
			throw new RuntimeException("Problem reading resource " + path, e);
		}
		return writer.toString();
	}

	/**
	 * Copy resource at the given path to the given output {@link File}.
	 */
	public static void copyResourceToFile(String path, File outFile) {
		try (InputStream input = ResourceUtils.class.getResourceAsStream(path)) {
			Files.copy(input, outFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException("Problem with copying resource to file", e);
		}
	}

}
