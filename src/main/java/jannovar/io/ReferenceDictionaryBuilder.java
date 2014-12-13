package jannovar.io;

import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

/**
 * Builder class for incremental building of immutable {@link ReferenceDictionary} objects.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class ReferenceDictionaryBuilder {

	/** interim map for {@link #contigID} */
	private final HashMap<String, Integer> tmpContigID = new HashMap<String, Integer>();

	/** builder for {@link ReferenceDictionary#contigID} */
	private final ImmutableMap.Builder<String, Integer> contigID = new ImmutableMap.Builder<String, Integer>();

	/** builder for {@link ReferenceDictionary#contigLength} */
	private final ImmutableMap.Builder<Integer, Integer> contigLength = new ImmutableMap.Builder<Integer, Integer>();

	/**
	 * Add a contig id to length mapping.
	 * 
	 * @param id
	 *            numeric contig ID
	 * @param length
	 *            contig length
	 */
	public void putContigLength(int id, int length) {
		contigLength.put(id, length);
	}

	/**
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
		tmpContigID.put(name, id);
		contigID.put(name, id);
	}

	/**
	 * @return instance of immutable ReferenceDictionary object
	 */
	public ReferenceDictionary build() {
		return new ReferenceDictionary(contigID.build(), contigLength.build());
	}
}
