package de.charite.compbio.jannovar.datasource;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.parse.TranscriptParseException;
import de.charite.compbio.jannovar.impl.parse.flatbed.FlatBEDParser;
import de.charite.compbio.jannovar.reference.TranscriptModel;
import java.util.List;

/**
 * {@link JannovarDataFactory} for flat BED files.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class FlatBEDJannovarDataFactory extends JannovarDataFactory {

	/**
	 * Construct the factory with the given {@link EnsemblDataSource}.
	 * 
	 * {@code geneIdentifiers} is being ignored.
	 *
	 * @param options
	 *            configuration for proxy settings
	 * @param dataSource
	 *            the data source to use.
	 * @param geneIdentifiers
	 *            list of gene identifiers to include
	 * @param iniSection
	 *            {@link Section} with configuration from INI file
	 */
	public FlatBEDJannovarDataFactory(DatasourceOptions options, DataSource dataSource, Section iniSection) {
		super(options, dataSource, iniSection);
	}

	@Override
	protected ImmutableList<TranscriptModel> parseTranscripts(ReferenceDictionary refDict, String targetDir,
			List<String> geneIdentifiers) throws TranscriptParseException {
		return new FlatBEDParser(refDict, targetDir, iniSection, options.doPrintProgressBars()).run();
	}

}
