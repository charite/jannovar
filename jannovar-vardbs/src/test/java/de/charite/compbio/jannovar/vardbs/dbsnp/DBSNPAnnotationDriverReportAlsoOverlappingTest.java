package de.charite.compbio.jannovar.vardbs.dbsnp;

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
 * Test for annotation with dbSNP with also reporting overlapping
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DBSNPAnnotationDriverReportAlsoOverlappingTest extends DBSNPAnnotationDriverBaseTest {

	@Before
	public void setUpClass() throws Exception {
		super.setUpClass();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(false);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		DBSNPAnnotationDriver driver = new DBSNPAnnotationDriver(dbSNPVCFPath, fastaPath, options);

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
		Assert.assertEquals(12, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(12, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_COMMON"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_CAF"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_G5"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_G5A"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_IDS"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_SAO"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_COMMON"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_CAF"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_G5"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_G5A"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_IDS"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_SAO"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		DBSNPAnnotationDriver driver = new DBSNPAnnotationDriver(dbSNPVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assert.assertEquals(0, vc.getAttributes().size());
		Assert.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assert.assertEquals("rs540538026", annotated.getID());

		Assert.assertEquals(8, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assert.assertEquals("[CAF, COMMON, G5, IDS, OVL_CAF, OVL_COMMON, OVL_G5, OVL_IDS]",
				keys.toString());

		Assert.assertEquals("[0.97324, 0.02676, 0.0, 0.0]", annotated.getAttributeAsString("CAF", null));
		Assert.assertEquals("[1, 0, 0]", annotated.getAttributeAsString("G5", null));
		Assert.assertNull(annotated.getAttributeAsString("G5A", null));
		Assert.assertEquals("[1, 0, 0]", annotated.getAttributeAsString("COMMON", null));
		Assert.assertEquals("[rs540538026, ., .]", annotated.getAttributeAsString("IDS", null));

		Assert.assertEquals("[0.91972, 0.02676, 0.02676, 0.02676]", annotated.getAttributeAsString("OVL_CAF", null));
		Assert.assertEquals("[1, 1, 1]", annotated.getAttributeAsString("OVL_G5", null));
		Assert.assertNull(annotated.getAttributeAsString("OVL_G5A", null));
		Assert.assertEquals("[1, 1, 1]", annotated.getAttributeAsString("OVL_COMMON", null));
		Assert.assertEquals("[rs540538026, rs540538026, rs540538026]", annotated.getAttributeAsString("OVL_IDS", null));
	}

}
