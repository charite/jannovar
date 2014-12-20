package jannovar.gff;

import jannovar.common.FeatureType;
import jannovar.exception.FeatureFormatException;
import jannovar.exception.InvalidAttributException;
import jannovar.io.ReferenceDictionary;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Processes {@link Feature} objects for {@link TranscriptModelBuilder}.
 *
 * Implementation class that groups the features into RNAs/Genes.
 *
 * @author Marten Jaeger <marten.jaeger@charite.de>
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
final class FeatureProcessor {

	/** {@link Logger} to use for logging */
	private static final Logger LOGGER = Logger.getLogger(GFFParser.class.getSimpleName());

	/** GFF version to assume */
	private final GFFVersion gffVersion;

	/** reference dictionary to use */
	ReferenceDictionary refDict;

	/** counter for the subregion index (used for auto-generating names) */
	private int subregionIndex = 0;

	private String curGeneName;
	private String curGeneID;
	private String curRnaID;
	private String curID;

	private HashMap<String, Gene> genes;
	private HashMap<String, String> rna2gene;

	private Gene curGene;
	private Transcript curRna;
	private GFFStruct curGFF;

	private String[] fields;
	private String[] subfields;

	FeatureProcessor(GFFVersion gffVersion, ReferenceDictionary refDict) {
		this.gffVersion = gffVersion;
		this.refDict = refDict;
		this.genes = new HashMap<String, Gene>();
		this.rna2gene = new HashMap<String, String>();
	}

	/**
	 * Process all features.
	 *
	 * @throws FeatureFormatException
	 *             on problems with the feature format
	 * @throws InvalidAttributException
	 *             on problems with attributes
	 */
	HashMap<String, Gene> run(ArrayList<Feature> featureList) throws InvalidAttributException,
			FeatureFormatException {
		for (Feature f : featureList)
			addFeature(f);
		return genes;
	}

	/**
	 * Perform internal feature processing.
	 *
	 * @param feature
	 *            feature to add and process
	 * @throws InvalidAttributException
	 *             on problems with the attributes
	 * @throws FeatureFormatException
	 *             on problems with the feature format
	 */
	private void addFeature(Feature feature) throws InvalidAttributException, FeatureFormatException {
		// System.out.println(feature.toLine());
		if (refDict.contigID.get(feature.getSequenceID()) == null)
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
		case STOP_CODON:
			processSubregion(feature);
			break;
		default:
			// logger.info("Skipped Feature: "+feature.toLine());
			break;
		}
	}

	/**
	 * This method will process a {@link Feature} with the {@link FeatureType} mRNA or transcript.
	 *
	 * These are only present in the GFF3 vers3 file format, not in version 2 or GTF files. Process the 1. get gene and
	 *
	 * @param feature
	 * @throws InvalidAttributException
	 */
	private void processRNA(Feature feature) throws InvalidAttributException {
		// gene and transcript ids
		curGeneID = feature.getAttributes().get("Parent");
		curRnaID = feature.getAttributes().get("ID");
		// System.out.println("curRNAID: "+curRnaID);
		// update mappings
		rna2gene.put(curRnaID, curGeneID);
		curGene = genes.get(curGeneID);
		if (curGene.rnas == null)
			curGene.rnas = new HashMap<String, Transcript>();
		// populate the new transcript
		curRna = new Transcript();
		curRna.start = feature.getStart();
		curRna.end = feature.getEnd();
		curRna.id = curRnaID;
		curRna.name = feature.getAttributes().get("Name");
		curRna.chromosom = refDict.contigID.get(feature.getSequenceID()).byteValue();
		if (curGene.chromosom != curRna.chromosom) {
			// throw new
			// InvalidAttributException("The chromosome/sequenceID of the gene and transcript do not match: "+curGene.chromosom+
			// " != "+curRna.chromosom+"\n"+feature);
			return;
		}
		curRna.strand = feature.getStrand();
		// check strand of transcript and gene
		if (curGene.strand != feature.getStrand()) {
			// throw new InvalidAttributException("The strand of the gene and transcript do not match: "+curGene.strand+
			// " != "+feature.getStrand()+"\n"+feature);
			return;
		}
		// add transcript to the gene
		curGene.rnas.put(curRnaID, curRna);
	}

	/**
	 * Processes a {@link Feature} of type CDS or EXON
	 *
	 * @param feature
	 */
	private void processSubregion(Feature feature) {
		int index;
		if (gffVersion.version == 3) {
			curID = feature.getAttributes().get("ID");
			curRnaID = feature.getAttributes().get("Parent");
			curGeneID = rna2gene.get(curRnaID);
		} else {
			curID = "sub" + (subregionIndex++);
			curRnaID = feature.getAttributes().get("transcript_id");
			curGeneID = feature.getAttributes().get("gene_id");
			if ((curGeneName = feature.getAttributes().get("gene_name")) == null)
				curGeneName = curGeneID;
		}
		// System.out.println("Gene: "+curGeneID+"\tRNA: "+curRnaID);
		// System.out.println(feature.toLine());
		// check if there is more than one parent
		if (curRnaID.contains(",")) {
			LOGGER.severe("More than one Parent. Will only link to the first.");
			curRnaID = curRnaID.substring(0, curRnaID.indexOf(","));
		}
		// System.out.println(curGeneID);
		// if the gene is not known yet --> add
		if (!genes.containsKey(curGeneID))
			genes.put(curGeneID, new Gene(curGeneID, curGeneName, refDict.contigID.get(feature.getSequenceID())
					.byteValue(), feature.getStrand()));
		// get Gene
		curGene = genes.get(curGeneID);
		// if the RNA is unknown --> add to map and gene
		if (!rna2gene.containsKey(curRnaID)) {
			rna2gene.put(curRnaID, curGeneID);
			curGene.rnas.put(curRnaID, new Transcript(curRnaID, curRnaID, refDict.contigID.get(feature.getSequenceID())
					.byteValue(), feature.getStrand()));
		}
		// get RNA
		curRna = curGene.rnas.get(curRnaID);

		if (curRna == null || curGene.id == null)
			return;

		// now finally process the Subregion
		curGFF = new GFFStruct();
		curGFF.chromosom = refDict.contigID.get(feature.getSequenceID()).byteValue();
		curGFF.start = feature.getStart();
		curGFF.end = feature.getEnd();
		curGFF.strand = feature.getStrand();
		curGFF.id = curID;
		// for Stop_Codonss
		if (feature.getType() == FeatureType.STOP_CODON) {
			curGFF.frame = feature.getPhase();
			if (!(curRna.cdss.contains(curGFF)))
				curRna.cdss.add(curGFF);
		}
		// for CDSs
		if (feature.getType() == FeatureType.CDS) {
			curGFF.frame = feature.getPhase();
			if (!(curRna.cdss.contains(curGFF)))
				curRna.cdss.add(curGFF);
		}
		// for Exons
		if (feature.getType() == FeatureType.EXON) {
			if ((index = curGene.exons.indexOf(curGFF)) >= 0) {
				curGFF = curGene.exons.get(index);
				if (!(curRna.exons.contains(curGFF)))
					curRna.exons.add(curGFF);
			} else {
				curGene.exons.add(curGFF);
				curRna.exons.add(curGFF);
			}
		}

		curGFF.parents.add(curRna);

	}

	/**
	 * This method will process a {@link Feature} with the {@link FeatureType} gene. These are only present in the GFF3
	 * vers3 file format, not in GTF files.<br>
	 * Process the
	 *
	 * @param feature
	 */
	private void processGene(Feature feature) {

		// get geneID
		curGeneID = feature.getAttributes().get("ID");
		// add to collection if not already known
		if (!genes.containsKey(curGeneID)) {
			genes.put(curGeneID, new Gene());
			// System.out.println("Added gene with ID: "+curGeneID);
		}
		curGene = this.genes.get(curGeneID);
		curGene.strand = feature.getStrand();
		curGene.start = feature.getStart();
		curGene.end = feature.getEnd();
		curGene.chromosom = refDict.contigID.get(feature.getSequenceID()).byteValue();
		// extract the Genesymbol
		if (feature.getAttributes().get("Name") != null)
			curGene.name = feature.getAttributes().get("Name");
		// extract Xreferences
		// if(feature.getAttribute("Dbxref") != null)
		// extractXreferences(feature.getAttribute("Dbxref"));
		curGene.id = curGeneID;
	}

	/**
	 * Extracts the database cross references. The list of cross references is split up and processed. Processed fields:<br>
	 * <UL>
	 * <LI>GeneID - this is corresponding the EntrezGene/LocalLink ID
	 * </UL>
	 *
	 * @param xrefs
	 */
	private void extractXreferences(String xrefs) {
		fields = xrefs.split(",");
		for (String xref : fields) {
			subfields = xref.split(":");
			if (subfields.length < 2)
				continue;
			// only for genes - gene_id
			if (subfields[0].equals("GeneID"))
				curGene.id = subfields[1];
			// // only for mRNAs
			// if(subfields[0].equals("Genbank"))
			// curTrans.
		}
	}

	/**
	 * This is the implementaion for the Subregions like Exons and CDS. The Comparator now only checks the chromosom and
	 * start and stop. This will be fine until there occurs a subregion with the same location but on the other strand.
	 *
	 * TODO(mjaeger) add strand specificity to the comparator
	 */
	class GFFStruct implements Comparable<GFFStruct> {
		ArrayList<GFFStruct> parents;
		byte chromosom;
		byte frame;
		String name;
		String id;
		int start = Integer.MAX_VALUE;
		int end = Integer.MIN_VALUE;
		boolean strand;

		public GFFStruct() {
			parents = new ArrayList<GFFStruct>();
		}

		/**
		 * Implementation of the compare function for the Comparator. A exon is equal if the chromosom and the start and
		 * end positions are the same. According to the used {@link Collator}, the exon is smaller/bigger if the
		 * chromosom differs.
		 */
		@Override
		public int compareTo(GFFStruct o) {
			if (chromosom == o.chromosom) {

				if (start == o.start) {
					if (end == o.end)
						return 0;
					else {
						if (end < o.end)
							return -1;
						else
							return 1;
					}
				} else {
					if (start < o.start)
						return -1;
					else
						return 1;
				}
			} else {
				if (chromosom < o.chromosom)
					return -1;
				else
					return 1;
			}
		}

		/** @return length of the region */
		public int length() {
			return end - start + 1;
		}
	}

	class Transcript extends GFFStruct {

		int cdsStart = Integer.MAX_VALUE;
		int cdsEnd = Integer.MIN_VALUE;
		// HashMap<String,GFFstruct> exons;
		// HashMap<String,GFFstruct> cdss;
		ArrayList<GFFStruct> exons;
		ArrayList<GFFStruct> cdss;

		public Transcript(String id, String name, byte chr, boolean strand) {
			this.id = id;
			this.name = name;
			this.chromosom = chr;
			this.strand = strand;
			exons = new ArrayList<GFFStruct>();
			cdss = new ArrayList<GFFStruct>();
		}

		public Transcript() {
			exons = new ArrayList<GFFStruct>();
			cdss = new ArrayList<GFFStruct>();
		}

		/**
		 * Returns the accending ordered start positions of the exons.
		 *
		 * @return exons start indices (1-based, including)
		 */
		public int[] getExonStarts() {
			int i = 0;
			int[] starts = new int[exons.size()];
			Collections.sort(exons);
			for (GFFStruct exon : exons) {
				starts[i++] = exon.start;
			}
			return starts;
		}

		/**
		 * Returns the accending ordert end positions of the exons.
		 *
		 * @return exons end indices (1-based, including)
		 */
		public int[] getExonEnds() {
			int i = 0;
			int[] ends = new int[exons.size()];
			Collections.sort(exons);
			for (GFFStruct exon : exons) {
				ends[i++] = exon.end;
			}
			return ends;
		}

		/**
		 * Returns the smallest exon start.
		 *
		 * @return transcription start index (1-based, including)
		 */
		int getTxStart() {
			if (start == Integer.MAX_VALUE)
				for (GFFStruct exon : exons)
					if (start > exon.start)
						start = exon.start;
			return start;
		}

		/**
		 * Returns the highest exon end index.
		 *
		 * @return transcription end index (1-based, including)
		 */
		int getTxEnd() {
			if (end == Integer.MIN_VALUE)
				for (GFFStruct exon : exons)
					if (end < exon.end)
						end = exon.end;
			return end;
		}

		/**
		 * Returns the highest cds start index.<br>
		 * The index can be extended when the phase of the CDS feature contains an offset.
		 *
		 * @return translation start index (1-based, including)
		 */
		int getCdsStart() {
			if (cdsStart == Integer.MAX_VALUE)
				for (GFFStruct cds : cdss)
					if (cdsStart > cds.start) {
						cdsStart = cds.start;
						if (cds.strand) {
							cdsStart -= (3 - cds.frame) % 3;
						} else {
							cdsStart -= 3 - ((cds.length() - cds.frame) % 3);
						}
					}
			if (cdsStart == Integer.MAX_VALUE)
				cdsStart = getTxStart() + 1;
			return cdsStart;
		}

		/**
		 * Returns the highest cds end index.<br>
		 * The index can be extended when the phase of the CDS feature contains an offset.
		 *
		 * @return translation end index (1-based, including)
		 */
		int getCdsEnd() {
			if (cdsEnd == Integer.MIN_VALUE)
				for (GFFStruct cds : cdss)
					if (cdsEnd < cds.end) {
						cdsEnd = cds.end;
						if (cds.strand) {
							cdsEnd += 3 - ((cds.length() - cds.frame) % 3);
						} else {
							cdsEnd += (3 - cds.frame) % 3;
						}
					}
			if (cdsEnd == Integer.MIN_VALUE)
				cdsEnd = getTxStart();
			return cdsEnd;
		}
	}

	class Gene extends GFFStruct {

		// String ensemblID;
		// String refseqID;
		// String entrezGeneID;
		boolean strand;
		// HashMap<String,GFFstruct> exons;
		// HashMap<String,GFFstruct> cdss;
		ArrayList<GFFStruct> exons;
		// ArrayList<GFFstruct> cdss;
		HashMap<String, Transcript> rnas;

		public Gene(String id, String name, byte chr, boolean strand) {
			this.id = id;
			this.name = name;
			this.chromosom = chr;
			this.strand = strand;
			exons = new ArrayList<GFFStruct>();
			rnas = new HashMap<String, Transcript>();

		}

		public Gene() {
			exons = new ArrayList<GFFStruct>();
			rnas = new HashMap<String, Transcript>();
		}

		@Override
		public String toString() {
			return String.format("id: %s\tname: %s\tchr: %s\tstrand: %b\tnexons: %d\tnrna: %d", id, name, chromosom,
					strand, exons.size(), rnas.size());
		}

	}
}
