package de.charite.compbio.jannovar.datasource;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.parse.FlatBEDParser;
import de.charite.compbio.jannovar.impl.parse.TranscriptParseException;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * {@link JannovarDataFactory} for flat BED files.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class FlatBEDJannovarDataFactory extends JannovarDataFactory {

	/**
	 * Construct the factory with the given {@link EnsemblDataSource}.
	 *
	 * @param options
	 *            configuration for proxy settings
	 * @param dataSource
	 *            the data source to use.
	 * @param iniSection
	 *            {@link Section} with configuration from INI file
	 */
	public FlatBEDJannovarDataFactory(DatasourceOptions options, DataSource dataSource, Section iniSection) {
		super(options, dataSource, iniSection);
	}

	@Override
	protected ImmutableList<TranscriptModel> parseTranscripts(ReferenceDictionary refDict, String targetDir)
			throws TranscriptParseException {
		return new FlatBEDParser(refDict, targetDir, iniSection, options.doPrintProgressBars()).run();
	}

}
