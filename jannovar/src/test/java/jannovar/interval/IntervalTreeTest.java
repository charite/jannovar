package jannovar.interval;



import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


import jannovar.interval.Interval;
import jannovar.interval.IntervalTree;
import jannovar.interval.LeftComparator;
import jannovar.exception.IntervalTreeException;

public class IntervalTreeTest {

    public List<Interval<String>> randomList() throws IntervalTreeException {
	List<Interval<String>> ilist = new ArrayList<Interval<String>>();
	// in der for-Schleife speichere ich random integers in
	// zwei variablen rLow und rHigh, wobei
	// 0 < rLow < 2000 und rHigh < rLow
        
	for (int index = 0; index < 5000; index++) {
	    Random randomLow = new Random();
	    Random randomHigh = new Random();
	    
	    int rLow = randomLow.nextInt(2000);
	    // the rHigh integer should be greater than the rLow integer
	    int rHigh = randomHigh.nextInt(2000) + rLow;
	    // put the rLow and the rHigh into an interval
	    Interval<String> interval = new Interval<String>(rLow, rHigh,
							     "value " + index);
	    //System.out.println(interval);
	    // and add all the intervals into an interval list:
	    
	    ilist.add(interval);
	    
	}
	return ilist;
    }

    public List<Interval<String>> getIntervalList1() throws IntervalTreeException {
	List<Interval<String>> ilist = new ArrayList<Interval<String>>();
	Interval<String> iA = new Interval(1,3,"a");
	Interval<String> iB = new Interval(4,6,"b");
	Interval<String> iC = new Interval(2,4,"c");
	Interval<String> iD = new Interval(5,7,"d");
	Interval<String> iE = new Interval(1,2,"e");
	Interval<String> iF = new Interval(3,6,"f");
	Interval<String> iG = new Interval(30,66,"g");
	ilist.add(iA);
	ilist.add(iB);
	ilist.add(iC);
	ilist.add(iD);
	ilist.add(iE);
	ilist.add(iF);
	ilist.add(iG);
	return ilist;

    }
    
    @Test
        public void findAllIntervals() throws IntervalTreeException {
	// create a tree with the interval list from setUp()
	IntervalTree<String> tree = new IntervalTree<String>(randomList());
	// all intervals in the random tree overlap with the interval (0,2000):
	Assert.assertEquals(5000, tree.search(0, 2000).size());	
    }

    @Test public void testSearch1() throws IntervalTreeException {
	IntervalTree<String> tree = new IntervalTree<String>( getIntervalList1() );
	List<String> qy = tree.search(7,8);
	Assert.assertEquals("d",qy.get(0));
    }

    @Test public void testSearch2a() throws IntervalTreeException {
	IntervalTree<String> tree = new IntervalTree<String>( getIntervalList1() );
	List<String> qy = tree.search(3,3);
	Collections.sort(qy);
	Assert.assertEquals(3,qy.size());
	Assert.assertEquals("a",qy.get(0));

    }

    @Test public void testSearch2b() throws IntervalTreeException {
	IntervalTree<String> tree = new IntervalTree<String>( getIntervalList1() );
	List<String> qy = tree.search(3,3);
	Collections.sort(qy);

	Assert.assertEquals(3,qy.size());
	Assert.assertEquals("c",qy.get(1));
    }

     @Test public void testSearch2c() throws IntervalTreeException {
	IntervalTree<String> tree = new IntervalTree<String>( getIntervalList1() );
	List<String> qy = tree.search(3,3);
	Collections.sort(qy);
	Assert.assertEquals(3,qy.size());
	Assert.assertEquals("f",qy.get(2));
	//
    }

    /** Tests not finding an interval but getting the neighbor */
    @Test public void testSearch3() throws IntervalTreeException {
	IntervalTree<String> tree = new IntervalTree<String>( getIntervalList1() );
	List<String> qy = tree.search(10,11);
	Assert.assertEquals(0,qy.size());
    }

}
