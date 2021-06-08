package de.charite.compbio.jannovar.vardbs.dbsnp;

import com.google.common.io.Files;
import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.variant.vcf.VCFFileReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

public class DBSNPInfoFactoryTest {

	static String vcfPath;
	static VCFFileReader vcfReader;

	@BeforeAll
	public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();
		vcfPath = tmpDir + "/dbsnp.vcf.gz";
		ResourceUtils.copyResourceToFile("/dbSNP147.head.vcf.gz", new File(vcfPath));
		String tbiPath = tmpDir + "/dbsnp.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/dbSNP147.head.vcf.gz.tbi", new File(tbiPath));

		vcfReader = new VCFFileReader(new File(vcfPath));
	}

	@Test
	public void test() {
		DBSNPInfoFactory factory = new DBSNPInfoFactory();
		DBSNPInfo info = factory.build(vcfReader.getFileHeader());

		Assertions.assertEquals("20160408", info.getFileDate());
		Assertions.assertEquals("dbSNP", info.getSource());
		Assertions.assertEquals(147, info.getDbSNPBuildID());
		Assertions.assertEquals("GRCh37.p13", info.getReference());
		Assertions.assertEquals("partial", info.getPhasing());
	}

}
