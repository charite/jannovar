package jannovar.io;

import jannovar.exception.VCFParseException;
import jannovar.genotype.GenotypeCall;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Note that this class inputs the file exomizer/data/TestExome.vcf, which is a small excerpt of the Manuel Corpas VCF
 * file for the f1000 research article.
 * <p>
 * Refactored on 13 April, 2013 to take changes in the API of VCFLine into account.
 * <P>
 * The test cases were made by examining some of the lines from that file.
 */
public class VCFLineTest {
	/** This is the tolerance for checking equality of floating point numbers for junit. */
	private float EPSILON = 0.000001f;

	private static VCFReader reader = null;
	private static String vcfPath = "src/test/resources/TestExome.vcf";
	private static ArrayList<VCFLine> VCFLineList = null;

	@BeforeClass
	public static void setUp() throws IOException, VCFParseException {
		reader = new VCFReader(vcfPath);
		reader.parseFile();
		VCFLineList = new ArrayList<VCFLine>();
		FileInputStream fstream = new FileInputStream(vcfPath);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line;

		while ((line = br.readLine()) != null) {
			if (line.isEmpty())
				continue;
			if (line.startsWith("#"))
				continue;
			VCFLine ln = new VCFLine(line);
			VCFLineList.add(ln);
		}
		br.close();
		in.close();
		fstream.close();
	}

	@AfterClass
	public static void releaseResources() {
		VCFLineList = null;
		System.gc();
	}

	/**
	 * Line 0 has
	 * 
	 * <pre>
	 * 1       866511  rs60722469      C       CCCCT
	 * </pre>
	 * 
	 * Since the C at 866511 is wildtype, the insertion is from "-" to CCCT
	 */
	@Test
	public void testGetReferenceSequence() throws VCFParseException {
		VCFLine line = VCFLineList.get(0);
		String ref = line.get_reference_sequence();
		Assert.assertEquals("-", ref);
		String alt = line.get_alternate_sequence();
		Assert.assertEquals("CCCT", alt);
		int pos = line.get_position();
		Assert.assertEquals(866511, pos);
	}

	/**
	 * 1 879317 rs7523549 C T 150.77
	 * */

	@Test
	public void testGetPosition() throws VCFParseException {
		VCFLine line = VCFLineList.get(1);
		int pos = line.get_position();
		Assert.assertEquals(879317, pos);
		byte chr = line.get_chromosome();
		Assert.assertEquals((byte) 1, chr);
		String alt = line.get_alternate_sequence();
		Assert.assertEquals("T", alt);
	}

	/**
	 * Line 2 is ...0/1:28,20:48:99:515,0,794 Thus, we expect "0/1"
	 */
	@Test
	public void testIsGenotype() {
		VCFLine line = VCFLineList.get(2);
		GenotypeCall GI = line.getGenotype();
		String gt = GI.get_genotype_as_string();
		Assert.assertEquals("0/1", gt);

	}

	// The following tests that the expected Exceptions are thrown.
	/** Bad line because there is no position (needs to be an int) */
	private String badline1 = "chr1	.	.	T	C	33.7	.	" + "EFFECT=missense;HGVS=OR4F5:NM_001005484:exon1:c.479T>C:p.L160P,;DP=7;VDB=0.0399;" + "AF1=0.7867;AC1=10;DP4=1,0,0,5;MQ=31;FQ=6.48;PV4=0.17,1e-05,0.013,1	GT:PL:GQ	" + "1/1:0,0,0:3	1/1:20,3,0:5	0/1:0,3,41:5";
	/** Bad line because alt sequence is missing (".") */
	private String badline2 = "chr9	125391241	.	G	.	999	.	" + "DP=64;VDB=0.0324;" + "AF1=0.4991;G3=2.124e-08,1,1.458e-37;HWE=0.00397;AC1=6;DP4=34,13,11,5;MQ=57;FQ=999;PV4=0.76,1,1,1	" + "GT:PL:GQ	0/1:17,0,193:20	0/1:82,0,114:85	0/1:49,0,204:52";
	/** Bad line because the reference sequence is missing (".") */
	private String badline3 = "chr20	44352740	.	G	.	163	.	" + "EFFECT=intronic;HGVS=SPINT4;DP=89;VDB=0.0115;AF1=0.5831;G3=2.72e-09,0.8334,0.1666;HWE=0.0467;" + "AC1=7;DP4=0,22,0,58;MQ=60;FQ=166;PV4=1,2.5e-106,0.19,1	GT:PL:GQ	0/1:34,0,93:38	0/1:39,0,93:43	" + "0/1:21,0,164:25";

	@Test(expected = VCFParseException.class)
	public void shouldChokeOnMalformedVCFLine1() throws VCFParseException {
		@SuppressWarnings("unused")
		VCFLine line = new VCFLine(badline1);
	}

	// @Test(expected = VCFParseException.class)
	// public void shouldChokeOnMalformedVCFLine2() throws VCFParseException {
	// VCFLine line = new VCFLine(badline2);
	// }
	//
	// @Test(expected = VCFParseException.class)
	// public void shouldChokeOnMalformedVCFLine3() throws VCFParseException {
	// VCFLine line = new VCFLine(badline3);
	// }

	/**
	 * The following tests are for indel notation in VCF The reference, GACACA, begins at position 1276973. The
	 * alternate sequence GACACACACA, shows that three additional CA dinucleotides have been inserted after the wildtype
	 * sequence GACACA.
	 */
	private String indelLine1 = "chr1	1276973	.	GACACA	GACACACACA	999	.	INDEL;DP=20;VDB=0.0141;AF1=1;AC1=12;DP4=0,0,19,0;MQ=34;FQ=-45.4	GT:PL:GQ	1/1:103,12,0:27	1/1:66,9,0:24	1/1:116,15,0:30";

	@Test
	public void testIndelLineChrom1() throws VCFParseException {
		VCFLine line = new VCFLine(indelLine1);
		Assert.assertEquals((byte) 1, line.get_chromosome());
	}

	@Test
	public void testIndelLineChrom2() throws VCFParseException {
		VCFLine line = new VCFLine(indelLine1);
		Assert.assertEquals(1276978, line.get_position());
	}

	@Test
	public void testIndelLineChrom3() throws VCFParseException {
		VCFLine line = new VCFLine(indelLine1);
		String r = line.get_reference_sequence();
		Assert.assertEquals("-", r);
		// Assert.assertEquals("TODO-- Figure out where to convert the VCF numbering",r);
	}

	/**
	 * The following test was added to check whether we are getting the PHRED variant quality correctly.
	 */
	private String qLine1 = "10	123256215	.	T	G	100	PASS	GENE=FGFR2;INHERITANCE=AD;MIM=101600	GT:DS:GL	1|0:2.000:-5.00,-1.10,-0.04";

	/**
	 * The following test was added to check whether we are getting the PHRED variant quality correctly.
	 */
	private String qLine2 = "10	123256215	.	T	G	168.56	PASS	GENE=FGFR2;INHERITANCE=AD;MIM=101600	GT:DS:GL	1|0:2.000:-5.00,-1.10,-0.04";

	@Test
	public void testQline2() throws VCFParseException {
		VCFLine line = new VCFLine(qLine2);
		float q = line.getVariantPhredScore();
		float delta = 0.001f;
		Assert.assertEquals(168.56, q, delta);
	}

	/**
	 * The following test was added to check whether we are getting the PHRED variant quality correctly.
	 */
	private String qLine3 = "10	123256215	.	T	G	227.23	PASS	GENE=FGFR2;INHERITANCE=AD;MIM=101600	GT:DS:GL	1|0:2.000:-5.00,-1.10,-0.04";

	@Test
	public void testQline3() throws VCFParseException {
		VCFLine line = new VCFLine(qLine3);
		float q = line.getVariantPhredScore();
		float delta = 0.001f;
		Assert.assertEquals(227.23, q, delta);
	}

}
