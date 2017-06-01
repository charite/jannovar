package de.charite.compbio.jannovar.vardbs.generic_tsv;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.vardbs.base.DatabaseVariantContextProvider;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.tribble.readers.TabixReader;
import htsjdk.tribble.readers.TabixReader.Iterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Read TSV records as {@link VariantContext} entries.
 * 
 * <p>
 * Note that there cannot be concurrent queries with the same
 * <code>GenericTSVVariantContextProvider</code> because we currently only shallowly wrap HTSJDK's
 * TabixReader.
 * </p>
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericTSVVariantContextProvider implements DatabaseVariantContextProvider {

	private final GenericTSVAnnotationOptions options;

	private final TabixReader tabixReader;

	public GenericTSVVariantContextProvider(GenericTSVAnnotationOptions options) {
		this.options = options;
		final String tsvPath = this.options.getTsvFile().toString();
		try {
			this.tabixReader = new TabixReader(tsvPath, tsvPath + ".tbi");
		} catch (IOException e) {
			throw new RuntimeException("Could not open TABIX file " + tsvPath, e);
		}
	}

	@Override
	public CloseableIterator<VariantContext> query(String contig, int beginPos, int endPos) {
		return new TabixIteratorWrapper(tabixReader.query(contig, beginPos, endPos));
	}

	/**
	 * Wrapper for iterator from {@link TabixReader}.
	 * 
	 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
	 */
	private class TabixIteratorWrapper implements CloseableIterator<VariantContext> {

		private final Iterator iter;

		private String next;

		public TabixIteratorWrapper(Iterator iter) {
			this.iter = iter;
			try {
				this.next = iter.next();
			} catch (IOException e) {
				throw new RuntimeException("Problem reading from " + options.getTsvFile(), e);
			}
		}

		@Override
		public boolean hasNext() {
			return this.next != null;
		}

		@Override
		public VariantContext next() {
			final String resultLine = next;
			try {
				next = iter.next();
			} catch (IOException e) {
				throw new RuntimeException("Problem reading from " + options.getTsvFile(), e);
			}
			return parseTabixLine(resultLine);
		}

		private VariantContext parseTabixLine(String resultLine) {
			final String[] tokens = resultLine.split("\t");

			final VariantContextBuilder builder = new VariantContextBuilder();

			builder.chr(tokens[options.getContigColumnIndex() - 1]);

			final int delta = options.isOneBasedPositions() ? 0 : 1;
			final int startPos = Integer.parseInt(tokens[options.getBeginColumnIndex() - 1])
					- delta;
			builder.start(startPos);
			builder.stop(startPos);

			if (options.getRefAlleleColumnIndex() > 0 && options.getAltAlleleColumnIndex() > 0) {
				builder.alleles(tokens[options.getRefAlleleColumnIndex() - 1],
						tokens[options.getAltAlleleColumnIndex() - 1]);
			} else {
				builder.alleles("N");
			}

			// Collect all required column names (ref column names might not be selected for
			// printing)
			Set<String> allColNames = new HashSet<>(options.getColumnNames());
			for (String colName : options.getColumnNames()) {
				final GenericTSVValueColumnDescription desc = options.getValueColumnDescriptions()
						.get(colName);
				if (desc.getRefField() != null) {
					allColNames.add(desc.getRefField());
				}
			}

			// Collect values from all required columns
			Map<String, List<Object>> colValues = new HashMap<>();
			for (String colName : allColNames) {
				final GenericTSVValueColumnDescription desc = options.getValueColumnDescriptions()
						.get(colName);
				final String token = tokens[desc.getColumnIndex() - 1];
				final String sep = ";";
				final ImmutableList<String> splitTokens = ImmutableList.copyOf(token.split(sep));

				switch (desc.getValueType()) {
				case Flag:
					colValues.put(colName, splitTokens.stream().map(s -> {
						if (s == null || ".".equals(s)) {
							return null;
						} else {
							return (Object) ImmutableList.of("1", "Y", "y", "T", "t", "yes", "true")
									.contains(s);
						}
					}).collect(Collectors.toList()));
					break;
				case Float:
					colValues.put(colName, splitTokens.stream().map(s -> {
						if (s == null || ".".equals(s)) {
							return null;
						} else {
							return (Object) Double.parseDouble(s);
						}
					}).collect(Collectors.toList()));
					break;
				case Integer:
					colValues.put(colName, splitTokens.stream().map(s -> {
						if (s == null || ".".equals(s)) {
							return null;
						} else {
							return (Object) Integer.parseInt(s);
						}
					}).collect(Collectors.toList()));
					break;
				case Character:
				case String:
				default:
					colValues.put(colName, ImmutableList.<Object> copyOf(splitTokens));
					break;
				}
			}

			// For each, now select the best according to strategy.
			Map<String, Object> values = new HashMap<>();

			for (String colName : options.getColumnNames()) {
				final GenericTSVValueColumnDescription desc = options.getValueColumnDescriptions()
						.get(colName);
				final GenericTSVValueColumnDescription refDesc = options
						.getValueColumnDescriptions().get(desc.getRefField());

				switch (refDesc.getValueType()) {
				case Character:
				case Flag:
				case String:
					// Pick first one
					values.put(colName, colValues.get(colName).get(0));
					break;
				case Float:
					final List<
							LabeledValue<Double, Object>> doubleLabeledValues = new ArrayList<>();
					for (int i = 0; i < colValues.get(refDesc.getFieldName()).size(); ++i) {
						Double value = (Double) colValues.get(refDesc.getFieldName()).get(i);
						if (value == null && refDesc
								.getAccumulationStrategy() == GenericTSVAccumulationStrategy.CHOOSE_MIN) {
							value = Double.MAX_VALUE;
						} else if (value == null && refDesc
								.getAccumulationStrategy() == GenericTSVAccumulationStrategy.CHOOSE_MAX) {
							value = Double.MIN_VALUE;
						}
						doubleLabeledValues.add(new LabeledValue<Double, Object>(value, i));
					}

					if (doubleLabeledValues.isEmpty()) {
						values.put(colName, ".");
					} else {
						final int key;
						switch (refDesc.getAccumulationStrategy()) {
						case CHOOSE_MIN:
							Collections.sort(doubleLabeledValues);
							key = (int) doubleLabeledValues.get(0).getValue();
							break;
						case CHOOSE_MAX:
							Collections.sort(doubleLabeledValues);
							key = (int) doubleLabeledValues.get(doubleLabeledValues.size() - 1)
									.getValue();
							break;
						case CHOOSE_FIRST:
						case AVERAGE:
						default:
							key = 0;
						}

						if (colValues.get(desc.getFieldName()).size() == 1) {  // might be single value...
							values.put(colName, colValues.get(desc.getFieldName()).get(0));
						} else {
							values.put(colName, colValues.get(desc.getFieldName()).get(key));
						}
					}
					break;
				case Integer:
					final List<LabeledValue<Integer, Object>> intLabeledValues = new ArrayList<>();
					for (int i = 0; i < colValues.get(refDesc.getFieldName()).size(); ++i) {
						Integer value = (Integer) colValues.get(refDesc.getFieldName()).get(i);
						if (value == null && refDesc
								.getAccumulationStrategy() == GenericTSVAccumulationStrategy.CHOOSE_MIN) {
							value = Integer.MAX_VALUE;
						} else if (value == null && refDesc
								.getAccumulationStrategy() == GenericTSVAccumulationStrategy.CHOOSE_MAX) {
							value = Integer.MIN_VALUE;
						}
						intLabeledValues.add(new LabeledValue<Integer, Object>(
								(Integer) colValues.get(refDesc.getFieldName()).get(i), i));
					}

					if (intLabeledValues.isEmpty()) {
						values.put(colName, ".");
					} else {
						final int key;
						switch (refDesc.getAccumulationStrategy()) {
						case CHOOSE_MIN:
							Collections.sort(intLabeledValues);
							key = (int) intLabeledValues.get(0).getValue();
							break;
						case CHOOSE_MAX:
							Collections.sort(intLabeledValues);
							key = (int) intLabeledValues.get(intLabeledValues.size() - 1)
									.getValue();
							break;
						case CHOOSE_FIRST:
						case AVERAGE:
						default:
							key = 0;
						}

						if (colValues.get(desc.getFieldName()).size() == 1) {  // might be single value...
							values.put(colName, colValues.get(desc.getFieldName()).get(0));
						} else {
							values.put(colName, colValues.get(desc.getFieldName()).get(key));
						}
					}
					break;
				default:
					break;
				}
			}

			// Finally, write out one value
			for (String colName : options.getColumnNames()) {
				final GenericTSVValueColumnDescription desc = options.getValueColumnDescriptions()
						.get(colName);
				builder.attribute(desc.getFieldName(), values.get(colName));
			}

			return builder.make();
		}

		@Override
		public void close() {
			/* nop */
		}

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

}
