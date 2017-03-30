package de.charite.compbio.jannovar.stats.facade;

import java.io.File;

/**
 * Implementation of writing the statistics to a CSV file
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class StatisticsWriter {

	public StatisticsWriter() {
		super();
	}
	
	public void writeStatistics(Statistics stats, File targetPath) {
		throw new RuntimeException("Implement me!");
	}
	
}
