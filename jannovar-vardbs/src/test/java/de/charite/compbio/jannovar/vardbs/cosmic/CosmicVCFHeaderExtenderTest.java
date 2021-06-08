package de.charite.compbio.jannovar.vardbs.cosmic;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.vcf.VCFHeader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CosmicVCFHeaderExtenderTest {

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
		options.setIdentifierPrefix("COSMIC_");

		new CosmicVCFHeaderExtender(options).addHeaders(header);

		// Check header after extension
		Assertions.assertEquals(0, header.getFilterLines().size());
		Assertions.assertEquals(6, header.getInfoHeaderLines().size());
		Assertions.assertEquals(0, header.getFormatHeaderLines().size());
		Assertions.assertEquals(6, header.getIDHeaderLines().size());
		Assertions.assertEquals(0, header.getOtherHeaderLines().size());

		Assertions.assertNotNull(header.getInfoHeaderLine("COSMIC_CNT"));
		Assertions.assertNotNull(header.getInfoHeaderLine("COSMIC_SNP"));
		Assertions.assertNotNull(header.getInfoHeaderLine("COSMIC_IDS"));
		Assertions.assertNotNull(header.getInfoHeaderLine("COSMIC_OVL_CNT"));
		Assertions.assertNotNull(header.getInfoHeaderLine("COSMIC_OVL_SNP"));
		Assertions.assertNotNull(header.getInfoHeaderLine("COSMIC_OVL_IDS"));
	}

}
