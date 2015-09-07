/** Antlr4 grammar for HGVS variant annotations.
 *
 * Supports the subset of HGVS mutnomen that is relevant for annotating variant calls from NGS data.
 */
parser grammar Antlr4HGVSParser;

options {
	tokenVocab = Antlr4HGVSLexer;
} // use tokens from Antlr4HGVSLexer

/** top-level production rule for both nucleotide and protein variants */
hgvs_variant
:
	nt_single_allele_var
	| nt_multi_allele_var
	| aa_single_allele_var
	| aa_multi_allele_var
;

// --------------------------------------------------------------------------
// Reference description
// --------------------------------------------------------------------------

reference
:
	REFERENCE
	(
		PAREN_OPEN REFERENCE PAREN_CLOSE
	)? REF_STOP
;

// --------------------------------------------------------------------------
// Protein allele variants
// --------------------------------------------------------------------------

aa_single_allele_var
:
	aa_single_allele_single_change_var
	| aa_single_allele_multi_change_var
;

aa_single_allele_single_change_var
:
	reference AA_CHANGE_DESCRIPTION aa_change
;

aa_single_allele_multi_change_var
:
	reference AA_CHANGE_DESCRIPTION aa_multi_change_allele
;

aa_multi_allele_var
:
	reference AA_CHANGE_DESCRIPTION aa_multi_change_allele
	(
		AA_SEMICOLON aa_multi_change_allele
	)*
;

// --------------------------------------------------------------------------
// Protein allele
// --------------------------------------------------------------------------

aa_multi_change_allele
:
	AA_SQUARE_PAREN_OPEN
	(
		aa_multi_change_allele_inner
		|
		(
			AA_PAREN_OPEN aa_multi_change_allele_inner AA_PAREN_CLOSE
		)
	) AA_SQUARE_PAREN_CLOSE
;

aa_multi_change_allele_inner
:
	aa_change
	(
		aa_var_sep aa_change
	)*
;

aa_var_sep
:
	AA_COMMA
	| AA_SLASHES
	| AA_SEMICOLON
	|
	(
		AA_PAREN_OPEN AA_SEMICOLON AA_PAREN_CLOSE
	)
;

// --------------------------------------------------------------------------
// Protein changes
// --------------------------------------------------------------------------

aa_change
:
	aa_change_inner
	|
	(
		AA_PAREN_OPEN aa_change_inner AA_PAREN_CLOSE
	)
;

aa_change_inner
:
	aa_change_deletion
	| aa_change_duplication
	| aa_change_extension
	| aa_change_frameshift
	| aa_change_indel
	| aa_change_substitution
	| aa_change_ssr
	| aa_change_insertion
	| aa_change_misc
;

/** amino acid deletion */
aa_change_deletion
:
	(
		aa_point_location
		| aa_range
	) AA_DEL
	(
		AA_NUMBER
		| aa_string
	)?
;

/** amino acid duplication */
aa_change_duplication
:
	(
		aa_point_location
		| aa_range
	) AA_DUP
	(
		AA_NUMBER
		| aa_string
	)?
;

/** amino acid extension */
aa_change_extension
:
	aa_point_location aa_char? AA_EXT
	(
		(
			AA_TERMINAL
			(
				AA_NUMBER
				| AA_QUESTION_MARK
			)
		)
		|
		(
			AA_MINUS AA_NUMBER
		)
	)
;

/** amino acid frameshift */
aa_change_frameshift
:
	aa_point_location
	(
		(
			aa_char AA_FS
			(
				AA_TERMINAL
				(
					AA_NUMBER
					| AA_QUESTION_MARK
				)
			)
		)
		| AA_FS
	)
;

/** amino acid indel / delins / block substitution */
aa_change_indel
:
	(
		aa_point_location
		| aa_range
	) AA_DEL
	(
		AA_NUMBER
		| aa_string
	)? AA_INS
	(
		AA_NUMBER
		| aa_string
	)?
;

/** amino acid substitution */
aa_change_substitution
:
	aa_point_location
	(
		aa_char
		| AA_QUESTION_MARK
		| AA_EQUAL
	)
;

/** amino acid short sequence repeat variability */
aa_change_ssr
:
	(
		aa_point_location
		| aa_range
	) AA_PAREN_OPEN AA_NUMBER AA_UNDERSCORE AA_NUMBER AA_PAREN_CLOSE
;

/** amino acid short insertion */
aa_change_insertion
:
	aa_range AA_INS
	(
		AA_NUMBER
		| aa_string
	)?
;

/** amino acid misc change */
aa_change_misc
:
	AA_QUESTION_MARK
	| AA_EQUAL
	|
	(
		AA_PAREN_OPEN AA_EQUAL AA_PAREN_CLOSE
	)
	|
	(
		AA_ZERO AA_QUESTION_MARK?
	)
;

/** amino acid / protein point location */
aa_point_location
:
	aa_char
	(
		AA_MINUS
		| AA_TERMINAL
	)? AA_NUMBER
	(
		(
			AA_PLUS
			| AA_MINUS
		) AA_NUMBER
	)?
;

/** amino acid / protein range */
aa_range
:
	aa_point_location AA_UNDERSCORE aa_point_location
;

/** amino acid string*/
aa_string
:
	(
		AA_MET
		| AA_AA3
	)+
	|
	(
		AA_MET
		| AA_AA1
	)+
;

/** amino acid character */
aa_char
:
	AA_AA3
	| AA_AA1
	| AA_MET
	| AA_TERMINAL
;

// --------------------------------------------------------------------------
// Nucleotide allele variants
// --------------------------------------------------------------------------

nt_single_allele_var
:
	nt_single_allele_single_change_var
	| nt_single_allele_multi_change_var
;

nt_single_allele_single_change_var
:
	reference NT_CHANGE_DESCRIPTION nt_change
;

nt_single_allele_multi_change_var
:
	reference NT_CHANGE_DESCRIPTION nt_multi_change_allele
;

nt_multi_allele_var
:
	reference NT_CHANGE_DESCRIPTION nt_multi_change_allele
	(
		NT_SEMICOLON nt_multi_change_allele
	)*
;

// --------------------------------------------------------------------------
// Nucleotide allele
// --------------------------------------------------------------------------

nt_multi_change_allele
:
	NT_SQUARE_PAREN_OPEN
	(
		nt_multi_change_allele_inner
		|
		(
			NT_PAREN_OPEN nt_multi_change_allele_inner NT_PAREN_CLOSE
		)
	) NT_SQUARE_PAREN_CLOSE
;

nt_multi_change_allele_inner
:
	nt_change
	(
		nt_var_sep nt_change
	)*
;

nt_var_sep
:
	NT_COMMA
	| NT_SLASHES
	| NT_SEMICOLON
	|
	(
		NT_PAREN_OPEN NT_SEMICOLON NT_PAREN_CLOSE
	)
;
// --------------------------------------------------------------------------
// Nucleotide changes
// --------------------------------------------------------------------------

nt_change
:
	nt_change_inner
	|
	(
		NT_PAREN_OPEN nt_change_inner NT_PAREN_CLOSE
	)
;

nt_change_inner
:
	nt_change_deletion
	| nt_change_duplication
	| nt_change_indel
	| nt_change_insertion
	| nt_change_inversion
	| nt_change_substitution
	| nt_change_ssr
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

/** nucleotide short sequence repeat variability */
nt_change_ssr
:
	(
		nt_point_location
		| nt_range
	) NT_PAREN_OPEN NT_NUMBER NT_UNDERSCORE NT_NUMBER NT_PAREN_CLOSE
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

// --------------------------------------------------------------------------
// Legacy Variant Descriptions
// --------------------------------------------------------------------------

/** top-level production rule for legacy variant (using IVS or EX) */
legacy_variant
:
	reference legacy_change
;

legacy_change
:
	legacy_change_substitution
	| legacy_change_indel
	| legacy_change_insertion
	| legacy_change_deletion
;

/** legacy deletion */
legacy_change_deletion
:
	legacy_point_location NT_DEL
	(
		nt_number
		| nt_string
	)?
;

/** legacy replacement/indel/delins */
legacy_change_indel
:
	legacy_point_location NT_DEL
	(
		nt_number
		| nt_string
	)? NT_INS
	(
		nt_number
		| nt_string
	)?
;

/** legacy insertion */
legacy_change_insertion
:
	legacy_point_location NT_INS
	(
		nt_number
		| nt_string
	)?
;

/** legacy substitution */
legacy_change_substitution
:
	legacy_point_location NT_STRING NT_GT NT_STRING
;

legacy_point_location
:
	LEGACY_IVS_OR_EX nt_number
	(
		NT_MINUS
		| NT_PLUS
	) nt_number
;