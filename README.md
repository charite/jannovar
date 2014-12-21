[![Build Status](https://travis-ci.org/charite/jannovar.svg?branch=master)](https://travis-ci.org/charite/jannovar)


Jannovar
========


Jannovar is a Java library and executable for annotating VCF files with
gene/transcript-based annotations and performing simple pedigree/genotype
filtering. If you are reading this, then preumably you have already
downloaded the source code from github by using the command

Jannovar is licenced under the BSD2 license.

Installation
------------

```
$ git clone https://github.com/charite/jannovar
```

The source code of Jannovar is organized as a maven project
that integrates the test and build phases. Here are the most important commands:


* Compile all the Java classes

```
$ cd jannovar
$ mvn compile
```

* Build a jar archive with all the classes but no Manifest for the main class

```
$ mvn jar:jar
```

* Cause all of the test classes to be executed. It is also possible
   to run the the cobertura test-coverage pluging. To do so, you need to
   uncomment the corresponding lines in the pom.xml file. The results of
   test coverage analysis will then be writtten to the
   target/site/cobertura directory.

```
$ mvn test
```

*  Generate javadoc and output it to the directory target/site/apidocs

```
$ mvn javadoc:javadoc
```

* Create an executable Jar file the directory "target". This command
   makes use of the shade:shade maven goal to package a Jar file that also
   includes the Apache CLI library, i.e., it stands on its own. It does however
   require that all of the test phase be performed as part of the
   build.

```
$ mvn package
```

Version
=======

Jannovar is currently at version 0.9-SNAPSHOT, meaning that we anticipate to extend the public API in the course of 2014 based on comments and suggestions from users. We do not anticipate deprecating or removing functions in the public API, but this cannot be entirely ruled out.
