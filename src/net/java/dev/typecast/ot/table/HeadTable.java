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
import net.java.dev.typecast.ot.Fixed;

/**
 * @version $Id: HeadTable.java,v 1.1.1.1 2004-12-05 23:14:44 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class HeadTable implements Table {

    private DirectoryEntry de;
    private int versionNumber;
    private int fontRevision;
    private int checkSumAdjustment;
    private int magicNumber;
    private short flags;
    private short unitsPerEm;
    private long created;
    private long modified;
    private short xMin;
    private short yMin;
    private short xMax;
    private short yMax;
    private short macStyle;
    private short lowestRecPPEM;
    private short fontDirectionHint;
    private short indexToLocFormat;
    private short glyphDataFormat;

    protected HeadTable(DirectoryEntry de, DataInput di) throws IOException {
        this.de = (DirectoryEntry) de.clone();
        versionNumber = di.readInt();
        fontRevision = di.readInt();
        checkSumAdjustment = di.readInt();
        magicNumber = di.readInt();
        flags = di.readShort();
        unitsPerEm = di.readShort();
        created = di.readLong();
        modified = di.readLong();
        xMin = di.readShort();
        yMin = di.readShort();
        xMax = di.readShort();
        yMax = di.readShort();
        macStyle = di.readShort();
        lowestRecPPEM = di.readShort();
        fontDirectionHint = di.readShort();
        indexToLocFormat = di.readShort();
        glyphDataFormat = di.readShort();
    }

    public int getCheckSumAdjustment() {
        return checkSumAdjustment;
    }

    public long getCreated() {
        return created;
    }

    public short getFlags() {
        return flags;
    }

    public short getFontDirectionHint() {
        return fontDirectionHint;
    }

    public int getFontRevision(){
        return fontRevision;
    }

    public short getGlyphDataFormat() {
        return glyphDataFormat;
    }

    public short getIndexToLocFormat() {
        return indexToLocFormat;
    }

    public short getLowestRecPPEM() {
        return lowestRecPPEM;
    }

    public short getMacStyle() {
        return macStyle;
    }

    public long getModified() {
        return modified;
    }

    public int getType() {
        return head;
    }

    public short getUnitsPerEm() {
        return unitsPerEm;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public short getXMax() {
        return xMax;
    }

    public short getXMin() {
        return xMin;
    }

    public short getYMax() {
        return yMax;
    }

    public short getYMin() {
        return yMin;
    }

    public String toString() {
        return new StringBuffer()
            .append("'head' Table - Font Header\n--------------------------")
            .append("\n  'head' version:      ").append(Fixed.floatValue(versionNumber))
            .append("\n  fontRevision:        ").append(Fixed.roundedFloatValue(fontRevision, 8))
            .append("\n  checkSumAdjustment:  0x").append(Integer.toHexString(checkSumAdjustment).toUpperCase())
            .append("\n  magicNumber:         0x").append(Integer.toHexString(magicNumber).toUpperCase())
            .append("\n  flags:               0x").append(Integer.toHexString(flags).toUpperCase())
            .append("\n  unitsPerEm:          ").append(unitsPerEm)
            .append("\n  created:             ").append(created)
            .append("\n  modified:            ").append(modified)
            .append("\n  xMin:                ").append(xMin)
            .append("\n  yMin:                ").append(yMin)
            .append("\n  xMax:                ").append(xMax)
            .append("\n  yMax:                ").append(yMax)
            .append("\n  macStyle bits:       ").append(Integer.toHexString(macStyle).toUpperCase())
            .append("\n  lowestRecPPEM:       ").append(lowestRecPPEM)
            .append("\n  fontDirectionHint:   ").append(fontDirectionHint)
            .append("\n  indexToLocFormat:    ").append(indexToLocFormat)
            .append("\n  glyphDataFormat:     ").append(glyphDataFormat)
            .toString();
    }
    
    /**
     * Get a directory entry for this table.  This uniquely identifies the
     * table in collections where there may be more than one instance of a
     * particular table.
     * @return A directory entry
     */
    public DirectoryEntry getDirectoryEntry() {
        return de;
    }
    
}
