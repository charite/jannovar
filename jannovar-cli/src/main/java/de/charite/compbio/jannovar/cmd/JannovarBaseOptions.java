package de.charite.compbio.jannovar.cmd;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import de.charite.compbio.jannovar.Jannovar;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Base option class for global Jannovar options
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class JannovarBaseOptions {

	/** Whether or not to print progress */
	private boolean reportProgress = true;

	/** proxy for HTTP */
	private URL httpProxy = null;

	/** proxy for HTTPS */
	private URL httpsProxy = null;

	/** proxy for FTP */
	private URL ftpProxy = null;

	/** Verbosity */
	private int verbosity = 1;

	/**
	 * Setup global {@link ArgumentParser}
	 * 
	 * @param parser
	 *            {@link ArgumentParser} to setup
	 */
	public static void setupParser(ArgumentParser parser) {
		parser.version(Jannovar.getVersion());
		parser.addArgument("--version").help("Show Jannovar version").action(Arguments.version());

		ArgumentGroup verboseGroup = parser.addArgumentGroup("Verbosity Options");
		verboseGroup.addArgument("--report-no-progress").help("Disable progress report, more quiet mode")
				.dest("report_progress").setDefault(true).action(Arguments.storeFalse());
		verboseGroup.addArgument("-v", "--verbose").help("Enable verbose mode").dest("verbose").setDefault(false)
				.action(Arguments.storeTrue());
		verboseGroup.addArgument("-vv", "--very-verbose").help("Enable very verbose mode").dest("very_verbose")
				.setDefault(false).action(Arguments.storeTrue());

		ArgumentGroup proxyGroup = parser.addArgumentGroup("Proxy Options");
		proxyGroup.description("Configuration related to Proxy, note that environment variables *_proxy "
				+ "and *_PROXY are also interpreted");
		proxyGroup.addArgument("--http-proxy").help("Set HTTP proxy to use, if any");
		proxyGroup.addArgument("--https-proxy").help("Set HTTPS proxy to use, if any");
		proxyGroup.addArgument("--ftp-proxy").help("Set FTP proxy to use, if any");
	}

	/**
	 * Get values from {@link Namespace} object
	 * 
	 * @param args
	 *            {@link Namespace} to get the option values from
	 * @throws CommandLineParsingException
	 *             on problems parsing a given URL
	 */
	public void setFromArgs(Namespace args) throws CommandLineParsingException {
		if (args.getBoolean("verbose"))
			this.verbosity = 2;
		if (args.getBoolean("very_verbose"))
			this.verbosity = 3;

		Map<String, String> env = System.getenv();

		try {
			if (args.getString("http_proxy") != null)
				this.httpProxy = new URL(args.getString("http_proxy"));
			else if (env.get("HTTP_PROXY") != null)
				this.httpProxy = new URL(env.get("HTTP_PROXY"));
			else if (env.get("http_proxy") != null)
				this.httpProxy = new URL(env.get("http_proxy"));

			if (args.getString("https_proxy") != null)
				this.httpsProxy = new URL(args.getString("https_proxy"));
			else if (env.get("HTTPS_PROXY") != null)
				this.httpsProxy = new URL(env.get("HTTPS_PROXY"));
			else if (env.get("https_proxy") != null)
				this.httpsProxy = new URL(env.get("https_proxy"));

			if (args.getString("ftp_proxy") != null)
				this.ftpProxy = new URL(args.getString("ftp_proxy"));
			else if (env.get("FTP_PROXY") != null)
				this.ftpProxy = new URL(env.get("FTP_PROXY"));
			else if (env.get("ftp_proxy") != null)
				this.ftpProxy = new URL(env.get("ftp_proxy"));
		} catch (MalformedURLException e) {
			throw new CommandLineParsingException("Problem parsing URL", e);
		}
	}

	public boolean isReportProgress() {
		return reportProgress;
	}

	public void setReportProgress(boolean reportProgress) {
		this.reportProgress = reportProgress;
	}

	public URL getHttpProxy() {
		return httpProxy;
	}

	public void setHttpProxy(URL httpProxy) {
		this.httpProxy = httpProxy;
	}

	public URL getHttpsProxy() {
		return httpsProxy;
	}

	public void setHttpsProxy(URL httpsProxy) {
		this.httpsProxy = httpsProxy;
	}

	public URL getFtpProxy() {
		return ftpProxy;
	}

	public void setFtpProxy(URL ftpProxy) {
		this.ftpProxy = ftpProxy;
	}

	public int getVerbosity() {
		return verbosity;
	}

	public void setVerbosity(int verbosity) {
		this.verbosity = verbosity;
	}

	@Override
	public String toString() {
		return "JannovarBaseOptions [reportProgress=" + reportProgress + ", httpProxy=" + httpProxy + ", httpsProxy="
				+ httpsProxy + ", ftpProxy=" + ftpProxy + ", verbosity=" + verbosity + "]";
	}

}
