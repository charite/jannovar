.. _ped_filters::

Mode Of Inheritance Filters
===========================

Jannovar includes functionality to filter variants for being compatible with a given pedigree and a mode of inheritance.
These filters work well for single individuals and the common case of two parents and a number of children.
However, there are limitations when using them for larger pedigrees.
For such larger families, the filters lose *specificity* but not *sensitivity*.
That is, they can fail to filter out less than theoretically possible, but they should not lose any data.

This section describes in detail the checks performed on the variant and pedigrees to give the user a clear understanding on the algorithms and limitations.

Autosomal Dominant Filter
-------------------------

This filter can be used to filter for *de novo* mutations as well.

Autosomal Recessive Filter
--------------------------

Autosomal X-Dominant Filter
---------------------------

Autosomal X-Recessive Filter
----------------------------

