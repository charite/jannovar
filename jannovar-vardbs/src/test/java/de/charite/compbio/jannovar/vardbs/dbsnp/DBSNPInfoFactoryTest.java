package de.charite.compbio.jannovar.vardbs.dbsnp;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.variant.vcf.VCFFileReader;

public class DBSNPInfoFactoryTest {

	static String vcfPath;
	static VCFFileReader vcfReader;

	@BeforeClass
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
		
		Assert.assertEquals("20160408", info.getFileDate());
		Assert.assertEquals("dbSNP", info.getSource());
		Assert.assertEquals(147, info.getDbSNPBuildID());
		Assert.assertEquals("GRCh37.p13", info.getReference());
		Assert.assertEquals("partial", info.getPhasing());
	}

}
