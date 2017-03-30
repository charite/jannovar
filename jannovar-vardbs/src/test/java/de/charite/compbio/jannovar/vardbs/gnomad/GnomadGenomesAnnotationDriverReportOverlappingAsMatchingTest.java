package de.charite.compbio.jannovar.vardbs.gnomad;

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
 * Test for annotation with ExAC with default options
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GnomadGenomesAnnotationDriverReportOverlappingAsMatchingTest
		extends GnomadGenomesAnnotationDriverBaseTest {

	@Before
	public void setUpClass() throws Exception {
		super.setUpClass();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(true);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		GnomadAnnotationDriver driver = new GnomadAnnotationDriver(gnomadVCFPath, fastaPath, options);

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
		Assert.assertEquals(61, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(61, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_ALL"));

		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_AFR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_AMR"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_EAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_FIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_NFE"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_OTH"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_SAS"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_ALL"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_ALL"));

		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_POPMAX"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_POPMAX"));
		Assert.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_POPMAX"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		GnomadAnnotationDriver driver = new GnomadAnnotationDriver(gnomadVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assert.assertEquals(0, vc.getAttributes().size());
		Assert.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assert.assertEquals(".", annotated.getID());

		Assert.assertEquals(56, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assert.assertEquals(
				"[AC_AFR, AC_ALL, AC_AMR, AC_ASJ, AC_EAS, AC_FIN, AC_NFE, AC_OTH, AC_POPMAX, AF_AFR, "
						+ "AF_ALL, AF_AMR, AF_ASJ, AF_EAS, AF_FIN, AF_NFE, AF_OTH, AF_POPMAX, AN_AFR, AN_ALL, "
						+ "AN_AMR, AN_ASJ, AN_EAS, AN_FIN, AN_NFE, AN_OTH, AN_POPMAX, AN_SAS, HEMI_AFR, HEMI_ALL, "
						+ "HEMI_AMR, HEMI_ASJ, HEMI_EAS, HEMI_FIN, HEMI_NFE, HEMI_OTH, HEMI_POPMAX, HET_AFR, "
						+ "HET_ALL, HET_AMR, HET_ASJ, HET_EAS, HET_FIN, HET_NFE, HET_OTH, HET_POPMAX, HOM_AFR, "
						+ "HOM_ALL, HOM_AMR, HOM_ASJ, HOM_EAS, HOM_FIN, HOM_NFE, HOM_OTH, HOM_POPMAX, POPMAX]",
				keys.toString());

		Assert.assertEquals("[210, 210, 210]", annotated.getAttributeAsString("AC_AFR", null));
		Assert.assertEquals("[303, 303, 303]", annotated.getAttributeAsString("AC_ALL", null));
		Assert.assertEquals("[1, 1, 1]", annotated.getAttributeAsString("AC_AMR", null));
		Assert.assertEquals("[39, 39, 39]", annotated.getAttributeAsString("AC_EAS", null));
		Assert.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_FIN", null));
		Assert.assertEquals("[46, 46, 46]", annotated.getAttributeAsString("AC_NFE", null));
		Assert.assertEquals("[6, 6, 6]", annotated.getAttributeAsString("AC_OTH", null));
		Assert.assertNull(annotated.getAttributeAsString("AC_SAS", null));

		Assert.assertEquals("[0.3488372093023256, 0.3488372093023256, 0.3488372093023256]",
				annotated.getAttributeAsString("AF_AFR", null));
		Assert.assertEquals("[0.04265202702702703, 0.04265202702702703, 0.04265202702702703]",
				annotated.getAttributeAsString("AF_ALL", null));
		Assert.assertEquals("[0.013888888888888888, 0.013888888888888888, 0.013888888888888888]",
				annotated.getAttributeAsString("AF_AMR", null));
		Assert.assertEquals("[0.5571428571428572, 0.5571428571428572, 0.5571428571428572]",
				annotated.getAttributeAsString("AF_EAS", null));
		Assert.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_FIN", null));
		Assert.assertEquals("[0.02346938775510204, 0.02346938775510204, 0.02346938775510204]",
				annotated.getAttributeAsString("AF_NFE", null));
		Assert.assertEquals("[0.061224489795918366, 0.061224489795918366, 0.061224489795918366]",
				annotated.getAttributeAsString("AF_OTH", null));
		Assert.assertNull(annotated.getAttributeAsString("AF_SAS", null));

		Assert.assertEquals("[602]", annotated.getAttributeAsString("AN_AFR", null));
		Assert.assertEquals("[7104]", annotated.getAttributeAsString("AN_ALL", null));
		Assert.assertEquals("[72]", annotated.getAttributeAsString("AN_AMR", null));
		Assert.assertEquals("[70]", annotated.getAttributeAsString("AN_EAS", null));
		Assert.assertEquals("[714]", annotated.getAttributeAsString("AN_FIN", null));
		Assert.assertEquals("[1960]", annotated.getAttributeAsString("AN_NFE", null));
		Assert.assertEquals("[98]", annotated.getAttributeAsString("AN_OTH", null));
		Assert.assertEquals("[0]", annotated.getAttributeAsString("AN_SAS", null));
	}

}
