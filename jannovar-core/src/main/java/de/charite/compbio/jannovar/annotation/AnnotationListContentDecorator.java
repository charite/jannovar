package de.charite.compbio.jannovar.annotation;

import java.util.HashSet;

// TODO(holtgrew): Test me!

/**
 * Decorator for {@link AnnotationList} that allows queries on the content.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 * @author Peter N Robinson <peter.robinson@charite.de>
 */
public class AnnotationListContentDecorator {

	/** the decorated {@link AnnotationList} */
	public final AnnotationList annotations;

	/**
	 * Initialize the decorator.
	 *
	 * @param annotations
	 *            {@link AnnotationList} of {@link Annotation} objects
	 */
	public AnnotationListContentDecorator(AnnotationList annotations) {
		this.annotations = annotations;
	}

	/**
	 * @return <code>true</code> if the list has entries for multiple gene symbols.
	 */
	public boolean hasMultipleGeneSymbols() {
		HashSet<String> geneSymbols = new HashSet<String>();
		for (Annotation entry : annotations.entries)
			geneSymbols.add(entry.transcript.geneSymbol);
		return (geneSymbols.size() > 0);
	}

	/**
	 * @return <code>true</code> if the list contains a variant where {@link VariantEffect#isSV()} returns
	 *         <code>true</code>.
	 */
	public boolean hasStructuralVariant() {
		for (Annotation entry : annotations.entries)
			if (entry.getMostPathogenicVarType().isStructural())
				return true;
		return false;
	}

	/**
	 * @return the gene symbol of the annotation with highest priority or <code>null</code> if {@link #annotations} is
	 *         empty.
	 */
	public String getGeneSymbol() {
		if (annotations.entries.size() == 0)
			return null;
		else
			return annotations.entries.get(0).transcript.geneSymbol;
	}

	/**
	 * @return the gene ID of the variant with highest priority, or <code>null</code> if no such variant
	 */
	public String getGeneID() {
		if (annotations.entries.size() == 0)
			return null;
		else
			return annotations.entries.get(0).transcript.geneID;
	}

	/**
	 * @return the {@link VariantEffect} of the variant with highest priority or <code>null</code> if no such variant
	 *         exists
	 */
	public VariantEffect getVariantType() {
		if (annotations.entries.size() == 0)
			return null;
		else
			return annotations.entries.get(0).getMostPathogenicVarType();
	}

}
