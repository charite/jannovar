package de.charite.compbio.jannovar.data;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

// NOTE(holtgrem): Part of the public interface of the Jannovar library.

/**
 * Builder class for incremental building of immutable {@link ReferenceDictionary} objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class ReferenceDictionaryBuilder {

	/** interim map for {@link #contigID} */
	private final HashMap<String, Integer> tmpContigID = new HashMap<String, Integer>();

	/** builder for {@link ReferenceDictionary#contigID} */
	private final ImmutableMap.Builder<String, Integer> contigID = new ImmutableMap.Builder<String, Integer>();

	/** interim map for {@link #contigName} */
	private final HashMap<Integer, String> tmpContigName = new HashMap<Integer, String>();

	/** builder for {@link ReferenceDictionary#contigName} */
	private final ImmutableMap.Builder<Integer, String> contigName = new ImmutableMap.Builder<Integer, String>();

	/** interim map for {@link #contigLength} */
	private final HashMap<Integer, Integer> tmpContigLength = new HashMap<Integer, Integer>();

	/** builder for {@link ReferenceDictionary#contigLength} */
	private final ImmutableMap.Builder<Integer, Integer> contigLength = new ImmutableMap.Builder<Integer, Integer>();

	/**
	 * Allows contig length retrieval before final construction of the {@link ReferenceDictionary}.
	 *
	 * @param id
	 *            id of the contig to get the length for
	 * @return length of the contig or <code>null</code> if it could not be found
	 */
	public Integer getContigLength(int id) {
		return tmpContigLength.get(id);
	}

	/**
	 * Add a contig id to length mapping.
	 *
	 * @param id
	 *            numeric contig ID
	 * @param length
	 *            contig length
	 */
	public void putContigLength(int id, int length) {
		tmpContigLength.put(id, length);
		contigLength.put(id, length);
	}

	/**
	 * Allows get contig name from a contig id.
	 *
	 * @param id
	 *            numeric contig ID
	 * @return primary contig name or <code>null</code> if none could be found
	 */
	public String getContigName(Integer id) {
		return tmpContigName.get(id);
	}

	/**
	 * Set primary contig ID to name mapping.
	 *
	 * @param id
	 *            numeric contig ID
	 * @param name
	 *            contig name
	 */
	public void putContigName(int id, String name) {
		tmpContigName.put(id, name);
		contigName.put(id, name);
	}

	/**
	 * Allows contig name to numeric ID translation before final construction of the {@link ReferenceDictionary}.
	 *
	 * @param name
	 *            name of contig to get numeric ID for
	 * @return canonical numeric ID for the contig with given <code>name</code> or <code>null</code> if none.
	 */
	public Integer getContigID(String name) {
		return tmpContigID.get(name);
	}

	/**
	 * Add a contig name to numeric ID mapping to builder.
	 *
	 * @param name
	 *            contig name
	 * @param id
	 *            numeric contig ID
	 */
	public void putContigID(String name, int id) {
		if (getContigID(name) != null)
			return;
		tmpContigID.put(name, id);
		contigID.put(name, id);
	}

	/**
	 * @return instance of immutable ReferenceDictionary object
	 */
	public ReferenceDictionary build() {
		return new ReferenceDictionary(contigID.build(), contigName.build(), contigLength.build());
	}

}
