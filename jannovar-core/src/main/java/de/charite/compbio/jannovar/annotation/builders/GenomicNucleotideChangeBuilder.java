package de.charite.compbio.jannovar.annotation.builders;

import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideDeletion;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideIndel;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideInsertion;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideInversion;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;

/**
 * Build {@link NucleotideChange} for a {@link GenomeVariant}.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenomicNucleotideChangeBuilder {

	private final GenomeVariant variant;

	/** Initialize with the given <code>variant</code>. */
	public GenomicNucleotideChangeBuilder(GenomeVariant variant) {
		this.variant = variant;
	}

	/**
	 * @return {@link GenomeVariant} that the {@link NucleotideChange} is to be built for.
	 */
	public GenomeVariant getVariant() {
		return variant;
	}

	/**
	 * @return {@link NucleotideChange} corresponding to {@link #getVariant()}.
	 */
	public NucleotideChange build() {
		final String ref = variant.getRef();
		final String alt = variant.getAlt();

		GenomePosition position = variant.getGenomePos();
		final int beginPos = position.getPos();

		if (ref.length() == 1 && alt.length() == 1)
			return NucleotideSubstitution.build(false, beginPos, ref, alt);

		if (ref.length() == 0)
			// if transcript is null it is intergenic
			return NucleotideInsertion.buildWithSequence(false, beginPos - 1, beginPos, alt);
		else if (alt.length() == 0)
			// if tm is null it is intergenic
			return NucleotideDeletion.buildWithSequence(false, beginPos, beginPos + ref.length() - 1, ref);
		else if (ref.length() != alt.length())
			// if tm is null it is intergenic
			return NucleotideIndel.buildWithSequence(false, beginPos, beginPos + ref.length() - 1, ref, alt);

		// Build reverse-complement of alt string.
		StringBuilder altRC = new StringBuilder(alt).reverse();
		for (int i = 0; i < altRC.length(); ++i)
			if (altRC.charAt(i) == 'A')
				altRC.setCharAt(i, 'T');
			else if (altRC.charAt(i) == 'T')
				altRC.setCharAt(i, 'A');
			else if (altRC.charAt(i) == 'C')
				altRC.setCharAt(i, 'G');
			else if (altRC.charAt(i) == 'G')
				altRC.setCharAt(i, 'C');

		if (ref.length() == alt.length() && ref.equals(altRC.toString()))
			return NucleotideInversion.buildWithoutSeqDescription(false, beginPos, beginPos + ref.length()
					- 1);
		else
			return NucleotideIndel.buildWithSequence(false, beginPos, beginPos + ref.length() - 1, ref, alt);
	}

}
