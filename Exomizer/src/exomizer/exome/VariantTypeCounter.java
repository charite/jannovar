package exomizer.exome;

import java.util.HashMap;
import java.io.BufferedWriter;
import java.io.IOException;

import exomizer.common.Constants;

/**
 * This class is intended to provide a simple way of counting up all of the
 * variants found in an exome being analyzed and to provide a method to
 * display these results as HTML or in a table.
 * @author Peter N Robinson
 * @version 0.01 (15 February, 2013)
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


    public void writeSummary(BufferedWriter out) throws IOException {
	out.write("<a name=\"#VariantClassSummary\">\n"+
		  "<h2>Distirubtion of Variant Types</h2>\n"+
		  "</a>\n");
	out.write("<table class=\"priority\">\n");
	out.write("<tr>\n");
	out.write("<th>Missense</th>");
	out.write("<th>Nonsense</th>");
	out.write("<th>Frameshift</th>");
	out.write("</tr>\n");
	int missense = this.variantCountMap.get(VariantType.MISSENSE);
	int nonsense = this.variantCountMap.get(VariantType.NONSENSE);
	int frameshift = this.variantCountMap.get(VariantType.FS_INSERTION);


	out.write(String.format("<tr><td>%d</td><td>%d</td><td>%d</td></tr>\n",missense,nonsense,frameshift));

	out.write("</table>\n");


    }


   




}