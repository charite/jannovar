package de.charite.compbio.jannovar.cmd.annotate_vcf;

import org.junit.Assert;
import org.junit.Test;

public class JannovarAnnotateSVTandemDuplicationVCFTest extends JannovarAnnotateVCFWithSVTestBase {

	@Test
	public void testTranscriptDuplicationOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58929877\t.\tN\t<DUP:TANDEM>\t.\t.\tSVTYPE=DUP;END=59028960");
		final String expected = "1\t58929877\t.\tN\t<DUP:TANDEM>\t.\t.\tEND=59028960;" +
			"SVANN=transcript_amplification&direct_tandem_duplication&structural_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=DUP";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testExonDuplicationOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58968128\t.\tN\t<DUP:TANDEM>\t.\t.\tSVTYPE=DUP;END=58994820");
		final String expected = "1\t58968128\t.\tN\t<DUP:TANDEM>\t.\t.\tEND=58994820;" +
			"SVANN=transcript_amplification&direct_tandem_duplication&structural_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=DUP";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testExonOverlapStartDuplicatedOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59004875\t.\tN\t<DUP:TANDEM>\t.\t.\tSVTYPE=DUP;END=59005052");
		final String expected = "1\t59004875\t.\tN\t<DUP:TANDEM>\t.\t.\tEND=59005052;" +
			"SVANN=transcript_amplification&direct_tandem_duplication&structural_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=DUP";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testExonOverlapDuplicationOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58946590\t.\tN\t<DUP:TANDEM>\t.\t.\tSVTYPE=DUP;END=58946946");
		final String expected = "1\t58946590\t.\tN\t<DUP:TANDEM>\t.\t.\tEND=58946946;" +
			"SVANN=transcript_amplification&direct_tandem_duplication&structural_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=DUP";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testExonOverlapNoFrameshiftOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58946843\t.\tN\t<DUP:TANDEM>\t.\t.\tSVTYPE=DUP;END=58946946");
		final String expected = "1\t58946843\t.\tN\t<DUP:TANDEM>\t.\t.\tEND=58946946;" +
			"SVANN=transcript_amplification&direct_tandem_duplication&structural_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=DUP";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testIntronicOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58957157\t.\tN\t<DUP:TANDEM>\t.\t.\tSVTYPE=DUP;END=58963900");
		final String expected = "1\t58957157\t.\tN\t<DUP:TANDEM>\t.\t.\tEND=58963900;" +
			"SVANN=transcript_amplification&direct_tandem_duplication&structural_variant&coding_transcript_variant" +
			"|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=DUP";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testUpstreamOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59012449\t.\tN\t<DUP:TANDEM>\t.\t.\tSVTYPE=DUP;END=59012899");
		final String expected = "1\t59012449\t.\tN\t<DUP:TANDEM>\t.\t.\tEND=59012899;" +
			"SVANN=upstream_gene_variant&structural_variant&coding_transcript_variant" +
			"|MODIFIER|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=DUP";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testDownstreamOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58942733\t.\tN\t<DUP:TANDEM>\t.\t.\tSVTYPE=DUP;END=58946074");
		final String expected = "1\t58942733\t.\tN\t<DUP:TANDEM>\t.\t.\tEND=58946074;" +
			"SVANN=downstream_gene_variant&structural_variant&coding_transcript_variant" +
			"|MODIFIER|OMA1|115209|transcript|NM_145243.3|Coding|;SVTYPE=DUP";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testIntergenicOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59017449\t.\tN\t<DUP:TANDEM>\t.\t.\tSVTYPE=DUP;END=59017899");
		final String expected = "1\t59017449\t.\tN\t<DUP:TANDEM>\t.\t.\tEND=59017899;" +
			"SVANN=intergenic_variant&structural_variant&coding_transcript_variant|MODIFIER|TACSTD2|4070|transcript|NM_002353.2|Coding|," +
			"intergenic_variant&structural_variant&coding_transcript_variant|MODIFIER|OMA1|115209|transcript|NM_145243.3|Coding|;" +
			"SVTYPE=DUP";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

}
