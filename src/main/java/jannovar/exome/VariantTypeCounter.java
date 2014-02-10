package jannovar.exome;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import jannovar.common.Constants;
import jannovar.common.VariantType;
import jannovar.exception.JannovarException;
import jannovar.genotype.GenotypeCall;

/**
 * This class is intended to provide a simple way of counting up all of the
 * variants found in an exome being analyzed and to provide a method to
 * display these results as HTML or in a table.
 * @author Peter N Robinson
 * @version 0.16 (29 December,2013)
 */

public class VariantTypeCounter implements Constants {
      /** key is a VariantType (e.g., MISSENSE, UTR5) and value is the corresponding index
       * in {@link #countMatrix}. */
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
     * @param n the nth element
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
     * @throws jannovar.exception.JannovarException
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
     * @param v the {@link Variant}
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



    /**
     * Counts the types of {@link Variant}s.
     * @param variantList list of {@link Variant}s
     * @throws JannovarException 
     */
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

    /** This class implements an iterator over
     * VariantType objects. The order is guaranteed to be
     * the prioritized order as defined in the VariantType class
     * of Jannovar. This is intended to be use by client code
     * to get a list of VariantTypes in order that will allow
     * HTML rows of variant counts, together with 
     * {@link #getTypeSpecificCounts}.
     */
    class VariantTypeIterator implements Iterator<VariantType> {
	private final int max;
	private int i;
	VariantType[] vta;
	VariantTypeIterator()   {
	    i=0;
	    this.vta = VariantType.getPrioritySortedList();
	    max = vta.length;
	}
	
	@Override public boolean hasNext() {
	    return (i<max);
	}

	@Override public VariantType next(){
	    VariantType vt = vta[i];
	    i++;
	    return vt;
	}

	@Override public void remove() {
	    throw new UnsupportedOperationException();
	}
    }


    public Iterator<VariantType> getVariantTypeIterator() {
	return new VariantTypeIterator();
    }

    /**
     * Returns the number of counts each person in the VCF file
     * has for a specific VariantType (such as MISSENSE, or UTR3).
     * The order of the entries is the same as the order of
     * entries in the VCF file.
     * @param vt the {@link VariantType}
     * @return count of this {@link VariantType}
     */
    public ArrayList<Integer> getTypeSpecificCounts(VariantType vt) {
	int idx =  this.variantTypeInd.get(vt);
	ArrayList<Integer> cts = new ArrayList<Integer>();
	for (int k=0;k<this.n_persons;++k) {
	    cts.add(this.countMatrix[k][idx]);
	}
	return cts;
    }
}

/* eof.*/
