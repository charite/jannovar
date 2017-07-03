package de.charite.compbio.jannovar.vardbs.generic_vcf;

import de.charite.compbio.jannovar.vardbs.base.AbstractDBAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.base.AnnotatingRecord;
import de.charite.compbio.jannovar.vardbs.base.GenotypeMatch;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import de.charite.compbio.jannovar.vardbs.base.VCFReaderVariantProvider;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

/**
 * Annotation driver class for annotations using generic VCF data
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericVCFAnnotationDriver extends AbstractDBAnnotationDriver<VariantContext> {

	private final GenericVCFAnnotationOptions genericVcfOptions;
	private GenericVCFHeaderExtender genericVcfHeaderExtender;

	public GenericVCFAnnotationDriver(String vcfPath, String fastaPath, GenericVCFAnnotationOptions options)
			throws JannovarVarDBException {
		super(new VCFReaderVariantProvider(options.getPathVcfFile()), fastaPath, options,
				new GenericVCFVariantContextToRecordConverter());
		this.genericVcfOptions = options;
	}

	@Override
	protected HashMap<Integer, AnnotatingRecord<VariantContext>> pickAnnotatingDBRecords(
			HashMap<Integer, ArrayList<GenotypeMatch>> annotatingRecords,
			HashMap<GenotypeMatch, AnnotatingRecord<VariantContext>> matchToRecord, boolean isMatch) {
		// Pick annotation for each alternative allele.
		//
		// Note that no smart allele picking has been implemented. Rather, the one from the first
		// record is returned. In case of {@code !isMatch} (overlaps) and there is a match, this
		// will be the first and thus used for annotation.
		//
		// TODO(holtgrewe): Implement better accumulation strategy? compare to handling TSV
		HashMap<Integer, AnnotatingRecord<VariantContext>> annotatingRecord = new HashMap<>();
		for (Entry<Integer, ArrayList<GenotypeMatch>> entry : annotatingRecords.entrySet()) {
			final int alleleNo = entry.getKey();
			for (GenotypeMatch m : entry.getValue()) {
				// Select first or match. 
				if (!annotatingRecord.containsKey(alleleNo) || m.isMatch()) {
					annotatingRecord.put(alleleNo, matchToRecord.get(m));
					if (m.isMatch()) { // stop looking forward in case of match
						break;
					}
				}
			}
		}
		return annotatingRecord;
	}

	@Override
	public VCFHeaderExtender constructVCFHeaderExtender() {
		// TODO(holtgrewe): This side-effect is very unclean, this whole module needs tests and
		// refactoring
		genericVcfHeaderExtender = new GenericVCFHeaderExtender(genericVcfOptions);
		return genericVcfHeaderExtender;
	}

	@Override
	protected VariantContext annotateWithDBRecords(VariantContext vc,
			HashMap<Integer, AnnotatingRecord<VariantContext>> matchRecords,
			HashMap<Integer, AnnotatingRecord<VariantContext>> overlapRecords) {
		VariantContextBuilder builder = new VariantContextBuilder(vc);

		// Annotate with records with matching allele
		for (String fieldName : genericVcfOptions.getFieldNames()) {
			annotate(vc, "", matchRecords, fieldName, builder);
		}

		// Annotate with records with overlapping positions
		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching()) {
			for (String fieldName : genericVcfOptions.getFieldNames()) {
				annotate(vc, "OVL_", overlapRecords, fieldName, builder);
			}
		}

		return builder.make();
	}

	private void annotate(VariantContext vc, String infix, HashMap<Integer, AnnotatingRecord<VariantContext>> records,
			String fieldName, VariantContextBuilder builder) {
		final VCFInfoHeaderLine headerLine = genericVcfHeaderExtender.getFileHeader().getInfoHeaderLine(fieldName);
		final VCFHeaderLineCount countType = headerLine.getCountType();

		// Note that any all cases are already caught in header extension and cause a
		// RuntimeException.
		int start = 0;
		int end = vc.getNAlleles();
		boolean annotatePos = false;
		if (countType == VCFHeaderLineCount.R) {
			/* nop */
		} else if (countType == VCFHeaderLineCount.A) {
			start = 1;
		} else if (countType == VCFHeaderLineCount.INTEGER) {
			assert headerLine.getCount() == 1;
			annotatePos = true;
		}

		final String attrID = options.getVCFIdentifierPrefix() + infix + fieldName;

		if (annotatePos) {
			for (AnnotatingRecord<VariantContext> record : records.values()) {
				if (record.getRecord().hasAttribute(fieldName)) {
					builder.attribute(attrID, record.getRecord().getAttribute(fieldName));
					return; // done annotating, default is "."
				}
			}
		} else {
			final ArrayList<Object> valueList = new ArrayList<>();
			for (int i = start; i < end; ++i) {
				int offset = (countType == VCFHeaderLineCount.R) ? 0 : 1;
				if (records.containsKey(i) && records.get(i).getRecord().hasAttribute(fieldName)) {
					final AnnotatingRecord<VariantContext> annoRecord = records.get(i);
					valueList.add(annoRecord.getRecord().getAttributeAsList(fieldName).get(
							annoRecord.getAlleleNo() - offset));
				} else {
					valueList.add(".");
				}
			}
			if (!valueList.stream().allMatch(s -> ".".equals(s))) {
				builder.attribute(attrID, valueList);
			}
		}
	}

}
