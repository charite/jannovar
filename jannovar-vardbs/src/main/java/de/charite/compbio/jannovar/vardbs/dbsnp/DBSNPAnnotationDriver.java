package de.charite.compbio.jannovar.vardbs.dbsnp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import de.charite.compbio.jannovar.vardbs.base.AlleleMatcher;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.GenotypeMatch;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFFileReader;

// TODO: handle MNVs appropriately

/**
 * Annotation driver class for annotations using dbSNP
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
final public class DBSNPAnnotationDriver implements DBAnnotationDriver {

	/** Path to dbSNP VCF file */
	final private String vcfPath;
	/** Helper objects for matching alleles */
	final private AlleleMatcher matcher;
	/** Helper for converting from VariantContex to DBSNP record */
	final private DBSNPVariantContextToRecordConverter vcToRecord;
	/** Configuration */
	final private DBAnnotationOptions options;
	/** VCFReader to use for loading the VCF records */
	final private VCFFileReader vcfReader;
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
		this.vcfPath = vcfPath;
		this.matcher = new AlleleMatcher(fastaPath);
		this.vcToRecord = new DBSNPVariantContextToRecordConverter();
		this.vcfReader = new VCFFileReader(new File(this.vcfPath), true);
		this.options = options;

		this.dbSNPInfo = new DBSNPInfoFactory().build(vcfReader.getFileHeader());
		if (dbSNPInfo.dbSNPBuildID != 147)
			throw new JannovarVarDBException(
					"Unsupported dbSNP build ID " + dbSNPInfo.dbSNPBuildID + " only supported is b174");
	}

	@Override
	public VCFHeaderExtender constructVCFHeaderExtender() {
		return new DBSNPVCFHeaderExtender();
	}

	@Override
	public VariantContext annotateVariantContext(VariantContext obsVC) {
		try (CloseableIterator<VariantContext> iter = vcfReader.query(obsVC.getContig(), obsVC.getStart(),
				obsVC.getEnd())) {
			if (options.isReportOverlapping() || options.isReportOverlappingAsIdentical())
				throw new RuntimeException("Not implemented yet!");

			// Fetch all possibly overlapping genotypes from dbSNP and collect those with matching genotypes to the
			// observed variant.
			List<GenotypeMatch> genotypeMatches = new ArrayList<>();
			while (iter.hasNext())
				genotypeMatches.addAll(matcher.matchGenotypes(obsVC, iter.next()));

			// Pick best dbSNP record for each alternative allele
			HashMap<Integer, DBSNPRecord> dbSNPRecords = buildAnnotatingDBSNPRecords(genotypeMatches);

			// Use these records to annotate the variant call in obsVC (record-wise but also per alternative allele)
			return annotateWithDBSNPRecords(obsVC, dbSNPRecords);
		}
	}

	/**
	 * Annotate the given {@link VariantContext} with the given dbSNP records
	 * 
	 * There can be more than one dbSNP record, for example in the case that a SNV is squished together with an indel.
	 * 
	 * @param vc
	 *            The {@link VariantContex} to annotate
	 * @param records
	 *            Map from alternative allele index to annotating {@link DBSNPRecord}
	 */
	private VariantContext annotateWithDBSNPRecords(VariantContext vc, HashMap<Integer, DBSNPRecord> records) {
		VariantContextBuilder builder = new VariantContextBuilder(vc);

		annotateIDs(vc, records, builder);

		annotateInfoCommon(vc, records, builder);
		annotateInfoCAF(vc, records, builder);
		annotateInfoG5(vc, records, builder);
		annotateInfoG5A(vc, records, builder);
		annotateInfoMatch(vc, records, builder);
		annotateInfoOverlap(vc, records, builder);

		return builder.make();
	}

	private void annotateInfoOverlap(VariantContext vc, HashMap<Integer, DBSNPRecord> records,
			VariantContextBuilder builder) {
		// XXX TODO XXX
	}

	private void annotateInfoMatch(VariantContext vc, HashMap<Integer, DBSNPRecord> records,
			VariantContextBuilder builder) {
		String idMatch = options.getVCFIdentifierPrefix() + "MATCH";
		ArrayList<String> matchList = Lists.newArrayList(vc.getID().split(";"));
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			final DBSNPRecord record = records.get(i);
			if (record != null) {
				final String id = record.getId();
				if (!matchList.contains(id))
					matchList.add(id);
			}
		}

		if (matchList.size() > 1)
			matchList.remove(".");

		builder.attribute(idMatch, matchList);
	}

	private void annotateInfoG5A(VariantContext vc, HashMap<Integer, DBSNPRecord> records,
			VariantContextBuilder builder) {
		String idG5A = options.getVCFIdentifierPrefix() + "G5A";
		ArrayList<Boolean> g5AList = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			final DBSNPRecord record = records.get(i);
			if (record != null) {
				g5AList.add(record.isFivePercentAll());
			} else {
				g5AList.add(false);
			}
		}
		builder.attribute(idG5A, g5AList);
	}

	private void annotateInfoG5(VariantContext vc, HashMap<Integer, DBSNPRecord> records,
			VariantContextBuilder builder) {
		String idG5 = options.getVCFIdentifierPrefix() + "G5";
		ArrayList<Boolean> g5List = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			final DBSNPRecord record = records.get(i);
			if (record != null) {
				g5List.add(record.isFivePercentOne());
			} else {
				g5List.add(false);
			}
		}
		builder.attribute(idG5, g5List);
	}

	private void annotateInfoCAF(VariantContext vc, HashMap<Integer, DBSNPRecord> records,
			VariantContextBuilder builder) {
		String idCAF = options.getVCFIdentifierPrefix() + "CAF";
		ArrayList<Double> cafList = new ArrayList<>();
		cafList.add(null);
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			final DBSNPRecord record = records.get(i);
			if (record != null && !record.getAlleleFrequenciesG1K().isEmpty()) {
				if (cafList.get(0) == null)
					cafList.set(0, record.getAlleleFrequenciesG1K().get(0));
				cafList.add(record.getAlleleFrequenciesG1K().get(i));
			} else {
				cafList.add(0.0);
			}
		}

		if (cafList.get(0) == null)
			cafList.set(0, 0.0);

		builder.attribute(idCAF, cafList);
	}

	private void annotateInfoCommon(VariantContext vc, HashMap<Integer, DBSNPRecord> records,
			VariantContextBuilder builder) {
		String idCommon = options.getVCFIdentifierPrefix() + "COMMON";
		ArrayList<Integer> commonList = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			final DBSNPRecord record = records.get(i);
			if (record != null) {
				commonList.add(record.isCommon() ? 1 : 0);
			} else {
				commonList.add(0);
			}
		}
		builder.attribute(idCommon, commonList);
	}

	private void annotateIDs(VariantContext vc, HashMap<Integer, DBSNPRecord> records, VariantContextBuilder builder) {
		ArrayList<String> idList = Lists.newArrayList(vc.getID().split(";"));
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			final DBSNPRecord record = records.get(i);
			if (record != null) {
				final String id = record.getId();
				if (!idList.contains(id))
					idList.add(id);
			}
		}

		if (idList.size() > 1)
			idList.remove(".");

		builder.id(Joiner.on(";").join(idList));
	}

	/**
	 * Build mapping from alternative allele number to dbSNP record to use
	 * 
	 * For SNVs, there should only be one value in the value set at which all alleles point to for most cases. The
	 * variant with the lowermost allele number will be chosen for annotating the reference allele.
	 * 
	 * @param genotypeMatches
	 *            List of {@link GenotypeMatch} objects to build the annotating dbSNP records from
	 * @return Resulting map from alternative allele ID (starting with 1) to the DBSNP record to use
	 */
	private HashMap<Integer, DBSNPRecord> buildAnnotatingDBSNPRecords(List<GenotypeMatch> genotypeMatches) {
		// Collect annotating variants for each allele
		HashMap<Integer, ArrayList<GenotypeMatch>> annotatingRecords = new HashMap<>();
		HashMap<GenotypeMatch, DBSNPRecord> matchToDBSNP = new HashMap<>();
		for (GenotypeMatch match : genotypeMatches) {
			final int alleleNo = match.getObservedAllele();
			annotatingRecords.putIfAbsent(alleleNo, new ArrayList<GenotypeMatch>());
			annotatingRecords.get(alleleNo).add(match);
			if (!matchToDBSNP.containsKey(match))
				matchToDBSNP.put(match, vcToRecord.convert(match.getDBVC()));
		}

		// Pick best annotation for each alternative allele
		HashMap<Integer, DBSNPRecord> annotatingDBSNPRecord = new HashMap<>();
		for (Entry<Integer, ArrayList<GenotypeMatch>> entry : annotatingRecords.entrySet()) {
			final int alleleNo = entry.getKey();
			for (GenotypeMatch m : entry.getValue()) {
				if (!annotatingDBSNPRecord.containsKey(alleleNo)) {
					annotatingDBSNPRecord.put(alleleNo, matchToDBSNP.get(m));
				} else {
					final DBSNPRecord current = annotatingDBSNPRecord.get(alleleNo);
					final DBSNPRecord update = matchToDBSNP.get(m);
					if (current.getAlleleFrequenciesG1K().get(alleleNo) < update.getAlleleFrequenciesG1K()
							.get(alleleNo))
						annotatingDBSNPRecord.put(alleleNo, matchToDBSNP.get(m));
				}
			}
		}
		return annotatingDBSNPRecord;
	}

	/** @return Information about the used dbSNP VCF file */
	public DBSNPInfo getDbSNPInfo() {
		return dbSNPInfo;
	}

}
