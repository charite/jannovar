package de.charite.compbio.jannovar.vardbs.gnomad;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.vardbs.base.VariantContextToRecordConverter;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;

/**
 * Helper class for the conversion of {@link VariantContext} to {@link GnomadRecord} objects
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
final class GnomadVariantContextToRecordConverter implements VariantContextToRecordConverter<GnomadRecord> {

	@Override
	public GnomadRecord convert(VariantContext vc) {
		GnomadRecordBuilder builder = new GnomadRecordBuilder();

		// Column-level properties from VCF file
		builder.setContig(vc.getContig());
		builder.setPos(vc.getStart() - 1);
		builder.setID(vc.getID());
		builder.setRef(vc.getReference().getBaseString());
		for (Allele all : vc.getAlternateAlleles())
			builder.getAlt().add(all.getBaseString());
		builder.getFilter().addAll(vc.getFilters());
		builder.getPopmax().addAll(vc.getAttributeAsStringList("POPMAX", "."));

		// Fields from INFO VCF field

		// AN: Chromosome count
		int allAN = 0;
		for (GnomadPopulation pop : GnomadPopulation.values()) {
			if (pop == GnomadPopulation.ALL)
				continue; // skip

			if (pop == GnomadPopulation.POPMAX) {
				final List<String> lst = vc.getAttributeAsStringList("AN_POPMAX", ".");
				builder.getChromCounts().put(pop, ImmutableList.copyOf(
						lst.stream().map(s -> ".".equals(s) ? 0 : Integer.parseInt(s)).collect(Collectors.toList())));
			} else {
				int an = vc.getAttributeAsInt("AN_" + pop, 0);
				builder.getChromCounts().put(pop, ImmutableList.of(an));
				for (int i = 0; i < vc.getAlternateAlleles().size(); ++i)
					allAN += an;
			}
		}
		builder.getChromCounts().put(GnomadPopulation.ALL, ImmutableList.of(allAN));

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
		for (GnomadPopulation pop : GnomadPopulation.values()) {
			if (pop == GnomadPopulation.ALL)
				continue; // skip

			// AC
			List<Integer> acLst = vc.getAttributeAsList("AC_" + pop).stream()
					.map(x -> ".".equals(x) ? 0 : Integer.parseInt((String) x)).collect(Collectors.toList());
			if (!acLst.isEmpty()) {
				builder.getAlleleCounts().put(pop, acLst);
				if (pop != GnomadPopulation.POPMAX)
					for (int i = 0; i < vc.getAlternateAlleles().size(); ++i)
						allAC.set(i, allAC.get(i) + acLst.get(i));
			}

			// Hom
			List<Integer> homLst = vc.getAttributeAsList("Hom_" + pop).stream()
					.map(x -> ".".equals(x) ? 0 : Integer.parseInt((String) x)).collect(Collectors.toList());
			if (!homLst.isEmpty()) {
				builder.getAlleleHomCounts().put(pop, homLst);
				if (pop != GnomadPopulation.POPMAX)
					for (int i = 0; i < vc.getAlternateAlleles().size(); ++i)
						allHom.set(i, allHom.get(i) + homLst.get(i));
			}

			// Hemi
			List<Integer> hemiLst = vc.getAttributeAsList("Hemi_" + pop).stream()
					.map(x -> ".".equals(x) ? 0 : Integer.parseInt((String) x)).collect(Collectors.toList());
			if (!hemiLst.isEmpty()) {
				builder.getAlleleHemiCounts().put(pop, hemiLst);
				if (pop != GnomadPopulation.POPMAX)
					for (int i = 0; i < vc.getAlternateAlleles().size(); ++i)
						allHemi.set(i, allHemi.get(i) + hemiLst.get(i));
			}

			// Het
			List<Integer> hetList = new ArrayList<>();
			if (!acLst.isEmpty()) {
				for (int i = 0; i < acLst.size(); ++i) {
					int het = acLst.get(i);
					if (homLst.size() > i)
						het -= 2 * homLst.get(i);
					if (hemiLst.size() > i)
						het -= hemiLst.get(i);
					hetList.add(het);
				}
				builder.getAlleleHetCounts().put(pop, hetList);
				if (pop != GnomadPopulation.POPMAX)
					for (int i = 0; i < vc.getAlternateAlleles().size(); ++i)
						allHet.set(i, allHet.get(i) + hetList.get(i));
			}
		}

		builder.getAlleleCounts().put(GnomadPopulation.ALL, allAC);
		if (!builder.getAlleleHetCounts().isEmpty())
			builder.getAlleleHetCounts().put(GnomadPopulation.ALL, allHet);
		if (!builder.getAlleleHomCounts().isEmpty())
			builder.getAlleleHomCounts().put(GnomadPopulation.ALL, allHom);
		if (!builder.getAlleleHemiCounts().isEmpty())
			builder.getAlleleHemiCounts().put(GnomadPopulation.ALL, allHemi);

		return builder.build();
	}

}
