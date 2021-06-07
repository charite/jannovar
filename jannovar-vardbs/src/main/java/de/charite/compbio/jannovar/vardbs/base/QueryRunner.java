package de.charite.compbio.jannovar.vardbs.base;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import de.charite.compbio.jannovar.Immutable;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Helper class for running queries on Jannovar H2 databases.
 */
@Immutable
final class QueryRunner {
	private final Table table;

	/**
	 * Constructor.
	 *
	 * @param table The {@code Table} to run queries on.
	 */
	public QueryRunner(Table table) {
		this.table = table;
	}

	/**
	 * Query Jannovar H2 database for result.
	 *
	 * @param genomeBuild	Genome build to use.
	 * @param contig		Contig name.
	 * @param start			Start position.
	 * @param end			End position.
	 * @param refAllele		Reference allele.
	 * @param altAlleles	Alternative alleles.
	 * @param conn			{@code Connection} to use for accessing the Jannovar H2 database.
	 * @return The query result.
	 */
	public Result run(
		String genomeBuild,
		String contig,
		int start,
		int end,
		String refAllele,
		List<String> altAlleles,
		Connection conn
	) throws JannovarVarDBException {
		// Prepare containers for collecting annotations.
		final Map<String, Annotation> posAnnos = new HashMap<>();
		final List<Map<String, Annotation>> alleleAnnos = new ArrayList<>();
		for (int i = 0; i < altAlleles.size(); i++) {
			alleleAnnos.add(new HashMap<>());
		}

		try {
			// Prepare query.
			final List<String> fields = table.getFields().stream().map(field -> field.getName()).collect(Collectors.toList());
			final List<String> altClauses = new ArrayList<>();
			for (String ignored : altAlleles) {
				altClauses.add("alt = ?");
			}
			final PreparedStatement stmt = conn.prepareStatement(
				"SELECT genome_build, contig, start, end, ref, alt, " + Joiner.on(", ").join(fields) +
					" FROM " + table.getName() +
					" WHERE genome_build = ? AND contig = ? AND start = ? AND end = ? AND ref = ? AND (" +
					Joiner.on(" OR ").join(altClauses) + ")"
			);
			stmt.setString(1, genomeBuild);
			stmt.setString(2, contig);
			stmt.setInt(3, start);
			stmt.setInt(4, end);
			stmt.setString(5, refAllele);
			for (int i = 0; i < altAlleles.size(); i++) {
				stmt.setString(6 + i, altAlleles.get(i));
			}

			// Execute query.
			final ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				final int altAlleleNo = altAlleles.indexOf(rs.getString(6));
				for (TableField field: table.getFields()) {
					final Map<String, Annotation> mapping;
					if (field.getCount().equals("1")) {
						mapping = posAnnos;
					} else if (field.getCount().equals("A")) {
						mapping = alleleAnnos.get(altAlleleNo);
					} else {
						throw new JannovarVarDBException("Invalid count: " + field.getCount());
					}
					switch (field.getType()) {
						case "Integer":
							mapping.put(
								field.getName(),
								new Annotation(field.getName(), rs.getInt(field.getName()), "Integer")
							);
							break;
						case "Float":
							mapping.put(
								field.getName(),
								new Annotation(field.getName(), rs.getDouble(field.getName()), "Float")
							);
							break;
						case "Boolean":
							mapping.put(
								field.getName(),
								new Annotation(field.getName(), rs.getBoolean(field.getName()), "Boolean")
							);
							break;
						case "String":
							mapping.put(
								field.getName(),
								new Annotation(field.getName(), rs.getString(field.getName()), "String")
							);
							break;
						default:
							throw new JannovarVarDBException("Unknown type: " + field.getType());
					}
				}
			}
		} catch (SQLException e) {
			throw new JannovarVarDBException("Problem with database", e);
		}

		final ImmutableList.Builder<Annotation> posAnnoBuilder = ImmutableList.builder();
		final ImmutableList.Builder<ImmutableList<Annotation>> alleleAnnoBuilder = ImmutableList.builder();
		for (TableField field : table.getFields()) {
			if (field.getCount().equals("1")) {
				if (posAnnos.containsKey(field.getName())) {
					posAnnoBuilder.add(posAnnos.get(field.getName()));
				}
			} else if (field.getCount().equals("A")) {
				boolean any = false;
				for (int i = 0; i < alleleAnnos.size(); i++) {
					any = any || alleleAnnos.get(i).containsKey(field.getName());
				}
				if (any) {
					ImmutableList.Builder<Annotation> fieldAnnoBuilder = ImmutableList.builder();
					for (int i = 0; i < alleleAnnos.size(); i++) {
						if (alleleAnnos.get(i).containsKey(field.getName())) {
							fieldAnnoBuilder.add(alleleAnnos.get(i).get(field.getName()));
						} else {
							fieldAnnoBuilder.add(new Annotation(field.getName(), null, field.getType()));
						}
					}
					alleleAnnoBuilder.add(fieldAnnoBuilder.build());
				}
			} else {
				throw new JannovarVarDBException("Invalid count: " + field.getCount());
			}
		}

		return new Result(posAnnoBuilder.build(), alleleAnnoBuilder.build());
	}

	/**
	 * Represent result of running a query.
	 */
	@Immutable
	final static class Result {
		private final ImmutableList<Annotation> positionAnnotations;
		private final ImmutableList<ImmutableList<Annotation>> altAlleleAnnotations;

		/**
		 * Constructor.
		 *
		 * @param positionAnnotations	Per-position annotations.
		 * @param altAlleleAnnotations	Per alternative allele annotations.
		 */
		public Result(
			Iterable<Annotation> positionAnnotations,
			Iterable<ImmutableList<Annotation>> altAlleleAnnotations
		) {
			this.positionAnnotations = ImmutableList.copyOf(positionAnnotations);
			this.altAlleleAnnotations = ImmutableList.copyOf(altAlleleAnnotations);
		}

		public ImmutableList<Annotation> getPositionAnnotations() {
			return positionAnnotations;
		}

		public ImmutableList<ImmutableList<Annotation>> getAltAlleleAnnotations() {
			return altAlleleAnnotations;
		}

		@Override
		public String toString() {
			return "Result{" +
				"positionAnnotations=" + positionAnnotations +
				", altAlleleAnnotations=" + altAlleleAnnotations +
				'}';
		}
	}

	/**
	 * Annotation value.
	 */
	@Immutable
	final static class Annotation {
		private final String key;
		private final Object value;
		private final String type;

		/**
		 * Constructor.
		 *
		 * @param key	Annotation key.
		 * @param value	Annotation value.
		 * @param type	Annotation type.
		 */
		public Annotation(String key, Object value, String type) {
			this.key = key;
			this.value = value;
			this.type = type;
		}

		public String getKey() {
			return key;
		}

		public Object getValue() {
			return value;
		}

		public String getType() {
			return type;
		}

		@Override
		public String toString() {
			return "Annotation{" +
				"key='" + key + '\'' +
				", value=" + value +
				", type='" + type + '\'' +
				'}';
		}
	}
}
