# Jannovar Changelog

## develop

### jannovar-cli

* Writing out annotation about Jannovar call and version into the VCF file.
* Added option `--no-3-prime-shifting` to disable shifting towards the
  3' end of the transcripts.
* Added option `--no-escape-ann-field` to disable escaping of the `ANN`
  `INFO` field.
* Variants in `ANN` field are now annotated with proper Sequence Ontology
  terms.

### jannovar-htsjdk

* Changing `VariantContextWriterConstructionHelper` to allow writing out
  of additional header lines.
* Added option to `VariantContextAnnotator#Options` for disabling
  3' shifting.
* Modified `VariantContextAnnotator` allowing to disable escaping of the
  `ANN` `INFO` field.

### jannovar-core

* Adding `AnnotationBuilderOption` object that allows disabling of 3'
  shifting towards the transcript.
* Adding `JannovarOptions#escapeAnnField`.
* Renaming `VariantType` to `VariantEffect`
* Changing `VariantType` to use proper Sequence Ontology terms. Legacy
  names can be obtained through `VariantType#getLegacyName`.

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
