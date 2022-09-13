package de.charite.compbio.jannovar.htsjdk;

import com.google.common.io.Files;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.data.SerializationException;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;

public class RealWorldVcfsTest {

	/**
	 * path to Jannovar database file
	 */
	static String dbPath;
	/**
	 * path to VCF file to annotate
	 */
	static String vcfPath;
	/**
	 * Jannovar database
	 */
	JannovarData jannovarData;

	@BeforeAll
	public static void setUpClass() throws Exception {
		// copy out files to temporary directory
		File tmpDir = Files.createTempDir();
		dbPath = tmpDir + "/hg19_refseq.ser";
		ResourceUtils.copyResourceToFile("/hg19_refseq.ser", new File(dbPath));
		vcfPath = tmpDir + "/NA-12878WGS_dragen.cnv.vcf";
		ResourceUtils.copyResourceToFile("/NA-12878WGS_dragen.cnv.vcf", new File(vcfPath));
	}

	@BeforeEach
	public void setUp() throws FileNotFoundException, SerializationException {
		this.jannovarData = new JannovarDataSerializer(dbPath).load();
	}

	@Test
	public void runOnDragenCnv() throws InvalidBreakendDescriptionException, MultipleSVAlleles, MissingSVTypeInfoField, MissingEndInfoField, InvalidCoordinatesException {
		VariantContextAnnotator annotator = new VariantContextAnnotator(jannovarData.getRefDict(), jannovarData.getChromosomes(),
			new VariantContextAnnotator.Options(false, AminoAcidCode.ONE_LETTER, false, false, false, false,
				false));

		final VCFFileReader vcfReader = new VCFFileReader(new File(vcfPath), false);
		for (VariantContext record : vcfReader) {
			if (record.getNAlleles() == 1) {
				continue;  // skip reference records
			}
			annotator.buildSVAnnotations(record);
		}
	}

}
