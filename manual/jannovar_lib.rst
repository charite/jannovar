.. _jannovar_lib:


Jannovar as a Library
=======================

You can obtain the Jannovar library JAR files from Maven Central.
Pre-built binaries are available from `Maven Central <https://search.maven.org>`_.
`This query <http://search.maven.org/#search%7Cga%7C1%7Cjannovar>`_ yields the Jannovar libraries.

.. note::

    Note that you should only use JAR files from version \ |version| \ and not older versions.
    Some modules have been removed (and merged with other modules) in previous versions.


API Documentation
-----------------

You can find the Jannovar Javadoc API documentation here:

- |api_url|


Jannovar in your pom.xml
-------------------------

If you plan to process HTSJDK `VariantContext` objects then you will probably only need to depend on ``jannovar-htsjdk``.

.. parsed-literal::

		<dependency>
			<groupId>de.charite.compbio</groupId>
			<artifactId>jannovar-htsjdk</artifactId>
			<version>\ |version|\ </version>
		</dependency>

Otherwise, there are the following JAR files:

jannovar-cli
    Command line interface for Jannovar, not a library.

jannovar-core
    Core Jannovar functionality with molecular impact annotation and inheritance filtering.

jannovar-hgvs
    Support for parsing HGVS Variant Nomenclature and representing HGVS variants as Java objects.

jannovar-htsjdk
    Bridge between core Jannovar functionality and HTSJDK

jannovar-inheritance-checker
    Older version of inheritance filtering.

jannovar-vardbs
    Support for annotating variants with VCF databases from various sources, e.g. dbSNP
