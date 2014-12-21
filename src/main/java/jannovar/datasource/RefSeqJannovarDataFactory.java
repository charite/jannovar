package jannovar.datasource;

import jannovar.JannovarOptions;
import jannovar.exception.TranscriptParseException;
import jannovar.impl.parse.RefSeqParser;
import jannovar.io.JannovarData;
import jannovar.io.ReferenceDictionary;
import jannovar.reference.TranscriptInfo;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

/**
 * Creation of {@link JannovarData} objects from a {@link RefSeqDataSource}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
final class RefSeqJannovarDataFactory extends JannovarDataFactory {

	/**
	 * Construct the factory with the given {@link RefSeqDataSource}.
	 *
	 * @param options
	 *            configuration for proxy settings
	 * @param dataSource
	 *            the data source to use.
	 * @param iniSection
	 *            {@link Section} with configuration from INI file
	 */
	public RefSeqJannovarDataFactory(JannovarOptions options, RefSeqDataSource dataSource, Section iniSection) {
		super(options, dataSource, iniSection);
	}

	@Override
	protected ImmutableList<TranscriptInfo> parseTranscripts(ReferenceDictionary refDict, String targetDir)
			throws TranscriptParseException {
		return new RefSeqParser(refDict, targetDir, iniSection).run();
	}

}
