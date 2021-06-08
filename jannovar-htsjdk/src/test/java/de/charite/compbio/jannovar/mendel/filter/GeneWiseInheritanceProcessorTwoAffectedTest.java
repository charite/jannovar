package de.charite.compbio.jannovar.mendel.filter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import de.charite.compbio.jannovar.data.JannovarData;
import de.charite.compbio.jannovar.factories.TestJannovarDataFactory;
import de.charite.compbio.jannovar.mendel.bridge.MendelVCFHeaderExtender;
import de.charite.compbio.jannovar.pedigree.*;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Test for annotating compatible modes of inheritance
 * <p>
 * This test case uses one trio for the test
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GeneWiseInheritanceProcessorTwoAffectedTest {

	final static String KEY = MendelVCFHeaderExtender.key();
	final static String KEY_SUB = MendelVCFHeaderExtender.keySub();

	/**
	 * Pedigree with two affected children
	 */
	private Pedigree trio;
	/**
	 * Jannovar DB
	 */
	private JannovarData jannovarDB;
	/**
	 * Variants to filter
	 */
	private List<VariantContext> variants;
	/**
	 * The VCF file reader to use
	 */
	private VCFFileReader reader;

	@BeforeEach
	public void setUp() throws PedParseException {
		// Construct second pedigree
		Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
		individuals.add(new PedPerson("ped", "Eva", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // Mother
		individuals.add(new PedPerson("ped", "Adam", "0", "0", Sex.MALE, Disease.AFFECTED)); // Father
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

		Assertions.assertEquals(7, result.size());

		Assertions.assertEquals("1", result.get(0).getContig());
		Assertions.assertEquals(145513532, result.get(0).getStart());
		Assertions.assertEquals("[AD]", result.get(0).getAttribute(KEY).toString());
		Assertions.assertNull(result.get(0).getAttribute(KEY_SUB));

		Assertions.assertEquals("1", result.get(1).getContig());
		Assertions.assertEquals(145513533, result.get(1).getStart());
		Assertions.assertNull(result.get(1).getAttribute(KEY));
		Assertions.assertNull(result.get(1).getAttribute(KEY_SUB));

		Assertions.assertEquals("1", result.get(2).getContig());
		Assertions.assertEquals(145513534, result.get(2).getStart());
		Assertions.assertNull(result.get(2).getAttribute(KEY));
		Assertions.assertNull(result.get(2).getAttribute(KEY_SUB));

		Assertions.assertEquals("1", result.get(3).getContig());
		Assertions.assertEquals(145515898, result.get(3).getStart());
		Assertions.assertNull(result.get(3).getAttribute(KEY));
		Assertions.assertNull(result.get(3).getAttribute(KEY_SUB));

		Assertions.assertEquals("1", result.get(4).getContig());
		Assertions.assertEquals(145515899, result.get(4).getStart());
		Assertions.assertNull(result.get(4).getAttribute(KEY));
		Assertions.assertNull(result.get(4).getAttribute(KEY_SUB));

		Assertions.assertEquals("10", result.get(5).getContig());
		Assertions.assertEquals(123239370, result.get(5).getStart());
		Assertions.assertNull(result.get(5).getAttribute(KEY));
		Assertions.assertNull(result.get(5).getAttribute(KEY_SUB));

		Assertions.assertEquals("10", result.get(6).getContig());
		Assertions.assertEquals(123357972, result.get(6).getStart());
		Assertions.assertEquals("[AR]", result.get(6).getAttribute(KEY).toString());
		Assertions.assertEquals("[AR_HOM_ALT]", result.get(6).getAttribute(KEY_SUB).toString());
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

		Assertions.assertEquals(7, result.size());

		Assertions.assertEquals("chr1", result.get(0).getContig());
		Assertions.assertEquals(145513532, result.get(0).getStart());
		Assertions.assertEquals("[AD]", result.get(0).getAttribute(KEY).toString());
		Assertions.assertNull(result.get(0).getAttribute(KEY_SUB));

		Assertions.assertEquals("chr1", result.get(1).getContig());
		Assertions.assertEquals(145513533, result.get(1).getStart());
		Assertions.assertNull(result.get(1).getAttribute(KEY));
		Assertions.assertNull(result.get(1).getAttribute(KEY_SUB));

		Assertions.assertEquals("chr1", result.get(2).getContig());
		Assertions.assertEquals(145513534, result.get(2).getStart());
		Assertions.assertNull(result.get(2).getAttribute(KEY));
		Assertions.assertNull(result.get(2).getAttribute(KEY_SUB));

		Assertions.assertEquals("chr1", result.get(3).getContig());
		Assertions.assertEquals(145515898, result.get(3).getStart());
		Assertions.assertNull(result.get(3).getAttribute(KEY));
		Assertions.assertNull(result.get(3).getAttribute(KEY_SUB));

		Assertions.assertEquals("chr1", result.get(4).getContig());
		Assertions.assertEquals(145515899, result.get(4).getStart());
		Assertions.assertNull(result.get(4).getAttribute(KEY));
		Assertions.assertNull(result.get(4).getAttribute(KEY_SUB));

		Assertions.assertEquals("chr10", result.get(5).getContig());
		Assertions.assertEquals(123239370, result.get(5).getStart());
		Assertions.assertNull(result.get(5).getAttribute(KEY));
		Assertions.assertNull(result.get(5).getAttribute(KEY_SUB));

		Assertions.assertEquals("chr10", result.get(6).getContig());
		Assertions.assertEquals(123357972, result.get(6).getStart());
		Assertions.assertEquals("[AR]", result.get(6).getAttribute(KEY).toString());
		Assertions.assertEquals("[AR_HOM_ALT]", result.get(6).getAttribute(KEY_SUB).toString());
	}

}
