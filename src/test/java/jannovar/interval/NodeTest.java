package jannovar.interval;

import jannovar.exception.IntervalTreeException;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tests the Interval class.
 */

public class NodeTest {

	/** Median should be 3,4,5,17,19,20,21,55,90,100 => 19? */
	private ArrayList<Interval<String>> createIntervalList1() throws IntervalTreeException {
		Interval<String> i1 = new Interval<String>(3, 100, "A");
		Interval<String> i2 = new Interval<String>(7, 90, "B");
		Interval<String> i3 = new Interval<String>(4, 55, "C");
		Interval<String> i4 = new Interval<String>(20, 21, "D");
		Interval<String> i5 = new Interval<String>(17, 19, "E");
		ArrayList<Interval<String>> lst = new ArrayList<Interval<String>>();
		lst.add(i1);
		lst.add(i2);
		lst.add(i3);
		lst.add(i4);
		lst.add(i5);
		return lst;
	}

	@BeforeClass
	public static void setUp() {
		// Node.setLeftComparator(new LeftComparator());
		// Node.setRightComparator(new RightComparator());
	}

	@Test
	public void testNodeConstruction1() throws IntervalTreeException {
		ArrayList<Interval<String>> lst = createIntervalList1();
		Node n = new Node(lst, new LeftComparator(), new RightComparator());
		Integer m = n.getMedian();
		Assert.assertEquals((Integer) 19, m);
	}

}