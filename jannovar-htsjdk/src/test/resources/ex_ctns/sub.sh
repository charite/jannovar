#!/bin/bash
awk -F $'\t' 'BEGIN {OFS=FS} {$1="ref";$4=$4-3500000;$5=$5-3500000; print $0}' orig.gff3 >transcript.gff3
