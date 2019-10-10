package de.charite.compbio.jannovar.datasource;

import com.google.common.collect.ImmutableList;
import org.ini4j.Profile.Section;

/**
 * {@link DataSource} implementation for data from RefSeq.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
final class RefSeqDataSource extends DataSource {

	/**
	 * expected keys in data source configuration file
	 */
	private final ImmutableList<String> urlKeys = ImmutableList.of("gff", "rna", "chromInfo", "chrToAccessions");
	/**
	 * optional keys in data source configuration file
	 */
	private final ImmutableList<String> optionalUrlKeys = ImmutableList.of("faMT");

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
	@Override
	protected ImmutableList<String> getOptionalURLKeys() {
		return optionalUrlKeys;
	}

}
