package de.charite.compbio.jannovar.vardbs.uk10k;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import de.charite.compbio.jannovar.vardbs.base.AbstractDBAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.base.AnnotatingRecord;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.GenotypeMatch;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

/**
 * Annotation driver class for annotations using UK10K data
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class UK10KAnnotationDriver extends AbstractDBAnnotationDriver<UK10KRecord> {

	public UK10KAnnotationDriver(String vcfPath, String fastaPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		super(vcfPath, fastaPath, options, new UK10KVariantContextToRecordConverter());
	}

	@Override
	protected HashMap<Integer, AnnotatingRecord<UK10KRecord>> pickAnnotatingDBRecords(
			HashMap<Integer, ArrayList<GenotypeMatch>> annotatingRecords,
			HashMap<GenotypeMatch, AnnotatingRecord<UK10KRecord>> matchToRecord, boolean isMatch) {
		// Pick best annotation for each alternative allele
		HashMap<Integer, AnnotatingRecord<UK10KRecord>> annotatingRecord = new HashMap<>();
		for (Entry<Integer, ArrayList<GenotypeMatch>> entry : annotatingRecords.entrySet()) {
			final int alleleNo = entry.getKey();
			for (GenotypeMatch m : entry.getValue()) {
				if (!annotatingRecord.containsKey(alleleNo)) {
					annotatingRecord.put(alleleNo, matchToRecord.get(m));
				} else {
					final UK10KRecord current = annotatingRecord.get(alleleNo).getRecord();
					final UK10KRecord update = matchToRecord.get(m).getRecord();
					if (update.getAltAlleleFrequencies().size() <= alleleNo)
						continue; // no number to update

					if ((isMatch && current.getAltAlleleFrequencies().get(alleleNo - 1) < update
							.getAltAlleleFrequencies().get(alleleNo - 1))
							|| (!isMatch && current.highestAlleleCountOverall() < update.highestAlleleCountOverall()))
						annotatingRecord.put(alleleNo, matchToRecord.get(m));
				}
			}
		}
		return annotatingRecord;
	}

	@Override
	public VCFHeaderExtender constructVCFHeaderExtender() {
		return new UK10KVCFHeaderExtender(options);
	}

	@Override
	protected VariantContext annotateWithDBRecords(VariantContext vc,
			HashMap<Integer, AnnotatingRecord<UK10KRecord>> matchRecords,
			HashMap<Integer, AnnotatingRecord<UK10KRecord>> overlapRecords) {
		VariantContextBuilder builder = new VariantContextBuilder(vc);

		// Annotate with records with matching allele
		annotateAlleleCounts(vc, "", matchRecords, builder);
		annotateChromosomeCounts(vc, "", matchRecords, builder);
		annotateFrequencies(vc, "", matchRecords, builder);

		// Annotate with records with overlapping positions
		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching()) {
			annotateAlleleCounts(vc, "OVL_", overlapRecords, builder);
			annotateChromosomeCounts(vc, "OVL_", overlapRecords, builder);
			annotateFrequencies(vc, "OVL_", overlapRecords, builder);
		}

		return builder.make();
	}

	private void annotateChromosomeCounts(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<UK10KRecord>> records, VariantContextBuilder builder) {
		if (records.isEmpty())
			return;
		UK10KRecord first = records.values().iterator().next().getRecord();
		builder.attribute(options.getVCFIdentifierPrefix() + infix + "AN", first.getChromCount());
	}

	private void annotateAlleleCounts(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<UK10KRecord>> records, VariantContextBuilder builder) {
		ArrayList<Integer> acList = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			if (records.get(i) == null) {
				acList.add(0);
				continue;
			}
			final UK10KRecord record = records.get(i).getRecord();
			final int alleleNo = records.get(i).getAlleleNo();
			if (record.getAltAlleleCounts().isEmpty()) {
				acList.add(0);
			} else {
				acList.add(record.getAltAlleleCounts().get(alleleNo - 1));
			}
		}

		if (acList.stream().allMatch(i -> (i == 0)))
			return; // do not set list of zeroes

		final String attrID = options.getVCFIdentifierPrefix() + infix + "AC";
		if (!acList.isEmpty())
			builder.attribute(attrID, acList);
	}

	private void annotateFrequencies(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<UK10KRecord>> records, VariantContextBuilder builder) {
		ArrayList<Double> afList = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			if (records.get(i) == null) {
				afList.add(0.0);
				continue;
			}
			final UK10KRecord record = records.get(i).getRecord();
			final int alleleNo = records.get(i).getAlleleNo();
			if (record.getAltAlleleCounts().isEmpty()) {
				afList.add(0.0);
			} else {
				afList.add(record.getAltAlleleFrequencies().get(alleleNo - 1));
			}
		}

		if (afList.stream().allMatch(i -> (i == 0.0)))
			return; // do not set list of zeroes

		final String attrID = options.getVCFIdentifierPrefix() + infix + "AF";
		if (!afList.isEmpty())
			builder.attribute(attrID, afList);
	}

}
