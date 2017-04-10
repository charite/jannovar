package de.charite.compbio.jannovar.stats.facade;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSortedSet;

import de.charite.compbio.jannovar.annotation.VariantAnnotations;
import de.charite.compbio.jannovar.annotation.VariantEffect;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.Genotype;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Facade class for collecting statistics from a {@link VariantContext} and a list of {@link VariantAnnotations}
 * objects.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class StatisticsCollector {

	/** Sample names */
	ImmutableList<String> sampleNames;

	/**
	 * Per-sample statistics, <code>null</code> codes for statistics on the variants regardless of genotype (i.e.,
	 * het/hom will all be 0, for example).
	 */
	private Map<String, Statistics> perSampleStats;

	public StatisticsCollector(Collection<String> sampleNames) {
		this.sampleNames = ImmutableList.copyOf(sampleNames);
		perSampleStats = new HashMap<>();
		perSampleStats.put(null, new Statistics());
		for (String name : sampleNames)
			perSampleStats.put(name, new Statistics());
	}

	/**
	 * Register {@link VariantContext} and allele {@link VariantAnnotations}
	 * 
	 * @param vc
	 *            {@link VariantContext} to register for
	 * @param alleleAnnotations
	 *            {@link VariantAnnotations} objects for each allele in <code>vc</code>
	 */
	public void put(VariantContext vc, List<VariantAnnotations> alleleAnnotations) {
		// Counts for the variant regardless of genotype
		//
		// register per-variant counts
		putAltAlleleCount(vc, null);
		putFilter(vc, null);
		putContig(vc, null);
		// register per-allele counts
		if (alleleAnnotations != null)
			for (int i = 1; i < vc.getNAlleles(); ++i) {
				putPutativeImpact(vc, null, alleleAnnotations.get(i - 1));
				putVariantEffect(vc, null, alleleAnnotations.get(i - 1));
				putGenomeRegion(vc, null, alleleAnnotations.get(i - 1));
				putTsTv(vc, null, i - 1);
			}

		// Counts for the variants for each sample
		Set<Integer> seen = new HashSet<>();
		for (String sampleName : sampleNames) {
			seen.clear();
			Genotype gt = vc.getGenotype(sampleName);
			if (gt.isHomRef() || gt.isNoCall())
				continue; // skip non-alternative genotypes

			// register per-variant counts
			putContig(vc, sampleName);
			putAltAlleleCount(vc, sampleName);
			putFilter(vc, sampleName);

			// register per-allele counts
			for (Allele allele : gt.getAlleles()) {
				final int aIdx = vc.getAlleleIndex(allele);
				// ignore wild-type allele, count each variant allele only once
				if (alleleAnnotations != null)
					if (aIdx != 0 && !seen.contains(aIdx)) {
						putPutativeImpact(vc, sampleName, alleleAnnotations.get(aIdx - 1));
						putVariantEffect(vc, sampleName, alleleAnnotations.get(aIdx - 1));
						putGenomeRegion(vc, sampleName, alleleAnnotations.get(aIdx - 1));
						putTsTv(vc, sampleName, aIdx);
					}
				seen.add(aIdx);
			}
		}
	}

	private void putContig(VariantContext vc, String sampleName) {
		final Statistics stats = perSampleStats.get(sampleName);
		if (sampleName == null) {
			stats.putContig(vc.getContig());
		} else {
			final Genotype gt = vc.getGenotype(sampleName);
			if (!gt.isHomRef() && !gt.isNoCall())
				stats.putContig(vc.getContig());
		}
	}

	private void putAltAlleleCount(VariantContext vc, String sampleName) {
		final Statistics stats = perSampleStats.get(sampleName);
		if (sampleName == null) {
			stats.putAltAlleleCount(vc.getNAlleles() - 1);
		} else {
			final Genotype gt = vc.getGenotype(sampleName);
			int count = 0;
			Set<Integer> seen = new HashSet<>();
			for (Allele a : gt.getAlleles()) {
				final int idx = vc.getAlleleIndex(a);
				if (!seen.contains(idx) && idx != 0)
					count += 1;
				seen.add(idx);
			}
			stats.putAltAlleleCount(count);
		}
	}

	private void putFilter(VariantContext vc, String sampleName) {
		final Statistics stats = perSampleStats.get(sampleName);
		for (String ft : vc.getFilters())
			stats.putFilter(ft);
	}

	private void putVariantEffect(VariantContext vc, String sampleName, VariantAnnotations alleleAnno) {
		final Statistics stats = perSampleStats.get(sampleName);
		if (alleleAnno != null && alleleAnno.getHighestImpactAnnotation() != null
				&& alleleAnno.getHighestImpactAnnotation().getMostPathogenicVarType() != null)
			stats.putVariantEffect(alleleAnno.getHighestImpactAnnotation().getMostPathogenicVarType());
	}

	private void putGenomeRegion(VariantContext vc, String sampleName, VariantAnnotations alleleAnno) {
		final Statistics stats = perSampleStats.get(sampleName);
		if (alleleAnno.getHighestImpactAnnotation() == null
				|| alleleAnno.getHighestImpactAnnotation().getEffects() == null)
			return;
		final SortedSet<VariantEffect> effects = alleleAnno.getHighestImpactAnnotation().getEffects();
		final ImmutableSortedSet<VariantEffect> codingEffects = ImmutableSortedSet.of(
				VariantEffect.FRAMESHIFT_ELONGATION, VariantEffect.FRAMESHIFT_TRUNCATION,
				VariantEffect.FRAMESHIFT_VARIANT, VariantEffect.INTERNAL_FEATURE_ELONGATION,
				VariantEffect.FEATURE_TRUNCATION, VariantEffect.MNV, VariantEffect.COMPLEX_SUBSTITUTION,
				VariantEffect.STOP_GAINED, VariantEffect.STOP_LOST, VariantEffect.START_LOST,
				VariantEffect.MISSENSE_VARIANT, VariantEffect.INFRAME_DELETION, VariantEffect.INFRAME_DELETION,
				VariantEffect.DISRUPTIVE_INFRAME_INSERTION, VariantEffect.DISRUPTIVE_INFRAME_INSERTION,
				VariantEffect.STOP_RETAINED_VARIANT, VariantEffect.INITIATOR_CODON_VARIANT,
				VariantEffect.SYNONYMOUS_VARIANT, VariantEffect.NON_CODING_TRANSCRIPT_EXON_VARIANT,
				VariantEffect.EXON_VARIANT);
		final ImmutableSortedSet<VariantEffect> intronicEffects = ImmutableSortedSet.of(
				VariantEffect.SPLICE_ACCEPTOR_VARIANT, VariantEffect.SPLICE_DONOR_VARIANT,
				VariantEffect.SPLICE_REGION_VARIANT, VariantEffect.FIVE_PRIME_UTR_INTRON_VARIANT,
				VariantEffect.THREE_PRIME_UTR_INTRON_VARIANT, VariantEffect.INTRON_VARIANT,
				VariantEffect.CODING_TRANSCRIPT_INTRON_VARIANT, VariantEffect.NON_CODING_TRANSCRIPT_INTRON_VARIANT,
				VariantEffect.INTRON_VARIANT);
		final ImmutableSortedSet<VariantEffect> utr5Effects = ImmutableSortedSet.of(
				VariantEffect.FIVE_PRIME_UTR_EXON_VARIANT, VariantEffect.FIVE_PRIME_UTR_TRUNCATION,
				VariantEffect.FIVE_PRIME_UTR_PREMATURE_START_CODON_GAIN_VARIANT);
		final ImmutableSortedSet<VariantEffect> utr3Effects = ImmutableSortedSet
				.of(VariantEffect.THREE_PRIME_UTR_EXON_VARIANT, VariantEffect.THREE_PRIME_UTR_TRUNCATION);
		final ImmutableSortedSet<VariantEffect> upstreamEffects = ImmutableSortedSet
				.of(VariantEffect.UPSTREAM_GENE_VARIANT);
		final ImmutableSortedSet<VariantEffect> downstreamEffects = ImmutableSortedSet
				.of(VariantEffect.DOWNSTREAM_GENE_VARIANT);
		final ImmutableSortedSet<VariantEffect> intergenicEffects = ImmutableSortedSet
				.of(VariantEffect.INTERGENIC_VARIANT);

		for (VariantEffect effect : effects) {
			if (codingEffects.contains(effect)) {
				stats.putGenomeRegion(GenomeRegion.EXONIC);
				break;
			} else if (intronicEffects.contains(effect)) {
				stats.putGenomeRegion(GenomeRegion.INTRONIC);
				break;
			} else if (utr5Effects.contains(effect)) {
				stats.putGenomeRegion(GenomeRegion.UTR5);
				break;
			} else if (utr3Effects.contains(effect)) {
				stats.putGenomeRegion(GenomeRegion.UTR3);
				break;
			} else if (upstreamEffects.contains(effect)) {
				stats.putGenomeRegion(GenomeRegion.UPSTREAM);
				break;
			} else if (downstreamEffects.contains(effect)) {
				stats.putGenomeRegion(GenomeRegion.DOWNSTREAM);
				break;
			} else if (intergenicEffects.contains(effect)) {
				stats.putGenomeRegion(GenomeRegion.INTERGENIC);
				break;
			}
		}
	}

	private void putTsTv(VariantContext vc, String sampleName, int alleleIdx) {
		final Statistics stats = perSampleStats.get(sampleName);
		final Allele ref = vc.getReference();
		final Allele alt = vc.getAlleles().get(alleleIdx);
		// Consider ts/tv if it is a SNV
		if (ref.length() == 1 && !alt.isSymbolic() && alt.length() == 1) {
			final String refStr = ref.getBaseString().toUpperCase();
			final String altStr = alt.getBaseString().toUpperCase();
			if (isTransition(refStr, altStr))
				stats.putTsTv(TsTv.TS);
			else if (isTransversion(refStr, altStr))
				stats.putTsTv(TsTv.TV);
		}
	}

	private boolean isTransversion(String refStr, String altStr) {
		switch (refStr + altStr) {
		case "AT":
		case "TA":
		case "GC":
		case "CG":
		case "AC":
		case "CA":
		case "GT":
		case "TG":
			return true;
		default:
			return false;
		}
	}

	private boolean isTransition(String refStr, String altStr) {
		switch (refStr + altStr) {
		case "AG":
		case "GA":
		case "CT":
		case "TC":
			return true;
		default:
			return false;
		}
	}

	private void putPutativeImpact(VariantContext vc, String sampleName, VariantAnnotations alleleAnno) {
		final Statistics stats = perSampleStats.get(sampleName);
		if (alleleAnno != null && alleleAnno.getHighestImpactAnnotation() != null
				&& alleleAnno.getHighestImpactAnnotation().getPutativeImpact() != null)
			stats.putPutativeImpact(alleleAnno.getHighestImpactAnnotation().getPutativeImpact());
	}

	public ImmutableList<String> getSampleNames() {
		return sampleNames;
	}

	public Map<String, Statistics> getPerSampleStats() {
		return perSampleStats;
	}

}
