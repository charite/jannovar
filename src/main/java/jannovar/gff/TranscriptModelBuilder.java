/**
 * 
 */
package jannovar.gff;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

import jannovar.common.ChromosomeMap;
import jannovar.common.FeatureType;
import jannovar.exception.FeatureFormatException;
import jannovar.exception.InvalidAttributException;
import jannovar.reference.TranscriptModel;

/**
 * This is the builder for the {@link TranscriptModel}s from GFF- files. It is feed with {@link Feature}s
 * and builds up Genemodels with the 
 * @author mjaeger
 * @version 0.2 (9 February, 2014)
 */
public class TranscriptModelBuilder implements ChromosomeMap{
	
	private static final Logger logger = Logger.getLogger(TranscriptModelBuilder.class.getSimpleName());
	
	private int gff_version	= 2;
	private int subregion_Index = 0;

	private String curGeneName;
	private String curGeneID;
	private String curRnaID;
	private String curID;
	
	private HashMap<String, Gene> genes;
	private HashMap<String, String> rna2gene;

	private Gene curGene;
	private Transcript curRna;
	private GFFstruct curGFF;
	

	private String[] fields;
	private String[] subfields;
	
	public TranscriptModelBuilder() {
		genes	= new HashMap<String, TranscriptModelBuilder.Gene>();
		rna2gene	= new HashMap<String, String>();
	}
	
	public int getNtranscripts(){
		return this.rna2gene.size();
	}
	
	public int getNgenes(){
		return this.genes.size();
	}
	
	/**
	 * Generates all possible {@link TranscriptModel}s from the given {@link Feature}s.
	 * If mapRna2Geneid is not null and contains appropriate values a mapping to the corresponding
	 * Entrez ids is stored. 
	 * @return {@link ArrayList} with generated {@link TranscriptModel}s
	 * @throws InvalidAttributException 
	 */
	public ArrayList<TranscriptModel> buildTranscriptModels() throws InvalidAttributException{
		logger.info("building transcript models");
		ArrayList<TranscriptModel> models	= new ArrayList<TranscriptModel>();
		TranscriptModel model;
		int curid;
		for (Gene gene : genes.values()) {
			if(gene.id == null)
				continue;
			for (Transcript rna : gene.rnas.values()) {
				model	= TranscriptModel.createTranscriptModel();
				model.setAccessionNumber(rna.name);
//				System.out.println(rna.chromosom+" --> "+identifier2chromosom.get(rna.chromosom));
//				if((curChrom = ChromosomMap.identifier2chromosom.get(rna.chromosom)) != null)
//					model.setChromosome(curChrom);
//				else
//					continue;
				model.setChromosome(rna.chromosom);
				model.setGeneSymbol(gene.name);
				model.setStrand(rna.strand ? '+' : '-');
				model.setTranscriptionStart(rna.getTxStart());
				model.setTranscriptionEnd(rna.getTxEnd());
				model.setCdsStart(rna.getCdsStart());
				model.setCdsEnd(rna.getCdsEnd());
				model.setExonCount((byte)rna.exons.size());
				model.setExonStartsAndEnds(rna.getExonStarts(),rna.getExonEnds());
				if(gff_version == 3)
					model.setGeneID(Integer.parseInt(gene.id.substring(4)));
				else
				if((curid = RNA2GeneIDMapper.getGeneID(gene.id)) > 0)
					model.setGeneID(curid);
				else
					throw new InvalidAttributException("Found no valid geneID mapping for accession: "+gene.id);
				models.add(model);
			}
		}
		return models;
	}
	
	/**
	 * Adds a new Feature to the internal {@link TranscriptModelBuilder}.
	 * @param feature
	 * @throws InvalidAttributException
	 * @throws FeatureFormatException
	 */
	public void addFeature(Feature feature) throws InvalidAttributException, FeatureFormatException{
//		System.out.println(feature.toLine());
		if(identifier2chromosom.get(feature.sequence_id) == null)
			return;
		switch (feature.getType()) {
		case GENE:
			processGene(feature);
			break;
		case NCRNA:
		case MRNA:
		case TRANSCRIPT:
			processRNA(feature);
			break;
		case CDS:
		case EXON:
			processSubregion(feature);
			break;

		default:
//			logger.info("Skipped Feature: "+feature.toLine());
			break;
		}
	}

	/**
	 * This method will process a {@link Feature} with the {@link FeatureType} mRNA or 
	 * transcript. These are only present in the GFF3 vers3 file format, not in version 2 
	 * or GTF files.<br>
	 * Process the
	 * 1. get gene and  
	 * @param feature
	 * @throws InvalidAttributException 
	 */	
	private void processRNA(Feature feature) throws InvalidAttributException {
		// gene and transcript ids
		curGeneID	= feature.getAttribute("Parent");
		curRnaID	= feature.getAttribute("ID");
//		System.out.println("curRNAID: "+curRnaID);
		// update mappings
		rna2gene.put(curRnaID, curGeneID);
		curGene = genes.get(curGeneID);
		if(curGene.rnas == null)
			curGene.rnas = new HashMap<String, Transcript>();
		// populate the new transcript
		curRna = new Transcript();
		curRna.start 		= feature.getStart();
		curRna.end 			= feature.getEnd();
		curRna.id			= curRnaID;
		curRna.name			= feature.getAttribute("Name");
		curRna.chromosom	= identifier2chromosom.get(feature.getSequence_id());
		if(curGene.chromosom != curRna.chromosom){
//			throw new InvalidAttributException("The chromosome/sequenceID of the gene and transcript do not match: "+curGene.chromosom+ " != "+curRna.chromosom+"\n"+feature);
			return;
		}
		curRna.strand		= feature.getStrand();
		// check strand of transcript and gene
		if(curGene.strand != feature.getStrand()){
//			throw new InvalidAttributException("The strand of the gene and transcript do not match: "+curGene.strand+ " != "+feature.getStrand()+"\n"+feature);
			return;
		}
		// add transcript to the gene
		curGene.rnas.put(curRnaID, curRna);
	}
	
	/**
	 * Processes a {@link Feature} of type CDS or EXON
	 * @param feature
	 */
	private void processSubregion(Feature feature) {
		int index;
		if(gff_version == 3){
			curID		= feature.getAttribute("ID");
			curRnaID	= feature.getAttribute("Parent");
			curGeneID	= rna2gene.get(curRnaID);
		}else{
			curID		= "sub"+(subregion_Index++);
			curRnaID	= feature.getAttribute("transcript_id");
			curGeneID	= feature.getAttribute("gene_id");
			if((curGeneName = feature.getAttribute("gene_name")) == null)
				curGeneName = curGeneID;
		}
//		System.out.println("Gene: "+curGeneID+"\tRNA: "+curRnaID);
//		System.out.println(feature.toLine());
		// check if there is more than one parent
		if(curRnaID.contains(",")){
			logger.severe("More than one Parent. Will only link to the first.");
			curRnaID	= curRnaID.substring(0,curRnaID.indexOf(","));
		}
//		System.out.println(curGeneID);	
		// if the gene is not known yet --> add
		if(!genes.containsKey(curGeneID))
			genes.put(curGeneID, new Gene(curGeneID,curGeneName,identifier2chromosom.get(feature.sequence_id),feature.strand));
		// get Gene
		curGene	= genes.get(curGeneID);
		// if the RNA is unknown --> add to map and gene
		if(!rna2gene.containsKey(curRnaID)){
			rna2gene.put(curRnaID, curGeneID);
			curGene.rnas.put(curRnaID, new Transcript(curRnaID,curRnaID,identifier2chromosom.get(feature.sequence_id),feature.strand));
		}
		// get RNA
		curRna	= curGene.rnas.get(curRnaID);
		
		if(curRna == null || curGene.id == null)
			return;
		
		// now finally process the Subregion
		curGFF	= new GFFstruct();
		curGFF.chromosom	= identifier2chromosom.get(feature.sequence_id);
		curGFF.start	= feature.getStart();
		curGFF.end		= feature.getEnd();
		curGFF.strand	= feature.getStrand();
		curGFF.id		= curID;
		// for CDSs
		if(feature.getType() == FeatureType.CDS){
			curGFF.frame	= feature.phase;
			if(!(curRna.cdss.contains(curGFF)))
					curRna.cdss.add(curGFF);
		}
		// for Exons
		if(feature.getType() == FeatureType.EXON){
			if((index = curGene.exons.indexOf(curGFF)) >= 0){
				curGFF	= curGene.exons.get(index);
				if(!(curRna.exons.contains(curGFF)))
					curRna.exons.add(curGFF);
			}
			else{
				curGene.exons.add(curGFF);
				curRna.exons.add(curGFF);
			}
		}
		
		curGFF.parents.add(curRna);
			
		
	}



	/**
	 * This method will process a {@link Feature} with the {@link FeatureType} gene.
	 * These are only present in the GFF3 vers3 file format, not in GTF files.<br>
	 * Process the 
	 * @param feature
	 */
	private void processGene(Feature feature) {
		
		// get geneID
		curGeneID = feature.getAttribute("ID");
		// add to collection if not already known
		if(!genes.containsKey(curGeneID)){
			genes.put(curGeneID, new Gene());
//			System.out.println("Added gene with ID: "+curGeneID);
		}
		curGene = this.genes.get(curGeneID);
		curGene.strand 	= feature.getStrand();
		curGene.start	= feature.getStart();
		curGene.end		= feature.getEnd();
		curGene.chromosom = identifier2chromosom.get(feature.sequence_id);
		// extract the Genesymbol
		if(feature.getAttribute("Name") != null)
			curGene.name = feature.getAttribute("Name");
		// extract Xreferences
//		if(feature.getAttribute("Dbxref") != null)
//			extractXreferences(feature.getAttribute("Dbxref"));
		curGene.id	= curGeneID;
	}


	/**
	 * Extracts the database cross references.
	 * The list of cross references is split up and processed.
	 * Processed fields:<br>
	 * <UL>
	 * 	<LI> GeneID - this is corresponding the EntrezGene/LocalLink ID
	 * </UL>
	 * @param xrefs
	 */
	private void extractXreferences(String xrefs) {
		fields = xrefs.split(",");
		for (String xref : fields) {
			subfields = xref.split(":");
			if(subfields.length < 2)
				continue;
			// only for genes - gene_id
			if(subfields[0].equals("GeneID"))
				curGene.id = subfields[1];
//			// only for mRNAs
//			if(subfields[0].equals("Genbank"))
//				curTrans.
		}
	}



	/**
	 * This is the implementaion for the Subregions like Exons and CDS.
	 * The Comparator now only checks the chromosom and start and stop. This will be fine until 
	 * there occurs a subregion with the same location but on the other strand.
	 * TODO add strand specificity to the comparator 
	 * @author mjaeger
	 * @version 0.1
	 */
	private class GFFstruct implements Comparable<GFFstruct>{
		ArrayList<GFFstruct> parents;
		byte chromosom;
		byte frame;
		String name;
		String id;
		int start	= Integer.MAX_VALUE;
		int end		= Integer.MIN_VALUE;
		boolean strand;
		
		public GFFstruct() {
			parents	= new ArrayList<TranscriptModelBuilder.GFFstruct>();
		}
		
		/**
		 * Implementation of the compare function for the Comparator.
		 * A exon is equal if the chromosom and the start and end positions are the same.
		 * According to the used {@link Collator}, the exon is smaller/bigger if the chromosom
		 * differs.
		 */
                @Override
		public int compareTo(GFFstruct o) {
			if(chromosom == o.chromosom){
				
				if(start == o.start){
					if(end == o.end)
						return 0;
					else{
						if(end < o.end)
							return -1;
						else
							return 1;
					}
				}else{
					if(start < o.start)
						return -1;
					else
						return 1;
				}
			}else{
				if(chromosom < o.chromosom)
					return -1;
				else
					return 1;
			}
		}
	}
	
	private class Transcript extends GFFstruct {
		
		int cdsStart	= Integer.MAX_VALUE;
		int cdsEnd		= Integer.MIN_VALUE;
//		HashMap<String,GFFstruct> exons;
//		HashMap<String,GFFstruct> cdss;
		ArrayList<GFFstruct> exons;
		ArrayList<GFFstruct> cdss;
		
		public Transcript(String id, String name, byte chr,boolean strand) {
			this.id	= id;
			this.name	= name;
			this.chromosom	= chr;
			this.strand	= strand;
			exons	= new ArrayList<TranscriptModelBuilder.GFFstruct>();
			cdss	= new ArrayList<TranscriptModelBuilder.GFFstruct>();
		}
		
		public Transcript() {
			exons	= new ArrayList<TranscriptModelBuilder.GFFstruct>();
			cdss	= new ArrayList<TranscriptModelBuilder.GFFstruct>();
		}
		
		/**
		 * Returns the accending ordered start positions of the exons. 
		 * @return exons start indices  (1-based, including)
		 */
		public int[] getExonStarts() {
			int i	=0;
			int[] starts	= new int[exons.size()];
			Collections.sort(exons);
			for (GFFstruct exon : exons) {
				starts[i++]	= exon.start;
			}
			return starts;
		}
		/**
		 * Returns the accending ordert end positions of the exons. 
		 * @return exons end indices  (1-based, including)
		 */
		public int[] getExonEnds() {
			int i	=0;
			int[] ends	= new int[exons.size()];
			Collections.sort(exons);
			for (GFFstruct exon : exons) {
				ends[i++]	= exon.end;
			}
			return ends;
		}

		/**
		 * Returns the smallest exon start.
		 * @return transcription start index (1-based, including)
		 */
		int getTxStart(){
			if(start == Integer.MAX_VALUE)
				for (GFFstruct exon : exons) 
					if(start > exon.start)
						start = exon.start;
			return start;
		}
		
		/**
		 * Returns the highest exon end index.
		 * @return transcription end index (1-based, including)
		 */
		int getTxEnd(){
			if(end == Integer.MIN_VALUE)
				for (GFFstruct exon : exons) 
					if(end < exon.end)
						end = exon.end;
			return end;
		}
		
		/**
		 * Returns the highest cds start index.<br>
		 * The index can be extended when the phase of the CDS feature
		 * contains an offset.
		 * @return translation start index (1-based, including)
		 */
		int getCdsStart(){
			if(cdsStart == Integer.MAX_VALUE)
				for (GFFstruct cds : cdss) 
					if(cdsStart > cds.start){
						cdsStart = cds.start;
						if(cds.strand){
							cdsStart -= (3-cds.frame)%3;
						}
					}
			if(cdsStart == Integer.MAX_VALUE)
				cdsStart = getTxStart()+1;
			return cdsStart;
		}
		
		/**
		 * Returns the highest cds end  index.<br>
		 * The index can be extended when the phase of the CDS feature
		 * contains an offset.
		 * @return translation end index (1-based, including)
		 */
		int getCdsEnd(){
			if(cdsEnd == Integer.MIN_VALUE)
				for (GFFstruct cds : cdss) 
					if(cdsEnd < cds.end){
						cdsEnd = cds.end;
						if(!cds.strand){
							cdsEnd += (3-cds.frame)%3;
						}
					}
			if(cdsEnd == Integer.MIN_VALUE)
				cdsEnd = getTxStart();
			return cdsEnd;
		}
	}

	private class Gene extends GFFstruct {

//		String ensemblID;
//		String refseqID;
//		String entrezGeneID;
		boolean strand;
//		HashMap<String,GFFstruct> exons;
//		HashMap<String,GFFstruct> cdss;
		ArrayList<GFFstruct> exons;
//		ArrayList<GFFstruct> cdss;
		HashMap<String,Transcript> rnas;
		
		public Gene(String id, String name, byte chr, boolean strand) {
			this.id	= id;
			this.name	= name;
			this.chromosom	= chr;
			this.strand	= strand;
			exons	= new ArrayList<TranscriptModelBuilder.GFFstruct>();
			rnas	= new HashMap<String, TranscriptModelBuilder.Transcript>();
			
		}
		
		public Gene() {
			exons	= new ArrayList<TranscriptModelBuilder.GFFstruct>();
			rnas	= new HashMap<String, TranscriptModelBuilder.Transcript>();
		}
		
                @Override
		public String toString(){
			return String.format("id: %s\tname: %s\tchr: %s\tstrand: %b\tnexons: %d\tnrna: %d", id, name, chromosom,strand,exons.size(),rnas.size());
		}
		
	}

	public void setGffversion(int gff_version) {
		this.gff_version= gff_version; 	
	}

}
