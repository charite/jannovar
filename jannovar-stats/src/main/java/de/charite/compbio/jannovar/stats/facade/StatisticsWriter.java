package de.charite.compbio.jannovar.stats.facade;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.TreeSet;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.annotation.PutativeImpact;
import de.charite.compbio.jannovar.annotation.VariantEffect;

/**
 * Implementation of writing the statistics to a CSV file
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class StatisticsWriter implements AutoCloseable {

	private StatisticsCollector statsCollector;
	PrintWriter writer;

	public StatisticsWriter(StatisticsCollector statsCollector, File targetPath) throws FileNotFoundException {
		this.statsCollector = statsCollector;
		this.writer = new PrintWriter(targetPath);
	}

	public void writeStatistics() throws FileNotFoundException {
		writeHeader();
		writePutativeImpacts();
		writeVariantEffects();
		writeGenomeRegions();
		writeTsTvCount();
		writeAltAlleleCountHist();
		writeFilterCount();
		writeIsFilteredCount();
	}

	private void printHeader(String token) {
		writer.println(token + "\t" + Joiner.on('\t').join(statsCollector.getSampleNames()));
	}

	private void writeVariantEffects() {
		TreeSet<VariantEffect> keys = new TreeSet<>();
		for (Statistics stats : statsCollector.getPerSampleStats().values())
			keys.addAll(stats.getCountVariantEffects().keySet());

		writer.println();
		writer.println("[variant_effects]");
		printHeader("VE");

		for (VariantEffect effect : keys) {
			ArrayList<String> arr = new ArrayList<>();
			arr.add(effect.toString());

			for (String name : statsCollector.getSampleNames()) {
				arr.add(Integer.toString(
						statsCollector.getPerSampleStats().get(name).getCountVariantEffects().getOrDefault(effect, 0)));
			}
			writer.println(Joiner.on('\t').join(arr));
		}
	}

	private void writeGenomeRegions() {
		TreeSet<GenomeRegion> keys = new TreeSet<>();
		for (Statistics stats : statsCollector.getPerSampleStats().values())
			keys.addAll(stats.getCountGenomeRegion().keySet());

		writer.println();
		writer.println("[genome_regions]");
		printHeader("GR");

		for (GenomeRegion region : keys) {
			ArrayList<String> arr = new ArrayList<>();
			arr.add(region.toString());

			for (String name : statsCollector.getSampleNames()) {
				arr.add(Integer.toString(
						statsCollector.getPerSampleStats().get(name).getCountGenomeRegion().getOrDefault(region, 0)));
			}
			writer.println(Joiner.on('\t').join(arr));
		}
	}

	private void writeTsTvCount() {
		writer.println();
		writer.println("[ts_tv_count]");
		printHeader("TT");

		for (TsTv tsTv : TsTv.values()) {
			ArrayList<String> arr = new ArrayList<>();
			arr.add(tsTv.toString());

			for (String name : statsCollector.getSampleNames()) {
				arr.add(Integer
						.toString(statsCollector.getPerSampleStats().get(name).getTsTvCount().getOrDefault(tsTv, 0)));
			}
			writer.println(Joiner.on('\t').join(arr));
		}
	}

	private void writeAltAlleleCountHist() {
		TreeSet<Integer> keys = new TreeSet<>();
		for (Statistics stats : statsCollector.getPerSampleStats().values())
			keys.addAll(stats.getAltAlleleCountHist().keySet());

		writer.println();
		writer.println("[alt_allele_count]");
		printHeader("AC");
		for (Integer count : keys) {
			ArrayList<String> arr = new ArrayList<>();
			arr.add(count.toString());

			for (String name : statsCollector.getSampleNames()) {
				arr.add(Integer.toString(
						statsCollector.getPerSampleStats().get(name).getAltAlleleCountHist().getOrDefault(count, 0)));
			}
			writer.println(Joiner.on('\t').join(arr));
		}
	}

	private void writeFilterCount() {
		TreeSet<String> keys = new TreeSet<>();
		for (Statistics stats : statsCollector.getPerSampleStats().values())
			keys.addAll(stats.getFilterCount().keySet());

		writer.println();
		writer.println("[filter_count]");
		printHeader("FC");
		for (String filter : keys) {
			ArrayList<String> arr = new ArrayList<>();
			arr.add(filter);

			for (String name : statsCollector.getSampleNames()) {
				arr.add(Integer.toString(
						statsCollector.getPerSampleStats().get(name).getFilterCount().getOrDefault(filter, 0)));
			}
			writer.println(Joiner.on('\t').join(arr));
		}
	}

	private void writeIsFilteredCount() {
		writer.println();
		writer.println("[is_filtered_count]");
		printHeader("FT");

		for (Boolean isFiltered : ImmutableList.of(false, true)) {
			ArrayList<String> arr = new ArrayList<>();
			if (isFiltered)
				arr.add("FILTER");
			else
				arr.add("PASS");

			for (String name : statsCollector.getSampleNames()) {
				arr.add(Integer.toString(
						statsCollector.getPerSampleStats().get(name).getIsFilteredCount().getOrDefault(isFiltered, 0)));
			}
			writer.println(Joiner.on('\t').join(arr));
		}
	}

	private void writePutativeImpacts() {
		TreeSet<PutativeImpact> keys = new TreeSet<>();
		for (Statistics stats : statsCollector.getPerSampleStats().values())
			keys.addAll(stats.getCountPutativeImpacts().keySet());

		writer.println();
		writer.println("[putative_impacts]");
		printHeader("PI");

		for (PutativeImpact impact : keys) {
			ArrayList<String> arr = new ArrayList<>();
			arr.add(impact.toString());

			for (String name : statsCollector.getSampleNames()) {
				arr.add(Integer.toString(statsCollector.getPerSampleStats().get(name).getCountPutativeImpacts()
						.getOrDefault(impact, 0)));
			}
			writer.println(Joiner.on('\t').join(arr));
		}
	}

	private void writeHeader() {
		writer.println("# This file was written by jannovar statistics");
	}

	@Override
	public void close() throws Exception {
		this.writer.close();
	}

}
