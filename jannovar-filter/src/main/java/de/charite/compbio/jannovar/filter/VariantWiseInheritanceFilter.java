package de.charite.compbio.jannovar.filter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.io.JannovarData;
import de.charite.compbio.jannovar.pedigree.CompatibilityCheckerException;
import de.charite.compbio.jannovar.pedigree.Genotype;
import de.charite.compbio.jannovar.pedigree.GenotypeListBuilder;
import de.charite.compbio.jannovar.pedigree.ModeOfInheritance;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.PedigreeDiseaseCompatibilityDecorator;
import de.charite.compbio.jannovar.pedigree.Person;

/**
 * A {@link VariantContext} filter that checks each variant individually for compatibility.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class VariantWiseInheritanceFilter implements VariantContextFilter {

	/** the logger object to use */
	private static final Logger LOGGER = LoggerFactory.getLogger(VariantWiseInheritanceFilter.class);

	/** Deserialized Jannovar data */
	private final JannovarData jannovarDB;
	/** The mode of inheritance to filter for */
	private final ModeOfInheritance modeOfInheritance;
	/** Names of {@link pedigree#members}. */
	private final ImmutableList<String> personNames;
	/** Next filter. */
	private final VariantContextFilter next;
	/** Compatibility checker for genotype call lists and {@link #pedigree}. */
	private final PedigreeDiseaseCompatibilityDecorator checker;

	/** Initialize */
	public VariantWiseInheritanceFilter(Pedigree pedigree, JannovarData jannovarDB,
			ModeOfInheritance modeOfInheritance, VariantContextFilter next) {
		this.jannovarDB = jannovarDB;
		this.modeOfInheritance = modeOfInheritance;
		this.next = next;
		this.checker = new PedigreeDiseaseCompatibilityDecorator(pedigree);

		ImmutableList.Builder<String> namesBuilder = new ImmutableList.Builder<String>();
		for (Person p : pedigree.members)
			namesBuilder.add(p.name);
		this.personNames = namesBuilder.build();
	}

	public void put(FlaggedVariant fv) throws FilterException {
		// check gene for compatibility and mark variants as compatible if so

		final int contigID = jannovarDB.refDict.contigID.get(fv.getVC().getChr());
		boolean isXChromosomal = (jannovarDB.refDict.contigID.get("chrX") != null && jannovarDB.refDict.contigID.get(
				"chrX").intValue() == contigID);

		GenotypeListBuilder builder = new GenotypeListBuilder(null, personNames, isXChromosomal);
		putGenotypes(fv, builder);
		try {
			fv.setIncluded(checker.isCompatibleWith(builder.build(), modeOfInheritance));
			if (fv.isIncluded())
				next.put(fv);
			LOGGER.trace("Variant {}compatible with {} (gt={}, var={})", new Object[] { fv.isIncluded() ? "" : "in",
					modeOfInheritance, builder.build(), fv.getVC() });
		} catch (CompatibilityCheckerException e) {
			throw new FilterException("Problem in mode of inheritance filter.", e);
		}
	}

	private void putGenotypes(FlaggedVariant fv, GenotypeListBuilder genotypeListBuilder) {
		final VariantContext vc = fv.getVC();
		for (int i = 0; i < vc.getAlternateAlleles().size(); ++i) {
			Allele currAlt = vc.getAlternateAllele(i);

			ImmutableList.Builder<Genotype> builder = new ImmutableList.Builder<Genotype>();
			for (int pID = 0; pID < personNames.size(); ++pID) {
				htsjdk.variant.variantcontext.Genotype gt = vc.getGenotype(personNames.get(pID));
				if (gt.getAlleles().size() > 2)
					throw new RuntimeException("Unexpected allele count: " + gt.getAlleles().size());

				// we consider everything non-ALT (for current alternative allele) to be REF
				final int idx0 = 0;
				final boolean isRef0 = !gt.getAllele(idx0).getBaseString().equals(currAlt.getBaseString());
				final int idx1 = (gt.getAlleles().size() == 2) ? 1 : 0;
				final boolean isRef1 = !gt.getAllele(idx1).getBaseString().equals(currAlt.getBaseString());

				// TODO(holtgrem): Handle case of symbolic alleles and write through?
				if (gt.getAllele(idx0).isNoCall() || gt.getAllele(idx1).isNoCall())
					builder.add(Genotype.NOT_OBSERVED);
				else if (isRef0 && isRef1)
					builder.add(Genotype.HOMOZYGOUS_REF);
				else if (!isRef0 && !isRef1)
					builder.add(Genotype.HOMOZYGOUS_ALT);
				else
					builder.add(Genotype.HETEROZYGOUS);
			}
			genotypeListBuilder.addGenotypes(builder.build());
			LOGGER.trace("genotype list={}", new Object[] { genotypeListBuilder.build() });
		}
	}

	public void finish() throws FilterException {
		next.finish();
	}

}
