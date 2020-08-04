/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package net.java.dev.typecast.ot.table;

import java.io.DataInput;
import java.io.IOException;

import net.java.dev.typecast.io.BinaryOutput;
import net.java.dev.typecast.io.Writable;
import net.java.dev.typecast.ot.Bits;
import net.java.dev.typecast.ot.Fixed;
import net.java.dev.typecast.ot.LongDateTime;

/**
 * head — Font Header Table
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/head">Spec: Font Header Table</a>
 */
public class HeadTable implements Table, Writable {
    
    private static final short FORMAT_LONG_OFFSETS = 1;

    private static final short FORMAT_SHORT_OFFSETS = 0;

    /**
     * @see #getMagicNumber()
     */
    public static final int MAGIC = 0x5F0F3CF5;
    
    /**
     * @see #getMajorVersion()
     */
    public static final int MAJOR_VERSION = 1;

    /**
     * @see #getMinorVersion()
     */
    public static final int MINOR_VERSION = 0;
    
    /**
     * @see #getGlyphDataFormat()
     */
    public static final short GLYPH_DATA_FORMAT = 0;

    /**
     * 0: Fully mixed directional glyphs;
     * @see #getFontDirectionHint()
     */
    public static final short FONT_DIRECTION_MIXED = 0;
    
    /**
     * 1: Only strongly left to right;
     * @see #getFontDirectionHint()
     */
    public static final short FONT_DIRECTION_LEFT_TO_RIGHT = 1;

    /**
     * 2: Like 1 but also contains neutrals;
     * @see #getFontDirectionHint()
     */
    public static final short FONT_DIRECTION_LEFT_TO_RIGHT_AND_NEUTRAL = 2;

    /**
     * -1: Only strongly right to left;
     * @see #getFontDirectionHint()
     */
    public static final short FONT_DIRECTION_RIGHT_TO_LEFT = -1;
    
    /**
     * -2: Like -1 but also contains neutrals.
     * @see #getFontDirectionHint()
     */
    public static final short FONT_DIRECTION_RIGHT_TO_LEFT_AND_NEUTRAL = -2;
    
    /**
     * @see #getMajorVersion()
     */
    private int _majorVersion = MAJOR_VERSION;
    
    /**
     * @see #getMinorVersion()
     */
    private int _minorVersion = MINOR_VERSION;
    
    /**
     * @see #getFontRevision()
     */
    private int _fontRevision;
    
    /**
     * @see #getCheckSumAdjustment()
     */
    private int _checkSumAdjustment;
    
    /**
     * @see #getMagicNumber()
     */
    private int _magicNumber = MAGIC;
    
    /**
     * @see #getFlags()
     */
    private short _flags;
    
    /**
     * @see #getUnitsPerEm()
     */
    private short _unitsPerEm;
    
    /**
     * @see #getCreated()
     */
    private long _created;
    
    /**
     * @see #getModified()
     */
    private long _modified;
    
    /**
     * @see #getXMin()
     */
    private short _xMin;
    
    /**
     * @see #getYMin()
     */
    private short _yMin;
    
    /**
     * @see #getXMax()
     */
    private short _xMax;
    
    /**
     * @see #getYMax()
     */
    private short _yMax;
    
    /**
     * @see #getMacStyle()
     */
    private short _macStyle;
    
    /**
     * @see #getLowestRecPPEM()
     */
    private short _lowestRecPPEM;
    
    /**
     * @see #getFontDirectionHint()
     */
    private short _fontDirectionHint = FONT_DIRECTION_LEFT_TO_RIGHT_AND_NEUTRAL;
    
    /**
     * @see #getIndexToLocFormat()
     */
    private short _indexToLocFormat;
    
    /**
     * @see #getGlyphDataFormat()
     */
    private short _glyphDataFormat = GLYPH_DATA_FORMAT;

    private long _checkSumAdjustmentPos;

    @Override
    public void read(DataInput di, int length) throws IOException {
        _majorVersion = di.readUnsignedShort();
        _minorVersion = di.readUnsignedShort();
        _fontRevision = di.readInt();
        _checkSumAdjustment = di.readInt();
        _magicNumber = di.readInt();
        _flags = di.readShort();
        _unitsPerEm = di.readShort();
        _created = di.readLong();
        _modified = di.readLong();
        _xMin = di.readShort();
        _yMin = di.readShort();
        _xMax = di.readShort();
        _yMax = di.readShort();
        _macStyle = di.readShort();
        _lowestRecPPEM = di.readShort();
        _fontDirectionHint = di.readShort();
        _indexToLocFormat = di.readShort();
        _glyphDataFormat = di.readShort();
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        
        out.writeShort(_majorVersion);
        out.writeShort(_minorVersion);
        out.writeInt(_fontRevision);

        // Updated later on.
        _checkSumAdjustmentPos = out.getPosition();
        _checkSumAdjustment = 0;
        out.writeInt(_checkSumAdjustment);
        
        out.writeInt(_magicNumber);
        out.writeShort(_flags);
        out.writeShort(_unitsPerEm);
        out.writeLong(_created);
        out.writeLong(_modified);
        out.writeShort(_xMin);
        out.writeShort(_yMin);
        out.writeShort(_xMax);
        out.writeShort(_yMax);
        out.writeShort(_macStyle);
        out.writeShort(_lowestRecPPEM);
        out.writeShort(_fontDirectionHint);
        out.writeShort(_indexToLocFormat);
        out.writeShort(_glyphDataFormat);
    }
    
    @Override
    public int getType() {
        return head;
    }
    
    /**
     * uint16     majorVersion     Major version number of the font header table — set to {@link #MAJOR_VERSION}.
     */
    public int getMajorVersion() {
        return _majorVersion;
    }
    
    /**
     * uint16     minorVersion     Minor version number of the font header table — set to {@link #MINOR_VERSION}.
     */
    public int getMinorVersion() {
        return _minorVersion;
    }

    /**
     * Composed version number from {@link #getMajorVersion()} and {@link #getMinorVersion()}.
     */
    public int getVersionNumber() {
        return _majorVersion << 16 | _minorVersion;
    }
    
    /**
     * Printable version number.
     * 
     * @see #getMajorVersion()
     * @see #getMinorVersion()
     */
    public String getVersion() {
        return _majorVersion + "." + _minorVersion;
    }

    /**
     * Fixed Set by font manufacturer.
     */
    public int getFontRevision(){
        return _fontRevision;
    }

    /**
     * uint32
     * 
     * To compute: set it to 0, sum the entire font as uint32, then store
     * 0xB1B0AFBA - sum.
     */
    public int getCheckSumAdjustment() {
        return _checkSumAdjustment;
    }
    
    void updateChecksumAdjustment(BinaryOutput out, int value) throws IOException {
        _checkSumAdjustment = value;
        out.setPosition(_checkSumAdjustmentPos);
        out.writeInt(_checkSumAdjustment);
    }
    
    /**
     * uint32     Set to {@link #MAGIC}.
     */
    public int getMagicNumber() {
        return _magicNumber;
    }

    /**
     * uint16
     */
    public short getFlags() {
        return _flags;
    }
    
    /**
     * The Left sidebearing point is at x=0 for all glyphs (relevant only for
     * TrueType rasterizers)
     */
    public boolean isLeftSidebearingNormalized() {
        return Bits.bit(_flags, 1);
    }

    /**
     * uint16
     * 
     * Set to a value from 16 to 16384. Any value in this range is valid. In
     * fonts that have TrueType outlines, a power of 2 is recommended as this
     * allows performance optimizations in some rasterizers.
     */
    public short getUnitsPerEm() {
        return _unitsPerEm;
    }

    /**
     * LONGDATETIME
     * 
     * Number of seconds since 12:00 midnight that started January 1st 1904 in
     * GMT/UTC time zone. 64-bit integer
     */
    public long getCreated() {
        return _created;
    }

    /**
     * LONGDATETIME
     * 
     * Number of seconds since 12:00 midnight that started January 1st 1904 in
     * GMT/UTC time zone. 64-bit integer
     */
    public long getModified() {
        return _modified;
    }

    /**
     * int16     
     * 
     * For all glyph bounding boxes.
     */
    public short getXMin() {
        return _xMin;
    }

    /**
     * int16     
     * 
     * For all glyph bounding boxes.
     */
    public short getYMin() {
        return _yMin;
    }

    /**
     * int16     
     * 
     * For all glyph bounding boxes.
     */
    public short getXMax() {
        return _xMax;
    }

    /**
     * int16     
     * 
     * For all glyph bounding boxes.
     */
    public short getYMax() {
        return _yMax;
    }

    /**
     * uint16
     * 
     * Contains information concerning the nature of the font patterns.
     */
    public short getMacStyle() {
        return _macStyle;
    }
    
    /**
     * Whether the glyphs are emboldened.
     * 
     * @see #getMacStyle()
     */
    public boolean isMacBold() {
        return Bits.bit(_macStyle, 0);
    }

    /**
     * Font contains italic or oblique glyphs, otherwise they are upright.
     * 
     * @see #getMacStyle()
     */
    public boolean isMacItalic() {
        return Bits.bit(_macStyle, 1);
    }
    
    /**
     * Glyphs are underscored.
     * 
     * @see #getMacStyle()
     */
    public boolean isMacUnderline() {
        return Bits.bit(_macStyle, 2);
    }
    
    /**
     * Outline (hollow) glyphs, otherwise they are solid.
     * 
     * @see #getMacStyle()
     */
    public boolean isMacOutline() {
        return Bits.bit(_macStyle, 3);
    }
    
    /**
     * Whether the font has shadow.
     * 
     * @see #getMacStyle()
     */
    public boolean isMacShadow() {
        return Bits.bit(_macStyle, 4);
    }
    
    /**
     * Whether the font is condensed.
     * 
     * @see #getMacStyle()
     */
    public boolean isMacCondensed() {
        return Bits.bit(_macStyle, 5);
    }
    
    /**
     * @see #getMacStyle()
     */
    public boolean isMacExtended() {
        return Bits.bit(_macStyle, 6);
    }
    
    /**
     * uint16     
     * 
     * Smallest readable size in pixels.
     */
    public short getLowestRecPPEM() {
        return _lowestRecPPEM;
    }

    /**
     * int16
     * 
     * Deprecated (Set to {@link #FONT_DIRECTION_LEFT_TO_RIGHT_AND_NEUTRAL}).
     * 
     * @see #FONT_DIRECTION_MIXED
     * @see #FONT_DIRECTION_LEFT_TO_RIGHT
     * @see #FONT_DIRECTION_LEFT_TO_RIGHT_AND_NEUTRAL
     * @see #FONT_DIRECTION_RIGHT_TO_LEFT
     * @see #FONT_DIRECTION_RIGHT_TO_LEFT_AND_NEUTRAL
     */
    public short getFontDirectionHint() {
        return _fontDirectionHint;
    }

    /**
     * int16
     * 
     * {@link #FORMAT_SHORT_OFFSETS} for short offsets (Offset16),
     * {@link #FORMAT_LONG_OFFSETS} for long (Offset32).
     */
    public short getIndexToLocFormat() {
        return _indexToLocFormat;
    }

    /**
     * Whether short offsets (Offset16) are used.
     */
    public boolean useShortEntries() {
        return getIndexToLocFormat() == FORMAT_SHORT_OFFSETS;
    }
    
    void setShortEntries(boolean value) {
        _indexToLocFormat = value ? FORMAT_SHORT_OFFSETS : FORMAT_LONG_OFFSETS;
    }

    /**
     * int16
     * 
     * {@link #GLYPH_DATA_FORMAT} for current format.
     */
    public short getGlyphDataFormat() {
        return _glyphDataFormat;
    }

    @Override
    public String toString() {
        return "'head' Table - Font Header\n--------------------------" +
                "\n  'head' version:      " + getVersion() +
                "\n  fontRevision:        " + Fixed.roundedFloatValue(_fontRevision, 8) +
                "\n  checkSumAdjustment:  0x" + Integer.toHexString(_checkSumAdjustment).toUpperCase() +
                "\n  magicNumber:         0x" + Integer.toHexString(_magicNumber).toUpperCase() +
                "\n  flags:               0x" + Integer.toHexString(_flags).toUpperCase() +
                "\n  unitsPerEm:          " + _unitsPerEm +
                "\n  created:             " + LongDateTime.toDate(_created) +
                "\n  modified:            " + LongDateTime.toDate(_modified) +
                "\n  xMin:                " + _xMin +
                "\n  yMin:                " + _yMin +
                "\n  xMax:                " + _xMax +
                "\n  yMax:                " + _yMax +
                "\n  macStyle bits:       " + Integer.toHexString(_macStyle).toUpperCase() +
                "\n  lowestRecPPEM:       " + _lowestRecPPEM +
                "\n  fontDirectionHint:   " + _fontDirectionHint +
                "\n  shortOffsets:        " + (_indexToLocFormat == FORMAT_SHORT_OFFSETS) + " (format: " + _indexToLocFormat + ")"+
                "\n  glyphDataFormat:     " + _glyphDataFormat +
                "\n";
    }

}
