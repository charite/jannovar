#!/bin/bash
awk -F $'\t' 'BEGIN {OFS=FS} {$4=$4-48500000;$5=$5-48500000; print $0}' orig.gff3 >transcript.gff3
