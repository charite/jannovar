package de.charite.compbio.jannovar;

import org.apache.commons.cli.ParseException;

public class JannovarFilterApp {

	public static void main(String[] args) {
		try {
			JannovarFilterOptions options = new JannovarFilterCommandLineParser().parse(args);
			options.print(System.err);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		} catch (HelpRequestedException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
