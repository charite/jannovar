package de.charite.compbio.jannovar.vardbs.exac;

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

// TODO: handle MNVs appropriately

/**
 * Annotation driver class for annotations using ExAC
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public class ExacAnnotationDriver extends AbstractDBAnnotationDriver<ExacRecord> {

	public ExacAnnotationDriver(String vcfPath, String fastaPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		super(vcfPath, fastaPath, options, new ExacVariantContextToRecordConverter());
	}

	@Override
	public VCFHeaderExtender constructVCFHeaderExtender() {
		return new ExacVCFHeaderExtender();
	}

	@Override
	protected HashMap<Integer, AnnotatingRecord<ExacRecord>> pickAnnotatingDBRecords(
			HashMap<Integer, ArrayList<GenotypeMatch>> annotatingRecords,
			HashMap<GenotypeMatch, AnnotatingRecord<ExacRecord>> matchToRecord) {
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
					if (update.getAlleleFrequencies(ExacPopulation.ALL).size() <= alleleNo)
						continue;
					else if (current.getAlleleFrequencies(ExacPopulation.ALL).size() <= alleleNo
							|| current.highestAlleleFreq(alleleNo) < update.highestAlleleFreq(alleleNo))
						annotatingExacRecord.put(alleleNo, matchToRecord.get(m));
				}
			}
		}
		return annotatingExacRecord;
	}

	@Override
	protected VariantContext annotateWithDBRecords(VariantContext vc,
			HashMap<Integer, AnnotatingRecord<ExacRecord>> records) {
		if (records.isEmpty())
			return vc;

		VariantContextBuilder builder = new VariantContextBuilder(vc);

		annotateAlleleCounts(vc, records, builder);
		annotateChromosomeCounts(vc, records, builder);
		annotateFrequencies(vc, records, builder);
		annotateBestAF(vc, records, builder);

		return builder.make();
	}

	private void annotateBestAF(VariantContext vc, HashMap<Integer, AnnotatingRecord<ExacRecord>> records,
			VariantContextBuilder builder) {
		ArrayList<Double> afs = new ArrayList<>();
		ArrayList<Integer> acs = new ArrayList<>();
		for (int i = 1; i < vc.getAlternateAlleles().size(); ++i) {
			if (records.get(i) == null) {
				afs.add(0.0);
				acs.add(0);
			} else {
				final ExacRecord record = records.get(i).getRecord();
				final int alleleNo = records.get(i).getAlleleNo();
				System.err.println(record.toString());
				final ExacPopulation pop = record.popWithHighestAlleleFreq(alleleNo);
				afs.add(record.getAlleleFrequencies(pop).get(alleleNo));
				acs.add(record.getAlleleCounts(pop).get(alleleNo));
			}
		}

		builder.attribute(options.getVCFIdentifierPrefix() + "BEST_AC", acs);
		builder.attribute(options.getVCFIdentifierPrefix() + "BEST_AF", afs);
	}

	private void annotateChromosomeCounts(VariantContext vc, HashMap<Integer, AnnotatingRecord<ExacRecord>> records,
			VariantContextBuilder builder) {
		if (records.isEmpty())
			return;
		ExacRecord first = records.values().iterator().next().getRecord();
		for (ExacPopulation pop : ExacPopulation.values())
			builder.attribute(options.getVCFIdentifierPrefix() + "AN_" + pop, first.getChromCount(pop));
	}

	private void annotateAlleleCounts(VariantContext vc, HashMap<Integer, AnnotatingRecord<ExacRecord>> records,
			VariantContextBuilder builder) {
		for (ExacPopulation pop : ExacPopulation.values()) {
			final String attrID = options.getVCFIdentifierPrefix() + "AC_" + pop;
			ArrayList<Integer> acList = new ArrayList<>();
			for (int i = 0; i < vc.getNAlleles(); ++i) {
				if (records.get(i) == null) {
					acList.add(0);
					continue;
				}
				final ExacRecord record = records.get(i).getRecord();
				final int alleleNo = records.get(i).getAlleleNo();
				if (record.getAlleleCounts(pop).isEmpty()) {
					acList.add(0);
				} else {
					acList.add(record.getAlleleCounts(pop).get(alleleNo));
				}
			}

			if (!acList.isEmpty())
				builder.attribute(attrID, acList);

			builder.attribute(attrID, acList);
		}
	}

	private void annotateFrequencies(VariantContext vc, HashMap<Integer, AnnotatingRecord<ExacRecord>> records,
			VariantContextBuilder builder) {
		for (ExacPopulation pop : ExacPopulation.values()) {
			final String attrID = options.getVCFIdentifierPrefix() + "AF_" + pop;
			ArrayList<Double> afList = new ArrayList<>();
			for (int i = 0; i < vc.getNAlleles(); ++i) {
				if (records.get(i) == null) {
					afList.add(0.0);
					continue;
				}
				final ExacRecord record = records.get(i).getRecord();
				final int alleleNo = records.get(i).getAlleleNo();
				if (record.getAlleleCounts(pop).isEmpty()) {
					afList.add(0.0);
				} else {
					afList.add(record.getAlleleFrequencies(pop).get(alleleNo));
				}
			}

			if (!afList.isEmpty())
				builder.attribute(attrID, afList);

			builder.attribute(attrID, afList);
		}
	}

}
