package de.charite.compbio.jannovar.vardbs.gnomad;

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
 * Test for annotation with ExAC with default options
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GnomadExomesAnnotationDriverReportNoOverlappingTest extends GnomadExomesAnnotationDriverBaseTest {

	@BeforeEach
	public void setUpClass() throws Exception {
		super.setUpClass();
		options.setReportOverlapping(false);
		options.setReportOverlappingAsMatching(false);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		options.setIdentifierPrefix("GNOMAD_");
		GnomadAnnotationDriver driver = new GnomadAnnotationDriver(gnomadVCFPath, fastaPath, options);

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
		Assertions.assertEquals(61, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(61, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_SAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_ALL"));

		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_SAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_ALL"));

		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_POPMAX"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_POPMAX"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_POPMAX"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_POPMAX"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		options.setIdentifierPrefix("");
		GnomadAnnotationDriver driver = new GnomadAnnotationDriver(gnomadVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assertions.assertEquals(0, vc.getAttributes().size());
		Assertions.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assertions.assertEquals(".", annotated.getID());

		Assertions.assertEquals(61, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assertions.assertEquals("[AC_AFR, AC_ALL, AC_AMR, AC_ASJ, AC_EAS, AC_FIN, AC_NFE, AC_OTH, AC_POPMAX, "
			+ "AC_SAS, AF_AFR, AF_ALL, AF_AMR, AF_ASJ, AF_EAS, AF_FIN, AF_NFE, AF_OTH, "
			+ "AF_POPMAX, AF_SAS, AN_AFR, AN_ALL, AN_AMR, AN_ASJ, AN_EAS, AN_FIN, AN_NFE, "
			+ "AN_OTH, AN_POPMAX, AN_SAS, HEMI_AFR, HEMI_ALL, HEMI_AMR, "
			+ "HEMI_ASJ, HEMI_EAS, HEMI_FIN, HEMI_NFE, HEMI_OTH, HEMI_POPMAX, HEMI_SAS, "
			+ "HET_AFR, HET_ALL, HET_AMR, HET_ASJ, HET_EAS, HET_FIN, HET_NFE, HET_OTH, "
			+ "HET_POPMAX, HET_SAS, HOM_AFR, HOM_ALL, HOM_AMR, HOM_ASJ, HOM_EAS, HOM_FIN, "
			+ "HOM_NFE, HOM_OTH, HOM_POPMAX, HOM_SAS, POPMAX]", keys.toString());

		Assertions.assertEquals("[0, 2, 0]", annotated.getAttributeAsString("AC_AFR", null));
		Assertions.assertEquals("[0, 2, 0]", annotated.getAttributeAsString("AC_ALL", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_AMR", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_ASJ", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_EAS", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_FIN", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_NFE", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_OTH", null));
		Assertions.assertEquals("[0, 2, 0]", annotated.getAttributeAsString("AC_POPMAX", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_SAS", null));

		Assertions.assertEquals("[0.0, 2.7533039647577095E-4, 0.0]", annotated.getAttributeAsString("AF_AFR", null));
		Assertions.assertEquals("[0.0, 7.552756000664643E-6, 0.0]", annotated.getAttributeAsString("AF_ALL", null));
		Assertions.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_AMR", null));
		Assertions.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_ASJ", null));
		Assertions.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_EAS", null));
		Assertions.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_FIN", null));
		Assertions.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_NFE", null));
		Assertions.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_OTH", null));
		Assertions.assertEquals("[0.0, 2.7533039647577095E-4, 0.0]", annotated.getAttributeAsString("AF_POPMAX", null));
		Assertions.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_SAS", null));

		Assertions.assertEquals("[7264]", annotated.getAttributeAsString("AN_AFR", null));
		Assertions.assertEquals("[264804]", annotated.getAttributeAsString("AN_ALL", null));
		Assertions.assertEquals("[22606]", annotated.getAttributeAsString("AN_AMR", null));
		Assertions.assertEquals("[7918]", annotated.getAttributeAsString("AN_ASJ", null));
		Assertions.assertEquals("[10268]", annotated.getAttributeAsString("AN_EAS", null));
		Assertions.assertEquals("[7648]", annotated.getAttributeAsString("AN_FIN", null));
		Assertions.assertEquals("[50484]", annotated.getAttributeAsString("AN_NFE", null));
		Assertions.assertEquals("[3568]", annotated.getAttributeAsString("AN_OTH", null));
		Assertions.assertEquals("[0, 7264]", annotated.getAttributeAsString("AN_POPMAX", null));
		Assertions.assertEquals("[22646]", annotated.getAttributeAsString("AN_SAS", null));

		Assertions.assertEquals("[0, 2, 0]", annotated.getAttributeAsString("HET_AFR", null));
		Assertions.assertEquals("[0, 2, 0]", annotated.getAttributeAsString("HET_ALL", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HET_AMR", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HET_ASJ", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HET_EAS", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HET_FIN", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HET_NFE", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HET_OTH", null));
		Assertions.assertEquals("[0, 2, 0]", annotated.getAttributeAsString("HET_POPMAX", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HET_SAS", null));

		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_AFR", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_ALL", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_AMR", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_ASJ", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_EAS", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_FIN", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_NFE", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_OTH", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_POPMAX", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_SAS", null));

		Assertions.assertEquals("[., AFR, .]", annotated.getAttributeAsString("POPMAX", null));
	}

}
