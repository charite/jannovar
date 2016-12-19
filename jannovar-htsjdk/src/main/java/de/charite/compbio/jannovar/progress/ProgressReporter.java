package de.charite.compbio.jannovar.progress;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper for displaying progress
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ProgressReporter extends TimerTask {

	private static ImmutableList<String> HEADERS = ImmutableList.of("Location", "processed.sites",
			"runtime.per.1M.sites", "completed", "total.runtime", "remaining");

	/** All contigs of the genome to expect */
	private final GenomeRegionList contigs;
	/** Current variant context */
	private VariantContext currentVC;
	/** Number of variant contexts */
	private int numProcessed;
	/** Number of seconds between intervals */
	private int seconds;
	/** Start time in miliseconds */
	private long startTime;

	public ProgressReporter(GenomeRegionList contigs, int seconds) {
		this.contigs = contigs;
		this.currentVC = null;
		this.numProcessed = 0;
		this.seconds = seconds;
		this.startTime = System.currentTimeMillis();
	}

	public void printHeader() {
		System.err.println(Joiner.on("\t").join(HEADERS));
	}

	public void print() {
		final VariantContext vc = currentVC;
		if (vc == null)
			return; // ignore

		final long elapsed = (System.currentTimeMillis() - this.startTime) / 1000;
		final double timeFor1MSites = elapsed / (this.numProcessed / 1000.0 / 1000.0);
		final long basesDone = contigs.lengthUpTo(vc.getContig(), vc.getStart() - 1);
		final long basesTotal = contigs.totalLength();
		final double percentDone = (100.0 * basesDone) / basesTotal;
		final long estimateTotal = (long) (elapsed / (percentDone / 100.0));
		final long estimateRemaining = estimateTotal - elapsed;

		ArrayList<String> arr = new ArrayList<>();
		arr.add(vc.getContig() + ":" + NumberFormat.getNumberInstance(Locale.US).format(vc.getStart()));
		arr.add(Integer.toString(this.numProcessed));
		arr.add(String.format("%.1f", timeFor1MSites));
		arr.add(String.format("%.1f%%", percentDone));
		arr.add(formatDuration(estimateTotal));
		arr.add(formatDuration(estimateRemaining));
		System.err.println(Joiner.on("\t").join(arr));
	}

	public String formatDuration(long seconds) {
		if (seconds > 60 * 60 * 24 * 1.5)
			return String.format("%.1f d", seconds / 60.0 / 60.0 / 24.0);
		else if (seconds > 60 * 60 * 1.5)
			return String.format("%.1f h", seconds / 60.0 / 60.0);
		else if (seconds > 60 * 1.5)
			return String.format("%.1f min", seconds / 60.0);
		else
			return String.format("%d s", seconds);
	}

	@Override
	public void run() {
		print();
	}

	public void start() {
		Timer timer = new Timer(true);
		timer.schedule(this, 0, this.seconds * 1000);
	}

	public synchronized VariantContext getCurrentVC() {
		return currentVC;
	}

	public synchronized void setCurrentVC(VariantContext currentVC) {
		this.numProcessed += 1;
		this.currentVC = currentVC;
	}

	public int getNumProcessed() {
		return numProcessed;
	}

	public void done() {
		final long elapsed = System.currentTimeMillis() - this.startTime;
		final double timeFor1MSites = (elapsed / 1000.0) / (this.numProcessed / 1000.0 / 1000.0);
		final long basesTotal = contigs.totalLength();
		final long basesDone = basesTotal;
		final double percentDone = (100.0 * basesDone) / basesTotal;
		final long estimateTotal = (long) (elapsed / (percentDone / 100.0));
		final long estimateRemaining = estimateTotal - elapsed;

		GenomeRegion last = contigs.getGenomeRegions().get(contigs.getGenomeRegions().size() - 1);

		ArrayList<String> arr = new ArrayList<>();
		arr.add(last.getContig() + ":" + (last.getBeginPos() + 1));
		arr.add(Integer.toString(this.numProcessed));
		arr.add(String.format("%.1f", timeFor1MSites));
		arr.add(String.format("%.1f%%", percentDone));
		arr.add(formatDuration(estimateTotal));
		arr.add(formatDuration(estimateRemaining));
		System.err.println(Joiner.on("\t").join(arr));
	}

}
