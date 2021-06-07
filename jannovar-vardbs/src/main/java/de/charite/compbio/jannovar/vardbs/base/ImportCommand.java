package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.Immutable;
import htsjdk.variant.variantcontext.CommonInfo;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFInfoHeaderLine;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

/**
 * Import a VCF file to a Jannovar H2 database file.
 */
@Immutable
public final class ImportCommand {
	private final ImportOptions options;

	/**
	 * Constructor.
	 *
	 * @param options The {@code ImportOptions} to use.
	 */
	public ImportCommand(ImportOptions options) {
		this.options = options;
	}

	/**
	 * Execute the import.
	 */
	public void run(Connection conn) throws JannovarVarDBException {
		final TableDao tableDao = new TableDao(conn);
		tableDao.initializeDatabase();
		final Table table = buildTable(options);
		System.err.println("Creating table " + table);
		tableDao.createTable(table);
		maybeTruncateTable(conn, table);
		runImport(conn, table);
	}

	private void runImport(Connection conn, Table table) throws JannovarVarDBException {
		for (String vcfPath : options.getVcfPaths()) {
			importVcfFile(conn, vcfPath, table);
		}
	}

	private void importVcfFile(Connection conn, String vcfPath, Table table) throws JannovarVarDBException {
		final List<String> fieldNames = new ArrayList<>();
		final List<String> placeHolders = new ArrayList<>();
		for (TableField field : table.getFields()) {
			fieldNames.add(field.getName());
			placeHolders.add("?");
		}
		final String sqlInsert = "MERGE INTO " + table.getName() +
			" (genome_build, contig, start, end, ref, alt, " + Joiner.on(", ").join(fieldNames) + ")" +
			"VALUES (?, ?, ?, ?, ?, ?, " + Joiner.on(", ").join(placeHolders) + ")";

		System.err.println("Starting import of " + vcfPath);
		try (VCFFileReader reader = new VCFFileReader(new File(vcfPath));
			PreparedStatement stmt = conn.prepareStatement(sqlInsert)) {
			for (VariantContext ctx : reader) {
				if (ctx.getReference().getBaseString().length() > TableDao.MAX_ALLELE_LENGTH) {
					System.err.println("Skipping because reference too long: " + ctx);
					continue;
				}

				stmt.setString(1, options.getGenomeBuild());
				stmt.setString(2, ctx.getContig());
				stmt.setInt(3, ctx.getStart());
				stmt.setInt(4, ctx.getEnd());
				stmt.setString(5, ctx.getReference().getBaseString());

				for (int i = 0; i < ctx.getAlternateAlleles().size(); ++i) {
					if (ctx.getAlternateAllele(i).getBaseString().length() > TableDao.MAX_ALLELE_LENGTH) {
						System.err.println("Skipping because alt #" + i + " too long: " + ctx);
						continue;
					}
					stmt.setString(6, ctx.getAlternateAllele(i).getBaseString());

					final CommonInfo info = ctx.getCommonInfo();
					for (int j = 0; j < table.getFields().size(); ++j) {
						final TableField field = table.getFields().get(j);
						switch (field.getType()) {
							case "Integer":
								switch (field.getCount()) {
									case "A":
										final List<Object> lst = info.getAttributeAsList(field.getName());
										if (!info.hasAttribute(field.getName()) || lst.size() < i) {
											stmt.setNull(j + 7, Types.INTEGER);
										} else {
											stmt.setInt(j + 7, Integer.parseInt((String)lst.get(i)));
										}
										break;
									case "1":
										if (!info.hasAttribute(field.getName())) {
											stmt.setNull(j + 7, Types.INTEGER);
										} else {
											stmt.setInt(j + 7, Integer.parseInt((String)info.getAttribute(field.getName())));
										}
										break;
									default:
										throw new RuntimeException("Invalid count " + field.getCount());
								}
								break;
							case "Float":
								switch (field.getCount()) {
									case "A":
										final List<Object> lst = info.getAttributeAsList(field.getName());
										if (!info.hasAttribute(field.getName()) || lst.size() < i) {
											stmt.setNull(j + 7, Types.DOUBLE);
										} else {
											stmt.setDouble(j + 7, Double.parseDouble((String)lst.get(i)));
										}
										break;
									case "1":
										if (!info.hasAttribute(field.getName())) {
											stmt.setNull(j + 7, Types.DOUBLE);
										} else {
											stmt.setDouble(j + 7, Double.parseDouble((String)info.getAttribute(field.getName())));
										}
										break;
									default:
										throw new RuntimeException("Invalid count " + field.getCount());
								}
								break;
							case "Boolean":
								switch (field.getCount()) {
									case "A":
										final List<Object> lst = info.getAttributeAsList(field.getName());
										if (!info.hasAttribute(field.getName()) || lst.size() < i) {
											stmt.setNull(j + 7, Types.BOOLEAN);
										} else {
											stmt.setBoolean(j + 7, (Boolean) lst.get(i));
										}
										break;
									case "1":
										if (!info.hasAttribute(field.getName())) {
											stmt.setNull(j + 7, Types.DOUBLE);
										} else {
											stmt.setBoolean(j + 7, (Boolean) info.getAttribute(field.getName()));
										}
										break;
									default:
										throw new RuntimeException("Invalid count " + field.getCount());
								}
								break;
							case "String":
								switch (field.getCount()) {
									case "A":
										final List<Object> lst = info.getAttributeAsList(field.getName());
										if (!info.hasAttribute(field.getName()) || lst.size() < i) {
											stmt.setNull(j + 7, Types.VARCHAR);
										} else {
											stmt.setString(j + 7, (String) lst.get(i));
										}
										break;
									case "1":
										if (!info.hasAttribute(field.getName())) {
											stmt.setNull(j + 7, Types.VARCHAR);
										} else {
											stmt.setString(j + 7, (String) info.getAttribute(field.getName()));
										}
										break;
									default:
										throw new RuntimeException("Invalid count " + field.getCount());
								}
								break;
							default:
								throw new RuntimeException("Invalid field type " + field.getType());
						}
					}

					stmt.executeUpdate();
				}
			}
		} catch (SQLException e) {
			throw new JannovarVarDBException("Problem with insert statement", e);
		}
	}

	private void maybeTruncateTable(Connection conn, Table table) throws JannovarVarDBException {
		if (options.isTruncateTable()) {
			System.err.println("Truncating old table");
			try {
				conn.prepareStatement("TRUNCATE TABLE " + table.getName()).executeUpdate();
			} catch (SQLException e) {
				throw new JannovarVarDBException("Problem with database query", e);
			}
		} else {
			System.err.println("Configured to NOT truncate old table");
		}
	}

	private Table buildTable(ImportOptions options) {
		final ImmutableList<TableField> fields = ImmutableList.copyOf(readTableFields(
			options.getVcfPaths().get(0)
		));
		for (String fileName: options.getVcfPaths()) {
			final ImmutableList<TableField> otherFields = ImmutableList.copyOf(readTableFields(
				fileName));
			if (!fields.equals(otherFields)) {
				throw new RuntimeException(
					"Different field types in " + options.getVcfPaths().get(0) + " and " + fileName +
					": " + fields + " vs. " + otherFields
				);
			}
		}

		return new Table(
			options.getTableName(),
			options.getDefaultPrefix(),
			fields
		);
	}

	private List<TableField> readTableFields(String fileName) {
		final List<TableField> result = new ArrayList<>();
		try (VCFFileReader reader = new VCFFileReader(new File(fileName))) {
			final VCFHeader fileHeader = reader.getFileHeader();
			for (VCFInfoHeaderLine line: fileHeader.getInfoHeaderLines()) {
				if (options.getVcfInfoFields().contains(line.getID())) {
					String count;
					if (line.getCountType() == VCFHeaderLineCount.A) {
						count = "A";
					} else if (line.getCountType() == VCFHeaderLineCount.INTEGER) {
						count = Integer.toString(line.getCount());
						if (line.getCount() != 1) {
							throw new RuntimeException("Invalid count: " + count + "; must be 1.");
						}
					} else {
						throw new RuntimeException("Invalid count type " + line.getCountType());
					}

					result.add(new TableField(
						line.getID(),
						line.getType().toString(),
						count,
						line.getDescription()
					));
				}
			}
		}
		return result;
	}
}
