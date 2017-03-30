package de.charite.compbio.jannovar.vardbs.dbsnp;

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
 * Annotation driver class for annotations using dbSNP
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final public class DBSNPAnnotationDriver extends AbstractDBAnnotationDriver<DBSNPRecord> {

	/** Information about the dbSNP VCF file */
	final private DBSNPInfo dbSNPInfo;

	/**
	 * Create annotation driver for a coordinate-sorted, bgzip-compressed, dbSNP VCF file
	 * 
	 * @param fastaPath
	 *            FAI-indexed FASTA file with reference
	 * @param vcfPath
	 *            Path to VCF file with dbSNP.
	 * @throws JannovarVarDBException
	 *             on problems loading the reference FASTA/FAI file or incompatible dbSNP version
	 */
	public DBSNPAnnotationDriver(String vcfPath, String fastaPath, DBAnnotationOptions options)
			throws JannovarVarDBException {
		super(vcfPath, fastaPath, options, new DBSNPVariantContextToRecordConverter());

		this.dbSNPInfo = new DBSNPInfoFactory().build(vcfReader.getFileHeader());
		if (dbSNPInfo.dbSNPBuildID != 147)
			throw new JannovarVarDBException(
					"Unsupported dbSNP build ID " + dbSNPInfo.dbSNPBuildID + " only supported is b174");
	}

	@Override
	protected VariantContext annotateWithDBRecords(VariantContext vc,
			HashMap<Integer, AnnotatingRecord<DBSNPRecord>> matchRecords,
			HashMap<Integer, AnnotatingRecord<DBSNPRecord>> overlapRecords) {
		VariantContextBuilder builder = new VariantContextBuilder(vc);

		annotateIDs(vc, matchRecords, builder);

		// Annotate with records with matching allele
		annotateInfoCommon(vc, "", matchRecords, builder);
		annotateInfoCAF(vc, "", matchRecords, builder);
		annotateInfoG5(vc, "", matchRecords, builder);
		annotateInfoG5A(vc, "", matchRecords, builder);
		annotateInfoIDs(vc, "", matchRecords, builder);
		annotateInfoOrigin(vc, "", matchRecords, builder);

		// Annotate with records with overlapping positions
		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching()) {
			annotateInfoCommon(vc, "OVL_", overlapRecords, builder);
			annotateInfoCAF(vc, "OVL_", overlapRecords, builder);
			annotateInfoG5(vc, "OVL_", overlapRecords, builder);
			annotateInfoG5A(vc, "OVL_", overlapRecords, builder);
			annotateInfoIDs(vc, "OVL_", overlapRecords, builder);
			annotateInfoOrigin(vc, "OVL_", overlapRecords, builder);
		}

		return builder.make();
	}

	private void annotateInfoG5A(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<DBSNPRecord>> records, VariantContextBuilder builder) {
		String idG5A = options.getVCFIdentifierPrefix() + infix + "G5A";
		ArrayList<Integer> g5AList = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			if (records.get(i) != null) {
				final DBSNPRecord record = records.get(i).getRecord();
				g5AList.add(record.isFivePercentAll() ? 1 : 0);
			} else {
				g5AList.add(0);
			}
		}

		if (g5AList.stream().allMatch(i -> (i == 0)))
			return; // do not set list of zeroes

		if (!g5AList.isEmpty())
			builder.attribute(idG5A, g5AList);
	}

	private void annotateInfoG5(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<DBSNPRecord>> records, VariantContextBuilder builder) {
		String idG5 = options.getVCFIdentifierPrefix() + infix + "G5";
		ArrayList<Integer> g5List = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			if (records.get(i) != null) {
				final DBSNPRecord record = records.get(i).getRecord();
				g5List.add(record.isFivePercentOne() ? 1 : 0);
			} else {
				g5List.add(0);
			}
		}

		if (g5List.stream().allMatch(i -> (i == 0)))
			return; // do not set list of zeroes

		if (!g5List.isEmpty())
			builder.attribute(idG5, g5List);
	}

	private void annotateInfoCAF(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<DBSNPRecord>> records, VariantContextBuilder builder) {
		String idCAF = options.getVCFIdentifierPrefix() + infix + "CAF";
		ArrayList<Double> cafList = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			DBSNPRecord record = null;
			if (records.get(i) != null)
				record = records.get(i).getRecord();
			if (record != null && !record.getAlleleFrequenciesG1K().isEmpty()) {
				final int alleleNo = records.get(i).getAlleleNo();
				if (alleleNo - 1 < record.getAlleleFrequenciesG1K().size())
					cafList.add(record.getAlleleFrequenciesG1K().get(alleleNo - 1));
				else
					cafList.add(0.0);
			} else {
				cafList.add(0.0);
			}
		}

		if (cafList.stream().allMatch(i -> (i == 0.0)))
			return; // do not set list of zeroes

		// Prepend reference frequency
		double afRef = 1.0 - cafList.stream().mapToDouble(x -> x).sum();
		afRef = Math.max(afRef, 0); // no negative values
		cafList.add(0, afRef);

		if (!cafList.isEmpty())
			builder.attribute(idCAF, cafList);
	}

	private void annotateInfoOrigin(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<DBSNPRecord>> records, VariantContextBuilder builder) {
		String idOrigin = options.getVCFIdentifierPrefix() + infix + "SAO";
		ArrayList<String> originList = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			if (records.get(i) != null) {
				final DBSNPRecord record = records.get(i).getRecord();
				originList.add(record.getVariantAlleleOrigin().toString());
			} else {
				originList.add(DBSNPVariantAlleleOrigin.UNSPECIFIED.toString());
			}
		}

		if (originList.stream().allMatch(s -> ("UNSPECIFIED".equals(s))))
			return; // do not set list of zeroes

		if (!originList.isEmpty())
			builder.attribute(idOrigin, originList);
	}

	private void annotateInfoCommon(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<DBSNPRecord>> records, VariantContextBuilder builder) {
		String idCommon = options.getVCFIdentifierPrefix() + infix + "COMMON";
		ArrayList<Integer> commonList = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			if (records.get(i) != null) {
				final DBSNPRecord record = records.get(i).getRecord();
				commonList.add(record.isCommon() ? 1 : 0);
			} else {
				commonList.add(0);
			}
		}

		if (commonList.stream().allMatch(i -> (i == 0)))
			return; // do not set list of zeroes

		if (!commonList.isEmpty())
			builder.attribute(idCommon, commonList);
	}

	private void annotateInfoIDs(VariantContext vc, String infix,
			HashMap<Integer, AnnotatingRecord<DBSNPRecord>> records, VariantContextBuilder builder) {
		String idIDs = options.getVCFIdentifierPrefix() + infix + "IDS";
		ArrayList<ArrayList<String>> matchList = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			ArrayList<String> lst = new ArrayList<>();
			if (records.get(i) != null) {
				final DBSNPRecord record = records.get(i).getRecord();
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

	private void annotateIDs(VariantContext vc, HashMap<Integer, AnnotatingRecord<DBSNPRecord>> records,
			VariantContextBuilder builder) {
		ArrayList<String> idList = Lists.newArrayList(vc.getID().split(";"));
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			if (records.get(i) != null) {
				DBSNPRecord record = records.get(i).getRecord();
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

	@Override
	protected HashMap<Integer, AnnotatingRecord<DBSNPRecord>> pickAnnotatingDBRecords(
			HashMap<Integer, ArrayList<GenotypeMatch>> annotatingRecords,
			HashMap<GenotypeMatch, AnnotatingRecord<DBSNPRecord>> matchToRecord, boolean isMatch) {
		// Pick best annotation for each alternative allele
		HashMap<Integer, AnnotatingRecord<DBSNPRecord>> annotatingDBSNPRecord = new HashMap<>();
		for (Entry<Integer, ArrayList<GenotypeMatch>> entry : annotatingRecords.entrySet()) {
			final int alleleNo = entry.getKey();
			for (GenotypeMatch m : entry.getValue()) {
				if (!annotatingDBSNPRecord.containsKey(alleleNo)) {
					annotatingDBSNPRecord.put(alleleNo, matchToRecord.get(m));
				} else {
					final DBSNPRecord current = annotatingDBSNPRecord.get(alleleNo).getRecord();
					final DBSNPRecord update = matchToRecord.get(m).getRecord();
					if (update.getAlleleFrequenciesG1K().size() <= alleleNo)
						continue; // no number to update
					if ((isMatch && (current.getAlleleFrequenciesG1K().size() <= alleleNo
							|| current.getAlleleFrequenciesG1K().get(alleleNo - 1) < update.getAlleleFrequenciesG1K()
									.get(alleleNo - 1)))
							|| (!isMatch
									&& current.highestAlleleFreqG1KOverall() < update.highestAlleleFreqG1KOverall()))
						annotatingDBSNPRecord.put(alleleNo, matchToRecord.get(m));
				}
			}
		}
		return annotatingDBSNPRecord;
	}

	/** @return Information about the used dbSNP VCF file */
	public DBSNPInfo getDbSNPInfo() {
		return dbSNPInfo;
	}

	@Override
	public VCFHeaderExtender constructVCFHeaderExtender() {
		return new DBSNPVCFHeaderExtender(options);
	}

}
