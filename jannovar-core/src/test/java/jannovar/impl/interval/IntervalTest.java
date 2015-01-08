package jannovar.impl.interval;



import java.util.Collections;
import java.util.ArrayList;

import jannovar.impl.interval.Interval;
import jannovar.impl.interval.IntervalTreeException;
import jannovar.impl.interval.LeftComparator;
import jannovar.impl.interval.RightComparator;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;


/**
 * Tests the Interval class.
 */
@SuppressWarnings("all")
public class IntervalTest {
    
    
    @Test public void testIntervalConstruction1()  throws IntervalTreeException
    {
	Interval<String> ivl = new Interval<String>(5,6,"test");
        Assert.assertEquals(5,ivl.getLow());
        Assert.assertEquals(6,ivl.getHigh());
        Assert.assertEquals("test",ivl.getValue());
    }
    
    @Test public void testIntervalConstruction2()  throws IntervalTreeException
    {
	
        Byte b = new Byte("4");
        Interval<Byte> ivl = new Interval<Byte>(50,99,b);
        Assert.assertEquals(50,ivl.getLow());
        Assert.assertEquals(99,ivl.getHigh());
        Assert.assertEquals(b,ivl.getValue());
    }
    
    @Test public void testIntervalConstruction3()  throws IntervalTreeException
    {
	Integer h = new Integer(100);
        
        Interval<Integer> ivl = new Interval<Integer>(505,959,h);
        Assert.assertEquals(505,ivl.getLow());
        Assert.assertEquals(959,ivl.getHigh());
        Assert.assertEquals(h,ivl.getValue());
        String i = ivl.toString();
        Assert.assertEquals("[505,959,100]",i);
    }
    
    /** 
	// REmove exception for the nonce
	@Test(expected =  IntervalTreeException.class)  
	public void shouldChokeOnIntervalCTOR() throws IntervalTreeException {
	Interval<String> ivl = new Interval(70,50,"badstring");
	}*/
    @Test public void testLeftCompare() throws IntervalTreeException {
        Interval<String> i1 = new Interval<String>(3,100,"A");
        Interval<String> i2 = new Interval<String>(7,90,"B");
        Interval<String> i3 = new Interval<String>(4,55,"C");
        Interval<String> i4 = new Interval<String>(20,21,"D");
        Interval<String> i5 = new Interval<String>(17,19,"E");
        ArrayList<Interval<String>>  lst = new  ArrayList<Interval<String>>();
        lst.add(i1);
        lst.add(i2);
        lst.add(i3);
        lst.add(i4);
        lst.add(i5);
        Collections.sort(lst,new LeftComparator());
        Assert.assertEquals(i1,lst.get(0));
        Assert.assertEquals(i3,lst.get(1));
        Assert.assertEquals(i2,lst.get(2));
        Assert.assertEquals(i5,lst.get(3));
        Assert.assertEquals(i4,lst.get(4));
    }


    @Test public void testRightCompare() throws IntervalTreeException {
        Interval<String> i1 = new Interval<String>(3,100,"A");
        Interval<String> i2 = new Interval<String>(7,200,"B");
        Interval<String> i3 = new Interval<String>(4,590,"C");
        Interval<String> i4 = new Interval<String>(20,21,"D");
        Interval<String> i5 = new Interval<String>(17,32,"E");
        ArrayList<Interval<String>>  lst = new  ArrayList<Interval<String>>();
        lst.add(i1);
        lst.add(i2);
        lst.add(i3);
        lst.add(i4);
        lst.add(i5);
        Collections.sort(lst,new RightComparator());
        Assert.assertEquals(i3,lst.get(0));
        Assert.assertEquals(i2,lst.get(1));
        Assert.assertEquals(i1,lst.get(2));
        Assert.assertEquals(i5,lst.get(3));
        Assert.assertEquals(i4,lst.get(4));
    }

    
    
}