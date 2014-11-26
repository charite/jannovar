package jannovar.util;

import java.io.File;

/**
 * Utility class with static methods for path manipulation.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class PathUtil {

	public static String join(String path1, String path2) {
		File file1 = new File(path1);
		File file2 = new File(file1, path2);
		return file2.getPath();
	}

}
