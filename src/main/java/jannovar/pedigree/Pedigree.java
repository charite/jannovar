package jannovar.pedigree;

import jannovar.exception.PedParseException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Models a single-family pedigree in an whole-exome sequencing project. Note that the constructor expects to receive a
 * String with the family ID. If an individual is added to the Pedigree with a different family ID, then an exception is
 * thrown.
 * <p>
 * This class is meant to simulate relatively simple pedigrees that can be conceived of as Affecteds, Parents of
 * Affects, and Unaffected Persons. The class is not meant to be used for formal Lander-Green or Elston-Stewart linkage
 * analysis. Instead, it is meant to be used for relatively simple pedigrees commonly analysed in the setting of
 * whole-exome sequencing.
 * <P>
 * The assumptions of this class are as follows:
 * <P>
 * For autosomal dominant pedigrees, all affected persons must share the same heterozygous mutation, and none of the
 * unaffected persons can carry the mutation.
 * <P>
 * For autosomal recessive pedigrees, there are two possibilities
 * <ul>
 * <li>All affected persons share the same homozygous mutation. In this case, both parents must be heterozygous for the
 * mutation. The unaffected persons can be heterozygous or homozygous wildtype.
 * <li>All affected persons are compound heterozygous for the same two mutations. One of the parents is heterozygous for
 * one of the mutations, and the other parent is heterozygous for the other mutation. Unaffecteds can carry up to one of
 * the mutations.
 * </ul>
 * For X chromosomal mutations, we are currently demanding that affected males are called homozygous for the mutation
 * (actually, they are hemizygous). Only X chromosomal recessive mutations can be analyzed.
 * <P>
 * Note that a common problem is the fact that the order of the sample names in the VCF file may be different from that
 * in the PED file. To simplify the analysis, we demand that the two orders be consolidated, meaning that the order of
 * the PED file samples is adjusted to match the order of the VCF file samples using the function
 * {@link #adjustSampleOrderInPedFile}.
 *
 * @author Peter Robinson
 * @version 0.21 (27 December, 2013)
 */
public class Pedigree {
	/**
	 * The identifier of the family in the PED file.
	 */
	private String familyID = null;
	/**
	 * The name of the sample in case this pedigree object is being used to describe a single sample only.
	 */
	private String singleSampleName = null;
	/**
	 * A list of all persons in this pedigree
	 */
	private ArrayList<Person> personList = null;
	/**
	 * A list of all persons in the pedigree affected by the disease. Note that this list is a subset of the persons in
	 * {@link #personList}.
	 */
	private ArrayList<Person> affectedList = null;
	/**
	 * A list of all persons in the pedigree who are parents of people affected by the disease. Note that it is possible
	 * that a parent is also an affected person (obviously, this is the case for autosomal dominant diseases). Note that
	 * this list is a subset of the persons in {@link #personList}.
	 */
	private ArrayList<Person> parentList = null;
	/**
	 * A list of all persons in the pedigree who are unaffected and also not parents of an affected person. This can
	 * comprise both siblings of affected persons and also other types of relative. For autosomal recessive diseases, we
	 * expect that these people can be either heterozygous or homozygous wildtype. For autosomal dominant diseases,
	 * these people should be homozygous wildtype. Note that this list is a subset of the persons in {@link #personList}
	 * .
	 */
	private ArrayList<Person> unaffectedList = null;
	/** True if this pedigree represents a single sample VCF file only. */
	private boolean isSingleSample;

	/**
	 * Constructs a Pedigree object for a single sample. This object is used with the same interface as the multisample
	 * pedigree.
	 *
	 * The pedigree will have one person with unknown sex but AFFECTED disease state.
	 *
	 * @param name
	 * @return new {@link Pedigree} with one male individual, the given name and in the family <tt>"FAMILY"</tt>.
	 */
	public static Pedigree constructSingleSamplePedigree(String name) {
		ArrayList<Person> pList = new ArrayList<Person>();
		try {
			pList.add(new Person("FAMILY", name, null, null, "0", "2"));
			return new Pedigree(pList, "FAMILY");
		} catch (PedParseException e) { // should neve happen!
			e.printStackTrace();
			throw new RuntimeException("Sample in single-sample pedigree was invalid.");
		}
	}

	/**
	 * @param pList
	 *            list of {@link Person}s
	 * @param famID
	 *            the Family ID of this Pedigree.
	 * @throws jannovar.exception.PedParseException
	 */
	public Pedigree(ArrayList<Person> pList, String famID) throws PedParseException {
		this.personList = new ArrayList<Person>();
		this.familyID = famID;
		for (Person p : pList) {
			addIndividual(p);
		}
		boolean success = findParentLinks();
		if (!success) {
			throw new PedParseException("Inconsistent Parent Relations in PED file");
		}
		setPersonIndices();
		initializeAffectedsParentsSibs();
		isSingleSample = (personList.size() == 1);
		if (isSingleSample)
			singleSampleName = personList.get(0).getIndividualID();
	}

	/**
	 * @return number of people represented in the pedigree
	 */
	public int getPedigreeSize() {
		return this.personList.size();
	}

	/**
	 * @return the name of the single sample (only or single-sample VCF files)
	 */
	public String getSingleSampleName() {
		if (this.isSingleSample)
			return this.singleSampleName;
		else
			return "[Pedigree] Error: Attempt to retrieve single sampl name for multiple sample PED file";
	}

	/**
	 * Returns true if the nth person is affected.
	 *
	 * @param n
	 *            number of the person
	 * @return true if the nth person in the PED file is affected.
	 */
	public boolean isNthPersonAffected(int n) {
		if (n < 0 || n >= personList.size())
			return false;
		Person p = this.personList.get(n);
		return p.isAffected();
	}

	/**
	 * Returns the PED file data for the nth person in the pedigree. See {@link jannovar.pedigree.Person#getPEDFileData
	 * getPEDFileData}.
	 *
	 * @param n
	 *            number of the person
	 * @return the PED file data for the nth person in the pedigree
	 */
	public ArrayList<String> getPEDFileDatForNthPerson(int n) {
		if (n < 0 || n >= personList.size())
			return null;
		Person p = this.personList.get(n);
		return p.getPEDFileData();
	}

	/**
	 * Returns if the nth person in the PED file is parent of an affected child.
	 *
	 * @param n
	 *            number of the person
	 * @return true if the nth person in the PED file is parent of an affected child.
	 */
	public boolean isNthPersonParentOfAffected(int n) {
		if (n < 0 || n >= personList.size())
			return false;
		Person p = this.personList.get(n);
		return this.parentList.contains(p);
	}

	/**
	 * This function is used to check whether a sample-id is represented in this pedigree (it can be used to check that
	 * a VCF file and a PED file have the same samples).
	 *
	 * @param name
	 *            The name of a sample (e.g., from a VCF file)
	 * @return true if the sample is also in this pedigree, otherwise false.
	 */
	public boolean sampleIsRepresentedInPedigree(String name) {
		for (Person p : this.personList) {
			if (name.equals(p.getIndividualID()))
				return true;
		}
		return false;
	}

	/**
	 * It is possible that the order of the names is different in the PED file and in the VCF file. We use this function
	 * to adjust the order of the samples in the PED file to be the same as in the VCF file, which makes it easier to
	 * visualize and perform the pedigree analysis.
	 *
	 * @param sampleNames
	 *            List of names from the VCF file.
	 * @throws jannovar.exception.PedParseException
	 */
	public void adjustSampleOrderInPedFile(ArrayList<String> sampleNames) throws PedParseException {
		if (sampleNames == null) {
			String e = "[Pedigree:adjustSampleOrderInPedFile] Error: VCF sample name list empty";
			throw new PedParseException(e);
		}
		/*
		 * Now check that the names of the samples are identical to the names in the PED file.
		 */
		if (sampleNames.size() != getPedigreeSize()) {
			String e = String.format("[Pedigree:adjustSampleOrderInPedFile] Error:"
					+ "%n individuals in pedigree but %d " + "individuals in the VCF file", sampleNames.size(),
					getPedigreeSize());
			throw new PedParseException(e);
		}

		ArrayList<String> badName = new ArrayList<String>();
		/* Check if all the VCF file sample names are in the PED file. */
		for (String s : sampleNames) {
			if (!sampleIsRepresentedInPedigree(s)) {
				badName.add(s);
			}
		}
		if (badName.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("[Pedigree:adjustSampleOrderInPedFile] Error: Did not find VCF sample names in PED file: ");
			boolean first = true;
			for (String s : badName) {
				if (first) {
					sb.append(s);
					first = false;
				} else {
					sb.append(", ").append(s);
				}
			}
			throw new PedParseException(sb.toString());
		}
		/* Check if all the PED file sample names are in the VCF file. */
		for (Person p : personList) {
			String id = p.getIndividualID();
			if (!sampleNames.contains(id)) {
				badName.add(id);
			}
		}
		if (badName.size() > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("[Pedigree:adjustSampleOrderInPedFile] Error: Did not find PED file names in VCF file: ");
			boolean first = true;
			for (String s : badName) {
				if (first) {
					sb.append(s);
					first = false;
				} else {
					sb.append(", ").append(s);
				}
			}
			throw new PedParseException(sb.toString());
		}
		/*
		 * If we get here, the names are the same in both files, but their order may be different.
		 */
		ArrayList<Person> newList = new ArrayList<Person>();
		for (String s : sampleNames) {
			Iterator<Person> it = this.personList.iterator();
			while (it.hasNext()) {
				Person p = it.next();
				if (s.equals(p.getIndividualID())) {
					newList.add(p);
					break;
				}
			}
		}
		if (newList.size() != this.personList.size()) {
			String e = String.format("Error adjusting sample order." + "Added %d samples to new list but "
					+ " the original PED file has %d", newList.size(), personList.size());
			throw new PedParseException(e);
		}
		this.personList = newList;
		setPersonIndices();
	}

	/**
	 * Add an individual to the current pedigree.
	 *
	 * @param person
	 * @throws jannovar.exception.PedParseException
	 */
	public void addIndividual(Person person) throws PedParseException {
		if (!this.familyID.equals(person.getFamilyID())) {
			String err = String.format("Attempt to add person with different family id (%s) to pedigree for family %s",
					person.getFamilyID(), this.familyID);
			throw new PedParseException(err);
		}
		this.personList.add(person);
	}

	public void setFamilyID(String id) {
		this.familyID = id;
	}

	/**
	 * This method sets the index field of the Person objects to their indices within the ArrayList {@link #personList}.
	 * This is useful to allow flexible searches for the pedigree analysis routines.
	 */
	private void setPersonIndices() {
		for (int i = 0; i < this.personList.size(); ++i) {
			Person p = this.personList.get(i);
			p.setIndex(i);
		}
	}

	/**
	 * Initialize the three lists, {@link #affectedList}, {@link #parentList} and {@link #unaffectedList}. This will put
	 * each {@link jannovar.pedigree.Person Person} object with a known Disease status into one of these three
	 * categories. Persons with an unknown Disease status are not included.
	 */
	private void initializeAffectedsParentsSibs() {
		this.affectedList = new ArrayList<Person>();
		this.parentList = new ArrayList<Person>();
		this.unaffectedList = new ArrayList<Person>();

		for (Person p : personList) {
			Disease ds = p.getDiseaseStatus();
			if (ds == Disease.AFFECTED) {
				this.affectedList.add(p);
			}
		}
		/*
		 * Note that the following code will still work if there are multiple sets of parents in the pedigree.
		 */
		for (Person p : affectedList) {
			Person father = p.getFather();
			Person mother = p.getMother();
			if (father != null && !this.parentList.contains(father))
				this.parentList.add(father);
			if (mother != null && !this.parentList.contains(mother))
				this.parentList.add(mother);
		}

		for (Person p : personList) {
			Disease ds = p.getDiseaseStatus();
			if (ds == Disease.UNAFFECTED) {
				if (!this.parentList.contains(p))
					this.unaffectedList.add(p);
			}
		}
	}

	/**
	 * This method sets links to the Mother and Father for each person in the pedigree. If there is some error, then the
	 * function returns false. If everything is ok, it returns true.
	 */
	private boolean findParentLinks() throws PedParseException {
		HashMap<String, Person> personMap = new HashMap<String, Person>();
		for (Person p : personList) {
			String id = p.getIndividualID();
			personMap.put(id, p);
		}
		for (Person p : personList) {
			String fatherID = p.getFatherID();
			String motherID = p.getMotherID();
			if (fatherID != null) {
				Person father = personMap.get(fatherID);
				if (father == null) {
					String s = String.format("[Pedigree] Could not find father id: %s", fatherID);
					throw new PedParseException(s);
				}
				p.setFather(father);
			}
			if (motherID != null) {
				Person mother = personMap.get(motherID);
				if (mother == null) {
					String s = String.format("[Pedigree] Could not find mother id: %s", motherID);
					throw new PedParseException(s);
				}
				p.setMother(mother);
			}
		}
		return true;
	}

	/**
	 * @return The number of individuals in the PED file (or simply "1" for a single-sample VCF file).
	 */
	public int getNumberOfIndividualsInPedigree() {
		if (isSingleSample)
			return 1;
		else
			return this.personList.size();
	}

	/**
	 * Returns the number of parents in the pedigree. If there is only a single sample, returns zero because the
	 * assumption is that a single sample is from an affected.
	 *
	 * @return the number of parents in the pedigree
	 */
	public int getNumberOfParentsInPedigree() {
		if (isSingleSample)
			return 0;
		else
			return this.parentList.size();
	}

	// TODO(holtgrew): Document restriction of single sample pedigrees!
	public int getNumberOfAffectedsInPedigree() {
		if (isSingleSample)
			return 1;
		else
			return this.affectedList.size();
	}

	public int getNumberOfUnaffectedsInPedigree() {
		if (isSingleSample)
			return 0;
		else
			return this.unaffectedList.size();
	}

	/** Return it-h Person in Pedigree. */
	public Person get(int idx) {
		return this.personList.get(idx);
	}

	public Person getPerson(String id) {
		for (Person p : this.personList) {
			if (id.equals(p.getIndividualID()))
				return p;
		}
		return null;
	}

	/**
	 * This function checks whether the Genotypes passed are compatible with autosomal dominant inheritance. The
	 * {@link jannovar.pedigree.GenotypeCall GenotypeCall} object passed to this function is expected to represent all
	 * of the variants found in a certain gene (possibly after filtering for rarity or predicted pathogenicity). For
	 * autosomal dominant inheritance, there must be at least one Variant that is shared by all affected persons but no
	 * unaffected persons in the pedigree. The samples represented by the {@link jannovar.pedigree.GenotypeCall
	 * GenotypeCall} must be in the same order as the list of Persons contained in this pedigree.
	 *
	 * @param varList
	 *            A list of variants (usually all variants corresponding to one gene).
	 * @return <code>true</code> if gene is autosomal dominant inheritance compatible
	 */
	public boolean isCompatibleWithAutosomalDominant(ArrayList<Variant> varList) {

		/**
		 * If the VCF file only contains one sample, the following if-clause will be executed.
		 */
		if (this.isSingleSample) {
			return singleSampleHasHeterozygousVariant(varList);
		}
		// for (GenotypeCall multiGT : gtypeList){
		for (Variant v : varList) {
			GenotypeCall multiGT = v.getGenotype();
			int N = multiGT.getNumberOfIndividuals();
			boolean variantCompatible = true; /*
											 * Is the current variant compatible with AD?
											 */
			int n_affected_with_het = 0;
			for (int i = 0; i < N; ++i) {
				Genotype gt = multiGT.getGenotypeInIndividualN(i);
				Disease diseaseStatus = personList.get(i).getDiseaseStatus();
				// System.out.println("i="+i+ ": " + gt + ", " + diseaseStatus);
				/* if person affected, there must be a heterozygous mutation. */
				if (diseaseStatus == Disease.AFFECTED) {
					if (gt == Genotype.HOMOZYGOUS_REF || gt == Genotype.HOMOZYGOUS_ALT) {
						variantCompatible = false;
						break; /* this variant is not compatible with AD */
					} else if (gt == Genotype.HETEROZYGOUS) {
						variantCompatible = true;
						n_affected_with_het++;
					}
					/*
					 * Note if genotype is unknown, then we still allow this variant to be compatible with AD. However,
					 * we require that ther is at least one "good" observation.
					 */
				} else if (diseaseStatus == Disease.UNAFFECTED) {
					if (gt == Genotype.HETEROZYGOUS || gt == Genotype.HOMOZYGOUS_ALT) {
						variantCompatible = false;
						break;
					}
				}
				// System.out.println("Variant compatible: "
				// +variantCompatible);
			}
			/*
			 * If we get here, we have either examined all members of the pedigree or have decided that the variant is
			 * incompatible in one person. If any one variant is compatible with AD inheritance, than the Gene is
			 * compatilbe and we can return true without examining the other variants.
			 */
			if (variantCompatible && n_affected_with_het > 0)
				return true;
		}
		return false;
	}

	/**
	 * This function checks if all affecteds in the pedigree are homozygous
	 *
	 * @param multiGT
	 *            Genotypes of all persons in the pedigree for the current variant.
	 * @return true if all affecteds have a HOMOZYGOUS_ALT genotype.
	 */
	private boolean affectedsAreHomozygousALT(GenotypeCall multiGT) {
		for (Person p : this.affectedList) {
			int idx = p.getIndex();
			Genotype gt = multiGT.getGenotypeInIndividualN(idx);
			if (gt != Genotype.HOMOZYGOUS_ALT)
				return false;
		}
		/* If we get here, all affecteds are homozygous for the ALT sequence. */
		return true;
	}

	/**
	 * @param multiGT
	 *            Genotypes of all persons in the pedigree for the current variant.
	 * @return true if all of the parents are heterozygous for this variant.
	 */
	private boolean parentsAreHeterozygous(GenotypeCall multiGT) {
		for (Person p : this.parentList) {
			int idx = p.getIndex();
			Genotype gt = multiGT.getGenotypeInIndividualN(idx);
			if (gt != Genotype.HETEROZYGOUS)
				return false;
		}
		/* If we get here, all parents are heterozygous for the ALT sequence. */
		return true;
	}

	/**
	 * This function checks if all of the unaffecteds are either HETEROZYGOUS, HOMOZYGOUS_REF, or UNKNOWN.
	 *
	 * @param multiGT
	 *            Genotypes of all persons in the pedigree for the current variant.
	 * @return true if none of the unaffecteds has a HOMOZYGOUS_ALT genotype.
	 */
	private boolean unaffectedsAreNotHomozygousALT(GenotypeCall multiGT) {
		for (Person p : this.unaffectedList) {
			int idx = p.getIndex();
			Genotype gt = multiGT.getGenotypeInIndividualN(idx);
			if (gt == Genotype.HOMOZYGOUS_ALT)
				return false;
		}
		return true;
	}

	/**
	 * This function checks if all affecteds in the pedigree are heterozygous
	 *
	 * @param multiGT
	 *            Genotypes of all persons in the pedigree for the current variant.
	 * @return true if all affecteds have a HETEROZYGOUS genotype.
	 */
	private boolean affectedsAreHeterozygous(GenotypeCall multiGT) {
		for (Person p : this.affectedList) {
			int idx = p.getIndex();
			Genotype gt = multiGT.getGenotypeInIndividualN(idx);
			if (gt != Genotype.HETEROZYGOUS)
				return false;
		}
		/* If we get here, all affecteds are heterozygous for the ALT sequence. */
		return true;
	}

	/**
	 * This function checks if only one of the parents is heterozygous
	 *
	 * @param multiGT
	 *            Genotypes of all persons in the pedigree for the current variant.
	 * @return true if only one of the parent has a HETEROZYGOUS genotype.
	 */
	private boolean onlyOneParentIsHeterozygous(GenotypeCall multiGT) {
		int n = 0;
		for (Person p : this.parentList) {
			int idx = p.getIndex();
			Genotype gt = multiGT.getGenotypeInIndividualN(idx);
			if (gt == Genotype.HETEROZYGOUS)
				n++;
		}
		return n == 1;
	}

	private boolean fatherIsHeterozygous(GenotypeCall multiGT) {
		for (Person p : this.parentList) {
			int idx = p.getIndex();
			Genotype gt = multiGT.getGenotypeInIndividualN(idx);
			if (p.isMale() && gt == Genotype.HETEROZYGOUS)
				return true;
		}
		return false;
	}

	private boolean motherIsHeterozygous(GenotypeCall multiGT) {
		for (Person p : this.parentList) {
			int idx = p.getIndex();
			Genotype gt = multiGT.getGenotypeInIndividualN(idx);
			if (p.isFemale() && gt == Genotype.HETEROZYGOUS)
				return true;
		}
		return false;
	}

	/**
	 * This function checks whether at least one of the variants represented by the list of genotypes is a homozygous
	 * ALT variant that is shared by all of the affecteds. Additionally, both parents (if they are represented in the
	 * pedigree) must be heterozygous. Any unaffected siblings must be either heterozygous or homozygous ref.
	 *
	 * @param GT
	 *            a List of Genotypes representing all variants seen in some gene
	 * @return true if the distribution of variants is compatible with a homozygous mutation.
	 */
	public boolean containsCompatibleHomozygousVariant(GenotypeCall GT) {
		if (this.isSingleSample) {
			return (Genotype.HOMOZYGOUS_ALT == GT.getGenotypeInIndividualN(0));
		} else if (affectedsAreHomozygousALT(GT) && parentsAreHeterozygous(GT) && unaffectedsAreNotHomozygousALT(GT)) {
			/*
			 * i.e., multiple sample VCF compatible with homozygous var and AR inheritance.
			 */
			return true;
		} else {
			/*
			 * If we get here, none of the variants represents a compatible homozygous mutation.
			 */
			return false;
		}
	}

	/**
	 * This function checks that none of the unaffecteds have both variants. By the time we call this function, we
	 * already know that all of the affecteds are heterozygous for both mutations, that each parent is heterozygous for
	 * only one of the mutations, and thus we only need to show that none of the unaffecteds is compound het.
	 *
	 * @param matGT
	 *            A het mutation candidate that is heterozygous in mother only
	 * @param patGT
	 *            A het mutation candidate that is heterozygous in father only
	 * @return true if none of the unaffecteds are compound het for this pair of variants.
	 */
	private boolean validCompoundHet(GenotypeCall matGT, GenotypeCall patGT) {
		for (Person p : this.unaffectedList) {
			int i = p.getIndex();
			Genotype g1 = matGT.getGenotypeInIndividualN(i);
			Genotype g2 = patGT.getGenotypeInIndividualN(i);
			if (g1 == Genotype.HETEROZYGOUS && g2 == Genotype.HETEROZYGOUS)
				return false;
		}
		return true;
	}

	/**
	 * This function checks whether the gene, whose variants are represented in the list of genotypes passed to the
	 * function, has at least two variants compatible with autosomal recessive inheritance. It first checks whether
	 * there is a homozygous variant that is compatible with AR. If there is none, it checks for compound hets. This is
	 * a little complicated. The function first checks whether there is a variant that is heterozygous in the affected
	 * and heteroygous in one, but not both, of the parents. All such variants are stored. If there are such variants,
	 * then it checks whether the maternal-het mutations are compatible with the paternal het mutations, and it returns
	 * all variants for which there are compatible pairs.
	 *
	 * @param varList
	 *            A list of variants (usually all variants in some gene).
	 * @return <code>true</code> if gene is autosomal recessive inheritance compatible
	 */
	public boolean isCompatibleWithAutosomalRecessiveHomozygous(ArrayList<Variant> varList) {
		if (this.isSingleSample) {
			for (Variant v : varList) {
				GenotypeCall gc = v.getGenotype();
				Genotype g = gc.getGenotypeInIndividualN(0);
				if (g == Genotype.HOMOZYGOUS_ALT)
					return true;
			}
			return false; /* if we get here, there is no homozygous var */
		}
		/* If we get here, there is a multiple sample VCF plus PED file */
		if (this.parentList.size() > 2) {
			throw new UnsupportedOperationException(
					"Autosomal recessive pedigree analysis with more than two parents is not supported!");
		}
		/* Just look and see if there is a homozygous variant that is compatible */
		for (Variant v : varList) {
			GenotypeCall multiGT = v.getGenotype();
			if (containsCompatibleHomozygousVariant(multiGT)) {
				/* If this is the case, we are good. */
				return true;
			}
		}
		return false;
	}

	/**
	 * This function checks whether the gene, whose variants are represented in the list of genotypes passed to the
	 * function, has at least two variants compatible with autosomal recessive inheritance. It first checks whether
	 * there is a homozygous variant that is compatible with AR. If there is none, it checks for compound hets. This is
	 * a little complicated. The function first checks whether there is a variant that is heterozygous in the affected
	 * and heteroygous in one, but not both, of the parents. All such variants are stored. If there are such variants,
	 * then it checks whether the maternal-het mutations are compatible with the paternal het mutations, and it returns
	 * all variants for which there are compatible pairs.
	 *
	 * @param varList
	 *            A list of variants (usually all variants in some gene).
	 * @return <code>true</code> if gene is autosomal recessive inheritance compatible
	 */
	public boolean isCompatibleWithAutosomalRecessiveCompoundHet(ArrayList<Variant> varList) {
		if (this.isSingleSample) {
			int n_het = 0;
			for (Variant v : varList) {
				GenotypeCall gc = v.getGenotype();
				Genotype g = gc.getGenotypeInIndividualN(0);
				if (g == Genotype.HETEROZYGOUS)
					n_het++;
			}
			return n_het > 1;
		}
		// boolean hasMaternallyInheritedCompatibleVariant = false;
		// boolean hasPaternallyInheritedCompatibleVariant = false;
		ArrayList<GenotypeCall> paternal = new ArrayList<GenotypeCall>();
		ArrayList<GenotypeCall> maternal = new ArrayList<GenotypeCall>();

		if (this.parentList.size() > 2) {
			throw new UnsupportedOperationException(
					"Autosomal recessive pedigree analysis with more than two parents is not supported!");
		}
		for (Variant v : varList) {
			GenotypeCall multiGT = v.getGenotype();
			if (affectedsAreHeterozygous(multiGT) && onlyOneParentIsHeterozygous(multiGT)
					&& unaffectedsAreNotHomozygousALT(multiGT)) {
				if (fatherIsHeterozygous(multiGT))
					paternal.add(multiGT);
				else if (motherIsHeterozygous(multiGT))
					maternal.add(multiGT);
				else {
					/* This can never happen, it is just a sanity check! */
					System.err.println("ERROR: Neither mother nor father het with at least one parent being het");
					System.exit(1);
				}
			}
		}
		/*
		 * When we get here, we have (potentially empty) lists of GenotypeCalls that are heterozygous in the father or
		 * mother. If there is a combination of maternal and paternal genotypes that could be a valid compound
		 * heterozygous mutation, then return true! The strategy is to iterate over paternal variants and check all
		 * maternal variants for compatibility.
		 */
		for (GenotypeCall patGT : paternal) {
			for (GenotypeCall matGT : maternal) {
				if (validCompoundHet(matGT, patGT))
					return true;
			}
		}
		return false;
	}

	/**
	 * This function checks whether the gene, whose variants are represented in the list of genotypes passed to the
	 * function, has at least two variants compatible with autosomal recessive inheritance. It first checks whether
	 * there is a homozygous variant that is compatible with AR. If there is none, it checks for compound hets. This is
	 * a little complicated. The function first checks whether there is a variant that is heterozygous in the affected
	 * and heteroygous in one, but not both, of the parents. All such variants are stored. If there are such variants,
	 * then it checks whether the maternal-het mutations are compatible with the paternal het mutations, and it returns
	 * all variants for which there are compatible pairs.
	 *
	 * @param varList
	 *            A list of {@link Variant}s (usually all variants in some gene).
	 * @return whether the list of variant is compatible with automal recessive inheritance
	 */
	public boolean isCompatibleWithAutosomalRecessive(ArrayList<Variant> varList) {
		if (this.isSingleSample) {
			return singleSampleCompatibleWithAutosomalRecessive(varList);
		}
		// boolean hasMaternallyInheritedCompatibleVariant = false;
		// boolean hasPaternallyInheritedCompatibleVariant = false;
		ArrayList<GenotypeCall> paternal = new ArrayList<GenotypeCall>();
		ArrayList<GenotypeCall> maternal = new ArrayList<GenotypeCall>();

		if (this.parentList.size() > 2) {
			return false;
			// throw new
			// UnsupportedOperationException("Autosomal recessive pedigree analysis with more than two parents is not supported!");
		}

		for (Variant v : varList) {
			GenotypeCall multiGT = v.getGenotype();
			if (containsCompatibleHomozygousVariant(multiGT)) {
				/* If this is the case, we are good. */
				return true;
			}
			/*
			 * System.out.println("# 1 not hom"); if (affectedsAreHeterozygous(multiGT))
			 * System.out.println("# 2 affecteds are het"); else System.out.println("# 2 affecteds are not het");
			 *
			 * if (onlyOneParentIsHeterozygous(multiGT)) System.out.println("# 3 only one paret is het"); else
			 * System.out.println("# 3 not only one parent is het het");
			 *
			 * if (unaffectedsAreNotHomozygousALT(multiGT) ) System.out.println("# 4 unaffects Not Hom"); else
			 * System.out.println("# 4 NOT unaffects Not Hom");
			 */
			if (affectedsAreHeterozygous(multiGT) && onlyOneParentIsHeterozygous(multiGT)
					&& unaffectedsAreNotHomozygousALT(multiGT)) {
				if (fatherIsHeterozygous(multiGT))
					paternal.add(multiGT);
				else if (motherIsHeterozygous(multiGT))
					maternal.add(multiGT);
				else {
					/* This can never happen, it is just a sanity check! */
					System.err.println("ERROR: Neither mother nor father het with at least one parent being het");
					System.exit(1);
				}
			}
		}
		/*
		 * When we get here, we have (potentially empty) lists of GenotypeCalls that are heterozygous in the father or
		 * mother. If there is a combination of maternal and paternal genotypes that could be a valid compound
		 * heterozygous mutation, then return true! The strategy is to iterate over paternal variants and check all
		 * maternal variants for compatibility.
		 */
		for (GenotypeCall patGT : paternal) {
			for (GenotypeCall matGT : maternal) {
				if (validCompoundHet(matGT, patGT))
					return true;
			}
		}
		return false;
	}

	/**
	 * This function checks whether the gene, whose variants are represented in the list of genotypes passed to the
	 * function, has a variant compatible with X chromosomal recessive inheritance.
	 *
	 * If there is only one sample, this is the case if the variant is called homozygous (we are assuming this is a male
	 * sample with a hemizygous variant that has been called homozygous alt)
	 *
	 * If there are multiple samples, then
	 *
	 * @param varList
	 *            list of {@link Variant}s (usually all variants in some gene)
	 * @return compatibility with X chromosomal recessiv inheritance
	 */
	public boolean isCompatibleWithXChromosomalRecessive(ArrayList<Variant> varList) {
		if (varList.isEmpty()) {
			System.out.println("[Pedigree.java] Warning: attempt to test zero-length variant list");
			return false;
		}
		/*
		 * First check whether the gene is X-chromosomal, if not, it cannot be X chromosomally inherited!.
		 */
		if (!varList.get(0).is_X_chromosomal()) {
			return false;
		}
		// TODO(holtgrem): If sample is female then we should also check for compound heterozygous
		if (this.isSingleSample) {
			for (Variant v : varList) {
				GenotypeCall gc = v.getGenotype();
				Genotype g = gc.getGenotypeInIndividualN(0);
				if (g == Genotype.HOMOZYGOUS_ALT)
					return true;
			}
			return false; /* Single sample, no appropriate variant in this gene. */
		}
		// TODO(holtgrem): There are pedigrees where compound heterozygous is possible.
		/* If we get here, there is a multiple sample. */
		for (Variant v : varList) {
			GenotypeCall gc = v.getGenotype();
			boolean compatible = true;
			for (Person p : affectedList) {
				int i = p.getIndex();
				Genotype g = gc.getGenotypeInIndividualN(i);
				if (g != Genotype.HOMOZYGOUS_ALT) {
					compatible = false;
					break;
					/*
					 * Cannot be disease-causing mutation, an affected male does not have it.
					 */
				}
				if (!compatible)
					break;
			}
			for (Person p : parentList) {
				int i = p.getIndex();
				Genotype g = gc.getGenotypeInIndividualN(i);
				if (p.isMale() && !p.isAffected() && g == Genotype.HOMOZYGOUS_ALT) {
					compatible = false;
					break;
					/*
					 * Cannot be disease-causing mutation, an unaffected father has it.
					 */
				}
				if (p.isFemale() && g != Genotype.HETEROZYGOUS) {
					compatible = false;
					break;
					/*
					 * Cannot be disease-causing mutation, mother of patient is not heterozygous.
					 */
				}
				if (!compatible)
					break;
			}
			for (Person p : unaffectedList) {
				int i = p.getIndex();
				Genotype g = gc.getGenotypeInIndividualN(i);
				if (p.isMale() && g == Genotype.HOMOZYGOUS_ALT) {
					compatible = false;
					break;
					/*
					 * Cannot be disease-causing mutation, an unaffected brother has it.
					 */
				}
				if (p.isFemale() && g == Genotype.HOMOZYGOUS_ALT) {
					compatible = false;
					break;
					/*
					 * Cannot be disease-causing mutation, an unaffected sister is homozygos.
					 */
				}
			}
			if (compatible)
				return true;
		}
		return false;
	}

	/**
	 * Prints Pedigree info for debugging.
	 */
	public void debugPrint() {
		System.out.println("Pedigree: " + familyID + " [n=" + getNumberOfIndividualsInPedigree() + "]");
		System.out.println(String.format("Parents: n=%d, Affecteds: n=%d, Unaffecteds: n=%d",
				getNumberOfParentsInPedigree(), getNumberOfAffectedsInPedigree(), getNumberOfUnaffectedsInPedigree()));
		for (Person p : personList) {
			System.out.println(p.getIndex() + ": " + p);
		}

		System.out.println("AffectedList");
		for (Person p : affectedList) {
			System.out.println(p.getIndex() + ": " + p);
		}
		System.out.println("ParentList");
		for (Person p : parentList) {
			System.out.println(p.getIndex() + ": " + p);
		}
		System.out.println("UnaffectedList");
		for (Person p : unaffectedList) {
			System.out.println(p.getIndex() + ": " + p);
		}
	}

	/**
	 * Get a summary line representing the pedigree that can be used for output
	 *
	 * @return summary of this {@link Pedigree}
	 */
	public String getPedigreeSummary() {
		StringBuilder sb = new StringBuilder();
		sb.append(familyID).append(":");
		boolean b = false;
		for (Person p : personList) {
			if (b)
				sb.append(":");
			b = true;
			sb.append(p.getIndividualID());
			if (p.isAffected())
				sb.append("[affected");
			else
				sb.append("[unaffected");
			if (this.parentList.contains(p)) {
				if (p.isFemale())
					sb.append(";mother]");
				else
					sb.append(";father]");
			} else {
				if (p.isFemale())
					sb.append(";female]");
				else
					sb.append(";male]");
			}
		}
		return sb.toString();

	}

	/**
	 * This function checks whether the gene, whose variants are represented in the list of genotypes passed to the
	 * function are compatible with autosomal recessive inheritance. This is the case if there is a homozygous variant
	 * or there are two heterozygous variants (this is a candidate for a compound heterozygous mutation).
	 *
	 * Since this is a single sample, we just check in the proband.
	 *
	 * @param varList
	 *            list of {@link Variant}s (usually all variants in some gene)
	 * @return <code>true</code> if gene is autosomal recessive inheritance compatible
	 */
	public boolean singleSampleCompatibleWithAutosomalRecessive(ArrayList<Variant> varList) {
		int n_het = 0;
		for (Variant v : varList) {
			GenotypeCall gc = v.getGenotype();
			Genotype g = gc.getGenotypeInIndividualN(0);
			if (g == Genotype.HOMOZYGOUS_ALT)
				return true;
			else if (g == Genotype.HETEROZYGOUS)
				n_het++;
		}
		return n_het > 1;
	}

	/**
	 * This function checks whether the gene, whose variants are represented in the list of genotypes passed to the
	 * function, has at least one heterozygous variant compatible with autosomal dominant inheritance or with
	 * X-chromosomeal recessive inheritance. Since this is a single sample, we just check in the proband.
	 *
	 * @param varList
	 *            list of {@link Variant}s (usually all variants in some gene)
	 * @return <code>true</code> if gene is autosomal dominant inheritance or X-chromosomeal recessive inheritance
	 *         compatible
	 */
	public boolean singleSampleHasHeterozygousVariant(ArrayList<Variant> varList) {
		for (Variant v : varList) {
			GenotypeCall gc = v.getGenotype();
			Genotype g = gc.getGenotypeInIndividualN(0);
			if (g == Genotype.HETEROZYGOUS)
				return true;
		}
		return false;
	}
}