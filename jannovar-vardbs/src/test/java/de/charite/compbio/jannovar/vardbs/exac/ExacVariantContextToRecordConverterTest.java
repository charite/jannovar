package de.charite.compbio.jannovar.vardbs.exac;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class ExacVariantContextToRecordConverterTest {

	static String vcfPath;
	static VCFFileReader vcfReader;

	@BeforeClass
	public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();
		vcfPath = tmpDir + "/exac.vcf.gz";
		ResourceUtils.copyResourceToFile("/ExAC.r0.3.sites.vep.head.vcf.gz", new File(vcfPath));
		String tbiPath = tmpDir + "/exac.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/ExAC.r0.3.sites.vep.head.vcf.gz.tbi", new File(tbiPath));

		vcfReader = new VCFFileReader(new File(vcfPath));
	}

	@Test
	public void test() {
		ExacVariantContextToRecordConverter converter = new ExacVariantContextToRecordConverter();
		VariantContext vc = vcfReader.iterator().next();

		ExacRecord record = converter.convert(vc);
		Assert.assertEquals("ExacRecord", record.toString());
	}

}
