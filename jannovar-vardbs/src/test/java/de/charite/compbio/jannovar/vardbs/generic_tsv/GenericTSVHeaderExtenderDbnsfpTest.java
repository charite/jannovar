package de.charite.compbio.jannovar.vardbs.generic_tsv;

import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test header extension code for generic TSV processing
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericTSVHeaderExtenderDbnsfpTest extends GenericTSVAnnotationDriverWithDbnsfpBaseTest {

	@Test
	public void test() throws JannovarVarDBException {
		VCFHeader header = new VCFHeader();

		// Check header before extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(0, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(0, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		new GenericTSVHeaderExtender(options).addHeaders(header);

		// Check header after extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(8, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(8, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("DBNSFP_AAREF"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBNSFP_RS_DBSNP147"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBNSFP_HG19POS"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBNSFP_SIFT_SCORE"));
	}

}
