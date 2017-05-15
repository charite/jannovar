package de.charite.compbio.jannovar.cmd.annotate_vcf;

import de.charite.compbio.jannovar.cmd.annotate_vcf.JannovarAnnotateVCFOptions.BedAnnotationOptions;
import htsjdk.samtools.util.Interval;
import htsjdk.tribble.AbstractFeatureReader;
import htsjdk.tribble.TabixFeatureReader;
import htsjdk.tribble.bed.BEDCodec;
import htsjdk.tribble.bed.BEDFeature;
import htsjdk.tribble.index.Index;
import htsjdk.tribble.index.IndexFactory;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.variantcontext.VariantContextBuilder;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderLineCount;
import htsjdk.variant.vcf.VCFHeaderLineType;
import htsjdk.variant.vcf.VCFInfoHeaderLine;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Perform annotation of {@link VariantContext}s using BED files.
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class BedFileAnnotator implements Closeable {

	/** Configuration of the annotator. */
	private final BedAnnotationOptions options;

	/** {@link File} with BED features. */
	private final File featureFile;

	/** This is used for reading. */
	TabixFeatureReader<BEDFeature, LineIterator> reader;

	public BedFileAnnotator(BedAnnotationOptions options) {
		this.options = options;
		this.featureFile = new File(options.getPathBed());

		try {
			this.reader = new TabixFeatureReader<>(featureFile.getAbsolutePath().toString(),
					featureFile.getAbsolutePath().toString() + ".tbi", new BEDCodec());
		} catch (IOException e) {
			throw new RuntimeException("Problem opening indexed BED file", e);
		}
	}

	/**
	 * Add header line describing the INFO field.
	 * 
	 * @param vcfHeader
	 */
	public void extendHeader(VCFHeader vcfHeader) {
		if (!vcfHeader.hasInfoLine(options.getInfoField())) {
			if (options.getColNo() < 0) {
				vcfHeader.addMetaDataLine(new VCFInfoHeaderLine(options.getInfoField(), 0,
						VCFHeaderLineType.Flag, options.getDescription()));
			} else {
				vcfHeader.addMetaDataLine(new VCFInfoHeaderLine(options.getInfoField(),
						VCFHeaderLineCount.UNBOUNDED, VCFHeaderLineType.String,
						options.getDescription() + "; column " + options.getColNo()));
			}
		}
	}

	/**
	 * Annotate and build the variant
	 * 
	 * @param vc
	 *            {@link VariantContext} to annotate
	 * @return annotated {@link VariantContext}
	 */
	public VariantContext annotateVariantContext(VariantContext vc) {
		List<String> overlaps = new ArrayList<>();
		try {
			final Interval vcInterval = new Interval(vc.getContig(), vc.getStart(), vc.getEnd());
			for (BEDFeature bedFeature : reader.query(vc.getContig(), vc.getStart() - 1,
					vc.getEnd() + 1)) {
				final Interval bedItv = new Interval(bedFeature.getContig(), bedFeature.getStart(),
						bedFeature.getEnd());
				if (vcInterval.intersects(bedItv)) {
					if (options.getColNo() == -1) {
						overlaps.add("true"); // marker is enough
						break;
					} else {
						overlaps.add(bedFeature.getName());
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(
					"Could not query " + vc.getContig() + ":" + vc.getStart() + "-" + vc.getEnd(),
					e);
		}

		if (overlaps.isEmpty()) {
			return vc;
		} else {
			VariantContextBuilder builder = new VariantContextBuilder(vc);
			if (options.getColNo() == -1) {
				builder.attribute(options.getInfoField(), true);
			} else {
				builder.attribute(options.getInfoField(), overlaps);
			}
			return builder.make();
		}
	}

	@Override
	public void close() {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				throw new RuntimeException("Could not close BED reader", e);
			}
			reader = null;
		}
	}

}
