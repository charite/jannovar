package jannovar.util;

import java.io.File;

/**
 * Utility class with static methods for path manipulation.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class PathUtil {

	public static String join(String... args) {
		File file = new File(args[0]);
		for (int i = 1; i < args.length; ++i)
			file = new File(file, args[i]);
		return file.getPath();
	}

}
