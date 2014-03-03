/**
 * 
 */
package jannovar.common;

import java.util.HashMap;

/**
 * This is a map for all currently known mappings used in the GTF/GFF files from
 * various databases for the chromosomes of the reference assembly.<br>
 * Version 0.1 contains current mappings (2013-06-17) for mouse and human from
 * <UL>
 * <LI>UCSC (hg19,mm9,mm10)
 * <LI>NCBI (GRCh37.p10,GRCm38.p1)
 * <LI>Ensembl (GRCh37.71,GRCm38.71)
 * </UL>
 * 
 * @author mjaeger
 * @version 0.1
 */
@SuppressWarnings("serial")
public interface ChromosomeMap extends Constants {

	HashMap<String, Byte> identifier2chromosom = new HashMap<String, Byte>() {
		{
			// UCSC
			put("chr1", (byte) 1);
			put("chr2", (byte) 2);
			put("chr3", (byte) 3);
			put("chr4", (byte) 4);
			put("chr5", (byte) 5);
			put("chr6", (byte) 6);
			put("chr7", (byte) 7);
			put("chr8", (byte) 8);
			put("chr9", (byte) 9);
			put("chr10", (byte) 10);
			put("chr11", (byte) 11);
			put("chr12", (byte) 12);
			put("chr13", (byte) 13);
			put("chr14", (byte) 14);
			put("chr15", (byte) 15);
			put("chr16", (byte) 16);
			put("chr17", (byte) 17);
			put("chr18", (byte) 18);
			put("chr19", (byte) 19);
			put("chr20", (byte) 20);
			put("chr21", (byte) 21);
			put("chr22", (byte) 22);
			put("chr23", (byte) 23);
			put("chrX", X_CHROMOSOME);
			put("chrY", Y_CHROMOSOME);
			put("chrM", M_CHROMOSOME);
			// ENSEMBL
			put("1", (byte) 1);
			put("2", (byte) 2);
			put("3", (byte) 3);
			put("4", (byte) 4);
			put("5", (byte) 5);
			put("6", (byte) 6);
			put("7", (byte) 7);
			put("8", (byte) 8);
			put("9", (byte) 9);
			put("10", (byte) 10);
			put("11", (byte) 11);
			put("12", (byte) 12);
			put("13", (byte) 13);
			put("14", (byte) 14);
			put("15", (byte) 15);
			put("16", (byte) 16);
			put("17", (byte) 17);
			put("18", (byte) 18);
			put("19", (byte) 19);
			put("20", (byte) 20);
			put("21", (byte) 21);
			put("22", (byte) 22);
			put("23", (byte) 23);
			put("X", X_CHROMOSOME);
			put("Y", Y_CHROMOSOME);
			put("MT", M_CHROMOSOME);
			// NCBI human hg38
			put("NC_000001.11", (byte) 1);
			put("NC_000010.11", (byte) 10);
			put("NC_000011.10", (byte) 11);
			put("NC_000012.12", (byte) 12);
			put("NC_000013.11", (byte) 13);
			put("NC_000014.9", (byte) 14);
			put("NC_000015.10", (byte) 15);
			put("NC_000016.10", (byte) 16);
			put("NC_000017.11", (byte) 17);
			put("NC_000018.10", (byte) 18);
			put("NC_000019.10", (byte) 19);
			put("NC_000002.12", (byte) 2);
			put("NC_000020.11", (byte) 20);
			put("NC_000021.8", (byte) 21);
			put("NC_000022.11", (byte) 22);
			put("NC_000003.12", (byte) 3);
			put("NC_000004.12", (byte) 4);
			put("NC_000005.10", (byte) 5);
			put("NC_000006.12", (byte) 6);
			put("NC_000007.14", (byte) 7);
			put("NC_000008.11", (byte) 8);
			put("NC_000009.12", (byte) 9);
			put("NC_000023.11", X_CHROMOSOME);
			put("NC_000024.10", Y_CHROMOSOME);
			put("NC_012920.1", M_CHROMOSOME);
			// NCBI human hg19
			put("NC_000001.10", (byte) 1);
			put("NC_000010.10", (byte) 10);
			put("NC_000011.9", (byte) 11);
			put("NC_000012.11", (byte) 12);
			put("NC_000013.10", (byte) 13);
			put("NC_000014.8", (byte) 14);
			put("NC_000015.9", (byte) 15);
			put("NC_000016.9", (byte) 16);
			put("NC_000017.10", (byte) 17);
			put("NC_000018.9", (byte) 18);
			put("NC_000019.9", (byte) 19);
			put("NC_000002.11", (byte) 2);
			put("NC_000020.10", (byte) 20);
			put("NC_000021.8", (byte) 21);
			put("NC_000022.10", (byte) 22);
			put("NC_000003.11", (byte) 3);
			put("NC_000004.11", (byte) 4);
			put("NC_000005.9", (byte) 5);
			put("NC_000006.11", (byte) 6);
			put("NC_000007.13", (byte) 7);
			put("NC_000008.10", (byte) 8);
			put("NC_000009.11", (byte) 9);
			put("NC_000023.10", X_CHROMOSOME);
			put("NC_000024.9", Y_CHROMOSOME);
			put("NC_012920.1", M_CHROMOSOME);
			// NCBI human hg18
			put("NC_000001.9", (byte) 1);
			put("NC_000010.9", (byte) 10);
			put("NC_000011.8", (byte) 11);
			put("NC_000012.10", (byte) 12);
			put("NC_000013.9", (byte) 13);
			put("NC_000014.7", (byte) 14);
			put("NC_000015.8", (byte) 15);
			put("NC_000016.8", (byte) 16);
			put("NC_000017.9", (byte) 17);
			put("NC_000018.8", (byte) 18);
			put("NC_000019.8", (byte) 19);
			put("NC_000002.10", (byte) 2);
			put("NC_000020.9", (byte) 20);
			put("NC_000021.7", (byte) 21);
			put("NC_000022.9", (byte) 22);
			put("NC_000003.10", (byte) 3);
			put("NC_000004.10", (byte) 4);
			put("NC_000005.8", (byte) 5);
			put("NC_000006.10", (byte) 6);
			put("NC_000007.12", (byte) 7);
			put("NC_000008.9", (byte) 8);
			put("NC_000009.10", (byte) 9);
			put("NC_000023.9", X_CHROMOSOME);
			put("NC_000024.8", Y_CHROMOSOME);
			// put("NC_012920.1",M_CHROMOSOME);
			// NCBI mouse mm10
			put("NC_000067.6", (byte) 1);
			put("NC_000068.7", (byte) 2);
			put("NC_000069.6", (byte) 3);
			put("NC_000070.6", (byte) 4);
			put("NC_000071.6", (byte) 5);
			put("NC_000072.6", (byte) 6);
			put("NC_000073.6", (byte) 7);
			put("NC_000074.6", (byte) 8);
			put("NC_000075.6", (byte) 9);
			put("NC_000076.6", (byte) 10);
			put("NC_000077.6", (byte) 11);
			put("NC_000078.6", (byte) 12);
			put("NC_000079.6", (byte) 13);
			put("NC_000080.6", (byte) 14);
			put("NC_000081.6", (byte) 15);
			put("NC_000082.6", (byte) 16);
			put("NC_000083.6", (byte) 17);
			put("NC_000084.6", (byte) 18);
			put("NC_000085.6", (byte) 19);
			put("NC_000086.7", X_CHROMOSOME);
			put("NC_000087.7", Y_CHROMOSOME);
			put("NC_005089.1", M_CHROMOSOME);
			// NCBI mouse mm9
			put("NC_000067.5", (byte) 1);
			put("NC_000068.6", (byte) 2);
			put("NC_000069.5", (byte) 3);
			put("NC_000070.5", (byte) 4);
			put("NC_000071.5", (byte) 5);
			put("NC_000072.5", (byte) 6);
			put("NC_000073.5", (byte) 7);
			put("NC_000074.5", (byte) 8);
			put("NC_000075.5", (byte) 9);
			put("NC_000076.5", (byte) 10);
			put("NC_000077.5", (byte) 11);
			put("NC_000078.5", (byte) 12);
			put("NC_000079.5", (byte) 13);
			put("NC_000080.5", (byte) 14);
			put("NC_000081.5", (byte) 15);
			put("NC_000082.5", (byte) 16);
			put("NC_000083.5", (byte) 17);
			put("NC_000084.5", (byte) 18);
			put("NC_000085.5", (byte) 19);
			put("NC_000086.6", X_CHROMOSOME);
			put("NC_000087.6", Y_CHROMOSOME);
		}
	};

}
