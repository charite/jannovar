package de.charite.compbio.jannovar.cmd.annotate_vcf;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;

import de.charite.compbio.jannovar.Jannovar;

/**
 * Test for annotating VCF files with compatible mode of inheritance
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class JannovarAnnotateVCFInheritanceTest {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	// path to file with the first 93 lines of hg19 RefSeq (up to "Gnomon exon 459822 459929").
	private String pathToSmallSer;

	@Before
	public void setUp() throws URISyntaxException {
		this.pathToSmallSer = this.getClass().getResource("/hg19_small.ser").toURI().getPath();
	}

	@Test
	public void testAnnotateAR() throws IOException, URISyntaxException {
		final File outFolder = tmpFolder.newFolder();
		final String inputVCFPath = this.getClass().getResource("/pedigree_vars.vcf").toURI().getPath();
		final String inputPEDPath = this.getClass().getResource("/pedigree_ar.ped").toURI().getPath();
		String[] argv = new String[] { "annotate-vcf", "-o", outFolder.toString() + "/pedigree_vars.jv_ar.vcf", "-d",
				pathToSmallSer, "-i", inputVCFPath, "--pedigree-file", inputPEDPath };
		System.err.println(Joiner.on(" ").join(argv));

		Jannovar.main(argv);

		File f = new File(outFolder.getAbsolutePath() + File.separator + "pedigree_vars.jv_ar.vcf");
		Assert.assertTrue(f.exists());

		final File expectedFile = new File(this.getClass().getResource("/pedigree_vars.jv_ar.vcf").toURI().getPath());
		final String expected = Files.toString(expectedFile, Charsets.UTF_8);
		final String actual = Files.toString(f, Charsets.UTF_8).replaceAll("##jannovarCommand.*", "##jannovarCommand")
				.replaceAll("##jannovarVersion.*", "##jannovarVersion");
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testAnnotateAD() throws IOException, URISyntaxException {
		final File outFolder = tmpFolder.newFolder();
		final String inputVCFPath = this.getClass().getResource("/pedigree_vars.vcf").toURI().getPath();
		final String inputPEDPath = this.getClass().getResource("/pedigree_ad.ped").toURI().getPath();
		String[] argv = new String[] { "annotate-vcf", "-o", outFolder.toString() + "/pedigree_vars.jv_ad.vcf", "-d",
				pathToSmallSer, "-i", inputVCFPath, "--pedigree-file", inputPEDPath };
		System.err.println(Joiner.on(" ").join(argv));

		Jannovar.main(argv);

		File f = new File(outFolder.getAbsolutePath() + File.separator + "pedigree_vars.jv_ad.vcf");
		Assert.assertTrue(f.exists());

		final File expectedFile = new File(this.getClass().getResource("/pedigree_vars.jv_ad.vcf").toURI().getPath());
		final String expected = Files.toString(expectedFile, Charsets.UTF_8);
		final String actual = Files.toString(f, Charsets.UTF_8).replaceAll("##jannovarCommand.*", "##jannovarCommand")
				.replaceAll("##jannovarVersion.*", "##jannovarVersion");
		Assert.assertEquals(expected, actual);
	}

}
