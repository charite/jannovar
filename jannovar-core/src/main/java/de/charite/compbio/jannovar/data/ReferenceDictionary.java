package de.charite.compbio.jannovar.data;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.Immutable;

// NOTE(holtgrem): Part of the public interface of the Jannovar library.

/**
 * Stores lengths of contigs/chromosomes and a mapping from string to numeric IDs.
 *
 * Often, there are various circulating versions of a genome release. While they all have the same sequence for the
 * chromosomes and contigs (and thus the same length), the names might vary slightly. For example, <a
 * href="http://www.ncbi.nlm.nih.gov/refseq/">RefSeq</a> names the human chromosomes as "1", "2" etc. and UCSC names
 * them "chr1", "chr2" etc. The mitochondrial chromosome is called "MT" by RefSeq and "chrM" by UCSC. Further, sometimes
 * RefSeq identifiers such as "NC_000001.10" are used as contig files when mapping.
 *
 * The purpose of this class is to
 *
 * <ol>
 * <li>Assign numeric identifiers to the chromosome names through {@link #contigID}. Possibly, more than one name is
 * assigned the same numeric id. Usually, numeric ids are assigned starting with 1 (as to correspond to the chromosome
 * names for human/mouse genomes, for example), but arbitrary numeric ids can be assigned (even non-continous sequence).
 * </li>
 * <li>Assign a primary name to each numeric identifier through {@link #contigName}.</li>
 * <li>Assign a length to each contig through {@link #contigLength}. This is used for coordinate transformation from
 * forward to reverse strand. This means that the genome position and interval types keep a reference to a central
 * {@link ReferenceDictionary} object.</li>
 * <ol>
 *
 * This class is immutable which makes it safe to share it between many other objects. You can easily construct objects
 * of its type using {@link ReferenceDictionaryBuilder}.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
@Immutable
public class ReferenceDictionary implements Serializable {

	/** serialization version ID */
	private static final long serialVersionUID = 1L;

	/**
	 * stores a mapping from the string chromosome/contig name to its numeric id, e.g. from both <code>"chr1"</code> and
	 * <code>"1"</code> to <code>(int)1</code>
	 */
	private final ImmutableMap<String, Integer> contigID;

	/** stores primary name for each numeric chromsomomeID/contigID */
	private final ImmutableMap<Integer, String> contigName;

	/** stores a mapping from numeric chromosomeID/contigID to chromosome/contig length */
	private final ImmutableMap<Integer, Integer> contigLength;

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
	ReferenceDictionary(ImmutableMap<String, Integer> contigID, ImmutableMap<Integer, String> contigName,
			ImmutableMap<Integer, Integer> contigLength) {
		this.contigID = contigID;
		this.contigName = contigName;
		this.contigLength = contigLength;
	}

	/** @return map from contig name to contig id */
	public ImmutableMap<String, Integer> getContigNameToID() {
		return contigID;
	}

	/** @return map from numeric contig id to primary name */
	public ImmutableMap<Integer, String> getContigIDToName() {
		return contigName;
	}

	/** @return map from numeric contig id to length */
	public ImmutableMap<Integer, Integer> getContigIDToLength() {
		return contigLength;
	}

	/**
	 * Print dictionary to <code>System.err</code> for debugging purposes.
	 */
	public void print(PrintStream out) {
		out.println("contig ID mapping");
		for (Entry<String, Integer> entry : contigID.entrySet())
			out.println("\t" + entry.getKey() + " -> " + entry.getValue());
		out.println("contigs lengths");
		for (Entry<Integer, Integer> entry : contigLength.entrySet())
			out.println("\t" + entry.getKey() + " -> " + entry.getValue());
		out.println("contig name mapping");
		for (Entry<Integer, String> entry : contigName.entrySet())
			out.println("\t" + entry.getKey() + " -> " + entry.getValue());
	}

}
