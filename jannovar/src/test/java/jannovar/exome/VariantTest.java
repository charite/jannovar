package jannovar.io;




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
 * 10 May, 2013.
 */
public class VariantTest {

    @Test public void testPosition() {
	Variant v = new Variant();
	v.setPosition(13);
	int p = v.get_position();
	Assert.assertEquals(13,p);
    }

    @Test public void testChromosome1() throws VCFParseException {
	Variant v = new Variant();
	v.setChromosome("chr5");
        String c= v.get_chrom_string();
	Assert.assertEquals("chr5",c);
    }

    @Test public void testChromosomeX() throws VCFParseException {
	Variant v = new Variant();
	v.setChromosome("chrX");
        String c= v.get_chrom_string();
	Assert.assertEquals("chrX",c);
	Assert.assertEquals(true,v.is_X_chromosomal());
    }

    @Test public void testChromosomeY() throws VCFParseException {
	Variant v = new Variant();
	v.setChromosome("chrY");
        String c= v.get_chrom_string();
	Assert.assertEquals("chrY",c);
	Assert.assertEquals(false,v.is_X_chromosomal());
    }


}