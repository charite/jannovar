package de.charite.compbio.jannovar.vardbs.gnomad;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class GnomadGenomesVariantContextToRecordConverterTest {

	static String vcfPath;
	static VCFFileReader vcfReader;

	@BeforeClass
	public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();

		vcfPath = tmpDir + "/gnomad.exomes.vcf.gz";
		ResourceUtils.copyResourceToFile("/gnomad.genomes.r2.0.1.sites.head.vcf.gz", new File(vcfPath));
		String tbiPath = tmpDir + "/gnomad.exomes.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/gnomad.genomes.r2.0.1.sites.head.vcf.gz.tbi", new File(tbiPath));

		vcfReader = new VCFFileReader(new File(vcfPath));
	}

	@Test
	public void test() {
		GnomadVariantContextToRecordConverter converter = new GnomadVariantContextToRecordConverter();
		VariantContext vc = vcfReader.iterator().next();

		GnomadRecord record = converter.convert(vc);
		Assert.assertEquals(
				"GnomadRecord [chrom=1, pos=10066, id=., ref=T, alt=[TAACCCTAACCCTAACCCTAACCCTAACCCTAACCCTAACCC], "
						+ "filter=[RF, LCR], popmax=[FIN], alleleCounts={AFR=[0], AMR=[0], ASJ=[0], EAS=[0], FIN=[1], "
						+ "NFE=[0], OTH=[0], POPMAX=[1], ALL=[1]}, alleleHetCounts={AFR=[0], AMR=[0], ASJ=[0], EAS=[0], "
						+ "FIN=[1], NFE=[0], OTH=[0], POPMAX=[1], ALL=[1]}, alleleHomCounts={AFR=[0], AMR=[0], ASJ=[0], "
						+ "EAS=[0], FIN=[0], NFE=[0], OTH=[0], ALL=[0]}, alleleHemiCounts={}, chromCounts={AFR=[7310], "
						+ "AMR=[670], ASJ=[276], EAS=[1458], FIN=[2672], NFE=[12892], OTH=[840], SAS=[0], POPMAX=[2672], "
						+ "ALL=[26118]}, alleleFrequencies={AFR=[0.0], AMR=[0.0], ASJ=[0.0], EAS=[0.0], "
						+ "FIN=[3.7425149700598805E-4], NFE=[0.0], OTH=[0.0], POPMAX=[3.7425149700598805E-4], ALL=[3.828777088597902E-5]}]",
				record.toString());
	}

}
