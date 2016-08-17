package de.charite.compbio.jannovar.mendel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.pedigree.Pedigree;

/**
 * Base class for tests for MendelianCompatibilityChecker JUnit tests
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 * @author <a href="mailto:max.schubach@charite.de">Max Schubach</a>
 */
public class MendelianCompatibilityCheckerTestBase {

	/** Helper enum type for simple genotypes */
	protected enum SimpleGenotype {
		/** heterozygous */
		HET,
		/** homozygous ref */
		REF,
		/** homozygous alt */
		ALT,
		/** unknown/no-call/not observed */
		UKN
	}

	protected final SimpleGenotype HET = SimpleGenotype.HET;
	protected final SimpleGenotype REF = SimpleGenotype.REF;
	protected final SimpleGenotype ALT = SimpleGenotype.ALT;
	protected final SimpleGenotype UKN = SimpleGenotype.UKN;

	protected ImmutableList<String> names;
	protected Pedigree pedigree;

	/**
	 * @return a {@link com.google.common.collect.ImmutableList} object.
	 */
	protected ImmutableList<SimpleGenotype> lst(SimpleGenotype... gts) {
		ImmutableList.Builder<SimpleGenotype> builder = new ImmutableList.Builder<SimpleGenotype>();
		for (int i = 0; i < gts.length; ++i)
			builder.add(gts[i]);
		return builder.build();
	}

	protected List<GenotypeCalls> getGenotypeCallsList(ImmutableList<SimpleGenotype> genotypes,
			boolean isXchromosomal) {
		HashMap<String, Genotype> entries = new HashMap<String, Genotype>();
		for (int i = 0; i < names.size(); ++i) {
			switch (genotypes.get(i)) {
			case HET:
				entries.put(names.get(i), new Genotype(ImmutableList.of(Genotype.REF_CALL, 1)));
				break;
			case REF:
				entries.put(names.get(i), new Genotype(ImmutableList.of(Genotype.REF_CALL, Genotype.REF_CALL)));
				break;
			case ALT:
				entries.put(names.get(i), new Genotype(ImmutableList.of(1, 1)));
				break;
			case UKN:
				entries.put(names.get(i), new Genotype(ImmutableList.of(Genotype.NO_CALL, Genotype.NO_CALL)));
				break;
			}
		}

		List<GenotypeCalls> gcs = new ArrayList<GenotypeCalls>();
		gcs.add(new GenotypeCalls(isXchromosomal ? ChromosomeType.X_CHROMOSOMAL : ChromosomeType.AUTOSOMAL,
				entries.entrySet()));
		return gcs;
	}

	@SuppressWarnings("unchecked")
	protected List<GenotypeCalls> getGenotypeCallsList(ImmutableList<SimpleGenotype> genotypes1,
			ImmutableList<SimpleGenotype> genotypes2, boolean isXchromosomal) {
		List<GenotypeCalls> gcs = new ArrayList<GenotypeCalls>();
		for (Object obj : new Object[] { genotypes1, genotypes2 }) {
			ImmutableList<SimpleGenotype> genotypes = (ImmutableList<SimpleGenotype>) obj;
			HashMap<String, Genotype> entries = new HashMap<String, Genotype>();
			for (int i = 0; i < names.size(); ++i) {
				switch (genotypes.get(i)) {
				case HET:
					entries.put(names.get(i), new Genotype(ImmutableList.of(Genotype.REF_CALL, 1)));
					break;
				case REF:
					entries.put(names.get(i), new Genotype(ImmutableList.of(Genotype.REF_CALL, Genotype.REF_CALL)));
					break;
				case ALT:
					entries.put(names.get(i), new Genotype(ImmutableList.of(1, 1)));
					break;
				case UKN:
					entries.put(names.get(i), new Genotype(ImmutableList.of(Genotype.NO_CALL, Genotype.NO_CALL)));
					break;
				}
			}

			gcs.add(new GenotypeCalls(isXchromosomal ? ChromosomeType.X_CHROMOSOMAL : ChromosomeType.AUTOSOMAL,
					entries.entrySet()));
		}
		return gcs;
	}

}
