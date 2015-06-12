package de.charite.compbio.jannovar.hgvs.parser;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideDeletion;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideDuplication;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideInsertion;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideInversion;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideMiscChange;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideShortSequenceRepeatVariability;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Hgvs_variantContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_base_locationContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_changeContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_change_deletionContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_change_duplicationContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_change_insertionContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_change_inversionContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_change_miscContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_change_ssrContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_change_substitutionContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_offsetContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_point_locationContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_rangeContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_single_allele_single_change_varContext;
import de.charite.compbio.jannovar.hgvs.parser.HGVSParser.Nt_single_allele_varContext;

/**
 * Master ParseTreeListener used in {@link HVSParser}
 *
 * @author Manuel Holtgrewe <manuel.holtgrewe@bihealth.de>
 */
class HGVSParserListenerImpl extends HGVSParserBaseListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(HGVSParserListenerImpl.class);

	/** maps nodes to integers with Map<ParseTree,Object> */
	ParseTreeProperty<Object> values = new ParseTreeProperty<Object>();

	public void setValue(ParseTree node, Object value) {
		// System.err.println("SetValue(" + node + ", " + value + ")");
		values.put(node, value);
	}

	public Object getValue(ParseTree node) {
		return values.get(node);
	}

	/** resulting {@link HGVSVariant} */
	HGVSVariant result = null;

	/** @return resulting {@link HGVSVariant} */
	public HGVSVariant getResult() {
		return result;
	}

	/**
	 * Leaving of the top-level hgvs_variant rule.
	 *
	 * The HGVSVariant for the result is stored in the map for the only child.
	 */
	@Override
	public void exitHgvs_variant(Hgvs_variantContext ctx) {
		LOGGER.debug("Leaving hgvs_variant");
		result = (HGVSVariant) getValue(ctx.getChild(0));
	}

	/**
	 * Leaving of the nt_single_allele_var rule.
	 *
	 * The result from the child is propagated to the label of this node.
	 */
	@Override
	public void exitNt_single_allele_var(Nt_single_allele_varContext ctx) {
		LOGGER.debug("Leaving nt_single_allele_var");
		setValue(ctx, getValue(ctx.getChild(0)));
	}

	/**
	 * Leaving of the nt_single_allele_single_change_var rule.
	 *
	 * Collect sequence type, sequence ID, and NucleotideChange from the children and label ctx with the resulting
	 * {@link SingleAlleleNucleotideVariant}.
	 */
	@Override
	public void exitNt_single_allele_single_change_var(Nt_single_allele_single_change_varContext ctx) {
		LOGGER.debug("Leaving nt_single_allele_single_change_var");
		final SequenceType seqType = SequenceType.findMatchingForPrefix(ctx.NT_CHANGE_DESCRIPTION().getText());
		final String seqID = ctx.reference().getText().substring(0, ctx.reference().getText().length() - 1);
		final NucleotideChange ntChange = (NucleotideChange) getValue(ctx.nt_change());
		setValue(ctx, SingleAlleleNucleotideVariant.makeSingleChangeVariant(seqType, seqID, ntChange));
	}

	/**
	 * Leaving of nt_change rule.
	 *
	 * The result from the child is propagated to the label of this node.
	 */
	@Override
	public void exitNt_change(Nt_changeContext ctx) {
		LOGGER.debug("Leaving nt_change");
		setValue(ctx, getValue(ctx.getChild(0)));
	}

	/**
	 * Leaving of nt_change_deletion rule
	 *
	 * Construct {@link NucleotideDeletion} from children's values and labels and label ctx with this.
	 */
	@Override
	public void exitNt_change_deletion(Nt_change_deletionContext ctx) {
		LOGGER.debug("Leaving nt_change_deletion");
		final NucleotideRange range = (NucleotideRange) getValue(ctx.nt_range());
		final NucleotideDeletion change;
		if (ctx.nt_number() != null)
			change = new NucleotideDeletion(false, range, new NucleotideSeqDescription(Integer.parseInt(ctx.nt_number()
					.getText())));
		else if (ctx.nt_string() != null)
			change = new NucleotideDeletion(false, range, new NucleotideSeqDescription(ctx.nt_string().getText()));
		else
			change = new NucleotideDeletion(false, range, new NucleotideSeqDescription());
		setValue(ctx, change);
	}

	/**
	 * Leaving of nt_change_duplication rule
	 *
	 * Construct {@link NucleotideDuplication} from children's values and labels and label ctx with this.
	 */
	@Override
	public void exitNt_change_duplication(Nt_change_duplicationContext ctx) {
		LOGGER.debug("Leaving nt_change_duplication");
		final NucleotideRange range = (NucleotideRange) getValue(ctx.nt_range());
		final NucleotideDuplication change;
		if (ctx.nt_number() != null)
			change = new NucleotideDuplication(false, range, new NucleotideSeqDescription(Integer.parseInt(ctx
					.nt_number().getText())));
		else if (ctx.nt_string() != null)
			change = new NucleotideDuplication(false, range, new NucleotideSeqDescription(ctx.nt_string().getText()));
		else
			change = new NucleotideDuplication(false, range, new NucleotideSeqDescription());
		setValue(ctx, change);
	}

	/**
	 * Leaving of nt_change_insertion rule
	 *
	 * Construct {@link NucleotideInsertion} from children's values and labels and label ctx with this.
	 */
	@Override
	public void exitNt_change_insertion(Nt_change_insertionContext ctx) {
		LOGGER.debug("Leaving nt_change_insertion");
		final NucleotideRange range = (NucleotideRange) getValue(ctx.nt_range());
		final NucleotideInsertion change;
		if (ctx.nt_number() != null)
			change = new NucleotideInsertion(false, range, new NucleotideSeqDescription(Integer.parseInt(ctx
					.nt_number().getText())));
		else if (ctx.nt_string() != null)
			change = new NucleotideInsertion(false, range, new NucleotideSeqDescription(ctx.nt_string().getText()));
		else
			change = new NucleotideInsertion(false, range, new NucleotideSeqDescription());
		setValue(ctx, change);
	}

	/**
	 * Leaving of nt_change_inversion rule
	 *
	 * Construct {@link NucleotideInversion} from children's values and labels and label ctx with this.
	 */
	@Override
	public void exitNt_change_inversion(Nt_change_inversionContext ctx) {
		LOGGER.debug("Leaving nt_change_inversion");
		final NucleotideRange range = (NucleotideRange) getValue(ctx.nt_range());
		final NucleotideInversion change;
		if (ctx.nt_number() != null)
			change = new NucleotideInversion(false, range, new NucleotideSeqDescription(Integer.parseInt(ctx
					.nt_number().getText())));
		else if (ctx.nt_string() != null)
			change = new NucleotideInversion(false, range, new NucleotideSeqDescription(ctx.nt_string().getText()));
		else
			change = new NucleotideInversion(false, range, new NucleotideSeqDescription());
		setValue(ctx, change);
	}

	/**
	 * Leaving of nt_change_substitution rule
	 *
	 * Construct {@link NucleotideSubstitution} from children's values and labels and label ctx with this.
	 */
	@Override
	public void exitNt_change_substitution(Nt_change_substitutionContext ctx) {
		LOGGER.debug("Leaving nt_change_substitution");
		// TODO(holtgrew): Needs change once nucleotide changes can be marked as predicted, see future.
		NucleotidePointLocation position = (NucleotidePointLocation) getValue(ctx.nt_point_location());
		setValue(ctx, new NucleotideSubstitution(false, position, ctx.NT_STRING(0).getText(), ctx.NT_STRING(1)
				.getText()));
	}

	/**
	 * Leaving of nt_change_ssr rule
	 * 
	 * Construct {@link NucleotideShortSequenceRepeatVariability} from the children's values and label ctx with this.
	 */
	@Override
	public void exitNt_change_ssr(Nt_change_ssrContext ctx) {
		LOGGER.debug("Leaving nt_change_ssr");
		final NucleotideRange range;
		if (ctx.nt_range() != null)
			range = (NucleotideRange) getValue(ctx.nt_range());
		else
			range = new NucleotideRange((NucleotidePointLocation) getValue(ctx.nt_point_location()),
					(NucleotidePointLocation) getValue(ctx.nt_point_location()));
		final int minCount = Integer.parseInt(ctx.NT_NUMBER(0).getText());
		final int maxCount = Integer.parseInt(ctx.NT_NUMBER(1).getText());
		setValue(ctx, new NucleotideShortSequenceRepeatVariability(false, range, minCount, maxCount));
	}

	/**
	 * Leaving of nt_change_misc rule
	 * 
	 * Construct {@link NucleotideMiscChange} from the children's values and label ctx with this.
	 */
	@Override
	public void exitNt_change_misc(Nt_change_miscContext ctx) {
		LOGGER.debug("Leaving nt_change_misc");
		setValue(ctx, NucleotideMiscChange.buildFromString(ctx.getText()));
	}

	/**
	 * Leaving of nt_range rule
	 */
	@Override
	public void exitNt_range(Nt_rangeContext ctx) {
		LOGGER.debug("Leaving nt_range");
		NucleotidePointLocation startPos = (NucleotidePointLocation) getValue(ctx.nt_point_location(0));
		NucleotidePointLocation stopPos = (NucleotidePointLocation) getValue(ctx.nt_point_location(1));
		setValue(ctx, new NucleotideRange(startPos, stopPos));
	}

	/**
	 * Leaving of nt_point_location rule
	 *
	 * Construction {@link NucleotidePointLocation} from the children's values and label ctx with this.
	 */
	@Override
	public void exitNt_point_location(Nt_point_locationContext ctx) {
		LOGGER.debug("Leaving nt_point_location");
		if (ctx.nt_offset() == null) {
			setValue(ctx, getValue(ctx.nt_base_location()));
		} else {
			NucleotidePointLocation baseLoc = (NucleotidePointLocation) getValue(ctx.nt_base_location());
			Integer offset = (Integer) getValue(ctx.nt_offset());
			setValue(ctx, new NucleotidePointLocation(baseLoc.getBasePos(), offset, baseLoc.isDownstreamOfCDS()));
		}
	}

	/**
	 * Leaving rule nt_base_location
	 */
	@Override
	public void exitNt_base_location(Nt_base_locationContext ctx) {
		LOGGER.debug("Leaving nt_base_location");
		int value = Integer.parseInt(ctx.nt_number().getText());
		if (ctx.NT_MINUS() != null)
			value = -value;
		boolean downstreamOfCDS = (ctx.NT_ASTERISK() != null);
		int delta = (value < 0) ? 0 : 1;
		setValue(ctx, new NucleotidePointLocation(value - delta, 0, downstreamOfCDS));
	}

	/**
	 * Leaving rule nt_offset, store integer as label of ctx
	 */
	@Override
	public void exitNt_offset(Nt_offsetContext ctx) {
		LOGGER.debug("Leaving nt_offset");
		int value = Integer.parseInt(ctx.nt_number().getText());
		if (ctx.NT_MINUS() != null)
			value = -value;
		setValue(ctx, value);
	}

}
