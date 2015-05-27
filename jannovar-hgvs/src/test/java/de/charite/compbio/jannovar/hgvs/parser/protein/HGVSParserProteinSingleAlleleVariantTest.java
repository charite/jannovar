package de.charite.compbio.jannovar.hgvs.parser.protein;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Hgvs_variantContext;

/**
 * Parser for HGVS indel amino acid changes.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class HGVSParserProteinSingleAlleleVariantTest extends HGVSParserTestBase {

	@Test
	public void testWithOneChangeJustTranscript() {
		HGVSParser parser = buildParserForString("NM_000109.3:p.Cys2Ala", HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (protein_single_allele_var (protein_single_allele_single_change_var (protein_reference NM_000109.3 :) p. (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithOneChangeWithProteinIsoform() {
		HGVSParser parser = buildParserForString("NM_000109.3 (DMD_i2):p.Cys2Ala", HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (protein_single_allele_var (protein_single_allele_single_change_var (protein_reference NM_000109.3 ( DMD_i2 ) :) p. (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithOneChangeWithProteinVersion() {
		HGVSParser parser = buildParserForString("NM_000109.3 (DMD_v2):p.Cys2Ala", HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (protein_single_allele_var (protein_single_allele_single_change_var (protein_reference NM_000109.3 ( DMD_v2 ) :) p. (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesSingleOrigin() {
		HGVSParser parser = buildParserForString("NM_000109.3:p.[Cys2Ala,Arg3Hys]", HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (protein_single_allele_var (protein_single_allele_multi_change_var (protein_reference NM_000109.3 :) p. (protein_multi_change_allele [ (protein_single_allele_multi_change_var_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))) (protein_var_sep ,) (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Arg) 3) (aa_char H))))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesInCis() {
		HGVSParser parser = buildParserForString("NM_000109.3:p.[Cys2Ala;Arg3Hys]", HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (protein_single_allele_var (protein_single_allele_multi_change_var (protein_reference NM_000109.3 :) p. (protein_multi_change_allele [ (protein_single_allele_multi_change_var_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))) (protein_var_sep ;) (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Arg) 3) (aa_char H))))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesUnknownCisTrans() {
		HGVSParser parser = buildParserForString("NM_000109.3:p.[Cys2Ala(;)Arg3Hys]", HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (protein_single_allele_var (protein_single_allele_multi_change_var (protein_reference NM_000109.3 :) p. (protein_multi_change_allele [ (protein_single_allele_multi_change_var_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))) (protein_var_sep ( ; )) (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Arg) 3) (aa_char H))))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesUnknownMosaic() {
		HGVSParser parser = buildParserForString("NM_000109.3:p.[Cys2Ala/Arg3Hys]", HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (protein_single_allele_var (protein_single_allele_multi_change_var (protein_reference NM_000109.3 :) p. (protein_multi_change_allele [ (protein_single_allele_multi_change_var_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))) (protein_var_sep /) (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Arg) 3) (aa_char H))))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesUnknownChimeric() {
		HGVSParser parser = buildParserForString("NM_000109.3:p.[Cys2Ala//Arg3Hys]", HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (protein_single_allele_var (protein_single_allele_multi_change_var (protein_reference NM_000109.3 :) p. (protein_multi_change_allele [ (protein_single_allele_multi_change_var_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))) (protein_var_sep //) (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Arg) 3) (aa_char H))))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

}
