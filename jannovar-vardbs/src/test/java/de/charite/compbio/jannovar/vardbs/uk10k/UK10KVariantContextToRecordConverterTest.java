package de.charite.compbio.jannovar.vardbs.uk10k;

import com.google.common.io.Files;
import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

public class UK10KVariantContextToRecordConverterTest {

	static String vcfPath;
	static VCFFileReader vcfReader;

	@BeforeAll
	public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();
		vcfPath = tmpDir + "/uk10k.vcf.gz";
		ResourceUtils.copyResourceToFile("/UK10K_COHORT.20160215.sites.head.vcf.gz", new File(vcfPath));
		String tbiPath = tmpDir + "/uk10k.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/UK10K_COHORT.20160215.sites.head.vcf.gz.tbi", new File(tbiPath));

		vcfReader = new VCFFileReader(new File(vcfPath));
	}

	@Test
	public void test() {
		UK10KVariantContextToRecordConverter converter = new UK10KVariantContextToRecordConverter();
		VariantContext vc = vcfReader.iterator().next();

		UK10KRecord record = converter.convert(vc);
		Assertions.assertEquals(
			"UK10KRecord [chrom=1, pos=28589, id=., ref=T, alt=[TTGG], filter=[], "
				+ "altAlleleCounts=[7226], chromCount=7562, altAlleleFrequencies=[0.9555673102353874]]",
			record.toString());
	}

}
