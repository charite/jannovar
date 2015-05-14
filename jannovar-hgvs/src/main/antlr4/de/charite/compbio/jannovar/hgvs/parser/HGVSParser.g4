/** Antlr4 grammar for HGVS variant annotations.
 *
 * Derived from
 *
 * Laros, J. F. J., Blavier, A., den Dunnen, J. T., & Taschner, P. E. M. (2011).
 * A formalized description of the standard human variant nomenclature in Extended
 * Backus-Naur Form. BMC Bioinformatics, 12(Suppl 4), S5.
 * http://doi.org/10.1186/1471-2105-12-S4-S5
 *
 * Limitations
 *
 * * mosaicism and chimerism not supported yet in proteins
 */
parser grammar HGVSParser;

options {
	tokenVocab = HGVSLexer;
} // use tokens from HGVSLexer

hgvs_variant
:
	nt_single_var
	| nt_multi_var
	| nt_multi_transcript_var
	| nt_unknown_effect_var
	| nt_no_rna_var
	| nt_splicing_var
	| protein_single_var
	| protein_multi_var
;

nt_single_var
:
/* TODO(holtgrew): Rule TransLoc missing for now */
	nt_reference nt_raw_var
;

nt_multi_var
:
	nt_single_allele_vars
	| nt_multi_allele_vars
;

nt_multi_transcript_var
:
	nt_reference NT_SQUARE_PAREN_OPEN nt_extended_raw_var
	(
		NT_SEMICOLON nt_extended_raw_var
	)*
	(
		NT_COMMA nt_extended_raw_var
		(
			NT_SEMICOLON nt_extended_raw_var
		)*
	)+ NT_SQUARE_PAREN_CLOSE
;

nt_unknown_effect_var
:
	nt_reference
	(
		NT_PAREN_OPEN NT_EQUAL NT_PAREN_CLOSE
		| NT_QUESTION_MARK
	)
;

nt_no_rna_var
:
	nt_reference NT_ZERO NT_QUESTION_MARK?
;

nt_single_allele_vars
:
	nt_reference nt_single_allele_var_set
;

nt_splicing_var
:
	nt_reference
	(
		NT_SPL NT_QUESTION_MARK
		| NT_PAREN_OPEN NT_SPL NT_QUESTION_MARK NT_PAREN_CLOSE
	)
;

nt_multi_allele_vars
:
	nt_reference nt_single_allele_var_set
	(
		NT_SEMICOLON nt_reference? nt_single_allele_var_set
	)+
;

nt_single_allele_var_set
:
	(
		NT_SQUARE_PAREN_OPEN nt_chimeron_set
		(
			(
				NT_SEMICOLON
				| NT_CIRCUMFLEX
			) nt_chimeron_set
		)*
		(
			NT_PAREN_OPEN NT_SEMICOLON NT_PAREN_CLOSE nt_chimeron_set
		)* NT_SQUARE_PAREN_CLOSE
	)
	| nt_chimeron_set
;

nt_chimeron_set
:
	(
		NT_SQUARE_PAREN_OPEN nt_mosaic_set
		(
			NT_DOUBLE_SLASH nt_mosaic_set
		)* NT_SQUARE_PAREN_CLOSE
	)
	| nt_mosaic_set
;

nt_mosaic_set
:
	(
		NT_SQUARE_PAREN_OPEN nt_simple_allele_var_set
		(
			NT_SLASH nt_simple_allele_var_set
		)* NT_SQUARE_PAREN_CLOSE
	)
	| nt_simple_allele_var_set
;

nt_simple_allele_var_set
:
	(
		NT_SQUARE_PAREN_OPEN nt_u_allele_var_set NT_SQUARE_PAREN_CLOSE
	)
	| nt_extended_raw_var
;

nt_extended_raw_var
:
	nt_raw_var
	| NT_EQUAL
	| NT_QUESTION_MARK
;

nt_u_allele_var_set
:
	(
		nt_c_allele_var_set
		|
		(
			NT_PAREN_OPEN nt_c_allele_var_set NT_PAREN_CLOSE
		)
	) NT_QUESTION_MARK?
;

nt_c_allele_var_set
:
	nt_extended_raw_var
	(
		NT_SEMICOLON nt_extended_raw_var
	)*
;

nt_nest
:
	NT_BRACE_OPEN nt_simple_allele_var_set NT_BRACE_CLOSE
;

nt_reference
:
	(
		REFERENCE REF_STOP
	)? NT_CHANGE_DESCRIPTION
;

nt_raw_var
:
	(
		NT_PAREN_OPEN nt_raw_var_inner NT_PAREN_CLOSE
	)
	| nt_raw_var_inner
;

nt_raw_var_inner
:
	nt_substitution
	| nt_deletion
	| nt_duplication
	| nt_varying_short_sequence_repeat
	| nt_insertion
	| nt_indel
	| nt_inversion
	| nt_no_change
	| NT_NUMBER /* in the case of second allele of SRR */
	| NT_ZERO /* to be used in [0] only */
	/* TODO(holtgrewe): | nt_conversion */
;

nt_substitution
:
	nt_pt_loc NT_CHAR NT_GT NT_CHAR
;

nt_indel
:
	nt_loc NT_DEL
	(
		NT_CHAR+
		| NT_NUMBER
	)? NT_INS
	(
		NT_CHAR+
		| NT_NUMBER nt_range_loc /* TODO(holtgrewe): allow farloc here */
	) nt_nest?
;

nt_deletion
:
	nt_loc NT_DEL
	(
		NT_CHAR+
		| NT_NUMBER
	)?
;

nt_duplication
:
	nt_loc NT_DUP
	(
		NT_CHAR+
		| NT_NUMBER
	)? nt_nest?
;

nt_abr_ssr
:
	nt_loc NT_CHAR* NT_PAREN_OPEN NT_NUMBER
	(
		NT_UNDERSCORE NT_NUMBER
	)? NT_PAREN_CLOSE
;

nt_varying_short_sequence_repeat
:
	(
		nt_pt_loc NT_CHAR+ NT_SQUARE_PAREN_OPEN NT_NUMBER NT_SQUARE_PAREN_CLOSE
	)
	|
	(
		nt_pt_range_loc NT_SQUARE_PAREN_OPEN NT_NUMBER NT_SQUARE_PAREN_CLOSE
	)
	| nt_abr_ssr
;

nt_insertion
:
/* TODO(holtgrew): allow far loc */
	nt_range_loc NT_INS
	(
		NT_CHAR+
		| NT_NUMBER
		| nt_range_loc
	) nt_nest?
;

nt_inversion
:
	nt_range_loc NT_INV
	(
		NT_CHAR+
		| NT_NUMBER
	)? nt_nest?
;

nt_no_change
:
	nt_pt_loc NT_CHAR NT_EQUAL
	| nt_range_loc NT_CHAR+ NT_EQUAL
;

nt_range_loc
:
	nt_extent
	| NT_PAREN_OPEN nt_extent NT_PAREN_CLOSE
;

nt_extent
:
	nt_real_extent
	| nt_ex_loc
;

nt_real_extent
:
	nt_pt_loc NT_UNDERSCORE
	(
		NT_OPPOSITE?
		(
			REFERENCE
		) NT_COLON
	)? nt_pt_loc
;

nt_ex_loc
:
	NT_EX NT_NUMBER
	(
		NT_MINUS NT_NUMBER
	)?
;

nt_pt_range_loc
:
	nt_extent
	| NT_PAREN_OPEN nt_extent NT_PAREN_CLOSE
;

nt_pt_loc
:
	nt_ivs_loc
	| nt_real_pt_loc
;

nt_ivs_loc
:
	NT_IVS NT_NUMBER
	(
		NT_PLUS
		| NT_MINUS
	) NT_NUMBER
;

nt_real_pt_loc
:
	(
		(
			NT_MINUS
			| NT_ASTERISK
		)? NT_NUMBER nt_offset?
	)
	| NT_QUESTION_MARK
;

nt_offset
:
	(
		NT_PLUS
		| NT_MINUS
	)
	(
		NT_UPSTREAM
		| NT_DOWNSTREAM
	)?
	(
		NT_NUMBER
		| NT_QUESTION_MARK
	)
;

nt_loc
:
	nt_pt_loc
	| nt_range_loc
;

protein_single_var
:
	protein_reference protein_raw_var
;

protein_reference
:
	(
	/* TODO(holtgrew): reference name is missing here */
		REFERENCE REF_STOP
	)? PROTEIN_CHANGE_DESCRIPTION
;

protein_raw_var
:
	(
		PROTEIN_PAREN_OPEN protein_raw_var_inner PROTEIN_PAREN_CLOSE
	)
	| protein_raw_var_inner
;

protein_raw_var_inner
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
		|
		(
			aa_or_terminal
			| PROTEIN_EQUAL
		)
	)
	| aa_pt_loc
	(
		aa_or_terminal
		| PROTEIN_EQUAL
	)
	(
		PROTEIN_EXT PROTEIN_TERMINAL
		(
			PROTEIN_NUMBER
			| PROTEIN_QUESTION_MARK
		)
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

/* note that UnkAlleleVars is mapped to protein-single_allele_var, using '(;') for infix separator */
protein_multi_var
:
	protein_single_allele_var
	| protein_multi_allele_var
;

protein_single_allele_var
:
	protein_reference protein_single_allele_var_set
;

protein_single_allele_var_set
:
	PROTEIN_SQUARE_PAREN_OPEN
	(
		protein_single_allele_var_set_inner
		| protein_single_allele_var_set_inner_paren
	) PROTEIN_SQUARE_PAREN_CLOSE
;

protein_single_allele_var_set_inner_paren
:
	PROTEIN_PAREN_OPEN protein_single_allele_var_set_inner PROTEIN_PAREN_CLOSE
;

protein_single_allele_var_set_inner
:
	protein_raw_var
	(
		(
			PROTEIN_SEMICOLON protein_raw_var
		)+
		|
		(
			PROTEIN_COMMA protein_raw_var
		)+
		|
		(
			PROTEIN_PAREN_OPEN PROTEIN_SEMICOLON PROTEIN_PAREN_CLOSE protein_raw_var
		)
	)
;

/* TODO(holtgrew): semicolon can be in parenthesis indicating that in cis/trans state is unknown */
protein_multi_allele_var
:
	protein_reference protein_single_allele_var_set PROTEIN_SEMICOLON
	protein_reference? protein_single_allele_var_set
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