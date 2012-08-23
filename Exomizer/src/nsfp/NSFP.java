package nsfp;


import nsfp.snv.SNV;
import nsfp.io.NSFP_Constants;

/**
 * Objects of the class encapsulate a single line of the NSFP and also
 * hold a link to the SNV object that they match to. The dbNFSP lines
 * have tab-separated fields. See NSFP_Constants for the list.
 // The following flags are not in the original dbNSFP dataset, but allow use to make composite fields
 101) GENOMIC_VAR  show variant as g.A12345627T
 */
public class NSFP implements NSFP_Constants {
    /** A SNV obeject represnting a variant from the user-supplied mutation file (usually, VCF) that
	corresponds to the variant denoted by this NSFP obejct.
    */
    private SNV snv = null;
    private boolean is_non_SNV_pathogenic = false;
    private int ESP_minor_count = 0;
    private int ESP_major_count = 0;
    private float ESP_freq = -1f;

    public boolean is_non_SNV_pathogenic() { return is_non_SNV_pathogenic; }
   
    private String[] fields = null;

    public String get_field(int idx) {
	if (idx == MUTATION_TASTER_SCORE) {
	    try {
		Float f = Float.parseFloat(fields[MUTATION_TASTER_SCORE]);
		if (f < -0.5) return ".";
		else return String.format("%.3f",f);
	    } catch (NumberFormatException e) {
		return fields[MUTATION_TASTER_SCORE];
	    }	
	} else if (idx >= 0 && idx < 49) {
	    if ( this.fields[idx] == null || this.fields[idx].startsWith("-1") ) return ".";
	    else return this.fields[idx];
	} else if (idx == VARTYPE_IDX) {
	    return snv.get_variant_type_as_string();
	} else if (idx == GENOTYPE_QUALITY) {
	    return Integer.toString( snv.get_genotype_quality());
	} else if (idx == GENOMIC_VAR) {
	    StringBuilder sb = new StringBuilder();
	    sb.append(this.snv.get_chrom_string() + ": ");
	    sb.append("g." +   fields[POS] + fields[REF] + ">" + fields[ALT] );
	    if (is_heterozygous()) {
		sb.append(" (het)");
	    } else if (is_homozygous_alt()) {
		sb.append(" (hom)");
	    } else {
		sb.append(" (zygosity: ?)");
	    }
	    return sb.toString();
	} else if (idx ==  POLYPHEN_WITH_PRED ) {
	    String prediction = null;
	    try {
		Float f = Float.parseFloat( fields[Polyphen2_HVAR_score] );
		float ff = f.floatValue();
		if (ff < 0) return "."; // flag for no entry.
		if (ff > 0.956) prediction = "D"; // probably damaging
		else if (ff > 0.446) prediction = "P"; // possibly damaging
		else prediction = "B"; // benign
	    } catch (NumberFormatException e) {
		; // no op
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(fields[Polyphen2_HVAR_score]);
	    if (prediction != null)
		sb.append(" (" + prediction + ")" );
	    return sb.toString();
	} else if ( idx == SIFT_WITH_PRED ) {
	    String prediction = null;
	    try {
		Float f = Float.parseFloat( fields[SIFT_SCORE] );
		float ff = f.floatValue();
		if (ff < -0.1) return ".";
		if (ff < 0.05)
		    prediction = "D"; /* damaging */
		else
		    prediction = "T"; /* tolerated */
	    } catch (NumberFormatException e) {
		; // no op
	    }
	    StringBuilder sb = new StringBuilder();
	    sb.append(fields[SIFT_SCORE]);
	    if (prediction != null)
		sb.append(" (" + prediction + ")" );
	    return sb.toString();
	}  else if ( idx == THOUSAND_GENOMES_AF_AC ) {
	    return get_frequency_UL();
	} else {
	    System.err.println("Error: Attempt to acccess field + " + idx  
			       + " of NSFP array field with 49 slots");
	    System.exit(1);
	    return null;
	}
    }

    /**
     * Get an unordered HTML list for the frequency data
     */
    private String get_frequency_UL() {
	StringBuilder sb = new StringBuilder();
	sb.append("<UL>\n");
	String AC = fields[TG_1000Gp1_AC]; 
	if (AC.startsWith("-1")) {
	    sb.append("<LI>1000G: 0 (0%)</LI>");
	} else {					       
	    String AF = null;
	    try{
		Float f = Float.parseFloat(fields[TG_1000Gp1_AF]);
		if (f>-1.0) {
		    f *= 100;
		    AF = String.format(" (%.1f%%)",f);
		}
	    } catch (NumberFormatException e) {
		; // no-op
	    }
	    if (AF == null)  sb.append("<LI> 1000G: .</LI>");
	    else  sb.append("<LI> 1000G: " + AC + AF +"</LI>");
	}
	   
	if (true){//this.ESP_freq > -0.5) {
	    sb.append("<LI>ESP: " + 
		      String.format("%s/%s (%.2f%%)", ESP_minor_count, ESP_major_count, 100*ESP_freq )
		      + "</LI>\n");
	}
        sb.append("</UL>\n");
	return sb.toString();
    }

    private static int unknown_counter = 1;
    public String get_name() {
	String name = null;
	if (fields[GENENAME] != null && ! fields[GENENAME].equals("unknown") ) {
	    name = fields[GENENAME];
	} else {
	    StringBuilder sb = new StringBuilder();
	    sb.append("unknown-" + unknown_counter);
	    unknown_counter++;
	    name =  sb.toString();
	}
	return name;
    }

    public boolean is_heterozygous() {
	return this.snv.is_heterozygous();
    }

    public boolean is_homozygous_alt() {
	return this.snv.is_homozygous_alt();
    }

    public boolean is_X_chromosomal() {
	return this.fields[CHR].equals("X");
    }
    

    /** Something of a hack. Refactor to make nicer. */
    public NSFP(SNV snv, int chrom, int pos, String ref, String alt) {
	//System.out.println("NSFP.java l. 164");

	this.fields = new String[49];	
	if (chrom == 23)
	    fields[CHR] = "X";
	else if (chrom == 24)
	    fields[CHR] = "Y";
	else
	    fields[CHR] = Integer.toString(chrom);
	
	fields[POS] = Integer.toString(pos);
	fields[REF] = ref;  
	fields[ALT] = alt;

	this.ESP_minor_count = snv.get_esp_minor();
	this.ESP_major_count = snv.get_esp_major();
	this.ESP_freq = snv.get_esp_frequency();
   
	this.snv = snv;
    }

    /**
     * Does at least one of SIFT, polyphen2, or mutation taster predict
     * pathogenicity?.
     */
    public boolean is_predicted_pathogenic()
    {
	if (fields[MUTATION_TASTER_SCORE] != null) {
	    try {
		Float f = Float.parseFloat(fields[MUTATION_TASTER_SCORE]);
		if (f > 0.94) return true;
	    } catch (NumberFormatException e) {
		; // no op
	    }	
	}
	if (fields[SIFT_SCORE] != null) {
	    try{
		Float f = Float.parseFloat(fields[SIFT_SCORE]);
		if (f < 0.06 && f > NOPARSE_FLOAT) return true; // remember, NOPARSE_FLOAT (-1) is flag for data not parsable.
	    } catch (NumberFormatException e) {
		; // no op
	    }	
	}
	if (fields[POLYPHEN2_HVAR_SCORE] != null) {
	    try {
		Float f = Float.parseFloat(fields[POLYPHEN2_HVAR_SCORE]);
		if (f > 0.446) return true; // note this is "possibly damaging, don't be too strict.
	    } catch (NumberFormatException e) {
		; // no op
	    }	
	}
	return false;
    }
      
    /**
     * If both 1000 genomes and ESP show rare variante, this is true,
     */
    public boolean is_rarer_than_threshold(float threshold)
    {
	boolean rare = false;

	System.out.println("is_rarer_than_threshold ESP =" + this.ESP_freq);
	
	if (this.ESP_freq > -0.5) {
	    if (ESP_freq <= threshold) rare = true;
	    else return false;
	}


	if (fields[TG_1000Gp1_AF] != null) {
	    try {
		Float f = Float.parseFloat(fields[TG_1000Gp1_AF]);
		if (f<=threshold) rare =  true;
	    } catch (NumberFormatException e) {
		; // no op
	    }
	}
	return rare; 
    }

    public void set_genename (String name ) { this.fields[GENENAME] = name; }
    public void set_uniprot_id(String id) { this.fields[UNIPROT_ID] = id; }
    public void set_ensembl_geneid(String id ) { this.fields[ENSEMBL_GENE_ID] = id; }
    public void set_aapos (int pos) { String p = Integer.toString(pos); this.fields[AAPOS] = p; }
    public void set_sift(float sift) {String f = Float.toString(sift); this.fields[SIFT_SCORE] =f; }
    public void set_polyphen_HVAR(float polyp) { String poly = Float.toString(polyp); 
	this.fields[POLYPHEN2_HVAR_SCORE] = poly; }
    public void set_mut_taster(float mut) { String tast =  Float.toString(mut); 
	this.fields[MUTATION_TASTER_SCORE ] = tast; }
    public void set_ThGenomes_AC(int tg) { String thousandAC = Integer.toString(tg); 
	this.fields[TG_1000Gp1_AC] = thousandAC; }

    public void set_ThGenomes_AF(float tg) { String thousandAF = Float.toString(tg); 
	this.fields[TG_1000Gp1_AF] = thousandAF; }

    public void  set_phylo_p(float phyl) { String pp = Float.toString(phyl); this.fields[PHYLO_P] = pp; }

   

    public void set_esp_minor(int minor) { this.ESP_minor_count = minor; }
    public void set_esp_major(int major)  { this.ESP_major_count = major; }
    public void set_esp_frequency(float freq) { this.ESP_freq = freq; }

    /** Private constructor intended tto be used only by static factory method. */
    private NSFP() { }

    public static NSFP get_NSFP_for_non_substition_variant(SNV snv) {
	if (! snv.is_non_SNV_pathogenic() ) return null; /* should always be true  */
	NSFP n = new NSFP();
	n.snv = snv;

	n.is_non_SNV_pathogenic = true;
	n.fields = new String[49];

	int c = snv.get_chromosome();
	if (c == X_CHROMOSOME) n.fields[CHR] = "X";
	else if (c == Y_CHROMOSOME) n.fields[CHR]="Y";
	else if (c == M_CHROMOSOME) n.fields[CHR]="M";
	else n.fields[CHR]=Integer.toString(c);

	n.fields[POS] = Integer.toString(snv.get_position());
	n.fields[REF] = snv.get_ref_nucleotide();   
	n.fields[ALT] = snv.get_alt_nucleotide();

	n.set_uniprot_id("-");
	n.set_ensembl_geneid("-");
	String gname = snv.get_genename();
	n.set_genename(gname);
	n.set_aapos(INT_NOT_AVAILABLE);
	n.set_sift(DATA_NOT_APPLICABLE);
	n.set_polyphen_HVAR(DATA_NOT_APPLICABLE);
	n.set_mut_taster(DATA_NOT_APPLICABLE);
	n.set_phylo_p(DATA_NOT_APPLICABLE);
	n.set_ThGenomes_AC(THOUSAND_GENOMES_NOT_AVAILABLE);
	n.set_ThGenomes_AF((float)THOUSAND_GENOMES_NOT_AVAILABLE);
		

	return n;
    }

    public NSFP(String [] A, SNV snv){
	if (A.length < 49) {
	    System.err.println("Badly formed NFPS line");
	    debugPrintArray(A);
	    System.exit(1);
	}
	this.fields = new String[49];
	this.snv = snv;

	fields[CHR] = A[CHR];
	fields[POS] = A[POS];
	fields[REF] = A[REF];   
	fields[ALT] = A[ALT];
	fields[AAREF] = A[AAREF];   
	fields[AAALT] = A[AAALT];   
	fields[HG18_POS] = A[HG18_POS];        
	fields[GENENAME] = A[GENENAME];        
	fields[UNIPROT_ACC] = A[UNIPROT_ACC];
	fields[UNIPROT_ID] = A[UNIPROT_ID];      
	fields[UNIPROT_AAPOS] = A[UNIPROT_AAPOS];   
	fields[INTERPRO_DOMAIN] = A[INTERPRO_DOMAIN];
	fields[CDS_STRAND] = A[CDS_STRAND];   
	fields[REFCODON] = A[REFCODON];      
	fields[SLR_TEST_STATISTIC] = A[SLR_TEST_STATISTIC];       
	fields[CODONPOS] = A[CODONPOS];      
	fields[FOLD_DEGENERATE] = A[FOLD_DEGENERATE];
	fields[ANCESTRAL_ALLELE] = A[ANCESTRAL_ALLELE];     
	fields[ENSEMBL_GENE_ID] = A[ENSEMBL_GENE_ID]; 
	fields[ENSEMBL_TRANSCRIPT_ID] = A[ENSEMBL_TRANSCRIPT_ID];  
	fields[AAPOS] = A[AAPOS]; 
	fields[SIFT_SCORE] = A[SIFT_SCORE];
	fields[Polyphen2_HDIV_score] = A[Polyphen2_HDIV_score];     
	fields[Polyphen2_HDIV_pred] = A[Polyphen2_HDIV_pred];      
	fields[Polyphen2_HVAR_score] = A[Polyphen2_HVAR_score];     
	fields[Polyphen2_HVAR_pred] = A[Polyphen2_HVAR_pred];     
	fields[LRT_score] = A[LRT_score];       
	fields[LRT_pred] = A[LRT_pred];        
	fields[MUTATION_TASTER_SCORE] = A[MUTATION_TASTER_SCORE];     
	fields[MUTATION_TASTER_PRED] = A[MUTATION_TASTER_PRED];     
	fields[GERP_NR] = A[GERP_NR];       
	fields[GERP_RS] = A[GERP_RS];        
	fields[PHYLO_P] = A[PHYLO_P];  
	fields[UCSC_29way_pi] = A[UCSC_29way_pi];        
	fields[UCSC_29way_logOdds] = A[UCSC_29way_logOdds];   
	fields[LRT_Omega] = A[LRT_Omega];       
	fields[UniSNP_ids] = A[UniSNP_ids];      
	fields[TG_1000Gp1_AC] = A[TG_1000Gp1_AC];      
	fields[TG_1000Gp1_AF] = A[TG_1000Gp1_AF];      
	fields[TG_1000Gp1_AFR_AC] = A[TG_1000Gp1_AFR_AC];  
	fields[TG_1000Gp1_AFR_AF] = A[TG_1000Gp1_AFR_AF]; 
	fields[TG_1000Gp1_EUR_AC] = A[TG_1000Gp1_EUR_AC]; 
	fields[TG_1000Gp1_EUR_AF] = A[TG_1000Gp1_EUR_AF];
	fields[TG_1000Gp1_AMR_AC] = A[TG_1000Gp1_AMR_AC]; 
	fields[TG_1000Gp1_AMR_AF] = A[TG_1000Gp1_AMR_AF];
	fields[TG_1000Gp1_ASN_AC] = A[TG_1000Gp1_ASN_AC]; 
	fields[TG_1000Gp1_ASN_AF] = A[TG_1000Gp1_ASN_AF]; 
	fields[TG_ESP5400_AA_AF] = A[TG_ESP5400_AA_AF];
	fields[ESP5400_EA_AF] = A[ESP5400_EA_AF];
    }

 
    public String get_SNV_annotation() { return this.snv.anno; }


    public String get_short_form()
    {
	StringBuilder sb = new StringBuilder();
	sb.append("chr" + fields[CHR] + ":" + fields[REF] + 	fields[POS] + fields[ALT]);
	sb.append(" [ref aa: " + 	fields[AAREF]  + "; var aa: " + 	fields[AAALT]  + "]");
	return sb.toString();
    }

    private void debugPrintArray(String [] A) {
	System.out.println("Size of array: " + A.length);
	for (int i=0;i<A.length;++i) {
	    System.out.println(i + ") " + A[i]);
	}

    }


    /** Return true if the SIFT score associated with this NSFP
	object (SNP) is less than the threshold. This is an indication that the
	sequence variant is pathogenic.
	@param threshold a threshold value between 0-1; 0.05 and less is considered pathogenic by SIFT.
    */
    public boolean passes_SIFT_threshold(double threshold) {
	try {
	    Double d = Double.parseDouble(fields[SIFT_SCORE]);
	    return (threshold >= d);
	} catch (NumberFormatException e) {
	    //System.err.println("Error [NSFP.java]: Could not parse SIFT score: " + SIFT_score);
	    return false;
	}


    }


    public String get_nsfp() {
	StringBuilder sb = new StringBuilder();
	String sf = get_short_form();
	sb.append(sf);
	sb.append(" => \"" + fields[GENENAME] + "\" [" + fields[UNIPROT_ACC] + "]\n");
	sb.append("\tSNV: " + get_SNV_annotation() + "\n");
	sb.append("\tSift score: " + fields[SIFT_SCORE] + "\n");
	sb.append("\tPolyphen2 HDIV score: " + 	fields[Polyphen2_HDIV_score] + "\n");
	sb.append("\tPolyphen2_HDIV pred: "  + fields[Polyphen2_HDIV_pred] + "\n");  
	sb.append("\tPolyphen2_HVAR score: " + fields[Polyphen2_HVAR_score] + "\n");  
	sb.append("\tPolyphen2_HVAR_pred: " + fields[Polyphen2_HVAR_pred] + "\n");
	sb.append("\tMutationTaster score: " + fields[MUTATION_TASTER_SCORE] + "\n");
	sb.append("\tMutationTaster pred: "  +   fields[MUTATION_TASTER_PRED] + "\n"); 
	sb.append("\tOriginal SNV:" + snv);
	return sb.toString();
    }

    // TODO: Make this more flexible and decouple latex from this class!
    public String getLatexTableRow() {
	StringBuilder sb = new StringBuilder();
	sb.append(fields[GENENAME] + " & ");
	sb.append(fields [CHR] + " & ");
	sb.append( 	fields[REF] + fields[POS] + fields[ALT] + " & ");
	sb.append( snv.anno + " & ");
	sb.append( "? & ");
	sb.append("p." +  fields[AAREF] + fields[AAPOS] + fields[AAALT] + " & ");
	sb.append( fields[SIFT_SCORE] + " & ");
	sb.append(  fields[Polyphen2_HVAR_score] + "(" +  fields[Polyphen2_HVAR_pred] + ") & ");
	sb.append(  fields[MUTATION_TASTER_SCORE] + "(" +   fields[MUTATION_TASTER_PRED] + ") & ");
	sb.append(  fields[PHYLO_P]  + " & "); 
	sb.append( fields[TG_1000Gp1_AC]);
	return sb.toString();

    }





    /**
     * This function can be used for debugging to show all the values. 
     */
    public String get_complete_nsfp() {
	StringBuilder sb = new StringBuilder();
	String sf = get_short_form();
	sb.append(sf);
	sb.append(" => " + fields[GENENAME] + " [" + fields[UNIPROT_ACC] + "]\n");
	sb.append("\t Uniprot_aapos: " + fields[UNIPROT_AAPOS] + "\n");
	sb.append("\t Interpro_domain = " +fields[INTERPRO_DOMAIN] +  "\n");  
	sb.append("\t cds_strand  = " + fields[CDS_STRAND] + "\n");      
	sb.append("\trefcodon = " + fields[REFCODON] +  "\n");        
	sb.append("\tSLR_test_statistic: " + fields[SLR_TEST_STATISTIC] +  "\n");    
	sb.append("\tcodonpos: " + fields[CODONPOS] + "\n");
	sb.append("\tfold_degenerate: " + fields[FOLD_DEGENERATE]  + "\n");
	sb.append("\tAncestral_allele: " + fields[ANCESTRAL_ALLELE]  + "\n");
	sb.append("\tEnsembl_geneid: " + fields[ENSEMBL_GENE_ID]  + "\n"); 
	sb.append("\tEnsembl_transcriptid: " + fields[ENSEMBL_TRANSCRIPT_ID] + "\n");  
	sb.append("\taapos: " + fields[AAPOS] +  "\n"); 
	sb.append("\tSift score: " + fields[SIFT_SCORE] + "\n");
	sb.append("\tPolyphen2 HDIV score: " + 	fields[Polyphen2_HDIV_score] + "\n");
	sb.append("\tPolyphen2_HDIV pred: " + fields[Polyphen2_HDIV_pred]  + "\n");  
	sb.append("\tPolyphen2_HVAR score: " + fields[Polyphen2_HVAR_score] + "\n");  
	sb.append("\tPolyphen2_HVAR_pred: " +  fields[Polyphen2_HVAR_pred] + "\n");
	sb.append("\tMutationTaster score: " + fields[MUTATION_TASTER_SCORE] + "\n");
	sb.append("\tMutationTaster pred: "     +   fields[MUTATION_TASTER_PRED] + "\n"); 
	sb.append("\tGERP_NR:" + fields[GERP_NR]  + "\n"); 
	sb.append("\tGERP_RS: " + fields[GERP_RS]    + "\n");   
	sb.append("\tphyloP: "  + fields[PHYLO_P]  + "\n"); 
	sb.append("\t_29way_pi: " + fields[UCSC_29way_pi]  + "\n");    
	sb.append("\t_29way_logOdds: " + fields[UCSC_29way_logOdds] + "\n"); 
	sb.append("\tLRT_Omega: " + fields[LRT_Omega]   + "\n");    
	sb.append("\tUniSNP_ids: " + fields[UniSNP_ids]   + "\n");     
	sb.append("\t_1000Gp1_AC: " + fields[TG_1000Gp1_AC]  + "\n"); 
	sb.append("\t_1000Gp1_AF: " + fields[TG_1000Gp1_AF]  + "\n"); 
	sb.append("\t_1000Gp1_EUR_AC: " + fields[TG_1000Gp1_EUR_AC]  + "\n"); 
	sb.append("\t_1000Gp1_EUR_AF: " + fields[TG_1000Gp1_EUR_AF]  + "\n"); 
	sb.append("\tOriginal SNV:" + snv);
	return sb.toString();
    }

    public SNV get_SNV() { return snv; }
    public String get_SIFT_score_as_String() { return fields[SIFT_SCORE]; }

    /** return a String representation of the given field */
    public static String get_field_name(int idx)
    {
	String s = null;
	switch(idx) {
	case CHR: s="Chromosome";break;
	case POS: s="Position"; break;
	case REF: s="Ref"; break;
	case ALT: s="Alt"; break;
	case AAREF: s="AA ref"; break;
	case AAALT: s="AA alt"; break;
    	case HG18_POS: s="HG18 position"; break;
	case GENENAME: s="Gene Name"; break;
	case UNIPROT_ACC: s="Uniprot acc"; break;
	case UNIPROT_ID: s="Uniprot id"; break;
	case UNIPROT_AAPOS: s="Uniprot aa pos"; break;
	case INTERPRO_DOMAIN: s ="Interpro_domain"; break;
	case CDS_STRAND: s ="cds_strand"; break;
	case REFCODON:  s = "refcodon"; break;  
	case SLR_TEST_STATISTIC: s="SLR test statistic"; break;      
	case CODONPOS: s= "codonpos";break;  
	case FOLD_DEGENERATE: s="fold-degenerate"; break;
	case ANCESTRAL_ALLELE: s="Ancestral_allele"; break;       
	case ENSEMBL_GENE_ID: s="Ensembl_geneid"; break;  
	case ENSEMBL_TRANSCRIPT_ID: s="Ensembl_transcriptid";
	case AAPOS: s="aapos";break;  
	case SIFT_SCORE: s="SIFT"; break;        
	case Polyphen2_HDIV_score: s="Polyphen2 (HDIV)";   break;    
	case Polyphen2_HDIV_pred:   s="Polyphen2 (HDIV-Pred)"; break;    
	case Polyphen2_HVAR_score:s="Polyphen2 (HVAR)"; break;  
	case Polyphen2_HVAR_pred: s="Polyphen2 (HVAR-Pred)";  break;      
	case LRT_score: s="LRT Score";      
	case LRT_pred: s="LRT Prediction"; 
	case MUTATION_TASTER_SCORE:   s="Mutation Taster";  break;   
	case MUTATION_TASTER_PRED:   s="Mutation Taster-Pred";break;  
	case GERP_NR: s="GERP Nr"; break;
	case GERP_RS: s="Gerp rs"; break;       
	case PHYLO_P: s="PhyloP"; break;
	case UCSC_29way_pi: s="29-way pi"; break;      
	case UCSC_29way_logOdds: s="29-way log odds"; break;
	case LRT_Omega: s="LRT Omega"; break;     
	case UniSNP_ids: s="UniSNP ids"; break;
	case TG_1000Gp1_AC: s="1000G AC"; break;     
	case TG_1000Gp1_AF: s="1000G AF";break;  
	case TG_1000Gp1_AFR_AC:  s="1000G (AFR) AC";break;  
	case TG_1000Gp1_AFR_AF: s="1000G (AFR) AF";break;  
	case TG_1000Gp1_EUR_AC: s="1000G (EUR) AC";break;  
	case TG_1000Gp1_EUR_AF : s="1000G (EUR) AF";break;  
	case TG_1000Gp1_AMR_AC: s="1000G (AMR) AC";break;  
	case TG_1000Gp1_AMR_AF : s="1000G (AMR) AF";break;  
	case TG_1000Gp1_ASN_AC:  s="1000G (ASN) AC";break;  
	case TG_1000Gp1_ASN_AF:  s="1000G (ASN) AF";break; 
	case TG_ESP5400_AA_AF : s="ESP5400_AA AF";break; 
	case ESP5400_EA_AF: s="ESP5400_EA AF";break; 
	case GENOMIC_VAR: s="variant"; break;
 	case POLYPHEN_WITH_PRED: s="Polyphen"; break;
   	case SIFT_WITH_PRED: s="Sift"; break;
    	case MUT_TASTER_WITH_PRED: s="Mut. Taster"; break;
    	case THOUSAND_GENOMES_AF_AC: s="1000 G: count(%)"; break;
	case  VARTYPE_IDX: s="Type"; break;
	case GENOTYPE_QUALITY: s="Genotype Quality"; break;
	default: System.err.println("Did not recognize index: " + idx);
	    System.exit(1);
	}
	return s;
	  
	}




	
}