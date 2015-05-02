/** Antlr4 grammar for HGVS variant annotations.
 * 
 *  Derived from
 * 
 * Laros, J. F. J., Blavier, A., den Dunnen, J. T., & Taschner, P. E. M. (2011).
 * A formalized description of the standard human variant nomenclature in Extended
 * Backus-Naur Form. BMC Bioinformatics, 12(Suppl 4), S5.
 * http://doi.org/10.1186/1471-2105-12-S4-S5
 */

grammar HGVS;

hgvs: protein_var
//    | dna_var
    ;

// Basix lexemes

AA: AA1
  | AA3
  | 'X';

AA1: 'A'
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
   | 'V';

AA3: 'Ala'
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
   | 'Val';

NUMBER: [0-9]+;

NAME: [a-zA-Z0-9_]+;

// Top-level Rule

/** Variant in a protein. */
protein_var: //single_var
           //| multi_var
           'p.' AA NUMBER AA1
           ;

single_var: ref raw_var;

// Locations

aa_loc: aa_pt_loc
      | aa_range
      ;

aa_pt_loc: AA pt_loc;

pt_loc: ('-' | '*')? NUMBER
      | NUMBER ('+' | '-') NUMBER
      ;

aa_range: extent
        | '(' extent ')'
        ;

extent: aa_pt_loc '_' aa_pt_loc;

// Reference sequences

ref: (NAME ':')? 'p.';

// Single Variations

raw_var: subst
       | del
       | dup
       | var_ssr
       | '='
       | '?'
       | '0'
       | '0?'
       ;

subst: aa_pt_loc AA ('extX' '*'? NUMBER)?
     | ('Met1' | 'M1') ('?' | 'ext' '-' NUMBER)
     ;

del: aa_loc 'del';

dup: aa_loc 'dup';

var_ssr: aa_loc '(' NUMBER '_' NUMBER ')';

ins: aa_range 'ins' (AA+ | NUMBER);

indel: aa_loc 'delins' (AA+ | NUMBER);

frame_shift: short_fs | long_fs;

short_fs: aa_pt_loc 'fs';

long_fs: aa_pt_loc AA 'fs' 'X' NUMBER;

// Multiple Variations

multi_var: single_allele_vars
         | multi_allele_vars
         | unk_allele_vars
         ;

single_allele_vars: ref single_allele_var_set;

single_allele_var_set: '[' raw_var
                        (';' raw_var)+ | (',' raw_var)+
                        ']';

multi_allele_vars: ref single_allele_var_set ';' ref? single_allele_var_set;

unk_allele_vars: ref '[' raw_var '(;)' raw_var ']';

