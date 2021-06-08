package de.charite.compbio.jannovar.vardbs.clinvar;

import com.google.common.collect.Lists;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Test for annotation with ClinVar with default options
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ClinVarAnnotationDriverReportNoOverlappingTest
	extends ClinVarAnnotationDriverBaseTest {

	@BeforeEach
	public void setUpClass() throws Exception {
		super.setUpClass();
		options.setReportOverlapping(false);
		options.setReportOverlappingAsMatching(false);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		options.setIdentifierPrefix("CLINVAR_");
		ClinVarAnnotationDriver driver =
			new ClinVarAnnotationDriver(dbClinVarVCFPath, fastaPath, options);

		VCFHeader header = vcfReader.getFileHeader();

		// Check header before extension
		Assertions.assertEquals(0, header.getFilterLines().size());
		Assertions.assertEquals(0, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(0, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		driver.constructVCFHeaderExtender().addHeaders(header);

		// Check header after extension
		Assertions.assertEquals(0, header.getFilterLines().size());
		Assertions.assertEquals(3, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(3, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		Assertions.assertNotNull(header.getInfoHeaderLine("CLINVAR_BASIC_INFO"));
		Assertions.assertNotNull(header.getInfoHeaderLine("CLINVAR_VAR_INFO"));
		Assertions.assertNotNull(header.getInfoHeaderLine("CLINVAR_DISEASE_INFO"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		ClinVarAnnotationDriver driver =
			new ClinVarAnnotationDriver(dbClinVarVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assertions.assertEquals(0, vc.getAttributes().size());
		Assertions.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assertions.assertEquals(".", annotated.getID());

		Assertions.assertEquals(3, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assertions.assertEquals("[BASIC_INFO, DISEASE_INFO, VAR_INFO]", keys.toString());

		Assertions.assertEquals(
			"[A|NC_000001.10%3Ag.2160305G%3EA|GERMLINE&DE_NOVO, T|NC_000001.10%3Ag.2160305G%3ET|GERMLINE&DE_NOVO]",
			annotated.getAttributeAsString("BASIC_INFO", null));
		Assertions.assertEquals(
			"[A|pathogenic|MedGen%3AOMIM%3ASNOMED_CT|C1321551%3A182212%3A83092002|Shprintzen-Goldberg_syndrome|"
				+ "single_submitter|RCV000030819.28, A|pathogenic|MedGen|CN221809|not_provided|single_submitter|RCV000200686.1, "
				+ "T|pathogenic|MedGen%3AOMIM%3ASNOMED_CT|C1321551%3A182212%3A83092002|Shprintzen-Goldberg_syndrome|"
				+ "single_submitter|RCV000030820.27, T|pathogenic|MedGen|CN221809|not_provided|single_submitter|RCV000197434.1]",
			annotated.getAttributeAsString("DISEASE_INFO", null));
		Assertions.assertEquals(
			"[A|OMIM_Allelic_Variant|164780.0004|GERMLINE%26DE_NOVO, A|UniProtKB_%28protein%29|P12755%23VAR_071176|GERMLINE%26DE_NOVO, "
				+ "T|OMIM_Allelic_Variant|164780.0005|GERMLINE%26DE_NOVO, T|UniProtKB_%28protein%29|P12755%23VAR_071174|GERMLINE%26DE_NOVO]",
			annotated.getAttributeAsString("VAR_INFO", null));
	}

}
