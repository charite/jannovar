package jannovar.pedigree;

import jannovar.annotation.AnnotationListContentDecorator;
import jannovar.annotation.AnnotationList;
import jannovar.annotation.VariantType;
import jannovar.io.ReferenceDictionary;

import java.util.ArrayList;

//TODO(holtgrem): Remove me, after adjusting pedigree module.

/**
 * A class that is used to hold information about the individual variants as parsed from the VCF file.
 *
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public class Variant implements Comparable<Variant> {

	private final ReferenceDictionary refDict;
	/** chromosome; 23=X, 24=Y */
	private int chromosome;
	/** position along chromosome; */
	private int position;
	/** Reference sequence (a single nucleotide for a SNV, more for some other types of variant) */
	private String ref;
	/** Variant sequence (called "ALT", alternate, in VCF files). */
	private String alt;
	/** The INFO column from VCF file */
	private String info;
	/**
	 * The genotype of this variant (note that {@link jannovar.pedigree.GenotypeCall GenotypeCall} objects can hold a
	 * single genotype for single-sample VCF files or multiple genotypes for for VCF files with multiple samples.
	 */
	private GenotypeCall genotype = null;
	/** The Quality score for the Variant call expressed as a Phred Score (QUAL column of the VCF file). */
	private float Phred;

	/**
	 * {@link AnnotationList} object resulting from Jannovar-type annotation of this variant.
	 */
	private AnnotationList annotList = null;

	/**
	 * @param c
	 *            The chromosome (note: X=23, Y=24)
	 * @param p
	 *            Position of the variant
	 * @param r
	 *            Reference nucleotide
	 * @param alternate
	 *            variant (alt) nucleotide
	 * @param gtype
	 *            The Genotype call (single or multiple sample)
	 * @param qual
	 *            The PHRED quality of the variant call.
	 * @param info
	 *            The INFO col from original VCF file
	 */
	public Variant(ReferenceDictionary refDict, int c, int p, String r, String alternate, GenotypeCall gtype,
			float qual, String info) {
		this.refDict = refDict;
		this.chromosome = c;
		this.position = p;
		this.ref = r;
		this.alt = alternate;
		this.genotype = gtype;
		this.Phred = qual;
		this.info = info;
		correctVariant();
	}

	/**
	 * This will correct some variants were parts of the reference and alternative sequence are the same at the start or
	 * end
	 */
	private void correctVariant() {
		int idx = 0;
		// beginning
		while (idx < ref.length() && idx < alt.length() && ref.charAt(idx) == alt.charAt(idx)) {
			idx++;
		}
		position += idx;
		ref = ref.substring(idx);
		alt = alt.substring(idx);

		// end
		int xdi = ref.length();
		int diff = ref.length() - alt.length();
		while (xdi > 0 && xdi - diff > 0 && ref.charAt(xdi - 1) == alt.charAt(xdi - 1 - diff)) {
			xdi--;
		}
		ref = xdi == 0 ? "-" : ref.substring(0, xdi);
		alt = xdi - diff == 0 ? "-" : alt.substring(0, xdi - diff);

	}

	// ########### SETTERS ######################### //
	/**
	 * Initialize the {@link #chromosome} field
	 *
	 * @param chr
	 *            A string representation of the chromosome such as chr3 or chrX
	 */
	public void setChromosome(int chr) {
		this.chromosome = chr;
	}

	/**
	 * Initialize the {@link #position} field
	 *
	 * @param p
	 *            Position of the alternate sequence on the chromosome
	 */
	public void setPosition(int p) {
		this.position = p;
	}

	/**
	 * Initialize the {@link #ref} field
	 *
	 * @param s
	 *            sequence of reference
	 */
	public void setRef(String s) {
		this.ref = s;
	}

	public void setGenotype(GenotypeCall gtype) {
		this.genotype = gtype;
	}

	/**
	 * Initialize the {@link #alt} field
	 *
	 * @param s
	 *            sequence of variant
	 */
	public void setVar(String s) {
		this.alt = s;
	}

	/**
	 * Set the {@link jannovar.annotation.AnnotationList AnnotationList} object for this variant. This method is
	 * intended to provide transcript- level annotation for the variants, for example, to annotate the chromosomal
	 * variant {@code chr7:34889222:T>C} to {@code NPSR1(uc003teh.1:exon10:c.1171T>C:p.*391R) (Type:STOPLOSS)}.
	 *
	 * @param a
	 *            An annotationList object representing annotations of all affected transcripts.
	 */
	public void setAnnotation(AnnotationList a) {
		this.annotList = a;
	}

	/**
	 * Some variants are located in positions where multiple genes overlap.
	 *
	 * @return true if the variant is located within more than one gene
	 */
	public boolean affectsMultipleGenes() {
		return new AnnotationListContentDecorator(annotList).hasMultipleGeneSymbols();
	}

	// ########### GETTERS ######################### //
	/**
	 * @return the 5' position of this variant on its chromosome.
	 */
	public int get_position() {
		return position;
	}

	/**
	 * @return The reference base or bases of this variant
	 */
	public String get_ref() {
		return ref;
	}

	/**
	 * The alternate base or bases of this variant.
	 */
	public String get_alt() {
		return alt;
	}

	/**
	 * The INFO column from the original variant VCF entry.
	 */
	public String get_info() {
		return info;
	}

	/**
	 * Get the genesymbol of the gene associated with this variant, if possible
	 */
	public String getGeneSymbol() {
		if (this.annotList != null) {
			return new AnnotationListContentDecorator(annotList).getGeneSymbol();
		} else {
			return ".";
		}
	}

	/**
	 * @return the NCBI Entrez Gene ID
	 */
	public int getEntrezGeneID() {
		return new AnnotationListContentDecorator(annotList).getGeneID();
	}

	/**
	 * @return true if this variant is a nonsynonymous substitution (missense).
	 */
	public boolean isMissenseVariant() {
		return (new AnnotationListContentDecorator(annotList).getVariantType() == VariantType.MISSENSE);
	}

	/**
	 * Synonymous variant defined as a single nucleotide variant within a coding sequence that does not change the
	 * encoded amino acid.
	 *
	 * @return true if this variant is a {@link VariantType#SYNONYMOUS} variant
	 */
	public boolean isSynonymousVariant() {
		return (new AnnotationListContentDecorator(annotList).getVariantType() == VariantType.SYNONYMOUS);
	}

	/**
	 * A transition is purine <-> purine or pyrimidine <-> pyrimidine. Only applies to single nucleotide subsitutions.
	 *
	 * @return true if the variant is a SNV and a transition.
	 */
	public boolean isTransition() {
		if (!isSingleNucleotideVariant())
			return false;
		/* purine to purine change */
		if (this.ref.equals("A") && this.alt.equals("G"))
			return true;
		else if (this.ref.equals("G") && this.alt.equals("A"))
			return true;
		/* pyrimidine to pyrimidine change */
		if (this.ref.equals("C") && this.alt.equals("T"))
			return true;
		else if (this.ref.equals("T") && this.alt.equals("C"))
			return true;
		/* If we get here, the variant must be a transversion. */
		return false;
	}

	/**
	 * A transversion is purine <-> pyrimidine. Only applies to single nucleotide subsitutions.
	 *
	 * @return true if the variant is a SNV and a transversion.
	 */
	public boolean isTransversion() {
		if (!isSingleNucleotideVariant())
			return false;
		/* purine to purine change */
		if (this.ref.equals("A") && this.alt.equals("G"))
			return false;
		else if (this.ref.equals("G") && this.alt.equals("A"))
			return false;
		/* pyrimidine to pyrimidine change */
		if (this.ref.equals("C") && this.alt.equals("T"))
			return false;
		else if (this.ref.equals("T") && this.alt.equals("C"))
			return false;
		/* If we get here, the variant must be a SNV and a transversion. */
		return true;
	}

	/**
	 * @return true if both the reference and the alternate sequence have a length of one nucleotide.
	 */
	public boolean isSingleNucleotideVariant() {
		return (this.ref.length() == 1 && this.alt.length() == 1);
	}

	/**
	 * @return an integer representation of the chromosome (note: X=23, Y=24, MT=25).
	 */
	public int get_chromosome() {
		return chromosome;
	}

	public String get_chromosome_as_string() {
		return refDict.contigName.get(chromosome);
	}

	/**
	 * @return true if the variant is located on the X chromosome.
	 */
	public boolean is_X_chromosomal() {
		return refDict.contigName.get(chromosome).equals("X");
	}

	/**
	 * @return true if the variant is located on the Y chromosome.
	 */
	public boolean is_Y_chromosomal() {
		return refDict.contigName.get(chromosome).equals("Y");
	}

	/**
	 * @return true if the variant is located on the mitochondrion.
	 */
	public boolean is_mitochondrial() {
		return refDict.contigName.get(chromosome).equals("M");
	}

	/**
	 * This function returns the quality of the first sample in the VCF file.
	 *
	 * @return The PHRED quality of this variant call.
	 */
	public float getVariantPhredScore() {
		return this.Phred;
	}

	/**
	 * This function returns the quality of the first sample in the VCF file.
	 *
	 * @return The PHRED quality of this variant call.
	 */
	public float getVariantGenotypeQualityIndividualN(int n) {
		return this.genotype.getQualityInIndividualN(n);
	}

	/**
	 * @return the Read Depth (DP) of this variant (in first or only individual in VCF file)
	 */
	public int getVariantReadDepth() {
		return this.genotype.getReadDepthInIndividualN(0);
	}

	/**
	 * @return the Read Depth (DP) of this variant (in individual n of VCF file)
	 */
	public int getVariantReadDepthIndividualN(int n) {
		return this.genotype.getReadDepthInIndividualN(n);
	}

	public boolean isHomozygousAlt() {
		return this.genotype.isHomozygousAltInIndividualN(0);
	}

	public boolean isHomozygousAltInIndividualN(int n) {
		return this.genotype.isHomozygousAltInIndividualN(n);
	}

	public boolean isHeterozygous() {
		return this.genotype.isHeterozygousInIndividualN(0);
	}

	public boolean isHeterozygousInIndividualN(int n) {
		return this.genotype.isHeterozygousInIndividualN(n);
	}

	/**
	 * @return true if the variant belongs to a class that is non-exonic and nonsplicing.
	 */
	public boolean isOffExomeTarget() {
		VariantType vt = getVariantTypeConstant();
		switch (vt) {
		case INTRONIC:
		case ncRNA_INTRONIC:
		case UPSTREAM:
		case DOWNSTREAM:
		case INTERGENIC:
			return true;
		default:
			return false;
		}
	}

	public boolean isMissingInIndividualN(int n) {
		return this.genotype.isMissingInIndividualN(n);
	}

	/**
	 * @return the {@link jannovar.pedigree.GenotypeCall GenotypeCall} object corresponding to this variant.
	 */
	public GenotypeCall getGenotype() {
		return this.genotype;
	}

	/**
	 * The Genotype coresponds to one of HOMOZYGOUS_REF (0/0), HOMOZYGOUS_ALT (1/1), HETEROZYGOUS (0/1), NOT_OBSERVED
	 * (./.), ERROR, UNINITIALIZED.
	 *
	 * @return the {@link jannovar.pedigree.Genotype Genotype} object corresponding to this variant.
	 */
	public Genotype getGenotypeInIndividualN(int n) {
		return this.genotype.getGenotypeInIndividualN(n);
	}

	/**
	 * @return A string representing the genotype of this variant.
	 */
	public String getGenotypeAsString() {
		return this.genotype.get_genotype_as_string();
	}

	/**
	 * This function uses the function of the same name of the the {@link jannovar.pedigree.GenotypeCall GenotypeCall}
	 * object corresponding to this variant.
	 *
	 * @return A list of genotype calls, e.g., "0/0","0/1","1/1"
	 */
	public ArrayList<String> getGenotypeList() {
		return this.genotype.getGenotypeList();
	}

	public String get_position_as_string() {
		return Integer.toString(this.position);
	}

	/**
	 * @return variant expressed in chromosomal coordinates
	 */
	public String get_chromosomal_variant() {
		StringBuilder sb = new StringBuilder();
		sb.append(get_chromosome_as_string());
		sb.append(":g.");
		sb.append(this.position + ref + ">" + alt);
		return sb.toString();
	}

	/**
	 * This method combines the fields of the String array s using a colon (":") as a separator, similar to the way that
	 * the Perl function join works. However, this function removes the first element of the array because it contains
	 * the transcript id in our application (e.g., uc003szt.3). For certain types of annotation, however, there is only
	 * one field (intronic annotations are currently handled this way). If so, return just this field.
	 */
	private String combineWithoutID(String[] s) {
		int k = s.length;
		if (k == 0)
			return null;
		if (k == 1)
			return s[0];
		StringBuilder out = new StringBuilder();
		out.append(s[1]);
		for (int x = 2; x < k; ++x)
			out.append(":").append(s[x]);
		return out.toString();
	}

	public String getVariantType() {
		if (this.annotList != null)
			return new AnnotationListContentDecorator(annotList).getVariantType().toString();
		else
			return "?";
	}

	/**
	 * @see jannovar.common.Constants
	 * @return a enum constant representing the variant class (NONSENSE, INTRONIC etc)
	 */
	public VariantType getVariantTypeConstant() {
		if (this.annotList != null)
			return new AnnotationListContentDecorator(annotList).getVariantType();
		else
			return VariantType.ERROR;
	}

	/**
	 * @return A String representing the variant in the chromosomal sequence, e.g., chr17:c.73221527G>A
	 */
	public String getChromosomalVariant() {
		return String.format("%s:c.%d%s>%s", get_chromosome_as_string(), position, ref, alt);
	}

	/**
	 * The variant types (e.e., MISSENSE, NONSENSE) are stored internally as byte values. This function converts these
	 * byte values into strings.
	 *
	 * @return A string representing the type of the current variant.
	 */
	public String get_variant_type_as_string() {
		if (this.annotList == null)
			return "uninitialized";
		else
			return new AnnotationListContentDecorator(annotList).getVariantType().toString();
	}

	/**
	 * Sort based on chromosome and position. If these are equal, sort based on the lexicographic order of the reference
	 * sequence. If this is equal, sort based on the lexicographic order of the alt sequence.
	 */
	@Override
	public int compareTo(Variant other) {
		if (other.chromosome > this.chromosome)
			return -1;
		else if (other.chromosome < this.chromosome)
			return 1;
		else if (other.position > this.position)
			return -1;
		else if (other.position < this.position)
			return 1;
		else if (!other.ref.equals(this.ref))
			return other.ref.compareTo(this.ref);
		else if (!other.alt.equals(this.alt))
			return other.alt.compareTo(this.alt);
		else
			return 0;
	}
}