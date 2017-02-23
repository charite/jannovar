package de.charite.compbio.jannovar.filter.impl.gt;

import org.junit.Assert;
import org.junit.Test;

import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Tests for the SupportedVarCaller enum
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class SupportedVarCallerTest extends GenotypeFilterTestBase {

	@Test
	public void testBcftools() throws Exception {
		String headerLines = BCFTOOLS_HEADER;
		String vcfLine = "1\t17452\t.\tT\tG\t106\t.\t"
				+ "DP=215;VDB=0.0267806;SGB=-0.693147;RPB=0.0333489;MQB=1.318e-08;MQSB=0.860898;BQB=0.000739087;MQ0F=0.260465;"
				+ "DPR=95,75;ICB=1;HOB=0.5;AC=1;AN=2;DP4=86,9,48,27;MQ=21\tGT:PL:DP:DV:SP:DP4:DPR:GP:GQ\t"
				+ "0/1:139,0,255:170:75:43:86,9,48,27:95,75:138,0,261:127\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);
		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(SupportedVarCaller.BCFTOOLS, SupportedVarCaller.guessFromGenotype(gt));
	}

	@Test
	public void testFreebayes() throws Exception {
		String headerLines = FREEBAYES_HEADER;
		String vcfLine = "1\t17452\t.\tCT\tC\t3.86312e-05\t.\t"
				+ "AB=0;ABP=0;AC=0;AF=0;AN=2;AO=2;CIGAR=1M1D1M;DP=10;DPB=9.33333;DPRA=0;EPP=7.35324;EPPR=5.80219;GTI=0;"
				+ "LEN=1;MEANALT=2;MQM=23;MQMR=33.4286;NS=1;NUMALT=1;ODDS=11.63;PAIRED=1;PAIREDR=1;PAO=0;PQA=0;PQR=0;PRO=0;"
				+ "QA=24;QR=228;RO=7;RPL=2;RPP=7.35324;RPPR=10.7656;RPR=0;RUN=1;SAF=2;SAP=7.35324;SAR=0;SRF=3;SRP=3.32051;SRR=4;"
				+ "TYPE=del;technology.ILLUMINA=1;OLD_VARIANT=1:10150:CTA/CA\t"
				+ "GT:GQ:DP:RO:QR:AO:QA:GL\t0/0:50.5085:10:7:228:2:24:0,-0.568818,-14.1241";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);
		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(SupportedVarCaller.FREEBAYES, SupportedVarCaller.guessFromGenotype(gt));
	}

	@Test
	public void testGatk() throws Exception {
		String headerLines = GATK_HEADER;
		String vcfLine = "1\t17452\t.\tG\tA\t35.74\t.\t"
				+ "AC=2;AF=1.00;AN=2;DP=250;Dels=0.00;FS=0.000;GC=60.85;HRun=0;HaplotypeScore=0.0000;MLEAC=2;MLEAF=1.00;MQ=2.41;"
				+ "MQ0=248;QD=0.14;SOR=2.303\t" + "GT:AD:DP:GQ:PL\t1/1:117,133:250:6:63,6,0\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);
		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(SupportedVarCaller.GATK_CALLER, SupportedVarCaller.guessFromGenotype(gt));
	}

	@Test
	public void testPlatypus() throws Exception {
		String headerLines = PLATYPUS_HEADER;
		String vcfLine = "1\t17452\t.\tA\tG\t388\tPASS\tFR=1.0;MMLQ=27.0;TCR=7;HP=1;WE=14938;"
				+ "Source=Platypus;WS=14920;PP=388.0;TR=17;NF=10;TCF=10;NR=7;TC=17;MGOF=29;SbPval=0.59;MQ=46.59;"
				+ "QD=24.8688948557;SC=ACAGAATTACAAGGTGCTGGC;BRF=0.38;HapScore=2\t"
				+ "GT:GL:GOF:GQ:NR:NV\t1/1:-14.5,-0.9,0.0:29.0:10:5:5\n";
		VariantContext variant = writeAndReadVcfLine(vcfLine, headerLines);
		Genotype gt = variant.getGenotype("individual");
		Assert.assertEquals(SupportedVarCaller.PLATYPUS, SupportedVarCaller.guessFromGenotype(gt));
	}

}
