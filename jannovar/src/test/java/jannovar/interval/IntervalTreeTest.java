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
	Interval<String> iB = new Interval(5,8,"b");
	Interval<String> iC = new Interval(4,7,"c");
	Interval<String> iD = new Interval(5,6,"d");
	Interval<String> iE = new Interval(16,19,"e");
	Interval<String> iF = new Interval(11,15,"f");
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
    /*
    @Test
        public void findAllIntervals() throws IntervalTreeException {
	// create a tree with the interval list from setUp()
	IntervalTree<String> tree = new IntervalTree<String>(randomList());
	// all intervals in the random tree overlap with the interval (0,2000):
	Assert.assertEquals(5000, tree.search(0, 2000).size());	
    }
    */

     @Test public void testSearch1() throws IntervalTreeException {
	IntervalTree<String> tree = new IntervalTree<String>( getIntervalList1() );
	List<String> qy = tree.search(1,2);
	Assert.assertEquals("a",qy.get(0));
    }

    @Test public void testSearch2a() throws IntervalTreeException {
	IntervalTree<String> tree = new IntervalTree<String>( getIntervalList1() );
	List<String> qy = tree.search(6,7);
	Collections.sort(qy);
	Assert.assertEquals(3,qy.size());
    }

    @Test public void testSearch2b() throws IntervalTreeException {
	IntervalTree<String> tree = new IntervalTree<String>( getIntervalList1() );
	List<String> qy = tree.search(11,12);
	Collections.sort(qy);
	Assert.assertEquals("f",qy.get(0));
    }

   

    /** Tests not finding an interval  */
    @Test public void testSearch3a() throws IntervalTreeException {
	IntervalTree<String> tree = new IntervalTree<String>( getIntervalList1() );
	List<String> qy = tree.search(20,20);
	Assert.assertEquals(0,qy.size());
    }

     /** Tests not finding an interval but getting the left neighbor, this is d=(5,7) */
    @Test public void testSearch3b() throws IntervalTreeException {
	IntervalTree<String> tree = new IntervalTree<String>( getIntervalList1() );
	List<String> qy = tree.search(20,20);
	String lft = tree.getLeftNeighbor();
	Assert.assertEquals("e",lft);
    }

      /** Tests not finding an interval but getting the right neighbor*/
    @Test public void testSearch3c() throws IntervalTreeException {
	IntervalTree<String> tree = new IntervalTree<String>( getIntervalList1() );
	List<String> qy = tree.search(20,20);
	String rt = tree.getRightNeighbor();
	Assert.assertEquals("g",rt);
    } 

     /** Tests not finding an interval but getting the right neighbor*/
    @Test public void testSearch3d() throws IntervalTreeException {
	IntervalTree<String> tree = new IntervalTree<String>( getIntervalList1() );
	List<String> qy = tree.search(62,62);
	String rt = tree.getRightNeighbor();
	Assert.assertEquals(null,rt);
    } 

   
    /** Tests not finding an interval but getting the right neighbor*/
    @Test public void testSearch3e() throws IntervalTreeException {
	IntervalTree<String> tree = new IntervalTree<String>( getIntervalList1() );
	List<String> qy = tree.search(69,69);
	String rt = tree.getLeftNeighbor();
	Assert.assertEquals("g",rt);
    } 

}
