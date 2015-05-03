lexer grammar HGVSLexer;

PROTEIN_CHANGE_START
:
	TOKEN_P_DOT .*? -> pushMode ( PROTEIN_CHANGE )
;

NAME
:
	[a-zA-Z0-9_]+
;

NUMBER
:
	[0-9]+
;

TOKEN_ZERO
:
	'0'
;

TOKEN_MINUS
:
	'-'
;

TOKEN_PLUS
:
	'+'
;

TOKEN_UNDERSCORE
:
	'_'
;

TOKEN_COLON
:
	':'
;

TOKEN_P_DOT
:
	'p.'
;

TOKEN_FS
:
	'fs'
;

TOKEN_DELINS
:
	'delins'
;

TOKEN_INS
:
	'ins'
;

TOKEN_EXT
:
	'ext'
;

TOKEN_DUP
:
	'dup'
;

TOKEN_DEL
:
	'del'
;

TOKEN_X
:
	'X'
;

TOKEN_EQUAL
:
	'='
;

TOKEN_QUESTION_MARK
:
	'?'
;

TOKEN_M1
:
	'M1'
	| 'Met1'
;

TOKEN_PAREN_OPEN
:
	'('
;

TOKEN_PAREN_CLOSE
:
	')'
;

TOKEN_ASTERISK
:
	'*'
;

// Lexing of protein changes
mode PROTEIN_CHANGE;

PROTEIN_AA
:
	PROTEIN_AA1
	| PROTEIN_AA3
	| 'X'
;

PROTEIN_AA1
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
	| 'M'
	| 'F'
	| 'P'
	| 'S'
	| 'T'
	| 'W'
	| 'Y'
	| 'V'
;

PROTEIN_AA3
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
	| 'Met'
	| 'Phe'
	| 'Pro'
	| 'Ser'
	| 'Thr'
	| 'Trp'
	| 'Tyr'
	| 'Val'
;

PROTEIN_CHANGE_WHITESPACE
:
	[ \t\r\n] -> popMode
;