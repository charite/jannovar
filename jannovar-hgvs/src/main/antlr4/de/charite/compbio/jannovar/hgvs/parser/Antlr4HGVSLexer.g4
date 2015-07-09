/** Lexer for the HGVS mutation nomenclature.
 *
 * Attempts to cover the most important cases for NGS-based analyses.
 *
 * Current limitations:
 *
 * - referencing references for end positions or insertions is currently not
 *   supported, e.g. the following from the HGVS website will not work:
 *
 *   - g.123_678conNG_012232.1:g.9456_10011
 *   - c.88+101_oGJB2:c.355-1045del
 *   - c.123+54_123+55insAB012345.2:g.76_420
 *   - g.123_678conNG_012232.1:g.9456_10011
 *   - c.[NM_000167.5:94A>G;NM_004006.2:76A>C]
 *   - c.123_678conNM_004006.1:c.123_678
 *   - c.88+101_oGJB2:c.355-1045del
 *   - c.123+54_123+55insAB012345.2:g.76_420
 *   - c.[GK:94A>G;DMD:76A>C]
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
lexer grammar Antlr4HGVSLexer;

/** anything that does not match "p." or "[cmngr]." starts a reference description */
REFERENCE
:
	(
		(
			[abdefh-lo-qs-zA-Z0-9] REF_IDENTIFIER*
		)
		|
		(
			REF_IDENTIFIER REF_IDENTIFIER+
		)
	)
	(
		'.' [1-9] [0-9]* // optional version

	)?
;

/** fragment used for reference identifier */
fragment
REF_IDENTIFIER
:
	[_a-zA-Z0-9_]+
;

/** opening parenthesis */
PAREN_OPEN
:
	'('
;

/** closing parenthesis */
PAREN_CLOSE
:
	')'
;

/** token used for stopping the reference description */
REF_STOP
:
	':' -> pushMode ( CHANGE_BRANCH )
;

/** skip line breaks, spaces are kept */
LINE_BREAKS
:
	[ \r\n\t] -> skip
;

/* the following rules branch into the NUCLEOTIDE_CHANGE or the AMINO_ACID_CHANGE mode */
mode CHANGE_BRANCH;

/** fire off protein change description*/
AA_CHANGE_DESCRIPTION
:
	'p.' -> pushMode ( AMINO_ACID_CHANGE )
;

/** fire off nucleotide change description */
NT_CHANGE_DESCRIPTION
:
	(
		'c.'
		| 'm.'
		| 'n.'
		| 'g.'
		| 'r.'
	) -> pushMode ( NUCLEOTIDE_CHANGE )
;

/** legacy positions start with "IVS", "EX", or "E" */
LEGACY_IVS_OR_EX
:
	(
		'IVS'
		| 'EX'
		| 'E'
	) -> pushMode ( NUCLEOTIDE_CHANGE )
;

/* Lexing of nucleotide changes
 */
mode NUCLEOTIDE_CHANGE;

/** whitespace is ignored */
NT_CHANGE_SPACE
:
	' ' -> skip
;

/** line breaks are ignored and end the nucleotide change parsing*/
NT_CHANGE_LINE_BREAK
:
	[\t\r\n] -> popMode , skip
;

/** a nucleotide character */
fragment
NT_CHAR
:
	'A'
	| 'C'
	| 'G'
	| 'T'
	| 'U'
;

/** a nucleotide string */
NT_STRING
:
	NT_CHAR+
;

/** a number, used for positions etc. */
NT_NUMBER
:
	[1-9] [0-9]*
;

/** literal minus character, for offsets etc. */
NT_MINUS
:
	'-'
;

/** literal plus character, for offsets etc. */
NT_PLUS
:
	'+'
;

/** literal asterisk, for terminal/stop codon */
NT_ASTERISK
:
	'*'
;

/** underscore for ranges */
NT_UNDERSCORE
:
	'_'
;

/** comma, used for multi-change variants */
NT_COMMA
:
	','
;

/** opening parenthesis */
NT_PAREN_OPEN
:
	'('
;

/** closing parenthesis */
NT_PAREN_CLOSE
:
	')'
;

/** double slash for chimeric or mosaic variants */
NT_SLASHES
:
	'//'
	| '/'
;

/** opening square parenthesis, for multi-variant case */
NT_SQUARE_PAREN_OPEN
:
	'['
;

/** closing square parenthesis, for multi-variant case */
NT_SQUARE_PAREN_CLOSE
:
	']'
;

/** semicolon, used in multi-variant case */
NT_SEMICOLON
:
	';'
;

/** token for denoting deletion */
NT_DEL
:
	'del'
;

/** token for denoting duplication */
NT_DUP
:
	'dup'
;

/** token for denoting insertion */
NT_INS
:
	'ins'
;

/** token for denoting inversion */
NT_INV
:
	'inv'
;

NT_EQUAL
:
	'='
;

/** token for denoting splicing */
NT_SPL
:
	'spl'
;

/** token for literal question mark */
NT_QUESTION_MARK
:
	'?'
;

/** token for literal zero */
NT_ZERO
:
	'0'
;

/** literal greater than sign, for denoting single nucleotide change */
NT_GT
:
	'>'
;

/* Lexing of amino acidchanges */
mode AMINO_ACID_CHANGE;

/** number used during protein annotation */
AA_NUMBER
:
	[1-9] [0-9]*
;

/** space is ignored */
AA_CHANGE_SPACE
:
	[ ] -> skip
;

/** line breaks are ignored but end protein change mode */
AA_CHANGE_LINE_BREAK
:
	[\t\r\n] -> popMode , skip
;

/** colon character ends the AA_CHANGE mode */
AA_COLON
:
	':' -> popMode
;

/** 3-letter protein codes, excluding Met */
AA_AA3
:
	'Ala'
	| 'Arg'
	| 'Asn'
	| 'Asp'
	| 'Cys'
	| 'Gln'
	| 'Glu'
	| 'Gly'
	| 'His'
	| 'Ile'
	| 'Leu'
	| 'Lys'
	| 'Phe'
	| 'Pro'
	| 'Ser'
	| 'Thr'
	| 'Trp'
	| 'Tyr'
	| 'Val'
;

/** 1-letter protein codes, excluding M */
AA_AA1
:
	'A'
	| 'R'
	| 'N'
	| 'D'
	| 'C'
	| 'Q'
	| 'E'
	| 'G'
	| 'H'
	| 'I'
	| 'L'
	| 'K'
	| 'F'
	| 'P'
	| 'S'
	| 'T'
	| 'W'
	| 'Y'
	| 'V'
;

/** start codon without position */
AA_MET
:
	'M'
	| 'Met'
;

/** zero, used for denoting no produced protein */
AA_ZERO
:
	'0'
;

AA_MINUS
:
	'-'
;

AA_PLUS
:
	'+'
;

AA_UNDERSCORE
:
	'_'
;

/** frameshift */
AA_FS
:
	'fs'
;

/** insertion */
AA_INS
:
	'ins'
;

/** extension */
AA_EXT
:
	'ext'
;

/** duplication */
AA_DUP
:
	'dup'
;

/** deletion */
AA_DEL
:
	'del'
;

AA_COMMA
:
	','
;

AA_EQUAL
:
	'='
;

AA_QUESTION_MARK
:
	'?'
;

AA_PAREN_OPEN
:
	'('
;

AA_PAREN_CLOSE
:
	')'
;

AA_SQUARE_PAREN_OPEN
:
	'['
;

AA_SQUARE_PAREN_CLOSE
:
	']'
;

AA_SEMICOLON
:
	';'
;

AA_SLASHES
:
	'//'
	| '/'
;

/** terminal codon */
AA_TERMINAL
:
	'*'
	| 'Ter'
;

ErrorChar : . ;