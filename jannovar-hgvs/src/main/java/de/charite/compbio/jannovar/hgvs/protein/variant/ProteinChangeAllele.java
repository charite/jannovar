package de.charite.compbio.jannovar.hgvs.protein.variant;

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
import de.charite.compbio.jannovar.hgvs.protein.change.ProteinChange;

/**
 * Contains the {@link ProteinChange}s on one allee.
 *
 * Examples:
 *
 * <ul>
 * <li><tt>[(Gly28Val);(Asn26His)]</tt></li>
 * <li><tt>[Gly28Val;Asn26His]</tt></li>
 * <li><tt>[Gly28Val;(Asn26His)]</tt></li>
 * </ul>
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public final class ProteinChangeAllele implements ConvertibleToHGVSString, List<ProteinChange> {

	/** variant configuration on this allele */
	protected final VariantConfiguration varConfig;
	/** protein changes that lie on the allele */
	protected final ImmutableList<ProteinChange> changes;

	/** @return a new allele containing one protein change */
	public static ProteinChangeAllele singleChangeAllele(ProteinChange change) {
		return new ProteinChangeAllele(VariantConfiguration.IN_CIS, ImmutableList.of(change));
	}

	/** @return a new allele with the given variant configuration and changes */
	public static ProteinChangeAllele build(VariantConfiguration varConfig, ProteinChange... changes) {
		return new ProteinChangeAllele(varConfig, ImmutableList.copyOf(changes));
	}

	/**
	 * Construct allele with the given a variant {@link VariantConfiguration} and {@link ProteinChange}s.
	 *
	 * @param varConfig
	 *            configuration of the changes
	 * @param changes
	 *            protein changes to store in the allele
	 */
	public ProteinChangeAllele(VariantConfiguration varConfig, Collection<? extends ProteinChange> changes) {
		this.varConfig = varConfig;
		this.changes = ImmutableList.copyOf(changes);
	}

	/** @return the {@link VariantConfiguration} of this allele */
	public VariantConfiguration getVarConfig() {
		return varConfig;
	}

	/** @return list of {@link ProteinChange}s */
	public ImmutableList<ProteinChange> getChanges() {
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
		for (ProteinChange change : changes) {
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
		ProteinChangeAllele other = (ProteinChangeAllele) obj;
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
	public Iterator<ProteinChange> iterator() {
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
	public boolean add(ProteinChange e) {
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
	public boolean addAll(Collection<? extends ProteinChange> c) {
		return changes.addAll(c);
	}

	@Deprecated
	@Override
	public boolean addAll(int index, Collection<? extends ProteinChange> c) {
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
	public ProteinChange get(int index) {
		return changes.get(index);
	}

	@Deprecated
	@Override
	public ProteinChange set(int index, ProteinChange element) {
		return changes.set(index, element);
	}

	@Deprecated
	@Override
	public void add(int index, ProteinChange element) {
		changes.add(index, element);
	}

	@Deprecated
	@Override
	public ProteinChange remove(int index) {
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
	public ListIterator<ProteinChange> listIterator() {
		return changes.listIterator();
	}

	@Override
	public ListIterator<ProteinChange> listIterator(int index) {
		return changes.listIterator(index);
	}

	@Override
	public List<ProteinChange> subList(int fromIndex, int toIndex) {
		return changes.subList(fromIndex, toIndex);
	}

}
