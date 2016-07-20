package de.charite.compbio.jannovar.vardbs.base;

import static org.junit.Assert.*;

import java.io.File;
import java.io.PrintWriter;
import java.util.Collection;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

/**
 * Test for the matching of alleles
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class AlleleMatcherTest {

	static String fastaPath;
	static VariantNormalizer normalizer;

	// Path to VCF file with single alternative alleles
	static String vcfSingle;
	// Path to VCF file with multiple alternative alleles
	static String vcfMultiple;

	// VCF record read from vcfSingle
	static VariantContext vcSingle;
	// VCF record read from vcfMultiple
	static VariantContext vcMultiple;

	@BeforeClass
	public static void setUpClass() throws Exception {
		// Write out FASTA file with FAI such that we can read it
		File tmpDir = Files.createTempDir();
		fastaPath = tmpDir + "/chr1.fasta";
		ResourceUtils.copyResourceToFile("/chr1.fasta", new File(fastaPath));
		ResourceUtils.copyResourceToFile("/chr1.fasta.fai", new File(fastaPath + ".fai"));

		// Construct variant normalizer with FASTA path to test
		normalizer = new VariantNormalizer(fastaPath);

		// Header of VCF file
		String vcfHeader = "##fileformat=VCFv4.0\n"
				+ "#CHROM\tPOS\tID\tREF\tALT\tQUAL\tFILTER\tINFO\tFORMAT\tindividual\n";

		vcfSingle = tmpDir + "/query_single.vcf";
		try (PrintWriter writer = new PrintWriter(vcfSingle)) {
			writer.write(vcfHeader);
			writer.write("1\t11022\t.\tG\tA\t.\t.\t.\tGT\t0/1\n");
		}
		try (VCFFileReader reader = new VCFFileReader(new File(vcfSingle), false)) {
			vcSingle = reader.iterator().next();
		}

		vcfMultiple = tmpDir + "/query_multiple.vcf";
		try (PrintWriter writer = new PrintWriter(vcfMultiple)) {
			writer.write(vcfHeader);
			writer.write("1\t11022\t.\tG\tT,A\t.\t.\t.\tGT\t0/1\n");
		}
		try (VCFFileReader reader = new VCFFileReader(new File(vcfMultiple), false)) {
			vcMultiple = reader.iterator().next();
		}
	}

	/**
	 * Test the case of matching single variants
	 */
	@Test
	public void testMatchSingle() throws JannovarVarDBException {
		AlleleMatcher matcher = new AlleleMatcher(fastaPath);
		Collection<GenotypeMatch> matches = matcher.matchGenotypes(vcSingle, vcSingle);

		assertEquals(1, matches.size());
		GenotypeMatch first = (GenotypeMatch) matches.toArray()[0];
		Assert.assertSame(vcSingle, first.getObsVC());
		Assert.assertSame(vcSingle, first.getDBVC());
		Assert.assertEquals(1, first.getObservedAllele());
		Assert.assertEquals(1, first.getDbAllele());
	}

	/**
	 * Test the case of matching single variant to cluster of multiple in database
	 */
	@Test
	public void testMatchSingleToMultiple() throws JannovarVarDBException {
		AlleleMatcher matcher = new AlleleMatcher(fastaPath);
		Collection<GenotypeMatch> matches = matcher.matchGenotypes(vcSingle, vcMultiple);

		assertEquals(1, matches.size());
		GenotypeMatch first = (GenotypeMatch) matches.toArray()[0];
		Assert.assertSame(vcSingle, first.getObsVC());
		Assert.assertSame(vcMultiple, first.getDBVC());
		Assert.assertEquals(1, first.getObservedAllele());
		Assert.assertEquals(2, first.getDbAllele());
	}

	/**
	 * Test the case of matching multiple variants to cluster of single in database
	 */
	@Test
	public void testMatchMultipleToSingle() throws JannovarVarDBException {
		AlleleMatcher matcher = new AlleleMatcher(fastaPath);
		Collection<GenotypeMatch> matches = matcher.matchGenotypes(vcMultiple, vcSingle);

		assertEquals(1, matches.size());
		GenotypeMatch first = (GenotypeMatch) matches.toArray()[0];
		Assert.assertSame(vcMultiple, first.getObsVC());
		Assert.assertSame(vcSingle, first.getDBVC());
		Assert.assertEquals(2, first.getObservedAllele());
		Assert.assertEquals(1, first.getDbAllele());
	}

	/**
	 * Test the case of matching multiple variants to cluster of multiple in database
	 */
	@Test
	public void testMatchMultipleToMultiple() throws JannovarVarDBException {
		AlleleMatcher matcher = new AlleleMatcher(fastaPath);
		Collection<GenotypeMatch> matches = matcher.matchGenotypes(vcMultiple, vcMultiple);

		assertEquals(2, matches.size());
		GenotypeMatch first = (GenotypeMatch) matches.toArray()[0];
		Assert.assertSame(vcMultiple, first.getObsVC());
		Assert.assertSame(vcMultiple, first.getDBVC());
		Assert.assertEquals(1, first.getObservedAllele());
		Assert.assertEquals(1, first.getDbAllele());
		GenotypeMatch second = (GenotypeMatch) matches.toArray()[1];
		Assert.assertSame(vcMultiple, second.getObsVC());
		Assert.assertSame(vcMultiple, second.getDBVC());
		Assert.assertEquals(2, second.getObservedAllele());
		Assert.assertEquals(2, second.getDbAllele());
	}

}
