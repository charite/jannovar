package de.charite.compbio.jannovar.impl.parse.gtfgff;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Splitter;

/**
 * Parse a line of GTF and return a FeatureRecord from this
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GTFRecordParser extends FeatureRecordParser {

	private enum State {
		INITIAL, KEY, VALUE, VALUE_QUOTED, VALUE_QUOTED_ESCAPED
	}

	/**
	 * Parse GFF attributes
	 */
	@Override
	protected Map<String, String> parseAttributes(String string) {
		Map<String, String> result = new HashMap<>();

		StringBuilder key = new StringBuilder();
		StringBuilder value = new StringBuilder();
		State state = State.INITIAL;

		for (char c : string.toCharArray()) {
			switch (state) {
			case INITIAL:
				if (Character.isWhitespace(c)) {
					break; // skip
				} else {
					state = State.KEY;
					key.append(c);
				}
				break;
			case KEY:
				if (Character.isWhitespace(c)) {
					state = State.VALUE;
				} else {
					key.append(c);
				}
				break;
			case VALUE:
				if (c == '"') {
					state = State.VALUE_QUOTED;
				} else if (c == ';') {
					result.put(key.toString(), value.toString());
					key.setLength(0);
					value.setLength(0);
					state = State.INITIAL;
				} else {
					value.append(c);
				}
				break;
			case VALUE_QUOTED:
				if (c == '\\') {
					state = State.VALUE_QUOTED_ESCAPED;
				} else if (c == '"') {
					state = State.VALUE;
				} else {
					value.append(c);
				}
				break;
			case VALUE_QUOTED_ESCAPED:
				if (c == '"') {
					value.append(c);
				} else {
					value.append('"');
					value.append(c);
				}
			}
		}

		if (key.length() > 0)
			result.put(key.toString(), value.toString());

		return result;
	}

}