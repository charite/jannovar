package de.charite.compbio.jannovar.vardbs.exac;

import java.util.HashMap;
import java.util.List;

import de.charite.compbio.jannovar.vardbs.base.AbstractDBAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.GenotypeMatch;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.variantcontext.VariantContext;

// TODO: handle MNVs appropriately

/**
 * Annotation driver class for annotations using ExAC
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class ExacAnnotationDriver extends AbstractDBAnnotationDriver<ExacRecord> {

	public ExacAnnotationDriver(String vcfPath, String fastaPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		super(vcfPath, fastaPath, options, new ExacVariantContextToRecordConverter());
	}

	@Override
	public VCFHeaderExtender constructVCFHeaderExtender() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VariantContext annotateVariantContext(VariantContext vc) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected HashMap<Integer, ExacRecord> buildAnnotatingDBRecords(List<GenotypeMatch> genotypeMatches) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected VariantContext annotateWithDBRecords(VariantContext vc, HashMap<Integer, ExacRecord> records) {
		// TODO Auto-generated method stub
		return null;
	}

}
