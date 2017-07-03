package de.charite.compbio.jannovar.vardbs.base;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import java.io.File;

/**
 * VCF file--backed provider of {@link VariantContext}s.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class VCFReaderVariantProvider implements DatabaseVariantContextProvider {

	/** VCFReader to use for loading the VCF records */
	private final VCFFileReader vcfReader;

	public VCFReaderVariantProvider(String vcfPath) {
		this.vcfReader = new VCFFileReader(new File(vcfPath), true);
	}

	public VCFFileReader getVcfReader() {
		return vcfReader;
	}

	@Override
	public CloseableIterator<VariantContext> query(String contig, int beginPos, int endPos) {
		return vcfReader.query(contig, beginPos, endPos);
	}

}
