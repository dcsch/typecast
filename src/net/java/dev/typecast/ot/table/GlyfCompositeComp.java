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

/**
 * @version $Id: GlyfCompositeComp.java,v 1.1.1.1 2004-12-05 23:14:39 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class GlyfCompositeComp {

    public static final short ARG_1_AND_2_ARE_WORDS = 0x0001;
    public static final short ARGS_ARE_XY_VALUES = 0x0002;
    public static final short ROUND_XY_TO_GRID = 0x0004;
    public static final short WE_HAVE_A_SCALE = 0x0008;
    public static final short MORE_COMPONENTS = 0x0020;
    public static final short WE_HAVE_AN_X_AND_Y_SCALE = 0x0040;
    public static final short WE_HAVE_A_TWO_BY_TWO = 0x0080;
    public static final short WE_HAVE_INSTRUCTIONS = 0x0100;
    public static final short USE_MY_METRICS = 0x0200;

    private int firstIndex;
    private int firstContour;
    private short argument1;
    private short argument2;
    private short flags;
    private short glyphIndex;
    private double xscale = 1.0;
    private double yscale = 1.0;
    private double scale01 = 0.0;
    private double scale10 = 0.0;
    private int xtranslate = 0;
    private int ytranslate = 0;
    private int point1 = 0;
    private int point2 = 0;

    protected GlyfCompositeComp(int firstIndex, int firstContour, DataInput di)
    throws IOException {
        this.firstIndex = firstIndex;
        this.firstContour = firstContour;
        flags = di.readShort();
        glyphIndex = di.readShort();

        // Get the arguments as just their raw values
        if ((flags & ARG_1_AND_2_ARE_WORDS) != 0) {
            argument1 = di.readShort();
            argument2 = di.readShort();
        } else {
            argument1 = (short) di.readByte();
            argument2 = (short) di.readByte();
        }

        // Assign the arguments according to the flags
        if ((flags & ARGS_ARE_XY_VALUES) != 0) {
            xtranslate = argument1;
            ytranslate = argument2;
        } else {
            point1 = argument1;
            point2 = argument2;
        }

        // Get the scale values (if any)
        if ((flags & WE_HAVE_A_SCALE) != 0) {
            int i = di.readShort();
            xscale = yscale = (double) i / (double) 0x4000;
        } else if ((flags & WE_HAVE_AN_X_AND_Y_SCALE) != 0) {
            short i = di.readShort();
            xscale = (double) i / (double) 0x4000;
            i = di.readShort();
            yscale = (double) i / (double) 0x4000;
        } else if ((flags & WE_HAVE_A_TWO_BY_TWO) != 0) {
            int i = di.readShort();
            xscale = (double) i / (double) 0x4000;
            i = di.readShort();
            scale01 = (double) i / (double) 0x4000;
            i = di.readShort();
            scale10 = (double) i / (double) 0x4000;
            i = di.readShort();
            yscale = (double) i / (double) 0x4000;
        }
    }

    public int getFirstIndex() {
        return firstIndex;
    }

    public int getFirstContour() {
        return firstContour;
    }

    public short getArgument1() {
        return argument1;
    }

    public short getArgument2() {
        return argument2;
    }

    public short getFlags() {
        return flags;
    }

    public short getGlyphIndex() {
        return glyphIndex;
    }

    public double getScale01() {
        return scale01;
    }

    public double getScale10() {
        return scale10;
    }

    public double getXScale() {
        return xscale;
    }

    public double getYScale() {
        return yscale;
    }

    public int getXTranslate() {
        return xtranslate;
    }

    public int getYTranslate() {
        return ytranslate;
    }

    /**
     * Transforms an x-coordinate of a point for this component.
     * @param x The x-coordinate of the point to transform
     * @param y The y-coordinate of the point to transform
     * @return The transformed x-coordinate
     */
    public int scaleX(int x, int y) {
        return (int)((double) x * xscale + (double) y * scale10);
    }

    /**
     * Transforms a y-coordinate of a point for this component.
     * @param x The x-coordinate of the point to transform
     * @param y The y-coordinate of the point to transform
     * @return The transformed y-coordinate
     */
    public int scaleY(int x, int y) {
        return (int)((double) x * scale01 + (double) y * yscale);
    }
}
