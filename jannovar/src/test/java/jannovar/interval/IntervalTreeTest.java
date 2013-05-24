package jannovar.interval;

import static org.junit.Assert.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.junit.Test;

import jannovar.interval.Interval;
import jannovar.interval.IntervalTree;
import jannovar.exception.IntervalTreeException;

public class IntervalTreeTest {

    public List<Interval<String>> setUp() throws IntervalTreeException {
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
    
    @Test
        public void findAllIntervals() throws IntervalTreeException {
	// create a tree with the interval list from setUp()
	IntervalTree<String> tree = new IntervalTree<String>(setUp());
	// all intervals in the random tree overlap with the interval (0,2000):
	assertEquals(5000, tree.search(0, 2000).size());
	
    }

}
