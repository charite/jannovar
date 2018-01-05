package de.charite.compbio.jannovar.hgvs.bridge;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

/**
 * Helper class that allows annotating a result type with warning messages.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
class ResultWithWarnings<V> {

	/** wrapped value */
	private final V value;
	/** list of warning messages */
	private final ImmutableList<String> warnings;

	public static <V> ResultWithWarnings<V> construct(V value) {
		return new ResultWithWarnings<V>(value, ImmutableList.<String> of());
	}

	public static <V> ResultWithWarnings<V> construct(V value, String... warnings) {
		return new ResultWithWarnings<V>(value, ImmutableList.copyOf(warnings));
	}

	public ResultWithWarnings(V value, Collection<String> warnings) {
		super();
		this.value = value;
		this.warnings = ImmutableList.copyOf(warnings);
	}

	public ImmutableList<String> getWarnings() {
		return warnings;
	}

	public V getValue() {
		return value;
	}

}
