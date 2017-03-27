package de.charite.compbio.jannovar.mendel.filter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.factories.TestJannovarDataFactory;
import de.charite.compbio.jannovar.mendel.bridge.MendelVCFHeaderExtender;
import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.PedFileContents;
import de.charite.compbio.jannovar.pedigree.PedParseException;
import de.charite.compbio.jannovar.pedigree.PedPerson;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Sex;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

/**
 * Test for annotating compatible modes of inheritance
 * 
 * This test case uses one trio for the test
 * 
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GeneWiseInheritanceProcessorOneAffectedTest {

	final static String KEY = MendelVCFHeaderExtender.key();
	final static String KEY_SUB = MendelVCFHeaderExtender.keySub();

	/** Pedigree with one affected child */
	private Pedigree trio;
	/** Jannovar DB */
	private JannovarData jannovarDB;
	/** Variants to filter */
	private List<VariantContext> variants;
	/** The VCF file reader to use */
	private VCFFileReader reader;

	@Before
	public void setUp() throws PedParseException {
		// Construct first pedigree
		ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "Eva", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // Mother
		individuals.add(new PedPerson("ped", "Adam", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // Father
		individuals.add(new PedPerson("ped", "Seth", "Adam", "Eva", Sex.MALE, Disease.AFFECTED)); // Child
		PedFileContents pedFileContents = new PedFileContents(new ImmutableList.Builder<String>().build(),
				individuals.build());
		trio = new Pedigree(pedFileContents, "ped");

		// Load test Jannovar data
		jannovarDB = new TestJannovarDataFactory().getJannovarData();
	}

	private void loadVariants(String infix) {
		Path inheritanceFilterVCFPath = Paths.get("src/test/resources/inheritanceFilterTest." + infix + "vcf");
		reader = new VCFFileReader(inheritanceFilterVCFPath.toFile(), false);
		variants = new ArrayList<VariantContext>();
		for (VariantContext variantContext : reader)
			variants.add(variantContext);
	}

	@Test
	public void testGRCh37() {
		loadVariants("b37.");

		ArrayList<VariantContext> result = new ArrayList<>();
		try (GeneWiseMendelianAnnotationProcessor proc = new GeneWiseMendelianAnnotationProcessor(trio, jannovarDB,
				vc -> result.add(vc), false)) {
			for (VariantContext vc : variants)
				proc.put(vc);
		}

		Assert.assertEquals(7, result.size());

		Assert.assertEquals("1", result.get(0).getContig());
		Assert.assertEquals(145513532, result.get(0).getStart());
		Assert.assertEquals("[AR]", result.get(0).getAttribute(KEY).toString());
		Assert.assertEquals("[AR_COMP_HET]", result.get(0).getAttribute(KEY_SUB).toString());

		Assert.assertEquals("1", result.get(1).getContig());
		Assert.assertEquals(145513533, result.get(1).getStart());
		Assert.assertNull(result.get(1).getAttribute(KEY));
		Assert.assertNull(result.get(1).getAttribute(KEY_SUB));

		Assert.assertEquals("1", result.get(2).getContig());
		Assert.assertEquals(145513534, result.get(2).getStart());
		Assert.assertEquals("[AR]", result.get(2).getAttribute(KEY).toString());
		Assert.assertEquals("[AR_COMP_HET]", result.get(2).getAttribute(KEY_SUB).toString());

		Assert.assertEquals("1", result.get(3).getContig());
		Assert.assertEquals(145515898, result.get(3).getStart());
		Assert.assertEquals("[AD]", result.get(3).getAttribute(KEY).toString());
		Assert.assertNull(result.get(3).getAttribute(KEY_SUB));

		Assert.assertEquals("1", result.get(4).getContig());
		Assert.assertEquals(145515899, result.get(4).getStart());
		Assert.assertNull(result.get(4).getAttribute(KEY));
		Assert.assertNull(result.get(4).getAttribute(KEY_SUB));

		Assert.assertEquals("10", result.get(5).getContig());
		Assert.assertEquals(123239370, result.get(5).getStart());
		Assert.assertEquals("[AR]", result.get(5).getAttribute(KEY).toString());
		Assert.assertNotNull(result.get(5).getAttribute(KEY_SUB));
		Assert.assertEquals("[AR_HOM_ALT]", result.get(5).getAttribute(KEY_SUB).toString());

		Assert.assertEquals("10", result.get(6).getContig());
		Assert.assertEquals(123357972, result.get(6).getStart());
		Assert.assertNull(result.get(6).getAttribute(KEY));
		Assert.assertNull(result.get(6).getAttribute(KEY_SUB));
	}

	@Test
	public void testHG19() {
		loadVariants("hg19.");

		ArrayList<VariantContext> result = new ArrayList<>();
		try (GeneWiseMendelianAnnotationProcessor proc = new GeneWiseMendelianAnnotationProcessor(trio, jannovarDB,
				vc -> result.add(vc), false)) {
			for (VariantContext vc : variants)
				proc.put(vc);
		}

		Assert.assertEquals(7, result.size());

		Assert.assertEquals("chr1", result.get(0).getContig());
		Assert.assertEquals(145513532, result.get(0).getStart());
		Assert.assertEquals("[AR]", result.get(0).getAttribute(KEY).toString());
		Assert.assertEquals("[AR_COMP_HET]", result.get(0).getAttribute(KEY_SUB).toString());

		Assert.assertEquals("chr1", result.get(1).getContig());
		Assert.assertEquals(145513533, result.get(1).getStart());
		Assert.assertNull(result.get(1).getAttribute(KEY));
		Assert.assertNull(result.get(1).getAttribute(KEY_SUB));

		Assert.assertEquals("chr1", result.get(2).getContig());
		Assert.assertEquals(145513534, result.get(2).getStart());
		Assert.assertEquals("[AR]", result.get(2).getAttribute(KEY).toString());
		Assert.assertEquals("[AR_COMP_HET]", result.get(2).getAttribute(KEY_SUB).toString());

		Assert.assertEquals("chr1", result.get(3).getContig());
		Assert.assertEquals(145515898, result.get(3).getStart());
		Assert.assertEquals("[AD]", result.get(3).getAttribute(KEY).toString());
		Assert.assertNull(result.get(3).getAttribute(KEY_SUB));

		Assert.assertEquals("chr1", result.get(4).getContig());
		Assert.assertEquals(145515899, result.get(4).getStart());
		Assert.assertNull(result.get(4).getAttribute(KEY));
		Assert.assertNull(result.get(4).getAttribute(KEY_SUB));

		Assert.assertEquals("chr10", result.get(5).getContig());
		Assert.assertEquals(123239370, result.get(5).getStart());
		Assert.assertEquals("[AR]", result.get(5).getAttribute(KEY).toString());
		Assert.assertEquals("[AR_HOM_ALT]", result.get(5).getAttribute(KEY_SUB).toString());

		Assert.assertEquals("chr10", result.get(6).getContig());
		Assert.assertEquals(123357972, result.get(6).getStart());
		Assert.assertNull(result.get(6).getAttribute(KEY));
		Assert.assertNull(result.get(6).getAttribute(KEY_SUB));
	}
}
