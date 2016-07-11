package de.charite.compbio.jannovar.vardbs.dbsnp;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.SortedSet;

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

/**
 * Test for annotation with dbSNP
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class DBSNPAnnotationDriverDefaultOptionsTest {

	// Path to dbSNP VCF file
	String dbSNPVCFPath;
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
		dbSNPVCFPath = tmpDir + "/dbsnp.vcf.gz";
		ResourceUtils.copyResourceToFile("/dbSNP147.head.vcf.gz", new File(dbSNPVCFPath));
		String tbiPath = tmpDir + "/dbsnp.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/dbSNP147.head.vcf.gz.tbi", new File(tbiPath));

		// Setup reference FASTA file
		fastaPath = tmpDir + "/chr1.fasta";
		ResourceUtils.copyResourceToFile("/chr1.fasta", new File(fastaPath));
		String faiPath = tmpDir + "/chr1.fasta.fai";
		ResourceUtils.copyResourceToFile("/chr1.fasta.fai", new File(faiPath));

		// Header of VCF file
		String vcfHeader = "##fileformat=VCFv4.0\n"
				+ "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tindividual\n";

		// Write out file to use in the test
		String testVCFPath = tmpDir + "/test_var_in_dbsnp.vcf";
		PrintWriter writer = new PrintWriter(testVCFPath);
		writer.write(vcfHeader);
		writer.write("1\t11022\t.\tG\tA\t.\t.\t.\tGT\t0/1\n");
		writer.close();

		vcfReader = new VCFFileReader(new File(testVCFPath), false);
	}

	@Test
	public void testAnnotateExtendHeaderWithDefaultPrefix() throws JannovarVarDBException {
		DBSNPAnnotationDriver driver = new DBSNPAnnotationDriver(dbSNPVCFPath, fastaPath, options);

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
		Assert.assertEquals(4, header.getInfoHeaderLines().size());
		Assert.assertEquals(0, header.getFormatHeaderLines().size());
		Assert.assertEquals(4, header.getIDHeaderLines().size());
		Assert.assertEquals(0, header.getOtherHeaderLines().size());

		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_COMMON"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_CAF"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_G5A"));
		Assert.assertNotNull(header.getInfoHeaderLine("DBSNP_MATCH"));
	}

	@Test
	public void testAnnotateVariantContext() throws JannovarVarDBException {
		DBAnnotationOptions options = DBAnnotationOptions.createDefaults();
		DBSNPAnnotationDriver driver = new DBSNPAnnotationDriver(dbSNPVCFPath, fastaPath, options);
		VariantContext vc = vcfReader.iterator().next();

		Assert.assertEquals(0, vc.getAttributes().size());
		Assert.assertEquals(".", vc.getID());

		VariantContext annotated = driver.annotateVariantContext(vc);

		Assert.assertEquals("rs28775022", annotated.getID());

		Assert.assertEquals(5, annotated.getAttributes().size());
		ArrayList<String> keys = Lists.newArrayList(annotated.getAttributes().keySet());
		Collections.sort(keys);
		Assert.assertEquals("[CAF, COMMON, G5, G5A, MATCH]", keys.toString());
		
		Assert.assertEquals("[0.0, 0.0]", annotated.getAttributeAsString("CAF", null));
		Assert.assertEquals("[false]", annotated.getAttributeAsString("G5", null));
		Assert.assertEquals("[false]", annotated.getAttributeAsString("G5A", null));
		Assert.assertEquals("[rs28775022]", annotated.getAttributeAsString("MATCH", null));
	}

}
