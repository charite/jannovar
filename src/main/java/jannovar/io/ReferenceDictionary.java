package jannovar.io;

import jannovar.common.Immutable;

import java.io.Serializable;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;

/**
 * Stores lengths of contigs/chromosomes and a mapping from string to numeric IDs.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public class ReferenceDictionary implements Serializable {

	/** serialization version ID */
	private static final long serialVersionUID = 1L;

	/**
	 * stores a mapping from the string chromosome/contig name to its numeric id, e.g. from both <code>"chr1"</code> and
	 * <code>"1"</code> to <code>(int)1</code>.
	 */
	public final ImmutableMap<String, Integer> contigID;

	/** stores primary name for each numeric chromsomomeID/contigID */
	public final ImmutableMap<Integer, String> contigName;

	/** stores a mapping from numeric chromosomeID/contigID to chromosome/contig length */
	public final ImmutableMap<Integer, Integer> contigLength;

	/**
	 * Initialize the object with the given values.
	 *
	 * @param contigID
	 *            contig ID map to use for the initialization
	 * @param contigName
	 *            mapping from numeric contigID to contig name
	 * @param contigLength
	 *            contig length map to use for the initialization
	 */
	public ReferenceDictionary(ImmutableMap<String, Integer> contigID, ImmutableMap<Integer, String> contigName,
			ImmutableMap<Integer, Integer> contigLength) {
		this.contigID = contigID;
		this.contigName = contigName;
		this.contigLength = contigLength;
	}

	/**
	 * Print dictionary to <code>System.err</code> for debugging purposes.
	 */
	public void print() {
		System.err.println("contig ID mapping");
		for (Entry<String, Integer> entry : contigID.entrySet())
			System.err.println("\t" + entry.getKey() + " -> " + entry.getValue());
		System.err.println("contig ID mapping");
		for (Entry<Integer, Integer> entry : contigLength.entrySet())
			System.err.println("\t" + entry.getKey() + " -> " + entry.getValue());
		System.err.println("contig name mapping");
		for (Entry<Integer, String> entry : contigName.entrySet())
			System.err.println("\t" + entry.getKey() + " -> " + entry.getValue());
	}

}
