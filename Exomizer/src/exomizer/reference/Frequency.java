package exomizer.reference;


import exomizer.common.Constants;



/**
 * This class is meant to be used for parsing dbSNP data and ESP data to 
 * get information about the population frequency of variants. Objects of this
 * class will be created by the parsers {@link exomizer.io.dbSNP2FrequencyParser dbSNP2FrequencyParser}
 * as well as {@link exomizer.io.ESP2FrequencyParser ESP2FrequencyParser} during parsing, and
 * matching variants will be combined. Finally, objects of this class know how to 
 * write themselves as a line of the postgres Dump File.
 * <P>
 * The frequencies (from dbSNP and ESP) are both stored as percentages. Note that the files
 * downloaded from ESP expressed the MAF (minor allele frequency) as a percentage, whereas
 * the files from dbSNP use a proportion. The code in 
 * {@link exomizer.io.dbSNP2FrequencyParser dbSNP2FrequencyParser} therefore converts the
 * data in dbSNP to percentages for uniformity's sake.
 * <P>
 * Note that this class implements {@code Comparable} because it is intended
 * to be used as an element of a {@code TreeSet} in the class {@link exomizer.dbSNP2SQL dbSNP2SQL}
 * in order to sort and search these objects while creating a dump file for postgreSQL.
 * @author Peter Robinson
 * @version 0.04 (8 February, 2013)
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
    /** Float representation of dbSNP minor allele frequency (from 1000G phase I via dbSNP), 
     * expressed as a percentage */
    private float dbSNPmaf;
    /** Float representation of the ESP minor allele frequency for European Americans,
      * expressed as a percentage **/
    private float espEA;
    /** Float representation of the ESP minor allele frequency for African Americans,
     * expressed as a percentage **/
    private float espAA;
    /** Float representation of the ESP minor allele frequency for all comers,
      * expressed as a percentage **/
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


    /** 
     * Sets the frequency (expressed as percent) in dbSNP data
     * of the current variant.
     * Note that client code is expected to transform propoprtions, i.e.
     * value \in [0,1] into percentages, i.e., value \in [0,100] before
     * calling this method.
     * @param maf The minor allele frequency, expressed as a percentage.
     */
    public void set_dbSNP_GMAF(float maf) {
	this.dbSNPmaf = maf;
    }

    /**
     * Sets the frequency (expressed as percent) in the ESP data of the current variant.
     * Note that the ESP MAF is expressed in percentage in the original files, and thus,
     * the parameter is a value \in [0,100]
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
     *   This method is used to create a single line of the file we will 
     * import into the postgreSQL database (table: frequency). The structure of the
     * line is 
     * <P>
     * chromosome | position | ref | alt | rsid | dbSNPmaf | ESPmafEA | ESPmafAA | ESPmafAll;
     * <P>
     * Note that the rsID is printed as an integer and that client code will need to add the "rs"
     * and transform it into a String.
     */
    public String getDumpLine() {
	String s = String.format("%d|%d|%s|%s|%d|%f|%f|%f|%f",this.chromosome,this.pos,this.ref,this.alt,this.rsID,this.dbSNPmaf,this.espEA,
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