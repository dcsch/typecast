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
 * @version $Id: GlyfDescript.java,v 1.1.1.1 2004-12-05 23:14:40 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public abstract class GlyfDescript extends Program implements GlyphDescription {

    // flags
    public static final byte onCurve = 0x01;
    public static final byte xShortVector = 0x02;
    public static final byte yShortVector = 0x04;
    public static final byte repeat = 0x08;
    public static final byte xDual = 0x10;
    public static final byte yDual = 0x20;

    protected GlyfTable parentTable;
    private int numberOfContours;
    private short xMin;
    private short yMin;
    private short xMax;
    private short yMax;

    protected GlyfDescript(GlyfTable parentTable, short numberOfContours, DataInput di)
    throws IOException {
        this.parentTable = parentTable;
        this.numberOfContours = numberOfContours;
//        xMin = (short)(di.readByte()<<8 | di.readByte());
//        yMin = (short)(di.readByte()<<8 | di.readByte());
//        xMax = (short)(di.readByte()<<8 | di.readByte());
//        yMax = (short)(di.readByte()<<8 | di.readByte());
        xMin = di.readShort();
        yMin = di.readShort();
        xMax = di.readShort();
        yMax = di.readShort();
    }

    public int getNumberOfContours() {
        return numberOfContours;
    }

    public short getXMaximum() {
        return xMax;
    }

    public short getXMinimum() {
        return xMin;
    }

    public short getYMaximum() {
        return yMax;
    }

    public short getYMinimum() {
        return yMin;
    }
    
    public String toString() {
        return new StringBuffer()
            .append("          numberOfContours: ").append(numberOfContours)
            .append("\n          xMin:             ").append(xMin)
            .append("\n          yMin:             ").append(yMin)
            .append("\n          xMax:             ").append(xMax)
            .append("\n          yMax:             ").append(yMax)
            .toString();
    }
}
