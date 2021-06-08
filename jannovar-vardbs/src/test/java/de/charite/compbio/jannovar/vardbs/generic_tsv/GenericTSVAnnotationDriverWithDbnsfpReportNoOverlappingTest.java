package de.charite.compbio.jannovar.vardbs.generic_tsv;

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
 * Test for annotation with dbNSFP with default options
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericTSVAnnotationDriverWithDbnsfpReportNoOverlappingTest
	extends GenericTSVAnnotationDriverWithDbnsfpBaseTest {

	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
		options.setReportOverlapping(false);
		options.setReportOverlappingAsMatching(false);
		options.setIdentifierPrefix("DBNSFP_");
	}

	@Test
	public void testAnnotateExtendHeader() throws JannovarVarDBException {
		GenericTSVAnnotationDriver driver = new GenericTSVAnnotationDriver(fastaPath, options);

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
		Assertions.assertEquals(4, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(4, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		Assertions.assertNotNull(header.getInfoHeaderLine("DBNSFP_AAREF"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBNSFP_RS_DBSNP147"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBNSFP_HG19POS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBNSFP_SIFT_SCORE"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		GenericTSVAnnotationDriver driver = new GenericTSVAnnotationDriver(fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assertions.assertEquals(0, vc.getAttributes().size());
		Assertions.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assertions.assertEquals(".", annotated.getID());

		Assertions.assertEquals(4, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assertions.assertEquals("[DBNSFP_AAREF, DBNSFP_HG19POS, DBNSFP_RS_DBSNP147, DBNSFP_SIFT_SCORE]",
			keys.toString());

		Assertions.assertEquals("[., L, L, L]", annotated.getAttributeAsString("DBNSFP_AAREF", null));
		Assertions.assertEquals("[., 69119, 69119, 69119]",
			annotated.getAttributeAsString("DBNSFP_HG19POS", null));
		Assertions.assertEquals("[., ., ., .]",
			annotated.getAttributeAsString("DBNSFP_RS_DBSNP147", null));
		Assertions.assertEquals("[., 0.0, 0.0, 0.0]",
			annotated.getAttributeAsString("DBNSFP_SIFT_SCORE", null));
	}

}
