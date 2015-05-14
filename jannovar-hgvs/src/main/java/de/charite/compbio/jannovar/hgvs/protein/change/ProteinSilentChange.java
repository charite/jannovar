package de.charite.compbio.jannovar.hgvs.protein.change;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;

/**
 * Represents a silent protein-level change, i.e., "p.=".
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class ProteinSilentChange extends ProteinChange {

	public ProteinSilentChange(boolean onlyPredicted) {
		super(onlyPredicted);
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		return wrapIfPredicted("=");
	}

}
