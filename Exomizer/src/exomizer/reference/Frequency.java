package exomizer.reference;


import exomizer.common.Constants;



/**
 * This class is meant to be used for parsing dbSNP data and ESP data to 
 * get information about the population frequency of variants. Objects of this
 * class will be created by the parsers {@link exomizer.io.dbSNP2SQLDumpParser dbSNP2SQLDumpParser}
 * as well as {@link exomizer.io.ESP2SQLDumpParser ESP2SQLDumpParser} during parsing, and
 * matching variants will be combined. Finally, objects of this class know how to 
 * write themselves as a line of the postgres Dump File.
 * @author Peter Robinson
 * @version 0.01 (28 December 2012)
 */
public class Frequency {
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
    /** Float representatrion of dbSNP minor allele frequency (often form 1000G) */
    private float dbSNPmaf;
    /** Float representation of the ESP minor allele frequency */
    private float ESPmaf;

    public Frequency(byte c, int p, String r, String a, int rs) {
	this.chromosome = c;
	this.pos = p;
	this.ref = r;
	this.alt = a;
	this.rsID = rs;
	this.dbSNPmaf = exomizer.common.Constants.UNINITIALIZED_FLOAT;
	this.ESPmaf = exomizer.common.Constants.UNINITIALIZED_FLOAT;
    }



    public void set_dbSNP_GMAF(float maf) {
	this.dbSNPmaf = maf;
    }
    /**
     *   chromosome + "|" + position + "|" + ref + "|" + alt + "|" + 
	    rsid + "|" + dbSNPmaf + "|" + ESPmaf;
    */
    public String getDumpLine() {
	String s = String.format("%d|%d|%s|%s|rs%d|%f|%f",this.chromosome,this.pos,this.ref,this.alt,this.rsID,this.dbSNPmaf,this.ESPmaf);
	return s;
    }


}