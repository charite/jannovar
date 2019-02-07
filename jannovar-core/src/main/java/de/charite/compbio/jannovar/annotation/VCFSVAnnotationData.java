package de.charite.compbio.jannovar.annotation;

import com.google.common.base.CharMatcher;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.reference.SVGenomeVariant;
import de.charite.compbio.jannovar.reference.TranscriptModel;

/**
 * Class for collecting the data for a VCF structural variant annotation string.
 * <p>
 * Simplifies building of <code>Object</code> arrays that can then be joined using {@link Joiner}.
 */
class VCFSVAnnotationData {

	/**
	 * predicted effects
	 */
	public ImmutableSet<VariantEffect> effects = ImmutableSortedSet.<VariantEffect>of();
	/**
	 * predicted impact
	 */
	public PutativeImpact impact = null;
	/**
	 * symbol of affected gene
	 */
	public String geneSymbol = null;
	/**
	 * ID of affected gene
	 */
	public String geneID = null;
	/**
	 * type of the feature (<code>null</code> or <code>"transcript"</code>).
	 */
	public String featureType = null;
	/**
	 * ID of the feature/transcript
	 */
	public String featureID = null;
	/**
	 * bio type of the feature, one of "Coding" and "Noncoding".
	 */
	public String featureBioType = null;
	/**
	 * whether or not the transcript is coding
	 */
	public boolean isCoding = false;
	/**
	 * additional messages for the annotation
	 */
	public ImmutableSet<AnnotationMessage> messages = ImmutableSortedSet.<AnnotationMessage>of();

	public void setTranscriptAndVariant(TranscriptModel tm, SVGenomeVariant variant) {
		if (tm == null)
			return;
		featureType = "transcript";
		featureID = tm.getAccession();
		geneSymbol = tm.getGeneSymbol();
		geneID = tm.getGeneID();
		featureBioType = tm.isCoding() ? "Coding" : "Noncoding";
	}

	/**
	 * @return array of objects to be converted to string and joined, the alternative allele is given by
	 */
	public Object[] toArray() {
		final Joiner joiner = Joiner.on('&').useForNull("");
		final String effectsString = joiner.join(FluentIterable.from(effects).transform(VariantEffect.TO_SO_TERM));
		return new Object[]{effectsString, impact, geneSymbol, geneID, featureType, featureID,
			featureBioType, joiner.join(messages)};
	}

	public String toUnescapedString() {
		return Joiner.on('|').useForNull("").join(toArray());
	}

	private String escape(String str) {
		// Escaping follows the requirements of (1) VCF 4.2 and (2) the "Variant annotations in VCF format document.
		// We use the strategy of keeping as much as possible reconstructable (bijective mappings, for the
		// mathematically inclined).
		String result = CharMatcher.is('%').replaceFrom(str, "%25");
		result = CharMatcher.is(',').replaceFrom(result, "%2C");
		result = CharMatcher.is(';').replaceFrom(result, "%3B");
		result = CharMatcher.is('=').replaceFrom(result, "%3D");
		result = CharMatcher.is(' ').replaceFrom(result, "%20");
		result = CharMatcher.is('\t').replaceFrom(result, "%09");
		return result;
	}

	/**
	 * @return String for putting into the "ANN" field of the VCF file
	 */
	public String toString() {
		return escape(toUnescapedString());
	}

}
