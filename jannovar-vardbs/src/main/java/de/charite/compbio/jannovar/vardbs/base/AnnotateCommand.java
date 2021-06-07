package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.Immutable;
import de.charite.compbio.jannovar.htsjdk.VariantContextWriterConstructionHelper;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.variantcontext.writer.VariantContextWriter;
import htsjdk.variant.vcf.*;

import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Annotate VCF file with Jannovar H2 database.
 */
@Immutable
public final class AnnotateCommand {
	private final AnnotateOptions options;
	private final ImmutableList<Table> tableInfos;

	/**
     * Constructor.
	 *
	 * @param options 	The {@code AnnotateOptions} to use.
	 * @param conn		The {@code Connection} to use for loading the annotation tables.
	 * @throws JannovarVarDBException if there is a problem with loading information from the database.
	 */
	public AnnotateCommand(AnnotateOptions options, Connection conn) throws JannovarVarDBException {
		this.options = options;

		final ImmutableList.Builder<Table> tableInfosBuilder = ImmutableList.builder();
		final TableDao tableDao = new TableDao(conn);
		for (Table table: tableDao.getAllTables()) {
			if (options.getTableNames().contains(table.getName())) {
				tableInfosBuilder.add(table);
			}
		}
		this.tableInfos = tableInfosBuilder.build();
	}

    /**
     * Execute the Listing.
	 */
	public void run(Connection conn) throws JannovarVarDBException {
		final long startTime = System.nanoTime();

		try (
			final VCFFileReader vcfReader = new VCFFileReader(new File(options.getInputVcfPath()), false);
			final VariantContextWriter vcfWriter = VariantContextWriterConstructionHelper.openVariantContextWriter(
				vcfReader.getFileHeader(), options.getOutputVcfPath(), buildAdditionalHeaderLines(),
				options.getOutputVcfPath().endsWith(".vcf.gz")
			)) {
			for (VariantContext record : vcfReader) {
				vcfWriter.add(annotateVC(record, conn));
			}
		}

		final long endTime = System.nanoTime();
		System.err.println(String.format("Annotation took %.2f sec.",
			(endTime - startTime) / 1000.0 / 1000.0 / 1000.0));
	}

	/**
	 * Augment VCF file header with INFO fields.
	 *
	 * @return Header lines to add to output file.
	 */
	public List<VCFHeaderLine> buildAdditionalHeaderLines() {
		final List<VCFHeaderLine> result = new ArrayList<>();

		for (Table tableInfo : tableInfos) {
			for (TableField tableField : tableInfo.getFields()) {
				if (tableField.getCount().equals("A")) {
					result.add(new VCFInfoHeaderLine(
						tableInfo.getDefaultPrefix() + tableField.getName(),
						VCFHeaderLineCount.A,
						VCFHeaderLineType.valueOf(tableField.getType()),
						tableField.getDescription()
					));
				} else if (tableField.getCount().equals("1")) {
					result.add(new VCFInfoHeaderLine(
						tableInfo.getDefaultPrefix() + tableField.getName(),
						1,
						VCFHeaderLineType.valueOf(tableField.getType()),
						tableField.getDescription()
					));
				} else {
					throw new RuntimeException("Unknown count " + tableField.getCount());
				}
			}
		}

		return result;
	}

	/**
	 * Annotate a {@code VariantContext} as configured in options.
	 *
	 * @param record 	The {@code VariantContext} to annotate.
	 * @param conn		The {@ocde Connection} to use for accessing the Jannovar H2 database.
	 * @return Annotated {@code VariantContext}.
	 * @throws JannovarVarDBException in case there is a problem with the database access.
	 */
	public VariantContext annotateVC(VariantContext record, Connection conn) throws JannovarVarDBException {
		final VariantContextBuilder resultBuilder = new VariantContextBuilder(record);

		for (Table tableInfo : tableInfos) {
			final ImmutableList.Builder<String> altAllelesBuilder = ImmutableList.builder();
			for (int i = 0; i < record.getAlternateAlleles().size(); ++i) {
				altAllelesBuilder.add(record.getAlternateAllele(i).getBaseString());
			}
			final QueryRunner queryRunner = new QueryRunner(tableInfo);
			final QueryRunner.Result result = queryRunner.run(
				options.getGenomeBuild(),
				record.getContig(),
				record.getStart(),
				record.getEnd(),
				record.getReference().getBaseString(),
				altAllelesBuilder.build(),
				conn
			);
			annotateRecord(resultBuilder, result, tableInfo);
		}

		return resultBuilder.make();
	}

	private void annotateRecord(VariantContextBuilder resultBuilder, QueryRunner.Result result, Table tableInfo) {
		final Map<String, Object> info = resultBuilder.getAttributes();

		// Annotate with per-position values.
		for (QueryRunner.Annotation ann: result.getPositionAnnotations()) {
			info.put(tableInfo.getDefaultPrefix() + ann.getKey(), ann.getValue());
		}

		// Annotate with per-allele values.
		for (ImmutableList<QueryRunner.Annotation> anns: result.getAltAlleleAnnotations()) {
			if (anns.size() == 0) {
				continue;  // skip
			}
			final String key = anns.get(0).getKey();

			final List<Object> values = new ArrayList<>();
			for (int i = 0; i < anns.size(); i++) {
				values.add(anns.get(i).getValue());
			}

			info.put(tableInfo.getDefaultPrefix() + key, values);
		}
	}
}
