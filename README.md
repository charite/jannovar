[![Build Status](https://travis-ci.org/charite/jannovar.svg?branch=master)](https://travis-ci.org/charite/jannovar)
[![Documentation](https://readthedocs.org/projects/jannovar/badge/?version=master)](http://jannovar.readthedocs.org/)
[![API Docs](https://img.shields.io/badge/api-v0.17-blue.svg?style=flat)](http://charite.github.io/jannovar/api/0.17/)

Jannovar
========

Functional variant file annotation in Java. Jannovar provides a program for
the annotation of VCF files and also exposes its functionality through a
library API.

Jannovar is licenced under the BSD2 license.

More information is available in the [Jannovar
manual](http://jannovar.readthedocs.org/).

Jannovar is compatible with Java 8 and higher.

Quickstart
----------

Download binary files of the current release from our [GitHub release
page](https://github.com/charite/jannovar/releases).

After extracting the ZIP file, you can call Jannovar as follows.

Download the UCSC transcripts for hg19:

```
# java -jar jannovar-cli-0.17.jar download -d hg19/ucsc
[...]
```

Annotate the example file `small.vcf`:

```
# java -jar jannovar-cli-0.17.jar annotate -d data/hg19_ucsc.ser -i examples/small.vcf
[...]
```

Inspect the resulting annotated file:

```
# less examples/small.jv.vcf
```

Also see the
[Quickstart](http://jannovar.readthedocs.org/en/develop/quickstart.html) section
in the Jannovar manual.
