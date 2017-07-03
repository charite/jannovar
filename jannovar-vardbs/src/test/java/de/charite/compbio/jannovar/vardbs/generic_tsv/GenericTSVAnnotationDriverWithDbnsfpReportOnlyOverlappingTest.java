package de.charite.compbio.jannovar.vardbs.generic_tsv;

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
 * Test for annotation with dbNSFP with default options
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericTSVAnnotationDriverWithDbnsfpReportOnlyOverlappingTest
		extends GenericTSVAnnotationDriverWithDbnsfpBaseTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(true);
		options.setIdentifierPrefix("DBNSFP_");
	}

	@Test
	public void testAnnotateExtendHeader() throws JannovarVarDBException {
		GenericTSVAnnotationDriver driver = new GenericTSVAnnotationDriver(fastaPath, options);

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
		Assert.assertEquals(4, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(4, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("DBNSFP_AAREF"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBNSFP_RS_DBSNP147"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBNSFP_HG19POS"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBNSFP_SIFT_SCORE"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		GenericTSVAnnotationDriver driver = new GenericTSVAnnotationDriver(fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assert.assertEquals(0, vc.getAttributes().size());
		Assert.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assert.assertEquals(".", annotated.getID());

		Assert.assertEquals(4, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assert.assertEquals("[DBNSFP_AAREF, DBNSFP_HG19POS, DBNSFP_RS_DBSNP147, DBNSFP_SIFT_SCORE]",
				keys.toString());

		Assert.assertEquals("[., L, L, L]", annotated.getAttributeAsString("DBNSFP_AAREF", null));
		Assert.assertEquals("[., 69119, 69119, 69119]",
				annotated.getAttributeAsString("DBNSFP_HG19POS", null));
		Assert.assertEquals("[., ., ., .]",
				annotated.getAttributeAsString("DBNSFP_RS_DBSNP147", null));
		Assert.assertEquals("[., 0.0, 0.0, 0.0]",
				annotated.getAttributeAsString("DBNSFP_SIFT_SCORE", null));
	}

}
