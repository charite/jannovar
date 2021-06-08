package de.charite.compbio.jannovar.data;

import de.charite.compbio.jannovar.Immutable;

import java.io.Serializable;
import java.util.Objects;

/**
 * 	Value type containing the contig id, name and length, as indexed in chromosome {@link ReferenceDictionary}.
 *
 * @author Jules Jacobsen <j.jacobsen@qmul.ac.uk>
 */
@Immutable
public class Contig implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int id;
	private final String name;
	private final int length;

	public Contig(int id, String name, int length) {
		this.id = id;
		this.name = Objects.requireNonNull(name, "contig must have a name");
		this.length = length;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getLength() {
		return length;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Contig)) return false;
		Contig contig = (Contig) o;
		return id == contig.id &&
			length == contig.length &&
			name.equals(contig.name);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, length);
	}

	@Override
	public String toString() {
		return "Contig{" +
			"id=" + id +
			", name='" + name + '\'' +
			", length=" + length +
			'}';
	}
}
