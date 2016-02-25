package de.charite.compbio.jannovar.impl.parse.gff;

import java.util.ArrayList;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.parse.InvalidAttributeException;
import de.charite.compbio.jannovar.impl.parse.gff.FeatureProcessor.Gene;
import de.charite.compbio.jannovar.impl.parse.gff.FeatureProcessor.Transcript;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;

/**
 * This is the builder for the {@link TranscriptModelBuilder}s from GFF files.
 *
 * @author <a href="mailto:marten.jaeger@charite.de">Marten Jaeger</a>
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public final class TranscriptModelBuilderFactory {

	/** {@link Logger} to use for logging */
	private static final Logger LOGGER = LoggerFactory.getLogger(TranscriptModelBuilderFactory.class);

	/** Whether or not the data comes from GTF */
	private final boolean isGTF;
	/** {@link GFFVersion} to assume for building transcripts from Feature objects */
	@SuppressWarnings("unused")
	private final GFFVersion gffVersion;

	/** reference dictionary to use */
	private final ReferenceDictionary refDict;

	public TranscriptModelBuilderFactory(boolean isGTF, GFFVersion gffVersion, ReferenceDictionary refDict) {
		this.isGTF = isGTF;
		this.gffVersion = gffVersion;
		this.refDict = refDict;
	}

	/**
	 * Forward to {@link #buildTranscriptModelBuilders(HashMap, boolean)}, setting the second parameter to
	 * <code>false</code>.
	 */
	public ArrayList<TranscriptModelBuilder> buildTranscriptModelBuilders(HashMap<String, Gene> genes)
			throws InvalidAttributeException {
		return buildTranscriptModelBuilders(genes, false);
	}

	/**
	 * Process the <code>genes</code> and convert into an {@link ArrayList} of {@link TranscriptModelBuilder}s.
	 *
	 * @param genes
	 *            the name/Gene map to build the {@link TranscriptModelBuilder} objects for
	 * @param useOnlyCurated
	 *            whether or not to only return curated transcripts
	 * @return list of {@link TranscriptModelBuilder} objects
	 * @throws InvalidAttributeException
	 *             on problems with invalid attributes
	 */
	public ArrayList<TranscriptModelBuilder> buildTranscriptModelBuilders(HashMap<String, Gene> genes,
			boolean useOnlyCurated) throws InvalidAttributeException {
		ArrayList<TranscriptModelBuilder> models = new ArrayList<TranscriptModelBuilder>();
		for (FeatureProcessor.Gene gene : genes.values()) {
			if (gene.id == null)
				continue;
			for (Transcript rna : gene.rnas.values()) {
				if (useOnlyCurated && !isCuratedName(rna.name))
					continue;

				TranscriptModelBuilder tib = new TranscriptModelBuilder();
				tib.setAccession(rna.name);
				tib.setGeneSymbol(gene.name);
				tib.setStrand(rna.strand ? Strand.FWD : Strand.REV);
				tib.setTXRegion(new GenomeInterval(refDict, Strand.FWD, rna.chromosom, rna.getTXStart(),
						rna.getTXEnd(), PositionType.ONE_BASED));

				// Check whether the corrected CDS start position returned from getCDSStart() is within an exon and do
				// the same for the CDS end position. The correction in these methods can lead to inconsistent positions
				// in the case of 3' and 5' UTR truncation.
				boolean cdsStartInExon = false;
				int cdsStart = rna.getCDSStart();
				for (int i = 0; i < rna.getExonStarts().length; ++i)
					cdsStartInExon = cdsStartInExon
							|| (cdsStart >= rna.getExonStarts()[i] && cdsStart <= rna.getExonEnds()[i]);
				boolean cdsEndInExon = false;
				int cdsEnd = rna.getCDSEnd();
				for (int i = 0; i < rna.getExonStarts().length; ++i)
					cdsEndInExon = cdsEndInExon || (cdsEnd >= rna.getExonStarts()[i] && cdsEnd <= rna.getExonEnds()[i]);
				if (!cdsStartInExon || !cdsEndInExon) {
					LOGGER.info("Transcript {} appears to be 3'/5' truncated. Ignoring.", new Object[] { rna.id });
					continue;
				}
				tib.setCDSRegion(new GenomeInterval(refDict, Strand.FWD, rna.chromosom, rna.getCDSStart(), rna
						.getCDSEnd(), PositionType.ONE_BASED));

				for (int i = 0; i < rna.exons.size(); ++i)
					tib.addExonRegion(new GenomeInterval(refDict, Strand.FWD, rna.chromosom, rna.getExonStarts()[i],
							rna.getExonEnds()[i], PositionType.ONE_BASED));

				tib.setGeneID(gene.id);

				models.add(tib);
			}
		}

		return models;
	}

	/**
	 * Checks whether <code>rnaName</code> is the name of a curated transcript.
	 *
	 * We consider a name being non-curated if it is <code>null</code> or begins with <code>"XM_"</code>, or
	 * <code>"XR_"</code>. In all other cases, we consider a RNA name as indicating a curated entry. This only works for
	 * RefSeq names.
	 *
	 * @param rnaName
	 *            RNA name to check for matching the "is curated" pattern
	 * @return <code>true</code> if the RNA name is curated.
	 */
	private static boolean isCuratedName(String rnaName) {
		return !((rnaName == null) || rnaName.startsWith("XM_") || rnaName.startsWith("XR_"));
	}

}
