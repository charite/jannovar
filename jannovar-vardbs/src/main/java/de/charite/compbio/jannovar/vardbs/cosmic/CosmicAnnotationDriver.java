package de.charite.compbio.jannovar.vardbs.cosmic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.vardbs.base.AbstractDBAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.base.AnnotatingRecord;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.GenotypeMatch;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;

/**
 * Annotation driver class for annotations using COSMIC data
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class CosmicAnnotationDriver extends AbstractDBAnnotationDriver<CosmicRecord> {

	public CosmicAnnotationDriver(String vcfPath, String fastaPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		super(vcfPath, fastaPath, options, new CosmicVariantContextToRecordConverter());
	}

	@Override
	protected HashMap<Integer, AnnotatingRecord<CosmicRecord>> pickAnnotatingDBRecords(
			HashMap<Integer, ArrayList<GenotypeMatch>> annotatingRecords,
			HashMap<GenotypeMatch, AnnotatingRecord<CosmicRecord>> matchToRecord, boolean isMatch) {
		// Pick best annotation for each alternative allele
		HashMap<Integer, AnnotatingRecord<CosmicRecord>> annotatingRecord = new HashMap<>();
		for (Entry<Integer, ArrayList<GenotypeMatch>> entry : annotatingRecords.entrySet()) {
			final int alleleNo = entry.getKey();
			for (GenotypeMatch m : entry.getValue()) {
				if (!annotatingRecord.containsKey(alleleNo)) {
					annotatingRecord.put(alleleNo, matchToRecord.get(m));
				} else {
					final CosmicRecord current = annotatingRecord.get(alleleNo).getRecord();
					final CosmicRecord update = matchToRecord.get(m).getRecord();
					if (current.getCnt() < update.getCnt())
						annotatingRecord.put(alleleNo, matchToRecord.get(m));
				}
			}
		}
		return annotatingRecord;
	}

	@Override
	public VCFHeaderExtender constructVCFHeaderExtender() {
		return new CosmicVCFHeaderExtender(options);
	}

	@Override
	protected VariantContext annotateWithDBRecords(VariantContext vc,
			HashMap<Integer, AnnotatingRecord<CosmicRecord>> matchRecords,
			HashMap<Integer, AnnotatingRecord<CosmicRecord>> overlapRecords) {
		VariantContextBuilder builder = new VariantContextBuilder(vc);

		annotateIDs(vc, matchRecords, builder);

		// Annotate with records with matching allele
		annotateInfoCnt(vc, "", matchRecords, builder);
		annotateInfoSnp(vc, "", matchRecords, builder);
		annotateInfoID(vc, "", matchRecords, builder);

		// Annotate with records with overlapping positions
		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching()) {
			annotateInfoCnt(vc, "OVL_", overlapRecords, builder);
			annotateInfoSnp(vc, "OVL_", overlapRecords, builder);
			annotateInfoID(vc, "OVL_", matchRecords, builder);
		}

		return builder.make();
	}

	private void annotateIDs(VariantContext vc, HashMap<Integer, AnnotatingRecord<CosmicRecord>> records,
			VariantContextBuilder builder) {
		ArrayList<String> idList = Lists.newArrayList(vc.getID().split(";"));
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			if (records.get(i) != null) {
				CosmicRecord record = records.get(i).getRecord();
				final String id = record.getId();
				if (!idList.contains(id))
					idList.add(id);
			}
		}

		if (idList.size() > 1)
			idList.remove(".");

		if (!idList.isEmpty())
			builder.id(Joiner.on(";").join(idList));
	}

	private void annotateInfoCnt(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<CosmicRecord>> records, VariantContextBuilder builder) {
		if (records.isEmpty())
			return;
		CosmicRecord first = records.values().iterator().next().getRecord();
		builder.attribute(options.getVCFIdentifierPrefix() + infix + "CNT", first.getCnt());
	}

	private void annotateInfoSnp(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<CosmicRecord>> records, VariantContextBuilder builder) {
		if (records.isEmpty())
			return;
		CosmicRecord first = records.values().iterator().next().getRecord();
		if (first.isSnp())
			builder.attribute(options.getVCFIdentifierPrefix() + infix + "SNP", true);
	}

	private void annotateInfoID(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<CosmicRecord>> records, VariantContextBuilder builder) {
		String idIDs = options.getVCFIdentifierPrefix() + infix + "IDS";
		ArrayList<ArrayList<String>> matchList = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			ArrayList<String> lst = new ArrayList<>();
			if (records.get(i) != null) {
				final CosmicRecord record = records.get(i).getRecord();
				final String id = record.getId();
				if (!lst.contains(id))
					lst.add(id);
			}
			matchList.add(lst);
		}

		ArrayList<String> vals = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			if (matchList.get(i - 1).isEmpty())
				vals.add(".");
			else
				vals.add(Joiner.on('|').join(matchList.get(i - 1)));
		}

		if (vals.stream().allMatch(s -> ".".equals(s)))
			return; // do not set list of "."

		builder.attribute(idIDs, vals);
	}

}
