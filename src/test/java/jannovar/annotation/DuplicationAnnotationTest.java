package jannovar.annotation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import jannovar.common.Constants;
import jannovar.common.VariantType;
import jannovar.exception.AnnotationException;
import jannovar.exception.JannovarException;
import jannovar.exception.VCFParseException;
import jannovar.exome.Variant;
import jannovar.genotype.GenotypeFactoryA;
import jannovar.genotype.SingleGenotypeFactory;
import jannovar.genotype.MultipleGenotypeFactory;
import jannovar.io.SerializationManager;
import jannovar.io.VCFLine;
import jannovar.reference.Chromosome;
import jannovar.reference.TranscriptModel;

/**
 * @author mjaeger
 * 
 */
public class DuplicationAnnotationTest implements Constants {

    private static HashMap<Byte, Chromosome> chromosomeMap = null;
    /** This is needed for the VCF line initialization. */
    private static GenotypeFactoryA genofactory=null;

    @BeforeClass public static void setUp() throws IOException, JannovarException {
	ArrayList<TranscriptModel> kgList = null;
	java.net.URL url = SynonymousAnnotationTest.class.getResource(UCSCserializationTestFileName);
	String path = url.getPath();
	SerializationManager manager = new SerializationManager();
	kgList = manager.deserializeKnownGeneList(path);
	chromosomeMap = Chromosome.constructChromosomeMapWithIntervalTree(kgList);
	genofactory = new MultipleGenotypeFactory();
	VCFLine.setGenotypeFactory(genofactory);
    }

    @AfterClass public static void releaseResources() {
	chromosomeMap = null;
	System.gc();
    }

    /**
     * <P>
     * This is the test for the in-frame duplication of a single triplicate /
     * one amino acids '+' strand
     * </P>
     * Mutalyzer:
     * NM_001005495(OR2T3_v001):c.769_771dup
     * NM_001005495(OR2T3_i001):p.(Phe257dup)
     */
    @Test public void testDuplicationVar1() throws JannovarException {
	String s = "1	248637422	.	C	CTTC	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
	VCFLine line = new VCFLine(s);
	Variant v = line.toVariant();
	byte chr = 1;
	int pos = v.get_position();
	String ref = v.get_ref();
	String alt = v.get_alt();
	Chromosome c = chromosomeMap.get(chr);
	if (c == null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos, ref, alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(248637422,pos);
	    Assert.assertEquals("-",ref);
	    Assert.assertEquals("TTC",alt);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
	    Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.769_771dupTTC:p.F257dup)", annot);
	}
    }

    /**
 *<P>
 * annovar: FRG1:uc003izs.3:exon6:c.439_440insA:p.M147fs,
 * chr4:190878559->A
 * FRG1 is on the "+" strand
 * Jannovar says: FRG1(uc003izs.3:exon6:c.438dupA:p.M147fs)
 * expected
 * <...c003izs.3:exon6:c.43[9]dupA:p.M147fs)> 
 * but was:
 * <...c003izs.3:exon6:c.43[8]dupA:p.M147fs)>
 * is  uc003izs.3 NM_004477.2 
 * Mutalyzer says
 * NM_004477.2(FRG1_v001):c.439dup
 * NM_004477.2(FRG1_i001):p.(Met147Asnfs*8)
 * Raw variant 1: duplication from 630 to 630
 * GAACCAGTCTTTCAAAATGGGAAAA - TGGCTTTGTTGGCCTCAAATAGCTG
 * GAACCAGTCTTTCAAAATGGGAAAA A TGGCTTTGTTGGCCTCAAATAGCTG 
 * Thus, 439 and not 438 is the correct number for the duplicated nucleotide.
 * Jannovar lists refvarstart as 630. This is the last "A" of a polyA tract in 
 * the gene (see genbank  L76159.1).
 * Jannovar lists refcdsstart as 192. This is the position of the start of the
 * start codon in FRG1 (L76159.1).
 <...c003izs.3:exon6:c.43[9]dupA:p.M147fs)> but was:
 <...c003izs.3:exon6:c.43[8]dupA:p.M147fs)>

 *</P>
*/
    @Test public void testInsertionVar29y() throws AnnotationException,VCFParseException  {
	String s = "4	190878559	.	A	AA	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
	VCFLine line = new VCFLine(s);
	
	Variant v = line.toVariant();
	byte chr = 4;
	int pos = v.get_position();
	String ref = v.get_ref();
	String alt = v.get_alt();
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    VariantType varType = ann.getVariantType();
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals(190878559,pos);
	    Assert.assertEquals("-",ref);
	    Assert.assertEquals("A",alt);
	    Assert.assertEquals(VariantType.FS_DUPLICATION,varType);
	    Assert.assertEquals("FRG1(uc003izs.3:exon6:c.439dupA:p.M147fs)",annot);
	}
    } 

    
    /**
     * <P>
     * This is the test for the in-frame duplication of a single triplicate /
     * one amino acids
     * '+' strand
     * </P>
     */
    @Test public void testDuplicationVar9test() throws AnnotationException,VCFParseException
    {
	String s = "9	137968918	.	A	AAGA	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
	VCFLine line = new VCFLine(s);
	Variant v = line.toVariant();
	byte chr = 9;
	int pos = v.get_position();
	String ref = v.get_ref();
	String alt = v.get_alt();
	Chromosome c = chromosomeMap.get(chr); 
	if (c==null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann =c.getAnnotationList(pos,ref,alt); 
	    ArrayList<Annotation> lst = ann.getAnnotationList();
	    Assert.assertEquals(137968918,pos);
	    Assert.assertEquals("-",ref);
	    Assert.assertEquals("AGA",alt);
	    VariantType varType = ann.getVariantType();
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals(VariantType.NON_FS_DUPLICATION,varType);
	    Assert.assertEquals("OLFM1(uc010naq.2:exon2:c.325_327dupAGA:p.R109dup)",annot);
	}
    }

    /**
     * <P>
     * This is the test for the in-frame duplication of six nuc.acids / two
     * amino acids '+' strand
     * </P>
     * Mutalyzer:
     * NM_001005495.1(OR2T3_v001):c.766_771dup
     * NM_001005495.1(OR2T3_i001):p.(Leu256_Phe257dup)
     *
     * 
     * <...6_771dupCTCTTC:p.L25[6_F257]dup)> but was:
     * <...6_771dupCTCTTC:p.L25[4_F256]dup)>

     */
    @Test public void testDuplicationVar2() throws JannovarException {
	String s = "1	248637422	.	C	CCTCTTC	100	PASS	QD=11.71;	GT:GQ	0/1:99	0/0:99	0/1:99	0/0:99	0/1:99";
	VCFLine line = new VCFLine(s);
	Variant v = line.toVariant();

	byte chr = 1;
	int pos = v.get_position();
	String ref = v.get_ref();
	String alt = v.get_alt();
	Chromosome c = chromosomeMap.get(chr);
	if (c == null) {
	    Assert.fail("Could not identify chromosome \"" + chr + "\"");
	} else {
	    AnnotationList ann = c.getAnnotationList(pos, ref, alt);
	    VariantType varType = ann.getVariantType();
	    Assert.assertEquals(248637422,pos);
	    Assert.assertEquals("-",ref);
	    Assert.assertEquals("CTCTTC",alt);
	    String annot = ann.getVariantAnnotation();
	    Assert.assertEquals(VariantType.NON_FS_DUPLICATION, varType);
	    Assert.assertEquals("OR2T3(uc001iel.1:exon1:c.766_771dupCTCTTC:p.L256_F257dup)", annot);
	}
    }


}
/* eof. */
