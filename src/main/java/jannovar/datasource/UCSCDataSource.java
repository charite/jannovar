package jannovar.datasource;

import jannovar.exception.InvalidDataSourceException;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

/**
 * {@link DataSource} implementation for data from UCSC.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class UCSCDataSource extends DataSource {

	/** expected keys in data source configuration file */
	private final ImmutableList<String> urlKeys = ImmutableList.of("knownGene", "knownGeneMrna", "kgXref",
			"knownToLocusLink", "chromInfo", "chrToAccessions");

	UCSCDataSource(Section iniSection) throws InvalidDataSourceException {
		super(iniSection);

		checkURLs();
	}

	@Override
	public JannovarDataFactory getDataFactory() {
		return new UCSCJannovarDataFactory(this, iniSection);
	}

	@Override
	protected ImmutableList<String> getURLKeys() {
		return urlKeys;
	}
}
