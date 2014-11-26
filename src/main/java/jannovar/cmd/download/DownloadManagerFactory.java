package jannovar.cmd.download;

import jannovar.JannovarOptions;

/**
 * Allows easy building of DownloadOrchestrators.
 *
 * @author Peter N Robinson
 * @author Marten Jaeger
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class DownloadManagerFactory {
	/**
	 * @param options
	 *            configuration to use
	 * @return appropriately configured DownloadOrchestrator or null if there is nothing to download
	 */
	public static DownloadManager build(JannovarOptions options) {
		if (options.dataSource == JannovarOptions.DataSource.UCSC)
			return new UCSCDownloadManager(options);
		else if (options.dataSource == JannovarOptions.DataSource.ENSEMBL)
			return new EnsembleDownloadManager(options);
		else if (options.dataSource == JannovarOptions.DataSource.REFSEQ)
			return new RefseqDownloadManager(options);
		else
			return null;
	}
}
