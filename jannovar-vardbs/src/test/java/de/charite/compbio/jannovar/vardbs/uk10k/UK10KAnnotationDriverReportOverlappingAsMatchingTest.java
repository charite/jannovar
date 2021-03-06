package de.charite.compbio.jannovar.vardbs.uk10k;

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
 * Test for annotation with UK10K reporting overlaps as matches
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class UK10KAnnotationDriverReportOverlappingAsMatchingTest extends UK10KAnnotationDriverBaseTest {

	@BeforeEach
	public void setUpClass() throws Exception {
		super.setUpClass();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(true);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		options.setIdentifierPrefix("UK10K_");
		UK10KAnnotationDriver driver = new UK10KAnnotationDriver(dbUK10KVCFPath, fastaPath, options);

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
		Assertions.assertEquals(3, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(3, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		Assertions.assertNotNull(header.getInfoHeaderLine("UK10K_AC"));
		Assertions.assertNotNull(header.getInfoHeaderLine("UK10K_AN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("UK10K_AF"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		UK10KAnnotationDriver driver = new UK10KAnnotationDriver(dbUK10KVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assertions.assertEquals(0, vc.getAttributes().size());
		Assertions.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assertions.assertEquals(".", annotated.getID());

		Assertions.assertEquals(3, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assertions.assertEquals("[AC, AF, AN]", keys.toString());

		Assertions.assertEquals("[5, 5, 5]", annotated.getAttributeAsString("AC", null));
		Assertions.assertEquals("[6.612007405448294E-4, 6.612007405448294E-4, 6.612007405448294E-4]",
			annotated.getAttributeAsString("AF", null));
		Assertions.assertEquals("7562", annotated.getAttributeAsString("AN", null));
	}

}
