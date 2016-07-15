package de.charite.compbio.jannovar.impl.parse.gtfgff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;

/**
 * Parse a line of GFF and return a FeatureRecord from this
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GFFRecordParser extends FeatureRecordParser {

	/**
	 * Parse GFF attributes
	 */
	@Override
	protected Map<String, String> parseAttributes(String string) {
		Map<String, String> result = new HashMap<>();

		for (String field : Splitter.on(';').trimResults().split(string)) {
			List<String> arr = Splitter.on("=").trimResults().limit(2).splitToList(field);
			if (arr.size() != 2) {
				LOGGER.warn("Found attribute not following key=value format (skipping) {}",
						new Object[] { field.trim() });
				continue;
			}
			result.put(arr.get(0), arr.get(1));
		}

		return result;
	}

}
