package de.charite.compbio.jannovar.vardbs.generic_vcf;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import java.util.List;

/**
 * Configuration for generic annotation of VCF with options.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericVCFAnnotationOptions extends DBAnnotationOptions {

	/** Path to VCF file. */
	private final String pathVcfFile;
	/** Field names to use for annotation. */
	private final List<String> fieldNames;
	// TODO: support different accumulation strategies, similar to TSV

	/**
	 * Parse configuration from command line string.
	 * 
	 * <p>
	 * The value must be of the format <code>pathToVfFile:prefix:field1,field2,field3</code>.
	 * </p>
	 */
	public static GenericVCFAnnotationOptions parseFrom(String s) {
		String tokens[] = s.split(":", 3);
		if (tokens.length != 3) {
			throw new RuntimeException("Could not parse VCF annotation config from " + s);
		}

		return new GenericVCFAnnotationOptions(true, false, tokens[1],
				MultipleMatchBehaviour.BEST_ONLY, tokens[0],
				ImmutableList.copyOf(tokens[2].split(",")));
	}

	public GenericVCFAnnotationOptions(boolean reportOverlapping,
			boolean reportOverlappingAsIdentical, String identifierPrefix,
			MultipleMatchBehaviour multiMatchBehaviour, String pathVcfFile,
			List<String> fieldNames) {
		super(reportOverlapping, reportOverlappingAsIdentical, identifierPrefix,
				multiMatchBehaviour);
		this.pathVcfFile = pathVcfFile;
		this.fieldNames = fieldNames;
	}

	public List<String> getFieldNames() {
		return fieldNames;
	}

	public String getPathVcfFile() {
		return pathVcfFile;
	}

	@Override
	public String toString() {
		return "GenericVCFAnnotationOptions [fieldNames=" + fieldNames + ", super="
				+ super.toString() + ", pathVcfFile=" + pathVcfFile + "]";
	}

}
