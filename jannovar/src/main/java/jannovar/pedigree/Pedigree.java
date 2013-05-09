package jpedfilter.pedigree;

import java.util.ArrayList;
import java.util.HashMap;


import jpedfilter.exception.PedParseException;
import jpedfilter.genotype.MultipleGenotype;
import jpedfilter.common.Disease;
import jpedfilter.common.Genotype;

/**
 * Model a single-family pedigree in an whole-exome sequencing project.
 * Note that the constructor expects to receive a String with the
 * family ID. If an individual is added to the Pedigree with a different
 * family ID, then an exception is thrown.
 * <p>
 * This class is meant to simulate relatively simple pedigrees that can
 * be conceived of as Affecteds, Parents of Affects, and Unaffected Persons. The class is not
 * meant to be used for formal Lander-Green or Elston-Stewart linkage analysis. Instead, it
 * is meant to be used for relatively simple pedigrees commonly analysed in teh setting of
 * whole-exome sequencing.
 * <P>
 * The assumptions of this class are as follows
 * <P>
 * For autosomal dominant pedigrees, all affected persons must share the same heterozygous
 * mutation, and none of the unaffected persons can carry the mutation.
 * <P>
 * For autosomal recessive pedigrees, there are two possibilities
 * <ul>
 * <li>All affected persons share the same homozygous mutation. In this case, both parents
 * must be heterozygous for the mutation. The unaffected persons can be heterozygous or
 * homozygous wildtype.
 * <li>All affected persons are compound heterozygous for the same two mutations. One of the
 * parents is heterozygous for one of the mutations, and the other parent is heterozygous for
 * the other mutation. Unaffecteds can carry up to one of the mutations.
 * </ul>
 * For X chromosomal mutations, we are currently demanding that affected males are called homozygous
 * for the mutation (actually, they are hemizygous). We are only looking for X chromosomal recessive mutations
 * currently.
 * 
 * @author Peter Robinson
 * @version 0.05 (10 May, 2013)
 */
public class Pedigree {
    /**
     * The identifier of the family in the PED file.
     */
    private String familyID = null;
    /**
     * A list of all persons in this pedigree
     */
    private ArrayList<Person> personList = null;
    /**
     * A list of all persons in the pedigree affected by the disease. Note that 
     * this list is a subset of the persons in {@link #personList}.
     */
    private ArrayList<Person> affectedList = null;
    /**
     * A list of all persons in the pedigree who are parents of people affected
     * by the disease. Note that it is possible that a parent is also an affected
     * person (obviously, this is the case for autosomal dominant diseases).
     * Note that this list is a subset of the persons in {@link #personList}.
     */
    private ArrayList<Person> parentList = null;
    /**
     * A list of all persons in the pedigree who are unaffected and also not parents
     * of an affected person. This can comprise both siblings of affected persons and
     * also other types of relative. For autosomal recessive diseases, we expect that 
     * these people can be either heterozygous or homozygous wildtype. For autosomal
     * dominant diseases, these people should be homozygous wildtype. 
     * Note that this list is a subset of the persons in {@link #personList}.
     */
    private ArrayList<Person> unaffectedList = null;
    



    /**
     * Disallow default constructor (The only valid way to construct a Pedigree
     * object is with a list of Persons and a family ID)
     */
    private Pedigree() {
    }
    
    /**
     * @param famID the Family ID of this Pedigree.
     */
    public Pedigree(ArrayList<Person> pList, String famID) throws PedParseException {
       this.personList = new ArrayList<Person>();
       this.familyID = famID;
       for (Person p: pList) {
	   addIndividual(p);
       }
       boolean success = findParentLinks();
       if (! success) {
	   throw new PedParseException("Inconsistent Parent Relations in PED file");
       }
       setPersonIndices();
       initializeAffectedsParentsSibs();
    }
    
    /**
     * Add an individual to the current pedigree.
     * @param person
     */
    public void addIndividual(Person person) throws PedParseException {
	if (! this.familyID.equals(person.getFamilyID()) ) {
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
     * This method sets the index field of the Person objects to their indices
     * within the ArrayList {@link #personList}. This is useful to allow
     * flexible searches for the pedigree analysis routines.
     */
    private void setPersonIndices() {
	for (int i=0; i< this.personList.size();++i) {
	    Person p = this.personList.get(i);
	    p.setIndex(i);
	}
    }

    /**
     * Initialize the three lists, {@link #affectedList},
     * {@link #parentList} and {@link #unaffectedList}.
     * This will put each {@link jpedfilter.pedigree.Person Person}
     * object with a known Disease status into one of these three
     * categories. Persons with an unknown Disease status are not
     * included.
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
	/* Note that the following code will still work if
	   there are multiple sets of parents in the pedigree. */
	for (Person p : affectedList) {
	    Person father = p.getFather();
	    Person mother = p.getMother();
	    if (! this.parentList.contains(father))
		this.parentList.add(father);
	    if (! this.parentList.contains(mother))
		this.parentList.add(mother);
	}
	
	for (Person p : personList) {
	    Disease ds = p.getDiseaseStatus();
	    if (ds == Disease.UNAFFECTED) {
		if (! this.parentList.contains(p) ) 
		    this.unaffectedList.add(p);
	    }
	}
    }
    
    /**
     * This method sets links to the Mother and Father for each
     * person in the pedigree. If there is some error, then the
     * function returns false. If everything is ok, it returns true.
     */
    private boolean findParentLinks() throws PedParseException {
        HashMap<String,Person> personMap = new HashMap<String,Person>();
        for (Person p : personList) {
            String id = p.getIndividualID();
            personMap.put(id,p);
        }
        for (Person p : personList) {
            String fatherID = p.getFatherID();
            String motherID = p.getMotherID();
            if (fatherID != null) {
                Person father = personMap.get(fatherID);
                if (father == null) {
		    System.out.println("FatherID=" + fatherID + " but father null");
                    debugPrint();
                    return false;
                }
                p.setFather(father);
            }
            if (motherID != null) {
                Person mother = personMap.get(motherID);
                if (mother == null) {
		    System.out.println("MotherID=" + fatherID + " but mother null");
                    debugPrint();
                    return false;
                }
                p.setMother(mother);
            }
        }
        return true;
    }
    
    public int getNumberOfIndividualsInPedigree(){
        return this.personList.size();
    }
    
    public Person getPerson(String id) {
        for (Person p : this.personList) {
            if (id.equals(p.getIndividualID()))
                return p;
        }
        return null;
    }
    
    /**
     * This function checks whether the Genotypes passed are compatible with
     * autosomal dominant inheritance. The
     * {@link jpedfilter.genotype.MultipleGenotype MultipleGenotype}
     * object passed to this function is expected to represent all of the variants found in
     * a certain gene (possibly after filtering for rarity or predicted pathogenicity).
     * For autosomal dominant inheritance, there must be at least one Variant that
     * is shared by all affected persons but no unaffected persons in the pedigree.
     * The samples represented by the
     * {@link jpedfilter.genotype.MultipleGenotype MultipleGenotype} must be in
     * the same order as the list of Persons contained in this pedigree.
     */
    public boolean isCompatibleWithAutosomalDominant(ArrayList<MultipleGenotype> gtypeList) {
        for (MultipleGenotype multiGT : gtypeList){
          int N = multiGT.getNumberOfIndividuals();
          boolean variantCompatible=true; /* Is the current variant compatible with AD? */
          int n_affected_with_het = 0;
          for (int i=0;i<N;++i) {
              Genotype gt = multiGT.getGenotypeInIndividualN(i);
              Disease diseaseStatus = personList.get(i).getDiseaseStatus();
              //System.out.println("i="+i+ ": " + gt + ", " + diseaseStatus);
              /* if person affected, there must be a heterozygous mutation. */
              if (diseaseStatus == Disease.AFFECTED) {
                if (gt == Genotype.HOMOZYGOUS_REF || gt == Genotype.HOMOZYGOUS_ALT) {
                    variantCompatible = false;
                    break; /* this variant is not compatible with AD */
                } else if (gt == Genotype.HETEROZYGOUS) {
                    variantCompatible = true;
                    n_affected_with_het++;
                }
                /* Note if genotype is unknown, then we still allow this
                  variant to be compatible with AD. However, we require that ther
                  is at least one "good" observation. */
              } else if (diseaseStatus == Disease.UNAFFECTED) {
                if (gt == Genotype.HETEROZYGOUS || gt == Genotype.HOMOZYGOUS_ALT) {
                    variantCompatible = false;
                    break;
                }
              }
              //System.out.println("Variant compatible: " +variantCompatible);
          }
          /* If we get here, we have either examined all members of the pedigree
                or have decided that the variant is incompatible in one person. If
                any one variant is compatible with AD inheritance, than the Gene is
                compatilbe and we can return true without examining the other variants. */
          if (variantCompatible && n_affected_with_het>0)
                return true;
        }
        return false;
    }


    /**
     * This function checks if all affecteds in the pedigree are
     * homozygous
     * @param multiGT Genotypes of all persons in the pedigree for the current variant.
     * @return true if all affecteds have a HOMOZYGOUS_ALT genotype.
     */
    private boolean affectedsAreHomozygousALT(MultipleGenotype multiGT) {
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
     * @param multiGT Genotypes of all persons in the pedigree for the current variant.
     * @return true if all of the parents are heterozygous for this variant.
     */
    private boolean parentsAreHeterozygous(MultipleGenotype multiGT) {
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
     * This function checks if all of the unaffecteds are either HETEROZYGOUS,
     * HOMOZYGOUS_REF, or UNKNOWN.
     * @param multiGT Genotypes of all persons in the pedigree for the current variant.
     * @return true if none of the unaffecteds has a HOMOZYGOUS_ALT genotype.
     */
     private boolean unaffectedsAreNotHomozygousALT(MultipleGenotype multiGT) {
	 for (Person p : this.unaffectedList) {
	     int idx = p.getIndex();
	     Genotype gt = multiGT.getGenotypeInIndividualN(idx);
	     if (gt == Genotype.HOMOZYGOUS_ALT)
		 return false;
	 }
	 return true;
     }


    /**
     * This function checks if all affecteds in the pedigree are
     * heterozygous
     * @param multiGT Genotypes of all persons in the pedigree for the current variant.
     * @return true if all affecteds have a HETEROZYGOUS genotype.
     */
    private boolean affectedsAreHeterozygous(MultipleGenotype multiGT) {
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
     * @param multiGT Genotypes of all persons in the pedigree for the current variant.
     * @return true if only one of the parent has a HETEROZYGOUS genotype.
     */
    private boolean onlyOneParentIsHeterozygous(MultipleGenotype multiGT) {
	int n=0;
	for (Person p : this.parentList) {
	    int idx = p.getIndex();
	    Genotype gt = multiGT.getGenotypeInIndividualN(idx);
	    if (gt == Genotype.HETEROZYGOUS)
		n++;
	}
	if (n==1)
	    return true;
	else
	    return false;
    }

    

    private boolean fatherIsHeterozygous(MultipleGenotype multiGT) {
	for (Person p : this.parentList) {
	    int idx = p.getIndex();
	    Genotype gt = multiGT.getGenotypeInIndividualN(idx);
	    if (p.isMale() &&  gt == Genotype.HETEROZYGOUS)
		return true;
	}
	return false;
    }

     private boolean motherIsHeterozygous(MultipleGenotype multiGT) {
	for (Person p : this.parentList) {
	    int idx = p.getIndex();
	    Genotype gt = multiGT.getGenotypeInIndividualN(idx);
	    if (p.isFemale() &&  gt == Genotype.HETEROZYGOUS)
		return true;
	}
	return false;
    }
	


    /**
     * This function checks whether at least one of the variants represented by the list of
     * genotypes is a homozygous ALT variant that is shared by all of the affecteds. Additionally,
     * both parents (if they are represented in the pedigree) must be heterozygous. Any unaffected
     * siblings must be either heterozygous or homozygous ref.
     * @param gtypeList a List of Genotypes representing all variants seen in some gene
     * @return true if the distribution of variants is compatible with a homozygous mutation.
     */
    public boolean containsCompatibleHomozygousVariant(ArrayList<MultipleGenotype> gtypeList) {
	for (MultipleGenotype multiGT : gtypeList){
	    if (affectedsAreHomozygousALT(multiGT) &&
		parentsAreHeterozygous(multiGT) &&
		unaffectedsAreNotHomozygousALT(multiGT))
		return true;
	}
	/* If we get here, none of the variants represents a compatible
	   homozygous mutation. */
	return false;
    }

    /**
     * This function checks that none of the unaffecteds have both variants. By the
     * time we call this function, we already know that all of the affecteds are 
     * heterozygous for both mutations, that each parent is heterozygous for only
     * one of the mutations, and thus we only need to show that none of the
     * unaffecteds is compound het.
     * @param matGT A het mutation candidate that is heterozygous in mother only
     * @param patGT A het mutation candidate that is heterozygous in father only
     * @return true if none of the unaffecteds are compound het for this pair of variants.
     */
    private boolean validCompoundHet(MultipleGenotype matGT, MultipleGenotype patGT) {
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
     * This function checks whether the gene, whose variants are represented in the list
     * of genotypes passed to the function, has at least two variants compatible with
     * autosomal recessive inheritance. It first checks whether there is a homozygous
     * variant that is compatible with AR. If there is none, it checks for compound hets.
     * This is a little complicated. The function first checks whether there is a variant
     * that is heterozygous in the affected and heteroygous in one, but not both, of the
     * parents. All such variants are stored. If there are such variants, then it checks
     * whether the maternal-het mutations are compatible with the paternal het mutations, and
     * it returns all variants for which there are compatible pairs.
     */
    public boolean isCompatibleWithAutosomalRecessive(ArrayList<MultipleGenotype> gtypeList) {
	if (containsCompatibleHomozygousVariant(gtypeList))
	    return true;
	/* If we get here, there is no compatible homozygous mutation. 
	   Check for compound heterozygous mutations. */
	boolean hasMaternallyInheritedCompatibleVariant = false;
	boolean hasPaternallyInheritedCompatibleVariant = false;
	ArrayList<MultipleGenotype> paternal = new ArrayList<MultipleGenotype> ();
	ArrayList<MultipleGenotype> maternal = new ArrayList<MultipleGenotype> ();

	if (this.parentList.size()>2) {
	    throw new UnsupportedOperationException("Autosomal recessive pedigree analysis with more than two parentsis not supported!");
	}

	for (MultipleGenotype multiGT : gtypeList) {
	    if (affectedsAreHeterozygous(multiGT) &&
		onlyOneParentIsHeterozygous(multiGT) &&
		unaffectedsAreNotHomozygousALT(multiGT) ) {
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
	/* When we get here, we have (potentially empty) lists of MultipleGenotypes that are
	   heterozygous in the father or mother. If there is a combination of maternal and paternal
	   genotypes that could be a valid compound heterozygous mutation, then return true!
	   The strategy is to iterate over paternal variants and check all maternal variants for
	   compatibility. */
	for (MultipleGenotype patGT : paternal) {
	    for (MultipleGenotype matGT : maternal) {
		System.out.println("BLA: patGT:" + patGT + "  matGT=" + matGT);
		if (validCompoundHet(matGT,patGT))
		    return true;
	    }
	}
	return false;
    }
    
    
    
    public void debugPrint() {
        System.out.println("Pedigree: " + familyID);
        
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
}