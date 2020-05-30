/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.ot.table;

import java.io.DataInput;
import java.io.IOException;

import net.java.dev.typecast.io.BinaryOutput;
import net.java.dev.typecast.io.Writable;
import net.java.dev.typecast.ot.Fixed;

/**
 * PostScript Table
 * 
 * @see "https://docs.microsoft.com/en-us/typography/opentype/spec/post"
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class PostTable implements Table, Writable {

    /**
     * This version is used in order to supply PostScript glyph names when the
     * font file contains exactly the 258 glyphs in the standard Macintosh
     * TrueType font file.
     */
    public static final int VERSION_1_0 = 0x00010000;
    
    /**
     * This is the version required in order to supply PostScript glyph names
     * for fonts which do not supply them elsewhere.
     */
    public static final int VERSION_2_0 = 0x00020000;
    
    /**
     * This version of the 'post' table has been deprecated as of OpenType
     * Specification v1.3.
     */
    public static final int VERSION_2_5 = 0x00025000;
    
    /**
     * This version makes it possible to create a font that is not burdened with
     * a large 'post' table set of glyph names.
     */
    public static final int VERSION_3_0 = 0x00030000;

    private static final String[] MAC_GLYPH_NAME = {
        ".notdef",      // 0
        "null",         // 1
        "CR",           // 2
        "space",        // 3
        "exclam",       // 4
        "quotedbl",     // 5
        "numbersign",   // 6
        "dollar",       // 7
        "percent",      // 8
        "ampersand",    // 9
        "quotesingle",  // 10
        "parenleft",    // 11
        "parenright",   // 12
        "asterisk",     // 13
        "plus",         // 14
        "comma",        // 15
        "hyphen",       // 16
        "period",       // 17
        "slash",        // 18
        "zero",         // 19
        "one",          // 20
        "two",          // 21
        "three",        // 22
        "four",         // 23
        "five",         // 24
        "six",          // 25
        "seven",        // 26
        "eight",        // 27
        "nine",         // 28
        "colon",        // 29
        "semicolon",    // 30
        "less",         // 31
        "equal",        // 32
        "greater",      // 33
        "question",     // 34
        "at",           // 35
        "A",            // 36
        "B",            // 37
        "C",            // 38
        "D",            // 39
        "E",            // 40
        "F",            // 41
        "G",            // 42
        "H",            // 43
        "I",            // 44
        "J",            // 45
        "K",            // 46
        "L",            // 47
        "M",            // 48
        "N",            // 49
        "O",            // 50
        "P",            // 51
        "Q",            // 52
        "R",            // 53
        "S",            // 54
        "T",            // 55
        "U",            // 56
        "V",            // 57
        "W",            // 58
        "X",            // 59
        "Y",            // 60
        "Z",            // 61
        "bracketleft",  // 62
        "backslash",    // 63
        "bracketright", // 64
        "asciicircum",  // 65
        "underscore",   // 66
        "grave",        // 67
        "a",            // 68
        "b",            // 69
        "c",            // 70
        "d",            // 71
        "e",            // 72
        "f",            // 73
        "g",            // 74
        "h",            // 75
        "i",            // 76
        "j",            // 77
        "k",            // 78
        "l",            // 79
        "m",            // 80
        "n",            // 81
        "o",            // 82
        "p",            // 83
        "q",            // 84
        "r",            // 85
        "s",            // 86
        "t",            // 87
        "u",            // 88
        "v",            // 89
        "w",            // 90
        "x",            // 91
        "y",            // 92
        "z",            // 93
        "braceleft",    // 94
        "bar",          // 95
        "braceright",   // 96
        "asciitilde",   // 97
        "Adieresis",    // 98
        "Aring",        // 99
        "Ccedilla",     // 100
        "Eacute",       // 101
        "Ntilde",       // 102
        "Odieresis",    // 103
        "Udieresis",    // 104
        "aacute",       // 105
        "agrave",       // 106
        "acircumflex",  // 107
        "adieresis",    // 108
        "atilde",       // 109
        "aring",        // 110
        "ccedilla",     // 111
        "eacute",       // 112
        "egrave",       // 113
        "ecircumflex",  // 114
        "edieresis",    // 115
        "iacute",       // 116
        "igrave",       // 117
        "icircumflex",  // 118
        "idieresis",    // 119
        "ntilde",       // 120
        "oacute",       // 121
        "ograve",       // 122
        "ocircumflex",  // 123
        "odieresis",    // 124
        "otilde",       // 125
        "uacute",       // 126
        "ugrave",       // 127
        "ucircumflex",  // 128
        "udieresis",    // 129
        "dagger",       // 130
        "degree",       // 131
        "cent",         // 132
        "sterling",     // 133
        "section",      // 134
        "bullet",       // 135
        "paragraph",    // 136
        "germandbls",   // 137
        "registered",   // 138
        "copyright",    // 139
        "trademark",    // 140
        "acute",        // 141
        "dieresis",     // 142
        "notequal",     // 143
        "AE",           // 144
        "Oslash",       // 145
        "infinity",     // 146
        "plusminus",    // 147
        "lessequal",    // 148
        "greaterequal", // 149
        "yen",          // 150
        "mu",           // 151
        "partialdiff",  // 152
        "summation",    // 153
        "product",      // 154
        "pi",           // 155
        "integral'",    // 156
        "ordfeminine",  // 157
        "ordmasculine", // 158
        "Omega",        // 159
        "ae",           // 160
        "oslash",       // 161
        "questiondown", // 162
        "exclamdown",   // 163
        "logicalnot",   // 164
        "radical",      // 165
        "florin",       // 166
        "approxequal",  // 167
        "increment",    // 168
        "guillemotleft",// 169
        "guillemotright",//170
        "ellipsis",     // 171
        "nbspace",      // 172
        "Agrave",       // 173
        "Atilde",       // 174
        "Otilde",       // 175
        "OE",           // 176
        "oe",           // 177
        "endash",       // 178
        "emdash",       // 179
        "quotedblleft", // 180
        "quotedblright",// 181
        "quoteleft",    // 182
        "quoteright",   // 183
        "divide",       // 184
        "lozenge",      // 185
        "ydieresis",    // 186
        "Ydieresis",    // 187
        "fraction",     // 188
        "currency",     // 189
        "guilsinglleft",// 190
        "guilsinglright",//191
        "fi",           // 192
        "fl",           // 193
        "daggerdbl",    // 194
        "middot",       // 195
        "quotesinglbase",//196
        "quotedblbase", // 197
        "perthousand",  // 198
        "Acircumflex",  // 199
        "Ecircumflex",  // 200
        "Aacute",       // 201
        "Edieresis",    // 202
        "Egrave",       // 203
        "Iacute",       // 204
        "Icircumflex",  // 205
        "Idieresis",    // 206
        "Igrave",       // 207
        "Oacute",       // 208
        "Ocircumflex",  // 209
        "apple",        // 210
        "Ograve",       // 211
        "Uacute",       // 212
        "Ucircumflex",  // 213
        "Ugrave",       // 214
        "dotlessi",     // 215
        "circumflex",   // 216
        "tilde",        // 217
        "overscore",    // 218
        "breve",        // 219
        "dotaccent",    // 220
        "ring",         // 221
        "cedilla",      // 222
        "hungarumlaut", // 223
        "ogonek",       // 224
        "caron",        // 225
        "Lslash",       // 226
        "lslash",       // 227
        "Scaron",       // 228
        "scaron",       // 229
        "Zcaron",       // 230
        "zcaron",       // 231
        "brokenbar",    // 232
        "Eth",          // 233
        "eth",          // 234
        "Yacute",       // 235
        "yacute",       // 236
        "Thorn",        // 237
        "thorn",        // 238
        "minus",        // 239
        "multiply",     // 240
        "onesuperior",  // 241
        "twosuperior",  // 242
        "threesuperior",// 243
        "onehalf",      // 244
        "onequarter",   // 245
        "threequarters",// 246
        "franc",        // 247
        "Gbreve",       // 248
        "gbreve",       // 249
        "Idot",         // 250
        "Scedilla",     // 251
        "scedilla",     // 252
        "Cacute",       // 253
        "cacute",       // 254
        "Ccaron",       // 255
        "ccaron",       // 256
        "dcroat"        // 257
    };

    private int version;
    private int italicAngle;
    private short underlinePosition;
    private short underlineThickness;
    private int isFixedPitch;
    private int minMemType42;
    private int maxMemType42;
    private int minMemType1;
    private int maxMemType1;
    
    // v2
    private int[] glyphNameIndex;
    private String[] psGlyphNames;

    /**
     * Creates new {@link PostTable}
     * 
     * @param length
     *        Total number of bytes.
     */
    public PostTable(DataInput di, int length) throws IOException {
        version = di.readInt();
        italicAngle = di.readInt();
        underlinePosition = di.readShort();
        underlineThickness = di.readShort();
        isFixedPitch = di.readInt();
        minMemType42 = di.readInt();
        maxMemType42 = di.readInt();
        minMemType1 = di.readInt();
        maxMemType1 = di.readInt();
        
        if (version == VERSION_2_0) {
            int numGlyphs = di.readUnsignedShort();
            glyphNameIndex = new int[numGlyphs];
            for (int i = 0; i < numGlyphs; i++) {
                glyphNameIndex[i] = di.readUnsignedShort();
            }
            int numberNewGlyphs = max(glyphNameIndex);
            if (numberNewGlyphs > 257) {
                numberNewGlyphs -= 257;
                psGlyphNames = new String[numberNewGlyphs];
                for (int i = 0; i < numberNewGlyphs; i++) {
                    int len = di.readUnsignedByte();
                    byte[] buf = new byte[len];
                    di.readFully(buf);
                    psGlyphNames[i] = new String(buf, "ASCII");
                }
            }
        } else if (version == VERSION_2_5) {
            // TODO
        } else if (version == VERSION_3_0) {
            // TODO
        }
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        out.writeInt(version);
        out.writeInt(italicAngle);
        out.writeShort(underlinePosition);
        out.writeShort(underlineThickness);
        out.writeInt(isFixedPitch);
        out.writeInt(minMemType42);
        out.writeInt(maxMemType42);
        out.writeInt(minMemType1);
        out.writeInt(maxMemType1);
        
        if (version == VERSION_2_0) {
            out.writeShort(glyphNameIndex.length);
            for (int nameIndex : glyphNameIndex) {
                out.writeShort(nameIndex);
            }
            
            if (psGlyphNames != null) {
                int numberNewGlyphs = max(glyphNameIndex);
                if (numberNewGlyphs > 257) {
                    numberNewGlyphs -= 257;
                    for (String glyphName : psGlyphNames) {
                        out.writeByte(glyphName.length());
                        out.write(glyphName.getBytes("ASCII"));
                    }
                }
            }
        } else if (version == VERSION_2_5) {
            // TODO
        } else if (version == VERSION_3_0) {
            // TODO
        }
    }

    @Override
    public int getType() {
        return post;
    }

    public int getVersion() {
        return version;
    }

    private static int max(int[] array) {
        int result = 0;
        for (int value : array) {
            if (result < value) {
                result = value;
            }
        }
        return result;
    }

    public int getNumGlyphs() {
        return glyphNameIndex.length;
    }

    public String getGlyphName(int glyph) {
        switch (version) {
        case VERSION_1_0:
            return MAC_GLYPH_NAME[glyph];
            
        case VERSION_2_0:
            int nameIndex = glyphNameIndex[glyph];
            if (nameIndex <= 257) {
                // Macintosh standard order.
                return MAC_GLYPH_NAME[nameIndex];
            } else {
                return psGlyphNames[nameIndex - 258];
            }
        default:
            return null;
        }
    }

    private boolean isMacGlyphName(int i) {
        switch (version) {
            case VERSION_1_0:
                return true;
                
            case VERSION_2_0:
                return glyphNameIndex[i] <= 257;
                
            default:
                return false;
        }
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'post' Table - PostScript Metrics\n---------------------------------")
            .append("\n        'post' version:        ").append(Fixed.floatValue(version))
            .append("\n        italicAngle:           ").append(Fixed.floatValue(italicAngle))
            .append("\n        underlinePosition:     ").append(underlinePosition)
            .append("\n        underlineThickness:    ").append(underlineThickness)
            .append("\n        isFixedPitch:          ").append(isFixedPitch)
            .append("\n        minMemType42:          ").append(minMemType42)
            .append("\n        maxMemType42:          ").append(maxMemType42)
            .append("\n        minMemType1:           ").append(minMemType1)
            .append("\n        maxMemType1:           ").append(maxMemType1)
            .append("\n");

        if (version == VERSION_2_0) {
            sb.append("\n        Format 2.0:  Non-Standard (for PostScript) TrueType Glyph Set.\n");
            sb.append("        numGlyphs:      ").append(getNumGlyphs()).append("\n");
            for (int glyph = 0; glyph < getNumGlyphs(); glyph++) {
                sb.append("        Glyf ").append(glyph).append(" -> ");
                if (isMacGlyphName(glyph)) {
                    sb.append("Mac Glyph # ").append(glyphNameIndex[glyph])
                        .append(", '").append(MAC_GLYPH_NAME[glyphNameIndex[glyph]]).append("'\n");
                } else {
                    sb.append("PSGlyf Name # ").append(glyphNameIndex[glyph] - 257)
                        .append(", name= '").append(psGlyphNames[glyphNameIndex[glyph] - 258]).append("'\n");
                }
            }
            sb.append("\n        Full List of PSGlyf Names\n        ------------------------\n");
            for (int i = 0; i < psGlyphNames.length; i++) {
                sb.append("        PSGlyf Name # ").append(i + 1)
                    .append(": ").append(psGlyphNames[i])
                    .append("\n");
            }
        }
        return sb.toString();
    }
    
}
