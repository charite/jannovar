package de.charite.compbio.jannovar.vardbs.generic_tsv;

import de.charite.compbio.jannovar.vardbs.base.DBAnnotationOptions;
import htsjdk.variant.vcf.VCFHeaderLineType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Configuration for generic TSV annotation.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericTSVAnnotationOptions extends DBAnnotationOptions {

	/** File with TSV annotations. */
	private File tsvFile;

	/** Configuration of annotation target. */
	private AnnotationTarget annotationTarget = AnnotationTarget.VARIANT;

	/** Whether or not coordinates are 1-based. */
	private boolean oneBasedPositions = true;

	/** 1-based index of column with contig name */
	private int contigColumnIndex = 1;

	/** 1-based index of column with begin position. */
	private int beginColumnIndex = 2;

	/** 1-based index of column with end position. */
	private int endColumnIndex = 3;

	/** 1-based index of column with reference allele, 0 for none. */
	private int refAlleleColumnIndex = 4;

	/** 1-based index of column with variant allele, 0 for none. */
	private int altAlleleColumnIndex = 5;

	/** Description of value columns. */
	List<ValueColumnDescription> valueColumnDescriptions = new ArrayList<>();

	public GenericTSVAnnotationOptions(boolean reportOverlapping,
			boolean reportOverlappingAsIdentical, String identifierPrefix,
			MultipleMatchBehaviour multiMatchBehaviour, File tsvFile,
			AnnotationTarget annotationTarget, boolean oneBasedPositions, int contigColumnIndex,
			int beginColumnIndex, int endColumnIndex, int refAlleleColumnIndex,
			int altAlleleColumnIndex, List<ValueColumnDescription> valueColumnDescriptions) {
		super(reportOverlapping, reportOverlappingAsIdentical, identifierPrefix,
				multiMatchBehaviour);

		this.tsvFile = tsvFile;
		this.annotationTarget = annotationTarget;
		this.oneBasedPositions = oneBasedPositions;
		this.contigColumnIndex = contigColumnIndex;
		this.beginColumnIndex = beginColumnIndex;
		this.endColumnIndex = endColumnIndex;
		this.refAlleleColumnIndex = refAlleleColumnIndex;
		this.altAlleleColumnIndex = altAlleleColumnIndex;
		this.valueColumnDescriptions = valueColumnDescriptions;
	}

	public File getTsvFile() {
		return tsvFile;
	}

	public void setTsvFile(File tsvFile) {
		this.tsvFile = tsvFile;
	}

	public AnnotationTarget getAnnotationTarget() {
		return annotationTarget;
	}

	public void setAnnotationTarget(AnnotationTarget annotationTarget) {
		this.annotationTarget = annotationTarget;
	}

	public boolean isOneBasedPositions() {
		return oneBasedPositions;
	}

	public void setOneBasedPositions(boolean oneBasedPositions) {
		this.oneBasedPositions = oneBasedPositions;
	}

	public int getContigColumnIndex() {
		return contigColumnIndex;
	}

	public void setContigColumnIndex(int contigColumnIndex) {
		this.contigColumnIndex = contigColumnIndex;
	}

	public int getBeginColumnIndex() {
		return beginColumnIndex;
	}

	public void setBeginColumnIndex(int beginColumnIndex) {
		this.beginColumnIndex = beginColumnIndex;
	}

	public int getEndColumnIndex() {
		return endColumnIndex;
	}

	public void setEndColumnIndex(int endColumnIndex) {
		this.endColumnIndex = endColumnIndex;
	}

	public int getRefAlleleColumnIndex() {
		return refAlleleColumnIndex;
	}

	public void setRefAlleleColumnIndex(int refAlleleColumnIndex) {
		this.refAlleleColumnIndex = refAlleleColumnIndex;
	}

	public int getAltAlleleColumnIndex() {
		return altAlleleColumnIndex;
	}

	public void setAltAlleleColumnIndex(int altAlleleColumnIndex) {
		this.altAlleleColumnIndex = altAlleleColumnIndex;
	}

	public List<ValueColumnDescription> getValueColumnDescriptions() {
		return valueColumnDescriptions;
	}

	public void setValueColumnDescriptions(List<ValueColumnDescription> valueColumnDescriptions) {
		this.valueColumnDescriptions = valueColumnDescriptions;
	}

	@Override
	public String toString() {
		return "GenericTSVAnnotationOptions [tsvFile=" + tsvFile + ", annotationTarget="
				+ annotationTarget + ", oneBasedPositions=" + oneBasedPositions
				+ ", contigColumnIndex=" + contigColumnIndex + ", beginColumnIndex="
				+ beginColumnIndex + ", endColumnIndex=" + endColumnIndex
				+ ", refAlleleColumnIndex=" + refAlleleColumnIndex + ", altAlleleColumnIndex="
				+ altAlleleColumnIndex + ", valueColumnDescriptions=" + valueColumnDescriptions
				+ ", super.toString()=" + super.toString() + "]";
	}

	/**
	 * Description of a value column.
	 *
	 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
	 */
	public static class ValueColumnDescription {

		private int columnIndex = 6;

		private VCFHeaderLineType valueType = VCFHeaderLineType.String;

		private String fieldName = "VALUE_";

		private String fieldDescription = "";

		private AccumulationStrategy accumulationStrategy = AccumulationStrategy.CHOOSE_FIRST;

		public ValueColumnDescription() {
		}

		public ValueColumnDescription(int columnIndex, VCFHeaderLineType valueType,
				String fieldName, String fieldDescription,
				AccumulationStrategy accumulationStrategy) {
			this.columnIndex = columnIndex;
			this.valueType = valueType;
			this.fieldName = fieldName;
			this.fieldDescription = fieldDescription;
			this.accumulationStrategy = accumulationStrategy;
		}

		public int getColumnIndex() {
			return columnIndex;
		}

		public void setColumnIndex(int columnIndex) {
			this.columnIndex = columnIndex;
		}

		public VCFHeaderLineType getValueType() {
			return valueType;
		}

		public void setValueType(VCFHeaderLineType valueType) {
			this.valueType = valueType;
		}

		public String getFieldName() {
			return fieldName;
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		public String getFieldDescription() {
			return fieldDescription;
		}

		public void setFieldDescription(String fieldDescription) {
			this.fieldDescription = fieldDescription;
		}

		public AccumulationStrategy getAccumulationStrategy() {
			return accumulationStrategy;
		}

		public void setAccumulationStrategy(AccumulationStrategy accumulationStrategy) {
			this.accumulationStrategy = accumulationStrategy;
		}

		@Override
		public String toString() {
			return "ValueColumnDescription [columnIndex=" + columnIndex + ", valueType=" + valueType
					+ ", fieldName=" + fieldName + ", fieldDescription=" + fieldDescription
					+ ", accumulationStrategy=" + accumulationStrategy + "]";
		}

	}

	/**
	 * Enumeration describing accumulation strategy for annotation with TSV for multiple matches.
	 * 
	 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
	 */
	public static enum AccumulationStrategy {
		/** Choose first */
		CHOOSE_FIRST,
		/** Use average, only applicable to numbers, fall back to first. */
		AVERAGE,
		/** Use largest value, only applicable to numbers, fall back to first. */
		CHOOSE_MAX;
	}

	/**
	 * Enumeration for describing annotation target (either position only or variant, e.g.
	 * <code>C&gt;T</code>.
	 *
	 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
	 */
	public static enum AnnotationTarget {
		/** TSV file annotates a position. */
		POSITION,
		/** TSV file annotates a variant allele. */
		VARIANT;
	};

}
