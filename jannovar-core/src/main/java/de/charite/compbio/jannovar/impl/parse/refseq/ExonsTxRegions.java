package de.charite.compbio.jannovar.impl.parse.refseq;

import com.google.common.collect.ImmutableMap;

import de.charite.compbio.jannovar.data.ReferenceDictionary;
import de.charite.compbio.jannovar.impl.parse.gtfgff.FeatureRecord;
import de.charite.compbio.jannovar.reference.GenomeInterval;
import de.charite.compbio.jannovar.reference.Strand;
import de.charite.compbio.jannovar.reference.TranscriptModelBuilder;

public class ExonsTxRegions {
    /*
     * Contig name mappings
     */
    public final ImmutableMap<String, Integer> contigDict;

    private ReferenceDictionary refDict;

    public ExonsTxRegions(ReferenceDictionary refDic) {
        this.refDict = refDic;
        this.contigDict = refDic.getContigNameToID();
    }

    public void updateExonsTxRegions(FeatureRecord record, TranscriptModelBuilder builder) {
        Strand strand = parseStrand(record);
        if (record.getType().equals("exon")) {
            GenomeInterval exon = buildGenomeInterval(record, strand);
            GenomeInterval txRegion = updateGenomeInterval(exon, builder.getTXRegion());
            builder.setTXRegion(txRegion);
            builder.addExonRegion(exon);
        } else if ("CDS".equals(record.getType()) || "stop_codon".equals(record.getType())) {
            GenomeInterval cds = buildGenomeInterval(record, strand);
            GenomeInterval cdsRegion = updateGenomeInterval(cds, builder.getCDSRegion());
            builder.setCDSRegion(cdsRegion);
        }
    }

    private GenomeInterval updateGenomeInterval(GenomeInterval latest, GenomeInterval existing) {
        return existing == null ? latest : existing.union(latest);
    }

    private Strand parseStrand(FeatureRecord record) {
        return (record.getStrand() == FeatureRecord.Strand.FORWARD) ? Strand.FWD : Strand.REV;
    }

    private GenomeInterval buildGenomeInterval(FeatureRecord record, Strand strand) {
        int chrom = contigDict.get(record.getSeqID());
        GenomeInterval interval = new GenomeInterval(refDict, Strand.FWD, chrom, record.getBegin(), record.getEnd());
        // CAUTION! GFF record begin and end are listed using the FORWARD strand, so
        // this needs adjusting-post build
        // rather than being supplied in the constructor.
        return interval.withStrand(strand);
    }
}