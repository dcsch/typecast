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
import net.java.dev.typecast.ot.Disassembler;
import net.java.dev.typecast.ot.Fmt;

/**
 * Simple Glyph Description
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/glyf">Spec: Simple Glyph Description</a>
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class GlyfSimpleDescript extends GlyfDescript {

    /**
     * @see #getEndPtOfContours(int)
     */
    private int[] _endPtsOfContours;
    
    /**
     * @see #getFlags(int)
     */
    private byte[] _flags;
    
    /**
     * @see #getXCoordinate(int)
     */
    private short[] _xCoordinates;
    
    /**
     * @see #getYCoordinate(int)
     */
    private short[] _yCoordinates;
    
    /**
     * Creates a {@link GlyfSimpleDescript}.
     *
     * @param parentTable
     *        The {@link GlyfTable} this instance belongs to.
     * @param glyphIndex
     *        See {@link #getGlyphIndex()}
     * @param numberOfContours
     *        See {@link #getNumberOfContours()}
     * @param di
     *        The reader to read from.
     */
    public GlyfSimpleDescript(
            GlyfTable parentTable,
            int glyphIndex,
            short numberOfContours,
            DataInput di) throws IOException {
        super(parentTable, glyphIndex, di);
        
        // Simple glyph description
        _endPtsOfContours = new int[numberOfContours];
        for (int i = 0; i < numberOfContours; i++) {
            _endPtsOfContours[i] = di.readUnsignedShort();
        }

        // The last end point index reveals the total number of points
        int count = _endPtsOfContours[numberOfContours-1] + 1;
        
        _flags = new byte[count];
        _xCoordinates = new short[count];
        _yCoordinates = new short[count];

        int instructionCount = di.readUnsignedShort();
        readInstructions(di, instructionCount);
        readFlags(count, di);
        readCoords(count, di);
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        super.write(out);
        
        for (int endPoint : _endPtsOfContours) {
            out.writeShort(endPoint);
        }
        
        writeInstructions(out);
        updateFlags();
        writeFlags(out);
        writeCoords(out);
    }

    @Override
    public int getNumberOfContours() {
        return _endPtsOfContours.length;
    }

    @Override
    public int getEndPtOfContours(int contour) {
        return _endPtsOfContours[contour];
    }

    @Override
    public byte getFlags(int i) {
        return _flags[i];
    }
    
    /**
     * Whether the point with the given index is an on-curve point.
     * 
     * @param i
     *        The point index.
     * @return Whether the point with the given index is a control point.
     * 
     * @see <a href="http://chanae.walon.org/pub/ttf/ttf_glyphs.htm">Glyph Hell:
     *      An introduction to glyphs, as used and defined in the FreeType
     *      engine</a>
     */
    public boolean isOnCurve(int i) {
        return (getFlags(i) & ON_CURVE_POINT) > 0;
    }

    @Override
    public short getXCoordinate(int i) {
        return _xCoordinates[i];
    }

    @Override
    public short getYCoordinate(int i) {
        return _yCoordinates[i];
    }

    @Override
    public boolean isComposite() {
        return false;
    }

    @Override
    public int getPointCount() {
        return _flags.length;
    }

    @Override
    public int getContourCount() {
        return getNumberOfContours();
    }
    /*
    public int getComponentIndex(int c) {
    return 0;
    }

    public int getComponentCount() {
    return 1;
    }
     */

    /**
     * Reads the glyph coordinates.
     * 
     * <p>
     * The table is stored as relative values, but we'll store them as absolutes
     * </p>
     */
    private void readCoords(int count, DataInput di) throws IOException {
        short x = 0;
        short y = 0;
        for (int i = 0; i < count; i++) {
            byte flag = _flags[i];
            if ((flag & X_IS_SAME_OR_POSITIVE_X_SHORT_VECTOR) != 0) {
                if ((flag & X_SHORT_VECTOR) != 0) {
                    x += (short) di.readUnsignedByte();
                }
            } else {
                if ((flag & X_SHORT_VECTOR) != 0) {
                    x -= (short) di.readUnsignedByte();
                } else {
                    x += di.readShort();
                }
            }
            _xCoordinates[i] = x;
        }

        for (int i = 0; i < count; i++) {
            if ((_flags[i] & Y_IS_SAME_OR_POSITIVE_Y_SHORT_VECTOR) != 0) {
                if ((_flags[i] & Y_SHORT_VECTOR) != 0) {
                    y += (short) di.readUnsignedByte();
                }
            } else {
                if ((_flags[i] & Y_SHORT_VECTOR) != 0) {
                    y -= (short) di.readUnsignedByte();
                } else {
                    y += di.readShort();
                }
            }
            _yCoordinates[i] = y;
        }
    }
    
    private void writeCoords(BinaryOutput out) throws IOException {
        int count = _flags.length;
        
        short lastX = 0;
        for (int i = 0; i < count; i++) {
            short x = _xCoordinates[i];
            byte flag = _flags[i];
            
            short dx = (short) (x - lastX);
            if ((flag & X_IS_SAME_OR_POSITIVE_X_SHORT_VECTOR) != 0) {
                if ((flag & X_SHORT_VECTOR) != 0) {
                    out.writeByte(dx);
                }
            } else {
                if ((flag & X_SHORT_VECTOR) != 0) {
                    out.writeByte(-dx);
                } else {
                    out.writeShort(dx);
                }
            }
            
            lastX = x;
        }
        
        short lastY = 0;
        for (int i = 0; i < count; i++) {
            short y = _yCoordinates[i];
            byte flag = _flags[i];
            
            short dy = (short) (y - lastY);
            if ((flag & Y_IS_SAME_OR_POSITIVE_Y_SHORT_VECTOR) != 0) {
                if ((flag & Y_SHORT_VECTOR) != 0) {
                    out.writeByte(dy);
                }
            } else {
                if ((flag & Y_SHORT_VECTOR) != 0) {
                    out.writeByte(-dy);
                } else {
                    out.writeShort(dy);
                }
            }

            lastY = y;
        }
    }
    
    /**
     * Reads the flags table.
     * 
     * <p>
     * Note: The flags are run-length encoded.
     * </p>
     * 
     * <p>
     * Each element in the flags array is a single byte, each of which has
     * multiple flag bits with distinct meanings, see {@link #getFlags(int)}.
     * </p>
     * 
     * @see #getFlags(int)
     */
    private void readFlags(int flagCount, DataInput di) throws IOException {
        try {
            for (int index = 0; index < flagCount; index++) {
                _flags[index] = di.readByte();
                if ((_flags[index] & REPEAT_FLAG) != 0) {
                    int repeats = di.readUnsignedByte();
                    for (int i = 1; i <= repeats; i++) {
                        _flags[index + i] = _flags[index];
                    }
                    index += repeats;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("error: array index out of bounds");
        }
    }
    
    /**
     * Writes the flags array reconstructing the repeat information.
     */
    private void writeFlags(BinaryOutput out) throws IOException {
        int flagCount = _flags.length;
        for (int index = 0; index < flagCount; index++) {
            byte nextFlag = _flags[index];
            
            int limit = Math.min(256, flagCount - index);
            int equalCnt = 1;
            while (equalCnt < limit && _flags[index + equalCnt] == nextFlag) {
                equalCnt++;
            }
            
            if (equalCnt > 1) {
                // The repeat count announces the number of identical flags following the first one.
                int repeatCnt = equalCnt - 1;
                
                out.writeByte(nextFlag | REPEAT_FLAG);
                out.writeByte(repeatCnt);
                index += repeatCnt;
            } else {
                out.writeByte(nextFlag);
            }
        }
    }
    
    private void updateFlags() {
        boolean overlap = (_flags[0] & OVERLAP_SIMPLE) != 0;
        
        int count = _flags.length;
        
        short lastX = 0;
        short lastY = 0;
        for (int i = 0; i < count; i++) {
            boolean onCurve = (_flags[i] & ON_CURVE_POINT) != 0;
            
            short x = _xCoordinates[i];
            short y = _yCoordinates[i];
            
            short dx = (short) (x - lastX);
            short dy = (short) (y - lastY);
            
            lastX = x;
            lastY = y;
            
            byte flags = 0;
            if (onCurve) {
                flags |= ON_CURVE_POINT;
            }
            
            boolean zeroX = dx == 0;
            if (zeroX) {
                flags |= X_IS_SAME_OR_POSITIVE_X_SHORT_VECTOR;
            } else {
                boolean xPositive = dx >= 0;
                short dxAbs = xPositive ? dx : (short) -dx;
    
                boolean shortX = dxAbs < 256;
                if (shortX) {
                    flags |= X_SHORT_VECTOR;
                    if (xPositive) {
                        flags |= X_IS_SAME_OR_POSITIVE_X_SHORT_VECTOR;
                    }
                }
            }
            
            boolean zeroY = dy == 0;
            if (zeroY) {
                flags |= Y_IS_SAME_OR_POSITIVE_Y_SHORT_VECTOR;
            } else {
                boolean yPositive = dy >= 0;
                short dyAbs = yPositive ? dy : (short) -dy;
    
                boolean shortY = dyAbs < 256;
                if (shortY) {
                    flags |= Y_SHORT_VECTOR;
                    if (yPositive) {
                        flags |= Y_IS_SAME_OR_POSITIVE_Y_SHORT_VECTOR;
                    }
                }
            }
            
            _flags[i] = flags;
        }
        
        if (overlap) {
            _flags[0] |= OVERLAP_SIMPLE;
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("        Simple Glyph\n");
        sb.append("        ------------\n");
        sb.append(super.toString());
        sb.append("\n");
        sb.append("          Overlap: " + ((_flags[0] & OVERLAP_SIMPLE) != 0) + "\n");
        sb.append("\n");
        
        sb.append("        EndPoints\n");
        sb.append("        ---------");
        for (int i = 0; i < _endPtsOfContours.length; i++) {
            sb.append("\n          ").append(i).append(": ").append(_endPtsOfContours[i]);
        }
        sb.append("\n\n");
        sb.append("        Instructions\n");
        sb.append("        ------------\n");
        sb.append("          length: ");
        sb.append(getInstructions().length).append("\n");
        sb.append(Disassembler.disassemble(getInstructions(), 10));
        sb.append("\n");
        sb.append("        Coordinates\n");
        sb.append("        -----------\n");
        short lastX = 0;
        short lastY = 0;
        for (int i = 0; i < _flags.length; i++) {
            sb.append("        ").append(Fmt.pad(3, i)).append(":  ");
            boolean xShort = (_flags[i] & X_SHORT_VECTOR) != 0;
            if (xShort) {
                sb.append("xShort ");
            } else {
                sb.append("       ");
            }
            boolean yShort = (_flags[i] & Y_SHORT_VECTOR) != 0;
            if (yShort) {
                sb.append("yShort ");
            } else {
                sb.append("       ");
            }
            boolean xDualOrPositive = (_flags[i] & X_IS_SAME_OR_POSITIVE_X_SHORT_VECTOR) != 0;
            if (xDualOrPositive) {
                if (xShort) {
                    sb.append("xPos  ");
                } else {
                    sb.append("xNull ");
                }
            } else {
                if (xShort) {
                    sb.append("xPos  ");
                } else {
                    sb.append("xNeg  ");
                }
            }
            boolean yDualOrPositive = (_flags[i] & Y_IS_SAME_OR_POSITIVE_Y_SHORT_VECTOR) != 0;
            if (yDualOrPositive) {
                if (yShort) {
                    sb.append("yPos  ");
                } else {
                    sb.append("yNull ");
                }
            } else {
                if (yShort) {
                    sb.append("yNeg  ");
                } else {
                    sb.append("      ");
                }
            }
            if ((_flags[i] & REPEAT_FLAG) != 0) {
                sb.append("Repeat ");
            } else {
                sb.append("       ");
            }
            if ((_flags[i] & ON_CURVE_POINT) != 0) {
                sb.append("OnCurve ");
            } else {
                sb.append("        ");
            }
            
            short x = _xCoordinates[i];
            short y = _yCoordinates[i];
            
            int dx = x - lastX;
            int dy = y - lastY;
            
            sb.append(": ");
            sb.append("Abs(" + Fmt.pad(5, x) + ", " + Fmt.pad(5, y) + ")");
            sb.append(" -> ");
            sb.append("Rel(" + Fmt.pad(5, dx) + ", " + Fmt.pad(5, dy) + ")");
            sb.append("\n");
            
            lastX = x;
            lastY = y;
        }
        return sb.toString();
    }
}
