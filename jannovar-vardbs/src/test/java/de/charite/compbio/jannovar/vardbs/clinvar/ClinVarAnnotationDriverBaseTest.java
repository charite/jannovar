package de.charite.compbio.jannovar.vardbs.clinvar;

import java.io.File;
import java.io.PrintWriter;

import org.junit.Before;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import htsjdk.variant.vcf.VCFFileReader;

public class ClinVarAnnotationDriverBaseTest {

	protected String dbClinVarVCFPath;
	protected String fastaPath;
	protected VCFFileReader vcfReader;
	protected DBAnnotationOptions options;

	@Before
	public void setUpClass() throws Exception {
		options = DBAnnotationOptions.createDefaults();

		// Setup dbSNP VCF file
		File tmpDir = Files.createTempDir();

		dbClinVarVCFPath = tmpDir + "/clinvar.vcf.gz";
		ResourceUtils.copyResourceToFile("/clinvar_20161003.head.vcf.gz", new File(dbClinVarVCFPath));
		String tbiPath = tmpDir + "/clinvar.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/clinvar_20161003.head.vcf.gz.tbi", new File(tbiPath));

		// Setup reference FASTA file
		fastaPath = tmpDir + "/chr1.fasta";
		ResourceUtils.copyResourceToFile("/chr1.fasta", new File(fastaPath));
		String faiPath = tmpDir + "/chr1.fasta.fai";
		ResourceUtils.copyResourceToFile("/chr1.fasta.fai", new File(faiPath));

		// Header of VCF file
		String vcfHeader = "##fileformat=VCFv4.0\n"
				+ "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tindividual\n";

		// Write out file to use in the test
		String testVCFPath = tmpDir + "/test_var_in_exac.vcf";
		PrintWriter writer = new PrintWriter(testVCFPath);
		writer.write(vcfHeader);
		writer.write("1\t2160305\t.\tG\tA,C,T\t.\t.\t.\tGT\t0/1\n");
		writer.close();

		vcfReader = new VCFFileReader(new File(testVCFPath), false);
	}

}