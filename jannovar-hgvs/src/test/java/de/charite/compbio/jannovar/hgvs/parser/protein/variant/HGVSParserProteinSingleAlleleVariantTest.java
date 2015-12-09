package de.charite.compbio.jannovar.hgvs.parser.protein.variant;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Hgvs_variantContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

/**
 * Parser for HGVS indel amino acid changes.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class HGVSParserProteinSingleAlleleVariantTest extends HGVSParserTestBase {

	@Test
	public void testWithOneChangeJustTranscript() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:p.Cys2Ala", Antlr4HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (aa_single_allele_var (aa_single_allele_single_change_var (reference NM_000109.3 :) p. (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithOneChangeWithProteinIsoform() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3 (DMD_i2):p.Cys2Ala", Antlr4HGVSLexer.DEFAULT_MODE,
				false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (aa_single_allele_var (aa_single_allele_single_change_var (reference NM_000109.3 ( DMD_i2 ) :) p. (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithOneChangeWithProteinVersion() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3 (DMD_v2):p.Cys2Ala", Antlr4HGVSLexer.DEFAULT_MODE,
				false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (aa_single_allele_var (aa_single_allele_single_change_var (reference NM_000109.3 ( DMD_v2 ) :) p. (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesSingleOrigin() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:p.[Cys2Ala,Arg3His]", Antlr4HGVSLexer.DEFAULT_MODE,
				false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (aa_single_allele_var (aa_single_allele_multi_change_var (reference NM_000109.3 :) p. (aa_multi_change_allele [ (aa_multi_change_allele_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))) (aa_var_sep ,) (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Arg) 3) (aa_char His))))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesInCis() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:p.[Cys2Ala;Arg3His]", Antlr4HGVSLexer.DEFAULT_MODE,
				false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (aa_single_allele_var (aa_single_allele_multi_change_var (reference NM_000109.3 :) p. (aa_multi_change_allele [ (aa_multi_change_allele_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))) (aa_var_sep ;) (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Arg) 3) (aa_char His))))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesUnknownCisTrans() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:p.[Cys2Ala(;)Arg3His]",
				Antlr4HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (aa_single_allele_var (aa_single_allele_multi_change_var (reference NM_000109.3 :) p. (aa_multi_change_allele [ (aa_multi_change_allele_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))) (aa_var_sep ( ; )) (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Arg) 3) (aa_char His))))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesUnknownMosaic() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:p.[Cys2Ala/Arg3His]", Antlr4HGVSLexer.DEFAULT_MODE,
				false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (aa_single_allele_var (aa_single_allele_multi_change_var (reference NM_000109.3 :) p. (aa_multi_change_allele [ (aa_multi_change_allele_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))) (aa_var_sep /) (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Arg) 3) (aa_char His))))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesUnknownChimeric() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:p.[Cys2Ala//Arg3His]",
				Antlr4HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (aa_single_allele_var (aa_single_allele_multi_change_var (reference NM_000109.3 :) p. (aa_multi_change_allele [ (aa_multi_change_allele_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 2) (aa_char Ala)))) (aa_var_sep //) (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Arg) 3) (aa_char His))))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

}
