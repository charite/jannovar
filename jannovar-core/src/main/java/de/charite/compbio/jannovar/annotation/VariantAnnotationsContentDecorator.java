package de.charite.compbio.jannovar.annotation;

import java.util.HashSet;

// TODO(holtgrew): Test me!

/**
 * Decorator for {@link VariantAnnotations} that allows queries on the content.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:Peter.Robinson@jax.org">Peter N Robinson</a>
 */
public class VariantAnnotationsContentDecorator {

	/** the decorated {@link VariantAnnotations} */
	private final VariantAnnotations annotations;

	/**
	 * Initialize the decorator.
	 *
	 * @param annotations
	 *            {@link VariantAnnotations} of {@link Annotation} objects
	 */
	public VariantAnnotationsContentDecorator(VariantAnnotations annotations) {
		this.annotations = annotations;
	}

	/** @return the decorated {@link VariantAnnotations} */
	public VariantAnnotations getAnnotations() {
		return annotations;
	}

	/**
	 * @return <code>true</code> if the list has entries for multiple gene symbols.
	 */
	public boolean hasMultipleGeneSymbols() {
		HashSet<String> geneSymbols = new HashSet<String>();
		for (Annotation entry : annotations.getAnnotations())
			geneSymbols.add(entry.getTranscript().getGeneSymbol());
		return (geneSymbols.size() > 0);
	}

	/**
	 * @return <code>true</code> if the list contains a variant where {@link VariantEffect#isStructural()} returns
	 *         <code>true</code>
	 */
	public boolean hasStructuralVariant() {
		for (Annotation entry : annotations.getAnnotations())
			if (entry.getMostPathogenicVarType().isStructural())
				return true;
		return false;
	}

	/**
	 * @return the gene symbol of the annotation with highest priority or <code>null</code> if {@link #annotations} is
	 *         empty
	 */
	public String getGeneSymbol() {
		if (annotations.getAnnotations().size() == 0)
			return null;
		else
			return annotations.getAnnotations().get(0).getTranscript().getGeneSymbol();
	}

	/**
	 * @return the gene ID of the variant with highest priority, or <code>null</code> if no such variant
	 */
	public String getGeneID() {
		if (annotations.getAnnotations().size() == 0)
			return null;
		else
			return annotations.getAnnotations().get(0).getTranscript().getGeneID();
	}

	/**
	 * @return the {@link VariantEffect} of the variant with highest priority or <code>null</code> if no such variant
	 *         exists
	 */
	public VariantEffect getVariantType() {
		if (annotations.getAnnotations().size() == 0)
			return null;
		else
			return annotations.getAnnotations().get(0).getMostPathogenicVarType();
	}

}
