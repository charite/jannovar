package de.charite.compbio.jannovar.vardbs.clinvar;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;

/**
 * Test for annotation with ClinVar with default options
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ClinVarAnnotationDriverReportNoOverlappingTest extends ClinVarAnnotationDriverBaseTest {

    @Before
    public void setUpClass() throws Exception {
        super.setUpClass();
        options.setReportOverlapping(false);
        options.setReportOverlappingAsMatching(false);
    }

    @Test
    public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
        ClinVarAnnotationDriver driver = new ClinVarAnnotationDriver(dbClinVarVCFPath, fastaPath, options);

        VCFHeader header = vcfReader.getFileHeader();

        // Check header before extension
        Assert.assertEquals(0, header.getFilterLines().size());
        Assert.assertEquals(0, header.getInfoHeaderLines().size());
        Assert.assertEquals(0, header.getFormatHeaderLines().size());
        Assert.assertEquals(0, header.getIDHeaderLines().size());
        Assert.assertEquals(0, header.getOtherHeaderLines().size());

        driver.constructVCFHeaderExtender().addHeaders(header);

        // Check header after extension
        Assert.assertEquals(0, header.getFilterLines().size());
        Assert.assertEquals(3, header.getInfoHeaderLines().size());
        Assert.assertEquals(0, header.getFormatHeaderLines().size());
        Assert.assertEquals(3, header.getIDHeaderLines().size());
        Assert.assertEquals(0, header.getOtherHeaderLines().size());

        Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_BASIC_INFO"));
        Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_VAR_INFO"));
        Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_DISEASE_INFO"));
    }

    @Test
    public void testAnnotateVariantContext() throws JannovarVarDBException {
        ClinVarAnnotationDriver driver = new ClinVarAnnotationDriver(dbClinVarVCFPath, fastaPath, options);
        VariantContext vc = vcfReader.iterator().next();

        Assert.assertEquals(0, vc.getAttributes().size());
        Assert.assertEquals(".", vc.getID());

        VariantContext annotated = driver.annotateVariantContext(vc);

        Assert.assertEquals(".", annotated.getID());

        Assert.assertEquals(3, annotated.getAttributes().size());
        ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
        Collections.sort(keys);
        Assert.assertEquals("[BASIC_INFO, DISEASE_INFO, VAR_INFO]", keys.toString());

        Assert.assertEquals("[A|NC_000001.10%3Ag.2160305G%3EA|SOMATIC, T|NC_000001.10%3Ag.2160305G%3ET|SOMATIC]",
                annotated.getAttributeAsString("BASIC_INFO", null));
        Assert.assertEquals(
                "[A|pathogenic|MedGen%3AOMIM%3ASNOMED_CT|C1321551%3A182212%3A83092002|Shprintzen-Goldberg_syndrome|"
                        + "single_submitter|RCV000030819.28, A|pathogenic|MedGen|CN221809|not_provided|single_submitter|RCV000200686.1, "
                        + "T|pathogenic|MedGen%3AOMIM%3ASNOMED_CT|C1321551%3A182212%3A83092002|Shprintzen-Goldberg_syndrome|"
                        + "single_submitter|RCV000030820.27, T|pathogenic|MedGen|CN221809|not_provided|single_submitter|RCV000197434.1]",
                annotated.getAttributeAsString("DISEASE_INFO", null));
        Assert.assertEquals(
                "[A|OMIM_Allelic_Variant|164780.0004|SOMATIC, A|UniProtKB_%28protein%29|P12755%23VAR_071176|SOMATIC, "
                        + "T|OMIM_Allelic_Variant|164780.0005|SOMATIC, T|UniProtKB_%28protein%29|P12755%23VAR_071174|SOMATIC]",
                annotated.getAttributeAsString("VAR_INFO", null));
    }

}
