package de.charite.compbio.jannovar.impl.util;

import java.io.File;

/**
 * Utility class with static methods for path manipulation.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class PathUtil {

	/**
	 * Join file name components.
	 *
	 * <pre>
	 * String path = Path.join(&quot;path&quot;, &quot;to&quot;, &quot;file&quot;);
	 * // =&gt; path == &quot;path/to/file&quot;;
	 * </pre>
	 *
	 * Note that this also works fine for joining paths in URLs.
	 *
	 * @param components
	 *            file name components to join
	 * @return joint file name components of <code>components</code>
	 */
	public static String join(String... components) {
		File file = new File(components[0]);
		for (int i = 1; i < components.length; ++i)
			file = new File(file, components[i]);
		return file.getPath();
	}

}
