package de.charite.compbio.jannovar.vardbs.cosmic;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class CosmicVariantContextToRecordConverterTest {

	static String vcfPath;
	static VCFFileReader vcfReader;

	@BeforeClass
	public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();
		vcfPath = tmpDir + "/cosmic.vcf.gz";
		ResourceUtils.copyResourceToFile("/COSMIC.v72.fake.vcf.gz", new File(vcfPath));
		String tbiPath = tmpDir + "/cosmic.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/COSMIC.v72.fake.vcf.gz.tbi", new File(tbiPath));

		vcfReader = new VCFFileReader(new File(vcfPath));
	}

	@Test
	public void test() {
		CosmicVariantContextToRecordConverter converter = new CosmicVariantContextToRecordConverter();
		VariantContext vc = vcfReader.iterator().next();

		CosmicRecord record = converter.convert(vc);
		Assert.assertEquals("CosmicRecord [chrom=1, pos=1230, id=COSM1231, ref=A, alt=[C], cnt=1, snp=false]", record.toString());
	}

}
