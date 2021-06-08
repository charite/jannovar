package de.charite.compbio.jannovar.vardbs.dbsnp;

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
 * Test for annotation with dbSNP with also reporting overlapping
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DBSNPAnnotationDriverReportAlsoOverlappingTest extends DBSNPAnnotationDriverBaseTest {

	@BeforeEach
	public void setUpClass() throws Exception {
		super.setUpClass();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(false);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		options.setIdentifierPrefix("DBSNP_");
		DBSNPAnnotationDriver driver = new DBSNPAnnotationDriver(dbSNPVCFPath, fastaPath, options);

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
		Assertions.assertEquals(12, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(12, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_COMMON"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_CAF"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_G5"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_G5A"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_IDS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_SAO"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_COMMON"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_CAF"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_G5"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_G5A"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_IDS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_SAO"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		DBSNPAnnotationDriver driver = new DBSNPAnnotationDriver(dbSNPVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assertions.assertEquals(0, vc.getAttributes().size());
		Assertions.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assertions.assertEquals("rs540538026", annotated.getID());

		Assertions.assertEquals(8, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assertions.assertEquals("[CAF, COMMON, G5, IDS, OVL_CAF, OVL_COMMON, OVL_G5, OVL_IDS]",
			keys.toString());

		Assertions.assertEquals("[0.97324, 0.02676, 0.0, 0.0]", annotated.getAttributeAsString("CAF", null));
		Assertions.assertEquals("[1, 0, 0]", annotated.getAttributeAsString("G5", null));
		Assertions.assertNull(annotated.getAttributeAsString("G5A", null));
		Assertions.assertEquals("[1, 0, 0]", annotated.getAttributeAsString("COMMON", null));
		Assertions.assertEquals("[rs540538026, ., .]", annotated.getAttributeAsString("IDS", null));

		Assertions.assertEquals("[0.91972, 0.02676, 0.02676, 0.02676]", annotated.getAttributeAsString("OVL_CAF", null));
		Assertions.assertEquals("[1, 1, 1]", annotated.getAttributeAsString("OVL_G5", null));
		Assertions.assertNull(annotated.getAttributeAsString("OVL_G5A", null));
		Assertions.assertEquals("[1, 1, 1]", annotated.getAttributeAsString("OVL_COMMON", null));
		Assertions.assertEquals("[rs540538026, rs540538026, rs540538026]", annotated.getAttributeAsString("OVL_IDS", null));
	}

}
