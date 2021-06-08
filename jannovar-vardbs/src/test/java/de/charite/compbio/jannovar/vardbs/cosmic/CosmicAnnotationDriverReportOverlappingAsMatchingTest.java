package de.charite.compbio.jannovar.vardbs.cosmic;

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
 * Test for annotation with COSMIC reporting overlaps as matches
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class CosmicAnnotationDriverReportOverlappingAsMatchingTest extends CosmicAnnotationDriverBaseTest {

	@BeforeEach
	public void setUpClass() throws Exception {
		super.setUpClass();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(true);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		options.setIdentifierPrefix("COSMIC_");
		CosmicAnnotationDriver driver = new CosmicAnnotationDriver(cosmicVCFPath, fastaPath, options);

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

		Assertions.assertNotNull(header.getInfoHeaderLine("COSMIC_CNT"));
		Assertions.assertNotNull(header.getInfoHeaderLine("COSMIC_SNP"));
		Assertions.assertNotNull(header.getInfoHeaderLine("COSMIC_IDS"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		CosmicAnnotationDriver driver = new CosmicAnnotationDriver(cosmicVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assertions.assertEquals(0, vc.getAttributes().size());
		Assertions.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assertions.assertEquals("COSM814119", annotated.getID());

		Assertions.assertEquals(3, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assertions.assertEquals("[CNT, IDS, SNP]", keys.toString());

		Assertions.assertEquals("34", annotated.getAttributeAsString("CNT", null));
		Assertions.assertEquals("[COSM814119, COSM814119, COSM814119]",
			annotated.getAttributeAsString("IDS", null));
		Assertions.assertEquals("true", annotated.getAttributeAsString("SNP", "false"));
	}

}
