package de.charite.compbio.jannovar.impl.parse.refseq;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import com.google.common.base.Splitter;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** RefSeq import files may have different formats in their FASTA record IDs. */
public enum RefSeqFastaRecordIdFormat {
  GI_REF_FORMAT("gi|", 3, 5),

  REF_FORMAT("ref|", 1, 3),

  ACCESSION_FORMAT("", 0, 1),

  UNKNOWN_FORMAT(null, -1, -1);

  private static final Logger LOGGER = LoggerFactory.getLogger(RefSeqFastaRecordIdFormat.class);

  /**
   * Number of digits depends on the prefix (here we allow 'NM_' and 'NR_', as well as non-curated
   * 'XM_' and 'X_R').
   *
   * @see <a
   *     href="https://www.ncbi.nlm.nih.gov/books/NBK21091/table/ch18.T.refseq_accession_numbers_and_mole/?report=objectonly">NCBI
   *     documentation</a>
   */
  private static final Pattern NCBI_ID = Pattern.compile("[NX][MR]_([0-9]+)\\.[0-9]+");

  private final String expectedPrefix;
  private final int accessionIndex;
  private final int expectedIdElementCount;

  RefSeqFastaRecordIdFormat(String expectedPrefix, int accessionIndex, int expectedIdElementCount) {
    this.expectedPrefix = expectedPrefix;
    this.accessionIndex = accessionIndex;
    this.expectedIdElementCount = expectedIdElementCount;
  }

  public static RefSeqFastaRecordIdFormat detect(String recordId) {
    if (recordId == null) {
      return UNKNOWN_FORMAT;
    }
    if (recordId.startsWith(GI_REF_FORMAT.expectedPrefix)) {
      return GI_REF_FORMAT;
    }
    if (recordId.startsWith(REF_FORMAT.expectedPrefix)) {
      return REF_FORMAT;
    }
    if (NCBI_ID.matcher(recordId).matches()) {
      return ACCESSION_FORMAT;
    }
    return UNKNOWN_FORMAT;
  }

  public static Optional<String> extractAccession(String recordId) {
    RefSeqFastaRecordIdFormat format = detect(recordId);
    if (format.equals(UNKNOWN_FORMAT)) {
      LOGGER.error("ID {} in FASTA did not have any of the expected formats.", recordId);
      return empty();
    }
    final List<String> tokens = Splitter.on('|').splitToList(recordId);
    if (tokens.size() != format.expectedIdElementCount) {
      LOGGER.error(
          "ID {} in FASTA did not have {} fields", recordId, format.expectedIdElementCount);
      return empty();
    }
    return of(tokens.get(format.accessionIndex));
  }

  public String getPrefix() {
    return this.expectedPrefix;
  }

  public int getAcdcessionIndex() {
    return this.accessionIndex;
  }

  public int getIDElementCount() {
    return this.expectedIdElementCount;
  }
}
