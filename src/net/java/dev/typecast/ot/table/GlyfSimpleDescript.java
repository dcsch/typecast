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
import net.java.dev.typecast.ot.Disassembler;

/**
 * @version $Id: GlyfSimpleDescript.java,v 1.1.1.1 2004-12-05 23:14:41 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class GlyfSimpleDescript extends GlyfDescript {

    private int[] endPtsOfContours;
    private byte[] flags;
    private short[] xCoordinates;
    private short[] yCoordinates;
    private int count;

    public GlyfSimpleDescript(GlyfTable parentTable, short numberOfContours, DataInput di)
    throws IOException {

        super(parentTable, numberOfContours, di);
        
        // Simple glyph description
        endPtsOfContours = new int[numberOfContours];
        for (int i = 0; i < numberOfContours; i++) {
//            endPtsOfContours[i] = (int)(bais.read()<<8 | bais.read());
            endPtsOfContours[i] = di.readShort();
        }

        // The last end point index reveals the total number of points
        count = endPtsOfContours[numberOfContours-1] + 1;
        flags = new byte[count];
        xCoordinates = new short[count];
        yCoordinates = new short[count];

//        int instructionCount = (int)(bais.read()<<8 | bais.read());
        int instructionCount = di.readShort();
        readInstructions(di, instructionCount);
        readFlags(count, di);
        readCoords(count, di);
    }

    public int getEndPtOfContours(int i) {
        return endPtsOfContours[i];
    }

    public byte getFlags(int i) {
        return flags[i];
    }

    public short getXCoordinate(int i) {
        return xCoordinates[i];
    }

    public short getYCoordinate(int i) {
        return yCoordinates[i];
    }

    public boolean isComposite() {
        return false;
    }

    public int getPointCount() {
        return count;
    }

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
     * The table is stored as relative values, but we'll store them as absolutes
     */
    private void readCoords(int count, DataInput di) throws IOException {
        short x = 0;
        short y = 0;
        for (int i = 0; i < count; i++) {
            if ((flags[i] & xDual) != 0) {
                if ((flags[i] & xShortVector) != 0) {
                    x += (short) di.readUnsignedByte();
                }
            } else {
                if ((flags[i] & xShortVector) != 0) {
                    x += (short) -((short) di.readUnsignedByte());
                } else {
                    x += di.readShort();
                }
            }
            xCoordinates[i] = x;
        }

        for (int i = 0; i < count; i++) {
            if ((flags[i] & yDual) != 0) {
                if ((flags[i] & yShortVector) != 0) {
                    y += (short) di.readUnsignedByte();
                }
            } else {
                if ((flags[i] & yShortVector) != 0) {
                    y += (short) -((short) di.readUnsignedByte());
                } else {
                    y += di.readShort();
                }
            }
            yCoordinates[i] = y;
        }
    }

    /**
     * The flags are run-length encoded
     */
    private void readFlags(int flagCount, DataInput di) throws IOException {
        try {
            for (int index = 0; index < flagCount; index++) {
                flags[index] = di.readByte();
                if ((flags[index] & repeat) != 0) {
                    int repeats = di.readByte();
                    for (int i = 1; i <= repeats; i++) {
                        flags[index + i] = flags[index];
                    }
                    index += repeats;
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("error: array index out of bounds");
        }
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(super.toString());
        sb.append("\n\n        EndPoints\n        ---------");
        for (int i = 0; i < endPtsOfContours.length; i++) {
            sb.append("\n          ").append(i).append(": ").append(endPtsOfContours[i]);
        }
        sb.append("\n\n          Length of Instructions: ");
        sb.append(getInstructions().length).append("\n");
        sb.append(Disassembler.disassemble(getInstructions(), 8));
        sb.append("\n        Flags\n        -----");
        for (int i = 0; i < flags.length; i++) {
            sb.append("\n          ").append(i).append(":  ");
            if ((flags[i] & 0x20) != 0) {
                sb.append("YDual ");
            } else {
                sb.append("      ");
            }
            if ((flags[i] & 0x10) != 0) {
                sb.append("XDual ");
            } else {
                sb.append("      ");
            }
            if ((flags[i] & 0x08) != 0) {
                sb.append("Repeat ");
            } else {
                sb.append("       ");
            }
            if ((flags[i] & 0x04) != 0) {
                sb.append("Y-Short ");
            } else {
                sb.append("        ");
            }
            if ((flags[i] & 0x02) != 0) {
                sb.append("X-Short ");
            } else {
                sb.append("        ");
            }
            if ((flags[i] & 0x01) != 0) {
                sb.append("On");
            } else {
                sb.append("  ");
            }
        }
        sb.append("\n\n        Coordinates\n        -----------");
        short oldX = 0;
        short oldY = 0;
        for (int i = 0; i < xCoordinates.length; i++) {
            sb.append("\n          ").append(i)
                .append(": Rel (").append(xCoordinates[i] - oldX)
                .append(", ").append(yCoordinates[i] - oldY)
                .append(")  ->  Abs (").append(xCoordinates[i])
                .append(", ").append(yCoordinates[i]).append(")");
            oldX = xCoordinates[i];
            oldY = yCoordinates[i];
        }
        return sb.toString();
    }
}
