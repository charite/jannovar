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

// Locations

aa_loc
:
	aa_pt_loc
	| aa_range
;

aa_pt_loc
:
	PROTEIN_AA pt_loc
;

pt_loc
:
	(
		TOKEN_MINUS
		| TOKEN_ASTERISK
	)? NUMBER
	| NUMBER
	(
		TOKEN_PLUS
		| TOKEN_MINUS
	) NUMBER
;

aa_range
:
	extent
	| TOKEN_PAREN_OPEN extent TOKEN_PAREN_CLOSE
;

extent
:
	aa_pt_loc TOKEN_UNDERSCORE aa_pt_loc
;

// Reference sequences

ref
:
	TOKEN_P_DOT
	//| NAME TOKEN_COLON TOKEN_P_DOT
;

// Single Variations

raw_var
:
	subst
	| del
	| dup
	| var_ssr
	| TOKEN_EQUAL
	| TOKEN_QUESTION_MARK
	| TOKEN_ZERO
	| TOKEN_ZERO TOKEN_QUESTION_MARK
;

subst
:
	aa_pt_loc PROTEIN_AA
	(
		TOKEN_EXT TOKEN_X TOKEN_ASTERISK? NUMBER
	)?
	| TOKEN_M1
	(
		TOKEN_QUESTION_MARK
		| TOKEN_EXT TOKEN_MINUS NUMBER
	)
;

del
:
	aa_loc TOKEN_DEL
;

dup
:
	aa_loc TOKEN_DUP
;

var_ssr
:
	aa_loc TOKEN_PAREN_OPEN NUMBER TOKEN_UNDERSCORE NUMBER TOKEN_PAREN_CLOSE
;

ins
:
	aa_range TOKEN_INS
	(
		PROTEIN_AA+
		| NUMBER
	)
;

indel
:
	aa_loc TOKEN_DELINS
	(
		PROTEIN_AA+
		| NUMBER
	)
;

frame_shift
:
	short_fs
	| long_fs
;

short_fs
:
	aa_pt_loc TOKEN_FS
;

long_fs
:
	aa_pt_loc PROTEIN_AA TOKEN_FS TOKEN_X NUMBER
;

// Top-level Rule

hgvs
:
	protein_var
	//    | dna_var

;

/** Variant in a protein. */
protein_var
:
	single_var
	//| multi_var

;

single_var
:
	ref raw_var
;