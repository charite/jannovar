package de.charite.compbio.jannovar.datasource;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.hgnc.HGNCParser;

/**
 * Base class for all data sources.
 *
 * Data sources combine the information of (1) a name, (2) a list of URLs with files to download, and (3) obtaining a
 * factory for constructing a {@link JannovarData} object from this information.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public abstract class DataSource {

	/** the configuration */
	protected final DatasourceOptions options;

	/** the {@link Section} to create the DataSource from */
	protected final Section iniSection;

	/** @return name of the data source, e.g. "hg19/ucsc" */
	public final String getName() {
		return iniSection.getName();
	}

	/**
	 * @param key The key to get the file name for.
	 * @return name of file with the given key in the data source, e.g. "knownGene.txt.gz" for
	 *         "knownGene=http://.../knownGene.txt.gz".
	 * @throws InvalidDataSourceException
	 *             if there are problems with retrieving or parsing the URL string
	 */
	public final String getFileName(String key) throws InvalidDataSourceException {
		String urlString = iniSection.fetch(key);
		if (urlString == null || urlString.equals(""))
			throw new InvalidDataSourceException("Cannot retrieve URL for key " + key);
		URL url;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			throw new InvalidDataSourceException("Invalid download URL.", e);
		}
		return new File(url.getPath()).getName();
	}

	/**
	 * @return {@link JannovarDataFactory} to use for creating a {@link JannovarData} object from this
	 *         {@link DataSource} .
	 */
	public abstract JannovarDataFactory getDataFactory();

	/**
	 * Construct {@link DataSource} from INI {@link Section}.
	 *
	 * @param options
	 *            configuration to use (for proxy settings)
	 * @param iniSection
	 *            data to construct the {@link DataSource} from.
	 */
	DataSource(DatasourceOptions options, Section iniSection) {
		this.options = options;
		this.iniSection = iniSection;
	}

	/**
	 * @return list of keys that are required to have a URL for download in {@link #iniSection}
	 */
	protected abstract ImmutableList<String> getURLKeys();

	/**
	 * @return list of URLs with files to download for this data source
	 */
	public final ImmutableList<String> getDownloadURLs() {
		ImmutableList.Builder<String> builder = new ImmutableList.Builder<String>();
		for (String key : getURLKeys())
			builder.add(iniSection.fetch(key));
		// Always download hgnc_complete_set.txt
		builder.add(HGNCParser.DOWNLOAD_URL);
		return builder.build();
	}

	/**
	 * Check {@link #iniSection} for having key/value pairs for all required URLs
	 *
	 * @throws InvalidDataSourceException
	 *             if a key is missing.
	 */
	protected final void checkURLs() throws InvalidDataSourceException {
		for (String key : getURLKeys())
			if (!iniSection.containsKey(key))
				throw new InvalidDataSourceException("Section " + iniSection.getName() + " does not contain key " + key);
	}

}
