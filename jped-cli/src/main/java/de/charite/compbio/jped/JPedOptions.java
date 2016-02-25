package de.charite.compbio.jped;

import java.io.PrintStream;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;

import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;

/**
 * Configuration for the jannovar-filter app.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class JPedOptions {

	/** verbosity level */
	int verbosity = 1;

	/** path to the Jannovar DB */
	String jannovarDB = null;

	/** path to pedigree file */
	String pedPath = null;

	/** path to input file */
	String inputPath = null;

	/** path to output file */
	String outputPath = null;

	/** selected mode of inheritance */
	ImmutableSet<ModeOfInheritance> modeOfInheritances = ImmutableSet.of(ModeOfInheritance.UNINITIALIZED);

	/** gene-wise instead of variant-wise processing (required for composite heterozygous) */
	boolean geneWise = false;

	/**
	 * Print option values to stderr.
	 */
	public void print(PrintStream out) {
		out.println("verbosity: " + verbosity);
		out.println("db path: " + jannovarDB);
		out.println("ped path: " + pedPath);
		out.println("input path: " + inputPath);
		out.println("output path: " + outputPath);
		out.println("modeOfInheritance: " + Joiner.on(", ").join(modeOfInheritances));
		out.println("geneWise: " + geneWise);
	}

}
