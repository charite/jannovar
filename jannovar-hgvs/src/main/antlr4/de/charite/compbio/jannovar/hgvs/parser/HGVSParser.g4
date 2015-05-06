/** Antlr4 grammar for HGVS variant annotations.
 *
 *  Derived from
 *
 * Laros, J. F. J., Blavier, A., den Dunnen, J. T., & Taschner, P. E. M. (2011).
 * A formalized description of the standard human variant nomenclature in Extended
 * Backus-Naur Form. BMC Bioinformatics, 12(Suppl 4), S5.
 * http://doi.org/10.1186/1471-2105-12-S4-S5
 */
parser grammar HGVSParser;

options {
	tokenVocab = HGVSLexer;
} // use tokens from HGVSLexer

hgvs_variant
:
	protein_single_var
;

protein_single_var
:
	protein_reference protein_raw_var
;

protein_reference
:
	(
		REFERENCE REF_STOP
	)? PROTEIN_CHANGE_DESCRIPTION
;

protein_raw_var
:
	protein_substitution
	| protein_duplication
	| protein_deletion
	| protein_varying_short_sequence_repeat
	| protein_insertion
	| protein_indel
	| protein_frame_shift
	/* the base cases '=' | '?' | '0' | '0?' */
	| PROTEIN_EQUAL
	| PROTEIN_QUESTION_MARK
	| PROTEIN_ZERO
	| PROTEIN_QUESTION_MARK
	| PROTEIN_ZERO PROTEIN_QUESTION_MARK
;

protein_substitution
:
	PROTEIN_M1
	(
		PROTEIN_QUESTION_MARK
		| PROTEIN_EXT PROTEIN_MET? PROTEIN_MINUS PROTEIN_NUMBER
		| aa_or_terminal
	)
	| aa_pt_loc aa_or_terminal
	(
		PROTEIN_EXT PROTEIN_TERMINAL PROTEIN_NUMBER
	)?
;

protein_duplication
:
	aa_loc PROTEIN_DUP
;

protein_deletion
:
	aa_loc PROTEIN_DEL
;

protein_varying_short_sequence_repeat
:
	aa_loc PROTEIN_PAREN_OPEN PROTEIN_NUMBER PROTEIN_UNDERSCORE PROTEIN_NUMBER
	PROTEIN_PAREN_CLOSE
;

protein_insertion
:
	aa_range PROTEIN_INS
	(
		PROTEIN_AA+
		| PROTEIN_NUMBER
	)
;

protein_indel
:
	aa_loc PROTEIN_DELINS
	(
		PROTEIN_AA+
		| PROTEIN_NUMBER
	)
;

protein_frame_shift
:
	protein_short_fs
	| protein_long_fs
;

protein_short_fs
:
	aa_pt_loc PROTEIN_FS
;

protein_long_fs
:
	aa_pt_loc PROTEIN_AA PROTEIN_FS PROTEIN_TERMINAL PROTEIN_NUMBER
;

aa_loc
:
	aa_pt_loc
	| aa_range
;

aa_pt_loc
:
	aa_or_terminal protein_pt_loc
;

aa_or_terminal
:
	PROTEIN_AA
	| PROTEIN_TERMINAL
;

protein_pt_loc
:
	(
		PROTEIN_MINUS
		| PROTEIN_TERMINAL
	)? PROTEIN_NUMBER
	| PROTEIN_NUMBER
	(
		PROTEIN_PLUS
		| PROTEIN_MINUS
	) PROTEIN_NUMBER
;

aa_range
:
	protein_extent
	| PROTEIN_PAREN_OPEN protein_extent PROTEIN_PAREN_CLOSE
;

protein_extent
:
	aa_pt_loc PROTEIN_UNDERSCORE aa_pt_loc
; 