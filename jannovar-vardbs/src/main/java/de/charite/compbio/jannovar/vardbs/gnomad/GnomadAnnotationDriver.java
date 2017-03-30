package de.charite.compbio.jannovar.vardbs.gnomad;

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
public class GnomadAnnotationDriver extends AbstractDBAnnotationDriver<GnomadRecord> {

	public GnomadAnnotationDriver(String vcfPath, String fastaPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		super(vcfPath, fastaPath, options, new GnomadVariantContextToRecordConverter());
	}

	@Override
	public VCFHeaderExtender constructVCFHeaderExtender() {
		return new GnomadVCFHeaderExtender(options);
	}

	@Override
	protected HashMap<Integer, AnnotatingRecord<GnomadRecord>> pickAnnotatingDBRecords(
			HashMap<Integer, ArrayList<GenotypeMatch>> annotatingRecords,
			HashMap<GenotypeMatch, AnnotatingRecord<GnomadRecord>> matchToRecord, boolean isMatch) {
		// Pick best annotation for each alternative allele
		HashMap<Integer, AnnotatingRecord<GnomadRecord>> annotatingGnomadRecord = new HashMap<>();
		for (Entry<Integer, ArrayList<GenotypeMatch>> entry : annotatingRecords.entrySet()) {
			final int alleleNo = entry.getKey();
			for (GenotypeMatch m : entry.getValue()) {
				if (!annotatingGnomadRecord.containsKey(alleleNo)) {
					annotatingGnomadRecord.put(alleleNo, matchToRecord.get(m));
				} else {
					final GnomadRecord current = annotatingGnomadRecord.get(alleleNo).getRecord();
					final GnomadRecord update = matchToRecord.get(m).getRecord();
					if (update.getAlleleFrequencies(GnomadPopulation.ALL).size() < alleleNo)
						continue;
					if ((isMatch && current.highestAlleleFreq(alleleNo - 1) < update.highestAlleleFreq(alleleNo - 1))
							|| (!isMatch && current.highestAlleleFreqOverall() < update.highestAlleleFreqOverall()))
						annotatingGnomadRecord.put(alleleNo, matchToRecord.get(m));
				}
			}
		}
		return annotatingGnomadRecord;
	}

	@Override
	protected VariantContext annotateWithDBRecords(VariantContext vc,
			HashMap<Integer, AnnotatingRecord<GnomadRecord>> matchRecords,
			HashMap<Integer, AnnotatingRecord<GnomadRecord>> overlapRecords) {
		if (matchRecords.isEmpty())
			return vc;

		VariantContextBuilder builder = new VariantContextBuilder(vc);

		// Annotate with records with matching allele
		boolean isMatch = !options.isReportOverlappingAsMatching();
		annotateAlleleCounts(vc, "", matchRecords, builder, isMatch);
		annotateAlleleHetCounts(vc, "", matchRecords, builder, isMatch);
		annotateAlleleHomCounts(vc, "", matchRecords, builder, isMatch);
		annotateAlleleHemiCounts(vc, "", matchRecords, builder, isMatch);
		annotateChromosomeCounts(vc, "", matchRecords, builder, isMatch);
		annotateFrequencies(vc, "", matchRecords, builder, isMatch);
		annotatePopmax(vc, "", matchRecords, builder, isMatch);

		// Annotate with records with overlapping positions
		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching()) {
			annotateAlleleCounts(vc, "OVL_", overlapRecords, builder, false);
			annotateAlleleHetCounts(vc, "OVL_", overlapRecords, builder, false);
			annotateAlleleHomCounts(vc, "OVL_", overlapRecords, builder, false);
			annotateAlleleHemiCounts(vc, "OVL_", overlapRecords, builder, false);
			annotateChromosomeCounts(vc, "OVL_", overlapRecords, builder, false);
			annotateFrequencies(vc, "OVL_", overlapRecords, builder, false);
			annotatePopmax(vc, "OVL_", matchRecords, builder, false);
		}

		return builder.make();
	}

	private void annotateChromosomeCounts(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<GnomadRecord>> records, VariantContextBuilder builder, boolean isMatch) {
		if (records.isEmpty())
			return;
		GnomadRecord first = records.values().iterator().next().getRecord();
		for (GnomadPopulation pop : GnomadPopulation.values())
			builder.attribute(options.getVCFIdentifierPrefix() + infix + "AN_" + pop, first.getChromCount(pop));
	}

	private void annotateAlleleCounts(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<GnomadRecord>> records, VariantContextBuilder builder, boolean isMatch) {
		Map<String, List<Integer>> acLists = new HashMap<>();
		for (GnomadPopulation pop : GnomadPopulation.values()) {
			final String attrID = options.getVCFIdentifierPrefix() + infix + "AC_" + pop;
			List<Integer> acList = new ArrayList<>();
			if (isMatch) {
				// Pick matching allele
				for (int i = 1; i < vc.getNAlleles(); ++i) {
					if (records.get(i) == null) {
						acList.add(0);
						continue;
					}
					final GnomadRecord record = records.get(i).getRecord();
					final int alleleNo = records.get(i).getAlleleNo();
					if (record.getAlleleCounts(pop) == null || (alleleNo - 1 >= record.getAlleleCounts(pop).size())) {
						acList.add(0);
					} else {
						acList.add(record.getAlleleCounts(pop).get(alleleNo - 1));
					}
				}
			} else {
				// Pick best annotating record with highest AF and and use for all
				final AnnotatingRecord<GnomadRecord> bestAnnoRecord = pickBestAnnoRecord(vc, records, pop);
				if (bestAnnoRecord != null)
					for (int i = 1; i < vc.getNAlleles(); ++i) {
						if (!bestAnnoRecord.getRecord().getAlleleCounts().containsKey(pop))
							acList.add(0);
						else
							acList.add(bestAnnoRecord.getRecord().getAlleleCounts().get(pop)
									.get(bestAnnoRecord.getAlleleNo() - 1));
					}
			}

			if (!acList.isEmpty())
				acLists.put(attrID, acList);
		}

		for (String attrID : acLists.keySet()) {
			builder.attribute(attrID, acLists.get(attrID));
		}
	}

	private void annotateAlleleHetCounts(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<GnomadRecord>> records, VariantContextBuilder builder, boolean isMatch) {
		Map<String, List<Integer>> acLists = new HashMap<>();
		for (GnomadPopulation pop : GnomadPopulation.values()) {
			final String attrID = options.getVCFIdentifierPrefix() + infix + "HET_" + pop;
			ArrayList<Integer> acList = new ArrayList<>();
			if (isMatch) {
				// Pick matching allele
				for (int i = 1; i < vc.getNAlleles(); ++i) {
					if (records.get(i) == null) {
						acList.add(0);
						continue;
					}
					final GnomadRecord record = records.get(i).getRecord();
					final int alleleNo = records.get(i).getAlleleNo();
					if (record.getAlleleHetCounts(pop) == null
							|| (alleleNo - 1 >= record.getAlleleHetCounts(pop).size())) {
						acList.add(0);
					} else {
						acList.add(record.getAlleleHetCounts(pop).get(alleleNo - 1));
					}
				}
			} else {
				// Pick best annotating record with highest AF and and use for all
				final AnnotatingRecord<GnomadRecord> bestAnnoRecord = pickBestAnnoRecord(vc, records, pop);
				if (bestAnnoRecord != null)
					for (int i = 1; i < vc.getNAlleles(); ++i) {
						if (!bestAnnoRecord.getRecord().getAlleleHetCounts().containsKey(pop))
							acList.add(0);
						else
							acList.add(bestAnnoRecord.getRecord().getAlleleHetCounts().get(pop)
									.get(bestAnnoRecord.getAlleleNo() - 1));
					}
			}

			if (!acList.isEmpty())
				acLists.put(attrID, acList);
		}

		for (String attrID : acLists.keySet()) {
			builder.attribute(attrID, acLists.get(attrID));
		}
	}

	private void annotateAlleleHomCounts(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<GnomadRecord>> records, VariantContextBuilder builder, boolean isMatch) {
		for (GnomadPopulation pop : GnomadPopulation.values()) {
			final String attrID = options.getVCFIdentifierPrefix() + infix + "HOM_" + pop;
			ArrayList<Integer> acList = new ArrayList<>();
			if (isMatch) {
				// Pick matching allele
				for (int i = 1; i < vc.getNAlleles(); ++i) {
					if (records.get(i) == null) {
						acList.add(0);
						continue;
					}
					final GnomadRecord record = records.get(i).getRecord();
					final int alleleNo = records.get(i).getAlleleNo();
					if (record.getAlleleHomCounts(pop) == null
							|| (alleleNo - 1 >= record.getAlleleHomCounts(pop).size())) {
						acList.add(0);
					} else {
						acList.add(record.getAlleleHomCounts(pop).get(alleleNo - 1));
					}
				}
			} else {
				// Pick best annotating record with highest AF and and use for all
				final AnnotatingRecord<GnomadRecord> bestAnnoRecord = pickBestAnnoRecord(vc, records, pop);
				if (bestAnnoRecord != null)
					for (int i = 1; i < vc.getNAlleles(); ++i) {
						if (!bestAnnoRecord.getRecord().getAlleleHomCounts().containsKey(pop))
							acList.add(0);
						else
							acList.add(bestAnnoRecord.getRecord().getAlleleHomCounts().get(pop)
									.get(bestAnnoRecord.getAlleleNo() - 1));
					}
			}

			if (!acList.isEmpty())
				builder.attribute(attrID, acList);
		}
	}

	private void annotateAlleleHemiCounts(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<GnomadRecord>> records, VariantContextBuilder builder, boolean isMatch) {
		Map<String, List<Integer>> acLists = new HashMap<>();
		for (GnomadPopulation pop : GnomadPopulation.values()) {
			final String attrID = options.getVCFIdentifierPrefix() + infix + "HEMI_" + pop;
			ArrayList<Integer> acList = new ArrayList<>();
			if (isMatch) {
				// Pick matching allele
				for (int i = 1; i < vc.getNAlleles(); ++i) {
					if (records.get(i) == null) {
						acList.add(0);
						continue;
					}
					final GnomadRecord record = records.get(i).getRecord();
					if (record.getAlleleHemiCounts().isEmpty())
						continue;
					final int alleleNo = records.get(i).getAlleleNo();
					if (record.getAlleleHemiCounts(pop) == null
							|| (alleleNo - 1 >= record.getAlleleHemiCounts(pop).size())) {
						acList.add(0);
					} else {
						acList.add(record.getAlleleHemiCounts(pop).get(alleleNo - 1));
					}
				}
			} else {
				// Pick best annotating record with highest AF and and use for all
				final AnnotatingRecord<GnomadRecord> bestAnnoRecord = pickBestAnnoRecord(vc, records, pop);
				if (bestAnnoRecord != null)
					for (int i = 1; i < vc.getNAlleles(); ++i) {
						if (!bestAnnoRecord.getRecord().getAlleleHemiCounts().containsKey(pop))
							acList.add(0);
						else
							acList.add(bestAnnoRecord.getRecord().getAlleleHemiCounts().get(pop)
									.get(bestAnnoRecord.getAlleleNo() - 1));
					}
			}

			if (!acList.isEmpty())
				acLists.put(attrID, acList);
		}

		for (String attrID : acLists.keySet()) {
			builder.attribute(attrID, acLists.get(attrID));
		}
	}

	private void annotateFrequencies(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<GnomadRecord>> records, VariantContextBuilder builder, boolean isMatch) {
		for (GnomadPopulation pop : GnomadPopulation.values()) {
			final String attrID = options.getVCFIdentifierPrefix() + infix + "AF_" + pop;
			ArrayList<Double> afList = new ArrayList<>();
			if (isMatch) {
				// Pick matching allele
				for (int i = 1; i < vc.getNAlleles(); ++i) {
					if (records.get(i) == null) {
						afList.add(0.0);
						continue;
					}
					final GnomadRecord record = records.get(i).getRecord();
					final int alleleNo = records.get(i).getAlleleNo();
					if (record.getAlleleCounts(pop) != null && record.getAlleleCounts(pop).isEmpty()) {
						afList.add(0.0);
					} else {
						if (record.getAlleleFrequencies(pop) == null
								|| (alleleNo - 1 >= record.getAlleleFrequencies(pop).size()))
							afList.add(0.0);
						else
							afList.add(record.getAlleleFrequencies(pop).get(alleleNo - 1));
					}
				}
			} else {
				// Pick best annotating record with highest AF and and use for all
				final AnnotatingRecord<GnomadRecord> bestAnnoRecord = pickBestAnnoRecord(vc, records, pop);
				if (bestAnnoRecord != null)
					for (int i = 1; i < vc.getNAlleles(); ++i) {
						if (!bestAnnoRecord.getRecord().getAlleleFrequencies().containsKey(pop))
							afList.add(0.0);
						else
							afList.add(bestAnnoRecord.getRecord().getAlleleFrequencies().get(pop)
									.get(bestAnnoRecord.getAlleleNo() - 1));
					}
			}

			if (!afList.isEmpty())
				builder.attribute(attrID, afList);
		}
	}

	private void annotatePopmax(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<GnomadRecord>> records, VariantContextBuilder builder, boolean isMatch) {
		final String attrID = options.getVCFIdentifierPrefix() + infix + "POPMAX";
		ArrayList<String> popmaxList = new ArrayList<>();
		if (isMatch) {
			// Pick matching allele
			for (int i = 1; i < vc.getNAlleles(); ++i) {
				if (records.get(i) == null) {
					popmaxList.add(".");
					continue;
				}
				final GnomadRecord record = records.get(i).getRecord();
				final int alleleNo = records.get(i).getAlleleNo();
				if (record.getPopmax().isEmpty()) {
					popmaxList.add(".");
				} else {
					popmaxList.add(record.getPopmax().get(alleleNo - 1));
				}
			}
		} else {
			// Pick best annotating record with highest AF and and use for all
			final GnomadPopulation bestPop = pickBestPop(vc, records);
			for (int i = 1; i < vc.getNAlleles(); ++i)
				popmaxList.add(bestPop.toString());
		}

		if (!popmaxList.isEmpty())
			builder.attribute(attrID, popmaxList);
	}

	private AnnotatingRecord<GnomadRecord> pickBestAnnoRecord(VariantContext vc,
			HashMap<Integer, AnnotatingRecord<GnomadRecord>> records, GnomadPopulation pop) {
		AnnotatingRecord<GnomadRecord> result = null;
		double bestAF = -1;
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			if (records.get(i) == null)
				continue;
			final GnomadRecord record = records.get(i).getRecord();
			if (record.getAlleleFrequencies(pop) != null)
				for (int alleleNo = 1; alleleNo <= record.getAlleleFrequencies(pop).size(); ++alleleNo)
					if (bestAF < record.getAlleleFrequencies(pop).get(alleleNo - 1)) {
						bestAF = record.getAlleleFrequencies(pop).get(alleleNo - 1);
						result = new AnnotatingRecord<>(record, alleleNo);
					}
		}
		return result;
	}

	private GnomadPopulation pickBestPop(VariantContext vc, HashMap<Integer, AnnotatingRecord<GnomadRecord>> records) {
		GnomadPopulation result = null;
		double bestAF = -1;
		for (GnomadPopulation pop : GnomadPopulation.values()) {
			for (int i = 1; i < vc.getNAlleles(); ++i) {
				if (records.get(i) == null)
					continue;
				final GnomadRecord record = records.get(i).getRecord();
				if (!record.getAlleleFrequencies().containsKey(pop))
					continue;
				if (record.getAlleleFrequencies(pop) != null)
					for (int alleleNo = 1; alleleNo <= record.getAlleleFrequencies(pop).size(); ++alleleNo)
						if (bestAF < record.getAlleleFrequencies(pop).get(alleleNo - 1)) {
							bestAF = record.getAlleleFrequencies(pop).get(alleleNo - 1);
							result = pop;
						}
			}
		}
		return result;
	}

}
