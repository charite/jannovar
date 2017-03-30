package de.charite.compbio.jannovar.vardbs.gnomad;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class GnomadExomesVariantContextToRecordConverterTest {

	static String vcfPath;
	static VCFFileReader vcfReader;

	@BeforeClass
	public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();

		vcfPath = tmpDir + "/gnomad.exomes.vcf.gz";
		ResourceUtils.copyResourceToFile("/gnomad.exomes.r2.0.1.sites.head.vcf.gz", new File(vcfPath));
		String tbiPath = tmpDir + "/gnomad.exomes.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/gnomad.exomes.r2.0.1.sites.head.vcf.gz.tbi", new File(tbiPath));

		vcfReader = new VCFFileReader(new File(vcfPath));
	}

	@Test
	public void test() {
		GnomadVariantContextToRecordConverter converter = new GnomadVariantContextToRecordConverter();
		VariantContext vc = vcfReader.iterator().next();

		GnomadRecord record = converter.convert(vc);
		Assert.assertEquals("GnomadRecord [chrom=1, pos=12197, id=rs62635282, ref=G, alt=[C], filter=[AC0], "
				+ "popmax=[.], alleleCounts={AFR=[0], AMR=[0], ASJ=[0], EAS=[0], FIN=[0], NFE=[0], "
				+ "OTH=[0], SAS=[0], POPMAX=[0], ALL=[0]}, alleleHetCounts={AFR=[0], AMR=[0], ASJ=[0], "
				+ "EAS=[0], FIN=[0], NFE=[0], OTH=[0], SAS=[0], POPMAX=[0], ALL=[0]}, "
				+ "alleleHomCounts={AFR=[0], AMR=[0], ASJ=[0], EAS=[0], FIN=[0], NFE=[0], OTH=[0], "
				+ "SAS=[0], ALL=[0]}, alleleHemiCounts={}, chromCounts={AFR=[0], AMR=[0], ASJ=[0], "
				+ "EAS=[0], FIN=[0], NFE=[0], OTH=[0], SAS=[0], POPMAX=[0], ALL=[0]}, "
				+ "alleleFrequencies={AFR=[0.0], AMR=[0.0], ASJ=[0.0], EAS=[0.0], FIN=[0.0], "
				+ "NFE=[0.0], OTH=[0.0], SAS=[0.0], POPMAX=[0.0], ALL=[0.0]}]", record.toString());
	}

}
