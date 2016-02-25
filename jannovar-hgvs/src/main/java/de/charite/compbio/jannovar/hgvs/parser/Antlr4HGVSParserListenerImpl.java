package de.charite.compbio.jannovar.hgvs.parser;

import java.util.ArrayList;

import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.charite.compbio.jannovar.hgvs.HGVSVariant;
import de.charite.compbio.jannovar.hgvs.SequenceType;
import de.charite.compbio.jannovar.hgvs.VariantConfiguration;
import de.charite.compbio.jannovar.hgvs.legacy.LegacyChange;
import de.charite.compbio.jannovar.hgvs.legacy.LegacyDeletion;
import de.charite.compbio.jannovar.hgvs.legacy.LegacyIndel;
import de.charite.compbio.jannovar.hgvs.legacy.LegacyInsertion;
import de.charite.compbio.jannovar.hgvs.legacy.LegacyLocation;
import de.charite.compbio.jannovar.hgvs.legacy.LegacySubstitution;
import de.charite.compbio.jannovar.hgvs.legacy.LegacyVariant;
import de.charite.compbio.jannovar.hgvs.nts.NucleotidePointLocation;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideRange;
import de.charite.compbio.jannovar.hgvs.nts.NucleotideSeqDescription;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideChange;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideDeletion;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideDuplication;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideIndel;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideInsertion;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideInversion;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideMiscChange;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideShortSequenceRepeatVariability;
import de.charite.compbio.jannovar.hgvs.nts.change.NucleotideSubstitution;
import de.charite.compbio.jannovar.hgvs.nts.variant.MultiAlleleNucleotideVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.NucleotideChangeAllele;
import de.charite.compbio.jannovar.hgvs.nts.variant.NucleotideVariant;
import de.charite.compbio.jannovar.hgvs.nts.variant.SingleAlleleNucleotideVariant;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Hgvs_variantContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Legacy_changeContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Legacy_change_deletionContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Legacy_change_indelContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Legacy_change_insertionContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Legacy_change_substitutionContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Legacy_point_locationContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Legacy_variantContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_base_locationContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_changeContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_deletionContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_duplicationContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_indelContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_innerContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_insertionContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_inversionContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_miscContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_ssrContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_change_substitutionContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_multi_allele_varContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_multi_change_alleleContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_multi_change_allele_innerContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_offsetContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_point_locationContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_rangeContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_single_allele_multi_change_varContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_single_allele_single_change_varContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_single_allele_varContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.Nt_var_sepContext;
import de.charite.compbio.jannovar.hgvs.parser.Antlr4HGVSParser.ReferenceContext;

// TODO(holtgrewe): support parsing amino acid changes

/**
 * Master ParseTreeListener used in {@link HVSParser} setB
 *
 * @author <a href="mailto:manuel.holtgrewe@bihealth.de">Manuel Holtgrewe</a>
 */
class Antlr4HGVSParserListenerImpl extends Antlr4HGVSParserBaseListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(Antlr4HGVSParserListenerImpl.class);

	/** maps nodes to Objects with Map<ParseTree,Object> */
	ParseTreeProperty<Object> values = new ParseTreeProperty<>();

	public void setValue(ParseTree node, Object value) {
		// System.err.println("setValue(" + node + ", " + value + ")");
		values.put(node, value);
	}

	public Object getValue(ParseTree node) {
		return values.get(node);
	}

	/** resulting {@link HGVSVariant} */
	HGVSVariant hgvsVariant = null;

	/** @return resulting {@link HGVSVariant} */
	public HGVSVariant getHGVSVariant() {
		return hgvsVariant;
	}

	/** resulting {@link LegacyVariant} */
	LegacyVariant legacyVariant = null;

	/** @return resulting {@link LegacyVariant} */
	public LegacyVariant getLegacyVariant() {
		return legacyVariant;
	}

	/**
	 * Leaving of the top-level hgvs_variant rule.
	 *
	 * The HGVSVariant for the result is taken from the child and stored in the object member.
	 */
	@Override
	public void exitHgvs_variant(Hgvs_variantContext ctx) {
		LOGGER.debug("Leaving hgvs_variant");
		hgvsVariant = (HGVSVariant) getValue(ctx.getChild(0));
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
		final ReferenceLabel refLabel = (ReferenceLabel) getValue(ctx.reference());
		final NucleotideChange ntChange = (NucleotideChange) getValue(ctx.nt_change());
		setValue(ctx, new SingleAlleleNucleotideVariant(seqType, refLabel.getTranscriptID(), refLabel.getProteinID(),
				refLabel.getTranscriptVersion(), NucleotideChangeAllele.singleChangeAllele(ntChange)));
	}

	/**
	 * Leaving of the nt_single_allele_multi_change_var rule.
	 *
	 * Construct new {@link SingleAlleleNucleotideVariant} as a label for this node, using
	 * {@link NucleotideChangeAllele} from child label.
	 */
	@Override
	public void exitNt_single_allele_multi_change_var(Nt_single_allele_multi_change_varContext ctx) {
		LOGGER.debug("Leaving nt_single_allele_multi_change_var");
		final SequenceType seqType = SequenceType.findMatchingForPrefix(ctx.NT_CHANGE_DESCRIPTION().getText());
		final ReferenceLabel refLabel = (ReferenceLabel) getValue(ctx.reference());
		final NucleotideChangeAllele allele = (NucleotideChangeAllele) getValue(ctx.nt_multi_change_allele());
		setValue(ctx, new SingleAlleleNucleotideVariant(seqType, refLabel.getTranscriptID(), refLabel.getProteinID(),
				refLabel.getTranscriptVersion(), allele));
	}

	/**
	 * Leaving of nt_multi_allele_var rule.
	 *
	 * Construct {@link MultiAlleleNucleotideVariant} and set as this node's label from children's
	 * {@link NucleotideChangeAllele} labels.
	 */
	@Override
	public void exitNt_multi_allele_var(Nt_multi_allele_varContext ctx) {
		LOGGER.debug("Leaving nt_multi_allele_var");
		ArrayList<NucleotideChangeAllele> alleles = new ArrayList<>();
		for (Nt_multi_change_alleleContext childCtx : ctx.nt_multi_change_allele())
			alleles.add((NucleotideChangeAllele) getValue(childCtx));
		final SequenceType seqType = SequenceType.findMatchingForPrefix(ctx.NT_CHANGE_DESCRIPTION().getText());
		final ReferenceLabel refLabel = (ReferenceLabel) getValue(ctx.reference());
		setValue(ctx, new MultiAlleleNucleotideVariant(seqType, refLabel.getTranscriptID(), refLabel.getProteinID(),
				refLabel.getTranscriptVersion(), alleles));
	}

	/**
	 * Leaving of nt_multi_change_allele rule.
	 *
	 * Label this node with the label of the child nt_multi_change_allele_inner.
	 */
	@Override
	public void exitNt_multi_change_allele(Nt_multi_change_alleleContext ctx) {
		LOGGER.debug("Leaving nt_multi_change_allele");
		if (ctx.NT_PAREN_OPEN() == null) {
			setValue(ctx, getValue(ctx.nt_multi_change_allele_inner()));
		} else {
			NucleotideChangeAllele allele = (NucleotideChangeAllele) getValue(ctx.nt_multi_change_allele_inner());
			setValue(ctx, allele.withOnlyPredicted(true));
		}
	}

	/**
	 * Leaving of nt_multi_change_allele_inner rule.
	 *
	 * Construct NucleotideChangeAllele from the children's labels.
	 */
	@Override
	public void exitNt_multi_change_allele_inner(Nt_multi_change_allele_innerContext ctx) {
		LOGGER.debug("Leaving nt_multi_change_allele_inner");
		VariantConfiguration varConfig = VariantConfiguration.IN_CIS;
		if (!ctx.nt_var_sep().isEmpty()) {
			Nt_var_sepContext firstSep = ctx.nt_var_sep().get(0);
			for (Nt_var_sepContext otherSep : ctx.nt_var_sep())
				if (!firstSep.getText().equals(otherSep.getText()))
					throw new RuntimeException("Mismatching variant separators in allele: " + firstSep.getText()
							+ " vs. " + otherSep.getText());
			varConfig = VariantConfiguration.fromString(firstSep.getText());
		}
		ArrayList<NucleotideChange> changes = new ArrayList<>();
		for (Nt_changeContext childCtx : ctx.nt_change())
			changes.add((NucleotideChange) getValue(childCtx));
		setValue(ctx, new NucleotideChangeAllele(varConfig, changes));
	}

	/**
	 * Leaving of nt_change rule.
	 *
	 * The result from the child is propagated to the label of this node.
	 */
	@Override
	public void exitNt_change(Nt_changeContext ctx) {
		LOGGER.debug("Leaving nt_change");
		if (ctx.NT_PAREN_OPEN() == null) {
			setValue(ctx, getValue(ctx.nt_change_inner()));
		} else {
			NucleotideChange change = (NucleotideChange) getValue(ctx.nt_change_inner());
			setValue(ctx, change.withOnlyPredicted(true));
		}
	}

	/**
	 * Leaving of nt_change_inner rule.
	 *
	 * The result from the child is propagated to the label of this node.
	 */
	@Override
	public void exitNt_change_inner(Nt_change_innerContext ctx) {
		LOGGER.debug("Leaving nt_change_inner");
		setValue(ctx, getValue(ctx.getChild(0)));
	}

	/**
	 * Leaving of nt_change_indel rule
	 *
	 * Construct {@link NucleotideIndel} from children's values and labels and label ctx with this.
	 */
	@Override
	public void exitNt_change_indel(Nt_change_indelContext ctx) {
		LOGGER.debug("Leaving nt_change_deletion");
		final NucleotideRange range;
		if (ctx.nt_range() != null)
			range = (NucleotideRange) getValue(ctx.nt_range());
		else
			range = new NucleotideRange((NucleotidePointLocation) getValue(ctx.nt_point_location()),
					(NucleotidePointLocation) getValue(ctx.nt_point_location()));

		final NucleotideSeqDescription seqDesc1;
		if (ctx.nt_number(0) != null)
			seqDesc1 = new NucleotideSeqDescription(Integer.parseInt(ctx.nt_number(0).getText()));
		else if (ctx.nt_string(0) != null)
			seqDesc1 = new NucleotideSeqDescription(ctx.nt_string(0).getText());
		else
			seqDesc1 = new NucleotideSeqDescription();

		final NucleotideSeqDescription seqDesc2;
		if (ctx.nt_number(1) != null)
			seqDesc2 = new NucleotideSeqDescription(Integer.parseInt(ctx.nt_number(1).getText()));
		else if (ctx.nt_string(1) != null)
			seqDesc2 = new NucleotideSeqDescription(ctx.nt_string(1).getText());
		else
			seqDesc2 = new NucleotideSeqDescription();

		setValue(ctx, new NucleotideIndel(false, range, seqDesc1, seqDesc2));
	}

	/**
	 * Leaving of nt_change_deletion rule
	 *
	 * Construct {@link NucleotideDeletion} from children's values and labels and label ctx with this.
	 */
	@Override
	public void exitNt_change_deletion(Nt_change_deletionContext ctx) {
		LOGGER.debug("Leaving nt_change_deletion");
		final NucleotideRange range;
		if (ctx.nt_range() != null)
			range = (NucleotideRange) getValue(ctx.nt_range());
		else
			range = new NucleotideRange((NucleotidePointLocation) getValue(ctx.nt_point_location()),
					(NucleotidePointLocation) getValue(ctx.nt_point_location()));
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
		final NucleotideRange range;
		if (ctx.nt_range() != null)
			range = (NucleotideRange) getValue(ctx.nt_range());
		else
			range = new NucleotideRange((NucleotidePointLocation) getValue(ctx.nt_point_location()),
					(NucleotidePointLocation) getValue(ctx.nt_point_location()));
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
	 * Leaving of the reference rule.
	 *
	 * Label node with {@link ReferenceLabel}
	 */
	@Override
	public void exitReference(ReferenceContext ctx) {
		LOGGER.debug("Leaving reference");
		String transcriptID;
		int transcriptVersion = NucleotideVariant.NO_TRANSCRIPT_VERSION;
		String proteinID = null;

		transcriptID = ctx.REFERENCE(0).getText();
		if (transcriptID.contains(".")) {
			int pos = transcriptID.lastIndexOf('.');
			transcriptVersion = Integer.parseInt(transcriptID.substring(pos + 1, transcriptID.length()));
			transcriptID = transcriptID.substring(0, pos);
		}
		if (ctx.PAREN_OPEN() != null)
			proteinID = ctx.REFERENCE(1).getText();

		setValue(ctx, new ReferenceLabel(transcriptID, transcriptVersion, proteinID));
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

	/**
	 * Leaving rule legacy_variant.
	 * 
	 * The LegacyVariant for the result is taken from the child and stored in the object member.
	 */
	@Override
	public void exitLegacy_variant(Legacy_variantContext ctx) {
		LOGGER.debug("Leaving legacy_variant");
		
		String ref = ctx.reference().REFERENCE(0).getText();
		LegacyChange change = (LegacyChange) getValue(ctx.getChild(1));
		this.legacyVariant = new LegacyVariant(ref, change); 
	}

	/**
	 * Leaving rule legacy_change.
	 * 
	 * Propagate result of child to label of this node.
	 */
	@Override
	public void exitLegacy_change(Legacy_changeContext ctx) {
		LOGGER.debug("Leaving legacy_change");
		setValue(ctx, getValue(ctx.getChild(0)));
	}

	/**
	 * Leaving of legacy_change_deletion rule
	 * 
	 * Construct {@link LegacyDeletion} from children's value and label this node with it.
	 */
	@Override
	public void exitLegacy_change_deletion(Legacy_change_deletionContext ctx) {
		LOGGER.debug("Leaving legacy_change_deletion");
		LegacyLocation location = (LegacyLocation) getValue(ctx.legacy_point_location());

		final NucleotideSeqDescription seqDesc;
		if (ctx.nt_number() != null)
			seqDesc = new NucleotideSeqDescription(Integer.parseInt(ctx.nt_number().getText()));
		else if (ctx.nt_string() != null)
			seqDesc = new NucleotideSeqDescription(ctx.nt_string().getText());
		else
			seqDesc = new NucleotideSeqDescription();

		setValue(ctx, new LegacyDeletion(location, seqDesc));
	}

	/**
	 * Leaving of legacy_change_substitution rule
	 * 
	 * Construct {@link LegacySubstitution} from children's value and label this node with it.
	 */
	@Override
	public void exitLegacy_change_substitution(Legacy_change_substitutionContext ctx) {
		LOGGER.debug("Leaving legacy_change_substitution");
		LegacyLocation location = (LegacyLocation) getValue(ctx.legacy_point_location());

		String from = ctx.NT_STRING(0).getText();
		String to = ctx.NT_STRING(1).getText();

		setValue(ctx, new LegacySubstitution(location, from, to));
	}

	/**
	 * Leaving of legacy_change_indel rule
	 * 
	 * Construct {@link LegacyIndel} from children's value and label this node with it.
	 */
	@Override
	public void exitLegacy_change_indel(Legacy_change_indelContext ctx) {
		LOGGER.debug("Leaving legacy_change_insertion");
		LegacyLocation location = (LegacyLocation) getValue(ctx.legacy_point_location());

		final NucleotideSeqDescription seqDesc1;
		if (ctx.nt_number(0) != null)
			seqDesc1 = new NucleotideSeqDescription(Integer.parseInt(ctx.nt_number(0).getText()));
		else if (ctx.nt_string(0) != null)
			seqDesc1 = new NucleotideSeqDescription(ctx.nt_string(0).getText());
		else
			seqDesc1 = new NucleotideSeqDescription();

		final NucleotideSeqDescription seqDesc2;
		if (ctx.nt_number(1) != null)
			seqDesc2 = new NucleotideSeqDescription(Integer.parseInt(ctx.nt_number(1).getText()));
		else if (ctx.nt_string(1) != null)
			seqDesc2 = new NucleotideSeqDescription(ctx.nt_string(1).getText());
		else
			seqDesc2 = new NucleotideSeqDescription();

		setValue(ctx, new LegacyIndel(location, seqDesc1, seqDesc2));
	}

	/**
	 * Leaving of legacy_change_insertion rule
	 * 
	 * Construct {@link LegacyInsertion} from children's value and label this node with it.
	 */
	@Override
	public void exitLegacy_change_insertion(Legacy_change_insertionContext ctx) {
		LOGGER.debug("Leaving legacy_change_insertion");
		LegacyLocation location = (LegacyLocation) getValue(ctx.legacy_point_location());

		final NucleotideSeqDescription seqDesc;
		if (ctx.nt_number() != null)
			seqDesc = new NucleotideSeqDescription(Integer.parseInt(ctx.nt_number().getText()));
		else if (ctx.nt_string() != null)
			seqDesc = new NucleotideSeqDescription(ctx.nt_string().getText());
		else
			seqDesc = new NucleotideSeqDescription();

		setValue(ctx, new LegacyInsertion(location, seqDesc));
	}

	/**
	 * Leaving of legacy_point_location rule
	 * 
	 * Construct {@link LegacyInsertion} from children's value and label this node with it.
	 */
	@Override
	public void exitLegacy_point_location(Legacy_point_locationContext ctx) {
		LOGGER.debug("Leaving legacy_point_location");
		int featureNo = Integer.parseInt(ctx.nt_number(0).getText());
		int offset = Integer.parseInt(ctx.nt_number(1).getText());
		if (ctx.NT_MINUS() != null)
			offset = -offset;
		
		if (ctx.LEGACY_IVS_OR_EX().getText().equals("IVS"))
			setValue(ctx, LegacyLocation.buildIntronicLocation(featureNo, offset));
		else
			setValue(ctx, LegacyLocation.buildExonicLocation(featureNo, offset));
	}

	/**
	 * Simple triple for labeling of "reference" nodes.
	 */
	private static class ReferenceLabel {

		/** transcript identifier string */
		private final String transcriptID;
		/** transcript version or NucleotideVariant.NO_TRANSCRIPT_VERSION */
		private final int transcriptVersion;
		/** protein ID string or null */
		private final String proteinID;

		public ReferenceLabel(String transcriptID, int transcriptVersion, String proteinID) {
			super();
			this.transcriptID = transcriptID;
			this.transcriptVersion = transcriptVersion;
			this.proteinID = proteinID;
		}

		public String getTranscriptID() {
			return transcriptID;
		}

		public int getTranscriptVersion() {
			return transcriptVersion;
		}

		public String getProteinID() {
			return proteinID;
		}

	}

}
