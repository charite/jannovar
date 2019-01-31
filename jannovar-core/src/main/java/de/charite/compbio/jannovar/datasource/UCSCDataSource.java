package de.charite.compbio.jannovar.datasource;

import com.google.common.collect.ImmutableList;
import org.ini4j.Profile.Section;

/**
 * {@link DataSource} implementation for data from UCSC.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
final class UCSCDataSource extends DataSource {

  /** expected keys in data source configuration file */
  private final ImmutableList<String> urlKeys =
      ImmutableList.of(
          "knownCanonical",
          "knownGene",
          "knownGeneMrna",
          "kgXref",
          "knownToLocusLink",
          "chromInfo",
          "chrToAccessions");

  UCSCDataSource(DatasourceOptions options, Section iniSection) throws InvalidDataSourceException {
    super(options, iniSection);

    checkURLs();
  }

  @Override
  public JannovarDataFactory getDataFactory() {
    return new UCSCJannovarDataFactory(options, this, iniSection);
  }

  @Override
  protected ImmutableList<String> getURLKeys() {
    return urlKeys;
  }
}
