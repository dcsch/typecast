# Typecast

[Retired java.net home](https://java.net/projects/typecast)

Typecast is a font development environment for OpenType font technology.
Developments include:

* An outline editor for both TrueType and PostScript outlines
* TrueType hinting engine
* PostScript hinting engine
* Hinting debuggers
* Font conversion utilities
* Font handling libraries for use in wider application development

Parts of the Typecast libraries are used in the Apache Batik project, for
conversion from TrueType to SVG fonts. Further contributions will be made to
Batik - particularly in the areas of PostScript outlines and font hinting.

## File Formats (Native)

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

## File Formats (Export)

* svg - Scalable Vector Graphics font data

## File Formats (Import)

No import file formats are supported as yet, but traditional PostScript Type 1
and Compact Font Format (CFF) font import are under development.

## Outlines

Current outline support is for TrueType outlines. But of course, PostScript
outlines are on the development roadmap.

## Advanced Typographic Tables

Partial support is in place for the Advanced Typographic Tables, specifically
the GPOS and GSUB tables. All advanced tables will be implemented in the
ongoing development.