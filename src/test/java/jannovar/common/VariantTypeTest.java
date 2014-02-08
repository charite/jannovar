package jannovar.common;





import jannovar.common.VariantType;


import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;

/**
 *Test the class VariantType. This class is pretty simple, but we want to 
 * avoid the possibility of an additional variant type being added to this
 * class and forgotten elsewhere.
 * There are currently 24 constants (Feb 9, 2014), e.g., DOWNSTREAM,  FS_DELETION, 
 */
public class VariantTypeTest {
   

    @Test public void testNumberOfConstants() 
    {
	int n = VariantType.class.getEnumConstants().length;
	Assert.assertEquals(24,n);
    }

    @Test public void testAllConstantsAreInPriorityList() 
    {
	int n = VariantType.getPrioritySortedList().length;
	int m = VariantType.class.getEnumConstants().length;
	Assert.assertEquals(n,m);
    }

    @Test public void testPriorityLevel1() 
    {
	int n = VariantType.priorityLevel( VariantType.FS_DELETION);
	Assert.assertEquals(1,n);
    }

    @Test public void testPriorityLevel2() 
    {
	int n = VariantType.priorityLevel( VariantType.FS_INSERTION);
	Assert.assertEquals(1,n);
    }

    @Test public void testPriorityLevel3() 
    {
	int n = VariantType.priorityLevel( VariantType.NON_FS_SUBSTITUTION);
	Assert.assertEquals(1,n);
    }
    
    @Test public void testPriorityLevel4() 
    {
	int n = VariantType.priorityLevel( VariantType.FS_SUBSTITUTION);
	Assert.assertEquals(1,n);
    }
    
    @Test public void testPriorityLevel5() 
    {
	int n = VariantType.priorityLevel( VariantType.MISSENSE);
	Assert.assertEquals(1,n);
    }

     @Test public void testPriorityLevel6() 
    {
	int n = VariantType.priorityLevel( VariantType.NON_FS_DELETION);
	Assert.assertEquals(1,n);
    }

    @Test public void testPriorityLevel7() 
    {
	int n = VariantType.priorityLevel( VariantType.NON_FS_INSERTION);
	Assert.assertEquals(1,n);
    }

    @Test public void testPriorityLevel8() 
    {
	int n = VariantType.priorityLevel( VariantType.SPLICING);
	Assert.assertEquals(1,n);
    }

    @Test public void testPriorityLevel9() 
    {
	int n = VariantType.priorityLevel( VariantType.STOPGAIN);
	Assert.assertEquals(1,n);
    }

 @Test public void testPriorityLevel10() 
    {
	int n = VariantType.priorityLevel( VariantType.STOPLOSS);
	Assert.assertEquals(1,n);
    }

    @Test public void testPriorityLevel11() 
    {
	int n = VariantType.priorityLevel( VariantType.FS_DUPLICATION);
	Assert.assertEquals(1,n);
    }

 @Test public void testPriorityLevel12() 
    {
	int n = VariantType.priorityLevel( VariantType.NON_FS_DUPLICATION);
	Assert.assertEquals(1,n);
    }
    @Test public void testPriorityLevel13() 
    {
	int n = VariantType.priorityLevel( VariantType.START_LOSS);
	Assert.assertEquals(1,n);
    }
   

    @Test public void testPriorityLevel15() 
    {
	int n = VariantType.priorityLevel( VariantType.ncRNA_EXONIC);
	Assert.assertEquals(2,n);
    }

    @Test public void testPriorityLevel16() 
    {
	int n = VariantType.priorityLevel( VariantType.ncRNA_SPLICING);
	Assert.assertEquals(2,n);
    }
    
    @Test public void testPriorityLevel17() 
    {
	int n = VariantType.priorityLevel( VariantType.UTR3);
	Assert.assertEquals(3,n);
    }

    @Test public void testPriorityLevel18() 
    {
	int n = VariantType.priorityLevel( VariantType.UTR5);
	Assert.assertEquals(4,n);
    }
    
    @Test public void testPriorityLevel19() 
    {
	int n = VariantType.priorityLevel( VariantType.SYNONYMOUS);
	Assert.assertEquals(5,n);
    }

    @Test public void testPriorityLevel20() 
    {
	int n = VariantType.priorityLevel( VariantType.INTRONIC);
	Assert.assertEquals(6,n);
    }
    @Test public void testPriorityLevel21() 
    {
	int n = VariantType.priorityLevel( VariantType.ncRNA_INTRONIC);
	Assert.assertEquals(7,n);
    }

    @Test public void testPriorityLevel22() 
    {
	int n = VariantType.priorityLevel( VariantType.UPSTREAM);
	Assert.assertEquals(8,n);
    }
    @Test public void testPriorityLevel23() 
    {
	int n = VariantType.priorityLevel( VariantType.DOWNSTREAM);
	Assert.assertEquals(8,n);
    }
    @Test public void testPriorityLevel24() 
    {
	int n = VariantType.priorityLevel( VariantType.INTERGENIC);
	Assert.assertEquals(9,n);
    }
    @Test public void testPriorityLevel25() 
    {
	int n = VariantType.priorityLevel( VariantType.ERROR);
	Assert.assertEquals(10,n);
    }

    /* This tests that the constants returned by the function
     * getPrioritySortedList() are arranged in monotonically non-decreasing order.*/
    @Test public void testOrderingOfPriorityLevel() 
    {
	VariantType n[] = VariantType.getPrioritySortedList();
	for (int i=1;i<n.length;++i) {
	    Assert.assertTrue(VariantType.priorityLevel(n[i-1]) <= VariantType.priorityLevel(n[i]));
	}
    }

    @Test public void testFSDeletionString() 
    {
	String s = VariantType.FS_DELETION.toDisplayString();
	Assert.assertEquals("frameshift truncation",s);
    }

    @Test public void isTopPriorityTest1() {
	boolean b = VariantType.MISSENSE.isTopPriorityVariant();
	Assert.assertEquals(true,b);
    }

     @Test public void isTopPriorityTest2() {
	boolean b = VariantType.UTR5.isTopPriorityVariant();
	Assert.assertEquals(false,b);
     }
    
    @Test public void isTopPriorityTest3() {
	boolean b = VariantType.DOWNSTREAM.isTopPriorityVariant();
	Assert.assertEquals(false,b);
    }




}
/* eof*/