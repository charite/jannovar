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
		Assert.assertEquals(
				"ExacRecord [chrom=1, pos=13371, id=., ref=G, alt=[C], filter=[], "
						+ "alleleCounts={AFR=[0], AMR=[0], EAS=[0], FIN=[0], NFE=[0], OTH=[0], SAS=[2], ALL=[2]}, "
						+ "alleleHetCounts={AFR=[0], AMR=[0], EAS=[0], FIN=[0], NFE=[0], OTH=[0], SAS=[0], ALL=[0]}, "
						+ "alleleHomCounts={AFR=[0], AMR=[0], EAS=[0], FIN=[0], NFE=[0], OTH=[0], SAS=[1], ALL=[1]}, "
						+ "alleleHemiCounts={}, "
						+ "chromCounts={AFR=770, AMR=134, EAS=254, FIN=16, NFE=2116, OTH=90, SAS=5052, ALL=8432}, "
						+ "alleleFrequencies={AFR=[0.0], AMR=[0.0], EAS=[0.0], FIN=[0.0], NFE=[0.0], OTH=[0.0], SAS=[3.95882818685669E-4], ALL=[2.3719165085388995E-4]}]",
				record.toString());
	}

}
