package de.charite.compbio.jannovar.vardbs.exac;

import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.vcf.VCFHeader;

/**
 * Helper class for extending {@link VCFHeader}s for ExAC annotations.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class ExacVCFHeaderExtender extends VCFHeaderExtender {

	@Override
	public String getDefaultPrefix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addHeaders(VCFHeader header, String prefix) {
		// TODO Auto-generated method stub

	}

}
