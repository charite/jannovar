package jannovar.interval;

import java.util.Comparator;

/**
 * This class is intended to be used to sort lists of
 * {@link jannovar.interval.Interval Interval} objects by ascending order of
 * their left low-points.
 * 
 * @author Christopher Dommaschenz, Radostina Misirkova, Nadine Taube, Gizem Top
 * @version 0.03 (22 May, 2013)
 * @param <T>
 */
public class LeftComparator implements Comparator<Interval<?>> {

	@Override
	public int compare(Interval<?> interval_1, Interval<?> interval_2) {
		/* returns -1 if the lowpoint of i is smaller than the lowpoint of j */
		if (interval_1.getLow() < interval_2.getLow())
			return -1;
		/* returns 1 if the lowpoint of i is bigger than the lowpoint of j */
		else if (interval_1.getLow() > interval_2.getLow())
			return 1;
		/*
		 * returns -1 if the highpoint of i is smaller than the highpoint of j
		 */
		else if (interval_1.getHigh() < interval_2.getHigh())
			return -1;
		/* returns 1 if the highpoint of i is bigger than the highpoint of j */
		else if (interval_1.getHigh() > interval_2.getHigh())
			return 1;
		/* returns 0 if they are equal */
		else
			return 0;
	}

}