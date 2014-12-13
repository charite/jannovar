package jannovar.io;

import com.google.common.collect.ImmutableMap;

/**
 * Builder class for incremental building of immutable {@link ReferenceDictionary} objects.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class ReferenceDictionaryBuilder {

	/** builder for {@link ReferenceDictionary#contigID} */
	private ImmutableMap.Builder<String, Integer> contigID = new ImmutableMap.Builder<String, Integer>();

	/** builder for {@link ReferenceDictionary#contigLength} */
	private ImmutableMap.Builder<Integer, Integer> contigLength = new ImmutableMap.Builder<Integer, Integer>();

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
	 * Add a contig name to numeric ID mapping to builder.
	 * 
	 * @param name
	 *            contig name
	 * @param id
	 *            numeric contig ID
	 */
	public void putContigID(String name, int id) {
		contigID.put(name, id);
	}

	/**
	 * @return instance of immutable ReferenceDictionary object
	 */
	public ReferenceDictionary build() {
		return new ReferenceDictionary(contigID.build(), contigLength.build());
	}
}
