package de.charite.compbio.jannovar.impl.parse.gtfgff;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract base class for GTF/GFF record parsers.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public abstract class FeatureRecordParser {

	protected static final Logger LOGGER = LoggerFactory.getLogger(GFFRecordParser.class);

	/**
	 * Parse the line and return the corresponding {@link FeatureRecord}
	 */
	public FeatureRecord parseLine(String line) {
		LOGGER.debug("Parsing GFF line\t{}", new Object[] { line });
	
		String[] arr = line.trim().split("\\t");
		if (arr.length != 9)
			throw new RuntimeException("Wrong number of fields in GFF file!");
	
		final String chrom = arr[0];
		final String source = arr[1];
		final String type = arr[2];
		final int beginPos = Integer.parseInt(arr[3]) - 1;
		final int endPos = Integer.parseInt(arr[4]);
		final String score = arr[5];
		FeatureRecord.Strand strand = arr[6].equals("+") ? FeatureRecord.Strand.FORWARD : FeatureRecord.Strand.REVERSE;
	
		int phase = 0;
		try {
			if (!arr[7].equals("."))
				phase = Integer.parseInt(arr[7]);
		} catch (NumberFormatException e) {
			LOGGER.warn("Invalid phase {}", new Object[] { arr[7] });
		}
		if (phase < 0 || phase > 3)
			phase = 0;
	
		FeatureRecord result = new FeatureRecord(chrom, source, type, beginPos, endPos, score, strand, phase,
				parseAttributes(arr[8]));
		LOGGER.debug("Resulting record is {}", new Object[] { result });
		return result;
	}

	protected abstract Map<String, String> parseAttributes(String string);

}