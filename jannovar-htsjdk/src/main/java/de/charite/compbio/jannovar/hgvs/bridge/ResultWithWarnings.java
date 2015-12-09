package de.charite.compbio.jannovar.hgvs.bridge;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

/**
 * Helper class that allows annotating a result type with warning messages.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
class ResultWithWarnings<Value> {

	/** wrapped value */
	private final Value value;
	/** list of warning messages */
	private final ImmutableList<String> warnings;

	public static <Value> ResultWithWarnings<Value> construct(Value value) {
		return new ResultWithWarnings<Value>(value, ImmutableList.<String> of());
	}

	public static <Value> ResultWithWarnings<Value> construct(Value value, String... warnings) {
		return new ResultWithWarnings<Value>(value, ImmutableList.copyOf(warnings));
	}

	public ResultWithWarnings(Value value, Collection<String> warnings) {
		super();
		this.value = value;
		this.warnings = ImmutableList.copyOf(warnings);
	}

	public ImmutableList<String> getWarnings() {
		return warnings;
	}

	public Value getValue() {
		return value;
	}

}
