package jannovar.gff;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * Test to check that the {@link RNA2GeneIDMapper} is runnign well.<br>
 * v0.1 <br>
 * Only check for the Ensembl Gene ids to simple gene ids.
 *  
 * @author mjaeger
 * @version 0.1 (2013-07-12)
 */
public class RNA2GeneIDMapperTest {

	@Test
	public void testGetGeneID001() {
		assertEquals(419, RNA2GeneIDMapper.getGeneID("ENSG00000000419"));
	}
	
	@Test
	public void testGetGeneID002() {
		assertEquals(1000000419, RNA2GeneIDMapper.getGeneID("ENSG01000000419"));
	}

}
