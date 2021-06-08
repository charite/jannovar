package de.charite.compbio.jannovar.cmd.annotate_vcf;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.io.Files;
import de.charite.compbio.jannovar.Jannovar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Test for annotating VCF files with compatible mode of inheritance
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class JannovarAnnotateVCFInheritanceTest {

	@TempDir
	public File tmpFolder;

	// path to file with the first 93 lines of hg19 RefSeq (up to "Gnomon exon 459822 459929").
	private String pathToSmallSer;

	@BeforeEach
	public void setUp() throws URISyntaxException {
		this.pathToSmallSer = this.getClass().getResource("/hg19_small.ser").toURI().getPath();
	}

	@Test
	public void testAnnotateAR() throws IOException, URISyntaxException {
		final File outFolder = new File(tmpFolder, "output");
		outFolder.mkdirs();
		final String inputVCFPath = this.getClass().getResource("/pedigree_vars.vcf").toURI().getPath();
		final String inputPEDPath = this.getClass().getResource("/pedigree_ar.ped").toURI().getPath();
		String[] argv = new String[]{"annotate-vcf", "-o", outFolder.toString() + "/pedigree_vars.jv_ar.vcf", "-d",
			pathToSmallSer, "-i", inputVCFPath, "--pedigree-file", inputPEDPath};
		System.err.println(Joiner.on(" ").join(argv));

		Jannovar.main(argv);

		File f = new File(outFolder.getAbsolutePath() + File.separator + "pedigree_vars.jv_ar.vcf");
		Assertions.assertTrue(f.exists());

		final File expectedFile = new File(this.getClass().getResource("/pedigree_vars.jv_ar.vcf").toURI().getPath());
		final String expected = Files.asCharSource(expectedFile, Charsets.UTF_8).read();
		final String actual = Files.asCharSource(f, Charsets.UTF_8).read().replaceAll("##jannovarCommand.*", "##jannovarCommand")
			.replaceAll("##jannovarVersion.*", "##jannovarVersion");
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testAnnotateAD() throws IOException, URISyntaxException {
		final File outFolder = new File(tmpFolder, "output");
		outFolder.mkdirs();
		final String inputVCFPath = this.getClass().getResource("/pedigree_vars.vcf").toURI().getPath();
		final String inputPEDPath = this.getClass().getResource("/pedigree_ad.ped").toURI().getPath();
		String[] argv = new String[]{"annotate-vcf", "-o", outFolder.toString() + "/pedigree_vars.jv_ad.vcf", "-d",
			pathToSmallSer, "-i", inputVCFPath, "--pedigree-file", inputPEDPath};
		System.err.println(Joiner.on(" ").join(argv));

		Jannovar.main(argv);

		File f = new File(outFolder.getAbsolutePath() + File.separator + "pedigree_vars.jv_ad.vcf");
		Assertions.assertTrue(f.exists());

		final File expectedFile = new File(this.getClass().getResource("/pedigree_vars.jv_ad.vcf").toURI().getPath());
		final String expected = Files.asCharSource(expectedFile, Charsets.UTF_8).read();
		final String actual = Files.asCharSource(f, Charsets.UTF_8).read().replaceAll("##jannovarCommand.*", "##jannovarCommand")
			.replaceAll("##jannovarVersion.*", "##jannovarVersion");
		Assertions.assertEquals(expected, actual);
	}

}
