Jannovar
========

Functional variant file annotation in Java. Jannovar provides a program for
the annotation of VCF files and also exposes its functionality through a
library API.

Jannovar is licenced under the BSD2 license.

Resources:

* [Homepage](http://charite.github.io/jannovar/)
* [Manual](http://jannovar.readthedocs.org/)
* [GitHub Page](https://github.com/charite/jannovar)

Quickstart
----------

Download the UCSC transcripts for hg19:

```
# java -jar jannovar-cli-0.11.jar download hg19/ucsc
[...]
```

Annotate the example file `small.vcf`:

```
# java -jar jannovar-cli-0.11.jar annotate data/hg19_ucsc.ser examples/small.vcf
[...]
```

Inspect the resulting annotated file:

```
# less small.jv.vcf
```

Also see the
[Quickstart](http://jannovar.readthedocs.org/en/develop/quickstart.html) section
in the Jannovar manual.
