package de.charite.compbio.jannovar.hgvs.parser.nts.variant;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Hgvs_variantContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

public class HGVSparserNucleotideSingleAlleleVariantTest extends HGVSParserTestBase {

	@Test
	public void testWithOneChangeJustTranscript() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:c.123A>C", Antlr4HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (nt_single_allele_var (nt_single_allele_single_change_var (reference NM_000109.3 :) c. (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 123))) A > C))))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithOneChangeWithProteinIsoform() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3 (DMD_i2):c.123A>C", Antlr4HGVSLexer.DEFAULT_MODE,
				false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (nt_single_allele_var (nt_single_allele_single_change_var (reference NM_000109.3 ( DMD_i2 ) :) c. (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 123))) A > C))))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithOneChangeWithProteinVersion() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3 (DMD_v2):c.123A>C", Antlr4HGVSLexer.DEFAULT_MODE,
				false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (nt_single_allele_var (nt_single_allele_single_change_var (reference NM_000109.3 ( DMD_v2 ) :) c. (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 123))) A > C))))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesSingleOrigin() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:c.[123A>C,124C>T]", Antlr4HGVSLexer.DEFAULT_MODE,
				false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (nt_single_allele_var (nt_single_allele_multi_change_var (reference NM_000109.3 :) c. (nt_multi_change_allele [ (nt_multi_change_allele_inner (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 123))) A > C))) (nt_var_sep ,) (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 124))) C > T)))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesInCis() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:c.[123A>C;124C>T]", Antlr4HGVSLexer.DEFAULT_MODE,
				false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (nt_single_allele_var (nt_single_allele_multi_change_var (reference NM_000109.3 :) c. (nt_multi_change_allele [ (nt_multi_change_allele_inner (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 123))) A > C))) (nt_var_sep ;) (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 124))) C > T)))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesUnknownCisTrans() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:c.[123A>C(;)124C>T]", Antlr4HGVSLexer.DEFAULT_MODE,
				false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (nt_single_allele_var (nt_single_allele_multi_change_var (reference NM_000109.3 :) c. (nt_multi_change_allele [ (nt_multi_change_allele_inner (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 123))) A > C))) (nt_var_sep ( ; )) (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 124))) C > T)))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesUnknownMosaic() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:c.[123A>C/124C>T]", Antlr4HGVSLexer.DEFAULT_MODE,
				false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (nt_single_allele_var (nt_single_allele_multi_change_var (reference NM_000109.3 :) c. (nt_multi_change_allele [ (nt_multi_change_allele_inner (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 123))) A > C))) (nt_var_sep /) (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 124))) C > T)))) ]))))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithMultiplesChangesUnknownChimeric() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:c.[123A>C//124C>T]", Antlr4HGVSLexer.DEFAULT_MODE,
				false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (nt_single_allele_var (nt_single_allele_multi_change_var (reference NM_000109.3 :) c. (nt_multi_change_allele [ (nt_multi_change_allele_inner (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 123))) A > C))) (nt_var_sep //) (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 124))) C > T)))) ]))))",
				hgvs_variant.toStringTree(parser));
	}
}
