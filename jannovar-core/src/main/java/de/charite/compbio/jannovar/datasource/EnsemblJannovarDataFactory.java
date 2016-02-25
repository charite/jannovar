package de.charite.compbio.jannovar.datasource;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.parse.EnsemblParser;
import de.charite.compbio.jannovar.impl.parse.TranscriptParseException;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Creation of {@link JannovarData} objects from a {@link EnsemblDataSource}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
final class EnsemblJannovarDataFactory extends JannovarDataFactory {

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
	public EnsemblJannovarDataFactory(DatasourceOptions options, EnsemblDataSource dataSource, Section iniSection,
			boolean printProgressBars) {
		super(options, dataSource, iniSection);
	}

	@Override
	protected ImmutableList<TranscriptModel> parseTranscripts(ReferenceDictionary refDict, String targetDir)
			throws TranscriptParseException {
		return new EnsemblParser(refDict, targetDir, iniSection, options.doPrintProgressBars()).run();
	}

}
