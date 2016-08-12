package de.charite.compbio.jannovar.vardbs.base;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

/**
 * Abstract base class for annotation based on VCF files.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
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
	public VariantContext annotateVariantContext(VariantContext obsVC) {
		try (CloseableIterator<VariantContext> iter = vcfReader.query(obsVC.getContig(), obsVC.getStart(),
				obsVC.getEnd())) {
			// Fetch all overlapping and matching genotypes from database and pair them with the correct allele from vc.
			List<GenotypeMatch> genotypeMatches = new ArrayList<>();
			List<GenotypeMatch> positionOverlaps = new ArrayList<>();
			while (iter.hasNext()) {
				final VariantContext dbVC = iter.next();
				genotypeMatches.addAll(matcher.matchGenotypes(obsVC, dbVC));
				if (options.isReportOverlapping() || options.isReportOverlappingAsMatching())
					positionOverlaps.addAll(matcher.positionOverlaps(obsVC, dbVC));
			}

			// Pick best dbSNP record for each alternative allele
			HashMap<Integer, AnnotatingRecord<RecordType>> dbRecordsMatch = buildAnnotatingDBRecordsWrapper(
					genotypeMatches);
			HashMap<Integer, AnnotatingRecord<RecordType>> dbRecordsOverlap = buildAnnotatingDBRecordsWrapper(
					positionOverlaps);
			HashMap<Integer, AnnotatingRecord<RecordType>> emptyMap = new HashMap<Integer, AnnotatingRecord<RecordType>>();

			// Use these records to annotate the variant call in obsVC (record-wise but also per alternative allele)
			if (options.isReportOverlappingAsMatching())
				return annotateWithDBRecords(obsVC, dbRecordsOverlap, emptyMap);
			else if (options.isReportOverlapping())
				return annotateWithDBRecords(obsVC, dbRecordsMatch, dbRecordsOverlap);
			else
				return annotateWithDBRecords(obsVC, dbRecordsMatch, emptyMap);
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
	 *            The {@link VariantContext} to annotate
	 * @param dbRecordMatches
	 *            Map from alternative allele index to annotating <code>RecordType</code> with matching allele
	 * @param dbRecordOverlaps
	 *            Map from alternative allele index to annotating <code>RecordType</code> with overlapping positions
	 */
	protected abstract VariantContext annotateWithDBRecords(VariantContext vc,
			HashMap<Integer, AnnotatingRecord<RecordType>> dbRecordMatches,
			HashMap<Integer, AnnotatingRecord<RecordType>> dbRecordOverlaps);

}