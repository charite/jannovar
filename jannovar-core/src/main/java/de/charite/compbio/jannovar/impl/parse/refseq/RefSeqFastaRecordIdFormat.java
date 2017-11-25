package de.charite.compbio.jannovar.impl.parse.refseq;

import com.google.common.base.Splitter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import static java.util.Optional.empty;
import static java.util.Optional.of;

/**
 * RefSeq import files may have different formats in their FASTA record IDs.
 */
public enum RefSeqFastaRecordIdFormat {

	DEFAULT_FORMAT,

	INTERIM_RELEASE_201701_FORMAT,

	UNKNOWN_FORMAT;

	private static final Logger LOGGER = LoggerFactory.getLogger(RefSeqFastaRecordIdFormat.class);

	private static final String EXPECTED_PREFIX_DEFAULT_FORMAT = "gi|";

	private static final int DEFAULT_FORMAT_ACCESSION_INDEX = 3;

	private static final int DEFAULT_FORMAT_EXPECTED_RECORD_ID_ELEMENTS = 5;

	/**
	 * Number of digits depends on the prefix (here we allow 'NM_' and 'NR_', as well as non-curated 'XM_' and 'X_R').
	 *
	 * @see <a href="https://www.ncbi.nlm.nih.gov/books/NBK21091/table/ch18.T.refseq_accession_numbers_and_mole/?report=objectonly">NCBI documentation</a>
	 */
	private static final String NCBI_ID_REGEXP = "[NX][MR]_([0-9]+)\\.[0-9]+";

	private static final Pattern NCBI_ID = Pattern.compile(NCBI_ID_REGEXP);

	public static RefSeqFastaRecordIdFormat detect(String recordId) {
		if (recordId == null) {
			return UNKNOWN_FORMAT;
		}
		if (recordId.startsWith(EXPECTED_PREFIX_DEFAULT_FORMAT)) {
			return DEFAULT_FORMAT;
		}
		if (NCBI_ID.matcher(recordId).matches()) {
			return INTERIM_RELEASE_201701_FORMAT;
		}
		return UNKNOWN_FORMAT;
	}

	public static Optional<String> extractAccession(String recordId) {
		switch (detect(recordId)) {
		case DEFAULT_FORMAT:
			final List<String> tokens = Splitter.on('|').splitToList(recordId);
			if (tokens.size() != DEFAULT_FORMAT_EXPECTED_RECORD_ID_ELEMENTS) {
				LOGGER.error("ID {} in FASTA did not have 4 fields", recordId);
				return empty();
			}
			return of(tokens.get(DEFAULT_FORMAT_ACCESSION_INDEX));
		case INTERIM_RELEASE_201701_FORMAT:
			return of(recordId);
		case UNKNOWN_FORMAT:
			LOGGER.error("ID {} in FASTA did not have any of the expected formats.", recordId);
		}
		return empty();
	}
}
