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

import de.charite.compbio.jannovar.JannovarException;

/**
 * This test runs the annotation command.
 */
public class JannovarAnnotateVCFTest {

	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();

	// path to file with the first 93 lines of hg19 RefSeq (up to "Gnomon exon 459822 459929").
	private String pathToSmallSer = null;

	@Before
	public void setUp() throws URISyntaxException {
		this.pathToSmallSer = this.getClass().getResource("/hg19_small.ser").toURI().getPath();
	}

	// Test on small.vcf (with default settings) and compare with the prepared gold-standard small.jv.vcf
	@Test
	public void testOnSmallExample() throws JannovarException, URISyntaxException, IOException {
		final File outFolder = tmpFolder.newFolder();
		final String inputFilePath = this.getClass().getResource("/small.vcf").toURI().getPath();
		String[] argv = new String[] { "annotate", "-o", outFolder.toString(), "-d", pathToSmallSer, "-i",
				inputFilePath };
		System.err.println(Joiner.on(" ").join(argv));
		new AnnotateVCFCommand(argv).run();
		File f = new File(outFolder.getAbsolutePath() + File.separator + "small.jv.vcf");
		Assert.assertTrue(f.exists());

		final File expectedFile = new File(this.getClass().getResource("/small.jv.vcf").toURI().getPath());
		final String expected = Files.toString(expectedFile, Charsets.UTF_8);
		final String actual = Files.toString(f, Charsets.UTF_8).replaceAll("##jannovarCommand.*", "##jannovarCommand");
		Assert.assertEquals(expected, actual);
	}

	// Test on semicolons.vcf. This file contains trailing semicolons at the end of the INFO and FILTER columns.
	// Previous versions of Jannovar directly used the HTSJDK, interpreted this as empty entries and moved the semicolon
	// to the beginning. The new versions remove it.
	@Test
	public void testOnTrailingSemicolons() throws JannovarException, URISyntaxException, IOException {
		final File outFolder = tmpFolder.newFolder();
		final String inputFilePath = this.getClass().getResource("/semicolons.vcf").toURI().getPath();
		String[] argv = new String[] { "annotate", "-o", outFolder.toString(), "-d", pathToSmallSer, "-i",
				inputFilePath };
		System.err.println(Joiner.on(" ").join(argv));
		new AnnotateVCFCommand(argv).run();
		File f = new File(outFolder.getAbsolutePath() + File.separator + "semicolons.jv.vcf");
		Assert.assertTrue(f.exists());

		final File expectedFile = new File(this.getClass().getResource("/semicolons.jv.vcf").toURI().getPath());
		final String expected = Files.toString(expectedFile, Charsets.UTF_8);
		final String actual = Files.toString(f, Charsets.UTF_8).replaceAll("##jannovarCommand.*", "##jannovarCommand");
		Assert.assertEquals(expected, actual);
	}

}
