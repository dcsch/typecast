# Typecast

[Typecast](http://dcsch.github.io/typecast/) is a Java library for the handling of OpenType fonts.

## File Formats

OpenType fonts come in a small range of file format flavours. Typecast supports
the following:

* ttf - The basic TrueType font file
* otf - Essentially the same as ttf, but usually contain a PostScript
  outline rather than a TrueType outline
* ttc - TrueType Collection file. This is a collection of fonts that share
  some common tables. Often utilised by far eastern fonts to make more
  efficient use of shared components
* dfont - A Macintosh Font Suitcase resource file, with the resources in
  the data fork - as utilised by MacOS X

## Export Formats

* svg - Scalable Vector Graphics font data

## Outlines

Outline support includes both TrueType and PostScript Charstring Type 2 outlines.

## Advanced Typographic Tables

Partial support is in place for the Advanced Typographic Tables, specifically
the GPOS and GSUB tables.

