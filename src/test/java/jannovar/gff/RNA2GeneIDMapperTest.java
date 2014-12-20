package jannovar.gff;

import static org.junit.Assert.assertEquals;
import jannovar.parse.gff.RNA2GeneIDMapper;

import org.junit.Test;

/**
 * Test to check that the {@link RNA2GeneIDMapper} is running well.
 *
 * Only check for the Ensembl Gene ids to simple gene ids.
 *
 * @author Marten Jaeger <marten.jaeger@charite.de>
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
