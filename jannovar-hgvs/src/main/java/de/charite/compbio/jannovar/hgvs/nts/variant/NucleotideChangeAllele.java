package de.charite.compbio.jannovar.hgvs.nts.variant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;

import de.charite.compbio.jannovar.hgvs.AminoAcidCode;
import de.charite.compbio.jannovar.hgvs.ConvertibleToHGVSString;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;

/**
 * Contains the {@link NucleotideChange}s on one allee.
 *
 * Examples:
 *
 * <ul>
 * <li><tt>[(33A&gt;C);(33A&gt;C)]</tt></li>
 * <li><tt>[33A&gt;C;33A&gt;C]</tt></li>
 * <li><tt>[33A&gt;C;(33A&gt;C)]</tt></li>
 * </ul>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class NucleotideChangeAllele implements ConvertibleToHGVSString, List<NucleotideChange> {

	/** variant configuration on this allele */
	protected final VariantConfiguration varConfig;
	/** nucleotide changes that lie on the allele */
	protected final ImmutableList<NucleotideChange> changes;

	/** @return a new allele containing one nucleotide change */
	public static NucleotideChangeAllele singleChangeAllele(NucleotideChange change) {
		if (change == null)  // parsing failed below
			return new NucleotideChangeAllele(VariantConfiguration.IN_CIS, ImmutableList.<NucleotideChange> of());
		else
			return new NucleotideChangeAllele(VariantConfiguration.IN_CIS, ImmutableList.of(change));
	}

	/** @return a new allele with the given variant configuration and changes */
	public static NucleotideChangeAllele build(VariantConfiguration varConfig, NucleotideChange... changes) {
		return new NucleotideChangeAllele(varConfig, ImmutableList.copyOf(changes));
	}

	/**
	 * Construct allele with the given a variant {@link VariantConfiguration} and {@link NucleotideChange}s.
	 *
	 * @param varConfig
	 *            configuration of the changes
	 * @param changes
	 *            nucleotide changes to store in the allele
	 */
	public NucleotideChangeAllele(VariantConfiguration varConfig, Collection<? extends NucleotideChange> changes) {
		this.varConfig = varConfig;
		this.changes = ImmutableList.copyOf(changes);
	}

	/** @return Allele with the onlyPredicted state of all contained changes set to the given value */
	public NucleotideChangeAllele withOnlyPredicted(boolean flag) {
		ArrayList<NucleotideChange> changesCopy = new ArrayList<>();
		for (NucleotideChange change : changes)
			changesCopy.add(change.withOnlyPredicted(flag));
		return new NucleotideChangeAllele(varConfig, changesCopy);
	}

	/** @return the {@link VariantConfiguration} of this allele */
	public VariantConfiguration getVarConfig() {
		return varConfig;
	}

	/** @return list of {@link NucleotideChange}s */
	public ImmutableList<NucleotideChange> getChanges() {
		return changes;
	}

	@Override
	public String toHGVSString() {
		return toHGVSString(AminoAcidCode.THREE_LETTER);
	}

	@Override
	public String toHGVSString(AminoAcidCode code) {
		ArrayList<String> parts = new ArrayList<>();
		parts.add("[");
		boolean first = true;
		for (NucleotideChange change : changes) {
			if (first)
				first = false;
			else
				parts.add(varConfig.toHGVSSeparator());
			parts.add(change.toHGVSString(code));
		}
		parts.add("]");

		return Joiner.on("").join(parts);
	}

	@Override
	public String toString() {
		return "AlleleVariants [varConfig=" + varConfig + ", changes=" + changes + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((changes == null) ? 0 : changes.hashCode());
		result = prime * result + ((varConfig == null) ? 0 : varConfig.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NucleotideChangeAllele other = (NucleotideChangeAllele) obj;
		if (changes == null) {
			if (other.changes != null)
				return false;
		} else if (!changes.equals(other.changes))
			return false;
		if (varConfig != other.varConfig)
			return false;
		return true;
	}

	@Override
	public int size() {
		return changes.size();
	}

	@Override
	public boolean isEmpty() {
		return changes.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return changes.contains(o);
	}

	@Override
	public Iterator<NucleotideChange> iterator() {
		return changes.iterator();
	}

	@Override
	public Object[] toArray() {
		return changes.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return changes.toArray(a);
	}

	@Deprecated
	@Override
	public boolean add(NucleotideChange e) {
		return changes.add(e);
	}

	@Deprecated
	@Override
	public boolean remove(Object o) {
		return changes.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return changes.containsAll(c);
	}

	@Deprecated
	@Override
	public boolean addAll(Collection<? extends NucleotideChange> c) {
		return changes.addAll(c);
	}

	@Deprecated
	@Override
	public boolean addAll(int index, Collection<? extends NucleotideChange> c) {
		return changes.addAll(index, c);
	}

	@Deprecated
	@Override
	public boolean removeAll(Collection<?> c) {
		return changes.removeAll(c);
	}

	@Deprecated
	@Override
	public boolean retainAll(Collection<?> c) {
		return changes.retainAll(c);
	}

	@Deprecated
	@Override
	public void clear() {
		changes.clear();
	}

	@Override
	public NucleotideChange get(int index) {
		return changes.get(index);
	}

	@Deprecated
	@Override
	public NucleotideChange set(int index, NucleotideChange element) {
		return changes.set(index, element);
	}

	@Deprecated
	@Override
	public void add(int index, NucleotideChange element) {
		changes.add(index, element);
	}

	@Deprecated
	@Override
	public NucleotideChange remove(int index) {
		return changes.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return changes.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return changes.lastIndexOf(o);
	}

	@Override
	public ListIterator<NucleotideChange> listIterator() {
		return changes.listIterator();
	}

	@Override
	public ListIterator<NucleotideChange> listIterator(int index) {
		return changes.listIterator(index);
	}

	@Override
	public List<NucleotideChange> subList(int fromIndex, int toIndex) {
		return changes.subList(fromIndex, toIndex);
	}

}
