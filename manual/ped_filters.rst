.. _ped_filters::

Mode Of Inheritance Filters
===========================

Jannovar includes functionality to filter variants for being compatible with a given pedigree and a mode of inheritance.
These filters work well for single individuals and the common case of two parents and a number of children.
However, there are limitations when using them for larger pedigrees.
For such larger families, the filters lose *specificity* but not *sensitivity*.
That is, they can fail to filter out less than theoretically possible, but they should not lose any data.

This section describes in detail the checks performed on the variant and pedigrees to give the user a clear understanding on the algorithms and limitations.

The filters are passed a pedigree and a list of genotype calls.
The mode of inheritance is selected by the filter choice.
The whole list of genotype calls (usually the genotypes of variants falling onto one gene or transcripts) is then checked for compatibility with the given mode of inheritance and pedigree.

The genotype calls of the variants can either be ``NOCALL`` (no genotype was determined for the person), ``REF`` (homozygous wild-type), ``HET`` (heterozygous alternative), or ``HOM`` (homozygous alternative).
In general a caller calls a hemizygous mutations as homozygous. Therefore we use ``HOM`` and for sensitivity ``HET`` on known males as hemizygous. The persons can either be affected, unaffected, or their affection state is unknown.

Autosomal Dominant Filter
-------------------------

This filter can be used to filter for *de novo* mutations as well.

* If the pedigree only contains one person then the variant call list must contain one ``HET`` call.
* If there is more than one person in the pedigree then there must be at least one compatible call, meaning:

   * at least one affected person has a ``HET`` call for this variant,
   * no affected person has a ``REF`` or ``HOM`` call, and
   * no unaffected person has a ``HET`` or ``HOM`` call.

Autosomal Recessive Filter
--------------------------

The filter first checks for compatibility with autosomal recessive (AR) homozygous and then AR compound heterozygous mode of inheritance.

For AR homozygous, the following checks are performed.

* If the pedigree only contains one person then the variant call list must contain one ``HOM`` call.
* If there is more than one person in the pedigree then there must be at least one compatible variant call in the list.
  For this, the following must be true for one variant in the list:

   * at least one affected person has a ``HOM`` call for this variant and
   * no affected person has a ``REF`` or ``HET`` call.
   * The unaffected parents of affected persons must not be ``REF`` or ``HOM``.
   * There is no unaffected person that has a ``HOM`` call.

For AR compound heterozygous, the following checks are performed.

* If the pedigree only contains one person then there must be at least two ``HET`` entries in the variant list.
* If there is more than one person in the pedigree then the algorithm first enumerates *candidate pairs* of variants.
  The pairs are enumerated for all affected persons that have a father, a mother, or both in the pedigree.

   * The first entry in the pair is compatible with inheritance from the maternal side and the second entry in the pair is compatible from the paternal side.
   * A variant is compatible regarding the paternal side if:

      * the person has calls ``HET`` or ``NOCALL``,
      * the person has no father or the father has calls ``HET`` or ``NOCALL``,
      * the person has no mother or the mother has calls ``REF`` or ``NOCALL``.

   * A variant is compatible regarding the maternal side if:

      * the person has calls ``HET`` or ``NOCALL``,
      * the person has no mother or the mother has calls ``HET`` or ``NOCALL``,
      * the person has no father or the father has calls ``REF`` or ``NOCALL``.

   * Further, no candidate pair may contain the same call for both the maternal and the paternal side, and
   * there must be at least one call for the person, mother, or father that is not ``NOCALL``.

* Each candidate pair is then check for compatibility with the unaffected persons.
  The following is performed as described below and also with a role swap of the paternal and maternal variant call list.
  
  * For each affected person, the maternal and paternal variant call list is performed for compatibility. For this, each of the following must be checked:

    * If the maternal list is not empty then the genotype of the person in the paternal list must not be ``REF`` or ``HOM``.
    * If the paternal list is not empty then the genotype of the person in the maternal list must not be ``REF`` or ``HOM``.
    * If the paternal list is not empty and the person has a father then the father's genotype in the paternal list must not be ``REF`` or ``HOM``.
    * If the maternal list is not empty and the person has a mother then the mother's genotype in the maternal list must not be ``REF`` or ``HOM``.
    * None of the affected person's unaffected siblings must be both ``HET`` in the paternal or maternal list.
    * Every affected siblings of an afffected person must have ``HET`` in the paternal or maternal list.

* Finally, we check every unaffected person in the pedigree.

   * For each unaffected person in the pedigree, neither the maternal nor the paternal call list from the candidate can contain a ``HOM`` call for the unaffected person.
   * If the call for the unaffected persons is ``HET`` in both the paternal and the maternal call list. Then, the father's and mother's genotype are checked in the maternal call list of the candidate their genotypes in the paternal call list are considered.

     * Let the first two genotypes be ``pp`` and ``mp`` and the second two genotypes be ``pm`` and ``mm``.
     * In the case of ``pp == HET and mp == REF and pm == REF and mm == HET`` and the case of ``pp == REF and mp == HET and pm == HET and mm == REF``, the candidate pairs incompatible and compatible otherwise.  

Autosomal X-Dominant Filter
---------------------------

Autosomal X-Recessive Filter
----------------------------

