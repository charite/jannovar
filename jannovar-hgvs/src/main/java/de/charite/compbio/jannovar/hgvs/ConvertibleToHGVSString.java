package de.charite.compbio.jannovar.hgvs;

import de.charite.compbio.jannovar.hgvs.protein.ProteinPointLocation;

/**
 * Provide HGVS string representation of an element.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public interface ConvertibleToHGVSString {

	/**
	 * Return HGVS representation in three-letter amino acid code.
	 *
	 * @return HGVS representation, e.g. "133L" for an {@link ProteinPointLocation}
	 */
	public String toHGVSString();

	/**
	 * Return HGVS representation using the given amino acid code.
	 *
	 * @param code
	 *            {@link AminoAcidCode} to use
	 * @return HGVS representation, e.g. "133L" for an {@link ProteinPointLocation}
	 */
	public String toHGVSString(AminoAcidCode code);

}
