package jannovar.io;

import jannovar.reference.TranscriptInfo;
import jannovar.util.Immutable;

import java.io.Serializable;

import com.google.common.collect.ImmutableList;

// NOTE(holtgrem): Part of the public interface of the Jannovar library.
// TODO(holtgrem): Add the interval trees here.
// TODO(holtgrem): Rename package "jannovar.io" to "jannovar.data"?

/**
 * This data type is used for serialization after downloading.
 *
 * Making this class immutable makes it a convenient serializeable read-only database.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class JannovarData implements Serializable {

	/** Serial version ID. */
	private static final long serialVersionUID = 1L;

	/** transcript models in the database */
	public final ImmutableList<TranscriptInfo> transcriptInfos;

	/** information about reference lengths and identities */
	public final ReferenceDictionary refDict;

	/**
	 * Initialize the object with the given values.
	 *
	 * @param referenceDict
	 *            the {@link ReferenceDictionary} to use in this object
	 * @param transcriptInfos
	 *            the list of {@link TranscriptInfo} objects to use in this object
	 */
	public JannovarData(ReferenceDictionary referenceDict, ImmutableList<TranscriptInfo> transcriptInfos) {
		this.refDict = referenceDict;
		this.transcriptInfos = transcriptInfos;
	}

}
