package de.charite.compbio.jannovar.vardbs.generic_tsv;

import com.google.common.collect.Lists;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.samtools.util.CloseableIterator;
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
public class GenericTSVAnnotationDriverWithRemmReportOnlyOverlappingTest
	extends GenericTSVAnnotationDriverWithRemmBaseTest {

	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
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
		Assertions.assertEquals(1, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(1, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		Assertions.assertNotNull(header.getInfoHeaderLine("REMM_SCORE"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		GenericTSVAnnotationDriver driver = new GenericTSVAnnotationDriver(fastaPath, options);
		// Annotation of first variant
		final CloseableIterator<VariantContext> iter = vcfReader.iterator();
		VariantContext vcFirst = iter.next();
		Assertions.assertEquals(vcFirst.getStart(), 10026);

		Assertions.assertEquals(0, vcFirst.getAttributes().size());
		Assertions.assertEquals(".", vcFirst.getID());

		VariantContext annotatedFirst = driver.annotateVariantContext(vcFirst);

		Assertions.assertEquals(".", annotatedFirst.getID());

		Assertions.assertEquals(1, annotatedFirst.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotatedFirst.getAttributes().keySet());
		Collections.sort(keys);
		Assertions.assertEquals("[REMM_SCORE]", keys.toString());

		Assertions.assertEquals("[., 0.102, 0.102, 0.102]",
			annotatedFirst.getAttributeAsString("REMM_SCORE", null));

		// Annotation of second variant
		VariantContext vcSecond = iter.next();
		Assertions.assertEquals(vcSecond.getStart(), 10032);

		Assertions.assertEquals(0, vcFirst.getAttributes().size());
		Assertions.assertEquals(".", vcFirst.getID());

		VariantContext annotatedSecond = driver.annotateVariantContext(vcSecond);

		Assertions.assertEquals(".", annotatedSecond.getID());

		Assertions.assertEquals(1, annotatedSecond.getAttributes().size());
		ArrayList<String> keysSecond = Lists.newArrayList(annotatedSecond.getAttributes().keySet());
		Collections.sort(keysSecond);
		Assertions.assertEquals("[REMM_SCORE]", keys.toString());

		Assertions.assertEquals("[., 0.104]", annotatedSecond.getAttributeAsString("REMM_SCORE", null));
	}

}
