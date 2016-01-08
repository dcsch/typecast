# Typecast

[Typecast](http://dcsch.github.io/typecast/) is a font development environment for OpenType font technology.
Developments include:

* An outline editor for both TrueType and PostScript (CFF) outlines
* TrueType hinting engine
* PostScript hinting engine
* Hinting debuggers
* Font conversion utilities
* Font handling libraries for use in wider application development

Parts of the Typecast libraries are used in the Apache Batik project, for
conversion from TrueType to SVG fonts.

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
the GPOS and GSUB tables. All advanced tables will be implemented in the
ongoing development.
