package jannovar.pedigree;

import java.util.ArrayList;

import jannovar.exception.PedParseException;
import jannovar.common.Disease;
/**
 * A class representing one individual within a
 * {@link jannovar.pedigree.Pedigree Pedigree}.
 * In essence, this class stores data representing the six fields of a PED file:
 * 
 * @author Peter Robinson
 * @version 0.06 (27 December, 2013)
 */
public class Person {
    /**
     * The family ID of the pedigree of which this person is a part.
     */
    private String familyID = null;
    /**
     * The ID of this person (individual) within the pedigree.
     */
    private String individualID = null;
    /**
     * The ID of the father of this person in the pedigree. Note: If this
     * person is a founder, then mother and father ID are both "0".
     */
    private String fatherID = null;
    /**
     * A reference to the Person object of the father of this person.
     * If this person is a founder, then this reference is null.
     */
    private Person father = null;
    /**
     * The ID of the mother of this person in the pedigree. Note: If this
     * person is a founder, then mther and father ID are both "0".
     */
    private String motherID = null;
     /**
     * A reference to the Person object of the mother of this person.
     * * If this person is a founder, then this reference is null.
     */
    private Person mother = null;
    /**
     * In the pedfile, this corresponds to  (1 = Male, 2 = Female) 
     */
    private static  enum Sex {MALE, FEMALE};
    
    /**
     * The gender of the person
     * */
    private Sex sex;
    /**
     * The disease status of the person.
     */
    private Disease disease;
    /**
     * Is this person a founder?
     */
    private boolean isFounder = false;
    /**
     * This variable is used to enable flexible searching within the
     * {@link jannovar.pedigree.Pedigree Pedigree} object. It corresponds
     * to the index of the Person in the list of persons of the pedigree.
     */
    private int index;

    public Person(String famID,String indID,String fathID,
                  String mothID,String sx,String dse) throws PedParseException {
        this.familyID = famID;
        this.individualID = indID;
        this.fatherID = fathID;
        this.motherID = mothID;
        if (fathID == null && mothID == null)
            this.isFounder = true;
        if (sx.equals("1"))
            this.sex = Sex.MALE;
        else if (sx.equals("2"))
            this.sex=Sex.FEMALE;
        else {
            String s = String.format("Malformed sex identifier (%s) in pedfile",sx);
            throw new PedParseException(s);
        }
        if (dse.equals("2"))
            this.disease = Disease.AFFECTED;
        else if (dse.equals("1"))
            this.disease = Disease.UNAFFECTED;
        else if (dse.equals("0"))
            this.disease = Disease.UNKNOWN;
        else {
            String s = String.format("Malformed disease identifier (%s) in pedfile",dse);
            throw new PedParseException(s);
        }
    }

    public String getFamilyID() { return this.familyID; }
    
    public String getIndividualID() { return this.individualID; }
    
    public String getFatherID() { return this.fatherID; }
    
    public void setFather(Person f){ this.father = f; }

    public Person getFather() { return this.father; }

    public Person getMother() { return this.mother; }
    
    public void setMother(Person m) { this.mother = m;}

    public void setIndex(int i) { this.index = i; }

    public int getIndex() { return this.index; }
    
    public String getMotherID() { return this.motherID; }
    
    public Disease getDiseaseStatus() { return this.disease; }

    public boolean isAffected() { return this.disease == Disease.AFFECTED; }

    public boolean isUnaffected() { return this.disease == Disease.UNAFFECTED; }

    public boolean isMale() { return this.sex == Sex.MALE; }

    public boolean isFemale() { return this.sex == Sex.FEMALE; }

    /**
     * This function returns a list with six fields representing the six entries
     * about this Person in the PED file. This is intended to be used by
     * client code to create HTML tables or to report the data in tabular form.
     * @return list of representative entries
     */
    public ArrayList<String> getPEDFileData() {
	ArrayList<String> lst = new ArrayList<String>();
	lst.add( getFamilyID() );
	lst.add( getIndividualID() );
	String f = getFatherID();
	if (f==null)
	    lst.add("-");
	else
	    lst.add( getFatherID() );
	String m = getMotherID();
	if (m == null)
	    lst.add("-");
	else
	    lst.add( getMotherID() );
	if (isMale()) lst.add("Male");
	else if (isFemale()) lst.add("Female");
	else lst.add("Gender unknown");
	if (isAffected())
	    lst.add("Affected");
	else if ( isUnaffected())
	    lst.add("Unaffected");
	else
	    lst.add("Disease status unknown");
	return lst;
    }

    
    @Override
    public String toString() {
        String s = String.format("%s\t%s\t%s\t%s\t%s\t%s\t",
                                 familyID,individualID,fatherID,motherID,sex,disease);
        return s;
    }
}