package exomizer.reference;



/**
 * This class encapsulates a single annotation and includes basically two pieces of information:
 * <OL>
 * <LI>The variant type: frameshift, synonymous substitution, etc
 * <LI>A string representing the actualy variant
 * </OL>
 * This class is meant to be used in place of the hash {@code %function} in Annovar. Typically,
 * variants are entered into that has with lines such as
 * <P>
 * {@code  $function->\{$index\}\{ssnv\} .= "$geneidmap->\{$seqid\}:$seqid:exon$exonpos:$canno:p.$wtaa$varpos$varaa,";}
 * <P>
 * This has then stores the potentially multiple variants for any one chromosomal variant, and these are then
 * printed to the variant and exonic variant files.
 * @author Peter N Robinson
 * @version 0.01 (6 October 2012)
 */
public class Annotation {
    public String variantType=null;
    public String variantAnnotation=null;

    public String getType() { return this.variantType; }


    public Annotation(String type, String anno) {
	this.variantType=type;
	this.variantAnnotation=anno;
    }






}