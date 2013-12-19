package jannovar.io;


import jannovar.genotype.GenotypeCall;

import org.junit.Test;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Assert;


/**
 * Note that this class inputs the file exomizer/data/TestExome.vcf, which is a small
 * excerpt of the Manuel Corpas VCF file for the f1000 research article.
 * <p>
 * Refactored on 13 April, 2013 to take changes in the API of VCFLine into account.
 * <P>
 * The test cases were made by examining some of the lines from that file.
 */
public class VCFLineTest {


     @Test public void testGetReferenceSequence() throws VCFParseException 
    {
	
	Assert.assertEquals(2,1);
    }


}