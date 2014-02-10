package jannovar.io;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

import jannovar.common.FeatureType;
import jannovar.exception.FeatureFormatException;
import jannovar.gff.Feature;
import jannovar.gff.TranscriptModelBuilder;
import java.util.logging.Level;

/**
 * This is the second version of the parser w/o any String-switch-case statements to be 
 * downwardly compatible with older Java versions.<br>
 * version 0.2:<br>
 * removed String switch statements<br>
 * version 0.3:<br>
 * added GFF version<br>
 * switched to {@link TranscriptModelBuilder}
 * @author mjaeger
 * @version 0.3 (2013-07-12)
 */
public class GFFparser {
	
	private static final Logger logger = Logger.getLogger(GFFparser.class.getSimpleName());

	private final static int SEQID		= 0;
	private final static int SOURCE		= 1;
	private final static int TYPE		= 2;
	private final static int START		= 3;
	private final static int END		= 4;
	private final static int SCORE		= 5;
	private final static int STRAND		= 6;
	private final static int PHASE		= 7;
	private final static int ATTRIBUTES	= 8;
	
	private File file;
//	private String line;
	private String[] fields;
//	private String[] fields_attribute;
//	private String[] fields_attribute_Pair;
	private BufferedReader in;
	private TranscriptModelBuilder transcriptBuilder;
	private int gff_version	= 2;
	
	// Feature Processing
	
	// Attributes processing
	private int start;
	private int index;
	private int subIndex;
	private String rawfeature;
	private String valueSeparator = " ";
	
        /**
         * Set the value separator (e.g. ' ' for GFF2, '=' for GFF3)
         * @param sep 
         */
	public void setValueSeparator(String sep){
		valueSeparator = sep;
	}
	
        /**
         * default constructor
         */
	public GFFparser(){
		
	}
	
	/**
	 * Checks if the specified file can be accessed.
     * @return <code>true</code> if the file can be accessed
	 */
	public boolean checkFile(){
		return this.file.canRead();
	}
	
	/**
	 * Returns the GFF version of the parsed file.
	 * @return the GFF version
	 */
	public int getGFFversion(){
		return gff_version;
	}
	
	/**
	 * Sets the GFF version of the parsed file.
	 * @param i The GFF version number.
	 */
	public void setGFFversion(int i){
		gff_version = i;
		valueSeparator = i == 3 ? "=" : " ";
	}
	
	/**
	 * This will check the GFF format version. There are several version known.<br>
	 * <UL>
	 * 	<LI>Version 2 - attributes separated by '; ' and gene_id, transcript_id tags<br>
	 * 		<code>GL456350.1      protein_coding  exon    993     1059    .       -       .        gene_id "ENSMUSG00000094121"; transcript_id "ENSMUST00000177695"; </code>
	 *  <LI>Version 2.5 - also known as GTF and quite similar to Version 2<br>
	 *  	<code>chr1    mm9_knownGene   exon    3195985 3197398 0.000000        -       .       gene_id "uc007aet.1"; transcript_id "uc007aet.1";</code>
	 * 	<LI>Version 3 - the current recommended Version.<br>
	 * 		<code>NC_000067.6     RefSeq  mRNA    3214482 3671498 .       -       .       ID=rna0;Name=NM_001011874.1;Parent=gene0;</code>
	 * </UL>
	 * @throws IOException if the file could not be read
	 */
	private void determineGFFversion() throws IOException{

		if(file.getName().endsWith(".gz"))
			in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
		else
			in = new BufferedReader(new FileReader(file));
		String str;
		int version;
		while((str = in.readLine()) != null){
			if(!str.startsWith("#"))
				break;
			else{
				if(str.startsWith("##gff-version")){
					fields	= str.split(" ");				
					try{
						version = Integer.parseInt(fields[1]);
						gff_version = version;
					}catch (NumberFormatException e){
						System.err.println("Failed to parse gff-version: "+str);
					}
				}
			}
		}
		logger.log(Level.INFO, "gff version: {0}", gff_version);
	}
	
	public void parse(String filename){
		parse(new File(filename));
	}
	
	/**
	 * Parses the file and feeds the {@link TranscriptModelBuilder}.
	 * First the GFF format version is verified. If there is no header containing the <code>##gff-version</code> 
	 * tag, we assume it is GFF version 2/2.5 aka. GTF.
     * @param file {@link File} object of the GTF file
	 */
	public void parse(File file){
		this.file = file;
		transcriptBuilder =  new TranscriptModelBuilder();
		try {
			logger.info("Get GFF version");
			determineGFFversion();
			valueSeparator = gff_version == 3 ? "=" : " ";
			transcriptBuilder.setGffversion(gff_version);
			logger.info("Read features");
			if(file.getName().endsWith(".gz"))
				in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));
			else
				in = new BufferedReader(new FileReader(file));
			String str;
			while ((str = in.readLine()) != null) {
				// skip info lines
				if(str.startsWith("#"))
					continue;
				this.transcriptBuilder.addFeature(processFeature(str));
//				break;
			}
		} catch (FeatureFormatException e){
			System.err.println("[WARNING] GFF with wrong Feature format:\n"+e.toString());
		} catch (IOException e) {
                            System.err.println("[WARNING] failed to read the GFF file:\n"+e.toString());
		} finally {
			try{
				if(in != null)
					in.close();
			}catch (IOException e){
                            System.err.println("[WARNING] failed to close the GFF file reader:\n"+e.toString());
			}
		}
		
	}
	

//	/**
//	 * Returns the {@link TranscriptModelFactory}.
//	 * @return the transcriptFactory
//	 */
//	public TranscriptModelFactory getTranscriptFactory() {
//		return transcriptFactory;
//	}
	
	/**
	 * Returns the {@link TranscriptModelBuilder}.
	 * @return the transcriptFactory
	 */
	public TranscriptModelBuilder getTranscriptModelBuilder() {
		return transcriptBuilder;
	}
    
	/**
	 * Processes a single feature / line from a GTF or GFF file.<br>
	 * e.g.<br>
	 *  chr1	mm9_knownGene   exon    3195985 3197398 0.000000        -       .       gene_id "uc007aet.1"; transcript_id "uc007aet.1"; 
	 * <br>Returns a {@link Feature} storing the data represented by this line or <code>null</code> if the 
	 * line contains less than 8 columns separated by '\t'. 
	 * @param featureLine
	 * @return {@link Feature}
	 * @throws FeatureFormatException 
	 */
	public Feature processFeature(String featureLine) throws FeatureFormatException {
		
		ArrayList<String> myfields	= new ArrayList<String>();
		start = 0;
		while((index = featureLine.indexOf('\t', start)) >= 0){
			myfields.add(featureLine.substring(start, index));
//			System.out.println(featureLine.substring(start, index));
			start = index+1;
		}
		if(start != featureLine.length()){
			myfields.add(featureLine.substring(start));
//			System.out.println();
		}
				

		if(myfields.size() < 9){
			logger.warning(String.format("skipping malformed feature line (missing columns (%d)): ",myfields.size(),featureLine));
			return null;
		}
		
		Feature feature = new Feature();
		feature.setSequence_id(myfields.get(SEQID));
		feature.setType(codeType(myfields.get(TYPE)));
		feature.setStart(Integer.parseInt(myfields.get(START)));
		feature.setEnd(Integer.parseInt(myfields.get(END)));
		feature.setStrand(codeStrand(myfields.get(STRAND)));
		feature.setPhase(codePhase(myfields.get(PHASE)));
		feature.setAttributes(processAttributes(myfields.get(ATTRIBUTES)));
		return feature;
		
		
		
//		fields = featureLine.split("\t");
//		if(fields.length < 9){
//			logger.warning("skipping malformed feature line (missing columns ("+myfields.size()+")): "+featureLine);
//			return null;
//		}
//		Feature feature = new Feature();
//		
//		feature.setSequence_id(fields[SEQID]);
////		feature.setSource(fields[SOURCE]);
//		feature.setType(codeType(fields[TYPE]));
//		feature.setStart(Integer.parseInt(fields[START]));
//		feature.setEnd(Integer.parseInt(fields[END]));
////		feature.setScore(Double.parseDouble(fields[SCORE]));
//		feature.setStrand(codeStrand(fields[STRAND]));
//		feature.setPhase(codePhase(fields[PHASE]));
////		if(!fields[ATTRIBUTES].endsWith(";"))
////			this.isGFF = true;
//		feature.setAttributes(processAttributes(fields[ATTRIBUTES]));
//		return feature;
	}
	
	/**
	 * Codes the phase of the CDS reading frame in the exon. A simple cast from String to byte.  
	 * @param phase The phase of the CDS reading frame 
	 * @return the phase of the CDS reading frame as a byte.
	 */
	private byte codePhase(String phase) {

		if(phase.equals("0"))
			return 0;
		if(phase.equals("1"))
			return 1;
		if(phase.equals("2"))
			return 2;

		return -1;
	}

	/**
	 * Processes the attributes in 
	 * GFF3 file format - e.g.:<br>
	 * ID=rna0;Name=NM_001011874.1;Parent=gene0;Dbxref=GeneID:497097<br>
	 * or
	 * GTF2.2 file format - e.g.:<br>
	 * gene_id "uc007aet.1"; transcript_id "uc007aet.1";
	 * @return a HashMap with attributes
	 * @throws FeatureFormatException 
	 */
	private HashMap<String, String> processAttributes(String attributeString) throws FeatureFormatException {
		HashMap<String, String> attributes = new HashMap<String, String>();	

		start = 0;
		if(attributeString.startsWith(" "))
			attributeString	= attributeString.substring(1);
		while((index = attributeString.indexOf(";", start)) > 0){
			rawfeature = attributeString.substring(start, index);
			splitNaddAttribute(rawfeature, attributes);
			
			if(gff_version == 3)
				start	= index+1;
			else
				start	= index+2;
		}
		// for GFF3 we need to add the last element
		if(start < attributeString.length()){
			rawfeature = attributeString.substring(start);
			splitNaddAttribute(rawfeature, attributes);
		}

		return attributes;
	}
	
	/**
	 * Split up the attribute, value pair and add this pair to the attributes Map.
	 * @param attribute 
	 * @param attributes {@link Map} with attribute - value pairs
	 * @throws FeatureFormatException is thrown if attribute String does not contain the 
	 * {@link #valueSeparator separator} for this GFF file format. 
	 */
	private void splitNaddAttribute(String attribute, HashMap<String, String> attributes) throws FeatureFormatException{
		if((subIndex = rawfeature.indexOf(valueSeparator)) > 0){
			if(gff_version == 3){
				attributes.put(rawfeature.substring(0, subIndex), rawfeature.substring(subIndex+1));
//				System.out.println(String.format("%s\t%s",rawfeature.substring(0, subIndex), rawfeature.substring(subIndex+1)));
			}
			else{
				attributes.put(rawfeature.substring(0, subIndex), rawfeature.substring(subIndex+2,rawfeature.length()-1));
//				System.out.println(String.format("%s\t%s",rawfeature.substring(0, subIndex), rawfeature.substring(subIndex+2,rawfeature.length()-1)));
			}
		}
		else
			throw new FeatureFormatException("attribut String without valid value separator ('"+valueSeparator+"'): '"+attribute+"'");
	}
//	/**
//	 * Processes the attributes in GTF2.2 file format.<br>
//	 * e.g.:<br>
//	 * ID=rna0;Name=NM_001011874.1;Parent=gene0;Dbxref=GeneID:497097
//	 * @return
//	 */
//	private HashMap<String, String> processGTFattributes(String attributes) {
//		// TODO Auto-generated method stub
//		return null;
//	}

	/**
	 * Returns the strand as boolean. <code>true</code> for the positive strand (+) and <code>false</code> 
	 * for the minus strand (-). If an other character or String is present in the strand field (e.g. '?')
	 * a {@link FeatureFormatException} is thrown.
	 * @param strand representation of the strand in the feature line
	 * @return <code>true</code> for the positive strand (+) and <code>false</code> for the minus strand (-)
	 * @throws FeatureFormatException
	 */
	private boolean codeStrand(String strand) throws FeatureFormatException{
		if(strand.equals("+"))
			return true;
		else if(strand.equals("-"))
			return false;
		else
			throw new FeatureFormatException("unknown strand: "+strand);

	}
	
	private FeatureType codeType(String type) throws FeatureFormatException{
		if(type.equals("exon"))
			return FeatureType.EXON;
		if(type.equals("CDS"))
			return FeatureType.CDS;
		if(type.equals("start_codon"))
			return FeatureType.START_CODON;
		if(type.equals("stop_codon"))
			return FeatureType.STOP_CODON;
		if(type.equals("gene"))
			return FeatureType.GENE;
		if(type.equals("mRNA"))
			return FeatureType.MRNA;
		if(type.equals("transcript"))
			return FeatureType.TRANSCRIPT;
		if(type.equals("region"))
			return FeatureType.REGION;
		if(type.equals("ncRNA"))
			return FeatureType.NCRNA;
		if(type.equals("tRNA"))
			return FeatureType.TRNA;
		return FeatureType.UNKNOWN;
		
	}


}
