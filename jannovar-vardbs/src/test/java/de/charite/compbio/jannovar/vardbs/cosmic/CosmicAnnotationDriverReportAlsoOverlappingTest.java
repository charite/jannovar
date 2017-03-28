package de.charite.compbio.jannovar.vardbs.cosmic;

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
public class CosmicAnnotationDriverReportAlsoOverlappingTest extends CosmicAnnotationDriverBaseTest {

	@Before
	public void setUpClass() throws Exception {
		super.setUpClass();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(false);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		CosmicAnnotationDriver driver = new CosmicAnnotationDriver(cosmicVCFPath, fastaPath, options);

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

		Assert.assertNotNull(header.getInfoHeaderLine("COSMIC_CNT"));
		Assert.assertNotNull(header.getInfoHeaderLine("COSMIC_SNP"));
		Assert.assertNotNull(header.getInfoHeaderLine("COSMIC_IDS"));
		Assert.assertNotNull(header.getInfoHeaderLine("COSMIC_OVL_CNT"));
		Assert.assertNotNull(header.getInfoHeaderLine("COSMIC_OVL_SNP"));
		Assert.assertNotNull(header.getInfoHeaderLine("COSMIC_OVL_IDS"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		DBAnnotationOptions options = DBAnnotationOptions.createDefaults();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(false);
		CosmicAnnotationDriver driver = new CosmicAnnotationDriver(cosmicVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assert.assertEquals(0, vc.getAttributes().size());
		Assert.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assert.assertEquals("COSM814119", annotated.getID());

		Assert.assertEquals(6, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assert.assertEquals("[CNT, IDS, OVL_CNT, OVL_IDS, OVL_SNP, SNP]", keys.toString());

		Assert.assertEquals("34", annotated.getAttributeAsString("CNT", null));
		Assert.assertEquals("[COSM814119, ., .]", annotated.getAttributeAsString("IDS", null));
		Assert.assertEquals("true", annotated.getAttributeAsString("SNP", null));

		Assert.assertEquals("34", annotated.getAttributeAsString("OVL_CNT", null));
		Assert.assertEquals("[COSM814119, ., .]", annotated.getAttributeAsString("OVL_IDS", null));
		Assert.assertEquals("true", annotated.getAttributeAsString("OVL_SNP", null));
	}

}
