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

Version
==========
Jannovar is currently at version 0.5-SNAPSHOT, meaning that we anticipate to extend the public API in the course of 2014 based on comments and suggestions from users. We do not anticipate deprecating or removing functions in the public API, but this cannot be entirely ruled out.

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
