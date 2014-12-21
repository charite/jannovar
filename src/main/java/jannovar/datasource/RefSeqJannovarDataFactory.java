package jannovar.datasource;

import jannovar.exception.TranscriptParseException;
import jannovar.io.JannovarData;
import jannovar.io.ReferenceDictionary;
import jannovar.parse.RefSeqParser;
import jannovar.reference.TranscriptInfo;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

/**
 * Creation of {@link JannovarData} objects from a {@link RefSeqDataSource}.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
class RefSeqJannovarDataFactory extends JannovarDataFactory {

	/**
	 * Construct the factory with the given {@link RefSeqDataSource}.
	 *
	 * @param dataSource
	 *            the data source to use.
	 * @param iniSection
	 *            {@link Section} with configuration from INI file
	 */
	public RefSeqJannovarDataFactory(RefSeqDataSource dataSource, Section iniSection) {
		super(dataSource, iniSection);
	}

	@Override
	protected ImmutableList<TranscriptInfo> parseTranscripts(ReferenceDictionary refDict, String targetDir)
			throws TranscriptParseException {
		return new RefSeqParser(refDict, targetDir, iniSection).run();
	}

}
