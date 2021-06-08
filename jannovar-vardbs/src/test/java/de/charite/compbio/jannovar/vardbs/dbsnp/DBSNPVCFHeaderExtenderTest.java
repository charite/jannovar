package de.charite.compbio.jannovar.vardbs.dbsnp;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class DBSNPVCFHeaderExtenderTest {

	@BeforeEach
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws JannovarVarDBException {
		VCFHeader header = new VCFHeader();

		// Check header before extension
		Assertions.assertEquals(0, header.getFilterLines().size());
		Assertions.assertEquals(0, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(0, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		DBAnnotationOptions options = DBAnnotationOptions.createDefaults();
		options.setReportOverlapping(true);
		options.setReportOverlappingAsMatching(false);
		options.setIdentifierPrefix("DBSNP_");

		new DBSNPVCFHeaderExtender(options).addHeaders(header);

		// Check header after extension
		Assertions.assertEquals(0, header.getFilterLines().size());
		Assertions.assertEquals(12, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(12, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_COMMON"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_CAF"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_G5"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_G5A"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_IDS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_SAO"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_COMMON"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_CAF"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_G5"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_G5A"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_IDS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("DBSNP_OVL_SAO"));
	}

}
