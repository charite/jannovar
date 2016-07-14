package de.charite.compbio.jannovar.vardbs.base;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.charite.compbio.jannovar.vardbs.dbsnp.DBSNPRecord;
import de.charite.compbio.jannovar.vardbs.dbsnp.DBSNPVCFHeaderExtender;
import de.charite.compbio.jannovar.vardbs.exac.ExacRecord;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

/**
 * Abstract base class for annotation based on VCF files.
 * 
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
public abstract class AbstractDBAnnotationDriver<RecordType> implements DBAnnotationDriver {

	/** Path to dbSNP VCF file */
	protected final String vcfPath;
	/** Helper objects for matching alleles */
	protected final AlleleMatcher matcher;
	/** Helper for converting from VariantContex to DBSNP record */
	protected final VariantContextToRecordConverter<RecordType> vcToRecord;
	/** Configuration */
	protected final DBAnnotationOptions options;
	/** VCFReader to use for loading the VCF records */
	protected final VCFFileReader vcfReader;

	/**
	 * Create annotation driver for a coordinate-sorted, bgzip-compressed, VCF file
	 * 
	 * @param fastaPath
	 *            FAI-indexed FASTA file with reference
	 * @param vcfPath
	 *            Path to VCF file with dbSNP.
	 * @param options
	 *            configuration
	 * @param vcToRecord
	 *            converter from {@link VariantContext} to record type
	 * @throws JannovarVarDBException
	 *             on problems loading the reference FASTA/FAI file or incompatible dbSNP version
	 */
	public AbstractDBAnnotationDriver(String vcfPath, String fastaPath, DBAnnotationOptions options,
			VariantContextToRecordConverter<RecordType> vcToRecord) throws JannovarVarDBException {
		this.vcfPath = vcfPath;
		this.matcher = new AlleleMatcher(fastaPath);
		this.vcToRecord = vcToRecord;
		this.vcfReader = new VCFFileReader(new File(this.vcfPath), true);
		this.options = options;

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
			HashMap<Integer, AnnotatingRecord<RecordType>> dbRecords = buildAnnotatingDBRecordsWrapper(genotypeMatches);

			// Use these records to annotate the variant call in obsVC (record-wise but also per alternative allele)
			return annotateWithDBRecords(obsVC, dbRecords);
		}
	}

	/**
	 * Build mapping from alternative allele number to db VCF record to use
	 * 
	 * For SNVs, there should only be one value in the value set at which all alleles point to for most cases. The
	 * variant with the lowermost allele number will be chosen for annotating the reference allele.
	 * 
	 * @param genotypeMatches
	 *            List of {@link GenotypeMatch} objects to build the annotating database records from
	 * @return Resulting map from alternative allele ID (starting with 1) to the database record to use
	 */
	private HashMap<Integer, AnnotatingRecord<RecordType>> buildAnnotatingDBRecordsWrapper(
			List<GenotypeMatch> genotypeMatches) {
		// Collect annotating variants for each allele
		HashMap<Integer, ArrayList<GenotypeMatch>> annotatingRecords = new HashMap<>();
		HashMap<GenotypeMatch, AnnotatingRecord<RecordType>> matchToRecord = new HashMap<>();
		for (GenotypeMatch match : genotypeMatches) {
			final int alleleNo = match.getObservedAllele();
			annotatingRecords.putIfAbsent(alleleNo, new ArrayList<GenotypeMatch>());
			annotatingRecords.get(alleleNo).add(match);
			if (!matchToRecord.containsKey(match))
				matchToRecord.put(match,
						new AnnotatingRecord<RecordType>(vcToRecord.convert(match.getDBVC()), match.getDbAllele()));
		}

		return pickAnnotatingDBRecords(annotatingRecords, matchToRecord);
	}

	/**
	 * Pick annotating DB records
	 * 
	 * @param annotatingRecords
	 *            Map of alternative allele number to genotype match
	 * @param matchToRecord
	 *            Mapping from alternative allel number to record
	 * @return Mapping from alternative allele number to <code>RecordType</code>
	 */
	protected abstract HashMap<Integer, AnnotatingRecord<RecordType>> pickAnnotatingDBRecords(
			HashMap<Integer, ArrayList<GenotypeMatch>> annotatingRecords,
			HashMap<GenotypeMatch, AnnotatingRecord<RecordType>> matchToRecord);

	/**
	 * Annotate the given {@link VariantContext} with the given database records
	 * 
	 * There can be more than one database record, for example in the case that a SNV is squished together with an
	 * indel.
	 * 
	 * @param vc
	 *            The {@link VariantContex} to annotate
	 * @param dbRecords
	 *            Map from alternative allele index to annotating <code>RecordType</code>
	 */
	protected abstract VariantContext annotateWithDBRecords(VariantContext vc,
			HashMap<Integer, AnnotatingRecord<RecordType>> dbRecords);

}