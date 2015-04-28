package de.charite.compbio.jannovar.datasource;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

/**
 * {@link DataSource} implementation for data from RefSeq.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
final class RefSeqDataSource extends DataSource {

	/** expected keys in data source configuration file */
	private final ImmutableList<String> urlKeys = ImmutableList.of("gff", "rna", "chromInfo", "chrToAccessions");

	RefSeqDataSource(DatasourceOptions options, Section iniSection) throws InvalidDataSourceException {
		super(options, iniSection);

		checkURLs();
	}

	@Override
	public JannovarDataFactory getDataFactory() {
		return new RefSeqJannovarDataFactory(options, this, iniSection);
	}

	@Override
	protected ImmutableList<String> getURLKeys() {
		return urlKeys;
	}

}
