package jannovar.exome;


import java.io.Writer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;


import jannovar.common.Constants;
import jannovar.common.VariantType;
import jannovar.exception.JannovarException;
import jannovar.exome.Variant;
import jannovar.genotype.GenotypeCall;

/**
 * This class is intended to provide a simple way of counting up all of the
 * variants found in an exome being analyzed and to provide a method to
 * display these results as HTML or in a table.
 * @author Peter N Robinson
 * @version 0.12 (18 November,2013)
 */

public class VariantTypeCounter implements Constants {

    private HashMap<VariantType,Integer> variantCountMap=null;

    private HashMap<VariantType,Integer> variantTypeInd=null;


    /** The first dimension (rows) represents the samples, the
     * second dimension (columns) represents the variant types.
     * Thus, countMatrix[i][j] represents the count in sample
     * i for variantType j. Note that the indices for the 
     * variantTypes are stored in the HashMap 
     * {@link #variantTypeInd}.
     */
    private int[][] countMatrix=null;
    /** Number of persons represented in the VCF file. */
    private int n_persons;
    /** Number of variant types */
    private int n_var_types;


    /**
     * Disallow construction of an object with no parameters.
     */
    private VariantTypeCounter() {

    }

    /**
     * Construct the map of variant type counts and initialize it to zero.
     */
    public VariantTypeCounter(int n) {
	this.n_var_types = VariantType.size();
	this.n_persons = n;
	/* Note that the following command automatically sets all of the values to zero. */
	this.countMatrix = new int[this.n_persons][this.n_var_types];
	initializeVarTypeIndices();
    }

    

    /**
     * The constructor takes a list of all variants found in 
     * the VCF file and generates a list of counts, one for each
     * variant type.
     * @param variantList List of all variants found in the VCF file.
     */
    public VariantTypeCounter(ArrayList<Variant> variantList) throws JannovarException {
	this.n_var_types = VariantType.size();
	
	this.n_persons = variantList.get(0).getGenotype().getNumberOfIndividuals();
	/* Note that the following command automatically sets all of the values to zero. */
	this.countMatrix = new int[this.n_persons][this.n_var_types];
	initializeVarTypeIndices();
	countVariants(variantList);
    }

    /**
     * Increment the counts for the VariantType represented
     * by this Variant. Note that we extract the 
     * GenotypeCall for all persons in the VCF file, and update
     * the corresponding fields in 
     * {@link #countMatrix}.
     */
    public void incrementCount(Variant v) {
	VariantType vtype = v.getVariantTypeConstant();
	GenotypeCall gtc = v.getGenotype();
	int vtypeIndex = this.variantTypeInd.get(vtype);
	for (int i=0; i<this.n_persons;++i) {
	    if (gtc.isALTInIndividualN(i)){
		this.countMatrix[i][vtypeIndex]++;
	    }
	}
    }



    private void countVariants(ArrayList<Variant> variantList) throws JannovarException {
	int N = variantList.size();
	for (int j=0;j<N;++j) {
	    Variant v = variantList.get(j);
	    VariantType vt = v.getVariantTypeConstant();
	    int vtypeIndex = this.variantTypeInd.get(vt);
	    GenotypeCall gtc = v.getGenotype();
	    for (int i=0; i<this.n_persons;++i) {
		if (gtc.isALTInIndividualN(i)){
		    this.countMatrix[i][vtypeIndex]++;
		}
	    }
	}
    }

    /**
     * We store the indices of the VariantTypes in the
     * HashMap {@link #variantTypeInd}. For instance,
     * FS_INSERTION might have the index 5. This function
     * initializes that HashMap.
     */
    private void initializeVarTypeIndices() {
	this.variantTypeInd=new HashMap<VariantType,Integer>();
	VariantType[] vtypes = VariantType.getPrioritySortedList();
	for (int i=0;i<vtypes.length;++i) {
	    this.variantTypeInd.put(vtypes[i],i);
	}
    }


    /**
     * This will write the summary of variants using as sample
     * names "sample 1", "sample 2", etc.
     */
    public void writeSummaryTable(Writer out) 
	throws IOException, JannovarException
    {
	 ArrayList<String> lst = new ArrayList<String>();
	 for (int i=0;i<this.n_persons;++i) {
	     String s = String.format("sample %d",i+1);
	     lst.add(s);
	 }
	 writeSummaryTable(lst,out);
     }


     public void writeSummaryTable(String sampleName, Writer out) 
	 throws IOException, JannovarException 
    {
	 ArrayList<String> lst = new ArrayList<String>();
	 lst.add(sampleName);
	 writeSummaryTable(lst,out);
     }

    public void writeSummaryTable(ArrayList<String> sampleNames, Writer out) 
	throws IOException, JannovarException 
    {
	int ncol = sampleNames.size();
	if (ncol != this.n_persons) {
	    String s = "Error: Attempt to write variant distribution table for " +
		ncol + " samples but data was entered for " + this.n_persons + " persons";
	    throw new JannovarException(s);
	}
	VariantType[] vta = VariantType.getPrioritySortedList();
	out.write("<a name=\"Distribution\">\n"+
		  "<h2>Distribution of Variant Types</h2>\n"+
		  "</a>\n");
	out.write("<table id=\"variantDistribution\">\n");
	out.write("<thead><tr>\n");
	out.write("<th>Variant Type</th>");
	for (int i=0;i<ncol;i++) {
	    out.write(String.format("<th>%s</th>",sampleNames.get(i)));
	}
	out.write("</tr></thead>\n");
	out.write("<tbody>\n");
	for (int i=0;i<vta.length;++i) {
	    out.write(String.format("<tr><td>%s</td>", VariantType.variantTypeAsString(vta[i])));
	    for (int k=0;k<ncol;++k) {
		out.write(String.format("<td>%d</td>",this.countMatrix[k][i]));
	    }
	    out.write("</tr>\n");
	}
	out.write("</tbody>\n</table><p>&nbsp;</p>\n");
    }

}

/* eof.*/
