package jannovar.io;

import jannovar.common.Immutable;
import jannovar.reference.TranscriptInfo;

import java.io.Serializable;

import com.google.common.collect.ImmutableList;

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
	public final ReferenceDictionary referenceDict;

	/**
	 * Initialize the object with the given values.
	 * 
	 * @param transcriptInfos
	 *            the list of {@link TranscriptInfo} objects to use in this object
	 * @param referenceDict
	 *            the {@link ReferenceDictionary} to use in this object
	 */
	public JannovarData(ImmutableList<TranscriptInfo> transcriptInfos, ReferenceDictionary referenceDict) {
		this.transcriptInfos = transcriptInfos;
		this.referenceDict = referenceDict;
	}

}
