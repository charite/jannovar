package de.charite.compbio.jannovar.cmd.annotate_vcf;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import htsjdk.samtools.util.Interval;

/**
 * Helper for parsing the region
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
class RegionParser {

	public static Interval parse(String region) {
		final String patternRegion = "^([a-zA-Z0-9_.]+):([1-9][0-9,]*)-([1-9][0-9,]*)$";
		final Matcher matcherRegion = Pattern.compile(patternRegion).matcher(region);
		if (!matcherRegion.find()) {
			final String patternChrom = "^([a-zA-Z0-9_.]+)$";
			final Matcher matcherChrom = Pattern.compile(patternChrom).matcher(region);
			if (!matcherChrom.find())
				return null;
			return new Interval(matcherChrom.group(1), 0, 0);
		} else {
			return new Interval(matcherRegion.group(1), Integer.parseInt(matcherRegion.group(2).replaceAll(",", "")),
					Integer.parseInt(matcherRegion.group(3).replaceAll(",", "")));
		}
	}

}
