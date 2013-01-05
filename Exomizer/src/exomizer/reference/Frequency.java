package exomizer.reference;


import exomizer.common.Constants;



/**
 * This class is meant to be used for parsing dbSNP data and ESP data to 
 * get information about the population frequency of variants. Objects of this
 * class will be created by the parsers {@link exomizer.io.dbSNP2SQLDumpParser dbSNP2SQLDumpParser}
 * as well as {@link exomizer.io.ESP2SQLDumpParser ESP2SQLDumpParser} during parsing, and
 * matching variants will be combined. Finally, objects of this class know how to 
 * write themselves as a line of the postgres Dump File.
 * <P>
 * Note that this class implements {@code Comparable} because it is intended
 * to be used as an element of a {@code TreeSet} in the class {@link exomizer.dbSNP2SQL dbSNP2SQL}
 * in order to sort and search these objects while creating a dump file for postgreSQL.
 * @author Peter Robinson
 * @version 0.02 (8 January, 2013)
 */
public class Frequency implements Comparable<Frequency>, Constants {
    /** Byte representation of the chromosome */
    private byte chromosome;
    /** Start position of the variant on the chromosome */
    private int pos;
    /** Sequence (one or more nucleotides) of the reference */
    private String ref;
    /** Sequence (one or more nucleotides) of the alt (variant)  sequence */
    private String alt;
    /** Integer representation of the rsID */
    private int rsID;
    /** Float representation of dbSNP minor allele frequency (often form 1000G) */
    private float dbSNPmaf;
    /** Float representation of the ESP minor allele frequency for European Americans */
    private float espEA;
    /** Float representation of the ESP minor allele frequency for African Americans */
    private float espAA;
    /** Float representation of the ESP minor allele frequency for all comers */
    private float espAll;

    public Frequency(byte c, int p, String r, String a, int rs) {
	this.chromosome = c;
	this.pos = p;
	this.ref = r;
	this.alt = a;
	this.rsID = rs;
	this.dbSNPmaf = exomizer.common.Constants.UNINITIALIZED_FLOAT;
	this.espEA = exomizer.common.Constants.UNINITIALIZED_FLOAT;
	this.espAA = exomizer.common.Constants.UNINITIALIZED_FLOAT;
	this.espAll = exomizer.common.Constants.UNINITIALIZED_FLOAT;
    }



    public void set_dbSNP_GMAF(float maf) {
	this.dbSNPmaf = maf;
    }

    /**
     * @param f The minor allele frequency of this variant as found in the ESP data.
     */
    public void setESPFrequencyEA(float f) {
	this.espEA = f;
    }
    /**
     * @param f The minor allele frequency of this variant as found in the ESP data.
     */
    public void setESPFrequencyAA(float f) {
	this.espAA = f;
    }
    /**
     * @param f The minor allele frequency of this variant as found in the ESP data.
     */
    public void setESPFrequencyAll(float f) {
	this.espAll = f;
    }
	


    /**
     *   chromosome + "|" + position + "|" + ref + "|" + alt + "|" + 
	    rsid + "|" + dbSNPmaf + "|" + ESPmafEA + "|" + ESPmafAA+ "|" +  ESPmafAll;
    */
    public String getDumpLine() {
	String s = String.format("%d|%d|%s|%s|rs%d|%f|%f|%f|%f",this.chromosome,this.pos,this.ref,this.alt,this.rsID,this.dbSNPmaf,this.espEA,
				 this.espAA, this.espAll);
	return s;
    }

    /**
     * This method is implemented for the {@code Comparable} interface.
     * We sort frequency objects based on
     * <OL>
     * <LI>Chromosome
     * <LI>Position
     * <LI>Reference sequence
     * <LI>Alt sequence
     * </OL>
     */
    public int compareTo(Frequency f) {
	if (this.chromosome != f.chromosome) return (this.chromosome - f.chromosome);
	if (this.pos != f.pos) return (this.pos - f.pos);
	if (! this.ref.equals(f.ref)) return this.ref.compareTo(f.ref);
	return this.alt.compareTo(f.alt);
    }


}