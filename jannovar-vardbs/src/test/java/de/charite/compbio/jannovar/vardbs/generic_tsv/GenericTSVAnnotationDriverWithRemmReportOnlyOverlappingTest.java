package de.charite.compbio.jannovar.vardbs.generic_tsv;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFHeader;

/**
 * Test for annotation with dbNSFP with default options
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericTSVAnnotationDriverWithRemmReportOnlyOverlappingTest
		extends GenericTSVAnnotationDriverWithRemmBaseTest {

	@Before
	public void setUp() throws Exception {
		super.setUp();
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
		Assert.assertEquals(1, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(1, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("REMM_SCORE"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		GenericTSVAnnotationDriver driver = new GenericTSVAnnotationDriver(fastaPath, options);
		// Annotation of first variant
		final CloseableIterator<VariantContext> iter = vcfReader.iterator();
		VariantContext vcFirst = iter.next();
		Assert.assertEquals(vcFirst.getStart(), 10026);

		Assert.assertEquals(0, vcFirst.getAttributes().size());
		Assert.assertEquals(".", vcFirst.getID());

		VariantContext annotatedFirst = driver.annotateVariantContext(vcFirst);

		Assert.assertEquals(".", annotatedFirst.getID());

		Assert.assertEquals(1, annotatedFirst.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotatedFirst.getAttributes().keySet());
		Collections.sort(keys);
		Assert.assertEquals("[REMM_SCORE]", keys.toString());

		Assert.assertEquals("[., 0.102, 0.102, 0.102]",
				annotatedFirst.getAttributeAsString("REMM_SCORE", null));

		// Annotation of second variant
		VariantContext vcSecond = iter.next();
		Assert.assertEquals(vcSecond.getStart(), 10032);

		Assert.assertEquals(0, vcFirst.getAttributes().size());
		Assert.assertEquals(".", vcFirst.getID());

		VariantContext annotatedSecond = driver.annotateVariantContext(vcSecond);

		Assert.assertEquals(".", annotatedSecond.getID());

		Assert.assertEquals(1, annotatedSecond.getAttributes().size());
		ArrayList<String> keysSecond = Lists.newArrayList(annotatedSecond.getAttributes().keySet());
		Collections.sort(keysSecond);
		Assert.assertEquals("[REMM_SCORE]", keys.toString());

		Assert.assertEquals("[., 0.104]", annotatedSecond.getAttributeAsString("REMM_SCORE", null));
	}

}
