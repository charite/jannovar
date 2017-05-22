package de.charite.compbio.jannovar.vardbs.generic_tsv;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.vardbs.base.DatabaseVariantContextProvider;
import de.charite.compbio.jannovar.vardbs.generic_tsv.GenericTSVAnnotationOptions.ValueColumnDescription;
import htsjdk.samtools.util.CloseableIterator;
import htsjdk.tribble.readers.TabixReader;
import htsjdk.tribble.readers.TabixReader.Iterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import java.io.IOException;

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
			final int startPos = Integer.parseInt(tokens[options.getBeginColumnIndex() - 1]) - delta;
			builder.start(startPos);
			builder.stop(startPos);

			if (options.getRefAlleleColumnIndex() > 0 && options.getAltAlleleColumnIndex() > 0) {
				builder.alleles(tokens[options.getRefAlleleColumnIndex() - 1],
						tokens[options.getAltAlleleColumnIndex() - 1]);
			} else {
				builder.alleles("N");
			}

			for (ValueColumnDescription desc : options.getValueColumnDescriptions()) {
				final Object value;
				final String token = tokens[desc.getColumnIndex() - 1];

				switch (desc.getValueType()) {
				case Flag:
					value = ImmutableList.of("1", "Y", "y", "T", "t", "yes", "true")
							.contains(token);
					break;
				case Float:
					value = Double.parseDouble(token);
					break;
				case Integer:
					value = Integer.parseInt(token);
					break;
				case Character:
				case String:
				default:
					value = token;
					break;
				}
				builder.attribute(desc.getFieldName(), value);
			}

			return builder.make();
		}

		@Override
		public void close() {
			/* nop */
		}

	}

}
