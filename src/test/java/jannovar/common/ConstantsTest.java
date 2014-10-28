package jannovar.common;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests for the Constants class.
 */
public class ConstantsTest {

	@Test
	public void testGetUCSCString() {
		// TODO(holtgrem): The Release class could be usable easier.
		Assert.assertEquals("hg18", Constants.Release.HG18.getUCSCString(Constants.Release.HG18));
		Assert.assertEquals("hg19", Constants.Release.HG18.getUCSCString(Constants.Release.HG19));
		Assert.assertEquals("hg38", Constants.Release.HG18.getUCSCString(Constants.Release.HG38));
		Assert.assertEquals("mm9", Constants.Release.HG18.getUCSCString(Constants.Release.MM9));
		Assert.assertEquals("mm10", Constants.Release.HG18.getUCSCString(Constants.Release.MM10));
	}

	@Test
	public void testGetNCBIString() {
		Assert.assertEquals("NCBI36.3", Constants.Release.HG18.getNCBIString(Constants.Release.HG18));
		Assert.assertEquals("GRCh37.p13", Constants.Release.HG18.getNCBIString(Constants.Release.HG19));
		Assert.assertEquals("GRCh38", Constants.Release.HG18.getNCBIString(Constants.Release.HG38));
		Assert.assertEquals("MGSCv37.2", Constants.Release.HG18.getNCBIString(Constants.Release.MM9));
		Assert.assertEquals("GRCm38.p1", Constants.Release.HG18.getNCBIString(Constants.Release.MM10));
	}

	@Test
	public void testGetEnsemblString() {
		Assert.assertEquals("NCBI36.54", Constants.Release.HG18.getEnsemblString(Constants.Release.HG18));
		Assert.assertEquals("GRCh37.74", Constants.Release.HG18.getEnsemblString(Constants.Release.HG19));
		Assert.assertEquals("error", Constants.Release.HG18.getEnsemblString(Constants.Release.HG38));
		Assert.assertEquals("NCBIM37.67", Constants.Release.HG18.getEnsemblString(Constants.Release.MM9));
		Assert.assertEquals("GRCm38.74", Constants.Release.HG18.getEnsemblString(Constants.Release.MM10));
	}
}
