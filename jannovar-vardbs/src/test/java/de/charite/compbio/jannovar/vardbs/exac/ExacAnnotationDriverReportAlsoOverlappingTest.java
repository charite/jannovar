package de.charite.compbio.jannovar.vardbs.exac;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;

/**
 * Test for annotation with ExAC with default options
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ExacAnnotationDriverReportAlsoOverlappingTest extends ExacAnnotationDriverBaseTest {

	@Before
	public void setUpClass() throws Exception {
		super.setUpClass();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(false);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		ExacAnnotationDriver driver = new ExacAnnotationDriver(dbExacVCFPath, fastaPath, options);

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
		Assert.assertEquals(100, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(100, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AC_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AF_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_AN_ALL"));

		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HET_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HOM_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_HEMI_ALL"));

		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_BEST_AC"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_BEST_AF"));

		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AC_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AF_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_AN_ALL"));

		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HOM_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HEMI_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_HET_ALL"));

		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_BEST_AC"));
		Assert.assertNotNull(header.getInfoHeaderLine("EXAC_OVL_BEST_AF"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		ExacAnnotationDriver driver = new ExacAnnotationDriver(dbExacVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assert.assertEquals(0, vc.getAttributes().size());
		Assert.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assert.assertEquals(".", annotated.getID());

		Assert.assertEquals(84, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assert.assertEquals("[AC_AFR, AC_ALL, AC_AMR, AC_EAS, AC_FIN, AC_NFE, AC_OTH, AC_SAS, AF_AFR, AF_ALL, "
				+ "AF_AMR, AF_EAS, AF_FIN, AF_NFE, AF_OTH, AF_SAS, AN_AFR, AN_ALL, AN_AMR, AN_EAS, "
				+ "AN_FIN, AN_NFE, AN_OTH, AN_SAS, BEST_AC, BEST_AF, "
				+ "HET_AFR, HET_ALL, HET_AMR, HET_EAS, HET_FIN, HET_NFE, HET_OTH, HET_SAS, "
				+ "HOM_AFR, HOM_ALL, HOM_AMR, HOM_EAS, HOM_FIN, HOM_NFE, HOM_OTH, HOM_SAS, "
				+ "OVL_AC_AFR, OVL_AC_ALL, OVL_AC_AMR, "
				+ "OVL_AC_EAS, OVL_AC_FIN, OVL_AC_NFE, OVL_AC_OTH, OVL_AC_SAS, OVL_AF_AFR, OVL_AF_ALL, "
				+ "OVL_AF_AMR, OVL_AF_EAS, OVL_AF_FIN, OVL_AF_NFE, OVL_AF_OTH, OVL_AF_SAS, OVL_AN_AFR, "
				+ "OVL_AN_ALL, OVL_AN_AMR, OVL_AN_EAS, OVL_AN_FIN, OVL_AN_NFE, OVL_AN_OTH, OVL_AN_SAS, "
				+ "OVL_BEST_AC, OVL_BEST_AF, "
				+ "OVL_HET_AFR, OVL_HET_ALL, OVL_HET_AMR, OVL_HET_EAS, OVL_HET_FIN, OVL_HET_NFE, OVL_HET_OTH, OVL_HET_SAS, "
				+ "OVL_HOM_AFR, OVL_HOM_ALL, OVL_HOM_AMR, OVL_HOM_EAS, OVL_HOM_FIN, OVL_HOM_NFE, OVL_HOM_OTH, OVL_HOM_SAS]", keys.toString());

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

		Assert.assertEquals("[0.0, 0.003194888178913738, 0.0]", annotated.getAttributeAsString("AF_AFR", null));
		Assert.assertEquals("[0.0, 1.7041581458759374E-4, 0.0]", annotated.getAttributeAsString("AF_ALL", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_AMR", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_EAS", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_FIN", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_NFE", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_OTH", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_SAS", null));

		Assert.assertEquals("[0.003194888178913738, 0.003194888178913738, 0.003194888178913738]",
				annotated.getAttributeAsString("OVL_AF_AFR", null));
		Assert.assertEquals("[1.7041581458759374E-4, 1.7041581458759374E-4, 1.7041581458759374E-4]",
				annotated.getAttributeAsString("OVL_AF_ALL", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("OVL_AF_AMR", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("OVL_AF_EAS", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("OVL_AF_FIN", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("OVL_AF_NFE", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("OVL_AF_OTH", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("OVL_AF_SAS", null));

		Assert.assertEquals("626", annotated.getAttributeAsString("AN_AFR", null));
		Assert.assertEquals("11736", annotated.getAttributeAsString("AN_ALL", null));
		Assert.assertEquals("148", annotated.getAttributeAsString("AN_AMR", null));
		Assert.assertEquals("218", annotated.getAttributeAsString("AN_EAS", null));
		Assert.assertEquals("24", annotated.getAttributeAsString("AN_FIN", null));
		Assert.assertEquals("3078", annotated.getAttributeAsString("AN_NFE", null));
		Assert.assertEquals("122", annotated.getAttributeAsString("AN_OTH", null));
		Assert.assertEquals("7520", annotated.getAttributeAsString("AN_SAS", null));

		Assert.assertEquals("626", annotated.getAttributeAsString("OVL_AN_AFR", null));
		Assert.assertEquals("11736", annotated.getAttributeAsString("OVL_AN_ALL", null));
		Assert.assertEquals("148", annotated.getAttributeAsString("OVL_AN_AMR", null));
		Assert.assertEquals("218", annotated.getAttributeAsString("OVL_AN_EAS", null));
		Assert.assertEquals("24", annotated.getAttributeAsString("OVL_AN_FIN", null));
		Assert.assertEquals("3078", annotated.getAttributeAsString("OVL_AN_NFE", null));
		Assert.assertEquals("122", annotated.getAttributeAsString("OVL_AN_OTH", null));
		Assert.assertEquals("7520", annotated.getAttributeAsString("OVL_AN_SAS", null));

		Assert.assertEquals("[0, 2, 0]", annotated.getAttributeAsString("BEST_AC", null));
		Assert.assertEquals("[0.0, 0.003194888178913738, 0.0]", annotated.getAttributeAsString("BEST_AF", null));

		Assert.assertEquals("[2, 2, 2]", annotated.getAttributeAsString("OVL_BEST_AC", null));
		Assert.assertEquals("[0.003194888178913738, 0.003194888178913738, 0.003194888178913738]",
				annotated.getAttributeAsString("OVL_BEST_AF", null));
	}

}
