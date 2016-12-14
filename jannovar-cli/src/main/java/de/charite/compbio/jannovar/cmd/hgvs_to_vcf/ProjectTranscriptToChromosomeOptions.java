package de.charite.compbio.jannovar.cmd.hgvs_to_vcf;

import java.util.function.BiFunction;

import de.charite.compbio.jannovar.UncheckedJannovarException;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationOptions;
import de.charite.compbio.jannovar.cmd.JannovarBaseOptions;
import net.sourceforge.argparse4j.impl.Arguments;
import net.sourceforge.argparse4j.inf.ArgumentGroup;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.Namespace;
import net.sourceforge.argparse4j.inf.Subparser;
import net.sourceforge.argparse4j.inf.Subparsers;

/**
 * Projection from transcript-level to chromosome-level changes
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ProjectTranscriptToChromosomeOptions extends JannovarAnnotationOptions {

	/** Path to input text file */
	private String pathInputText;

	/** Path to output VCF file */
	private String pathOutputVCF;

	/** Path to reference FASTA file */
	private String pathReferenceFASTA;

	@Override
	public void setFromArgs(Namespace args) throws CommandLineParsingException {
		super.setFromArgs(args);

		pathInputText = args.getString("input_txt");
		pathOutputVCF = args.getString("output_vcf");
		pathReferenceFASTA = args.getString("reference_fasta");
	}

	/**
	 * Setup {@link ArgumentParser}
	 * 
	 * @param subParsers
	 *            {@link Subparsers} to setup
	 */
	public static void setupParser(Subparsers subParsers) {
		BiFunction<String[], Namespace, ProjectTranscriptToChromosome> handler = (argv, args) -> {
			try {
				return new ProjectTranscriptToChromosome(argv, args);
			} catch (CommandLineParsingException e) {
				throw new UncheckedJannovarException("Could not parse command line", e);
			}
		};

		Subparser subParser = subParsers.addParser("hgvs-to-vcf", true)
				.help("project transcript-level to chromosome-level changes").setDefault("cmd", handler);
		subParser.description("Project transcript-level changes to chromosome level ones");
		ArgumentGroup requiredGroup = subParser.addArgumentGroup("Required arguments");
		requiredGroup.addArgument("-r", "--reference-fasta").help("Path to reference FASTA file").required(true);
		requiredGroup.addArgument("-d", "--database").help("Path to database .ser file").required(true);
		requiredGroup.addArgument("-i", "--input-txt").help("Input file with HGVS transcript-level changes, line-by-line")
				.required(true);
		requiredGroup.addArgument("-o", "--output-vcf").help("Output VCF file with chromosome-level changes")
				.required(true);

		ArgumentGroup optionalGroup = subParser.addArgumentGroup("Optional Arguments");
		optionalGroup.addArgument("--show-all").help("Show all effects").setDefault(false);
		optionalGroup.addArgument("--no-3-prime-shifting").help("Disable shifting towards 3' of transcript")
				.dest("3_prime_shifting").setDefault(true).action(Arguments.storeFalse());
		optionalGroup.addArgument("--3-letter-amino-acids").help("Enable usage of 3 letter amino acid codes")
				.setDefault(false).action(Arguments.storeTrue());

		subParser.epilog("Example: java -jar Jannovar.jar tx-to-chrom -i in.txt -o out.vcf");

		JannovarBaseOptions.setupParser(subParser);
	}

	public String getPathReferenceFASTA() {
		return pathReferenceFASTA;
	}

	public void setPathReferenceFASTA(String pathReferenceFASTA) {
		this.pathReferenceFASTA = pathReferenceFASTA;
	}

	public String getPathInputText() {
		return pathInputText;
	}

	public void setPathInputText(String pathInputText) {
		this.pathInputText = pathInputText;
	}

	public String getPathOutputVCF() {
		return pathOutputVCF;
	}

	public void setPathOutputVCF(String pathOutputVCF) {
		this.pathOutputVCF = pathOutputVCF;
	}

	@Override
	public String toString() {
		return "ProjectTranscriptToChromosomeOptions [pathInputText=" + pathInputText + ", pathOutputVCF="
				+ pathOutputVCF + ", pathReferenceFASTA=" + pathReferenceFASTA + ", toString()=" + super.toString()
				+ "]";
	}

}
