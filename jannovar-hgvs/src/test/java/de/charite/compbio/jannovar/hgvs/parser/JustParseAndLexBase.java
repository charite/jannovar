package de.charite.compbio.jannovar.hgvs.parser;

/**
 * Shared protein/nucleotide change strings for "JustParse" and "JustLex" tests.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class JustParseAndLexBase {

	// "c.(87+1_88-1)_(923+1_924-1)del", "c.(87+1_88-1)_(301+1_302-1)dup", "c.(?_-30)_(*220_?)del",

	/** nucleotide changes that should be parseable/lexable */
	public static String[] NT_STRINGS = { "c.3delG", "c.76_78del", "c.76_78delACT", "c.88-?_923+?del", "c.5_7del",
			"c.301-3delT", "c.77+1G>T", "c.76A>C", "c.-14G>C", "c.88+1G>T", "c.89-2A>C", "c.*46delT",
			"c.76_77delinsTT", "c.[76A>T; 77G>T]", "c.76_77delinsTT", "c.76_78del", "g.210T[5]", "g.210_211dupTT",
			"c.76_78del", "c.76_78delACT", "g.5dupT", "g.7_8dup", "g.7_8dupTC", "g.7_8[4]", "c.77_79dup",
			"c.88-?_301+?dup", "c.76_77insT", "c.112_117delinsTG", "c.113delinsTACTAGC", "c.113delGinsTACTAGC",
			"c.[114G>A; 115delT]", "c.203_506inv", "c.203_506inv304", "c.[76A>C; 83G>C]", "c.[76A>C];[83G>C]",
			"c.[76A>C];[?]", "c.[76A>C];[=]", "c.[76A>C];[0]", "hg19 chrX:g.[30683643A>G;33038273T>G]",
			"GJB2:c.[638T>A]", "GJB4:c.[732C>G]", "c.[83G=/83G>C]", "c.[=//83G>C]", "g.123TG[4]",
			"g.456TG[4]TA[9]TG[3]", "g.123_124[4]", "c.123+74TG(3_6)", "c.123+74_123+75(3_6)", "c.123+74TG[4];[5]",
			"c.-128_-126[79]", "c.-128GGC[79]", "c.1032-?_1357+?(3)", "c.1032-?_1357+?[3]", "g.1209_4523(12_45)",
			"g.1209_4523[14];[23]" };

	/** protein changes that should be parseable/lexable */
	public static String[] PROTEIN_STRINGS = { "p.Gly2L", "p.G2L", "p.G2L", "p.Glu124Serfs*148",
			"p.Gln16dup", "p.E124Sfs*148", "p.Gln3_Leu7delinsGlnGlnTrpSerLeu", "p.Q3_L7delinsGlnGlnTrpSerLeu",
			"p.Met1Leu", "p.Met1?", "p.M1?", "p.[(Ala25Thr)(;)(Pro323Leu)]", "p.Arg83=", /* "p.[Arg83=/Arg83Ser]", */
			/* "p.[Arg83=//Arg83Ser]", */"p.[Ala25Thr(;)Pro323Leu]", "p.[(Ala25Thr)(;)(Pro323Leu)]",
			"p.[(Ala25Thr(;)Pro323Leu)]", "p.[Asn26His, Ala25_Gly29del]", "p.[(Ala25Thr; Gly28Val)]", "p.*327Argext*?",
			"p.Ter110GlnextTer17", "p.*110Qext*17", "p.Met1Valext-12", "p.Met1ext-5", "p.Arg97ProfsTer23" };

}
