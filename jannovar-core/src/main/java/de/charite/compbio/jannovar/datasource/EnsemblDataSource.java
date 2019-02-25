package de.charite.compbio.jannovar.datasource;

import com.google.common.collect.ImmutableList;
import org.ini4j.Profile.Section;

/**
 * {@link DataSource} implementation for data from Ensembl.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
final class EnsemblDataSource extends DataSource {

	/**
	 * expected keys in data source configuration file
	 */
	private final ImmutableList<String> urlKeys = ImmutableList.of(
		"cdna", "gtf", "chromInfo", "chrToAccessions", "table_gene_main", "table_hgnc",
		"table_entrezgene");

	EnsemblDataSource(DatasourceOptions options, Section iniSection)
		throws InvalidDataSourceException {
		super(options, iniSection);

		checkURLs();
	}

	@Override
	public JannovarDataFactory getDataFactory() {
		return new EnsemblJannovarDataFactory(options, this, iniSection, options.doPrintProgressBars());
	}

	@Override
	protected ImmutableList<String> getURLKeys() {
		return urlKeys;
	}

}
