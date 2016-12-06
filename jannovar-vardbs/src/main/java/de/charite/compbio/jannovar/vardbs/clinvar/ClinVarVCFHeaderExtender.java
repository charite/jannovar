package de.charite.compbio.jannovar.vardbs.clinvar;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * Helper class for extending {@link VCFHeader}s for ClinVar annotations.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ClinVarVCFHeaderExtender extends VCFHeaderExtender {

	public ClinVarVCFHeaderExtender(DBAnnotationOptions options) {
		super(options);
	}

	@Override
	public String getDefaultPrefix() {
		return "CLINVAR_";
	}

	@Override
	public void addHeaders(VCFHeader header, String prefix) {
		addHeaders(header, prefix, "", "");
		final String note = " (requiring no genotype match, only position overlap)";
		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching())
			addHeaders(header, prefix, "OVL_", note);
	}

	private void addHeaders(VCFHeader header, String prefix, String infix, String note) {
		VCFInfoHeaderLine infoClinVarHGVS = new VCFInfoHeaderLine(prefix + infix + "HGVS", VCFHeaderLineCount.UNBOUNDED,
				VCFHeaderLineType.String,
				"Variant names from HGVS. The order of these variants corresponds to the order of the info "
						+ "in the other clinical INFO tags" + note);
		header.addMetaDataLine(infoClinVarHGVS);

		VCFInfoHeaderLine infoClinVarAllele = new VCFInfoHeaderLine(prefix + infix + "ALLELE",
				VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.Integer,
				"Variant alleles from REF or ALT columns.  0 is REF, 1 is the first ALT allele, etc.  This is used "
						+ "to match alleles with other corresponding clinical (CLN) INFO tags.  A value of -1 indicates that "
						+ "no allele was found to match a corresponding HGVS allele name" + note);
		header.addMetaDataLine(infoClinVarAllele);

		VCFInfoHeaderLine infoClinVarSource = new VCFInfoHeaderLine(prefix + infix + "SOURCE",
				VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String, "ClinVar source" + note);
		header.addMetaDataLine(infoClinVarSource);

		VCFInfoHeaderLine infoClinVarOrigin = new VCFInfoHeaderLine(prefix + infix + "ORIGIN",
				VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String, "Variant Clinical Chanels" + note);
		header.addMetaDataLine(infoClinVarOrigin);

		VCFInfoHeaderLine infoClinVarSignificance = new VCFInfoHeaderLine(prefix + infix + "SIGNIFICANCE",
				VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String, "Clinical significance" + note);
		header.addMetaDataLine(infoClinVarSignificance);

		VCFInfoHeaderLine infoClinVarDiseaseDB = new VCFInfoHeaderLine(prefix + infix + "DISEASE_DB",
				VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String, "Name of database used for annotation" + note);
		header.addMetaDataLine(infoClinVarDiseaseDB);

		VCFInfoHeaderLine infoClinVarDiseaseDBID = new VCFInfoHeaderLine(prefix + infix + "DISEASE_DB_ID",
				VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String, "Variant ID in database" + note);
		header.addMetaDataLine(infoClinVarDiseaseDBID);

		VCFInfoHeaderLine infoClinVarDiseaseName = new VCFInfoHeaderLine(prefix + infix + "DISEASE_DB_NAME",
				VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String, "Variant disease name in database" + note);
		header.addMetaDataLine(infoClinVarDiseaseName);

		VCFInfoHeaderLine infoClinVarRevisionStatus = new VCFInfoHeaderLine(prefix + infix + "REVISION_STATUS",
				VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String, "Variant revision status" + note);
		header.addMetaDataLine(infoClinVarRevisionStatus);

		VCFInfoHeaderLine infoClinVarClinicalAccession = new VCFInfoHeaderLine(prefix + infix + "CLINICAL_ACCESSION",
				VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String, "Variant accession and version" + note);
		header.addMetaDataLine(infoClinVarClinicalAccession);
	}

}