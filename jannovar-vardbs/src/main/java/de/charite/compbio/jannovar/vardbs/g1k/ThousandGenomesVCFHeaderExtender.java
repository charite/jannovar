package de.charite.compbio.jannovar.vardbs.g1k;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

/**
 * Helper class for extending {@link VCFHeader}s for thousand genomes annotations.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class ThousandGenomesVCFHeaderExtender extends VCFHeaderExtender {

  public ThousandGenomesVCFHeaderExtender(DBAnnotationOptions options) {
    super(options);
  }

  @Override
  public String getDefaultPrefix() {
    return "G1K_";
  }

  @Override
  public void addHeaders(VCFHeader header, String prefix) {
    // Add headers for exactly matching variants
    for (ThousandGenomesPopulation pop : ThousandGenomesPopulation.values()) {
      addACHeader(header, prefix, "", "", pop);
      addANHeader(header, prefix, "", "", pop);
      addAFHeader(header, prefix, "", "", pop);
      addACHetHeader(header, prefix, "", "", pop);
      addACHomHeader(header, prefix, "", "", pop);
      addACHemiHeader(header, prefix, "", "", pop);
    }
    addPopmaxHeader(header, prefix, "", "");

    if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching()) {
      // Add headers for overlapping variants
      final String note = " (requiring no genotype match, only position overlap)";
      for (ThousandGenomesPopulation pop : ThousandGenomesPopulation.values()) {
        addACHeader(header, prefix, "OVL_", note, pop);
        addANHeader(header, prefix, "OVL_", note, pop);
        addAFHeader(header, prefix, "OVL_", note, pop);
        addACHetHeader(header, prefix, "OVL_", note, pop);
        addACHomHeader(header, prefix, "OVL_", note, pop);
        addACHemiHeader(header, prefix, "OVL_", note, pop);
      }
      addPopmaxHeader(header, prefix, "OVL_", note);
    }
  }

  /** Add header with allele frequency */
  private void addAFHeader(
      VCFHeader header,
      String prefix,
      String idInfix,
      String noteInfix,
      ThousandGenomesPopulation pop) {
    String popName;
    if (pop == ThousandGenomesPopulation.ALL) popName = "all populations";
    else popName = pop + " / " + pop.getLabel() + " population";
    VCFInfoHeaderLine line =
        new VCFInfoHeaderLine(
            prefix + idInfix + "AF_" + pop,
            VCFHeaderLineCount.A,
            VCFHeaderLineType.Float,
            "Frequency observed in thousand genomes data set in " + popName + noteInfix);
    header.addMetaDataLine(line);
  }

  /** Add header with chromosome count */
  private void addANHeader(
      VCFHeader header,
      String prefix,
      String idInfix,
      String noteInfix,
      ThousandGenomesPopulation pop) {
    // TODO: change counts to 1 for AN?
    String popName;
    if (pop == ThousandGenomesPopulation.ALL) popName = "all populations";
    else popName = pop + " / " + pop.getLabel() + " population";
    VCFInfoHeaderLine line =
        new VCFInfoHeaderLine(
            prefix + idInfix + "AN_" + pop,
            VCFHeaderLineCount.A,
            VCFHeaderLineType.Integer,
            "Overall number of positions/chromosomes with coverage in " + popName + noteInfix);
    header.addMetaDataLine(line);
  }

  /** Add header with allele counts */
  private void addACHeader(
      VCFHeader header,
      String prefix,
      String idInfix,
      String noteInfix,
      ThousandGenomesPopulation pop) {
    String popName;
    if (pop == ThousandGenomesPopulation.ALL) popName = "all populations";
    else popName = pop + " / " + pop.getLabel() + " population";
    VCFInfoHeaderLine line =
        new VCFInfoHeaderLine(
            prefix + idInfix + "AC_" + pop,
            VCFHeaderLineCount.A,
            VCFHeaderLineType.Integer,
            "Overall number of observed alleles in " + popName + noteInfix);
    header.addMetaDataLine(line);
  }

  /** Add header with het allele counts */
  private void addACHetHeader(
      VCFHeader header,
      String prefix,
      String idInfix,
      String noteInfix,
      ThousandGenomesPopulation pop) {
    String popName;
    if (pop != ThousandGenomesPopulation.ALL) popName = "all populations";
    else popName = pop + " / " + pop.getLabel() + " population";
    VCFInfoHeaderLine line =
        new VCFInfoHeaderLine(
            prefix + idInfix + "Het_" + pop,
            VCFHeaderLineCount.A,
            VCFHeaderLineType.Integer,
            "Overall number of observed heterozygous alleles in " + popName + noteInfix);
    header.addMetaDataLine(line);
  }

  /** Add header with hom allele counts */
  private void addACHomHeader(
      VCFHeader header,
      String prefix,
      String idInfix,
      String noteInfix,
      ThousandGenomesPopulation pop) {
    String popName;
    if (pop == ThousandGenomesPopulation.ALL) popName = "all populations";
    else popName = pop + " / " + pop.getLabel() + " population";
    VCFInfoHeaderLine line =
        new VCFInfoHeaderLine(
            prefix + idInfix + "Hom_" + pop,
            VCFHeaderLineCount.A,
            VCFHeaderLineType.Integer,
            "Overall number of observed homozygous alleles in " + popName + noteInfix);
    header.addMetaDataLine(line);
  }

  /** Add header with hemi allele counts */
  private void addACHemiHeader(
      VCFHeader header,
      String prefix,
      String idInfix,
      String noteInfix,
      ThousandGenomesPopulation pop) {
    String popName;
    if (pop == ThousandGenomesPopulation.ALL) popName = "all populations";
    else popName = pop + " / " + pop.getLabel() + " population";
    VCFInfoHeaderLine line =
        new VCFInfoHeaderLine(
            prefix + idInfix + "Hemi_" + pop,
            VCFHeaderLineCount.A,
            VCFHeaderLineType.Integer,
            "Overall number of observed hemizygous alleles in " + popName + noteInfix);
    header.addMetaDataLine(line);
  }

  /** Add POPMAX name */
  private void addPopmaxHeader(VCFHeader header, String prefix, String idInfix, String noteInfix) {
    VCFInfoHeaderLine line =
        new VCFInfoHeaderLine(
            prefix + idInfix + "POPMAX",
            VCFHeaderLineCount.A,
            VCFHeaderLineType.String,
            "Population with the max AF" + noteInfix);
    header.addMetaDataLine(line);
  }
}
