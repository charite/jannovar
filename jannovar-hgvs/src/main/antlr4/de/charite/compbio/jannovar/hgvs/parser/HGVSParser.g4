/** Antlr4 grammar for HGVS variant annotations.
 *
 * Supports the subset of HGVS mutnomen that is relevant for annotating variant calls from NGS data.
 */
parser grammar HGVSParser;

options {
	tokenVocab = HGVSLexer;
} // use tokens from HGVSLexer

/** top-level production rule for both nucleotide and protein variants */
hgvs_variant
:
;

// --------------------------------------------------------------------------
// Protein changes
// --------------------------------------------------------------------------

aa_change
:
	aa_change_deletion
	| aa_change_duplication
	| aa_change_indel
;

/** amino acid deletion */
aa_change_deletion
:
	(
		aa_point_location
		| aa_range
	) PROTEIN_DEL
	(
		PROTEIN_NUMBER
		| aa_string
	)?
;

/** amino acid duplication */
aa_change_duplication
:
	(
		aa_point_location
		| aa_range
	) PROTEIN_DUP
	(
		PROTEIN_NUMBER
		| aa_string
	)?
;

/** amino acid indel / delins / block substitution */
aa_change_indel
:
	(
		aa_point_location
		| aa_range
	) PROTEIN_DEL
	(
		PROTEIN_NUMBER
		| aa_string
	)? PROTEIN_INS
	(
		PROTEIN_NUMBER
		| aa_string
	)?
;

/** amino acid / protein point location */
aa_point_location
:
	aa_char
	(
		PROTEIN_MINUS
		| PROTEIN_TERMINAL
	)? PROTEIN_NUMBER
	(
		(
			PROTEIN_PLUS
			| PROTEIN_MINUS
		) PROTEIN_NUMBER
	)?
;

/** amino acid / protein range */
aa_range
:
	aa_point_location PROTEIN_UNDERSCORE aa_point_location
;

/** amino acid string*/
aa_string
:
	PROTEIN_AA3+
	| PROTEIN_AA1+
;

/** amino acid character */
aa_char
:
	PROTEIN_AA3
	| PROTEIN_AA1
;

// --------------------------------------------------------------------------
// Nucleotide changes
// --------------------------------------------------------------------------

nt_change
:
	nt_change_deletion
	| nt_change_duplication
	| nt_change_indel
	| nt_change_insertion
	| nt_change_inversion
	| nt_change_substitution
	| nt_change_misc
;

/** nucleotide deletion */
nt_change_deletion
:
	(
		nt_point_location
		| nt_range
	) NT_DEL
	(
		nt_number
		| nt_string
	)?
;

/** nucleotide duplication */
nt_change_duplication
:
	(
		nt_point_location
		| nt_range
	) NT_DUP
	(
		nt_number
		| nt_string
	)?
;

/** nucleotide replacement/indel/delins */
nt_change_indel
:
	(
		nt_point_location
		| nt_range
	) NT_DEL
	(
		nt_number
		| nt_string
	)? NT_INS
	(
		nt_number
		| nt_string
	)?
;

/** nucleotide inversion */
nt_change_inversion
:
	nt_range NT_INV
	(
		nt_number
		| nt_string
	)?
;

/** nucleotide insertion */
nt_change_insertion
:
	nt_range NT_INS
	(
		nt_number
		| nt_string
	)?
;

/** nucleotide substitution */
nt_change_substitution
:
	nt_point_location NT_STRING NT_GT NT_STRING
;

/** miscellaneous change specifications */
nt_change_misc
:
	(
		NT_PAREN_OPEN NT_QUESTION_MARK NT_PAREN_CLOSE
	)
	| NT_QUESTION_MARK
	|
	(
		NT_SPL NT_QUESTION_MARK
	)
	|
	(
		NT_PAREN_OPEN NT_SPL NT_QUESTION_MARK NT_PAREN_CLOSE
	)
	|
	(
		NT_PAREN_OPEN NT_EQUAL NT_PAREN_CLOSE
	)
	| NT_EQUAL
	|
	(
		NT_PAREN_OPEN NT_ZERO NT_PAREN_CLOSE
	)
	| NT_ZERO
;

// --------------------------------------------------------------------------
// Locations in nucleotide changes
// --------------------------------------------------------------------------

/** number in nucleotide variants */
nt_point_location
:
	nt_base_location nt_offset?
;

/** number used when parsing nucleotide variants */
nt_number
:
	NT_NUMBER
;

/** (coding) base location in a nt_point_location */
nt_base_location
:
	(
		NT_MINUS
		| NT_ASTERISK
	)? nt_number
;

/** offset in a nt_point_location */
nt_offset
:
	(
		NT_PLUS
		| NT_MINUS
	) nt_number
;

/** range in nucleotide variants*/
nt_range
:
	nt_point_location NT_UNDERSCORE nt_point_location
;

// --------------------------------------------------------------------------
// Nucleotide Strings
// --------------------------------------------------------------------------

/** String of nucleotide characters */
nt_string
:
	NT_STRING
;