package de.charite.compbio.jannovar.annotation;

/**
 * Small helper for printing {@link VariantEffect}.
 *
 * Run directly in Eclipse via right-click and "Run As > Java Application".
 */
class VariantEffectPrinter {

	public static void main(String[] args) {
		System.err.println("Updated Effects\n");
		for (VariantEffect ve : VariantEffect.values())
			System.err.println(ve.getImpact() + "\t" + ve.getSequenceOID() + "\t" + ve.getSequenceOntologyTerm());

		System.err.println("\nClassic Jannovar Effects\n");
		for (OldVariantType t : OldVariantType.values())
			System.err.println(t.priorityLevel() + "\t" + t + "\t" + t.toDisplayString());

		System.err.println("\nSO To Classic Term\n");
		for (VariantEffect ve : VariantEffect.values())
			if (ve.getSequenceOntologyTerm() != null)
				System.err.println(ve.getSequenceOntologyTerm() + "\t"
						+ ((ve.getLegacyTerm() == null) ? "-" : ve.getLegacyTerm()));
	}
}
