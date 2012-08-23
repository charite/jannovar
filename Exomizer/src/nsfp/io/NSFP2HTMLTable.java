package nsfp.io;


import nsfp.NSFP;
import nsfp.io.NSFP_Constants;


/**
 * This class is responsible for creating a table to represent variants that have beeen annotated to
 * NSFP. The class is initialized with a list of fields that are to be shown. It then returns an
 * HTML header and provides a function that transforms an NSFP object into a table row. Clients of 
 * this class can therefore initialize the NSFP2HTMLTable with the fields they want to show, and then
 * use it to obtain strings representing NSFP obejcts that are formatted as rows for the HTML table.
 */
public class NSFP2HTMLTable implements NSFP_Constants {
    private int[] idx;
    
    
    public NSFP2HTMLTable(int... indices) {
	int N = indices.length;
	this.idx = new int[N+1];
	for (int i=0;i<N;++i) {
	    this.idx[i+1]=indices[i];
	}
    }

    /**
     * This function dynamically creates a table header based on the indices passed to the
     * constructor. Note that the zero-th cell in the table is always coming from the
     * SNV object, which contains annotation information that comes directly from the 
     * VCF file (gene name, name of mutation on trascription level).
     */
    public String table_header() {
	StringBuilder sb = new StringBuilder();
	sb.append("<TABLE border=\"1\" cellpaddding=\"1\" cellspacing=\"0\">\n");
	sb.append("<tbody align=\"center\" style=\"font-family:verdana; "+
		  "color:black;background-color:#00FFFF\">\n");
	sb.append("<TR>");
	sb.append("<TD>variant</TD>");
	for (int i=1;i<idx.length;++i) {
	    String item = NSFP.get_field_name(this.idx[i]);
	    sb.append("<TD>" + item + "</TD>");
	}
	sb.append("</TR>\n");
	sb.append("</tbody>\n");

	return sb.toString();
    }

    public String table_footer() {
	return "</TABLE>";
    }

    /** This method extracts the fields from the current NSFP object that have been 
	indicated in the array idx with the corresponding fields. */
    public String table_row(NSFP n) {
	StringBuilder sb = new StringBuilder();
	sb.append("<TR>");
	sb.append("<TD>" + n.get_SNV().get_mutation() + "</TD>");
	for (int i=1;i<idx.length;++i) {
	    try{
		String item = n.get_field(this.idx[i]);
		sb.append("<TD>" + item + "</TD>");
	    } catch (Exception e) {
		System.out.print("Exception while creating table row for item " + i +": ");
		System.out.println(get_name_of_index(idx[i]));
		System.out.println("NSFP obejct: \n" +n.get_nsfp());
		System.exit(1);
	    }
	}
	sb.append("</TR>\n");
	return sb.toString();
    }

    public String get_name_of_index(int i) {
	switch(i) {
	case GENOMIC_VAR: return "genomic var";
	case POLYPHEN_WITH_PRED: return "polyphen with pred";
	case SIFT_WITH_PRED: return "Sift with pred";
	case MUT_TASTER_WITH_PRED: return "mut taster with pred";
	case VARTYPE_IDX: return "Vartype";
	case GENOTYPE_QUALITY: return "Gtype qual";
	default: return "index " + i;
	}
    }




}