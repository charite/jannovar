package de.charite.compbio.jannovar.hgvs.parser.nts.variant;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Hgvs_variantContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

public class HGVSparserNucleotideMultiAlleleVariantTest extends HGVSParserTestBase {

	@Test
	public void testWithOneChangePerAllele() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:c.[123A>C];[123A>C]", Antlr4HGVSLexer.DEFAULT_MODE,
				false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (nt_multi_allele_var (reference NM_000109.3 :) c. (nt_multi_change_allele [ (nt_multi_change_allele_inner (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 123))) A > C)))) ]) ; (nt_multi_change_allele [ (nt_multi_change_allele_inner (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 123))) A > C)))) ])))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithTwoChangesPerAllele() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:c.[123A>C,156C>T];[123A>C,156C>T]",
				Antlr4HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (nt_multi_allele_var (reference NM_000109.3 :) c. (nt_multi_change_allele [ (nt_multi_change_allele_inner (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 123))) A > C))) (nt_var_sep ,) (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 156))) C > T)))) ]) ; (nt_multi_change_allele [ (nt_multi_change_allele_inner (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 123))) A > C))) (nt_var_sep ,) (nt_change (nt_change_inner (nt_change_substitution (nt_point_location (nt_base_location (nt_number 156))) C > T)))) ])))",
				hgvs_variant.toStringTree(parser));
	}

}
