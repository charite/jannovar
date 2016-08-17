package de.charite.compbio.jannovar.vardbs.dbsnp;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class DBSNPVariantContextToRecordConverterTest {

	static String vcfPath;
	static VCFFileReader vcfReader;

	@BeforeClass
	public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();
		vcfPath = tmpDir + "/dbsnp.vcf.gz";
		ResourceUtils.copyResourceToFile("/dbSNP147.head.vcf.gz", new File(vcfPath));
		String tbiPath = tmpDir + "/dbsnp.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/dbSNP147.head.vcf.gz.tbi", new File(tbiPath));

		vcfReader = new VCFFileReader(new File(vcfPath));
	}

	@Test
	public void test() {
		DBSNPVariantContextToRecordConverter converter = new DBSNPVariantContextToRecordConverter();
		VariantContext vc = vcfReader.iterator().next();

		DBSNPRecord record = converter.convert(vc);
		Assert.assertEquals("DBSNPRecord [chrom=1, pos=10018, id=rs775809821, ref=TA, alt=[T], "
				+ "filter=[], rsID=775809821, rsPos=10020, reversed=false, "
				+ "variantProperty=null, geneInfos=[DBSNPGeneInfo [symbol=DDX11L1, id=100287102]], "
				+ "dbSNPBuildID=144, variantAlleleOrigin=UNSPECIFIED, "
				+ "variantSuspectReasonCode=[UNSPECIFIED], weights=1, variationClass=DIV, "
				+ "precious=false, thirdPartyAnnotation=false, pubMedCentral=false, threeDStructure=false, "
				+ "submitterLinkOut=false, nonSynonymousFrameShift=false, nonSynonymousMissense=false, "
				+ "nonSynonymousNonsense=false, reference=false, synonymous=false, inThreePrimeUTR=false, "
				+ "inFivePrimeUTR=false, inAcceptor=false, inDonor=false, inIntron=false, inThreePrime=false, "
				+ "inFivePrime=true, otherVariant=false, assemblyConflict=false, assemblySpecific=true,"
				+ " mutation=false, validated=false, fivePercentAll=false, fivePersonOne=false, "
				+ "highDensityGenotyping=false, genotypesAvailable=false, g1kPhase1=false, g1kPhase3=false, "
				+ "clinicalDiagnosticAssay=false, locusSpecificDatabase=false, "
				+ "microattributionThirdParty=false, hasOMIMOrOMIA=false, contigAlelleNotVariant=false, "
				+ "withdrawn=false, nonOverlappingAlleleSet=false, alleleFrequenciesG1K=[], "
				+ "common=false, oldVariants=[]]", record.toString());
	}

}
