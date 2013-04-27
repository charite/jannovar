package jannovar.exome;

import java.util.HashMap;
import java.io.Writer;
import java.io.IOException;

import jannovar.common.Constants;
import jannovar.common.VariantType;

/**
 * This class is intended to provide a simple way of counting up all of the
 * variants found in an exome being analyzed and to provide a method to
 * display these results as HTML or in a table.
 * @author Peter N Robinson
 * @version 0.05 (28 April, 2013)
 */

public class VariantTypeCounter implements Constants {

    private HashMap<VariantType,Integer> variantCountMap=null;

    /**
     * Construct the map of variant type counts and initialize it to zero.
     */
    public VariantTypeCounter() {
	variantCountMap = new HashMap<VariantType,Integer>();
	for (VariantType vt : VariantType.values()) {
	    variantCountMap.put(vt,0);
	}
    }

    /**
     * Increment the count for the VariantType in question by one. Note that 
     * because of the constructor, all possible VariantTypes are guaranteed to 
     * already be in the HashMap before this method gets called.
     * @param vt the VariantType (e.g., MISSENSE) to be incremented
     */
    public void incrementCount(VariantType vt) {
	Integer i = this.variantCountMap.get(vt);
	this.variantCountMap.put(vt,i+1);
    }

    /**
     * @param sampleName The name of the exome sample as given in the VCF file.
     * @param out a java.io.Writer handle (can be either BufferedWriter or StringWriter)
     */
    public void writeSummary(String sampleName, Writer out) throws IOException {
	out.write("<a name=\"#Distribution\">\n"+
		  "<h2>Distribution of Variant Types</h2>\n"+
		  "</a>\n");
	out.write("<table id=\"ztable\">\n");
	out.write("<thead><tr>\n");
	out.write("<th>Sample</th>");
	out.write("<th>NS/SS/I</th>");
	out.write("<th>Deemed nonpathogenic</th>");
	out.write("</tr></thead>\n");
	out.write("<tbody>\n");
	int missense = this.variantCountMap.get(VariantType.MISSENSE);
	int nonsense = this.variantCountMap.get(VariantType.STOPGAIN);
	int frameshift = this.variantCountMap.get(VariantType.FS_INSERTION) + this.variantCountMap.get(VariantType.FS_DELETION);
	int splice = this.variantCountMap.get(VariantType.SPLICING);
	int nonfs = this.variantCountMap.get(VariantType.NON_FS_SUBSTITUTION) +
	    this.variantCountMap.get(VariantType.NON_FS_DELETION) +
	    this.variantCountMap.get(VariantType.NON_FS_INSERTION);
	String nsssiCell = String.format("<tr><th>%s</th><td><ul><li>Nonsynonymous: %d</li><li>nonsense: %d</li>"+
					 "<li>frameshift: %d</li><li>splice: %s</li>"+
					 "<li>non-fs indel: %d</li></ul></td>", sampleName, missense,nonsense,frameshift,splice,nonfs);

	out.write(nsssiCell);
	outputNonpathogenicTableCell(out);
	out.write("</tr></tbody>\n");

	out.write("</table><p>&nbsp;</p>\n");


    }


    /**
     * Write an unordered list with the variants deeemed to be nonpathogenic.
     */
    private void outputNonpathogenicTableCell(Writer out) throws IOException {
	
	int ncrna = this.variantCountMap.get(VariantType.ncRNA_EXONIC) +
	    this.variantCountMap.get(VariantType.ncRNA_SPLICING) + 
	    this.variantCountMap.get(VariantType.ncRNA_UTR3) +
	    this.variantCountMap.get(VariantType.ncRNA_UTR5);
	int intron = this.variantCountMap.get(VariantType.INTRONIC) +
	    this.variantCountMap.get(VariantType.ncRNA_INTRONIC);
	int upstream = this.variantCountMap.get(VariantType.UPSTREAM);
	int downstream = this.variantCountMap.get(VariantType.DOWNSTREAM);
	int intergen =  this.variantCountMap.get(VariantType.INTERGENIC);
	int utr = this.variantCountMap.get(VariantType.UTR3) + 
	    this.variantCountMap.get(VariantType.UTR5) +
	    this.variantCountMap.get(VariantType.UTR53);
	int synonym = this.variantCountMap.get(VariantType.SYNONYMOUS);
	int total = ncrna + intron + upstream + downstream + intergen + utr + synonym;
	int unknown = this.variantCountMap.get(VariantType.UNKNOWN);
	int posErr = this.variantCountMap.get(VariantType.ERROR);

	out.write("<td><ul>\n");

	out.write(String.format("<li>ncRNA: %d</li>\n<li>intronic: %d</li>",ncrna, intron));
	out.write(String.format("<li>upstream: %d</li>\n<li>downstream: %d</li>",upstream,downstream));
	out.write(String.format("<li>intergenic: %d</li>\n<li>UTR3/UTR5: %d</li>",intergen,utr));
	out.write(String.format("<li>Synonymous: %d</li>\n",synonym));
	out.write(String.format("<li>Total: %d</li>\n",total));
	if (unknown>0)
	    out.write(String.format("<li>Unknown: %d</li>\n",unknown));
	if (posErr>0)
	    out.write(String.format("<li>Possible annotation errors: %d</li>\n",posErr));
	out.write("</ul></td>\n");

    }




}

/**
Have: SPLICING, STOPGAIN,FS_DELETION, FS_INSERTION,MISSENSE, ncRNA_EXONIC, ncRNA_INTRONIC, ncRNA_SPLICING,ncRNA_UTR5,NON_FS_SUBSTITUTION


  public static enum VariantType { DOWNSTREAM, EXONIC,  NON_FS_SUBSTITUTION,
	    FS_SUBSTITUTION , INTERGENIC, INTRONIC, ,
	      NON_FS_DELETION , NON_FS_INSERTION, 

	    STOPLOSS, SYNONYMOUS, UNKNOWN, UPSTREAM, UTR3, UTR5, UTR53,POSSIBLY_ERRONEOUS};
*/