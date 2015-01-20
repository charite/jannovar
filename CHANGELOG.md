**develop**

*jannovar-cli*

* Adding unit tests for jannovar-cli.
* Fixing problem with empty `INFO` fields in output.
* Adding back `--output-dir` to jannovar-cli.
* Writing output parallel to input file by default.
* Adding `-v` and `-vv` command line options.
* Fixing problems with block substitution (delins) case (#87).

*jannovar-core*

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

**v0.11**
