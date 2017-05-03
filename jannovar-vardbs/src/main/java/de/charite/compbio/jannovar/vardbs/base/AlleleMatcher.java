package de.charite.compbio.jannovar.vardbs.base;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Find matches between two allels (an observed and a database variant)
 * 
 * This class is an implementation detail and not part of the public interface.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class AlleleMatcher {

	/** Helper to use for indel normalization */
	private final VariantNormalizer normalizer;

	/**
	 * Construct GenotypeMatcher
	 * 
	 * @param pathFasta
	 *            Path to FAI-indexed FASTA file
	 * @throws JannovarVarDBException
	 *             On problems with loading the FASTA/FAI file
	 */
	public AlleleMatcher(String pathFasta) throws JannovarVarDBException {
		this.normalizer = new VariantNormalizer(pathFasta);
	}

	/**
	 * Match genotypes of two {@link VariantContext}s (chrom, position, ref, and alt have to be equal)
	 * 
	 * Indels will be left-shifted and normalized when necessary
	 * 
	 * @param obsVC
	 *            {@link VariantContext} describing the observed variant
	 * @param dbVC
	 *            {@link VariantContext} describing the database variant
	 * @return {@link Collection} of {@link GenotypeMatch}es for the two variants
	 */
	public Collection<GenotypeMatch> matchGenotypes(VariantContext obsVC, VariantContext dbVC) {
		List<GenotypeMatch> result = new ArrayList<>();

		// Get normalized description of all alternative observed and database alleles
		Collection<VariantDescription> obsVars = ctxToVariants(obsVC);
		Collection<VariantDescription> dbVars = ctxToVariants(dbVC);

		int i = 1; // excludes reference allele
		for (VariantDescription obsVar : obsVars) {
			int j = 1; // excludes reference allele
			for (VariantDescription dbVar : dbVars) {
				if (dbVar.equals(obsVar))
					result.add(new GenotypeMatch(i, j, obsVC, dbVC));
				j += 1;
			}

			i += 1;
		}

		return result;
	}

	/**
	 * Pair genotypes of two {@link VariantContext}s based on their position, regardless of their genotype
	 * 
	 * In the end, all genotypes will be matched regardless of matching alleles, such that later the "best" (e.g., the
	 * highest frequency one) can be used for annotating a variant.
	 * 
	 * @param obsVC
	 *            {@link VariantContext} describing the observed variant
	 * @param dbVC
	 *            {@link VariantContext} describing the database variant
	 * @return {@link Collection} of {@link GenotypeMatch}es for the two variants
	 */
	public Collection<GenotypeMatch> positionOverlaps(VariantContext obsVC, VariantContext dbVC) {
		List<GenotypeMatch> result = new ArrayList<>();

		// Get normalized description of all alternative observed and database alleles
		Collection<VariantDescription> obsVars = ctxToVariants(obsVC);
		Collection<VariantDescription> dbVars = ctxToVariants(dbVC);

		int i = 1; // excludes reference allele
		for (VariantDescription obsVar : obsVars) {
			int j = 1; // excludes reference allele
			for (VariantDescription dbVar : dbVars) {
				if (dbVar.overlapsWith(obsVar))
					result.add(new GenotypeMatch(i, j, obsVC, dbVC));
				j += 1;
			}

			i += 1;
		}

		return result;
	}

	/**
	 * Convert a {@link VariantContext} to a list of normalized variant descriptions
	 *
	 * This will generate one {@link VariantDescription} for each alternative allele in <code>vcf</code>.
	 *
	 * @param vc
	 *            {@link VariantContext} to convert
	 * @return A {@link Collection} of {@link VariantDescription} objects corresponding to <code>vc</code>
	 */
	private Collection<VariantDescription> ctxToVariants(VariantContext vc) {
		List<VariantDescription> vars = new ArrayList<>();
		for (int i = 1; i < vc.getNAlleles(); ++i) {
			VariantDescription vd = new VariantDescription(vc.getContig(), vc.getStart() - 1,
					vc.getAlleles().get(0).getBaseString(), vc.getAlleles().get(i).getBaseString());
			VariantDescription nd = normalizer.normalizeVariant(vd);
			if (nd.getRef().isEmpty()) // is insertion
				nd = normalizer.normalizeInsertion(vd);
			vars.add(nd);
		}
		return vars;
	}

}
