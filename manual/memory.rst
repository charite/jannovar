.. _memory:

Java Memory Settings
====================

Jannovar is a Java program that runs using the Java Virtual Machine (JVM).
The program does not allocate memory directly but through the JVM which uses a fixed maximal memory limit.
If Jannovar terminates by throwing an exception ``java.lang.OutOfMemoryError`` then you have to increase the memory limit of the JVM.

One way of doing this by setting the environment variable ``JAVA_TOOL_OPTIONS``.
For example, the following line increases the available memory to 2 GB of RAM.

.. code-block:: bash

    export JAVA_TOOL_OPTIONS="-Xms2G -Xmx2G"

If you prefer, then you can also pass these options to the invokation of JVM.
The following Jannovar invocation allows to use up to 2 GB of RAM:

.. code-block:: bash

    java -Xms2G -Xmx2G -jar jannovar-cli-0.14.jar [...]
