package de.charite.compbio.jannovar.cmd.annotate_vcf;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JannovarAnnotateSVMultiGeneVCFTest extends JannovarAnnotateVCFWithSVTestBase {

	@Test
	public void testMultipleTranscriptAblations() throws Exception {
		final String outPath = runJannovarOnVCFLine(
			"/sv_header.vcf",
			"1\t58649139\t.\tN\t<DEL>\t.\t.\tSVTYPE=DEL;END=59309698");
		final String expected = "1\t58649139\t.\tN\t<DEL>\t.\t.\tEND=59309698;" +
			"SVANN=transcript_ablation&coding_transcript_variant|HIGH|TACSTD2|4070|transcript|NM_002353.2|Coding|," +
			"transcript_ablation&coding_transcript_variant|HIGH|OMA1|115209|transcript|NM_145243.3|Coding|," +
			"transcript_ablation&coding_transcript_variant|HIGH|MYSM1|114803|transcript|NM_001085487.2|Coding" +
			"|,transcript_ablation&coding_transcript_variant|HIGH|JUN|3725|transcript|NM_002228.3|Coding|;" +
			"SVTYPE=DEL";
		final String actual = loadVcfBody(outPath);
		Assertions.assertEquals(expected, actual);
	}

}
