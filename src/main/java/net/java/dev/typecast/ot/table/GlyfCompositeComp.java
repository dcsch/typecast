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
import net.java.dev.typecast.ot.Fixed_2_14;
import net.java.dev.typecast.ot.Fmt;

/**
 * A component of a {@link GlyfCompositeDescript}
 * 
 * @see GlyfCompositeDescript#getComponent(int)
 * @see "https://docs.microsoft.com/en-us/typography/opentype/spec/glyf"
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class GlyfCompositeComp implements Writable {

    /**
     * Bit 0: If this is set, the arguments are 16-bit (uint16 or int16);
     * otherwise, they are bytes (uint8 or int8).
     */
    private static final short ARG_1_AND_2_ARE_WORDS = 0x0001;
    
    /**
     * Bit 1: If this is set, the arguments are signed xy values; otherwise,
     * they are unsigned point numbers.
     */
    private static final short ARGS_ARE_XY_VALUES = 0x0002;
    
    /**
     * Bit 2: For the xy values if the preceding is true.
     */
    public static final short ROUND_XY_TO_GRID = 0x0004;
    
    /**
     * Bit 3: This indicates that there is a simple scale for the component.
     * Otherwise, scale = 1.0.
     */
    private static final short WE_HAVE_A_SCALE = 0x0008;
    
    /**
     * Bit 5: Indicates at least one more glyph after this one.
     */
    public static final short MORE_COMPONENTS = 0x0020;
    
    /**
     * Bit 6: The x direction will use a different scale from the y direction.
     */
    private static final short WE_HAVE_AN_X_AND_Y_SCALE = 0x0040;
    
    /**
     * Bit 7: There is a 2 by 2 transformation that will be used to scale the
     * component.
     */
    private static final short WE_HAVE_A_TWO_BY_TWO = 0x0080;
    
    /**
     * Bit 8: Following the last component are instructions for the composite character.
     */
    public static final short WE_HAVE_INSTRUCTIONS = 0x0100;
    
    /**
     * Bit 9: If set, this forces the aw and lsb (and rsb) for the composite to
     * be equal to those from this original glyph. This works for hinted and
     * unhinted characters.
     */
    public static final short USE_MY_METRICS = 0x0200;
    
    /**
     * Bit 10: If set, the components of the compound glyph overlap. Use of this
     * flag is not required in OpenType — that is, it is valid to have
     * components overlap without having this flag set. It may affect behaviors
     * in some platforms, however. (See Apple’s specification for details
     * regarding behavior in Apple platforms.) When used, it must be set on the
     * flag word for the first component. See additional remarks, above, for the
     * similar {@link GlyfDescript#OVERLAP_SIMPLE} flag used in simple-glyph
     * descriptions.
     */
    public static final short OVERLAP_COMPOUND = 0x0400;
    
    /**
     * Bit 11: The composite is designed to have the component offset scaled.
     * 
     * @see #UNSCALED_COMPONENT_OFFSET
     */
    public static final short SCALED_COMPONENT_OFFSET = 0x0800;
    
    /**
     * Bit 12: The composite is designed not to have the component offset
     * scaled.
     * 
     * @see #SCALED_COMPONENT_OFFSET
     */
    public static final short UNSCALED_COMPONENT_OFFSET = 0x1000;

    private final int _firstIndex;
    private final int _firstContour;
    
    /**
     * @see #getFlags()
     */
    private int _flags;
    
    /**
     * @see #getGlyphIndex()
     */
    private int _glyphIndex;
    
    /**
     * @see #getXScale()
     */
    private double _xscale = 1.0;

    /**
     * @see #getScale01()
     */
    private double _scale01 = 0.0;

    /**
     * @see #getScale10()
     */
    private double _scale10 = 0.0;

    /**
     * @see #getYScale()
     */
    private double _yscale = 1.0;
    
    /**
     * @see #getXTranslate()
     */
    private int _xtranslate = 0;
    
    /**
     * @see #getYTranslate()
     */
    private int _ytranslate = 0;
    
    /**
     * @see #getPoint1()
     */
    private int _point1 = 0;

    /**
     * @see #getPoint2()
     */
    private int _point2 = 0;

    GlyfCompositeComp(int firstIndex, int firstContour, DataInput di)
    throws IOException {
        _firstIndex = firstIndex;
        _firstContour = firstContour;
        
        _flags = di.readUnsignedShort();
        _glyphIndex = di.readUnsignedShort();

        // Argument1 and argument2 can be either x and y offsets to be added to
        // the glyph (the ARGS_ARE_XY_VALUES flag is set), or two point numbers
        // (the ARGS_ARE_XY_VALUES flag is not set). In the latter case, the
        // first point number indicates the point that is to be matched to the
        // new glyph. The second number indicates the new glyph’s “matched”
        // point. Once a glyph is added, its point numbers begin directly after
        // the last glyphs (endpoint of first glyph + 1).
        
        boolean wordArgs = (_flags & ARG_1_AND_2_ARE_WORDS) != 0;
        if ((_flags & ARGS_ARE_XY_VALUES) != 0) {
            // TODO:
            // When arguments 1 and 2 are an x and a y offset instead of points and
            // the bit ROUND_XY_TO_GRID is set to 1, the values are rounded to those
            // of the closest grid lines before they are added to the glyph. X and Y
            // offsets are described in FUnits.
            if (wordArgs) {
                _xtranslate = di.readShort();
                _ytranslate = di.readShort();
            } else {
                _xtranslate = di.readByte();
                _ytranslate = di.readByte();
            }
            
            _point1 = -1;
            _point2 = -1;
        } else {
            if (wordArgs) {
                _point1 = di.readUnsignedShort();
                _point2 = di.readUnsignedShort();
            } else {
                _point1 = di.readUnsignedByte();
                _point2 = di.readUnsignedByte();
            }
            
            _xtranslate = 0;
            _ytranslate = 0;
        }
        
        // Get the scale values (if any)
        if ((_flags & WE_HAVE_A_SCALE) != 0) {
            _xscale = _yscale = Fixed_2_14.read(di);
            _scale01 = 0.0;
            _scale10 = 0.0;
        } else if ((_flags & WE_HAVE_AN_X_AND_Y_SCALE) != 0) {
            _xscale = Fixed_2_14.read(di);
            _scale01 = 0.0;
            _scale10 = 0.0;
            _yscale = Fixed_2_14.read(di);
        } else if ((_flags & WE_HAVE_A_TWO_BY_TWO) != 0) {
            _xscale = Fixed_2_14.read(di);
            _scale01 = Fixed_2_14.read(di);
            _scale10 = Fixed_2_14.read(di);
            _yscale = Fixed_2_14.read(di);
        } else {
            _xscale = 1.0;
            _scale01 = 0.0;
            _scale10 = 0.0;
            _yscale = 1.0;
        }
    }
    
    void updateFlags() {
        int flags = Bits.clearMask(_flags, 
                ARGS_ARE_XY_VALUES | 
                ARG_1_AND_2_ARE_WORDS | 
                WE_HAVE_A_SCALE | 
                WE_HAVE_AN_X_AND_Y_SCALE | 
                WE_HAVE_A_TWO_BY_TWO);
        
        boolean xyValues = _point1 < 0;
        boolean useWords;
        if (xyValues) {
            flags = Bits.setMask(flags, ARGS_ARE_XY_VALUES);
            useWords = isSignedWord(_xtranslate) || isSignedWord(_ytranslate);
        } else {
            useWords = isUnsignedWord(_point1) || isUnsignedWord(_point2);
        }
        
        if (useWords) {
            flags = Bits.setMask(flags, ARG_1_AND_2_ARE_WORDS);
        }
        
        if (_scale01 == 0.0 && _scale10 == 0.0) {
            if (_xscale != 1.0 || _yscale != 1.0) {
                if (_xscale == _yscale) {
                    flags = Bits.setMask(flags, WE_HAVE_A_SCALE);
                } else {
                    flags = Bits.setMask(flags, WE_HAVE_AN_X_AND_Y_SCALE);
                }
            }
        } else {
            flags = Bits.setMask(flags, WE_HAVE_A_TWO_BY_TWO);
        }
        
        _flags = flags;
    }
    
    private static boolean isSignedWord(int value) {
        // TODO: The value -128 is not encoded as byte but as short in the Lato
        // font but the value range of int8 should be [-128, 127].
        return value < Byte.MIN_VALUE || value > Byte.MAX_VALUE;
    }

    private static boolean isUnsignedWord(int value) {
        return value > 0xFF;
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        out.writeShort(_flags);
        out.writeShort(_glyphIndex);

        boolean wordArgs = (_flags & ARG_1_AND_2_ARE_WORDS) != 0;
        if ((_flags & ARGS_ARE_XY_VALUES) != 0) {
            if (wordArgs) {
                out.writeShort(_xtranslate);
                out.writeShort(_ytranslate);
            } else {
                out.writeByte(_xtranslate);
                out.writeByte(_ytranslate);
            }
        } else {
            if (wordArgs) {
                out.writeShort(_point1);
                out.writeShort(_point2);
            } else {
                out.writeByte(_point1);
                out.writeByte(_point2);
            }
        }
        
        // Get the scale values (if any)
        if ((_flags & WE_HAVE_A_SCALE) != 0) {
            Fixed_2_14.write(out, _xscale);
        } else if ((_flags & WE_HAVE_AN_X_AND_Y_SCALE) != 0) {
            Fixed_2_14.write(out, _xscale);
            Fixed_2_14.write(out, _yscale);
        } else if ((_flags & WE_HAVE_A_TWO_BY_TWO) != 0) {
            Fixed_2_14.write(out, _xscale);
            Fixed_2_14.write(out, _scale01);
            Fixed_2_14.write(out, _scale10);
            Fixed_2_14.write(out, _yscale);
        }
    }

    public int getFirstIndex() {
        return _firstIndex;
    }

    public int getFirstContour() {
        return _firstContour;
    }

    /**
     * uint16   Component flags.
     * 
     * @see #ARG_1_AND_2_ARE_WORDS
     * @see #ARGS_ARE_XY_VALUES
     * @see #ROUND_XY_TO_GRID
     * @see #WE_HAVE_A_SCALE
     * @see #MORE_COMPONENTS
     * @see #WE_HAVE_AN_X_AND_Y_SCALE
     * @see #WE_HAVE_INSTRUCTIONS
     * @see #USE_MY_METRICS
     * @see #OVERLAP_COMPOUND
     * @see #SCALED_COMPONENT_OFFSET
     * @see #UNSCALED_COMPONENT_OFFSET
     */
    public int getFlags() {
        return _flags;
    }

    /** 
     * Whether this is not the last component in its glyph.
     */
    boolean hasMoreComponents() {
        return (getFlags() & GlyfCompositeComp.MORE_COMPONENTS) != 0;
    }

    /**
     * @see #hasMoreComponents()
     */
    void setMoreComponents(boolean value) {
        _flags = Bits.updateMask(_flags, GlyfCompositeComp.MORE_COMPONENTS, value);
    }

    /**
     * Whether the glyph of this component has hinting instructions (set on the
     * last component only).
     */
    boolean hasInstructions() {
        return (getFlags() & GlyfCompositeComp.WE_HAVE_INSTRUCTIONS) != 0;
    }

    /**
     * @see #hasInstructions()
     */
    void setInstructions(boolean value) {
        _flags = Bits.updateMask(_flags, GlyfCompositeComp.WE_HAVE_INSTRUCTIONS, value);
    }
    
    /**
     * uint16   Glyph index of this component.
     * 
     * @see #getReferencedGlyph(GlyfTable)
     */
    public int getGlyphIndex() {
        return _glyphIndex;
    }

    /**
     * Coordinate transformation.
     * 
     * <p>
     * The transformation matrix is:
     * </p>
     * 
     * <pre>
     * [ {@link #getXScale()}  {@link #getScale01()} {@link #getXTranslate()} ]
     * [ {@link #getScale10()} {@link #getYScale()}  {@link #getYTranslate()} ]
     * [ 0                     0                     1                        ]
     * </pre>
     */
    public double getXScale() {
        return _xscale;
    }

    /**
     * @see #getXScale()
     */
    public double getScale01() {
        return _scale01;
    }

    /**
     * @see #getXScale()
     */
    public double getScale10() {
        return _scale10;
    }

    /**
     * @see #getXScale()
     */
    public double getYScale() {
        return _yscale;
    }

    /**
     * @see #getXScale()
     */
    public int getXTranslate() {
        return _xtranslate;
    }

    /**
     * @see #getXScale()
     */
    public int getYTranslate() {
        return _ytranslate;
    }
    
    /**
     * The point that is to be matched to the new glyph.
     * 
     * @see #getPoint2()
     */
    public int getPoint1() {
        return _point1;
    }
    
    /**
     * The new glyph’s “matched” point. Once a glyph is added, its point numbers
     * begin directly after the last glyphs (endpoint of first glyph + 1).
     * 
     * @see #getPoint1()
     */
    public int getPoint2() {
        return _point2;
    }

    /**
     * Transforms an x-coordinate of a point for this component.
     * @param x The x-coordinate of the point to transform
     * @param y The y-coordinate of the point to transform
     * @return The transformed x-coordinate
     */
    public int scaleX(int x, int y) {
        return (int)(x * _xscale + y * _scale10);
    }

    /**
     * Transforms a y-coordinate of a point for this component.
     * @param x The x-coordinate of the point to transform
     * @param y The y-coordinate of the point to transform
     * @return The transformed y-coordinate
     */
    public int scaleY(int x, int y) {
        return (int)(x * _scale01 + y * _yscale);
    }

    /**
     * The transformed X coordinate of the given point.
     */
    public short transformX(int x, int y) {
        return (short) (scaleX(x, y) + getXTranslate());
    }

    /**
     * The transformed Y coordinate of the given point.
     */
    public short transformY(int x, int y) {
        return (short) (scaleY(x, y) + getYTranslate());
    }

    /**
     * The glyph referenced by this {@link GlyfCompositeComp}
     * 
     * @see #getGlyphIndex()
     * @see GlyfTable#getDescription(int)
     */
    public GlyfDescript getReferencedGlyph(GlyfTable table) {
        return table.getDescription(getGlyphIndex());
    }
    
    @Override
    public String toString() {
        return "            glyphIndex: " + getGlyphIndex() +
             "\n            firstIndex: " + getFirstIndex() +  
             "\n            firstContour: " + getFirstContour() +  
             "\n            flags: " + 
                (Bits.isSet(_flags, ARG_1_AND_2_ARE_WORDS) ? "ARG_1_AND_2_ARE_WORDS " : "") + 
                (Bits.isSet(_flags, ARGS_ARE_XY_VALUES) ? "ARGS_ARE_XY_VALUES " : "") + 
                (Bits.isSet(_flags, ROUND_XY_TO_GRID) ? "ROUND_XY_TO_GRID " : "") + 
                (Bits.isSet(_flags, WE_HAVE_A_SCALE) ? "WE_HAVE_A_SCALE " : "") + 
                (Bits.isSet(_flags, MORE_COMPONENTS) ? "MORE_COMPONENTS " : "") + 
                (Bits.isSet(_flags, WE_HAVE_AN_X_AND_Y_SCALE) ? "WE_HAVE_AN_X_AND_Y_SCALE " : "") + 
                (Bits.isSet(_flags, WE_HAVE_A_TWO_BY_TWO) ? "WE_HAVE_A_TWO_BY_TWO " : "") + 
                (Bits.isSet(_flags, WE_HAVE_INSTRUCTIONS) ? "WE_HAVE_INSTRUCTIONS " : "") + 
                (Bits.isSet(_flags, USE_MY_METRICS) ? "USE_MY_METRICS " : "") + 
                (Bits.isSet(_flags, OVERLAP_COMPOUND) ? "OVERLAP_COMPOUND " : "") + 
                (Bits.isSet(_flags, SCALED_COMPONENT_OFFSET) ? "SCALED_COMPONENT_OFFSET " : "") + 
                (Bits.isSet(_flags, UNSCALED_COMPONENT_OFFSET) ? "UNSCALED_COMPONENT_OFFSET" : "") +
                " (0x" + Fmt.padHex(4, _flags) + ")" + 
             "\n            transform:"+
             "\n                "+ getXScale() + "  " + getScale01() + "  " + getXTranslate() +
             "\n                "+ getScale10() + "  " + getYScale() + "  " + getYTranslate() + 
             "\n            point1: " + getPoint1() + 
             "\n            point2: " + getPoint2();
    }
}
