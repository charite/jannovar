package jannovar.io;

import java.io.Serializable;

import com.google.common.collect.ImmutableMap;

import jannovar.common.Immutable;

/**
 * Stores lengths of contigs/chromosomes and a mapping from string to numeric IDs.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public class ReferenceDictionary implements Serializable {
	
	/** serialization version ID */
	private static final long serialVersionUID = 1L;

	/** stores a mapping from numeric chromosomeID/contigID to chromosome/contig length */
	final ImmutableMap<Integer, Integer> contigLength;
	
	/**
	 * stores a mapping from the string chromosome/contig name to its numeric id, e.g. from both <code>"chr1"</code> and
	 * <code>"1"</code> to <code>(int)1</code>.
	 */
	final ImmutableMap<String, Integer> contigID;

	/**
	 * Initialize the object with the given values.
	 * 
	 * @param contigLength contig length map to use for the initialization
	 * @param contigID contig ID map to use for the initialization
	 */
	public ReferenceDictionary(ImmutableMap<String, Integer> contigID, ImmutableMap<Integer, Integer> contigLength) {
		this.contigID = contigID;
		this.contigLength = contigLength;
	}	
	
}
