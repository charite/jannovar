package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import de.charite.compbio.jannovar.utils.ResourceUtils;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class VardbAnnotateTest {

	protected File tmpDir;
	protected String h2Path;
	protected Connection conn;
	protected String inputVcfPath;

	@BeforeEach
	public void setUp() throws SQLException {
		tmpDir = Files.createTempDir();

		h2Path = tmpDir + "/gnomad.exomes.h2.db";
		ResourceUtils.copyResourceToFile("/gnomad.exomes.h2.db", new File(h2Path));

		inputVcfPath = tmpDir + "/input.vcf";

		conn = DriverManager.getConnection(
			"jdbc:h2:"
				+ h2Path.substring(0, h2Path.length() - 6)
				+ ";TRACE_LEVEL_FILE=0;MV_STORE=FALSE;DB_CLOSE_ON_EXIT=FALSE;ACCESS_MODE_DATA=r",
			"sa",
			"");
	}

	/**
	 * Test simple annotation with H2 based database.
	 *
	 * Mostly a smoke test that annotates with a known variants from the first variants in gnomAD exomes r2.1.1 of
	 * a H2 file previously built and committed into the repository.
	 */
	@Test
	public void testAnnotateMonoallelicVar() throws JannovarVarDBException {
		ResourceUtils.copyResourceToFile("/gnomad.exomes.r2.1.1.monoallelic_var.vcf", new File(inputVcfPath));
		String outputVcfPath = tmpDir + "/output.vcf";

		AnnotateOptions options = new AnnotateOptions(
			"GRCh37",
			h2Path,
			inputVcfPath,
			outputVcfPath,
			ImmutableList.of("gnomad_exomes_r2_1_1")
		);
		AnnotateCommand cmd = new AnnotateCommand(options, conn);
		cmd.run(conn);

		VCFFileReader vcfReader = new VCFFileReader(new File(outputVcfPath), false);
		VariantContext vc = null;
		for (VariantContext record : vcfReader) {
			assertEquals(null, vc, "There should only be one record in the output file");
			vc = record;
		}
		assertNotEquals(null, vc);

		assertEquals("7.289e-06", vc.getCommonInfo().getAttribute("GNOMAD_EXOMES_AF").toString());
		assertEquals("137190", vc.getCommonInfo().getAttribute("GNOMAD_EXOMES_AN").toString());
		assertEquals("1", vc.getCommonInfo().getAttribute("GNOMAD_EXOMES_AC").toString());
	}

	/**
	 * Test annotation of triallelic var.
	 *
	 * The first and third allele have gnomAD annotations. Also see {@link #testAnnotateMonoallelicVar()}.
	 */
	@Test
	public void testAnnotateTriallelicVar() throws JannovarVarDBException {
		ResourceUtils.copyResourceToFile("/gnomad.exomes.r2.1.1.triallelic_var.vcf", new File(inputVcfPath));
		String outputVcfPath = tmpDir + "/output.vcf";

		AnnotateOptions options = new AnnotateOptions(
			"GRCh37",
			h2Path,
			inputVcfPath,
			outputVcfPath,
			ImmutableList.of("gnomad_exomes_r2_1_1")
		);
		AnnotateCommand cmd = new AnnotateCommand(options, conn);
		cmd.run(conn);

		VCFFileReader vcfReader = new VCFFileReader(new File(outputVcfPath), false);
		VariantContext vc = null;
		for (VariantContext record : vcfReader) {
			assertEquals(null, vc, "There should only be one record in the output file");
			vc = record;
		}
		assertNotEquals(null, vc);

		assertEquals("[1.458e-05, ., 7.289e-06]", vc.getCommonInfo().getAttribute("GNOMAD_EXOMES_AF").toString());
		assertEquals("137190", vc.getCommonInfo().getAttribute("GNOMAD_EXOMES_AN").toString());
		assertEquals("[2, ., 1]", vc.getCommonInfo().getAttribute("GNOMAD_EXOMES_AC").toString());
	}

	/**
	 * Test annotation of variant that is not in gnomAD.
	 */
	@Test
	public void testAnnotateUnknownVar() throws JannovarVarDBException {
		ResourceUtils.copyResourceToFile("/gnomad.exomes.r2.1.1.unknown_var.vcf", new File(inputVcfPath));
		String outputVcfPath = tmpDir + "/output.vcf";

		AnnotateOptions options = new AnnotateOptions(
			"GRCh37",
			h2Path,
			inputVcfPath,
			outputVcfPath,
			ImmutableList.of("gnomad_exomes_r2_1_1")
		);
		AnnotateCommand cmd = new AnnotateCommand(options, conn);
		cmd.run(conn);

		VCFFileReader vcfReader = new VCFFileReader(new File(outputVcfPath), false);
		VariantContext vc = null;
		for (VariantContext record : vcfReader) {
			assertEquals(null, vc, "There should only be one record in the output file");
			vc = record;
		}
		assertNotEquals(null, vc);

		assertEquals(null, vc.getCommonInfo().getAttribute("GNOMAD_EXOMES_AF"));
		assertEquals(null, vc.getCommonInfo().getAttribute("GNOMAD_EXOMES_AN"));
		assertEquals(null, vc.getCommonInfo().getAttribute("GNOMAD_EXOMES_AC"));
	}
}
