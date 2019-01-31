package de.charite.compbio.jannovar.mendel;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import de.charite.compbio.jannovar.pedigree.Disease;
import de.charite.compbio.jannovar.pedigree.PedFileContents;
import de.charite.compbio.jannovar.pedigree.PedPerson;
import de.charite.compbio.jannovar.pedigree.Pedigree;
import de.charite.compbio.jannovar.pedigree.Sex;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class InheritanceCheckerMTSmallTest extends MendelianCompatibilityCheckerTestBase {

  private MendelianInheritanceChecker checker;
  private List<GenotypeCalls> gcList;
  private ImmutableMap<ModeOfInheritance, ImmutableList<GenotypeCalls>> result;

  private Pedigree inconsistentMTpedigree;

  @Before
  public void setUp() throws Exception {
    ImmutableList.Builder<PedPerson> individuals = new ImmutableList.Builder<PedPerson>();
    individuals.add(new PedPerson("ped", "I.1", "0", "0", Sex.MALE, Disease.UNAFFECTED)); // father
    individuals.add(new PedPerson("ped", "I.2", "0", "0", Sex.FEMALE, Disease.AFFECTED)); // mother
    individuals.add(new PedPerson("ped", "II.1", "I.1", "I.2", Sex.MALE, Disease.AFFECTED)); // son
    individuals.add(
        new PedPerson("ped", "II.2", "I.1", "I.2", Sex.FEMALE, Disease.AFFECTED)); // daughter
    PedFileContents pedFileContents =
        new PedFileContents(new ImmutableList.Builder<String>().build(), individuals.build());
    this.pedigree = new Pedigree(pedFileContents, "ped");

    this.names = ImmutableList.of("I.1", "I.2", "II.1", "II.2");

    this.checker = new MendelianInheritanceChecker(this.pedigree);

    this.result = null;
    this.gcList = null;
    /*
     * same as above but father is transmitting mitochrondrial mutation, which is
     * impossible
     */
    ImmutableList.Builder<PedPerson> individuals2 = new ImmutableList.Builder<PedPerson>();
    individuals2.add(new PedPerson("ped2", "I.1", "0", "0", Sex.MALE, Disease.AFFECTED)); // father
    individuals2.add(
        new PedPerson("ped2", "I.2", "0", "0", Sex.FEMALE, Disease.UNAFFECTED)); // mother
    individuals2.add(
        new PedPerson("ped2", "II.1", "I.1", "I.2", Sex.MALE, Disease.AFFECTED)); // son
    individuals2.add(
        new PedPerson("ped2", "II.2", "I.1", "I.2", Sex.FEMALE, Disease.AFFECTED)); // daughter
    PedFileContents pedFileContents2 =
        new PedFileContents(new ImmutableList.Builder<String>().build(), individuals2.build());
    this.inconsistentMTpedigree = new Pedigree(pedFileContents2, "ped2");
  }

  @Test
  public void testSizeOfPedigree() {
    Assert.assertEquals(4, pedigree.getMembers().size());
  }

  @Test
  public void testSizeOfInconsistentPedigree() {
    Assert.assertEquals(4, inconsistentMTpedigree.getMembers().size());
  }

  /**
   * no affected is HET or ALT
   *
   * @throws IncompatiblePedigreeException
   */
  @Test
  public void testCaseNegativesOneVariant1() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(REF, REF, REF, REF), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  /**
   * TWO affected are REF
   *
   * @throws IncompatiblePedigreeException
   */
  @Test
  public void testCaseNegativesOneVariant2() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(REF, REF, REF, ALT), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  /**
   * TWO affected are REF
   *
   * @throws IncompatiblePedigreeException
   */
  @Test
  public void testCaseNegativesOneVariant3() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(REF, REF, ALT, REF), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  /**
   * Two affected are REF
   *
   * @throws IncompatiblePedigreeException
   */
  @Test
  public void testCaseNegativesOneVariant4() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(REF, ALT, REF, REF), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  /**
   * One unaffected is also ALT
   *
   * @throws IncompatiblePedigreeException
   */
  @Test
  public void testCaseNegativesOneVariant5() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(ALT, ALT, ALT, ALT), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  /**
   * Affected Okay but one unaffected has it HET. We allow this because of heteroplasmie in
   * unaffected
   *
   * @throws IncompatiblePedigreeException
   */
  @Test
  public void testCasePositivesOneVariantHeteroplasmie1() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(HET, ALT, ALT, ALT), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  /**
   * At least one HET or ALT call in affected
   *
   * @throws IncompatiblePedigreeException
   */
  @Test
  public void testCaseNegativesOneVariant7() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(UKN, UKN, UKN, UKN), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  /**
   * At least one HET or ALT call in affected
   *
   * @throws IncompatiblePedigreeException
   */
  @Test
  public void testCaseNegativesOneVariant8() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(REF, UKN, UKN, UKN), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  /**
   * One affected is REF
   *
   * @throws IncompatiblePedigreeException
   */
  @Test
  public void testCaseNegativesOneVariant9() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(REF, ALT, HET, REF), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  /**
   * Affected Okay but one unaffected has it HET. We allow this because of heteroplasmie in
   * unaffected. This is in general difficult an can only be figured out by using the exact "allele
   * balances".
   *
   * @throws IncompatiblePedigreeException
   */
  @Test
  public void testCasePositivesOneVariantHeteroplasmie2() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(HET, HET, HET, HET), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  /**
   * The checker object has a pedigree with an affected mother, son, and daughter as well as an
   * unaffected father in the order father, mother, son, daughter. Adding REF, HET, ALT, REF means
   * that the pedigree should be judged compatible with mitochondrial inheritance, because the
   * unaffected father is REF, the affected mother and her affected daughter are HET, and her
   * affected son is ALT (OK because he might have a higher degree of heteroplasmy).
   *
   * @throws IncompatiblePedigreeException
   */
  @Test
  public void testCasePositiveOneVariant1() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(REF, HET, ALT, HET), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  @Test
  public void testCasePositiveOneVariant2() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(REF, ALT, ALT, ALT), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  @Test
  public void testCasePositiveOneVariant3() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(REF, HET, HET, HET), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  @Test
  public void testCasePositiveOneVariant4() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(UKN, HET, HET, HET), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  @Test
  public void testCasePositiveOneVariant5() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(UKN, ALT, HET, HET), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  @Test
  public void testCasePositiveOneVariant6() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(REF, HET, UKN, HET), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  @Test
  public void testCasePositiveOneVariant7() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(UKN, UKN, UKN, ALT), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  @Test
  public void testCasePositiveOneVariant8() throws IncompatiblePedigreeException {
    gcList = getGenotypeCallsList(lst(REF, HET, UKN, UKN), ChromosomeType.MITOCHONDRIAL);
    result = checker.checkMendelianInheritance(gcList);

    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
    Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
    Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  }

  // @Test
  // public void testInconsistentCase() throws IncompatiblePedigreeException {
  // gcList = getGenotypeCallsList(lst(REF, HET, ALT, REF),
  // ChromosomeType.MITOCHONDRIAL);
  // MendelianInheritanceChecker checkerForInconsistentCase = new
  // MendelianInheritanceChecker(
  // this.inconsistentMTpedigree);
  //
  // result = checkerForInconsistentCase.checkMendelianInheritance(gcList);
  //
  // Assert.assertEquals(0,
  // result.get(ModeOfInheritance.AUTOSOMAL_DOMINANT).size());
  // Assert.assertEquals(0,
  // result.get(ModeOfInheritance.AUTOSOMAL_RECESSIVE).size());
  // Assert.assertEquals(0, result.get(ModeOfInheritance.X_DOMINANT).size());
  // Assert.assertEquals(0, result.get(ModeOfInheritance.X_RECESSIVE).size());
  // Assert.assertEquals(0, result.get(ModeOfInheritance.MITOCHONDRIAL).size());
  // Assert.assertEquals(1, result.get(ModeOfInheritance.ANY).size());
  // }

}
