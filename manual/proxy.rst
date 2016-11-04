.. _proxy_settings:

Proxy Settings
==============

If you have to use a proxy for connecting to the internet then you can do so either using command line parameters to Jannovar ``dowload`` or using environment variables.
If you do not have to use a proxy then you can ignore this section.

Proxy Command Line Arguments
----------------------------

You can specify one proxy URL for all protocols or give a different proxy for each protocol.
Below is a list of the proxy-related command line arguments with an example value.
The value of ``--proxy`` can be overridden by the protocol-specific options.

``--proxy http://proxy.example.com:8080/``
  Fallback proxy URL **most users only have to specify this**.
``--http-proxy http://proxy.example.com:8080/``
  Proxy URL for the HTTP protocol.
``--ftp-proxy http://proxy.example.com:8080/``
  Proxy URL for the FTP protocol.
``--https-proxy http://proxy.example.com:8080/``
  Proxy URL for the HTTPS protocol.

For most users, it is sufficient to use ``--proxy`` only:

.. parsed-literal::
    # java -jar jannovar-cli-\ |version|\ .jar download --proxy http://proxy.example.com:8080/ -d hg19/ucsc

Proxy Environment Variables
---------------------------

It might be more convenient to use environment variables that can be configured globally.
Jannovar interprets the following environment variables that are commonly used on Unix systems (and also interpreted by tools such as ``curl``).
For each protocol, Jannovar accepts both the upper and the lower case version and it is sufficient to specify one for each protocol.

``http_proxy``, ``HTTP_PROXY``
  Proxy URL for the HTTP protocol.
``https_proxy``, ``HTTPS_PROXY``
  Proxy URL for the HTTPS protocol.
``ftp_proxy``, ``FTP_PROXY``
  Proxy URL for the FTP protocol.

If you are on Linux and have not already done so, you can add the following lines to your startup script (e.g., ``~/.profile``):

.. code-block:: bash

    export http_proxy=http://proxy.example.com:8080/
    export https_proxy=http://proxy.example.com:8080/
    export ftp_proxy=http://proxy.example.com:8080/
    export no_proxy="localhost,127.0.0.1,localaddress,.localdomain.com,*.example.com"
    export HTTP_PROXY=http://proxy.example.com:8080/
    export HTTPS_PROXY=http://proxy.example.com:8080/
    export FTP_PROXY=http://proxy.example.com:8080/

If you have write access to ``/etc/environment``, you can add the following lines there:

.. code-block:: bash

    http_proxy=http://proxy.example.com:8080/
    https_proxy=http://proxy.example.com:8080/
    ftp_proxy=http://proxy.example.com:8080/
    no_proxy="localhost,127.0.0.1,localaddress,.localdomain.com,*.example.com"
    HTTP_PROXY=http://proxy.example.com:8080/
    HTTPS_PROXY=http://proxy.example.com:8080/
    FTP_PROXY=http://proxy.example.com:8080/


