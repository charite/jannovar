package de.charite.compbio.jannovar.vardbs.clinvar;

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
 * Test for annotation with ClinVar with default options
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ClinVarAnnotationDriverReportNoOverlappingTest extends ClinVarAnnotationDriverBaseTest {

	@Before
	public void setUpClass() throws Exception {
		super.setUpClass();
		options.setReportOverlapping(false);
		options.setReportOverlappingAsMatching(false);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		ClinVarAnnotationDriver driver = new ClinVarAnnotationDriver(dbClinVarVCFPath, fastaPath, options);

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
		Assert.assertEquals(10, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(10, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_HGVS"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_ALLELE"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_SOURCE"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_ORIGIN"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_SIGNIFICANCE"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_DISEASE_DB"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_DISEASE_DB_ID"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_DISEASE_DB_NAME"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_REVISION_STATUS"));
		Assert.assertNotNull(header.getInfoHeaderLine("CLINVAR_CLINICAL_ACCESSION"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		ClinVarAnnotationDriver driver = new ClinVarAnnotationDriver(dbClinVarVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assert.assertEquals(0, vc.getAttributes().size());
		Assert.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assert.assertEquals(".", annotated.getID());

		Assert.assertEquals(2, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assert.assertEquals("[CLINVAR_HGVS]", keys.toString());

		Assert.assertEquals("[NC_000001.10:g.949523C>T]", annotated.getAttributeAsString("CLINVAR_HGVS", null));
	}

}
