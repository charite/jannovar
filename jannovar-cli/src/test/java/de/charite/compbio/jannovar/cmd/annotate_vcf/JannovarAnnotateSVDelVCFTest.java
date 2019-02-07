package de.charite.compbio.jannovar.cmd.annotate_vcf;

import org.junit.Assert;
import org.junit.Test;

public class JannovarAnnotateSVDelVCFTest extends JannovarAnnotateVCFWithSVTestBase {

	@Test
	public void testTranscriptAblationOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58929878\t.\tN\t<DEL>\t.\t.\tSVTYPE=DEL;END=59028960");
		final String expected = "1\t58929878\t.\tN\t<DEL>\t.\t.\tEND=59028960;" +
			"SVANN=transcript_ablation&coding_transcript_variant|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;" +
			"SVTYPE=DEL";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testExonLossOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58968128\t.\tN\t<DEL>\t.\t.\tSVTYPE=DEL;END=58994820");
		final String expected = "1\t58968128\t.\tN\t<DEL>\t.\t.\tEND=58994820;" +
			"SVANN=exon_loss_variant&coding_transcript_variant|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;" +
			"SVTYPE=DEL";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testExonOverlapStartLostOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59004876\t.\tN\t<DEL>\t.\t.\tSVTYPE=DEL;END=59005053");
		final String expected = "1\t59004876\t.\tN\t<DEL>\t.\t.\tEND=59005053;" +
			"SVANN=start_lost&coding_transcript_variant|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;" +
			"SVTYPE=DEL";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testExonOverlapStopLostOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58946591\t.\tN\t<DEL>\t.\t.\tSVTYPE=DEL;END=58946671");
		final String expected = "1\t58946591\t.\tN\t<DEL>\t.\t.\tEND=58946671;" +
			"SVANN=stop_lost&coding_transcript_variant|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;" +
			"SVTYPE=DEL";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testExonOverlapFrameshiftOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58946845\t.\tN\t<DEL>\t.\t.\tSVTYPE=DEL;END=58946946");
		final String expected = "1\t58946845\t.\tN\t<DEL>\t.\t.\tEND=58946946;" +
			"SVANN=frameshift_truncation&coding_transcript_variant|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;" +
			"SVTYPE=DEL";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testExonOverlapNoFrameshiftOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58946844\t.\tN\t<DEL>\t.\t.\tSVTYPE=DEL;END=58946946");
		final String expected = "1\t58946844\t.\tN\t<DEL>\t.\t.\tEND=58946946;" +
			"SVANN=feature_truncation&coding_transcript_variant|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|;" +
			"SVTYPE=DEL";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testIntronicOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58957158\t.\tN\t<DEL>\t.\t.\tSVTYPE=DEL;END=58963900");
		final String expected = "1\t58957158\t.\tN\t<DEL>\t.\t.\tEND=58963900;" +
			"SVANN=coding_transcript_intron_variant&coding_transcript_variant|LOW|OMA1|115209|transcript|" +
			"NM_145243.3|Coding|;SVTYPE=DEL";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testUpstreamOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59012450\t.\tN\t<DEL>\t.\t.\tSVTYPE=DEL;END=59012899");
		final String expected = "1\t59012450\t.\tN\t<DEL>\t.\t.\tEND=59012899;" +
			"SVANN=upstream_gene_variant&coding_transcript_variant|MODIFIER|OMA1|115209|transcript|" +
			"NM_145243.3|Coding|;SVTYPE=DEL";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testDownstreamOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58942734\t.\tN\t<DEL>\t.\t.\tSVTYPE=DEL;END=58946074");
		final String expected = "1\t58942734\t.\tN\t<DEL>\t.\t.\tEND=58946074;" +
			"SVANN=downstream_gene_variant&coding_transcript_variant|MODIFIER|OMA1|115209|transcript|" +
			"NM_145243.3|Coding|;SVTYPE=DEL";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

	@Test
	public void testIntergenicOma1() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t59017450\t.\tN\t<DEL>\t.\t.\tSVTYPE=DEL;END=59017899");
		final String expected = "1\t59017450\t.\tN\t<DEL>\t.\t.\tEND=59017899;" +
			"SVANN=intergenic_variant&coding_transcript_variant|MODIFIER|OMA1|115209|transcript|NM_145243.3|Coding|;" +
			"SVTYPE=DEL";
		final String actual = loadVcfBody(outPath);
		Assert.assertEquals(expected, actual);
	}

}
