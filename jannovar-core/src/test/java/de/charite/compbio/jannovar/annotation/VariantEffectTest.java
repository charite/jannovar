package de.charite.compbio.jannovar.annotation;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Unit tests for {@link VariantEffect}s.
 *
 * @author Roland Ewald <roland.ewald@limbus-medtec.com>
 */
public class VariantEffectTest {

	@Test
	public void sequenceOntologyTermsAreUnique() {
		Set<String> soTerms = new HashSet<String>();
		for (VariantEffect variantEffect : VariantEffect.values()) {
			if (variantEffect.getSequenceOntologyTerm() != null) {
				Assertions.assertFalse(
					soTerms.contains(variantEffect.getSequenceOntologyTerm()), "SO terms should be unique, but '" + variantEffect + "' defines '"
						+ variantEffect.getSequenceOntologyTerm() + "', which is already taken.");
				soTerms.add(variantEffect.getSequenceOntologyTerm());
			}
		}
	}

	@Test
	public void sequenceOIDsAreUnique() {
		Set<String> soTerms = new HashSet<String>();
		for (VariantEffect variantEffect : VariantEffect.values()) {
			if (variantEffect.getSequenceOID() != null) {
				Assertions.assertFalse(
					soTerms.contains(variantEffect.getSequenceOID()), "SO terms should be unique, but '" + variantEffect + "' defines '"
						+ variantEffect.getSequenceOID() + "', which is already taken.");
				soTerms.add(variantEffect.getSequenceOID());
			}
		}
	}

}
