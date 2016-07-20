package de.charite.compbio.jannovar.mendel;

import java.util.ArrayList;

/**
 * Helper class for building {@link Genotype} objects
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenotypeBuilder {

	/** Allele numbers */
	private final ArrayList<Integer> alleleNumbers;

	public GenotypeBuilder() {
		this.alleleNumbers = new ArrayList<>();
	}
	
	public Genotype build() {
		return new Genotype(alleleNumbers);
	}

	public ArrayList<Integer> getAlleleNumbers() {
		return alleleNumbers;
	}

}
