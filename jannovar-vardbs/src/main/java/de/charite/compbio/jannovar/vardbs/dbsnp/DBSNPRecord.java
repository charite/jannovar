package de.charite.compbio.jannovar.vardbs.dbsnp;

import java.util.Collection;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

/**
 * An entry in dbSNP
 * 
 * Note that as with all databases, the annotation is for actual variants and not just positions.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class DBSNPRecord {

	// Fields up to the INFO column

	/** Name of the chromosome */
	final private String chrom;
	/** Position of the variant, 0-based */
	final private int pos;
	/** ID of the variant */
	final private String id;
	/** Reference sequence */
	final private String ref;
	/** Alternative alleles in cluster */
	final private ImmutableList<String> alt;
	/** Filters, NC: inconsistent genotype submission for at least one sample */
	final private ImmutableList<String> filter;

	// Entries of the INFO column

	/** Numeric RS cluster ID */
	final private int rsID;
	/** Position of the cluster */
	final private int rsPos;
	/** Whether or not the RS orientation is reversed */
	final private boolean reversed;
	/** Representation of the dbSNP bit flags */
	final private DBSNPVariantProperty variantProperty;
	/** Gene information of overlapping genes */
	final private ImmutableList<DBSNPGeneInfo> geneInfos;
	/** ID of first dbSNP build where this variant appears */
	final private int dbSNPBuildID;
	/** Variant origin (germline/somatic) */
	final private DBSNPVariantAlleleOrigin variantAlleleOrigin;
	/** Explanation of possible suspectiveness */
	final private ImmutableSet<DBSNPVariantSuspectReasonCode> variantSuspectReasonCode;
	/** Weight of the variant */
	final private int weights;
	/** Class of the variation */
	final private String variationClass;
	/** Whether the variant is precious (clinical or pubmed cited) */
	final boolean precious;
	/** Has third-party annotation */
	final boolean thirdPartyAnnotation;
	/** Has pub med central citation */
	final boolean pubMedCentral;
	/** Has 3D structure information */
	final boolean threeDStructure;
	/** Has submitter link-out */
	final boolean submitterLinkOut;
	/** Has non-synonymous frameshift effect */
	final boolean nonSynonymousFrameShift;
	/** Has non-synonymous missense effect */
	final boolean nonSynonymousMissense;
	/** Has non-synonymous nonsense effect */
	final boolean nonSynonymousNonsense;
	/** Coding variant with one allele being reference */
	final boolean reference;
	/** Coding variant with synonymous effect */
	final boolean synonymous;
	/** Is in 3' UTR */
	final boolean inThreePrimeUTR;
	/** Is in 5' UTR */
	final boolean inFivePrimeUTR;
	/** Is in splice acceptor site */
	final boolean inAcceptor;
	/** Is in splice donor site */
	final boolean inDonor;
	/** Is in intron */
	final boolean inIntron;
	/** Is in 3' gene region */
	final boolean inThreePrime;
	/** Is in 5' gene region */
	final boolean inFivePrime;
	/** Has other variatn with exactly same set of mapped positions */
	final boolean otherVariant;
	/** Has assembly conflict */
	final boolean assemblyConflict;
	/** Is assembly specific */
	final boolean assemblySpecific;
	/** Is known mutation (journal citation, explicit fact), low-frequency */
	final boolean mutation;
	/** Has been validated */
	final boolean validated;
	/** Has >5% minor allele frequency in each and all populations */
	final boolean fivePercentAll;
	/** Has >5% minor allele frequency in >=1 population */
	final boolean fivePercentOne;
	/** Marker is on high-density genotyping kit */
	final boolean highDensityGenotyping;
	/** Individual genotypes available */
	final boolean genotypesAvailable;
	/** Is in 1000 genomes phase 1 list */
	final boolean g1kPhase1;
	/** Is in 1000 genomes phase 3 list */
	final boolean g1kPhase3;
	/** Is interrogated in clinical diagnostic assay */
	final boolean clinicalDiagnosticAssay;
	/** Comes from locus-specific database */
	final boolean locusSpecificDatabase;
	/** Microattribution/thirdy-aprty annotation */
	final boolean microattributionThirdParty;
	/** Has OMIM/OMIA id */
	final boolean hasOMIMOrOMIA;
	/** Contig allele not present in variant allele list */
	final boolean contigAlelleNotVariant;
	/** Has been withdrawn by submitter */
	final boolean withdrawn;
	/** NonHas non-overlapping allele sets */
	final boolean nonOverlappingAlleleSet;
	/**
	 * Alternative frequencies as seen in 1000 Genomes project, entry with index 0 is the first alternative allele
	 */
	final ImmutableList<Double> alleleFrequenciesG1K;
	/** Is a common SNP (>=1% in at least one 1000 genomes population with at least 2 founders contributing) */
	final boolean common;
	/** List of information on old variants */
	final ImmutableList<String> oldVariants;

	public DBSNPRecord(String chrom, int pos, String id, String ref, Collection<String> alt, Collection<String> filter,
			int rsID, int rsPos, boolean reversed, DBSNPVariantProperty variantProperty,
			Collection<DBSNPGeneInfo> geneInfos, int dbSNPBuildID, DBSNPVariantAlleleOrigin variantAlleleOrigin,
			Collection<DBSNPVariantSuspectReasonCode> variantSuspectReasonCode, int weights, String variationClass,
			boolean precious, boolean thirdPartyAnnotation, boolean pubMedCentral, boolean threeDStructure,
			boolean submitterLinkOut, boolean nonSynonymousFrameShift, boolean nonSynonymousMissense,
			boolean nonSynonymousNonsense, boolean reference, boolean synonymous, boolean inThreePrimeUTR,
			boolean inFivePrimeUTR, boolean inAcceptor, boolean inDonor, boolean inIntron, boolean inThreePrime,
			boolean inFivePrime, boolean otherVariant, boolean assemblyConflict, boolean assemblySpecific,
			boolean mutation, boolean validated, boolean fivePercentAll, boolean fivePersonOne,
			boolean highDensityGenotyping, boolean genotypesAvailable, boolean g1kPhase1, boolean g1kPhase3,
			boolean clinicalDiagnosticAssay, boolean locusSpecificDatabase, boolean microattributionThirdParty,
			boolean hasOMIMOrOMIA, boolean contigAlelleNotVariant, boolean withdrawn, boolean nonOverlappingAlleleSet,
			Collection<Double> alleleFrequenciesG1K, boolean common, Collection<String> oldVariants) {
		this.chrom = chrom;
		this.pos = pos;
		this.id = id;
		this.ref = ref;
		this.alt = ImmutableList.copyOf(alt);
		this.filter = ImmutableList.copyOf(filter);
		this.rsID = rsID;
		this.rsPos = rsPos;
		this.reversed = reversed;
		this.variantProperty = variantProperty;
		this.geneInfos = ImmutableList.copyOf(geneInfos);
		this.dbSNPBuildID = dbSNPBuildID;
		this.variantAlleleOrigin = variantAlleleOrigin;
		this.variantSuspectReasonCode = ImmutableSet.copyOf(variantSuspectReasonCode);
		this.weights = weights;
		this.variationClass = variationClass;
		this.precious = precious;
		this.thirdPartyAnnotation = thirdPartyAnnotation;
		this.pubMedCentral = pubMedCentral;
		this.threeDStructure = threeDStructure;
		this.submitterLinkOut = submitterLinkOut;
		this.nonSynonymousFrameShift = nonSynonymousFrameShift;
		this.nonSynonymousMissense = nonSynonymousMissense;
		this.nonSynonymousNonsense = nonSynonymousNonsense;
		this.reference = reference;
		this.synonymous = synonymous;
		this.inThreePrimeUTR = inThreePrimeUTR;
		this.inFivePrimeUTR = inFivePrimeUTR;
		this.inAcceptor = inAcceptor;
		this.inDonor = inDonor;
		this.inIntron = inIntron;
		this.inThreePrime = inThreePrime;
		this.inFivePrime = inFivePrime;
		this.otherVariant = otherVariant;
		this.assemblyConflict = assemblyConflict;
		this.assemblySpecific = assemblySpecific;
		this.mutation = mutation;
		this.validated = validated;
		this.fivePercentAll = fivePercentAll;
		this.fivePercentOne = fivePersonOne;
		this.highDensityGenotyping = highDensityGenotyping;
		this.genotypesAvailable = genotypesAvailable;
		this.g1kPhase1 = g1kPhase1;
		this.g1kPhase3 = g1kPhase3;
		this.clinicalDiagnosticAssay = clinicalDiagnosticAssay;
		this.locusSpecificDatabase = locusSpecificDatabase;
		this.microattributionThirdParty = microattributionThirdParty;
		this.hasOMIMOrOMIA = hasOMIMOrOMIA;
		this.contigAlelleNotVariant = contigAlelleNotVariant;
		this.withdrawn = withdrawn;
		this.nonOverlappingAlleleSet = nonOverlappingAlleleSet;
		this.alleleFrequenciesG1K = ImmutableList.copyOf(alleleFrequenciesG1K);
		this.common = common;
		this.oldVariants = ImmutableList.copyOf(oldVariants);
	}

	public String getChrom() {
		return chrom;
	}

	public int getPos() {
		return pos;
	}

	public String getId() {
		return id;
	}

	public String getRef() {
		return ref;
	}

	public ImmutableList<String> getAlt() {
		return alt;
	}

	public ImmutableList<String> getFilter() {
		return filter;
	}

	public int getRsID() {
		return rsID;
	}

	public int getRsPos() {
		return rsPos;
	}

	public boolean isReversed() {
		return reversed;
	}

	public DBSNPVariantProperty getVariantProperty() {
		return variantProperty;
	}

	public ImmutableList<DBSNPGeneInfo> getGeneInfos() {
		return geneInfos;
	}

	public int getDbSNPBuildID() {
		return dbSNPBuildID;
	}

	public DBSNPVariantAlleleOrigin getVariantAlleleOrigin() {
		return variantAlleleOrigin;
	}

	public ImmutableSet<DBSNPVariantSuspectReasonCode> getVariantSuspectReasonCode() {
		return variantSuspectReasonCode;
	}

	public int getWeights() {
		return weights;
	}

	public String getVariationClass() {
		return variationClass;
	}

	public boolean isPrecious() {
		return precious;
	}

	public boolean isThirdPartyAnnotation() {
		return thirdPartyAnnotation;
	}

	public boolean isPubMedCentral() {
		return pubMedCentral;
	}

	public boolean isThreeDStructure() {
		return threeDStructure;
	}

	public boolean isSubmitterLinkOut() {
		return submitterLinkOut;
	}

	public boolean isNonSynonymousFrameShift() {
		return nonSynonymousFrameShift;
	}

	public boolean isNonSynonymousMissense() {
		return nonSynonymousMissense;
	}

	public boolean isNonSynonymousNonsense() {
		return nonSynonymousNonsense;
	}

	public boolean isReference() {
		return reference;
	}

	public boolean isSynonymous() {
		return synonymous;
	}

	public boolean isInThreePrimeUTR() {
		return inThreePrimeUTR;
	}

	public boolean isInFivePrimeUTR() {
		return inFivePrimeUTR;
	}

	public boolean isInAcceptor() {
		return inAcceptor;
	}

	public boolean isInDonor() {
		return inDonor;
	}

	public boolean isInIntron() {
		return inIntron;
	}

	public boolean isInThreePrime() {
		return inThreePrime;
	}

	public boolean isInFivePrime() {
		return inFivePrime;
	}

	public boolean isOtherVariant() {
		return otherVariant;
	}

	public boolean isAssemblyConflict() {
		return assemblyConflict;
	}

	public boolean isAssemblySpecific() {
		return assemblySpecific;
	}

	public boolean isMutation() {
		return mutation;
	}

	public boolean isValidated() {
		return validated;
	}

	public boolean isFivePercentAll() {
		return fivePercentAll;
	}

	public boolean isFivePercentOne() {
		return fivePercentOne;
	}

	public boolean isHighDensityGenotyping() {
		return highDensityGenotyping;
	}

	public boolean isGenotypesAvailable() {
		return genotypesAvailable;
	}

	public boolean isG1kPhase1() {
		return g1kPhase1;
	}

	public boolean isG1kPhase3() {
		return g1kPhase3;
	}

	public boolean isClinicalDiagnosticAssay() {
		return clinicalDiagnosticAssay;
	}

	public boolean isLocusSpecificDatabase() {
		return locusSpecificDatabase;
	}

	public boolean isMicroattributionThirdParty() {
		return microattributionThirdParty;
	}

	public boolean isHasOMIMOrOMIA() {
		return hasOMIMOrOMIA;
	}

	public boolean isContigAlelleNotVariant() {
		return contigAlelleNotVariant;
	}

	public boolean isWithdrawn() {
		return withdrawn;
	}

	public boolean isNonOverlappingAlleleSet() {
		return nonOverlappingAlleleSet;
	}

	public ImmutableList<Double> getAlleleFrequenciesG1K() {
		return alleleFrequenciesG1K;
	}

	public boolean isCommon() {
		return common;
	}

	public ImmutableList<String> getOldVariants() {
		return oldVariants;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alleleFrequenciesG1K == null) ? 0 : alleleFrequenciesG1K.hashCode());
		result = prime * result + ((alt == null) ? 0 : alt.hashCode());
		result = prime * result + (assemblyConflict ? 1231 : 1237);
		result = prime * result + (assemblySpecific ? 1231 : 1237);
		result = prime * result + ((chrom == null) ? 0 : chrom.hashCode());
		result = prime * result + (clinicalDiagnosticAssay ? 1231 : 1237);
		result = prime * result + (common ? 1231 : 1237);
		result = prime * result + (contigAlelleNotVariant ? 1231 : 1237);
		result = prime * result + dbSNPBuildID;
		result = prime * result + ((filter == null) ? 0 : filter.hashCode());
		result = prime * result + (fivePercentAll ? 1231 : 1237);
		result = prime * result + (fivePercentOne ? 1231 : 1237);
		result = prime * result + (g1kPhase1 ? 1231 : 1237);
		result = prime * result + (g1kPhase3 ? 1231 : 1237);
		result = prime * result + ((geneInfos == null) ? 0 : geneInfos.hashCode());
		result = prime * result + (genotypesAvailable ? 1231 : 1237);
		result = prime * result + (hasOMIMOrOMIA ? 1231 : 1237);
		result = prime * result + (highDensityGenotyping ? 1231 : 1237);
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + (inAcceptor ? 1231 : 1237);
		result = prime * result + (inDonor ? 1231 : 1237);
		result = prime * result + (inFivePrime ? 1231 : 1237);
		result = prime * result + (inFivePrimeUTR ? 1231 : 1237);
		result = prime * result + (inIntron ? 1231 : 1237);
		result = prime * result + (inThreePrime ? 1231 : 1237);
		result = prime * result + (inThreePrimeUTR ? 1231 : 1237);
		result = prime * result + (locusSpecificDatabase ? 1231 : 1237);
		result = prime * result + (microattributionThirdParty ? 1231 : 1237);
		result = prime * result + (mutation ? 1231 : 1237);
		result = prime * result + (nonOverlappingAlleleSet ? 1231 : 1237);
		result = prime * result + (nonSynonymousFrameShift ? 1231 : 1237);
		result = prime * result + (nonSynonymousMissense ? 1231 : 1237);
		result = prime * result + (nonSynonymousNonsense ? 1231 : 1237);
		result = prime * result + ((oldVariants == null) ? 0 : oldVariants.hashCode());
		result = prime * result + (otherVariant ? 1231 : 1237);
		result = prime * result + pos;
		result = prime * result + (precious ? 1231 : 1237);
		result = prime * result + (pubMedCentral ? 1231 : 1237);
		result = prime * result + ((ref == null) ? 0 : ref.hashCode());
		result = prime * result + (reference ? 1231 : 1237);
		result = prime * result + (reversed ? 1231 : 1237);
		result = prime * result + rsID;
		result = prime * result + rsPos;
		result = prime * result + (submitterLinkOut ? 1231 : 1237);
		result = prime * result + (synonymous ? 1231 : 1237);
		result = prime * result + (thirdPartyAnnotation ? 1231 : 1237);
		result = prime * result + (threeDStructure ? 1231 : 1237);
		result = prime * result + (validated ? 1231 : 1237);
		result = prime * result + ((variantAlleleOrigin == null) ? 0 : variantAlleleOrigin.hashCode());
		result = prime * result + ((variantProperty == null) ? 0 : variantProperty.hashCode());
		result = prime * result + ((variantSuspectReasonCode == null) ? 0 : variantSuspectReasonCode.hashCode());
		result = prime * result + ((variationClass == null) ? 0 : variationClass.hashCode());
		result = prime * result + weights;
		result = prime * result + (withdrawn ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBSNPRecord other = (DBSNPRecord) obj;
		if (alleleFrequenciesG1K == null) {
			if (other.alleleFrequenciesG1K != null)
				return false;
		} else if (!alleleFrequenciesG1K.equals(other.alleleFrequenciesG1K))
			return false;
		if (alt == null) {
			if (other.alt != null)
				return false;
		} else if (!alt.equals(other.alt))
			return false;
		if (assemblyConflict != other.assemblyConflict)
			return false;
		if (assemblySpecific != other.assemblySpecific)
			return false;
		if (chrom == null) {
			if (other.chrom != null)
				return false;
		} else if (!chrom.equals(other.chrom))
			return false;
		if (clinicalDiagnosticAssay != other.clinicalDiagnosticAssay)
			return false;
		if (common != other.common)
			return false;
		if (contigAlelleNotVariant != other.contigAlelleNotVariant)
			return false;
		if (dbSNPBuildID != other.dbSNPBuildID)
			return false;
		if (filter == null) {
			if (other.filter != null)
				return false;
		} else if (!filter.equals(other.filter))
			return false;
		if (fivePercentAll != other.fivePercentAll)
			return false;
		if (fivePercentOne != other.fivePercentOne)
			return false;
		if (g1kPhase1 != other.g1kPhase1)
			return false;
		if (g1kPhase3 != other.g1kPhase3)
			return false;
		if (geneInfos == null) {
			if (other.geneInfos != null)
				return false;
		} else if (!geneInfos.equals(other.geneInfos))
			return false;
		if (genotypesAvailable != other.genotypesAvailable)
			return false;
		if (hasOMIMOrOMIA != other.hasOMIMOrOMIA)
			return false;
		if (highDensityGenotyping != other.highDensityGenotyping)
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (inAcceptor != other.inAcceptor)
			return false;
		if (inDonor != other.inDonor)
			return false;
		if (inFivePrime != other.inFivePrime)
			return false;
		if (inFivePrimeUTR != other.inFivePrimeUTR)
			return false;
		if (inIntron != other.inIntron)
			return false;
		if (inThreePrime != other.inThreePrime)
			return false;
		if (inThreePrimeUTR != other.inThreePrimeUTR)
			return false;
		if (locusSpecificDatabase != other.locusSpecificDatabase)
			return false;
		if (microattributionThirdParty != other.microattributionThirdParty)
			return false;
		if (mutation != other.mutation)
			return false;
		if (nonOverlappingAlleleSet != other.nonOverlappingAlleleSet)
			return false;
		if (nonSynonymousFrameShift != other.nonSynonymousFrameShift)
			return false;
		if (nonSynonymousMissense != other.nonSynonymousMissense)
			return false;
		if (nonSynonymousNonsense != other.nonSynonymousNonsense)
			return false;
		if (oldVariants == null) {
			if (other.oldVariants != null)
				return false;
		} else if (!oldVariants.equals(other.oldVariants))
			return false;
		if (otherVariant != other.otherVariant)
			return false;
		if (pos != other.pos)
			return false;
		if (precious != other.precious)
			return false;
		if (pubMedCentral != other.pubMedCentral)
			return false;
		if (ref == null) {
			if (other.ref != null)
				return false;
		} else if (!ref.equals(other.ref))
			return false;
		if (reference != other.reference)
			return false;
		if (reversed != other.reversed)
			return false;
		if (rsID != other.rsID)
			return false;
		if (rsPos != other.rsPos)
			return false;
		if (submitterLinkOut != other.submitterLinkOut)
			return false;
		if (synonymous != other.synonymous)
			return false;
		if (thirdPartyAnnotation != other.thirdPartyAnnotation)
			return false;
		if (threeDStructure != other.threeDStructure)
			return false;
		if (validated != other.validated)
			return false;
		if (variantAlleleOrigin != other.variantAlleleOrigin)
			return false;
		if (variantProperty == null) {
			if (other.variantProperty != null)
				return false;
		} else if (!variantProperty.equals(other.variantProperty))
			return false;
		if (variantSuspectReasonCode == null) {
			if (other.variantSuspectReasonCode != null)
				return false;
		} else if (!variantSuspectReasonCode.equals(other.variantSuspectReasonCode))
			return false;
		if (variationClass == null) {
			if (other.variationClass != null)
				return false;
		} else if (!variationClass.equals(other.variationClass))
			return false;
		if (weights != other.weights)
			return false;
		if (withdrawn != other.withdrawn)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DBSNPRecord [chrom=" + chrom + ", pos=" + pos + ", id=" + id + ", ref=" + ref + ", alt=" + alt
				+ ", filter=" + filter + ", rsID=" + rsID + ", rsPos=" + rsPos + ", reversed=" + reversed
				+ ", variantProperty=" + variantProperty + ", geneInfos=" + geneInfos + ", dbSNPBuildID=" + dbSNPBuildID
				+ ", variantAlleleOrigin=" + variantAlleleOrigin + ", variantSuspectReasonCode="
				+ variantSuspectReasonCode + ", weights=" + weights + ", variationClass=" + variationClass
				+ ", precious=" + precious + ", thirdPartyAnnotation=" + thirdPartyAnnotation + ", pubMedCentral="
				+ pubMedCentral + ", threeDStructure=" + threeDStructure + ", submitterLinkOut=" + submitterLinkOut
				+ ", nonSynonymousFrameShift=" + nonSynonymousFrameShift + ", nonSynonymousMissense="
				+ nonSynonymousMissense + ", nonSynonymousNonsense=" + nonSynonymousNonsense + ", reference="
				+ reference + ", synonymous=" + synonymous + ", inThreePrimeUTR=" + inThreePrimeUTR
				+ ", inFivePrimeUTR=" + inFivePrimeUTR + ", inAcceptor=" + inAcceptor + ", inDonor=" + inDonor
				+ ", inIntron=" + inIntron + ", inThreePrime=" + inThreePrime + ", inFivePrime=" + inFivePrime
				+ ", otherVariant=" + otherVariant + ", assemblyConflict=" + assemblyConflict + ", assemblySpecific="
				+ assemblySpecific + ", mutation=" + mutation + ", validated=" + validated + ", fivePercentAll="
				+ fivePercentAll + ", fivePersonOne=" + fivePercentOne + ", highDensityGenotyping="
				+ highDensityGenotyping + ", genotypesAvailable=" + genotypesAvailable + ", g1kPhase1=" + g1kPhase1
				+ ", g1kPhase3=" + g1kPhase3 + ", clinicalDiagnosticAssay=" + clinicalDiagnosticAssay
				+ ", locusSpecificDatabase=" + locusSpecificDatabase + ", microattributionThirdParty="
				+ microattributionThirdParty + ", hasOMIMOrOMIA=" + hasOMIMOrOMIA + ", contigAlelleNotVariant="
				+ contigAlelleNotVariant + ", withdrawn=" + withdrawn + ", nonOverlappingAlleleSet="
				+ nonOverlappingAlleleSet + ", alleleFrequenciesG1K=" + alleleFrequenciesG1K + ", common=" + common
				+ ", oldVariants=" + oldVariants + "]";
	}

}
