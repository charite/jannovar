package jannovar.exome;




import java.io.File;
import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException; 
import java.util.ArrayList;

import jannovar.exception.VCFParseException;
import jannovar.exome.Variant;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;


/**
 * VariantTest
 * 16 August, 2013.
 */
public class VariantTest {

    /** We need this for the constructor but it is not
	used by most tests. */
    public int dummyPhred=100;

    @Test public void testPosition() {
	byte chr = 3;
	Variant v = new Variant(chr,13,"A","C",null,dummyPhred);
	int p = v.get_position();
	Assert.assertEquals(13,p);
    }

    @Test public void testChromosome1() throws VCFParseException {
	byte chr5 = 5;
	Variant v = new Variant(chr5,13,"A","A",null,dummyPhred);
        String c= v.get_chromosome_as_string();
	Assert.assertEquals("chr5",c);
    }

    @Test public void testChromosomeX() throws VCFParseException {
	byte chrX = 23;
	Variant v = new Variant(chrX,13,"A","C",null,dummyPhred);

        String c= v.get_chromosome_as_string();
	Assert.assertEquals("chrX",c);
	Assert.assertEquals(true,v.is_X_chromosomal());
    }

    @Test public void testChromosomeY() throws VCFParseException {
	byte chrY = 24;
	Variant v = new Variant(chrY,13,"A","C",null,dummyPhred);

        String c= v.get_chromosome_as_string();
	Assert.assertEquals("chrY",c);
	Assert.assertEquals(false,v.is_X_chromosomal());
    }

     @Test public void testTransition1() {
	byte chr = 3;
	Variant v = new Variant(chr,13,"A","G",null,dummyPhred);
	boolean b = v.isTransition();
	Assert.assertEquals(true,b);
    }

    /* A<->T is not a transition */
     @Test public void testTransition2() {
	byte chr = 3;
	Variant v = new Variant(chr,13,"A","T",null,dummyPhred); 
	boolean b = v.isTransition();
	Assert.assertEquals(false,b);
    }

    /* A<->GC is not a transition because not a SNV */
     @Test public void testTransition3() {
	byte chr = 3;
	Variant v = new Variant(chr,13,"A","GC",null,dummyPhred); 
	boolean b = v.isTransition();
	Assert.assertEquals(false,b);
    }

     @Test public void testTransversion1() {
	byte chr = 3;
	Variant v = new Variant(chr,13,"C","G",null,dummyPhred);
	boolean b = v.isTransversion();
	Assert.assertEquals(true,b);
    }

      @Test public void testTransversion2() {
	byte chr = 3;
	Variant v = new Variant(chr,13,"A","T",null,dummyPhred);
	boolean b = v.isTransversion();
	Assert.assertEquals(true,b);
    }

     @Test public void testTransversion3() {
	byte chr = 3;
	Variant v = new Variant(chr,13,"C","T",null,dummyPhred);
	boolean b = v.isTransversion();
	Assert.assertEquals(false,b);
    }


}