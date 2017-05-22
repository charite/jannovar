package de.charite.compbio.jannovar.vardbs.generic_tsv;

import de.charite.compbio.jannovar.vardbs.base.AlleleMatcher;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.base.DatabaseVariantContextProvider;
import de.charite.compbio.jannovar.vardbs.base.GenotypeMatch;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import de.charite.compbio.jannovar.vardbs.generic_tsv.GenericTSVAnnotationOptions.ValueColumnDescription;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Annotation driver class for annotations generic TSV data
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class GenericTSVAnnotationDriver implements DBAnnotationDriver {

	/** Path to dbSNP VCF file */
	private final DatabaseVariantContextProvider variantProvider;
	/** Helper objects for matching alleles */
	private final AlleleMatcher matcher;
	/** Configuration */
	private final GenericTSVAnnotationOptions options;

	public GenericTSVAnnotationDriver(String fastaPath, GenericTSVAnnotationOptions options)
			throws JannovarVarDBException {
		this.variantProvider = new GenericTSVVariantContextProvider(options);
		this.matcher = new AlleleMatcher(fastaPath);
		this.options = options;
	}

	@Override
	public VCFHeaderExtender constructVCFHeaderExtender() {
		return new GenericTSVHeaderExtender(options);
	}

	@Override
	public VariantContext annotateVariantContext(VariantContext vc) {
		VariantContextBuilder builder = new VariantContextBuilder(vc);

		// Matching and overlapping records for each allele. For the generic TSV annotation, we
		// assume that only one allele is given for each database record.
		Map<Integer, List<VariantContext>> dbRecordsMatch = null;
		Map<Integer, List<VariantContext>> dbRecordsOverlap = null;
		if (options.isReportOverlapping() && options.isReportOverlappingAsMatching()) {
			dbRecordsMatch = pickDBRecords(vc, false);
		} else {
			dbRecordsMatch = pickDBRecords(vc, true);
			dbRecordsOverlap = pickDBRecords(vc, false);
		}

		// Annotate with records with genotype matches
		for (int i = 0; i < options.getValueColumnDescriptions().size(); i++) {
			final ValueColumnDescription desc = options.getValueColumnDescriptions().get(i);
			annotateWith(vc, "", dbRecordsMatch, desc, i, builder);
		}

		// Annotate with records with overlapping positions
		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching()) {
			for (int i = 0; i < options.getValueColumnDescriptions().size(); i++) {
				final ValueColumnDescription desc = options.getValueColumnDescriptions().get(i);
				annotateWith(vc, "OVL_", dbRecordsOverlap, desc, i, builder);
			}
		}

		return builder.make();
	}

	/**
	 * Pick database records for the given {@link VariantContext} <code>vc</code>.
	 */
	private Map<Integer, List<VariantContext>> pickDBRecords(VariantContext vc,
			boolean requireGenotypeMatch) {
		final Map<Integer, List<VariantContext>> result = new HashMap<>();
		for (int i = 0; i < vc.getNAlleles(); ++i) {
			result.put(i, new ArrayList<>());
		}

		try (CloseableIterator<VariantContext> it = variantProvider.query(vc.getContig(),
				vc.getStart() - 1, vc.getEnd())) {
			while (it.hasNext()) {
				final VariantContext dbVC = it.next();
				for (int i = 0; i < vc.getNAlleles(); ++i) {
					final Collection<GenotypeMatch> matches;
					if (requireGenotypeMatch) {
						matches = matcher.matchGenotypes(vc, dbVC);
					} else {
						matches = matcher.positionOverlaps(vc, dbVC);
					}
					for (GenotypeMatch match : matches) {
						result.get(match.getObservedAllele()).add(dbVC);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Annotate <code>vc</code> with the annotating database records.
	 */
	private void annotateWith(VariantContext vc, String infix,
			Map<Integer, List<VariantContext>> dbRecords, ValueColumnDescription desc, int i,
			VariantContextBuilder builder) {
		// Prepare annotation list with one entry for each allele
		final List<Object> annotations = new ArrayList<>();
		final Map<Integer, List<Object>> values = new HashMap<>();
		for (int j = 0; j < vc.getNAlleles(); ++j) {
			annotations.add(".");
			values.put(j, new ArrayList<>());
			for (VariantContext dbRecord : dbRecords.get(j)) {
				values.get(j).add(dbRecord.getAttribute(desc.getFieldName()));
			}
		}

		// TODO: refactor this and make it prettier
		switch (desc.getValueType()) {
		case Character:
		case Flag:
		case String:
			// Only pick first available
			for (int j = 0; j < vc.getNAlleles(); ++j) {
				if (!values.get(j).isEmpty()) {
					annotations.set(j, values.get(j).get(0));
				}
			}
			break;
		case Float:
			for (int j = 0; j < vc.getNAlleles(); ++j) {
				if (!values.get(j).isEmpty()) {
					switch (desc.getAccumulationStrategy()) {
					case AVERAGE:
						annotations.set(j, values.get(j).stream().mapToDouble(x -> (Double) x)
								.average().orElse(0.0));
						break;
					case CHOOSE_FIRST:
						annotations.set(j, values.get(j).get(0));
						break;
					case CHOOSE_MAX:
					default:
						annotations.set(j, values.get(j).stream().mapToDouble(x -> (Double) x).max()
								.orElse(0.0));
						break;
					}
				}
			}
			break;
		case Integer:
			for (int j = 0; j < vc.getNAlleles(); ++j) {
				if (!values.get(j).isEmpty()) {
					switch (desc.getAccumulationStrategy()) {
					case AVERAGE:
						annotations.set(j, values.get(j).stream().mapToInt(x -> (Integer) x)
								.average().orElse(0));
						break;
					case CHOOSE_FIRST:
						annotations.set(j, values.get(j).get(0));
						break;
					case CHOOSE_MAX:
					default:
						annotations.set(j,
								values.get(j).stream().mapToInt(x -> (Integer) x).max().orElse(0));
						break;
					}
				}
			}
			break;
		default:
			break;
		}

		
		// Put annotation into variant context builder
		final String label = options.getVCFIdentifierPrefix() + infix + desc.getFieldName();
		builder.attribute(label, annotations);
	}

}
