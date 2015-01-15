.. _install:

Installation
============

There are two options of installing Jannovar.
The recommended way for most users is to download a prebuilt binary and is well-described in the :ref:`quickstart` section.
This section describes how to build Jannovar from scratch.

Prequisites
-----------

For building Jannovar, you will need

#. `Java JDK 6 or higher <http://www.oracle.com/technetwork/java/javase/downloads/index.html>`_ for compiling Jannovar,
#. `Maven 3 <http://maven.apache.org/>`_ for building Jannovar, and
#. `Git <http://git-scm.com/>`_ for getting the sources.

Git Checkout
------------

In this tutorial, we will download the Jannovar sources and build them in ``~/Development/jannovar``.

.. code-block:: console

   ~ # mkdir -p ~/Development
   ~ # cd ~/Development
   Development # git clone https://github.com/charite/jannovar.git jannovar
   Development # cd jannovar

Maven Proxy Settings
--------------------

If you are behind a proxy, you will get problems with Maven downloading dependencies.
If you run into problems, make sure to also delete ``~/.m2/repository``.
Then, execute the following commands to fill ``~/.m2/settings.xml``.

.. code-block:: console

    jannovar # mkdir -p ~/.m2
    jannovar # test -f ~/.m2/settings.xml || cat >~/.m2/settings.xml <<END
    <settings>
      <proxies>
       <proxy>
          <active>true</active>
          <protocol>http</protocol>
          <host>proxy.example.com</host>
          <port>8080</port>
          <nonProxyHosts>*.example.com</nonProxyHosts>
        </proxy>
      </proxies>
    </settings>
    END

Building
--------

You can build Jannovar using ``mvn package``.
This will automatically download all dependencies, build Jannovar, and run all tests.

.. code-block:: console

    jannovar # mvn package

In case that you have non-compiling test, you can use the `-DskipTests=true` parameter for skipping them.

.. code-block:: console

    jannovar # mvn install -DskipTests=true

Creating Eclipse Projects
-------------------------

Maven can be used to generate Eclipse projects that can be imported by the Eclipse IDE.
This can be done calling ``mvn eclipse:eclipse`` command after calling ``mvn install``:

.. code-block:: console

    jannovar # mvn install
    jannovar # mvn eclipse:eclipse
