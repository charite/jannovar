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
// Nucleotide changes
// --------------------------------------------------------------------------

nt_change
:
	nt_change_deletion
	| nt_change_duplication
	| nt_change_indel
	| nt_change_inversion
	| nt_change_insertion
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