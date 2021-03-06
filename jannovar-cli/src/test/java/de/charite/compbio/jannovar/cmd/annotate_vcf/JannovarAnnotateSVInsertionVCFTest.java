package de.charite.compbio.jannovar.cmd.annotate_vcf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JannovarAnnotateSVInsertionVCFTest extends JannovarAnnotateVCFWithSVTestBase {

	@Test
	public void testExonicOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58946660\t.\tN\t<INS>\t.\t.\tSVTYPE=INS;END=58946660");
		final String expected = "1\t58946660\t.\tN\t<INS>\t.\t.\tEND=58946660;" +
			"SVANN=insertion&structural_variant&coding_sequence_variant&coding_transcript_variant|HIGH|OMA1|" +
			"115209|transcript|NM_145243.3|Coding|;SVTYPE=INS";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testIntronicFivePrimeOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58957157\t.\tN\t<INS>\t.\t.\tSVTYPE=INS;END=58957157");
		final String expected = "1\t58957157\t.\tN\t<INS>\t.\t.\tEND=58957157;" +
			"SVANN=insertion&structural_variant&intron_variant&coding_transcript_variant|HIGH|OMA1|" +
			"115209|transcript|NM_145243.3|Coding|;SVTYPE=INS";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testExonicThreePrimeOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58946502\t.\tN\t<INS>\t.\t.\tSVTYPE=INS;END=58946502");
		final String expected = "1\t58946502\t.\tN\t<INS>\t.\t.\tEND=58946502;" +
			"SVANN=insertion&3_prime_UTR_exon_variant&structural_variant&coding_transcript_variant|HIGH|OMA1|" +
			"115209|transcript|NM_145243.3|Coding|;SVTYPE=INS";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testExonicFivePrimeOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59012403\t.\tN\t<INS>\t.\t.\tSVTYPE=INS;END=59012403");
		final String expected = "1\t59012403\t.\tN\t<INS>\t.\t.\tEND=59012403;" +
			"SVANN=insertion&5_prime_UTR_exon_variant&structural_variant&coding_transcript_variant|HIGH|OMA1|" +
			"115209|transcript|NM_145243.3|Coding|;SVTYPE=INS";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testIntronicThreePrimeOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59012355\t.\tN\t<INS>\t.\t.\tSVTYPE=INS;END=59012355");
		final String expected = "1\t59012355\t.\tN\t<INS>\t.\t.\tEND=59012355;" +
			"SVANN=insertion&5_prime_UTR_intron_variant&structural_variant&coding_transcript_variant|HIGH|OMA1|" +
			"115209|transcript|NM_145243.3|Coding|;SVTYPE=INS";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testUpstreamOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59012449\t.\tN\t<INS>\t.\t.\tSVTYPE=INS;END=59012449");
		final String expected = "1\t59012449\t.\tN\t<INS>\t.\t.\tEND=59012449;" +
			"SVANN=insertion&upstream_gene_variant&structural_variant&coding_transcript_variant|HIGH|OMA1|" +
			"115209|transcript|NM_145243.3|Coding|;SVTYPE=INS";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testDownstreamOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58942733\t.\tN\t<INS>\t.\t.\tSVTYPE=INS;END=58942733");
		final String expected = "1\t58942733\t.\tN\t<INS>\t.\t.\tEND=58942733;" +
			"SVANN=insertion&downstream_gene_variant&structural_variant&coding_transcript_variant|HIGH|OMA1|" +
			"115209|transcript|NM_145243.3|Coding|;SVTYPE=INS";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testIntergenicOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59017449\t.\tN\t<INS>\t.\t.\tSVTYPE=INS;END=59017449");
		final String expected = "1\t59017449\t.\tN\t<INS>\t.\t.\tEND=59017449;" +
			"SVANN=intergenic_variant&structural_variant&coding_transcript_variant|MODIFIER|TACSTD2|4070|transcript|NM_002353.2|Coding|," +
			"intergenic_variant&structural_variant&coding_transcript_variant|MODIFIER|OMA1|115209|transcript|NM_145243.3|Coding|;" +
			"SVTYPE=INS";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

}
