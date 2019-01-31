package de.charite.compbio.jannovar.vardbs.g1k;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.vardbs.base.VariantContextToRecordConverter;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Helper class for the conversion of {@link VariantContext} to {@link ThousandGenomesRecord}
 * objects
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final class ThousandGenomesVariantContextToRecordConverter
    implements VariantContextToRecordConverter<ThousandGenomesRecord> {

  @Override
  public ThousandGenomesRecord convert(VariantContext vc) {
    ThousandGenomesRecordBuilder builder = new ThousandGenomesRecordBuilder();

    // Column-level properties from VCF file
    builder.setContig(vc.getContig());
    builder.setPos(vc.getStart() - 1);
    builder.setID(vc.getID());
    builder.setRef(vc.getReference().getBaseString());
    for (Allele all : vc.getAlternateAlleles()) {
      builder.getAlt().add(all.getBaseString());
    }
    builder.getFilter().addAll(vc.getFilters());
    builder.getPopmax().addAll(vc.getAttributeAsStringList("POPMAX", "."));

    // Fields from INFO VCF field

    // AN: Chromosome count
    int allAN = 0;
    for (ThousandGenomesPopulation pop : ThousandGenomesPopulation.values()) {
      if (pop == ThousandGenomesPopulation.ALL) {
        continue; // skip
      }

      if (pop == ThousandGenomesPopulation.POPMAX) {
        final List<String> lst = vc.getAttributeAsStringList("POPMAX_AN", ".");
        builder
            .getChromCounts()
            .put(
                pop,
                ImmutableList.copyOf(
                    lst.stream()
                        .map(s -> ".".equals(s) ? 0 : Integer.parseInt(s))
                        .collect(Collectors.toList())));
      } else {
        int an = vc.getAttributeAsInt(pop + "_AN", 0);
        builder.getChromCounts().put(pop, ImmutableList.of(an));
        for (int i = 0; i < vc.getAlternateAlleles().size(); ++i) {
          allAN += an;
        }
      }
    }
    builder.getChromCounts().put(ThousandGenomesPopulation.ALL, ImmutableList.of(allAN));

    // AC: Alternative allele count (+ het, hom,hemi)
    ArrayList<Integer> allAC = new ArrayList<>();
    ArrayList<Integer> allHet = new ArrayList<>();
    ArrayList<Integer> allHom = new ArrayList<>();
    ArrayList<Integer> allHemi = new ArrayList<>();
    for (int i = 0; i < vc.getAlternateAlleles().size(); ++i) {
      allAC.add(0);
      allHet.add(0);
      allHom.add(0);
      allHemi.add(0);
    }
    for (ThousandGenomesPopulation pop : ThousandGenomesPopulation.values()) {
      if (pop == ThousandGenomesPopulation.ALL) {
        continue; // skip
      }

      // AC
      List<Integer> acLst =
          vc.getAttributeAsList(pop + "_AC").stream()
              .map(x -> ".".equals(x) ? 0 : Integer.parseInt((String) x))
              .collect(Collectors.toList());
      if (!acLst.isEmpty()) {
        builder.getAlleleCounts().put(pop, acLst);
        if (pop != ThousandGenomesPopulation.POPMAX) {
          for (int i = 0; i < vc.getAlternateAlleles().size(); ++i) {
            allAC.set(i, allAC.get(i) + acLst.get(i));
          }
        }
      }

      // Hom
      List<Integer> homLst =
          vc.getAttributeAsList(pop + "_Hom").stream()
              .map(x -> ".".equals(x) ? 0 : Integer.parseInt((String) x))
              .collect(Collectors.toList());
      if (!homLst.isEmpty()) {
        builder.getAlleleHomCounts().put(pop, homLst);
        if (pop != ThousandGenomesPopulation.POPMAX) {
          for (int i = 0; i < vc.getAlternateAlleles().size(); ++i) {
            allHom.set(i, allHom.get(i) + homLst.get(i));
          }
        }
      }

      // Hemi
      List<Integer> hemiLst =
          vc.getAttributeAsList(pop + "_Hemi").stream()
              .map(x -> ".".equals(x) ? 0 : Integer.parseInt((String) x))
              .collect(Collectors.toList());
      if (!hemiLst.isEmpty()) {
        builder.getAlleleHemiCounts().put(pop, hemiLst);
        if (pop != ThousandGenomesPopulation.POPMAX) {
          for (int i = 0; i < vc.getAlternateAlleles().size(); ++i) {
            allHemi.set(i, allHemi.get(i) + hemiLst.get(i));
          }
        }
      }

      // Het
      List<Integer> hetLst =
          vc.getAttributeAsList(pop + "_Het").stream()
              .map(x -> ".".equals(x) ? 0 : Integer.parseInt((String) x))
              .collect(Collectors.toList());
      if (!hetLst.isEmpty()) {
        builder.getAlleleHemiCounts().put(pop, hemiLst);
        if (pop != ThousandGenomesPopulation.POPMAX) {
          for (int i = 0; i < vc.getAlternateAlleles().size(); ++i) {
            allHet.set(i, allHet.get(i) + hetLst.get(i));
          }
        }
      }
    }

    builder.getAlleleCounts().put(ThousandGenomesPopulation.ALL, allAC);
    if (!builder.getAlleleHetCounts().isEmpty()) {
      builder.getAlleleHetCounts().put(ThousandGenomesPopulation.ALL, allHet);
    }
    if (!builder.getAlleleHomCounts().isEmpty()) {
      builder.getAlleleHomCounts().put(ThousandGenomesPopulation.ALL, allHom);
    }
    if (!builder.getAlleleHemiCounts().isEmpty()) {
      builder.getAlleleHemiCounts().put(ThousandGenomesPopulation.ALL, allHemi);
    }

    return builder.build();
  }
}
