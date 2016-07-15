package de.charite.compbio.jannovar;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import com.google.common.base.Supplier;

import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper for displaying progress
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ProgressReporter extends TimerTask {

	/** Callable to use for retrieving the {@link VariantContext} for the current point */
	private final Supplier<VariantContext> getVC;
	/** Number of seconds between intervals */
	private int seconds;

	public ProgressReporter(Supplier<VariantContext> getVC, int seconds) {
		this.getVC = getVC;
		this.seconds = seconds;
	}

	public void print() {
		final VariantContext vc;
		try {
			vc = this.getVC.get();
			if (vc == null)
				return; // ignore, no VC
		} catch (Exception e) {
			return; // ignore
		}

		String strPos = NumberFormat.getNumberInstance(Locale.US).format(vc.getStart());
		System.err.println(String.format("Currently at %s:%s", vc.getContig(), strPos));
	}

	@Override
	public void run() {
		print();
	}

	public void start() {
		Timer timer = new Timer(true);
		timer.schedule(this, 0, this.seconds * 1000);
	}

	public void stop() {
		// TODO Auto-generated method stub

	}

}
