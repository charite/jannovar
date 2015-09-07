package de.charite.compbio.jannovar.hgvs;

/**
 * Type of the sequence that the variant is on.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public enum SequenceType {

	/** coding DNA <code>"c."</code> */
	CODING_DNA,
	/** genomic <code>"g."</code> */
	GENOMIC,
	/** mitochondrial DNA <code>"m."</code> */
	MITOCHONDRIAL_DNA,
	/** non-coding DNA <code>"n."</code> */
	NON_CODING_DNA,
	/** RNA <code>"r."</code> */
	RNA,
	/** protein <code>"p."</code> */
	PROTEIN;

	/** @return matching {@link SequenceType} value for the given <code>prefix</code> */
	public static SequenceType findMatchingForPrefix(String prefix) {
		for (SequenceType st : SequenceType.values())
			if (st.getPrefix().equals(prefix))
				return st;
		throw new IllegalArgumentException("No enum constant has matching prefix \"" + prefix + "\"");
	}

	/** @return prefix for the sequence variant, e.g., <code>"c."</code> or <code>"g."</code>. */
	public String getPrefix() {
		switch (this) {
		case CODING_DNA:
			return "c.";
		case GENOMIC:
			return "g.";
		case MITOCHONDRIAL_DNA:
			return "m.";
		case NON_CODING_DNA:
			return "n.";
		case RNA:
			return "r.";
		case PROTEIN:
			return "p.";
		default:
			throw new RuntimeException("Unexpected SequenceType " + this);
		}
	}

}
