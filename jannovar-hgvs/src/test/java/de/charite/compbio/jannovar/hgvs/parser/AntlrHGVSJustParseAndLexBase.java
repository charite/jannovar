package de.charite.compbio.jannovar.hgvs.parser;

/**
 * Shared protein/nucleotide change strings for "JustParse" and "JustLex" tests.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class AntlrHGVSJustParseAndLexBase {

	// "c.(87+1_88-1)_(923+1_924-1)del", "c.(87+1_88-1)_(301+1_302-1)dup", "c.(?_-30)_(*220_?)del",

	/** nucleotide changes that should be parseable/lexable */
	public static String[] NT_STRINGS = { "c.3delG", "c.76_78del", "c.76_78delACT", "c.5_7del", "c.301-3delT",
			"c.77+1G>T", "c.76A>C", "c.-14G>C", "c.88+1G>T", "c.89-2A>C", "c.*46delT", "c.76_77delinsTT",
			"c.[76A>T; 77G>T]", "c.76_77delinsTT", "c.76_78del", "g.210_211dupTT", "c.76_78del", "c.76_78delACT",
			"g.5dupT", "g.7_8dup", "g.7_8dupTC", "c.77_79dup", "c.76_77insT", "c.112_117delinsTG",
			"c.113delinsTACTAGC", "c.113delGinsTACTAGC", "c.[114G>A; 115delT]", "c.203_506inv", "c.203_506inv304",
			"c.[76A>C; 83G>C]", "c.[76A>C];[83G>C]", "c.[76A>C];[?]", "c.[76A>C];[=]", "c.[76A>C];[0]", "c.[638T>A]",
			"c.[732C>G]", "c.[=//83G>C]", "c.123+74_123+75(3_6)", "g.1209_4523(12_45)" };

	/** legacy changes that should be parseable/lexable */
	public static String[] LEGACY_STRINGS = { "IVS3+3G>T", "IVS3-3G>T", "E3+3G>T", "E3-3G>T", "EX3+3G>T", "EX3-3G>T",
			"IVS3+3delG", "IVS3-3delGT", "E3+3delGTT", "E3-3del10", "EX3+3del1", "EX3-3del5", "IVS3+3insG",
			"IVS3-3insGT", "E3+3insGTT", "E3-3ins10", "EX3+3ins1", "EX3-3ins5", "IVS3+3delCTTAinsG", "IVS3-3del3insGT",
			"E3+3delCTTAinsGTT", "E3-3delCTTAins10", "EX3+3delCTTAins1", "EX3-3delCTTAins5", };

	/** protein changes that should be parseable/lexable */
	public static String[] PROTEIN_STRINGS = { "p.Gly2L", "p.G2L", "p.G2L", "p.Glu124Serfs*148", "p.Gln16dup",
			"p.E124Sfs*148", "p.Gln3_Leu7delinsGlnGlnTrpSerLeu", "p.Q3_L7delinsGlnGlnTrpSerLeu", "p.Met1Leu",
			"p.Met1?", "p.M1?", "p.[(Ala25Thr)(;)(Pro323Leu)]", "p.Arg83=", /* "p.[Arg83=/Arg83Ser]", */
			/* "p.[Arg83=//Arg83Ser]", */"p.[Ala25Thr(;)Pro323Leu]", "p.[(Ala25Thr)(;)(Pro323Leu)]",
			"p.[(Ala25Thr(;)Pro323Leu)]", "p.[Asn26His, Ala25_Gly29del]", "p.[(Ala25Thr; Gly28Val)]", "p.*327Argext*?",
			"p.Ter110GlnextTer17", "p.*110Qext*17", "p.Met1Valext-12", "p.Met1ext-5", "p.Arg97ProfsTer23" };

}
