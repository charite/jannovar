package de.charite.compbio.jannovar.impl.util;

/**
 * A simple status bar that only work on terminals where "\r" has an affect.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class ProgressBar {

	// TODO(holtgrem): improve documentation
	// TODO(holtgrem): allow incremental printing for text files
	public final long min;
	public final long max;

	public ProgressBar(long min, long max) {
		this.min = min;
		this.max = max;
	}

	public void print(long pos) {
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