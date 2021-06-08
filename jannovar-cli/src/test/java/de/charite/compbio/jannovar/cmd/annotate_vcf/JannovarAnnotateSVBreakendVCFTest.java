package de.charite.compbio.jannovar.cmd.annotate_vcf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JannovarAnnotateSVBreakendVCFTest extends JannovarAnnotateVCFWithSVTestBase {

	@Test
	public void testExonicOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58946660\t.\tN\tG]1:42]\t.\t.\tSVTYPE=BND");
		final String expected = "1\t58946660\t.\tN\tG]1:42]\t.\t.\t" +
			"SVANN=translocation&structural_variant&coding_sequence_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=BND";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testIntronicFivePrimeOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58957157\t.\tN\tG]1:42]\t.\t.\tSVTYPE=BND");
		final String expected = "1\t58957157\t.\tN\tG]1:42]\t.\t.\t" +
			"SVANN=translocation&structural_variant&intron_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=BND";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testExonicFivePrimeOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58946502\t.\tN\tG]1:42]\t.\t.\tSVTYPE=BND");
		final String expected = "1\t58946502\t.\tN\tG]1:42]\t.\t.\t" +
			"SVANN=translocation&3_prime_UTR_exon_variant&structural_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=BND";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testExonicThreePrimeOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59012403\t.\tN\tG]1:42]\t.\t.\tSVTYPE=BND");
		final String expected = "1\t59012403\t.\tN\tG]1:42]\t.\t.\t" +
			"SVANN=translocation&5_prime_UTR_exon_variant&structural_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=BND";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testIntronicThreePrimeOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59012355\t.\tN\tG]1:42]\t.\t.\tSVTYPE=BND");
		final String expected = "1\t59012355\t.\tN\tG]1:42]\t.\t.\t" +
			"SVANN=translocation&5_prime_UTR_intron_variant&structural_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=BND";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testUpstreamOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59012449\t.\tN\tG]1:42]\t.\t.\tSVTYPE=BND");
		final String expected = "1\t59012449\t.\tN\tG]1:42]\t.\t.\t" +
			"SVANN=translocation&upstream_gene_variant&structural_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=BND";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testDownstreamOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58941393\t.\tN\tG]1:42]\t.\t.\tSVTYPE=BND");
		final String expected = "1\t58941393\t.\tN\tG]1:42]\t.\t.\t" +
			"SVANN=translocation&downstream_gene_variant&structural_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=BND";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

	@Test
	public void testIntergenicOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59017449\t.\tN\tG]1:42]\t.\t.\tSVTYPE=BND");
		final String expected = "1\t59017449\t.\tN\tG]1:42]\t.\t.\t" +
			"SVANN=translocation&intergenic_variant&structural_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=BND";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

}
