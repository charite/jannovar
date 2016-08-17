package de.charite.compbio.jannovar.datasource;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.parse.TranscriptParseException;
import de.charite.compbio.jannovar.impl.parse.ucsc.UCSCParser;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Creation of {@link JannovarData} objects from a {@link UCSCDataSource}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
final class UCSCJannovarDataFactory extends JannovarDataFactory {

	/**
	 * Construct the factory with the given {@link UCSCDataSource}.
	 *
	 * @param options
	 *            configuration for proxy settings
	 * @param dataSource
	 *            the data source to use.
	 * @param iniSection
	 *            {@link Section} with configuration from INI file
	 */
	public UCSCJannovarDataFactory(DatasourceOptions options, UCSCDataSource dataSource, Section iniSection) {
		super(options, dataSource, iniSection);
	}

	@Override
	protected ImmutableList<TranscriptModel> parseTranscripts(ReferenceDictionary refDict, String targetDir)
			throws TranscriptParseException {
		return new UCSCParser(refDict, targetDir, iniSection).run();
	}

}
