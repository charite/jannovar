package de.charite.compbio.jannovar.datasource;

import java.net.URL;

/**
 * Configuration for data sources.
 * 
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class DatasourceOptions {

	/** proxy for HTTP */
	private URL httpProxy = null;

	/** proxy for HTTPS */
	private URL httpsProxy = null;

	/** proxy for FTP */
	private URL ftpProxy = null;

	/** whether to print progress bars to stderr or not */
	private boolean printProgressBars = false;

	/**
	 * Initialize with default settings.
	 * 
	 * Proxy URLs are set to <code>null</code>, {@link #printProgressBars} is set to <code>false</code>.
	 */
	public DatasourceOptions() {
	}
	
	public DatasourceOptions(URL httpProxy, URL httpsProxy, URL ftpProxy, boolean printProgressBars) {
		this.httpProxy = httpProxy;
		this.httpsProxy = httpsProxy;
		this.ftpProxy = ftpProxy;
		this.printProgressBars = printProgressBars;
	}

	/** @return HTTP proxy URL */
	public URL getHTTPProxy() {
		return httpProxy;
	}

	/** Set HTTP proxy URL */
	public void setHTTPProxy(URL httpProxy) {
		this.httpProxy = httpProxy;
	}

	/** @return HTTPS proxy URL */
	public URL getHTTPSProxy() {
		return httpsProxy;
	}

	/** Set HTTPS proxy URL */
	public void setHTTPSProxy(URL httpsProxy) {
		this.httpsProxy = httpsProxy;
	}

	/** @return FTP proxy URL */
	public URL getFTPProxy() {
		return ftpProxy;
	}

	/** Set FTP proxy URL */
	public void setFTPProxy(URL ftpProxy) {
		this.ftpProxy = ftpProxy;
	}

	/** @return whether to print progress bars or not */
	public boolean doPrintProgressBars() {
		return printProgressBars;
	}

	/** Set whether to print progress bars or not */
	public void setPrintProgressBars(boolean printProgressBars) {
		this.printProgressBars = printProgressBars;
	}

}
