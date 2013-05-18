package jannovar.interval;






import jannovar.interval.Interval;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;




public class IntervalTest {
    
    
    @Test public void testIntervalConstruction1()  
    {
	Interval<String> ivl = new Interval<String>(5,6,"test");
        Assert.assertEquals(5,ivl.getLow());
        Assert.assertEquals(6,ivl.getHigh());
        Assert.assertEquals("test",ivl.getValue());
    }
    
    @Test public void testIntervalConstruction2()  
    {
	
        Byte b = new Byte("4");
        Interval<Byte> ivl = new Interval<Byte>(50,99,b);
        Assert.assertEquals(50,ivl.getLow());
        Assert.assertEquals(99,ivl.getHigh());
        Assert.assertEquals(b,ivl.getValue());
    }
    
    @Test public void testIntervalConstruction3()  
    {
	Integer h = new Integer(100);
        
        Interval<Integer> ivl = new Interval<Integer>(505,959,h);
        Assert.assertEquals(505,ivl.getLow());
        Assert.assertEquals(959,ivl.getHigh());
        Assert.assertEquals(h,ivl.getValue());
        String i = ivl.toString();
        Assert.assertEquals("[505,959,100]",i);
    }
    
    
}