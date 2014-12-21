package jannovar.datasource;

import jannovar.exception.InvalidDataSourceException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;
import org.ini4j.Profile.Section;

import com.google.common.collect.ImmutableList;

/**
 * Factory class that allows the construction of {@link DataSource} objects as configured in INI files.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class DataSourceFactory {

	/** {@link Ini} object to use for loading data */
	private final ImmutableList<Ini> inis;

	/**
	 * @param iniFilePaths
	 *            path to INI file to load the data source config from
	 * @throws InvalidDataSourceException
	 *             on problems with the data source config file
	 */
	public DataSourceFactory(ImmutableList<String> iniFilePaths) throws InvalidDataSourceException {
		ImmutableList.Builder<Ini> inisBuilder = new ImmutableList.Builder<Ini>();
		for (String iniFilePath : iniFilePaths) {
			InputStream is;
			final String BUNDLE_PREFIX = "bundle://";
			if (iniFilePath.startsWith(BUNDLE_PREFIX)) {
				String strippedPath = iniFilePath.substring(BUNDLE_PREFIX.length());
				is = this.getClass().getResourceAsStream(strippedPath);
				if (is == null)
					throw new InvalidDataSourceException("BUG: bundled file " + strippedPath + " not in JAR!");
			} else {
				try {
					is = new FileInputStream(iniFilePath);
				} catch (FileNotFoundException e) {
					throw new InvalidDataSourceException("Problem opening data source file " + iniFilePath + ": "
							+ e.getMessage());
				}
			}
			Ini ini = new Ini();
			try {
				ini.load(is);
			} catch (InvalidFileFormatException e) {
				throw new InvalidDataSourceException("Problem loading data source file: " + e.getMessage());
			} catch (IOException e) {
				throw new InvalidDataSourceException("Problem loading data source file: " + e.getMessage());
			}
			inisBuilder.add(ini);
		}
		this.inis = inisBuilder.build();
	}

	/**
	 * Construct {@link DataSource}
	 *
	 * @param name
	 *            key of the INI section to load the data source from
	 * @return {@link DataSource} with data from the file
	 * @throws InvalidDataSourceException
	 *             if <code>name</code> could not be found in any data source config file
	 */
	public DataSource getDataSource(String name) throws InvalidDataSourceException {
		for (Ini ini : inis) {
			if (!ini.keySet().contains(name))
				continue; // not found in data source
			Section section = ini.get(name);
			String type = section.fetch("type");
			if (type == null)
				throw new InvalidDataSourceException("Data source config does not have \"type\" key.");
			else if (type.equals("ucsc"))
				return new UCSCDataSource(section);
			else if (type.equals("ensembl"))
				return new EnsemblDataSource(section);
			else if (type.equals("refseq"))
				return new RefSeqDataSource(section);
			else
				throw new InvalidDataSourceException("Data source config has invalid \"type\" key: " + type);
		}

		throw new InvalidDataSourceException("Could not find data source " + name + " in any data source file.");
	}
}
