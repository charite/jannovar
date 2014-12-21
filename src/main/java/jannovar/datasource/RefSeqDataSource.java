package jannovar.datasource;

import jannovar.exception.InvalidDataSourceException;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

/**
 * {@link DataSource} implementation for data from RefSeq.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class RefSeqDataSource extends DataSource {

	/** expected keys in data source configuration file */
	private final ImmutableList<String> urlKeys = ImmutableList.of("gff", "rna", "chromInfo", "chrToAccessions");

	RefSeqDataSource(Section iniSection) throws InvalidDataSourceException {
		super(iniSection);

		checkURLs();
	}

	@Override
	public JannovarDataFactory getDataFactory() {
		return new RefSeqJannovarDataFactory(this, iniSection);
	}

	@Override
	protected ImmutableList<String> getURLKeys() {
		return urlKeys;
	}

}
