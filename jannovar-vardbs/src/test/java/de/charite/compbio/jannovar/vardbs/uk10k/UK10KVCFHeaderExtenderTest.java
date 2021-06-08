package de.charite.compbio.jannovar.vardbs.uk10k;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class UK10KVCFHeaderExtenderTest {

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
		options.setIdentifierPrefix("UK10K_");

		new UK10KVCFHeaderExtender(options).addHeaders(header);

		// Check header after extension
		Assertions.assertEquals(0, header.getFilterLines().size());
		Assertions.assertEquals(6, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(6, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		Assertions.assertNotNull(header.getInfoHeaderLine("UK10K_AC"));
		Assertions.assertNotNull(header.getInfoHeaderLine("UK10K_AN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("UK10K_AF"));
		Assertions.assertNotNull(header.getInfoHeaderLine("UK10K_OVL_AC"));
		Assertions.assertNotNull(header.getInfoHeaderLine("UK10K_OVL_AN"));
		Assertions.assertNotNull(header.getInfoHeaderLine("UK10K_OVL_AF"));
	}

}
