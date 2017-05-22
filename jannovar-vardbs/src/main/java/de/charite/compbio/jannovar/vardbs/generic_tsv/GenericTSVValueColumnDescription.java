package de.charite.compbio.jannovar.vardbs.generic_tsv;

import htsjdk.variant.vcf.VCFHeaderLineType;

/**
 * Description of a value column.
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
public class GenericTSVValueColumnDescription {

	private int columnIndex = 6;

	private VCFHeaderLineType valueType = VCFHeaderLineType.String;

	private String fieldName = "VALUE_";

	private String fieldDescription = "";

	private GenericTSVAccumulationStrategy accumulationStrategy = GenericTSVAccumulationStrategy.CHOOSE_FIRST;

	// Name of field to refer to for min/max
	private String refField;

	public GenericTSVValueColumnDescription() {
	}

	public GenericTSVValueColumnDescription(int columnIndex, VCFHeaderLineType valueType,
			String fieldName, String fieldDescription,
			GenericTSVAccumulationStrategy accumulationStrategy) {
		this(columnIndex, valueType, fieldName, fieldDescription, accumulationStrategy, fieldName);
	}

	public GenericTSVValueColumnDescription(int columnIndex, VCFHeaderLineType valueType,
			String fieldName, String fieldDescription,
			GenericTSVAccumulationStrategy accumulationStrategy, String refField) {
		this.columnIndex = columnIndex;
		this.valueType = valueType;
		this.fieldName = fieldName;
		this.fieldDescription = fieldDescription;
		this.accumulationStrategy = accumulationStrategy;
		this.refField = refField;
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

	public GenericTSVAccumulationStrategy getAccumulationStrategy() {
		return accumulationStrategy;
	}

	public void setAccumulationStrategy(GenericTSVAccumulationStrategy accumulationStrategy) {
		this.accumulationStrategy = accumulationStrategy;
	}

	public String getRefField() {
		return refField;
	}

	public void setRefField(String refField) {
		this.refField = refField;
	}

	@Override
	public String toString() {
		return "GenericTSVValueColumnDescription [columnIndex=" + columnIndex + ", valueType="
				+ valueType + ", fieldName=" + fieldName + ", fieldDescription=" + fieldDescription
				+ ", accumulationStrategy=" + accumulationStrategy + ", refField=" + refField + "]";
	}

}