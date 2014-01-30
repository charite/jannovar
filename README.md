Jannovar
=================


Jannovar is a Java library and executable for annotating VCF files with 
gene/transcript-based annotations and performing simple pedigree/genotype 
filtering. If you are reading this, then preumably you have already 
downloaded the source code from github by using the command

`$ git clone https://github.com/charite/jannovar`

The source code of Jannovar is organized as a maven project 
that integrates the test and build phases. Here are the most important commands:


* Compile all the Java classes

`$ cd jannovar`
`$ mvn compile`
 
* Build a jar archive with all the classes but no Manifest for the main class

`$ mvn jar:jar`
 
* Cause all of the test classes to be executed. It is also possible
   to run the the cobertura test-coverage pluging. To do so, you need to 
   uncomment the corresponding lines in the pom.xml file. The results of
   test coverage analysis will then be writtten to the 
   target/site/cobertura directory.

`$ mvn test`

*  Generate javadoc and output it to the directory target/site/apidocs

`$ mvn javadoc:javadoc`

* Create an executable Jar file the directory "target". This command 
   makes use of the shade:shade maven goal to package a Jar file that also 
   includes the Apache CLI library, i.e., it stands on its own. It does however 
   require that all of the test phase be performed as part of the 
   build.

`$ mvn package`

deprecated: Note
--------

Note that currently, full testing of Jannovar requires that a transcript datafile
from the UCSC website be built. To do this, first run mvn package as above. This
will generate an executable in the target directory (jannovar-0.5-SNAPSHOT.jar).
For simplicity, copy this into the current directory with the name Jannovar:

`$ cp target/jannovar-0.5-SNAPSHOT.jar Jannovar.jar`

Now create the transcript datafile

`$ java -jar Jannovar.jar --create-ucsc`

By default this will create a file called "ucsc_hg19.ser" in the "data/" folder with the transcript definition data. Depending on the choosen resource (ucsc, refseq, ensembl) and genomebuild (eg. hg18, hg19, mm9, mm10) a file called "\<resource\>_\<genomebuild\>.ser" will be created.

To run all of the unit tests (which is required by maven for the package task), copy this
file to the resources directory of the test subdirectory:

`$ cp ucsc.ser src/test/resources/.`

Now you can run either

`$ mvn test`
or
`$ mvn package`
and all of the tests should now run correctly.
For more information, see the main tutorial or enter
`$ java -jar Jannovar.jar -H`

See the tutorial at http://compbio.charite.de/contao/index.php/jannovar.html for 
further information.

License
===========
Jannovar is licenced under a BSD2 license.


Copyright (c) 2013, Charite Universitätsmedizin Berlin
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are
met:

Redistributions of source code must retain the above copyright notice,
this list of conditions and the following disclaimer.  

Redistributions in binary form must reproduce the above copyright
notice, this list of conditions and the following disclaimer in the
documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
