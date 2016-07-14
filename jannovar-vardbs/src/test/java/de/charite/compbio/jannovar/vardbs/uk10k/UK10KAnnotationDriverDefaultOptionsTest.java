package de.charite.compbio.jannovar.vardbs.uk10k;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;

public class UK10KAnnotationDriverDefaultOptionsTest {

	// Path to UK10K VCF file
	String dbUK10KVCFPath;
	// Path to reference FASTA file
	String fastaPath;
	// VCF reader for file to be used in the test
	VCFFileReader vcfReader;
	// Configuration to use in the tests
	DBAnnotationOptions options;

	@Before
	public void setUpClass() throws Exception {
		options = DBAnnotationOptions.createDefaults();

		// Setup dbSNP VCF file
		File tmpDir = Files.createTempDir();
		dbUK10KVCFPath = tmpDir + "/uk10k.vcf.gz";
		ResourceUtils.copyResourceToFile("/UK10K_COHORT.20160215.sites.head.vcf.gz", new File(dbUK10KVCFPath));
		String tbiPath = tmpDir + "/uk10k.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/UK10K_COHORT.20160215.sites.head.vcf.gz.tbi", new File(tbiPath));

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
		writer.write("1\t714118\t.\tC\tA,G,T\t.\t.\t.\tGT\t0/1\n");
		writer.close();

		vcfReader = new VCFFileReader(new File(testVCFPath), false);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		UK10KAnnotationDriver driver = new UK10KAnnotationDriver(dbUK10KVCFPath, fastaPath, options);

		VCFHeader header = vcfReader.getFileHeader();

		// Check header before extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(0, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(0, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		driver.constructVCFHeaderExtender().addHeaders(header);

		// Check header after extension
		Assert.assertEquals(0, header.getFilterLines().size());
		Assert.assertEquals(3, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(3, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_AC"));
		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_AN"));
		Assert.assertNotNull(header.getInfoHeaderLine("UK10K_AF"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		DBAnnotationOptions options = DBAnnotationOptions.createDefaults();
		UK10KAnnotationDriver driver = new UK10KAnnotationDriver(dbUK10KVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assert.assertEquals(0, vc.getAttributes().size());
		Assert.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assert.assertEquals(".", annotated.getID());

		Assert.assertEquals(3, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assert.assertEquals("[AC, AF, AN]", keys.toString());

		Assert.assertEquals("[0, 0, 5]", annotated.getAttributeAsString("AC", null));
		Assert.assertEquals("[0.0, 0.0, 6.612007405448294E-4]", annotated.getAttributeAsString("AF", null));
		Assert.assertEquals("7562", annotated.getAttributeAsString("AN", null));
	}

}
