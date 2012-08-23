package nsfp.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.DataOutputStream;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.IOException; 

import java.util.ArrayList;
import java.util.Iterator;


import nsfp.*;

public class LatexTableWriter {

    private String outfile=null;

    public LatexTableWriter(String outfilename) {
	this.outfile = outfilename;
    }



    
    public void writefile(ArrayList<NSFP> hits) {
			    

	System.out.println("Output results to file \"" + outfile + "\"");
	try{
 
	    FileWriter fstream = new FileWriter(this.outfile);
	    BufferedWriter out = new BufferedWriter(fstream);
	    if (hits == null) {
		System.err.println("Error: LatexTableWriter got empty list of NSFP Hits!");
		return;
	    }
	    Iterator<NSFP> it = hits.iterator();
	    while (it.hasNext()) {
		NSFP n = it.next();
		out.write(n.getLatexTableRow() +  "\\\\ \n");
	    }
	    out.close();
	}catch (IOException e){
	    System.err.println("Error: " + e.getMessage());
	}
    }







}
