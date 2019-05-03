package de.charite.compbio.jannovar.impl.util;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.data.ReferenceDictionary;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Helper class for reading FAI-indexed FASTA files.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public class IndexedFASTAReader {

	private static final Logger LOGGER = LoggerFactory.getLogger(IndexedFASTAReader.class);

	/** Reference dictionary. */
	private final ReferenceDictionary refDict;

	/** Path to FASTA file. */
	private final String fastaPath;

	/** Path to FAI file. */
	private final String faiPath;

	/** The FAI records from the file. */
	private final ImmutableList<FAIRecord> faiRecords;

	/**
	 * Mapping from {@code refDict} index to {@code faiRecords} index.
	 * <p>
	 * The mapping is only created for existing entries in {@code faiRecords}.
	 */
	private final ImmutableMap<Integer, Integer> idx2idx;

	/**
	 * Construct with default FAI path ({@code fastaPath + ".fai"}.
	 *
	 * @param refDict The {@link ReferenceDictionary} to use for contig name mapping.
	 * @param fastaPath Path to FASTA file.
	 * @throws JannovarException on problems reading FAI file.
	 */
	public IndexedFASTAReader(ReferenceDictionary refDict, String fastaPath)
		throws JannovarException {
		this(refDict, fastaPath, fastaPath + ".fai");
	}

	/**
	 * Construct with explicit FAI path.
	 *
	 * @param refDict The {@link ReferenceDictionary} to use for contig name mapping.
	 * @param fastaPath Path to FASTA file.
	 * @param faiPath Path to FAI file.
	 * @throws JannovarException on problems reading FAI file.
	 */
	public IndexedFASTAReader(ReferenceDictionary refDict, String fastaPath, String faiPath)
		throws JannovarException {
		this.refDict = refDict;
		this.fastaPath = fastaPath;
		this.faiPath = faiPath;
		this.faiRecords = loadFAIRecord();
		this.idx2idx = buildIdx2Idx();
	}

	private ImmutableList<FAIRecord> loadFAIRecord() throws JannovarException {
		final ImmutableList.Builder<FAIRecord> builder = ImmutableList.builder();
		try (
			FileInputStream fis = new FileInputStream(faiPath);
			InputStreamReader reader = new InputStreamReader(fis);
			BufferedReader bufReader = new BufferedReader(reader)
		) {
			String line;
			while ((line = bufReader.readLine()) != null) {
				final String[] arr = line.trim().split("\t");
				builder.add(new FAIRecord(
					arr[0],
					Integer.parseInt(arr[1]),
					Long.parseLong(arr[2]),
					Integer.parseInt(arr[3]),
					Integer.parseInt(arr[4])
				));
			}
		} catch (Exception e) {
			throw new JannovarException("Could not load FAI file", e);
		}

		return builder.build();
	}

	private ImmutableMap<Integer, Integer> buildIdx2Idx() {
		final Builder<Integer, Integer> builder = ImmutableMap.builder();
		int i = 0;
		for (FAIRecord record: faiRecords) {
			final Integer id = refDict.getContigNameToID().get(record.name);
			if (id == null) {
				LOGGER.info(
					"Contig {} from FAI not found in reference dictionary!",
					new Object[]{ record.name }
				);
			} else {
				builder.put(id, i);
			}
			++i;
		}
		return builder.build();
	}

	/**
	 * Read the bases from contig {@code contigName} between 0-based {@code beginPos} and
	 * {@code endPos}.
	 *
	 * @param contigName Name of the contig to read from.
	 * @param beginPos   0-based start position to read from.
	 * @param endPos     0-based end position to start reading
	 * @return String with the sequences.
	 * @throws JannovarException on problems with I/O.
	 */
	public String readBases(String contigName, int beginPos, int endPos) throws JannovarException {
		final int faiIdx = idx2idx.get(refDict.getContigNameToID().get(contigName));
		final FAIRecord faiRecord = faiRecords.get(faiIdx);

		int beginLine = beginPos / faiRecord.lineBases;
		int beginRow = beginPos - beginLine * faiRecord.lineBases;
		long beginOffset = faiRecord.offset + beginLine * faiRecord.lineWidth + beginRow;

		int endLine = endPos / faiRecord.lineBases;
		int endRow = endPos - endLine * faiRecord.lineBases;
		long endOffset = faiRecord.offset + endLine * faiRecord.lineWidth + endRow;

		final char[] buffer = new char[(int)(endOffset - beginOffset)];
		try (
			FileInputStream fis = new FileInputStream(faiPath);
			InputStreamReader reader = new InputStreamReader(fis);
			BufferedReader bufReader = new BufferedReader(reader)
		) {
			bufReader.skip(beginOffset);
			bufReader.read(buffer, 0, (int)(endOffset - beginOffset));
		} catch (Exception e) {
			throw new UncheckedJannovarException("Could not load FAI file", e);
		}

		final StringBuilder builder = new StringBuilder();
		for (int i = 0; i < buffer.length; ++i) {
			if (!Character.isSpaceChar(buffer[i])) {
				builder.append(buffer[i]);
			}
		}
		return builder.toString();
	}

	/**
	 * Record in an FAI file.
	 */
	private static final class FAIRecord {

		/** Contig name. */
		final String name;

		/** Length of the sequence. */
		final int length;

		/** Offset of first sequence base in file. */
		final long offset;

		/** Number of basese per line. */
		final int lineBases;

		/** Characters in each line. */
		final int lineWidth;

		/**
		 * Construct the {@code FAIRecord}.
		 */
		public FAIRecord(String name, int length, long offset, int lineBases, int lineWidth) {
			this.name = name;
			this.length = length;
			this.offset = offset;
			this.lineBases = lineBases;
			this.lineWidth = lineWidth;
		}
	}

}
