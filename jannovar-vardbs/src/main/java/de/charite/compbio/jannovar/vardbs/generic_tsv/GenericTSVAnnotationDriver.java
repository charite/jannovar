package de.charite.compbio.jannovar.vardbs.generic_tsv;

import de.charite.compbio.jannovar.vardbs.base.AlleleMatcher;
import de.charite.compbio.jannovar.vardbs.base.DBAnnotationDriver;
import de.charite.compbio.jannovar.vardbs.base.DatabaseVariantContextProvider;
import de.charite.compbio.jannovar.vardbs.base.GenotypeMatch;
import de.charite.compbio.jannovar.vardbs.base.JannovarVarDBException;
import de.charite.compbio.jannovar.vardbs.base.VCFHeaderExtender;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
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
			final String colName = options.getColumnNames().get(i);
			final GenericTSVValueColumnDescription desc = options.getValueColumnDescriptions()
					.get(colName);
			final String refColName = desc.getRefField();
			final GenericTSVValueColumnDescription refDesc = options.getValueColumnDescriptions()
					.get(refColName);
			annotateWith(vc, "", dbRecordsMatch, desc, refDesc, builder);
		}

		// Annotate with records with overlapping positions
		if (options.isReportOverlapping() && !options.isReportOverlappingAsMatching()) {
			for (int i = 0; i < options.getValueColumnDescriptions().size(); i++) {
				final String colName = options.getColumnNames().get(i);
				final GenericTSVValueColumnDescription desc = options.getValueColumnDescriptions()
						.get(colName);
				final String refColName = desc.getRefField();
				final GenericTSVValueColumnDescription refDesc = options
						.getValueColumnDescriptions().get(refColName);
				annotateWith(vc, "OVL_", dbRecordsOverlap, desc, refDesc, builder);
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
	 * Helper for comparable pairs.
	 */
	private static class LabeledValue<Label extends Comparable<Label>, Value>
			implements Comparable<LabeledValue<Label, Value>> {

		private final Label label;
		private final Value value;

		public LabeledValue(Label label, Value value) {
			this.label = label;
			this.value = value;
		}

		public Label getLabel() {
			return label;
		}

		public Value getValue() {
			return value;
		}

		@Override
		public String toString() {
			return "LabeledValue [label=" + label + ", value=" + value + "]";
		}

		@Override
		public int compareTo(LabeledValue<Label, Value> o) {
			return label.compareTo(o.getLabel());
		}

	}

	/**
	 * Annotate <code>vc</code> with the annotating database records.
	 * 
	 * @param refDesc
	 */
	private void annotateWith(VariantContext vc, String infix,
			Map<Integer, List<VariantContext>> dbRecords, GenericTSVValueColumnDescription desc,
			GenericTSVValueColumnDescription refDesc, VariantContextBuilder builder) {
		if (dbRecords.values().stream().allMatch(lst -> lst.isEmpty())) {
			return; // no annotation necessary
		}

		switch (desc.getValueType()) {
		case Character:
			switch (refDesc.getValueType()) {
			case Character:
				this.<Character, Character> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, null, null);
				break;
			case Flag:
				this.<Boolean, Character> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, null, null);
				break;
			case Float:
				this.<Double, Character> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, Double.MIN_VALUE, Double.MAX_VALUE);
				break;
			case Integer:
				this.<Integer, Character> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, Integer.MIN_VALUE, Integer.MAX_VALUE);
				break;
			case String:
			default:
				this.<String, Character> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, null, null);
				break;
			}
			break;
		case Flag:
			switch (refDesc.getValueType()) {
			case Character:
				this.<Character, Boolean> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, null, null);
				break;
			case Flag:
				this.<Boolean, Boolean> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, null, null);
				break;
			case Float:
				this.<Double, Boolean> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, Double.MIN_VALUE, Double.MAX_VALUE);
				break;
			case Integer:
				this.<Integer, Boolean> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, Integer.MIN_VALUE, Integer.MAX_VALUE);
				break;
			case String:
			default:
				this.<String, Boolean> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, null, null);
				break;
			}
			break;
		case Float:
			switch (refDesc.getValueType()) {
			case Character:
				this.<Character, Double> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, null, null);
				break;
			case Flag:
				this.<Boolean, Double> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, null, null);
				break;
			case Float:
				this.<Double, Double> annotateWithImpl(vc, infix, dbRecords, desc, refDesc, builder,
						Double.MIN_VALUE, Double.MAX_VALUE);
				break;
			case Integer:
				this.<Integer, Double> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, Integer.MIN_VALUE, Integer.MAX_VALUE);
				break;
			case String:
			default:
				this.<String, Double> annotateWithImpl(vc, infix, dbRecords, desc, refDesc, builder,
						null, null);
				break;
			}
			break;
		case Integer:
			switch (refDesc.getValueType()) {
			case Character:
				this.<Character, Integer> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, null, null);
				break;
			case Flag:
				this.<Boolean, Integer> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, null, null);
				break;
			case Float:
				this.<Double, Integer> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, Double.MIN_VALUE, Double.MAX_VALUE);
				break;
			case Integer:
				this.<Integer, Integer> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, Integer.MIN_VALUE, Integer.MAX_VALUE);
				break;
			case String:
			default:
				this.<String, Integer> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, null, null);
				break;
			}
			break;
		case String:
		default:
			switch (refDesc.getValueType()) {
			case Character:
				this.<Character, String> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, null, null);
				break;
			case Flag:
				this.<Boolean, String> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, null, null);
				break;
			case Float:
				this.<Double, String> annotateWithImpl(vc, infix, dbRecords, desc, refDesc, builder,
						Double.MIN_VALUE, Double.MAX_VALUE);
				break;
			case Integer:
				this.<Integer, String> annotateWithImpl(vc, infix, dbRecords, desc, refDesc,
						builder, Integer.MIN_VALUE, Integer.MAX_VALUE);
				break;
			case String:
			default:
				this.<String, String> annotateWithImpl(vc, infix, dbRecords, desc, refDesc, builder,
						null, null);
				break;
			}
			break;
		}
	}

	private <Label extends Comparable<Label>, Value> void annotateWithImpl(VariantContext vc,
			String infix, Map<Integer, List<VariantContext>> dbRecords,
			GenericTSVValueColumnDescription desc, GenericTSVValueColumnDescription refDesc,
			VariantContextBuilder builder, Label minValue, Label maxValue) {
		// Prepare annotation list with one entry for each allele
		final List<Object> annotations = new ArrayList<>();
		final Map<Integer, List<LabeledValue<Label, Value>>> labeledValues = new HashMap<>();
		for (int alleleNo = 0; alleleNo < vc.getNAlleles(); ++alleleNo) {
			annotations.add(".");
			labeledValues.put(alleleNo, new ArrayList<>());
			for (VariantContext dbRecord : dbRecords.get(alleleNo)) {
				Label label = (Label) dbRecord.getAttribute(refDesc.getFieldName());
				if (label == null && minValue != null && maxValue != null) {
					if (refDesc
							.getAccumulationStrategy() == GenericTSVAccumulationStrategy.CHOOSE_MIN) {
						label = maxValue;
					} else if (refDesc
							.getAccumulationStrategy() == GenericTSVAccumulationStrategy.CHOOSE_MAX) {
						label = minValue;
					}
				}
				labeledValues.get(alleleNo).add(new LabeledValue<Label, Value>(label,
						(Value) dbRecord.getAttribute(desc.getFieldName())));
			}
		}

		switch (refDesc.getValueType()) {
		case Character:
		case Flag:
		case String:
			// Only pick first available
			for (int j = 0; j < vc.getNAlleles(); ++j) {
				if (!labeledValues.get(j).isEmpty()) {
					annotations.set(j, labeledValues.get(j).get(0).getValue());
				}
			}
			break;
		case Float:
			for (int j = 0; j < vc.getNAlleles(); ++j) {
				if (!labeledValues.get(j).isEmpty()) {
					switch (refDesc.getAccumulationStrategy()) {
					case AVERAGE:
						annotations.set(j, labeledValues.get(j).stream()
								.mapToDouble(x -> (Double) x.getValue()).average().orElse(0.0));
						break;
					case CHOOSE_FIRST:
						annotations.set(j, labeledValues.get(j).get(0).getValue());
						break;
					case CHOOSE_MIN:
						annotations.set(j,
								labeledValues.get(j).stream()
										.min(Comparator.<LabeledValue<Label, Value>> naturalOrder())
										.map(x -> (Object) x.getValue()).orElse("."));
						break;
					case CHOOSE_MAX:
					default:
						annotations.set(j,
								labeledValues.get(j).stream()
										.max(Comparator.<LabeledValue<Label, Value>> naturalOrder())
										.map(x -> (Object) x.getValue()).orElse("."));
						break;
					}
				}
			}
			break;
		case Integer:
			for (int j = 0; j < vc.getNAlleles(); ++j) {
				if (!labeledValues.get(j).isEmpty()) {
					switch (refDesc.getAccumulationStrategy()) {
					case AVERAGE:
						annotations.set(j, labeledValues.get(j).stream()
								.mapToDouble(x -> (Integer) x.getValue()).average().orElse(0.0));
						break;
					case CHOOSE_FIRST:
						annotations.set(j, labeledValues.get(j).get(0).getValue());
						break;
					case CHOOSE_MIN:
						annotations.set(j,
								labeledValues.get(j).stream()
										.min(Comparator.<LabeledValue<Label, Value>> naturalOrder())
										.map(x -> (Object) x.getValue()).orElse("."));
						break;
					case CHOOSE_MAX:
					default:
						annotations.set(j,
								labeledValues.get(j).stream()
										.max(Comparator.<LabeledValue<Label, Value>> naturalOrder())
										.map(x -> (Object) x.getValue()).orElse("."));
						break;
					}
				}
			}
			break;
		default:
			break;
		}

		if (!options.isRefAlleleAnnotated()) {
			annotations.remove(0);
		}

		// Put annotation into variant context builder
		final String label = options.getVCFIdentifierPrefix() + infix + desc.getFieldName();
		builder.attribute(label, annotations);
	}

}
