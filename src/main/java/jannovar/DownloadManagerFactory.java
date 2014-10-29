package jannovar;

/**
 * Allows easy building of DownloadOrchestrators.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
public class DownloadManagerFactory {
	/**
	 * @param options
	 *            configuration to use
	 * @return appropriately configured DownloadOrchestrator or null if there is nothing to download
	 */
	public static DownloadManager build(JannovarOptions options) {
		if (options.createUCSC)
			return new UCSCDownloadManager(options);
		else if (options.createEnsembl)
			return new EnsembleDownloadManager(options);
		else if (options.createRefseq)
			return new RefseqDownloadManager(options);
		else
			return null;
	}
}
