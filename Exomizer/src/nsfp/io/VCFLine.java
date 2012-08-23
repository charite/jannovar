package nsfp.io;

import nsfp.io.NSFP_Constants;

import java.util.MissingFormatArgumentException;
/**
 * Parse a VCF line and provide convenient access / getter functions.
 * TODO: Initialize this class dynamically to look just for the fields that
 * the VCF file really has. */
public class VCFLine implements NSFP_Constants {
    private String vcf_line = null;
    private String chromosome = null;
    private int position;
    /** Reference nucleotide or sequence */
    private String ref =null;
    /** Alternate nucleotide/sequence */
    private String alt=null;
    /** The type of this variant (initialize to UNKNOWN) */
    private int vtype=UNKNOWN;
    private String genename=null;
    private String refseq_mrna=null;
    private String exon=null;
    private String cds_mutation=null;
    private String aa_mutation=null;
    /** one of GENOTYPE_HOMOZYGOUS_REF,GENOTYPE_HOMOZYGOUS_VAR, GENOTYPE_HETEROZYGOUS or GENOTYPE_UNKNOWN */
    private int genotype= GENOTYPE_UNKNOWN;
    private int genotype_quality=GENOTYPE_UNKNOWN;
    /** in what field do the samples begin? */
    private static int begin_sample_index =-1;
    /** Does this VCFFile have a FORMAT field? */
    private static boolean has_format=false;
    
   
   
   

    /** getters */
    public String get_chromosome_as_string() { return this.chromosome; }
    public int get_position() { return this.position; }
    public String get_reference_sequence() { return this.ref; }
    public String get_alternate_sequence() { return this.alt; }
    public String get_variant_sequence() { return this.alt; }
    public int get_variant_type_as_int() { return this.vtype; }
    public int get_genotype() { return this.genotype; }
    public String get_genotype_as_string() {
	switch(this.genotype) {
	case GENOTYPE_HOMOZYGOUS_REF: return "homozygous reference"; 
	case GENOTYPE_HOMOZYGOUS_ALT: return "homozygous alt"; 
	case GENOTYPE_HETEROZYGOUS: return "heterozygous"; 
	default: return "unknown";
	}
    }
    public int get_genotype_quality() { return this.genotype_quality; }
    public boolean is_homozygous_alt() { return this.genotype == GENOTYPE_HOMOZYGOUS_ALT; }
    public boolean is_homozygous_ref() { return this.genotype == GENOTYPE_HOMOZYGOUS_REF; }
    public boolean is_heterozygous(){ return this.genotype == GENOTYPE_HETEROZYGOUS; }

    public String get_genename() { return genename != null ? genename : "-"; }
    public String get_refseq() { return refseq_mrna != null ? refseq_mrna : "-"; }
    public String get_exon() { return exon != null ? exon : "-"; }
    public String get_cds_mutation() { return cds_mutation != null ? cds_mutation : "-"; }
    public String get_aa_mutation() { return aa_mutation != null ? aa_mutation : "-"; }
    public boolean is_predicted_non_missense_path() {
	if (vtype == FS_DELETION) return true;
	if (vtype == FS_INSERTION) return true;
	if (vtype == NON_FS_SUBSTITUTION ) return true;
	if (vtype == FS_SUBSTITUTION) return true;
	if (vtype == NONSENSE) return true;
	if (vtype == SPLICING) return true;
	if (vtype == STOPGAIN) return true;
	if (vtype == STOPLOSS) return true;
	//if (vtype == UTR3) return true;
	return false;
    }

    public boolean is_SNV() { return vtype == MISSENSE; }

    /** Setters */
    public static void set_has_format(boolean hf) { VCFLine.has_format = hf; }
    public static void set_sample_begin_index(int i) { VCFLine.begin_sample_index = i; }
     

    /**
     * The CTOR parses the line, and then  everything is ready. */
    public VCFLine(String line) throws NumberFormatException, MissingFormatArgumentException {
	this.vcf_line = line;
	parse_line(line);
    }


     /**
     * Parse one line of a VCF file and decide whether this is a single nucleotide variant
     * The VCF format always has 8 fixed, mandatory columns. 
     * 0)CHROM  e.g., 1
     * 1)POS    e.g. 866511
     * 2)ID     e.g. rs60722469
     * 3)REF    e.g. C
     * 4)ALT    e.g. CCCCT
     * 5) QUAL  e.g.  258.62
     * 6)FILTER  e.g.  PASS
     * 7)INFO   e.g.   AC=2;AF=1.00;AN=2;DB;DP  (....)
     * All data lines are tab-delimited. In all cases, missing values are specified with a dot (”.”). 
     * Since the purpose of this code is to extract variants that we can parse with dbNSFP, we are only
     * interested in single nucleotide variants. We will thus demand that both REF and ALT single characters
     * Otherwise, return null, this is a flag to skip this line (for instance if the variant is an insertion).
     * @param line A single line of a VCF file representing one variant
     * @return a SNV object if parsing was successful and the variant is a single nucleotide variant, otherwise null.
     */
    private void parse_line(String line) throws NumberFormatException, MissingFormatArgumentException {
	String A[] = line.split("\t");
	if (A.length < 8) throw new MissingFormatArgumentException("Less than 8 fields in VCF line:" + line);
	this.ref = A[3];
	if (ref.equals(".")  || ref.length()<1 ) throw new MissingFormatArgumentException("Could not parse ref field: \"" +
											  ref + "\"\n" + line);
	this.alt = A[4]; 
	if (alt.equals(".")  || alt.length()<1 ) throw new MissingFormatArgumentException("Could not parse alt field:\"" +
											  alt + "\"\n" + line);
	this.chromosome = A[0];
	Integer pos = null;
	pos = Integer.parseInt(A[1]);
	this.position = pos.intValue();
	extract_INFO(A[7]);
	if (VCFLine.has_format) {
	    parse_genotype(A[8],A[VCFLine.begin_sample_index]);
	}
    }




     /** Info -line
     * May have annopvar-added annotations: EFFECT=missense;HGVS=GNB1:NM_002074:exon11:c.980T>C:p.V327A,
     * Always has fields as indicated in header.
     * TODO: Make more general. Right now, only the first variant is extracted.
     */
    private void extract_INFO(String info) {
	String A[] = info.split(";");
	for (String item : A) {
	    if (item.startsWith("EFFECT="))
		set_variant_type(item.substring(7)); //Extract part of string after EFFECT=
	    else if (item.startsWith("HGVS="))
		set_HGVS(item.substring(5));


	}
    }



     /**
     * We are expecting to get two fields from the VCF file, from which we will parse the genotype.
     * Note that for now we will not worry about genotype quality or about multiple sample fields representing
     * duplicate sequences.
     * @param format VCF FORMAT field, e.g., GT:PL:GQ	
     * @param sample1 VCF sample field, e.g., 1/1:21,9,0:17
     */
    private void parse_genotype(String format, String sample1) {
	String A[] = format.split(":");
	int gt_index = -1;
	int qual_idx = -1; //genotype quality
	for (int i=0;i<A.length; ++i) {
	    if (A[i].equals("GT")) { gt_index = i;}
	    if (A[i].equals("GQ")) { qual_idx =i; }
	}
	if (gt_index < 0) return; // Couldn't find genotype field.
	String B[] = sample1.split(":");
	String genotype = B[gt_index];
	if (genotype.equals("0/1"))
	    this.genotype = GENOTYPE_HETEROZYGOUS; 
	else if (genotype.equals("1/1"))
	    this.genotype = GENOTYPE_HOMOZYGOUS_ALT;
	else if (genotype.equals("0/0"))
	    this.genotype = GENOTYPE_HOMOZYGOUS_REF;
	if (qual_idx < 0) return;
	try {
	    genotype_quality = Integer.parseInt(B[qual_idx]);
	} catch (NumberFormatException e) {
	    System.err.println("Problem reading genotype quality field");
	    System.err.println(e);
	}

    }
    
    /** lines such as HGVS=HSPG2(NM_005529:exon94:c.12744+2T>C); */
     private void set_HGVS_by_paren(String hgvs) {
	 int i = hgvs.indexOf("(");
	 this.genename = hgvs.substring(0,i);
	 int j = hgvs.indexOf(")");
	 if (j<0) return;
	 hgvs = hgvs.substring(i+1,j);
	 /* The following takes just the first alternative */
	 String B[] = hgvs.split(",");
	 hgvs = B[0];
	 String A[] = hgvs.split(":");
	 if (A[0].startsWith("NM_"))
	     this.refseq_mrna=A[0];
	 if (A.length>1 && A[1].startsWith("exon"))
	     this.exon = A[1];
	 if (A.length>2 && A[2].startsWith("c."))
	     this.cds_mutation=A[2];
	 this.aa_mutation = "-";
	 
     }
    
    /** ;HGVS=KIAA1751:NM_001080484:exon18:c.2287T>C:p.X763Q */
    private void set_HGVS_by_split(String A[]) {
	 this.genename=A[0]; 
	if ( A[1].startsWith("NM_")) this.refseq_mrna=A[1]; 
	if ( A[2].startsWith("exon")) this.exon=A[2];
	if (A[3].startsWith("c.")) this.cds_mutation=A[3];
	if (A.length > 4 && A[4].startsWith("p.")) this.aa_mutation = A[4];
	if (aa_mutation == null) return;
	int i = aa_mutation.indexOf(",");
	if (i>0) 
	    aa_mutation = aa_mutation.substring(0,i);
    }
   
    /**
     * This extracts (only) the first variant froma a String such as
     * HGVS=PLEKHN1:NM_001160184:exon13:c.1355G>C:p.R452P,*/
    private void set_HGVS(String hgvs) {
	int paren = hgvs.indexOf("(");
	if (paren>0) {
	    set_HGVS_by_paren(hgvs);
	    return;
	}
	String A[] = hgvs.split(":");  
	if (A.length>3) {
	    set_HGVS_by_split(A);
	    return;
	}
	this.genename = hgvs;
	//System.err.println(get_variant_type_as_string());
	//System.err.println("Could not parse line " + hgvs);
	//System.exit(1);
	

    }


  /** Uses constants in NSFP_Constants. */
    private void set_variant_type(String vartype) {
	 if (vartype.equals("intergenic") )
	    this.vtype = INTERGENIC;
	else if (vartype.equals("ncRNA_intronic" ) )
	    this.vtype = ncRNA_INTRONIC;
	else if (vartype.equals("intronic") )
	    this.vtype = INTRONIC;
	else if (vartype.equals("missense") )
	    this.vtype = MISSENSE;
	else if (vartype.equals("nonsense") )
	    this.vtype = NONSENSE;
	else if (vartype.equals("synonymous") )
	    this.vtype = SYNONYMOUS;
	else if (vartype.equals("downstream") )
	    this.vtype = DOWNSTREAM;
	else if (vartype.equals("ncRNA_exonic") )
	    this.vtype = ncRNA_EXONIC;
	else if (vartype.equals("?") )
	    this.vtype = UNKNOWN;
    	else if (vartype.equals("unknown") )
	    this.vtype = UNKNOWN;
	else if (vartype.equals("UTR5") )
	    this.vtype = UTR5;
	else if (vartype.equals("UTR3") )
	    this.vtype = UTR3;
	else if (vartype.equals("stoploss") )
	    this.vtype = STOPLOSS;
	else if (vartype.equals("nonframeshift-insertion") )
	    this.vtype = NON_FS_INSERTION;
	else if (vartype.equals("ncRNA_UTR3") )
	    this.vtype = ncRNA_UTR3;
	else if (vartype.equals("ncRNA_UTR5") )
	    this.vtype = ncRNA_UTR5;
	else if (vartype.equals("stopgain") )
	    this.vtype = STOPGAIN;
	else if (vartype.equals("frameshift-insertion") )
	    this.vtype = FS_INSERTION;
    	else if (vartype.equals("frameshift-deletion") )
	    this.vtype = FS_DELETION;
	else if (vartype.equals("frameshift-substitution") )
	    this.vtype = FS_SUBSTITUTION;
	else if (vartype.equals("nonframeshift-deletion") )
	    this.vtype = NON_FS_DELETION;
	else if (vartype.equals("upstream") )
	    this.vtype = UPSTREAM;	 
	else if (vartype.equals("splicing") )
	    this.vtype = SPLICING;
	else if (vartype.equals("nonframeshift-substitution") )
	    this.vtype = NON_FS_SUBSTITUTION;
    	else if (vartype.equals("ncRNA_splicing") )
	    this.vtype = ncRNA_SPLICING;
	else if (vartype.equals("exonic") )
	    this.vtype = EXONIC;
	else {
	    System.out.println("Did not recognize vartype = " + vartype);
	    this.vtype = UNKNOWN;
	}
    }

    public String get_variant_type_as_string()
    {
	String s = "-";
	switch(this.vtype) {
	case INTERGENIC: s = "intergenic"; break;
	case ncRNA_INTRONIC: s = "ncRNA_intronic"; break;
	case INTRONIC: s = "intronic"; break;
	case MISSENSE: s = "missense"; break;
	case NONSENSE: s = "nonsense"; break;
	case SYNONYMOUS: s = "synonymous"; break;
	case DOWNSTREAM: s =  "downstream"; break;
	case ncRNA_EXONIC: s = "ncRNA_exonic"; break;
	case UNKNOWN: s = "unknown"; break;
	case UTR5: s = "UTR5"; break;
	case UTR3: s= "UTR3"; break;
	case STOPLOSS: s = "stoploss"; break;
	case NON_FS_INSERTION: s = "nonframeshift-insertion"; break;
	case  ncRNA_UTR3: s = "ncRNA_UTR5"; break;
	case STOPGAIN: s = "stopgain"; break;
	case FS_INSERTION: s = "frameshift-insertion"; break;
	case FS_DELETION: s = "frameshift-deletion"; break;
	case FS_SUBSTITUTION: s = "frameshift-substitution"; break;
	case NON_FS_DELETION: s = "nonframeshift-deletion"; break;
	case UPSTREAM: s = "upstream"; break;
	case SPLICING: s = "splicing"; break;
	case NON_FS_SUBSTITUTION: s = "nonframeshift-substitution"; break;
	case ncRNA_SPLICING: s = "ncRNA_splicing"; break;
	case EXONIC: s = "exonic"; break;
	default: s = "unparsed";
	}
	return s;
    }

    public void dump_VCF_line_for_debug()
    {
	System.err.println(vcf_line);
	System.err.println("chromosome: " + get_chromosome_as_string());
	System.err.println("position: " + get_position() );
	System.err.println("reference sequence:" +  get_reference_sequence() );
	System.err.println("alt sequence:" +  get_alternate_sequence() );
	System.err.println("variant as int " + get_variant_type_as_int());
	System.err.println("genotype: " +  get_genotype_as_string());
	System.err.println("genotype quality: " +  get_genotype_quality() );
	System.err.println("Gene name: " +  get_genename() );
	System.err.println("refseq id: " +  get_refseq() );
	System.err.println("Exon: " +  get_exon());
	System.err.println("CDS mutation: " +  get_cds_mutation() );
	System.err.println("AA mutation: " +  get_aa_mutation() );
	System.err.println("is_predicted_non_missense_path(): " + is_predicted_non_missense_path());
   


    }


}