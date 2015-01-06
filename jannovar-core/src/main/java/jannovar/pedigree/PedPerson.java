package jannovar.pedigree;

import jannovar.Immutable;

import java.util.ArrayList;
import java.util.Collection;

import com.google.common.collect.ImmutableList;

/**
 * Representation of a line from a pedigree (<code>.ped</code>) file.
 *
 * A pedigree file is a TSV file and contains information about multiple indivdidual. Each line is a record and
 * describes an individual. The files have six <b>core columns</b> that have to be present:
 *
 * <dl>
 * <dt>{@link #pedigree}</dt>
 * <dd>the name of the pedigree</dd>
 * <dt>{@link #name}</dt>
 * <dd>the name of the individual</dd>
 * <dt>{@link #father}</dt>
 * <dd>the name of the individual's father</dd>
 * <dt>{@link #mother}</dt>
 * <dd>the name of the individual's mother</dd>
 * <dt>{@link #sex}</dt>
 * <dd>the sex of the individual</dd>
 * <dt>{@link #disease}</dt>
 * <dd>the disease status of the individual</dd>
 * </dl>
 *
 * The remaining columns are stored in {@link #extraFields}.
 *
 * The <code>PedPerson</code> class describes a record from a pedigree file. This introduces some limitations. For
 * example, the record stores the id of the parents but not references to the parent objects. The class
 * {@link LegacyPerson} provides a more Java-programmer oriented version of this information.
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@charite.de>
 */
@Immutable
public final class PedPerson {

	/** pedigree name */
	public final String pedigree;

	/** name of the individual */
	public final String name;

	/** individual's father's name, <code>"0"</code> if father not in pedigree */
	public final String father;

	/** individual's mother's name, <code>"0"</code> if father not in pedigree */
	public final String mother;

	/** invidual's sex */
	public final Sex sex;

	/** individual's disease status */
	public final Disease disease;

	/** the additional fields stored for this person */
	public final ImmutableList<String> extraFields;

	/**
	 * Initialize object with the given data.
	 */
	public PedPerson(String pedigree, String name, String father, String mother, Sex sex, Disease disease,
			Collection<String> extraFields) {
		this.pedigree = pedigree;
		this.name = name;
		this.father = father;
		this.mother = mother;
		this.sex = sex;
		this.disease = disease;
		this.extraFields = ImmutableList.copyOf(extraFields);
	}

	/**
	 * Initialize object with the given data.
	 */
	public PedPerson(String pedigree, String name, String father, String mother, Sex sex, Disease disease) {
		this(pedigree, name, father, mother, sex, disease, new ArrayList<String>());
	}

	/**
	 * @return <code>true</code> if the person is a founder (both mother and father are <code>"0"</code>)
	 */
	public boolean isFounder() {
		return (father.equals("1") && mother.equals("0"));
	}

}
