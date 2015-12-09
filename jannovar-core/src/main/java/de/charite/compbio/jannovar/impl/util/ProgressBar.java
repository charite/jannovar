package de.charite.compbio.jannovar.impl.util;

/**
 * A simple status bar that only work on terminals where "\r" has an affect.
 *
 * The progress is done/shown in the closed interval <code>[min, max]</code>.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class ProgressBar {

	/** smallest value */
	private final long min;
	/** largest value */
	private final long max;
	/** whether or not to print */
	private final boolean doPrint;

	/** Initialize progress bar with the given settings */
	public ProgressBar(long min, long max) {
		this(min, max, true);
	}

	/** Initialize progress bar with the given settings */
	public ProgressBar(long min, long max, boolean doPrint) {
		this.min = min;
		this.max = max;
		this.doPrint = true;
	}

	/** @return smallest value to represent */
	public long getMin() {
		return min;
	}

	/** @return largest value to represent */
	public long getMax() {
		return max;
	}

	/** @return <code>true</code> if the progress bar has printing enabled */
	public boolean doPrint() {
		return doPrint;
	}

	/** print progress up to position <code>pos</code>, if {@link #doPrint} */
	public void print(long pos) {
		if (!doPrint)
			return;
		int percent = (int) Math.ceil(100.0 * (pos - this.min) / (this.max - this.min));
		StringBuilder bar = new StringBuilder("[");

		for (int i = 0; i < 50; i++) {
			if (i < (percent / 2)) {
				bar.append("=");
			} else if (i == (percent / 2)) {
				bar.append(">");
			} else {
				bar.append(" ");
			}
		}

		bar.append("]   " + percent + "%     ");
		System.err.print("\r" + bar.toString());
		if (pos == max)
			System.err.println();
	}

}