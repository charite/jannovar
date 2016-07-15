package de.charite.compbio.jannovar.vardbs.uk10k;

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
 * Test for annotation with UK10K reporting overlaps besides matches
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class UK10KAnnotationDriverReportAlsoOverlappingTest extends UK10KAnnotationDriverBaseTest {

	@Before
	public void setUpClass() throws Exception {
		super.setUpClass();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(false);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		UK10KAnnotationDriver driver = new UK10KAnnotationDriver(dbUK10KVCFPath, fastaPath, options);

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
		Assert.assertEquals(6, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(6, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_AC"));
		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_AN"));
		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_AF"));
		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_OVL_AC"));
		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_OVL_AN"));
		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_OVL_AF"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		DBAnnotationOptions options = DBAnnotationOptions.createDefaults();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(false);
		UK10KAnnotationDriver driver = new UK10KAnnotationDriver(dbUK10KVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assert.assertEquals(0, vc.getAttributes().size());
		Assert.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assert.assertEquals(".", annotated.getID());

		Assert.assertEquals(6, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assert.assertEquals("[AC, AF, AN, OVL_AC, OVL_AF, OVL_AN]", keys.toString());

		Assert.assertEquals("[0, 0, 5]", annotated.getAttributeAsString("AC", null));
		Assert.assertEquals("[0.0, 0.0, 6.612007405448294E-4]", annotated.getAttributeAsString("AF", null));
		Assert.assertEquals("7562", annotated.getAttributeAsString("AN", null));

		Assert.assertEquals("[5, 5, 5]", annotated.getAttributeAsString("OVL_AC", null));
		Assert.assertEquals("[6.612007405448294E-4, 6.612007405448294E-4, 6.612007405448294E-4]",
				annotated.getAttributeAsString("OVL_AF", null));
		Assert.assertEquals("7562", annotated.getAttributeAsString("OVL_AN", null));
	}

}
