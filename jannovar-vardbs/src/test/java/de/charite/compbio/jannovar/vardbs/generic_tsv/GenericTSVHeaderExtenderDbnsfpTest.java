package de.charite.compbio.jannovar.vardbs.generic_tsv;

import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
		Assertions.assertEquals(0, header.getFilterLines().size());
		Assertions.assertEquals(0, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(0, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		new GenericTSVHeaderExtender(options).addHeaders(header);

		// Check header after extension
		Assertions.assertEquals(0, header.getFilterLines().size());
		Assertions.assertEquals(8, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(8, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		Assertions.assertNotNull(header.getInfoHeaderLine("DBNSFP_AAREF"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBNSFP_RS_DBSNP147"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBNSFP_HG19POS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBNSFP_SIFT_SCORE"));
	}

}
