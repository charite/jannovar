package de.charite.compbio.jannovar.vardbs.gnomad;

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
 * Test for annotation with gnomAD with default options
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GnomadExomesAnnotationDriverReportAlsoOverlappingTest extends GnomadExomesAnnotationDriverBaseTest {

	@Before
	public void setUpClass() throws Exception {
		super.setUpClass();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(false);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		GnomadAnnotationDriver driver = new GnomadAnnotationDriver(gnomadVCFPath, fastaPath, options);

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
		Assert.assertEquals(122, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(122, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_ALL"));

		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_ALL"));

		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_POPMAX"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_POPMAX"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_POPMAX"));

		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_ALL"));

		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HOM_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HEMI_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_HET_ALL"));

		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AC_POPMAX"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AF_POPMAX"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_OVL_AN_POPMAX"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		GnomadAnnotationDriver driver = new GnomadAnnotationDriver(gnomadVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assert.assertEquals(0, vc.getAttributes().size());
		Assert.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assert.assertEquals(".", annotated.getID());

		Assert.assertEquals(122, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assert.assertEquals(
				"[AC_AFR, AC_ALL, AC_AMR, AC_ASJ, AC_EAS, AC_FIN, AC_NFE, AC_OTH, AC_POPMAX, "
						+ "AC_SAS, AF_AFR, AF_ALL, AF_AMR, AF_ASJ, AF_EAS, AF_FIN, AF_NFE, AF_OTH, "
						+ "AF_POPMAX, AF_SAS, AN_AFR, AN_ALL, AN_AMR, AN_ASJ, AN_EAS, AN_FIN, AN_NFE, "
						+ "AN_OTH, AN_POPMAX, AN_SAS, HEMI_AFR, HEMI_ALL, HEMI_AMR, "
						+ "HEMI_ASJ, HEMI_EAS, HEMI_FIN, HEMI_NFE, HEMI_OTH, HEMI_POPMAX, HEMI_SAS, "
						+ "HET_AFR, HET_ALL, HET_AMR, HET_ASJ, HET_EAS, HET_FIN, HET_NFE, HET_OTH, "
						+ "HET_POPMAX, HET_SAS, HOM_AFR, HOM_ALL, HOM_AMR, HOM_ASJ, HOM_EAS, HOM_FIN, "
						+ "HOM_NFE, HOM_OTH, HOM_POPMAX, HOM_SAS, OVL_AC_AFR, OVL_AC_ALL, OVL_AC_AMR, "
						+ "OVL_AC_ASJ, OVL_AC_EAS, OVL_AC_FIN, OVL_AC_NFE, OVL_AC_OTH, OVL_AC_POPMAX, "
						+ "OVL_AC_SAS, OVL_AF_AFR, OVL_AF_ALL, OVL_AF_AMR, OVL_AF_ASJ, OVL_AF_EAS, "
						+ "OVL_AF_FIN, OVL_AF_NFE, OVL_AF_OTH, OVL_AF_POPMAX, OVL_AF_SAS, OVL_AN_AFR, "
						+ "OVL_AN_ALL, OVL_AN_AMR, OVL_AN_ASJ, OVL_AN_EAS, OVL_AN_FIN, OVL_AN_NFE, "
						+ "OVL_AN_OTH, OVL_AN_POPMAX, OVL_AN_SAS, OVL_HEMI_AFR, "
						+ "OVL_HEMI_ALL, OVL_HEMI_AMR, OVL_HEMI_ASJ, OVL_HEMI_EAS, OVL_HEMI_FIN, OVL_HEMI_NFE, "
						+ "OVL_HEMI_OTH, OVL_HEMI_POPMAX, OVL_HEMI_SAS, OVL_HET_AFR, OVL_HET_ALL, OVL_HET_AMR, "
						+ "OVL_HET_ASJ, OVL_HET_EAS, OVL_HET_FIN, OVL_HET_NFE, OVL_HET_OTH, OVL_HET_POPMAX, "
						+ "OVL_HET_SAS, OVL_HOM_AFR, OVL_HOM_ALL, OVL_HOM_AMR, OVL_HOM_ASJ, OVL_HOM_EAS, "
						+ "OVL_HOM_FIN, OVL_HOM_NFE, OVL_HOM_OTH, OVL_HOM_POPMAX, OVL_HOM_SAS, OVL_POPMAX, POPMAX]",
				keys.toString());

		Assert.assertEquals("[0, 2, 0]", annotated.getAttributeAsString("AC_AFR", null));
		Assert.assertEquals("[0, 2, 0]", annotated.getAttributeAsString("AC_ALL", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_AMR", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_EAS", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_FIN", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_NFE", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_OTH", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_SAS", null));

		Assert.assertEquals("[0, 2, 0]", annotated.getAttributeAsString("HET_AFR", null));
		Assert.assertEquals("[0, 2, 0]", annotated.getAttributeAsString("HET_ALL", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HET_AMR", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HET_EAS", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HET_FIN", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HET_NFE", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HET_OTH", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HET_SAS", null));

		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_AFR", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_ALL", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_AMR", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_EAS", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_FIN", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_NFE", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_OTH", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("HOM_SAS", null));

		Assert.assertEquals("[2, 2, 2]", annotated.getAttributeAsString("OVL_AC_AFR", null));
		Assert.assertEquals("[2, 2, 2]", annotated.getAttributeAsString("OVL_AC_ALL", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_AC_AMR", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_AC_EAS", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_AC_FIN", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_AC_NFE", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_AC_OTH", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_AC_SAS", null));

		Assert.assertEquals("[2, 2, 2]", annotated.getAttributeAsString("OVL_HET_AFR", null));
		Assert.assertEquals("[2, 2, 2]", annotated.getAttributeAsString("OVL_HET_ALL", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HET_AMR", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HET_EAS", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HET_FIN", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HET_NFE", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HET_OTH", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HET_SAS", null));

		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HOM_AFR", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HOM_ALL", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HOM_AMR", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HOM_EAS", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HOM_FIN", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HOM_NFE", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HOM_OTH", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("OVL_HOM_SAS", null));

		Assert.assertEquals("[0.0, 2.7533039647577095E-4, 0.0]", annotated.getAttributeAsString("AF_AFR", null));
		Assert.assertEquals("[0.0, 7.552756000664643E-6, 0.0]", annotated.getAttributeAsString("AF_ALL", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_AMR", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_EAS", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_FIN", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_NFE", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_OTH", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_SAS", null));

		Assert.assertEquals("[2.7533039647577095E-4, 2.7533039647577095E-4, 2.7533039647577095E-4]",
				annotated.getAttributeAsString("OVL_AF_AFR", null));
		Assert.assertEquals("[7.552756000664643E-6, 7.552756000664643E-6, 7.552756000664643E-6]",
				annotated.getAttributeAsString("OVL_AF_ALL", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("OVL_AF_AMR", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("OVL_AF_EAS", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("OVL_AF_FIN", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("OVL_AF_NFE", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("OVL_AF_OTH", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("OVL_AF_SAS", null));

		Assert.assertEquals("[7264]", annotated.getAttributeAsString("AN_AFR", null));
		Assert.assertEquals("[264804]", annotated.getAttributeAsString("AN_ALL", null));
		Assert.assertEquals("[22606]", annotated.getAttributeAsString("AN_AMR", null));
		Assert.assertEquals("[10268]", annotated.getAttributeAsString("AN_EAS", null));
		Assert.assertEquals("[7648]", annotated.getAttributeAsString("AN_FIN", null));
		Assert.assertEquals("[50484]", annotated.getAttributeAsString("AN_NFE", null));
		Assert.assertEquals("[3568]", annotated.getAttributeAsString("AN_OTH", null));
		Assert.assertEquals("[22646]", annotated.getAttributeAsString("AN_SAS", null));

		Assert.assertEquals("[7264]", annotated.getAttributeAsString("OVL_AN_AFR", null));
		Assert.assertEquals("[264804]", annotated.getAttributeAsString("OVL_AN_ALL", null));
		Assert.assertEquals("[22606]", annotated.getAttributeAsString("OVL_AN_AMR", null));
		Assert.assertEquals("[10268]", annotated.getAttributeAsString("OVL_AN_EAS", null));
		Assert.assertEquals("[7648]", annotated.getAttributeAsString("OVL_AN_FIN", null));
		Assert.assertEquals("[50484]", annotated.getAttributeAsString("OVL_AN_NFE", null));
		Assert.assertEquals("[3568]", annotated.getAttributeAsString("OVL_AN_OTH", null));
		Assert.assertEquals("[22646]", annotated.getAttributeAsString("OVL_AN_SAS", null));
	}

}
