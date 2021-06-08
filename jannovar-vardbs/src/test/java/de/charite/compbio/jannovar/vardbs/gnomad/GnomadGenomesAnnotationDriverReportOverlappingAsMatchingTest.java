package de.charite.compbio.jannovar.vardbs.gnomad;

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
 * Test for annotation with ExAC with default options
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GnomadGenomesAnnotationDriverReportOverlappingAsMatchingTest
	extends GnomadGenomesAnnotationDriverBaseTest {

	@BeforeEach
	public void setUp() throws Exception {
		super.setUp();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(true);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		options.setIdentifierPrefix("GNOMAD_");
		GnomadAnnotationDriver driver = new GnomadAnnotationDriver(gnomadVCFPath, fastaPath, options);

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
		Assertions.assertEquals(61, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(61, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_SAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_ALL"));

		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_AFR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_AMR"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_EAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_FIN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_NFE"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_OTH"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_SAS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HOM_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HEMI_ALL"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_HET_ALL"));

		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AC_POPMAX"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AF_POPMAX"));
		Assertions.assertNotNull(header.getInfoHeaderLine("GNOMAD_AN_POPMAX"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		GnomadAnnotationDriver driver = new GnomadAnnotationDriver(gnomadVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assertions.assertEquals(0, vc.getAttributes().size());
		Assertions.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assertions.assertEquals(".", annotated.getID());

		Assertions.assertEquals(56, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assertions.assertEquals(
			"[AC_AFR, AC_ALL, AC_AMR, AC_ASJ, AC_EAS, AC_FIN, AC_NFE, AC_OTH, AC_POPMAX, AF_AFR, "
				+ "AF_ALL, AF_AMR, AF_ASJ, AF_EAS, AF_FIN, AF_NFE, AF_OTH, AF_POPMAX, AN_AFR, AN_ALL, "
				+ "AN_AMR, AN_ASJ, AN_EAS, AN_FIN, AN_NFE, AN_OTH, AN_POPMAX, AN_SAS, HEMI_AFR, HEMI_ALL, "
				+ "HEMI_AMR, HEMI_ASJ, HEMI_EAS, HEMI_FIN, HEMI_NFE, HEMI_OTH, HEMI_POPMAX, HET_AFR, "
				+ "HET_ALL, HET_AMR, HET_ASJ, HET_EAS, HET_FIN, HET_NFE, HET_OTH, HET_POPMAX, HOM_AFR, "
				+ "HOM_ALL, HOM_AMR, HOM_ASJ, HOM_EAS, HOM_FIN, HOM_NFE, HOM_OTH, HOM_POPMAX, POPMAX]",
			keys.toString());

		Assertions.assertEquals("[210, 210, 210]", annotated.getAttributeAsString("AC_AFR", null));
		Assertions.assertEquals("[303, 303, 303]", annotated.getAttributeAsString("AC_ALL", null));
		Assertions.assertEquals("[1, 1, 1]", annotated.getAttributeAsString("AC_AMR", null));
		Assertions.assertEquals("[39, 39, 39]", annotated.getAttributeAsString("AC_EAS", null));
		Assertions.assertEquals("[0, 0, 0]", annotated.getAttributeAsString("AC_FIN", null));
		Assertions.assertEquals("[46, 46, 46]", annotated.getAttributeAsString("AC_NFE", null));
		Assertions.assertEquals("[6, 6, 6]", annotated.getAttributeAsString("AC_OTH", null));
		Assertions.assertNull(annotated.getAttributeAsString("AC_SAS", null));

		Assertions.assertEquals("[0.3488372093023256, 0.3488372093023256, 0.3488372093023256]",
			annotated.getAttributeAsString("AF_AFR", null));
		Assertions.assertEquals("[0.04265202702702703, 0.04265202702702703, 0.04265202702702703]",
			annotated.getAttributeAsString("AF_ALL", null));
		Assertions.assertEquals("[0.013888888888888888, 0.013888888888888888, 0.013888888888888888]",
			annotated.getAttributeAsString("AF_AMR", null));
		Assertions.assertEquals("[0.5571428571428572, 0.5571428571428572, 0.5571428571428572]",
			annotated.getAttributeAsString("AF_EAS", null));
		Assertions.assertEquals("[0.0, 0.0, 0.0]", annotated.getAttributeAsString("AF_FIN", null));
		Assertions.assertEquals("[0.02346938775510204, 0.02346938775510204, 0.02346938775510204]",
			annotated.getAttributeAsString("AF_NFE", null));
		Assertions.assertEquals("[0.061224489795918366, 0.061224489795918366, 0.061224489795918366]",
			annotated.getAttributeAsString("AF_OTH", null));
		Assertions.assertNull(annotated.getAttributeAsString("AF_SAS", null));

		Assertions.assertEquals("[602]", annotated.getAttributeAsString("AN_AFR", null));
		Assertions.assertEquals("[7104]", annotated.getAttributeAsString("AN_ALL", null));
		Assertions.assertEquals("[72]", annotated.getAttributeAsString("AN_AMR", null));
		Assertions.assertEquals("[70]", annotated.getAttributeAsString("AN_EAS", null));
		Assertions.assertEquals("[714]", annotated.getAttributeAsString("AN_FIN", null));
		Assertions.assertEquals("[1960]", annotated.getAttributeAsString("AN_NFE", null));
		Assertions.assertEquals("[98]", annotated.getAttributeAsString("AN_OTH", null));
		Assertions.assertEquals("[0]", annotated.getAttributeAsString("AN_SAS", null));
	}

}
