package de.charite.compbio.jannovar.cmd.rest_server;

import static spark.Spark.get;
import static spark.Spark.ipAddress;
import static spark.Spark.port;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import de.charite.compbio.jannovar.JannovarException;
import de.charite.compbio.jannovar.annotation.Annotation;
import de.charite.compbio.jannovar.annotation.AnnotationMessage;
import de.charite.compbio.jannovar.annotation.VariantAnnotations;
import de.charite.compbio.jannovar.annotation.VariantAnnotator;
import de.charite.compbio.jannovar.annotation.builders.AnnotationBuilderOptions;
import de.charite.compbio.jannovar.cmd.CommandLineParsingException;
import de.charite.compbio.jannovar.cmd.JannovarAnnotationCommand;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.data.JannovarDataSerializer;
import de.charite.compbio.jannovar.data.SerializationException;
import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.htsjdk.InvalidCoordinatesException;
import de.charite.compbio.jannovar.reference.GenomePosition;
import de.charite.compbio.jannovar.reference.GenomeVariant;
import de.charite.compbio.jannovar.reference.PositionType;
import de.charite.compbio.jannovar.reference.Strand;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import net.sourceforge.argparse4j.inf.Namespace;

/**
 * Allows running a simple REST server.
 *
 * @author <a href="mailto:manuel.holtgrewe@charite.de">Manuel Holtgrewe</a>
 */
public class RestServerCommand extends JannovarAnnotationCommand {

	/**
	 * Configuration
	 */
	private RestServerOptions options;

	public RestServerCommand(String argv[], Namespace args) throws CommandLineParsingException {
		this.options = new RestServerOptions();
		this.options.setFromArgs(args);
	}

	@Override public void run() throws JannovarException {
		System.err.println("Options: " + options);
		ipAddress(options.getHost());
		port(options.getPort());

		System.err.println("Loading database");
		final ImmutableMap<String, JannovarData> jvDatas = loadDatabases();

		get("/annotate-var/:release/:database/:chromosome/:position/:reference/:alternative",
			(req, res) -> {
				final String chromosome = req.params(":chromosome");
				final int position = Integer.parseInt(req.params(":position"));
				final String reference = req.params(":reference");
				final String alternative = req.params(":alternative");

				final String key = Joiner.on("/")
					.join(req.params(":release"), req.params(":database"));
				final JannovarData jvData = jvDatas.get(key);
				final VariantAnnotator annotator = new VariantAnnotator(jvData.getRefDict(),
					jvData.getChromosomes(), new AnnotationBuilderOptions(true, false));

				final Integer boxedInt = jvData.getRefDict().getContigNameToID().get(chromosome);
				if (boxedInt == null) {
					throw new InvalidCoordinatesException("Unknown reference " + chromosome,
						AnnotationMessage.ERROR_CHROMOSOME_NOT_FOUND);
				}
				final int chr = boxedInt.intValue();

				final GenomePosition gPos = new GenomePosition(jvData.getRefDict(), Strand.FWD, chr,
					position, PositionType.ONE_BASED);
				final VariantAnnotations annotations = annotator
					.buildAnnotations(new GenomeVariant(gPos, reference, alternative));

				final List<VariantAnnotationInfo> result = new ArrayList<>(0);
				for (Annotation anno : annotations.getAnnotations()) {
					result.add(new VariantAnnotationInfo(anno.getTranscript().getAccession(),
						anno.getEffects().stream().map(x -> x.toString().toLowerCase())
							.collect(Collectors.toList()), anno.getTranscript().isCoding(),
						anno.getProteinChangeStr(AminoAcidCode.ONE_LETTER),
						anno.getCDSNTChangeStr()));
				}

				res.type("application/json");
				return new Gson().toJson(result);
			});
	}

	private ImmutableMap<String, JannovarData> loadDatabases() throws SerializationException {
		ImmutableMap.Builder<String, JannovarData> builder = ImmutableMap.builder();

		for (String dbPath : options.getDbPaths()) {
			final String fileName = new File(dbPath).getName();
			final String[] arr = fileName.replace(".ser", "").split("_", 2);
			final String key = Joiner.on("/").join(arr[1], arr[0]);
			System.err.println("Loading " + dbPath + "...");
			builder.put(key, new JannovarDataSerializer(dbPath).load());
			System.err.println("Done loading.");
		}

		return builder.build();
	}

	/**
	 * Helper class for simple serialization using Gson.
	 */
	private static class VariantAnnotationInfo {
		private final String transcriptId;
		private final ImmutableList variantEffects;
		private final boolean isCoding;
		private final String hgvsProtein;
		private final String hgvsNucleotides;

		public VariantAnnotationInfo(String transcriptId, Collection<String> variantEffects,
			boolean isCoding, String hgvsProtein, String hgvsNucleotides) {
			this.transcriptId = transcriptId;
			this.variantEffects = ImmutableList.copyOf(variantEffects);
			this.isCoding = isCoding;
			this.hgvsProtein = hgvsProtein;
			this.hgvsNucleotides = hgvsNucleotides;
		}
	}

}
