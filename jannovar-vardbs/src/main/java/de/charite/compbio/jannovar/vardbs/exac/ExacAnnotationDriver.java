package de.charite.compbio.jannovar.vardbs.exac;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.charite.compbio.jannovar.vardbs.base.AbstractDBAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.base.AnnotatingRecord;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.GenotypeMatch;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

// TODO: handle MNVs appropriately

/**
 * Annotation driver class for annotations using ExAC
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ExacAnnotationDriver extends AbstractDBAnnotationDriver<ExacRecord> {

	public ExacAnnotationDriver(String vcfPath, String fastaPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		super(vcfPath, fastaPath, options, new ExacVariantContextToRecordConverter());
	}

	@Override
	public VCFHeaderExtender constructVCFHeaderExtender() {
		return new ExacVCFHeaderExtender(options);
	}

	@Override
	protected HashMap<Integer, AnnotatingRecord<ExacRecord>> pickAnnotatingDBRecords(
			HashMap<Integer, ArrayList<GenotypeMatch>> annotatingRecords,
			HashMap<GenotypeMatch, AnnotatingRecord<ExacRecord>> matchToRecord, boolean isMatch) {
		// Pick best annotation for each alternative allele
		HashMap<Integer, AnnotatingRecord<ExacRecord>> annotatingExacRecord = new HashMap<>();
		for (Entry<Integer, ArrayList<GenotypeMatch>> entry : annotatingRecords.entrySet()) {
			final int alleleNo = entry.getKey();
			for (GenotypeMatch m : entry.getValue()) {
				if (!annotatingExacRecord.containsKey(alleleNo)) {
					annotatingExacRecord.put(alleleNo, matchToRecord.get(m));
				} else {
					final ExacRecord current = annotatingExacRecord.get(alleleNo).getRecord();
					final ExacRecord update = matchToRecord.get(m).getRecord();
					if (update.getAlleleFrequencies(ExacPopulation.ALL).size() < alleleNo)
						continue;
					if ((isMatch && current.highestAlleleFreq(alleleNo - 1) < update.highestAlleleFreq(alleleNo - 1))
							|| (!isMatch && current.highestAlleleFreqOverall() < update.highestAlleleFreqOverall()))
						annotatingExacRecord.put(alleleNo, matchToRecord.get(m));
				}
			}
		}
		return annotatingExacRecord;
	}

	@Override
	protected VariantContext annotateWithDBRecords(VariantContext vc,
			HashMap<Integer, AnnotatingRecord<ExacRecord>> matchRecords,
			HashMap<Integer, AnnotatingRecord<ExacRecord>> overlapRecords) {
		if (matchRecords.isEmpty())
			return vc;

		VariantContextBuilder builder = new VariantContextBuilder(vc);

		// Annotate with records with matching allele
		annotateAlleleCounts(vc, "", matchRecords, builder);
		annotateAlleleHetCounts(vc, "", matchRecords, builder);
		annotateAlleleHomCounts(vc, "", matchRecords, builder);
		annotateAlleleHemiCounts(vc, "", matchRecords, builder);
		annotateChromosomeCounts(vc, "", matchRecords, builder);
		annotateFrequencies(vc, "", matchRecords, builder);
		annotateBestAF(vc, "", matchRecords, builder);

		// Annotate with records with overlapping positions
		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching()) {
			annotateAlleleCounts(vc, "OVL_", overlapRecords, builder);
			annotateAlleleHetCounts(vc, "OVL_", overlapRecords, builder);
			annotateAlleleHomCounts(vc, "OVL_", overlapRecords, builder);
			annotateAlleleHemiCounts(vc, "OVL_", overlapRecords, builder);
			annotateChromosomeCounts(vc, "OVL_", overlapRecords, builder);
			annotateFrequencies(vc, "OVL_", overlapRecords, builder);
			annotateBestAF(vc, "OVL_", overlapRecords, builder);
		}

		return builder.make();
	}

	private void annotateBestAF(VariantContext vc, String infix, HashMap<Integer, AnnotatingRecord<ExacRecord>> records,
			VariantContextBuilder builder) {
		ArrayList<Double> afs = new ArrayList<>();
		ArrayList<Integer> acs = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			if (records.get(i) == null) {
				afs.add(0.0);
				acs.add(0);
			} else {
				final ExacRecord record = records.get(i).getRecord();
				final int alleleNo = records.get(i).getAlleleNo();
				final ExacPopulation pop = record.popWithHighestAlleleFreq(alleleNo - 1);
				afs.add(record.getAlleleFrequencies(pop).get(alleleNo - 1));
				acs.add(record.getAlleleCounts(pop).get(alleleNo - 1));
			}
		}

		builder.attribute(options.getVCFIdentifierPrefix() + infix + "BEST_AC", acs);
		builder.attribute(options.getVCFIdentifierPrefix() + infix + "BEST_AF", afs);
	}

	private void annotateChromosomeCounts(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<ExacRecord>> records, VariantContextBuilder builder) {
		if (records.isEmpty())
			return;
		ExacRecord first = records.values().iterator().next().getRecord();
		for (ExacPopulation pop : ExacPopulation.values())
			builder.attribute(options.getVCFIdentifierPrefix() + infix + "AN_" + pop, first.getChromCount(pop));
	}

	private void annotateAlleleCounts(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<ExacRecord>> records, VariantContextBuilder builder) {
		Map<String, List<Integer>> acLists = new HashMap<>();
		for (ExacPopulation pop : ExacPopulation.values()) {
			final String attrID = options.getVCFIdentifierPrefix() + infix + "AC_" + pop;
			List<Integer> acList = new ArrayList<>();
			boolean use = false;
			for (int i = 1; i < vc.getNAlleles(); ++i) {
				if (records.get(i) == null) {
					acList.add(0);
					continue;
				}
				final ExacRecord record = records.get(i).getRecord();
				final int alleleNo = records.get(i).getAlleleNo();
				if (record.getAlleleCounts(pop).isEmpty()) {
					acList.add(0);
				} else {
					acList.add(record.getAlleleCounts(pop).get(alleleNo - 1));
					use = true;
				}
			}

			if (use && !acList.isEmpty())
				acLists.put(attrID, acList);
		}

		for (String attrID : acLists.keySet()) {
			builder.attribute(attrID, acLists.get(attrID));
		}

	}

	private void annotateAlleleHetCounts(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<ExacRecord>> records, VariantContextBuilder builder) {
		Map<String, List<Integer>> acLists = new HashMap<>();
		for (ExacPopulation pop : ExacPopulation.values()) {
			final String attrID = options.getVCFIdentifierPrefix() + infix + "HET_" + pop;
			ArrayList<Integer> acList = new ArrayList<>();
			boolean use = false;
			for (int i = 1; i < vc.getNAlleles(); ++i) {
				if (records.get(i) == null) {
					acList.add(0);
					continue;
				}
				final ExacRecord record = records.get(i).getRecord();
				final int alleleNo = records.get(i).getAlleleNo();
				if (record.getAlleleHetCounts(pop) != null && record.getAlleleHetCounts(pop).isEmpty()) {
					acList.add(0);
				} else {
					acList.add(record.getAlleleHetCounts(pop).get(alleleNo - 1));
					use = true;
				}
			}

			if (use && !acList.isEmpty())
				acLists.put(attrID, acList);
		}

		for (String attrID : acLists.keySet()) {
			builder.attribute(attrID, acLists.get(attrID));
		}
	}

	private void annotateAlleleHomCounts(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<ExacRecord>> records, VariantContextBuilder builder) {
		for (ExacPopulation pop : ExacPopulation.values()) {
			final String attrID = options.getVCFIdentifierPrefix() + infix + "HOM_" + pop;
			ArrayList<Integer> acList = new ArrayList<>();
			boolean use = false;
			for (int i = 1; i < vc.getNAlleles(); ++i) {
				if (records.get(i) == null) {
					acList.add(0);
					continue;
				}
				final ExacRecord record = records.get(i).getRecord();
				final int alleleNo = records.get(i).getAlleleNo();
				if (record.getAlleleHomCounts(pop).isEmpty()) {
					acList.add(0);
				} else {
					acList.add(record.getAlleleHomCounts(pop).get(alleleNo - 1));
					use = true;
				}
			}

			if (use)
				builder.attribute(attrID, acList);

			builder.attribute(attrID, acList);
		}
	}

	private void annotateAlleleHemiCounts(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<ExacRecord>> records, VariantContextBuilder builder) {
		Map<String, List<Integer>> acLists = new HashMap<>();
		for (ExacPopulation pop : ExacPopulation.values()) {
			final String attrID = options.getVCFIdentifierPrefix() + infix + "HEMI_" + pop;
			ArrayList<Integer> acList = new ArrayList<>();
			boolean use = false;
			for (int i = 1; i < vc.getNAlleles(); ++i) {
				if (records.get(i) == null) {
					acList.add(0);
					continue;
				}
				final ExacRecord record = records.get(i).getRecord();
				if (record.getAlleleHemiCounts().isEmpty())
					continue;
				final int alleleNo = records.get(i).getAlleleNo();
				if (record.getAlleleHemiCounts(pop).isEmpty()) {
					acList.add(0);
				} else {
					acList.add(record.getAlleleHemiCounts(pop).get(alleleNo - 1));
					use = true;
				}
			}

			if (use && !acList.isEmpty())
				acLists.put(attrID, acList);
		}

		for (String attrID : acLists.keySet()) {
			builder.attribute(attrID, acLists.get(attrID));
		}
	}

	private void annotateFrequencies(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<ExacRecord>> records, VariantContextBuilder builder) {
		for (ExacPopulation pop : ExacPopulation.values()) {
			final String attrID = options.getVCFIdentifierPrefix() + infix + "AF_" + pop;
			ArrayList<Double> afList = new ArrayList<>();
			for (int i = 1; i < vc.getNAlleles(); ++i) {
				if (records.get(i) == null) {
					afList.add(0.0);
					continue;
				}
				final ExacRecord record = records.get(i).getRecord();
				final int alleleNo = records.get(i).getAlleleNo();
				if (record.getAlleleCounts(pop).isEmpty()) {
					afList.add(0.0);
				} else {
					afList.add(record.getAlleleFrequencies(pop).get(alleleNo - 1));
				}
			}

			if (!afList.isEmpty())
				builder.attribute(attrID, afList);

		}
	}

}
