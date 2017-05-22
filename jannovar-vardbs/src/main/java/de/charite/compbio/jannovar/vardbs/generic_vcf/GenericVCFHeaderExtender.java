package de.charite.compbio.jannovar.vardbs.generic_vcf;

import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.io.File;

/**
 * Helper class for extending {@link VCFHeader}s for UK10K annotations.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericVCFHeaderExtender extends VCFHeaderExtender {

	GenericVCFAnnotationOptions genericVcfOptions;
	VCFHeader fileHeader;

	public GenericVCFHeaderExtender(GenericVCFAnnotationOptions options) {
		super(options);
		this.genericVcfOptions = options;
	}

	@Override
	public String getDefaultPrefix() {
		return "";
	}

	@Override
	public void addHeaders(VCFHeader header, String prefix) {
		addHeadersInfixes(header, prefix, "", "");
		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching())
			addHeadersInfixes(header, prefix, "OVL_", " (requiring no genotype match, only position overlap)");
	}

	public void addHeadersInfixes(VCFHeader header, String prefix, String infix, String note) {
		try (VCFFileReader reader = new VCFFileReader(new File(genericVcfOptions.getPathVcfFile()), false)) {
			fileHeader = reader.getFileHeader();
		}

		for (String fieldName : genericVcfOptions.getFieldNames()) {
			final VCFInfoHeaderLine headerLine = fileHeader.getInfoHeaderLine(fieldName);
			final VCFHeaderLineCount countType = headerLine.getCountType();
			// TODO(holtgrewe): support more counts, could require using "|" for nested lists
			VCFInfoHeaderLine line;
			if (countType == VCFHeaderLineCount.R) {
				line = new VCFInfoHeaderLine(prefix + infix + fieldName, VCFHeaderLineCount.R, headerLine.getType(),
						"Field " + fieldName + " from file " + genericVcfOptions.getPathVcfFile() + note);
			} else if (countType == VCFHeaderLineCount.A) {
				line = new VCFInfoHeaderLine(prefix + infix + fieldName, VCFHeaderLineCount.A, headerLine.getType(),
						"Field " + fieldName + " from file " + genericVcfOptions.getPathVcfFile() + note);
			} else if (countType == VCFHeaderLineCount.INTEGER) {
				if (headerLine.getCount() != 1) {
					throw new RuntimeException("Unsupported integer count " + headerLine.getCount());
				}
				line = new VCFInfoHeaderLine(prefix + infix + fieldName, 1, headerLine.getType(),
						"Field " + fieldName + " from file " + genericVcfOptions.getPathVcfFile() + note);
			} else {
				throw new RuntimeException("Unsupported count type " + countType);
			}

			header.addMetaDataLine(line);
		}
	}

	public VCFHeader getFileHeader() {
		return fileHeader;
	}

}
