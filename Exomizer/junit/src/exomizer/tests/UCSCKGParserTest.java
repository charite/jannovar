package exomizer.tests;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;

import java.util.ArrayList;


import exomizer.io.KGLine;
import exomizer.io.UCSCKGParser;
import exomizer.common.Constants;



import org.junit.Test;
import org.junit.Before;
import junit.framework.Assert;


public class UCSCKGParserTest implements Constants {

    private UCSCKGParser parser = null;
    private ArrayList<KGLine> knownGeneList=null;

    @Before public void setUp() throws IOException {
	System.out.println("ABout");
	File tmp = File.createTempFile("ucsckg-test","ucsckg-test");
	PrintStream ps = new PrintStream(new FileOutputStream(tmp));
	ps.append("uc009vis.3	chr1	-	14361	16765	14361	14361	4	14361,14969,15795,16606,	14829,15038,15942,16765,		uc009vis.3\n");
	ps.append("uc009viv.2	chr1	-	14406	29370	14406	14406	7	14406,16857,17232,17605,17914,24737,29320,	16765,17055,17368,17742,18061,24891,29370,		uc009viv.2\n");
	ps.close();
	//String mypath="/home/peter/data/ucsc/knownGene.txt";
	parser = new UCSCKGParser(tmp.getAbsolutePath());
	parser.parseFile();
	this.knownGeneList = parser.getKnownGeneList();
    }

    @Test public void testSizeOfKGList() {

	int N = this.knownGeneList.size();
	Assert.assertEquals(2,N);	  
    }

    /**
     *uc009vis.3 is the gene WASH7P and has a lenght of 843 nt. */
    @Test public void testGene_uc009vis() {
	KGLine kgl = knownGeneList.get(0);
	int len = kgl.getMRNALength();
	Assert.assertEquals(843,len);

    }




}

