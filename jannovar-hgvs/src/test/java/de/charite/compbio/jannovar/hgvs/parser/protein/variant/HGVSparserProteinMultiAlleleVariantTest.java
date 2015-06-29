package de.charite.compbio.jannovar.hgvs.parser.protein.variant;

import org.junit.Assert;
import org.junit.Test;

import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSLexer;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Hgvs_variantContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParserTestBase;

public class HGVSparserProteinMultiAlleleVariantTest extends HGVSParserTestBase {

	@Test
	public void testWithOneChangePerAllele() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:p.[Cys23Ala];[Ala23Cys]",
				Antlr4HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (aa_multi_allele_var (reference NM_000109.3 :) p. (aa_multi_change_allele [ (aa_multi_change_allele_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 23) (aa_char Ala))))) ]) ; (aa_multi_change_allele [ (aa_multi_change_allele_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Ala) 23) (aa_char Cys))))) ])))",
				hgvs_variant.toStringTree(parser));
	}

	@Test
	public void testWithTwoChangesPerAllele() {
		Antlr4HGVSParser parser = buildParserForString("NM_000109.3:p.[Cys23Ala,Thr44Cys];[Ala23Cys,Thr44Ala]",
				Antlr4HGVSLexer.DEFAULT_MODE, false);
		Hgvs_variantContext hgvs_variant = parser.hgvs_variant();
		Assert.assertEquals(
				"(hgvs_variant (aa_multi_allele_var (reference NM_000109.3 :) p. (aa_multi_change_allele [ (aa_multi_change_allele_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Cys) 23) (aa_char Ala)))) (aa_var_sep ,) (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Thr) 44) (aa_char Cys))))) ]) ; (aa_multi_change_allele [ (aa_multi_change_allele_inner (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Ala) 23) (aa_char Cys)))) (aa_var_sep ,) (aa_change (aa_change_inner (aa_change_substitution (aa_point_location (aa_char Thr) 44) (aa_char Ala))))) ])))",
				hgvs_variant.toStringTree(parser));
	}

}
