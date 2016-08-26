# Jannovar Changelog

## develop

### jannovar-cli

* Consistently writing out HUGO symbols for gene names, using the `hgnc_complete_set.txt` information downloaded when building the annotation DB
* Upgrading from ENSEMBL-74 to ENSEMBL-75 for annotation database files
* Removing support for old Jannovar-style annotations (#241)

### jannovar-core

* downloading `hgnc_complete_set.txt` together with data sets, `TranscriptModel` objects now consistently contain additional IDs
* making ENSEMBL parsing more robust (falling back to transcript name if no transcript ID)
* fixing bug #248 for ENSEMBL that used `gene_id` for `gene_name`

## v0.17

### jped-cli

* this is gone, the functionality is now available as part of jannovar-cli

### jannovar-filter

* this module is done, everything here is merged into jannovar-htsjdk

### jannovar-vardbs

* The first version ships with support for dbSNP b147, ExAC 0.3, and the UK10K COHORT data base
* Initial version of this module, the aim is precise annotation from variant databases

### jannovar-cli

* Updated `default_sources.ini` for latest patches of mouse and human genomes
* Using one-letter amino acid code by default
* Removed slf4j2 warning at program startup
* Checking pedigree for compatibility with VCF file if given

### jannovar-core

* Adjusting API for annotating amino acid code by default
* Checking pedigree for compatibility with genotypes on Mendelian inheritance checking
* Refurbishing `Genotype`, `GenotypeList`, and `GenotypeListBuilder` in `de.charite.compbio.jannovar.mendel`.
* Moving `ModeOfInheritance` to `de.charite.compbio.jannovar.mendel`.
* Creating new package `de.charite.compbio.jannovar.mendel` with code for filtering for mendelian inheritance modes.
* Renaming of `ModeOfInheritance.UNINITIALIZED` to `ModeOfInheritance.ANY`.
* Fixing handling of invalid transcripts (e.g., incomplete 3' end)
* Adding `altGeneIDs` mapping to `TranscriptModel`, makes data bases backwards incompatible.
* Rewrite of GFF parsers for RefSeq and ENSEMBL.
* Bumping HTSJDK to 2.5.0, requiring Java 8 from now on.
* Removal of `AnnotationCollector`, priotization of variant effects is done after collecting all effect predictions now.
* Fix for intronic variants between 5' or 3' UTRs. These variants were misclassified as `FIVE_PRIME_UTR_VARIANT` or `THREE_PRIME_UTR_VARIANT`. SequenceOntology implements new terms so that we can decide between the two UTR exon and intron variants. Now we have `FIVE_PRIME_UTR_EXON_VARIANT` or `FIVE_PRIME_UTR_EXON_INTRON_VARIANT` (the same for `THREE_PRIME_UTR_EXON_VARIANT` or `THREE_PRIME_UTR_EXON_INTRON_VARIANT`).

### jannovar-cli

* Adding better progress display with estimate of pending time.
* Adding support for annotating values from dbSNP VCF file (currently, only b147 is supported).
* Adding simple progress reporting (from verbosity level 2).
* Using Java 8 stream interface for `VariantContext` processing.
* Removing support for Jannovar output format, VCF offer all features and more.

## v0.16

### jannovar-cli

* Updating htsjdk to 1.142
* using simple logger of slf4j
* fixing version output in command line help
* changing command line interface to use more named arguments
* removing deprecated usage of commons-cli command line parser
* renaming of some internal classes and functions, fixing Javadocs

### jannovar-core

* fixing bug in `TranscriptSequenceChangeHelper` for reverse transcript (did not reverse complement alternate allele)
* fixing bug in parsing GFF3 with some transcripts (e.g. GNAT1)
* less intrusive escaping in `ANN` field
* renaming of some internal classes and functions, fixing Javadocs

### jannovar-htsjdk

* Updating htsjdk to 1.142
* renaming `InvalidGenomeChange` to `InvalidGenomeVariant`
* renaming `VariantContextAnnotator.buildGenomeChange` to `.buildGenomeVariant`
* renaming of some internal classes and functions, fixing Javadocs

### jannovar-hgvs

* extending API of ProteinChange hierarchy for HGVS generation
* renaming of some internal classes and functions, fixing Javadocs

### jped-cli

* Updating htsjdk to 1.142
* changing command line interface to use more named arguments

### jannovar-inheritance-checker

* adding two new functions to InheritanceCompatibilityChecker
* resolve boolean if passes inheritance into set where passed inheritances are stored
* Updating htsjdk to 1.142

### manual

* updating manual for 0.16 and using parameters for commands!
* updating readme for parameters

## v0.15

### jannovar-core

* making `CompatibilityCheckerAutosomalRecessiveHomozygous` public
* using jannovar-hgvs for representing the changes
* more precise HGVS annotation in some cases
* predictions are wrapped in parentheses
* Mark everything that is related to the compatibility checkers as depricated (see new jannovar-inheritance-checker)

### jannover-hgvs

* adding module for parsing and representing HGVS-compatible nucleic and protein changes


### jannover-htsjdk

* Updating htsjdk to 1.138
* Replacing depricatded method `VariantContext.getChr()` with `VariantContext.getContig()`

### jannovar-cli

* Updating htsjdk to 1.138
* Replacing depricatded method `VariantContext.getChr()` with `VariantContext.getContig()`
* Updating commons-cli to 1.3.1

### jannover-inheritance-checker

* Bugfix detecting autosomal chromosomes
* Bugfix with handling variant files with a leading "chr" in the contig.
* Adding this new module.
* Replaces the compatibility checker oh jannobvar-core.
* Now runs with VariantContext (htsjdk) instead of Jannovar Genotypes
* Use `InheritanceCompatibilityChecker.Builder` to build `InheritanceCompatibilityChecker`.
* Use the method `getCompatibleWith` of the `InheritanceCompatibilityChecker` with a List of `VariantContext`.
* The method will return all `VariantContext` that matches the inheritance. If no variant matches the List is empty.

### jannover-filter

* Refactoring `VariantWiseInheritanceFilter` to handle the new `InheritanceCompatibilityChecker`.
* Rewrite `GeneWiseInheritanceFilter` to handle the new `InheritanceCompatibilityChecker`.
* Updating htsjdk to 1.138
* Replacing depricatded method `VariantContext.getChr()` with `VariantContext.getContig()`

### jped-cli

* Adapting program to the `GeneWiseInheritanceFilter` and `VariantWiseInheritanceFilter` (see jannovar-filter)
* Updating commons-cli to 1.3.1
* Changing cli option inheritance-mode to multiple args (Now you can check multiple inheritances at once)

## v0.14

### jannovar-cli

* Improving output file generation, jannovar-cli now uses the same extension
  as in the input and the infix is configurable instead of being fixed to
  ".jv".
* Default extension is ".vcf.gz" instead of ".vcf" now.

### jannovar-core

* Fixing label for `FRAMESHIFT_VARIANT` in `VariantEffect`.
* Moving CompatibilityCheckerException to package
  `...jannovar.pedigree.compatibilitychecker`
* Fixing bug in transcript coordinate projection.
* Renaming `TranscriptSequenceChangeHelper.getCDSWithChange` to
  `.getCDSWithGenomeVariant`.
* Renaming `*.getChange()` to `*.getGenomeVariant()`
* Renaming `VariantAnnotator.buildAnnotationList` to `.buildAnnotations`,
  `VariantContextAnnotator.buildAnnotationList` to `.buildAnnotations`,
  and `VariantContextAnnotator.buildErrorAnnotationList` to
  `VariantContextAnnotator.buildErrorAnnotations`
* VariantAnnotations does not implement `List<Annotation>` any more
* Adding `VariantAnnotations.getAnnotations`
* Renaming `AnnotationList` to `VariantAnnotations`
* changing treatment of insertions at exon/intron junctions; they are
  considered as intronic insertions now that affect splicing
* converting `GenomeVariant` of `AnnotationList` to always be on the forward
  strand after construction of  `AnnotationList`
* deprecating the `{,All,Best}AnnotationTextGenerator` classes

## v.0.13

### jannovar-cli

* Moving `JannovarOptions` into jannovar-cli.
* Displaying online help on unknown Jannovar command.
* Fixing `NullPointerException` bug for local paths.
* Switching to official HTSJDK release and version 0.128.
* Writing out annotation about Jannovar call and version into the VCF file.
* Added option `--no-3-prime-shifting` to disable shifting towards the
  3' end of the transcripts.
* Added option `--no-escape-ann-field` to disable escaping of the `ANN`
  `INFO` field.
* Variants in `ANN` field are now annotated with proper Sequence Ontology
  terms.

### jannovar-htsjdk

* Modified `VariantContextWriterConstructionHelper` to allow explicit
  disabling of index creation.
* Modified `VariantContextAnnotator` for adjustment to the new Exomiser.
* Switching to official HTSJDK release and version 0.128.
* Changing `VariantContextWriterConstructionHelper` to allow writing out
  of additional header lines.
* Added option to `VariantContextAnnotator#Options` for disabling
  3' shifting.
* Modified `VariantContextAnnotator` allowing to disable escaping of the
  `ANN` `INFO` field.

### jannovar-core

* Moving `JannovarOptions` into jannovar-cli.
* Renaming `ACompatibilityChecker` and `ICompatibilityChecker`.
* Adding `GenomePosition.differenceTo(GenomeInterval)`.
* Renaming package `de.charite.compbio.jannovar.io` to `de.charite.compbio.jannovar.data`
* Renaming `AnnotationLocation.toHGVSString` to `.toHGVSChunk`.
* Adding `Pedigree.subsetOfMembers`
* Renaming `GenomeChange` to `GenomeVariant`, same with types having the same
  prefix.
* Introducing `DatasourceOptions` for configuring data download.
* Removing support for using `"-"` as REF or ALT value.
* Making previous `public final` members `private final` (or
 `protected final`) and adding getters for read-only access to them.
* Removing position type member of `CDSInterval`.
* Using type `Strand` instead of `'+'` and `'-'`, requires database rebuild.
* Adding enum `Strand` with `PLUS` and `MINUS` values.
* Adding `VariantEffect.isOffExome` and updating
  `VariantEffect.isOffTranscript`.
* Removing `genomeRegion` member from `GenotypeList`. Also, adjusting the
  pedigree compatibility checkers for this, the check for being on the X
  chromosome has to be performed outside the checker now.
* `VariantList.getHighestImpactEffect` now returns
  `VariantEffect#SEQUENCE_VARIANT` if no annotation can be found.
* `VariantList` implements the `List<Annotation>` interface now and the
  `entries` member has become private.
* Adding `VariantEffect#SEQUENCE_VARIANT` for variants with unknown
  effects.
* `GenomeChange.toString()` now always converts to forward strand.
* Fixing bug in `Annotation` and enforcing forward strand `GenomeChange`
  instances.
* Updates to the manual.
* `JannovarData` now also stores a mapping from transcript accession to
  `TranscriptModel` and from gene symbol to `TranscriptModel`.
* Adding functionality for conversion from CDS to transcript and genome
  position and tests.
* Adding `AnnotationBuilderOption` object that allows disabling of 3'
  shifting towards the transcript.
* Adding `JannovarOptions#escapeAnnField`.
* Renaming `VariantType` to `VariantEffect`
* Changing `VariantType` to use proper Sequence Ontology terms. Legacy
  names can be obtained through `VariantType#getLegacyName`.
* Spliting `CompatibilityCheckerXRecessive` into `CompatibilityCheckerXRecessiveCompoundHet` and `CompatibilityCheckerXRecessiveHomozygous`. Now all inheritance checkers ar ready to use (AR,XR,AD,XD)
* move all pedigree compatibility checkers from `de.charite.compbio.jannovar.pedigree` to `de.charite.compbio.jannovar.pedigree.compatibilitychecker` and divide it into ar,xr,ad,xd.
* generate interface `ICompatibilityChecker` for pedigree compatibility checkers.
* Combine compatibility fields and methods in an abstract class`ACompatibilityChecker` to unify methods, builders, and fields.

### jannovar-filter

* Splitting into `jped-cli` and `jannovar-filter`
* Changing public final members to accessors. 
* `jannovar-filter` now has the Jannovar DB as the mandatory first argument.

### jannovar-htsjdk

* Changing public final members to accessors. 

## v0.12

### jannovar-htsjdk

* Started bridge module between Jannovar and HTSJDK.

### jannovar-filter

* Started tool for mode of inheritance--based filters.

### jannovar-cli

* Splitting out bridge module between jannovar-core and HTSJDK to
  jannovar-htsjdk.
* Adding implementation of variant annotation standard 1.0.
* Adding unit tests for jannovar-cli.
* Fixing problem with empty `INFO` fields in output.
* Adding back `--output-dir` to jannovar-cli.
* Writing output parallel to input file by default.
* Adding `-v` and `-vv` command line options.
* Fixing problems with block substitution (delins) case (#87).

### jannovar-core

* Adding initial support for the transcript support level feature of the new VCF
  annotation standard (only in very recent ENSEMBL releases, apparently).
* `TranscriptModel#geneID` is now a `String`
* Update in various classes, e.g. Annotation.
* Fixing bug in PED parsing (empty lines are properly skipped now).
* More tests and fixes for the inheritance compatibility checkers.
* Updating `Annotation` for the variant annotation standard.
* `TranscriptPosition` and `TranscriptInterval` use zero-based positions now.
* Reordering values of `VariantType`.
* Somewhat renaming `VariantType` method names. 
* Removing the `VariantType#size` function in favor of a `static public`
  `final` member.
* Using log4j/slf4j for I/O in jannovar-core.
* Adding `PrintStream` as parameter to `JannovarOptions#print`.
* Compressing serialized file.
* Changing namespace to `de.charite.compbio.jannovar`.
* Making `VariantType#priorityLevel` a non-static member.
* Renaming `TranscriptInfo` to `TranscriptModel`.
* Moving `HG19RefDictbuilder` from tests to main.
* Using `ImmutableMap` in `Translator` for small performance improvements.
* Using `StringBuilder`-based concatenation of strings for generation of HGVS
  strings etc. since this is much faster than using `String#format`.
* `GenomePosition` and `GenomeInterval` use zero-based coordinates internally
  now.

## v0.11
