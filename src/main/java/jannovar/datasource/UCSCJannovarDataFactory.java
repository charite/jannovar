package jannovar.datasource;

import jannovar.JannovarOptions;
import jannovar.exception.TranscriptParseException;
import jannovar.io.JannovarData;
import jannovar.io.ReferenceDictionary;
import jannovar.parse.UCSCParser;
import jannovar.reference.TranscriptInfo;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

/**
 * Creation of {@link JannovarData} objects from a {@link UCSCDataSource}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
class UCSCJannovarDataFactory extends JannovarDataFactory {

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
	public UCSCJannovarDataFactory(JannovarOptions options, UCSCDataSource dataSource, Section iniSection) {
		super(options, dataSource, iniSection);
	}

	@Override
	protected ImmutableList<TranscriptInfo> parseTranscripts(ReferenceDictionary refDict, String targetDir)
			throws TranscriptParseException {
		return new UCSCParser(refDict, targetDir, iniSection).run();
	}

}
