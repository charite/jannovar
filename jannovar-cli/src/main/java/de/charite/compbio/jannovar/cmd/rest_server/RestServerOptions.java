package de.charite.compbio.jannovar.cmd.rest_server;

import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationOptions;
import de.charite.compbio.jannovar.cmd.JannovarBaseOptions;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * Options for the <tt>rest-server</tt> comman
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class RestServerOptions extends JannovarBaseOptions {

	/**
	 * The {@code .ser} files to load at startup.
	 */
	private List<String> dbPaths = new ArrayList<>();

	/**
	 * The host to listen on.
	 */
	private String host = "127.0.0.1";

	/**
	 * The port to listen on.
	 */
	private int port = 5050;

	/**
	 * whether or not to shift variants towards the 3' end of the transcript
	 * (default is <code>true</code>)
	 */
	private boolean nt3PrimeShifting=true;

	/**
	 * Setup {@link ArgumentParser}
	 *
	 * @param subParsers {@link Subparsers} to setup
	 */
	public static void setupParser(Subparsers subParsers) {
		BiFunction<String[], Namespace, RestServerCommand> handler = (argv, args) -> {
			try {
				return new RestServerCommand(argv, args);
			} catch (CommandLineParsingException e) {
				throw new UncheckedJannovarException("Could not parse command line", e);
			}
		};

		Subparser subParser = subParsers.addParser("rest-server", true).help("start REST server")
			.setDefault("cmd", handler);
		subParser.description("Start built-in REST server for the annotation of single variants");
		ArgumentGroup requiredGroup = subParser.addArgumentGroup("Required arguments");
		requiredGroup.addArgument("--host").help("Host specification to listen on")
			.setDefault("127.0.0.1");
		requiredGroup.addArgument("--port").help("Port to listen on").type(Integer.class)
			.setDefault(5050);
		requiredGroup.addArgument("-d", "--database").help(
			"Path to .ser file(s) with database, naming is $genome_$label; can be given multiple times")
			.action(Arguments.append()).required(true);

		subParser.epilog(
			"Example: java -jar Jannovar.jar rest-server --host 0.0.0.0 --port 80 -d hg19_refseq.ser");

		JannovarAnnotationOptions.setupParser(subParser);
	}

	@Override public void setFromArgs(Namespace args) throws CommandLineParsingException {
		super.setFromArgs(args);

		host = args.getString("host");
		port = args.getInt("port");
		dbPaths = args.getList("database");
		nt3PrimeShifting = args.getBoolean("3_prime_shifting");
	}

	public List<String> getDbPaths() {
		return dbPaths;
	}

	public void setDbPaths(List<String> dbPaths) {
		this.dbPaths = dbPaths;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public boolean isNt3PrimeShifting() {
		return nt3PrimeShifting;
	}

	public void setNt3PrimeShifting(boolean nt3PrimeShifting) {
		this.nt3PrimeShifting = nt3PrimeShifting;
	}

	@Override public String toString() {
		return "RestServerOptions{" + "dbPaths=" + dbPaths + ", host='" + host + '\'' + ", port="
			+ port + ", 3-prime-shifting=" + nt3PrimeShifting + '}';
	}

}
