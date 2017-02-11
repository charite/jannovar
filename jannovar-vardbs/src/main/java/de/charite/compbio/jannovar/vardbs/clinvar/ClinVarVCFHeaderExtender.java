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
        VCFInfoHeaderLine infoClinVarBasicInfo = new VCFInfoHeaderLine(prefix + infix + "BASIC_INFO",
                VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String,
                "Annotation of basic info with the form 'allele | hgvs string | origin'" + note);
        header.addMetaDataLine(infoClinVarBasicInfo);

        VCFInfoHeaderLine infoClinVarVarInfo = new VCFInfoHeaderLine(prefix + infix + "VAR_INFO",
                VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String,
                "Annotation of variant source information of the form 'allele | db name | id in db'" + note);
        header.addMetaDataLine(infoClinVarVarInfo);
        
        VCFInfoHeaderLine infoClinVarDiseaseInfo = new VCFInfoHeaderLine(prefix + infix + "DISEASE_INFO",
                VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String,
                "Annotation of disease information of the form 'allele | significance | disease db | id in disease db "
                        + "| name in disease db | revision status | clinical accession'" + note);
        header.addMetaDataLine(infoClinVarDiseaseInfo);
    }

}