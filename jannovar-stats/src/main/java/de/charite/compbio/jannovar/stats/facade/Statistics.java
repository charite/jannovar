package de.charite.compbio.jannovar.stats.facade;

import java.util.HashMap;
import java.util.Map;

import de.charite.compbio.jannovar.annotation.PutativeImpact;
import de.charite.compbio.jannovar.annotation.VariantEffect;

/**
 * Collection of statistics by certain properties of the variants
 * 
 * Note that for impact and predicted functional effect, only the highest-impact ones are registered.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class Statistics {

	/** Counts by putative impact */
	private Map<PutativeImpact, Integer> countPutativeImpacts;

	/** Counts by predicted functional effect */
	private Map<VariantEffect, Integer> countVariantEffects;

	/** Counts by genomic region */
	private Map<GenomeRegion, Integer> countGenomeRegion;

	/** Count of transition/transversion variants of SNVs */
	private Map<TsTv, Integer> tsTvCount;

	/** Count of multiallelic sites */
	private Map<Integer, Integer> altAlleleCountHist;

	/** Occurences of per-record filters */
	private Map<String, Integer> filterCount;

	/** Number of failing/passing variants (any filter is considered failing) */
	private Map<Boolean, Integer> isFilteredCount;

	/** Number of variants on the contigs */
	private Map<String, Integer> contigCount;

	// Counts of variant GQ scores should already be in bcftools stats

	public Statistics() {
		this.countPutativeImpacts = new HashMap<>();
		this.countVariantEffects = new HashMap<>();
		this.countGenomeRegion = new HashMap<>();
		this.tsTvCount = new HashMap<>();
		this.altAlleleCountHist = new HashMap<>();
		this.filterCount = new HashMap<>();
		this.isFilteredCount = new HashMap<>();
		this.contigCount = new HashMap<>();
	}

	public void putPutativeImpact(PutativeImpact impact) {
		this.countPutativeImpacts.putIfAbsent(impact, 0);
		this.countPutativeImpacts.put(impact, this.countPutativeImpacts.get(impact) + 1);
	}

	public void putVariantEffect(VariantEffect effect) {
		this.countVariantEffects.putIfAbsent(effect, 0);
		this.countVariantEffects.put(effect, this.countVariantEffects.get(effect) + 1);
	}

	public void putGenomeRegion(GenomeRegion region) {
		this.countGenomeRegion.putIfAbsent(region, 0);
		this.countGenomeRegion.put(region, this.countGenomeRegion.get(region) + 1);
	}

	public void putTsTv(TsTv tsTv) {
		this.tsTvCount.putIfAbsent(tsTv, 0);
		this.tsTvCount.put(tsTv, this.tsTvCount.get(tsTv) + 1);
	}

	public void putAltAlleleCount(int count) {
		this.altAlleleCountHist.putIfAbsent(count, 0);
		this.altAlleleCountHist.put(count, this.altAlleleCountHist.get(count) + 1);
	}

	public void putFilter(String filter) {
		if (".".equals(filter) || "PASS".equals(filter) || "".equals(filter)) {
			isFilteredCount.putIfAbsent(false, 0);
			isFilteredCount.put(false, isFilteredCount.get(false) + 1);
		} else {
			isFilteredCount.putIfAbsent(true, 0);
			isFilteredCount.put(false, isFilteredCount.get(true) + 1);
			String[] filters = filter.split(";");
			for (String filterValue : filters) {
				filterCount.putIfAbsent(filterValue, 0);
				filterCount.put(filterValue, filterCount.get(filterValue) + 1);
			}
		}
	}

	public void putContig(String contig) {
		contigCount.putIfAbsent(contig, 0);
		contigCount.put(contig, contigCount.get(contig) + 1);
	}

	public Map<PutativeImpact, Integer> getCountPutativeImpacts() {
		return countPutativeImpacts;
	}

	public Map<VariantEffect, Integer> getCountVariantEffects() {
		return countVariantEffects;
	}

	public Map<GenomeRegion, Integer> getCountGenomeRegion() {
		return countGenomeRegion;
	}

	public Map<TsTv, Integer> getTsTvCount() {
		return tsTvCount;
	}

	public Map<Integer, Integer> getAltAlleleCountHist() {
		return altAlleleCountHist;
	}

	public Map<String, Integer> getFilterCount() {
		return filterCount;
	}

	public Map<Boolean, Integer> getIsFilteredCount() {
		return isFilteredCount;
	}

	public Map<String, Integer> getContigCount() {
		return contigCount;
	}

}
