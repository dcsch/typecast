/*
 * Typecast
 *
 * Copyright © 2004-2019 David Schweinsberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.java.dev.typecast.exchange;

import java.io.OutputStream;
import java.io.PrintStream;

import net.java.dev.typecast.ot.*;
import net.java.dev.typecast.ot.table.CmapFormat;
import net.java.dev.typecast.ot.table.Feature;
import net.java.dev.typecast.ot.table.FeatureTags;
import net.java.dev.typecast.ot.table.GsubTable;
import net.java.dev.typecast.ot.table.ID;
import net.java.dev.typecast.ot.table.KernSubtable;
import net.java.dev.typecast.ot.table.KernTable;
import net.java.dev.typecast.ot.table.KerningPair;
import net.java.dev.typecast.ot.table.LangSys;
import net.java.dev.typecast.ot.table.PostTable;
import net.java.dev.typecast.ot.table.Script;
import net.java.dev.typecast.ot.table.ScriptTags;
import net.java.dev.typecast.ot.table.SingleSubst;
import net.java.dev.typecast.ot.table.TableException;
import org.apache.batik.util.SVGConstants;
import org.apache.batik.util.XMLConstants;

/**
 * Converts a TrueType font to an SVG embedded font.
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class SVGExporter
        extends Exporter
        implements XMLConstants, SVGConstants, ScriptTags, FeatureTags {

    static final String EOL;

    static final String PROPERTY_LINE_SEPARATOR = "line.separator";
    static final String PROPERTY_LINE_SEPARATOR_DEFAULT = "\n";

    static {
        String  temp;
        try { 
            temp = System.getProperty (PROPERTY_LINE_SEPARATOR, 
                                       PROPERTY_LINE_SEPARATOR_DEFAULT); 
        } catch (SecurityException e) { 
            temp = PROPERTY_LINE_SEPARATOR_DEFAULT;
        }
        EOL = temp;
    }
    
    private static final String QUOT_EOL = XML_CHAR_QUOT + EOL;

    /**
     * Defines the start of the generated SVG document
     * {0} SVG public ID
     * {1} SVG system ID
     */
    private static final String CONFIG_SVG_BEGIN = 
        "SVGFont.config.svg.begin";

    /**
     * Defines the SVG start fragment that exercise the generated
     * Font.
     */
    private static final String CONFIG_SVG_TEST_CARD_START = 
        "SVGFont.config.svg.test.card.start";

    /**
     * Defines the end of the SVG fragment that exercise the generated
     * Font.
     */
    private static final String CONFIG_SVG_TEST_CARD_END = 
        "SVGFont.config.svg.test.card.end";

    protected static String encodeEntities(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            switch (s.charAt(i)) {
                case XML_CHAR_LT:
                    sb.append(XML_ENTITY_LT);
                    break;
                case XML_CHAR_GT:
                    sb.append(XML_ENTITY_GT);
                    break;
                case XML_CHAR_AMP:
                    sb.append(XML_ENTITY_AMP);
                    break;
                case XML_CHAR_APOS:
                    sb.append(XML_ENTITY_APOS);
                    break;
                case XML_CHAR_QUOT:
                    sb.append(XML_ENTITY_QUOT);
                    break;
                default:
                    sb.append(s.charAt(i));
                    break;
            }
        }
        return sb.toString();
    }

    protected static String getContourAsSVGPathData(Glyph glyph, int startIndex, int count) {

        // If this is a single point on it's own, we can't do anything with it
        if (glyph.getPoint(startIndex).endOfContour) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        int offset = 0;

        while (offset < count) {
            Point point = glyph.getPoint(startIndex + offset%count);
            Point point_plus1 = glyph.getPoint(startIndex + (offset+1)%count);
            Point point_plus2 = glyph.getPoint(startIndex + (offset+2)%count);

            if (offset == 0) {
                sb.append(PATH_MOVE)
                .append(point.x)
                .append(XML_SPACE)
                .append(point.y);
            }

            if (point.onCurve && point_plus1.onCurve) {
                if (point_plus1.x == point.x) { // This is a vertical line
                    sb.append(PATH_VERTICAL_LINE_TO)
                    .append(point_plus1.y);
                } else if (point_plus1.y == point.y) { // This is a horizontal line
                    sb.append(PATH_HORIZONTAL_LINE_TO)
                    .append(point_plus1.x);
                } else {
                    sb.append(PATH_LINE_TO)
                    .append(point_plus1.x)
                    .append(XML_SPACE)
                    .append(point_plus1.y);
                }
                offset++;
            } else if (point.onCurve && !point_plus1.onCurve && point_plus2.onCurve) {
                // This is a curve with no implied points
                sb.append(PATH_QUAD_TO)
                .append(point_plus1.x)
                .append(XML_SPACE)
                .append(point_plus1.y)
                .append(XML_SPACE)
                .append(point_plus2.x)
                .append(XML_SPACE)
                .append(point_plus2.y);
                offset+=2;
            } else if (point.onCurve && !point_plus1.onCurve && !point_plus2.onCurve) {
                // This is a curve with one implied point
                sb.append(PATH_QUAD_TO)
                .append(point_plus1.x)
                .append(XML_SPACE)
                .append(point_plus1.y)
                .append(XML_SPACE)
                .append(midValue(point_plus1.x, point_plus2.x))
                .append(XML_SPACE)
                .append(midValue(point_plus1.y, point_plus2.y));
                offset+=2;
            } else if (!point.onCurve && !point_plus1.onCurve) {
                // This is a curve with two implied points
                sb.append(PATH_SMOOTH_QUAD_TO)
                .append(midValue(point.x, point_plus1.x))
                .append(XML_SPACE)
                .append(midValue(point.y, point_plus1.y));
                offset++;
            } else if (!point.onCurve && point_plus1.onCurve) {
                sb.append(PATH_SMOOTH_QUAD_TO)
                .append(point_plus1.x)
                .append(XML_SPACE)
                .append(point_plus1.y);
                offset++;
            } else {
                System.out.println("drawGlyph case not catered for!!");
                break;
            }
        }
        sb.append(PATH_CLOSE);

        return sb.toString();
    }

    protected static String getSVGFontFaceElement(OTFont font) {
        StringBuilder sb = new StringBuilder();
        String fontFamily = font.getNameTable().getRecordString(ID.nameFontFamilyName);
        short unitsPerEm = font.getHeadTable().getUnitsPerEm();
        String panose = font.getOS2Table().getPanose().toString();
        short ascent = font.getHheaTable().getAscender();
        short descent = font.getHheaTable().getDescender();
        int baseline = 0; // bit 0 of head.flags will indicate if this is true

	// 	<!ELEMENT font-face (%descTitleMetadata;,font-face-src?,definition-src?) >
	//           <!ATTLIST font-face 
	//             %stdAttrs;
	//             font-family CDATA #IMPLIED
	//             font-style CDATA #IMPLIED
	//             font-variant CDATA #IMPLIED
	//             font-weight CDATA #IMPLIED
	//             font-stretch CDATA #IMPLIED
	//             font-size CDATA #IMPLIED
	//             unicode-range CDATA #IMPLIED
	//             units-per-em %Number; #IMPLIED
	//             panose-1 CDATA #IMPLIED
	//             stemv %Number; #IMPLIED
	//             stemh %Number; #IMPLIED
	//             slope %Number; #IMPLIED
	//             cap-height %Number; #IMPLIED
	//             x-height %Number; #IMPLIED
	//             accent-height %Number; #IMPLIED
	//             ascent %Number; #IMPLIED
	//             descent %Number; #IMPLIED
	//             widths CDATA #IMPLIED
	//             bbox CDATA #IMPLIED
	//             ideographic %Number; #IMPLIED
	//             alphabetic %Number; #IMPLIED
	//             mathematical %Number; #IMPLIED
	//             hanging %Number; #IMPLIED
	//             v-ideographic %Number; #IMPLIED
	//             v-alphabetic %Number; #IMPLIED
	//             v-mathematical %Number; #IMPLIED
	//             v-hanging %Number; #IMPLIED
	//             underline-position %Number; #IMPLIED
	//             underline-thickness %Number; #IMPLIED
	//             strikethrough-position %Number; #IMPLIED
	//             strikethrough-thickness %Number; #IMPLIED
	//             overline-position %Number; #IMPLIED
	//             overline-thickness %Number; #IMPLIED >
	
        sb.append(XML_OPEN_TAG_START).append(SVG_FONT_FACE_TAG).append(EOL)
            .append(XML_TAB).append(SVG_FONT_FAMILY_ATTRIBUTE).append(XML_EQUAL_QUOT).append(fontFamily).append(QUOT_EOL)
            .append(XML_TAB).append(SVG_UNITS_PER_EM_ATTRIBUTE).append(XML_EQUAL_QUOT).append(unitsPerEm).append(QUOT_EOL)
            .append(XML_TAB).append(SVG_PANOSE_1_ATTRIBUTE).append(XML_EQUAL_QUOT).append(panose).append(QUOT_EOL)
            .append(XML_TAB).append(SVG_ASCENT_ATTRIBUTE).append(XML_EQUAL_QUOT).append(ascent).append(QUOT_EOL)
            .append(XML_TAB).append(SVG_DESCENT_ATTRIBUTE).append(XML_EQUAL_QUOT).append(descent).append(QUOT_EOL)
            .append(XML_TAB).append(SVG_ALPHABETIC_ATTRIBUTE).append(XML_EQUAL_QUOT).append(baseline).append(XML_CHAR_QUOT)
            .append(XML_OPEN_TAG_END_NO_CHILDREN).append(EOL);
 
        return sb.toString();
    }

    /**
     * Returns a <font>...</font> block, defining the specified font.
     *
     * @param ps
     * @param font The TrueType font to be converted to SVG
     * @param id An XML id attribute for the font element
     * @param first The first character in the output range
     * @param last The last character in the output range
     * @param forceAscii Force the use of the ASCII character map
     * @throws net.java.dev.typecast.ot.table.TableException
     */
    protected static void writeFontAsSVGFragment(PrintStream ps, TTFont font, String id, int first, int last, boolean forceAscii)
    throws TableException {
        int horiz_advance_x = font.getOS2Table().getAvgCharWidth();

        ps.print(XML_OPEN_TAG_START);
        ps.print(SVG_FONT_TAG);
        ps.print(XML_SPACE);
        if (id != null) {
            ps.print(SVG_ID_ATTRIBUTE);
            ps.print(XML_EQUAL_QUOT);
            ps.print(id);
            ps.print(XML_CHAR_QUOT);
            ps.print(XML_SPACE);
        }

        ps.print(SVG_HORIZ_ADV_X_ATTRIBUTE);
        ps.print(XML_EQUAL_QUOT);
        ps.print(horiz_advance_x);
        ps.print(XML_CHAR_QUOT);
        ps.print(XML_OPEN_TAG_END_CHILDREN);

        ps.print(getSVGFontFaceElement(font));

        // Decide upon a cmap table to use for our character to glyph look-up
        CmapFormat cmapFmt = null;
        if (forceAscii) {
            
            // We've been asked to use the ASCII/Macintosh cmap format
            cmapFmt = font.getCmapTable().getCmapFormat(
                ID.platformMacintosh,
                ID.encodingRoman);
        } else {
            
            // The default behaviour is to use the Unicode cmap encoding
            cmapFmt = font.getCmapTable().getCmapFormat(
                ID.platformMicrosoft,
                ID.encodingUnicode);
            if (cmapFmt == null) {

                // This might be a symbol font
                cmapFmt = font.getCmapTable().getCmapFormat(
                    ID.platformMicrosoft,
                    ID.encodingSymbol);
            }
        }
        if (cmapFmt == null) {
            throw new TableException("Cannot find a suitable cmap table");
        }

        // If this font includes arabic script, we want to specify substitutions
        // for initial, medial, terminal & isolated cases.
        GsubTable gsub = font.getGsubTable();
        SingleSubst initialSubst = null;
        SingleSubst medialSubst = null;
        SingleSubst terminalSubst = null;
        if (gsub != null && gsub.getScriptList() != null) {
            Script s = gsub.getScriptList().findScript(SCRIPT_TAG_ARAB);
            if (s != null) {
                LangSys ls = s.getDefaultLangSys();
                if (ls != null) {
                    Feature init = gsub.getFeatureList().findFeature(ls, FEATURE_TAG_INIT);
                    Feature medi = gsub.getFeatureList().findFeature(ls, FEATURE_TAG_MEDI);
                    Feature fina = gsub.getFeatureList().findFeature(ls, FEATURE_TAG_FINA);

                    initialSubst = (SingleSubst)
                        gsub.getLookupList().getLookup(init, 0).getSubtable(0);
                    medialSubst = (SingleSubst)
                        gsub.getLookupList().getLookup(medi, 0).getSubtable(0);
                    terminalSubst = (SingleSubst)
                        gsub.getLookupList().getLookup(fina, 0).getSubtable(0);
                }
            }
        }

        // Include the missing glyph
        ps.println(getGlyphAsSVG(font, font.getGlyph(0), 0, horiz_advance_x,
            initialSubst, medialSubst, terminalSubst, ""));

        try {
            // Include our requested range
            for (int i = first; i <= last; i++) {
                int glyphIndex = cmapFmt.mapCharCode(i);

                if (glyphIndex > 0) {
                    ps.println(getGlyphAsSVG(
                        font,
                        font.getGlyph(glyphIndex),
                        glyphIndex,
                        horiz_advance_x,
                        initialSubst, medialSubst, terminalSubst,
                        (32 <= i && i <= 127) ?
                        encodeEntities("" + (char) i) :
                        XML_CHAR_REF_PREFIX + Integer.toHexString(i) + XML_CHAR_REF_SUFFIX));
                }

            }

            // Output kerning pairs from the requested range
            KernTable kern = font.getKernTable();
            if (kern != null) {
                KernSubtable kst = kern.getSubtable(0);
                PostTable post = font.getPostTable();
                for (int i = 0; i < kst.getKerningPairCount(); i++) {
                    ps.println(getKerningPairAsSVG(kst.getKerningPair(i), post));
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        ps.print(XML_CLOSE_TAG_START);
        ps.print(SVG_FONT_TAG);
        ps.println(XML_CLOSE_TAG_END);
    }

    protected static String getGlyphAsSVG(
            OTFont font,
            Glyph glyph,
            int glyphIndex,
            int defaultHorizAdvanceX,
            String attrib,
            String code) {

        StringBuilder sb = new StringBuilder();
        int firstIndex = 0;
        int count = 0;
        int i;
        int horiz_advance_x;

        horiz_advance_x = font.getHmtxTable().getAdvanceWidth(glyphIndex);

        if (glyphIndex == 0) {
            sb.append(XML_OPEN_TAG_START);
            sb.append(SVG_MISSING_GLYPH_TAG);
        } else {

            // Unicode value
            sb.append(XML_OPEN_TAG_START)
                .append(SVG_GLYPH_TAG).append(XML_SPACE).append(SVG_UNICODE_ATTRIBUTE)
                .append(XML_EQUAL_QUOT).append(code).append(XML_CHAR_QUOT);

            // Glyph name
            sb.append(XML_SPACE).append(SVG_GLYPH_NAME_ATTRIBUTE).append(XML_EQUAL_QUOT)
                .append(font.getPostTable().getGlyphName(glyphIndex))
                .append(XML_CHAR_QUOT);
        }
        if (horiz_advance_x != defaultHorizAdvanceX) {
            sb.append(XML_SPACE).append(SVG_HORIZ_ADV_X_ATTRIBUTE).append(XML_EQUAL_QUOT)
                .append(horiz_advance_x).append(XML_CHAR_QUOT);
        }

        if (attrib != null) {
            sb.append(XML_SPACE).append(attrib);
        }

        if (glyph != null) {
            sb.append(XML_SPACE).append(SVG_D_ATTRIBUTE).append(XML_EQUAL_QUOT);
            for (i = 0; i < glyph.getPointCount(); i++) {
                count++;
                if (glyph.getPoint(i).endOfContour) {
                    sb.append(getContourAsSVGPathData(glyph, firstIndex, count));
                    firstIndex = i + 1;
                    count = 0;
                }
            }
            sb.append(XML_CHAR_QUOT);
        }

        sb.append(XML_OPEN_TAG_END_NO_CHILDREN);
 
        // Chop-up the string into 255 character lines
        chopUpStringBuffer(sb);

        return sb.toString();
    }

    protected static String getGlyphAsSVG(
            TTFont font,
            Glyph glyph,
            int glyphIndex,
            int defaultHorizAdvanceX,
            SingleSubst arabInitSubst,
            SingleSubst arabMediSubst,
            SingleSubst arabTermSubst,
            String code) {

        StringBuilder sb = new StringBuilder();
        boolean substituted = false;

        // arabic = "initial | medial | terminal | isolated"
        int arabInitGlyphIndex = glyphIndex;
        int arabMediGlyphIndex = glyphIndex;
        int arabTermGlyphIndex = glyphIndex;
        if (arabInitSubst != null) {
            arabInitGlyphIndex = arabInitSubst.substitute(glyphIndex);
        }
        if (arabMediSubst != null) {
            arabMediGlyphIndex = arabMediSubst.substitute(glyphIndex);
        }
        if (arabTermSubst != null) {
            arabTermGlyphIndex = arabTermSubst.substitute(glyphIndex);
        }

        if (arabInitGlyphIndex != glyphIndex) {
            sb.append(getGlyphAsSVG(
                font,
                font.getGlyph(arabInitGlyphIndex),
                arabInitGlyphIndex,
                defaultHorizAdvanceX,
                SVG_ARABIC_FORM_ATTRIBUTE + XML_EQUAL_QUOT + SVG_INITIAL_VALUE + XML_CHAR_QUOT,
                code));
            sb.append(EOL);
            substituted = true;
        }

        if (arabMediGlyphIndex != glyphIndex) {
            sb.append(getGlyphAsSVG(
                font,
                font.getGlyph(arabMediGlyphIndex),
                arabMediGlyphIndex,
                defaultHorizAdvanceX,
                SVG_ARABIC_FORM_ATTRIBUTE + XML_EQUAL_QUOT + SVG_MEDIAL_VALUE + XML_CHAR_QUOT,
                code));
            sb.append(EOL);
            substituted = true;
        }

        if (arabTermGlyphIndex != glyphIndex) {
            sb.append(getGlyphAsSVG(
                font,
                font.getGlyph(arabTermGlyphIndex),
                arabTermGlyphIndex,
                defaultHorizAdvanceX,
                SVG_ARABIC_FORM_ATTRIBUTE + XML_EQUAL_QUOT + SVG_TERMINAL_VALUE + XML_CHAR_QUOT,
                code));
            sb.append(EOL);
            substituted = true;
        }

        if (substituted) {
            sb.append(getGlyphAsSVG(
                font,
                glyph,
                glyphIndex,
                defaultHorizAdvanceX,
                SVG_ARABIC_FORM_ATTRIBUTE + XML_EQUAL_QUOT + SVG_ISOLATED_VALUE + XML_CHAR_QUOT,
                code));
        } else {
            sb.append(getGlyphAsSVG(
                font,
                glyph,
                glyphIndex,
                defaultHorizAdvanceX,
                null,
                code));
        }

        return sb.toString();
    }

    protected static String getKerningPairAsSVG(KerningPair kp, PostTable post) {
        StringBuilder sb = new StringBuilder();
        sb.append(XML_OPEN_TAG_START).append(SVG_HKERN_TAG).append(XML_SPACE);
        sb.append(SVG_G1_ATTRIBUTE).append(XML_EQUAL_QUOT);

        sb.append(post.getGlyphName(kp.getLeft()));
        sb.append(XML_CHAR_QUOT).append(XML_SPACE).append(SVG_G2_ATTRIBUTE).append(XML_EQUAL_QUOT);

        sb.append(post.getGlyphName(kp.getRight()));
         sb.append(XML_CHAR_QUOT).append(XML_SPACE).append(SVG_K_ATTRIBUTE).append(XML_EQUAL_QUOT);

        // SVG kerning values are inverted from TrueType's.
        sb.append(-kp.getValue());
        sb.append(XML_CHAR_QUOT).append(XML_OPEN_TAG_END_NO_CHILDREN);

        return sb.toString();
    }

    protected static void writeSvgBegin(PrintStream ps) {
        ps.println(Messages.formatMessage(CONFIG_SVG_BEGIN,
                                          new Object[]{SVG_PUBLIC_ID, SVG_SYSTEM_ID}));
                   
    }
        
    protected static void writeSvgDefsBegin(PrintStream ps) {
        ps.println(XML_OPEN_TAG_START + SVG_DEFS_TAG + XML_OPEN_TAG_END_CHILDREN);
    }

    protected static void writeSvgDefsEnd(PrintStream ps) {
         ps.println(XML_CLOSE_TAG_START + SVG_DEFS_TAG + XML_CLOSE_TAG_END);
    }

    protected static void writeSvgEnd(PrintStream ps) {
        ps.println(XML_CLOSE_TAG_START + SVG_SVG_TAG + XML_CLOSE_TAG_END);
    }

    protected static void writeSvgTestCard(PrintStream ps, String fontFamily) {
        ps.println(Messages.formatMessage(CONFIG_SVG_TEST_CARD_START, null));
        ps.println(fontFamily);
        ps.println(Messages.formatMessage(CONFIG_SVG_TEST_CARD_END, null));
    }

    private final TTFont _font;
    private final int _low;
    private final int _high;
    private String _id;
    private final boolean _ascii;
    private final boolean _testCard;
    
    public SVGExporter(TTFont font, int low, int high, String id, boolean ascii, boolean testCard) {
        _font = font;
        _low = low;
        _high = high;
        _id = id;
        _ascii = ascii;
        _testCard = testCard;
    }

    /**
     * Does the deed
     * @param os the stream to put the SVG data to
     * @throws net.java.dev.typecast.ot.table.TableException
     */
    @Override
    public void export(OutputStream os) throws TableException {
        PrintStream ps = new PrintStream(os);
        
        // Write the various parts of the SVG file
        writeSvgBegin(ps);
        writeSvgDefsBegin(ps);
        writeFontAsSVGFragment(
            ps,
            _font,
            _id,
            _low,
            _high,
            _ascii);
        writeSvgDefsEnd(ps);
        if (_testCard) {
            String fontFamily = _font.getNameTable().getRecordString(ID.nameFontFamilyName);
            writeSvgTestCard(ps, fontFamily);
        }
        writeSvgEnd(ps);
    }

    private static void chopUpStringBuffer(StringBuilder sb) {
        if (sb.length() < 256) {
            return;
        } else {
            // Being rather simplistic about it, for now we'll insert a newline after
            // 240 chars
            for (int i = 240; i < sb.length(); i++) {
                if (sb.charAt(i) == ' ') {
                    sb.setCharAt(i, '\n');
                    i += 240;
                }
            }
        }
    }

    private static int midValue(int a, int b) {
        return a + (b - a)/2;
    }
}
