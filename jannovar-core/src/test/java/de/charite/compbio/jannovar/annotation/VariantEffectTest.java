package de.charite.compbio.jannovar.annotation;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;

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
      if (variantEffect.getSequenceOID() != null) {
        assertFalse("SO terms should be unique, but '" + variantEffect + "' defines '" + variantEffect.getSequenceOID()
            + "', which is already taken.", soTerms.contains(variantEffect.getSequenceOID()));
        soTerms.add(variantEffect.getSequenceOID());
      }
    }
  }

}