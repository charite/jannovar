package de.charite.compbio.jannovar.datasource;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

/**
 * A {@link DataSource} that reads regions from a BED file.
 * 
 * It then generates one transcript with one exon for each entry in the BED file.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class FlatBEDDataSource extends DataSource {

	/** expected keys in data source configuration file */
	private final ImmutableList<String> urlKeys = ImmutableList.of("chromInfo", "chrToAccessions", "bed", "dna");

	FlatBEDDataSource(DatasourceOptions options, Section iniSection) throws InvalidDataSourceException {
		super(options, iniSection);

		checkURLs();
	}

	@Override
	public JannovarDataFactory getDataFactory() {
		return new FlatBEDJannovarDataFactory(options, this, iniSection);
	}

	@Override
	protected ImmutableList<String> getURLKeys() {
		return urlKeys;
	}

}
