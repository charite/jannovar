package de.charite.compbio.jannovar.vardbs.dbsnp;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Builder for {@link DBSNPRecord} class
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class DBSNPRecordBuilder {

	/** Name of the chromosome */
	private String chrom;
	/** Position of the variant, 0-based */
	private int pos;
	/** ID of the variant */
	private String id;
	/** Reference sequence */
	private String ref;
	/** Alternative alleles in cluster */
	private ArrayList<String> alt;
	/** Filters, NC: inconsistent genotype submission for at least one sample */
	private ArrayList<String> filter;

	// Entries of the INFO column

	/** Numeric RS cluster ID */
	private int rsID;
	/** Position of the cluster */
	private int rsPos;
	/** Whether or not the RS orientation is reversed */
	private boolean reversed;
	/** Representation of the dbSNP bit flags */
	private DBSNPVariantProperty variantProperty;
	/** Gene information of overlapping genes */
	private ArrayList<DBSNPGeneInfo> geneInfos;
	/** ID of first dbSNP build where this variant appears */
	private int dbSNPBuildID;
	/** Variant origin (germline/somatic) */
	private DBSNPVariantAlleleOrigin variantAlleleOrigin;
	/** Explanation of possible suspectiveness */
	private HashSet<DBSNPVariantSuspectReasonCode> variantSuspectReasonCode;
	/** Weight of the variant */
	private int weight;
	/** Class of the variation */
	private String variationClass;
	/** Whether the variant is precious (clinical or pubmed cited) */
	private boolean precious;
	/** Has third-party annotation */
	private boolean thirdPartyAnnotation;
	/** Has pub med central citation */
	private boolean pubMedCentral;
	/** Has 3D structure information */
	private boolean threeDStructure;
	/** Has submitter link-out */
	private boolean submitterLinkOut;
	/** Has non-synonymous frameshift effect */
	private boolean nonSynonymousFrameShift;
	/** Has non-synonymous missense effect */
	private boolean nonSynonymousMissense;
	/** Has non-synonymous nonsense effect */
	private boolean nonSynonymousNonsense;
	/** Coding variant with one allele being reference */
	private boolean reference;
	/** Coding variant with synonymous effect */
	private boolean synonymous;
	/** Is in 3' UTR */
	private boolean inThreePrimeUTR;
	/** Is in 5' UTR */
	private boolean inFivePrimeUTR;
	/** Is in splice acceptor site */
	private boolean inAcceptor;
	/** Is in splice donor site */
	private boolean inDonor;
	/** Is in intron */
	private boolean inIntron;
	/** Is in 3' gene region */
	private boolean inThreePrime;
	/** Is in 5' gene region */
	private boolean inFivePrime;
	/** Has other variatn with exactly same set of mapped positions */
	private boolean otherVariant;
	/** Has assembly conflict */
	private boolean assemblyConflict;
	/** Is assembly specific */
	private boolean assemblySpecific;
	/** Is known mutation (journal citation, explicit fact), low-frequency */
	private boolean mutation;
	/** Has been validated */
	private boolean validated;
	/** Has >5% minor allele frequency in each and all populations */
	private boolean fivePercentAll;
	/** Has >5% minor allele frequency in >=1 population */
	private boolean fivePercentOne;
	/** Marker is on high-density genotyping kit */
	private boolean highDensityGenotyping;
	/** Individual genotypes available */
	private boolean genotypesAvailable;
	/** Is in 1000 genomes phase 1 list */
	private boolean g1kPhase1;
	/** Is in 1000 genomes phase 3 list */
	private boolean g1kPhase3;
	/** Is interrogated in clinical diagnostic assay */
	private boolean clinicalDiagnosticAssay;
	/** Comes from locus-specific database */
	private boolean locusSpecificDatabase;
	/** Microattribution/thirdy-aprty annotation */
	private boolean microattributionThirdParty;
	/** Has OMIM/OMIA id */
	private boolean hasOMIMOrOMIA;
	/** Contig allele not present in variant allele list */
	private boolean contigAlelleNotVariant;
	/** Has been withdrawn by submitter */
	private boolean withdrawn;
	/** NonHas non-overlapping allele sets */
	private boolean nonOverlappingAlleleSet;
	/**
	 * Allele frequencies as seen in 1000 Genomes project, entry with index 0 is from reference, the others are (in the
	 * same order) the frequencies for the alternative alleles.
	 */
	private ArrayList<Double> alleleFrequenciesG1K;
	/** Is a common SNP (>=1% in at least one 1000 genomes population with at least 2 founders contributing */
	boolean common;
	/** List of information on old variants */
	private ArrayList<String> oldVariants;

	DBSNPRecordBuilder() {
		chrom = null;
		pos = -1;
		id = null;
		ref = null;
		alt = new ArrayList<>();
		filter = new ArrayList<>();

		rsID = -1;
		rsPos = -1;
		reversed = false;
		variantProperty = null;
		geneInfos = new ArrayList<>();
		dbSNPBuildID = -1;
		variantAlleleOrigin = DBSNPVariantAlleleOrigin.UNSPECIFIED;
		variantSuspectReasonCode = new HashSet<>();
		weight = -1;
		variationClass = null;
		precious = false;
		thirdPartyAnnotation = false;
		pubMedCentral = false;
		threeDStructure = false;
		submitterLinkOut = false;
		nonSynonymousFrameShift = false;
		nonSynonymousMissense = false;
		nonSynonymousNonsense = false;
		reference = false;
		synonymous = false;
		inThreePrimeUTR = false;
		inFivePrimeUTR = false;
		inAcceptor = false;
		inDonor = false;
		inIntron = false;
		inThreePrime = false;
		inFivePrime = false;
		otherVariant = false;
		assemblyConflict = false;
		assemblySpecific = false;
		mutation = false;
		validated = false;
		fivePercentAll = false;
		fivePercentOne = false;
		highDensityGenotyping = false;
		genotypesAvailable = false;
		g1kPhase1 = false;
		g1kPhase3 = false;
		clinicalDiagnosticAssay = false;
		locusSpecificDatabase = false;
		microattributionThirdParty = false;
		hasOMIMOrOMIA = false;
		contigAlelleNotVariant = false;
		withdrawn = false;
		nonOverlappingAlleleSet = false;
		alleleFrequenciesG1K = new ArrayList<>();
		common = false;
		oldVariants = new ArrayList<>();
	}

	public DBSNPRecord build() {
		return new DBSNPRecord(chrom, pos, id, ref, alt, filter, rsID, rsPos, reversed, variantProperty, geneInfos,
				dbSNPBuildID, variantAlleleOrigin, variantSuspectReasonCode, weight, variationClass, precious,
				thirdPartyAnnotation, pubMedCentral, threeDStructure, submitterLinkOut, nonSynonymousFrameShift,
				nonSynonymousMissense, nonSynonymousNonsense, reference, synonymous, inThreePrimeUTR, inFivePrimeUTR,
				inAcceptor, inDonor, inIntron, inThreePrime, inFivePrime, otherVariant, assemblyConflict,
				assemblySpecific, mutation, validated, fivePercentAll, fivePercentOne, highDensityGenotyping,
				genotypesAvailable, g1kPhase1, g1kPhase3, clinicalDiagnosticAssay, locusSpecificDatabase,
				microattributionThirdParty, hasOMIMOrOMIA, contigAlelleNotVariant, withdrawn, nonOverlappingAlleleSet,
				alleleFrequenciesG1K, common, oldVariants);
	}

	public String getChrom() {
		return chrom;
	}

	public void setContig(String chrom) {
		this.chrom = chrom;
	}

	public int getPos() {
		return pos;
	}

	public void setPos(int pos) {
		this.pos = pos;
	}

	public String getId() {
		return id;
	}

	public void setID(String id) {
		this.id = id;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public ArrayList<String> getAlt() {
		return alt;
	}

	public void setAlt(ArrayList<String> alt) {
		this.alt = alt;
	}

	public ArrayList<String> getFilter() {
		return filter;
	}

	public void setFilter(ArrayList<String> filter) {
		this.filter = filter;
	}

	public int getRSID() {
		return rsID;
	}

	public void setRSID(int rsID) {
		this.rsID = rsID;
	}

	public int getRSPos() {
		return rsPos;
	}

	public void setRSPos(int rsPos) {
		this.rsPos = rsPos;
	}

	public boolean isReversed() {
		return reversed;
	}

	public void setReversed(boolean reversed) {
		this.reversed = reversed;
	}

	public DBSNPVariantProperty getVariantProperty() {
		return variantProperty;
	}

	public void setVariantProperty(DBSNPVariantProperty variantProperty) {
		this.variantProperty = variantProperty;
	}

	public ArrayList<DBSNPGeneInfo> getGeneInfos() {
		return geneInfos;
	}

	public void setGeneInfos(ArrayList<DBSNPGeneInfo> geneInfos) {
		this.geneInfos = geneInfos;
	}

	public int getDbSNPBuildID() {
		return dbSNPBuildID;
	}

	public void setDbSNPBuildID(int dbSNPBuildID) {
		this.dbSNPBuildID = dbSNPBuildID;
	}

	public DBSNPVariantAlleleOrigin getVariantAlleleOrigin() {
		return variantAlleleOrigin;
	}

	public void setVariantAlleleOrigin(DBSNPVariantAlleleOrigin variantAlleleOrigin) {
		this.variantAlleleOrigin = variantAlleleOrigin;
	}

	public HashSet<DBSNPVariantSuspectReasonCode> getVariantSuspectReasonCode() {
		return variantSuspectReasonCode;
	}

	public void setVariantSuspectReasonCode(HashSet<DBSNPVariantSuspectReasonCode> variantSuspectReasonCode) {
		this.variantSuspectReasonCode = variantSuspectReasonCode;
	}

	public int getWeights() {
		return weight;
	}

	public void setWeights(int weights) {
		this.weight = weights;
	}

	public String getVariationClass() {
		return variationClass;
	}

	public void setVariationClass(String variationClass) {
		this.variationClass = variationClass;
	}

	public boolean isPrecious() {
		return precious;
	}

	public void setPrecious(boolean precious) {
		this.precious = precious;
	}

	public boolean isThirdPartyAnnotation() {
		return thirdPartyAnnotation;
	}

	public void setThirdPartyAnnotation(boolean thirdPartyAnnotation) {
		this.thirdPartyAnnotation = thirdPartyAnnotation;
	}

	public boolean isPubMedCentral() {
		return pubMedCentral;
	}

	public void setPubMedCentral(boolean pubMedCentral) {
		this.pubMedCentral = pubMedCentral;
	}

	public boolean isThreeDStructure() {
		return threeDStructure;
	}

	public void setThreeDStructure(boolean threeDStructure) {
		this.threeDStructure = threeDStructure;
	}

	public boolean isSubmitterLinkOut() {
		return submitterLinkOut;
	}

	public void setSubmitterLinkOut(boolean submitterLinkOut) {
		this.submitterLinkOut = submitterLinkOut;
	}

	public boolean isNonSynonymousFrameShift() {
		return nonSynonymousFrameShift;
	}

	public void setNonSynonymousFrameShift(boolean nonSynonymousFrameShift) {
		this.nonSynonymousFrameShift = nonSynonymousFrameShift;
	}

	public boolean isNonSynonymousMissense() {
		return nonSynonymousMissense;
	}

	public void setNonSynonymousMissense(boolean nonSynonymousMissense) {
		this.nonSynonymousMissense = nonSynonymousMissense;
	}

	public boolean isNonSynonymousNonsense() {
		return nonSynonymousNonsense;
	}

	public void setNonSynonymousNonsense(boolean nonSynonymousNonsense) {
		this.nonSynonymousNonsense = nonSynonymousNonsense;
	}

	public boolean isReference() {
		return reference;
	}

	public void setReference(boolean reference) {
		this.reference = reference;
	}

	public boolean isSynonymous() {
		return synonymous;
	}

	public void setSynonymous(boolean synonymous) {
		this.synonymous = synonymous;
	}

	public boolean isInThreePrimeUTR() {
		return inThreePrimeUTR;
	}

	public void setInThreePrimeUTR(boolean inThreePrimeUTR) {
		this.inThreePrimeUTR = inThreePrimeUTR;
	}

	public boolean isInFivePrimeUTR() {
		return inFivePrimeUTR;
	}

	public void setInFivePrimeUTR(boolean inFivePrimeUTR) {
		this.inFivePrimeUTR = inFivePrimeUTR;
	}

	public boolean isInAcceptor() {
		return inAcceptor;
	}

	public void setInAcceptor(boolean inAcceptor) {
		this.inAcceptor = inAcceptor;
	}

	public boolean isInDonor() {
		return inDonor;
	}

	public void setInDonor(boolean inDonor) {
		this.inDonor = inDonor;
	}

	public boolean isInIntron() {
		return inIntron;
	}

	public void setInIntron(boolean inIntron) {
		this.inIntron = inIntron;
	}

	public boolean isInThreePrime() {
		return inThreePrime;
	}

	public void setInThreePrime(boolean inThreePrime) {
		this.inThreePrime = inThreePrime;
	}

	public boolean isInFivePrime() {
		return inFivePrime;
	}

	public void setInFivePrime(boolean inFivePrime) {
		this.inFivePrime = inFivePrime;
	}

	public boolean isOtherVariant() {
		return otherVariant;
	}

	public void setOtherVariant(boolean otherVariant) {
		this.otherVariant = otherVariant;
	}

	public boolean isAssemblyConflict() {
		return assemblyConflict;
	}

	public void setAssemblyConflict(boolean assemblyConflict) {
		this.assemblyConflict = assemblyConflict;
	}

	public boolean isAssemblySpecific() {
		return assemblySpecific;
	}

	public void setAssemblySpecific(boolean assemblySpecific) {
		this.assemblySpecific = assemblySpecific;
	}

	public boolean isMutation() {
		return mutation;
	}

	public void setMutation(boolean mutation) {
		this.mutation = mutation;
	}

	public boolean isValidated() {
		return validated;
	}

	public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public boolean isFivePercentAll() {
		return fivePercentAll;
	}

	public void setFivePercentAll(boolean fivePercentAll) {
		this.fivePercentAll = fivePercentAll;
	}

	public boolean isFivePercentOne() {
		return fivePercentOne;
	}

	public void setFivePercentOne(boolean fivePercentOne) {
		this.fivePercentOne = fivePercentOne;
	}

	public boolean isHighDensityGenotyping() {
		return highDensityGenotyping;
	}

	public void setHighDensityGenotyping(boolean highDensityGenotyping) {
		this.highDensityGenotyping = highDensityGenotyping;
	}

	public boolean isGenotypesAvailable() {
		return genotypesAvailable;
	}

	public void setGenotypesAvailable(boolean genotypesAvailable) {
		this.genotypesAvailable = genotypesAvailable;
	}

	public boolean isG1kPhase1() {
		return g1kPhase1;
	}

	public void setG1kPhase1(boolean g1kPhase1) {
		this.g1kPhase1 = g1kPhase1;
	}

	public boolean isG1kPhase3() {
		return g1kPhase3;
	}

	public void setG1kPhase3(boolean g1kPhase3) {
		this.g1kPhase3 = g1kPhase3;
	}

	public boolean isClinicalDiagnosticAssay() {
		return clinicalDiagnosticAssay;
	}

	public void setClinicalDiagnosticAssay(boolean clinicalDiagnosticAssay) {
		this.clinicalDiagnosticAssay = clinicalDiagnosticAssay;
	}

	public boolean isLocusSpecificDatabase() {
		return locusSpecificDatabase;
	}

	public void setLocusSpecificDatabase(boolean locusSpecificDatabase) {
		this.locusSpecificDatabase = locusSpecificDatabase;
	}

	public boolean isMicroattributionThirdParty() {
		return microattributionThirdParty;
	}

	public void setMicroattributionThirdParty(boolean microattributionThirdParty) {
		this.microattributionThirdParty = microattributionThirdParty;
	}

	public boolean isHasOMIMOrOMIA() {
		return hasOMIMOrOMIA;
	}

	public void setHasOMIMOrOMIA(boolean hasOMIMOrOMIA) {
		this.hasOMIMOrOMIA = hasOMIMOrOMIA;
	}

	public boolean isContigAlelleNotVariant() {
		return contigAlelleNotVariant;
	}

	public void setContigAlelleNotVariant(boolean contigAlelleNotVariant) {
		this.contigAlelleNotVariant = contigAlelleNotVariant;
	}

	public boolean isWithdrawn() {
		return withdrawn;
	}

	public void setWithdrawn(boolean withdrawn) {
		this.withdrawn = withdrawn;
	}

	public boolean isNonOverlappingAlleleSet() {
		return nonOverlappingAlleleSet;
	}

	public void setNonOverlappingAlleleSet(boolean nonOverlappingAlleleSet) {
		this.nonOverlappingAlleleSet = nonOverlappingAlleleSet;
	}

	public ArrayList<Double> getAlleleFrequenciesG1K() {
		return alleleFrequenciesG1K;
	}

	public void setAlleleFrequenciesG1K(ArrayList<Double> alleleFrequenciesG1K) {
		this.alleleFrequenciesG1K = alleleFrequenciesG1K;
	}

	public boolean isCommon() {
		return common;
	}

	public void setCommon(boolean common) {
		this.common = common;
	}

	public ArrayList<String> getOldVariants() {
		return oldVariants;
	}

	public void setOldVariants(ArrayList<String> oldVariants) {
		this.oldVariants = oldVariants;
	}

}
