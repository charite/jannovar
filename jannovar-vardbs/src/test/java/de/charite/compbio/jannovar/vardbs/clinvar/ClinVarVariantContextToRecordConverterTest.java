package de.charite.compbio.jannovar.vardbs.clinvar;

import java.io.File;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.common.io.Files;

import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

public class ClinVarVariantContextToRecordConverterTest {

	static String vcfPath;
	static VCFFileReader vcfReader;

	@BeforeClass
	public static void setUpClass() throws Exception {
		File tmpDir = Files.createTempDir();
		vcfPath = tmpDir + "/clinvar.vcf.gz";
		ResourceUtils.copyResourceToFile("/clinvar_20161003.head.vcf.gz", new File(vcfPath));
		String tbiPath = tmpDir + "/clinvar.vcf.gz.tbi";
		ResourceUtils.copyResourceToFile("/clinvar_20161003.head.vcf.gz.tbi", new File(tbiPath));

		vcfReader = new VCFFileReader(new File(vcfPath));
	}

	@Test
	public void test() {
		ClinVarVariantContextToRecordConverter converter = new ClinVarVariantContextToRecordConverter();
		CloseableIterator<VariantContext> it = vcfReader.iterator();
		VariantContext vc = null;
		while (vc == null || !vc.getID().equals("rs387907305"))
			vc = it.next();

		ClinVarRecord record = converter.convert(vc);
		Assert.assertEquals("ClinVarRecord [chrom=1, pos=2160305, id=rs387907305, ref=G, alt=[A, T], "
				+ "annotations={1=[ClinVarAnnotation [alleleMapping=1, hgvsVariant=NC_000001.10:g.2160306G>A, "
				+ "sourceInfos=[ClinVarAnnotationSourceInfo [dbName=OMIM_Allelic_Variant, dbId=164780.0003], "
				+ "ClinVarAnnotationSourceInfo [dbName=UniProtKB_(protein), dbId=P12755#VAR_071175]], origin=[BIPARENTAL], "
				+ "diseaseInfos=[ClinVarDiseaseInfo [significance=PATHOGENIC, diseaseDB=MedGen:OMIM:SNOMED_CT, "
				+ "diseaseDBID=C1321551:182212:83092002, diseaseDBName=Shprintzen-Goldberg_syndrome, revisionStatus=SINGLE, "
				+ "ClinicalAccession=RCV000030818.28]]]], 2=[ClinVarAnnotation [alleleMapping=2, hgvsVariant=NC_000001.10:g.2160306G>T, "
				+ "sourceInfos=[ClinVarAnnotationSourceInfo [dbName=OMIM_Allelic_Variant, dbId=164780.0007], "
				+ "ClinVarAnnotationSourceInfo [dbName=UniProtKB_(protein), dbId=P12755#VAR_071177]], origin=[UNKNOWN], "
				+ "diseaseInfos=[ClinVarDiseaseInfo [significance=PATHOGENIC, diseaseDB=MedGen:OMIM:SNOMED_CT, "
				+ "diseaseDBID=C1321551:182212:83092002, diseaseDBName=Shprintzen-Goldberg_syndrome, revisionStatus=NO_CRITERIA, "
				+ "ClinicalAccession=RCV000033005.27]]]]}]", record.toString());
	}

}
