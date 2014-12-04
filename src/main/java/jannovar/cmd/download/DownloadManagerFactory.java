package jannovar.cmd.download;

import jannovar.JannovarOptions;

/**
 * Allows easy building of DownloadOrchestrators.
 *
 * @author Peter N Robinson
 * @author Marten Jaeger
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public final class DownloadManagerFactory {
	/**
	 * @param options
	 *            configuration to use
	 * @return appropriately configured DownloadOrchestrator or null if there is nothing to download
	 */
	public static DownloadManager build(JannovarOptions options) {
		if (options.dataSource == JannovarOptions.DataSource.UCSC)
			return new UCSCDownloadManager(options);
		else if (options.dataSource == JannovarOptions.DataSource.ENSEMBL)
			return new EnsemblDownloadManager(options);
		else if (options.dataSource == JannovarOptions.DataSource.REFSEQ
				|| options.dataSource == JannovarOptions.DataSource.REFSEQ_CURATED)
			return new RefseqDownloadManager(options);
		else
			return null;
	}
}
