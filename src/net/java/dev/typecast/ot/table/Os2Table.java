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
 * @version $Id: Os2Table.java,v 1.1.1.1 2004-12-05 23:14:54 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class Os2Table implements Table {

    private DirectoryEntry de;
    private int version;
    private short xAvgCharWidth;
    private int usWeightClass;
    private int usWidthClass;
    private short fsType;
    private short ySubscriptXSize;
    private short ySubscriptYSize;
    private short ySubscriptXOffset;
    private short ySubscriptYOffset;
    private short ySuperscriptXSize;
    private short ySuperscriptYSize;
    private short ySuperscriptXOffset;
    private short ySuperscriptYOffset;
    private short yStrikeoutSize;
    private short yStrikeoutPosition;
    private short sFamilyClass;
    private Panose panose;
    private int ulUnicodeRange1;
    private int ulUnicodeRange2;
    private int ulUnicodeRange3;
    private int ulUnicodeRange4;
    private int achVendorID;
    private short fsSelection;
    private int usFirstCharIndex;
    private int usLastCharIndex;
    private short sTypoAscender;
    private short sTypoDescender;
    private short sTypoLineGap;
    private int usWinAscent;
    private int usWinDescent;
    private int ulCodePageRange1;
    private int ulCodePageRange2;
    private short sxHeight;
    private short sCapHeight;
    private int usDefaultChar;
    private int usBreakChar;
    private int usMaxContext;

    protected Os2Table(DirectoryEntry de, DataInput di) throws IOException {
        this.de = (DirectoryEntry) de.clone();
        version = di.readUnsignedShort();
        xAvgCharWidth = di.readShort();
        usWeightClass = di.readUnsignedShort();
        usWidthClass = di.readUnsignedShort();
        fsType = di.readShort();
        ySubscriptXSize = di.readShort();
        ySubscriptYSize = di.readShort();
        ySubscriptXOffset = di.readShort();
        ySubscriptYOffset = di.readShort();
        ySuperscriptXSize = di.readShort();
        ySuperscriptYSize = di.readShort();
        ySuperscriptXOffset = di.readShort();
        ySuperscriptYOffset = di.readShort();
        yStrikeoutSize = di.readShort();
        yStrikeoutPosition = di.readShort();
        sFamilyClass = di.readShort();
        byte[] buf = new byte[10];
        di.readFully(buf);
        panose = new Panose(buf);
        ulUnicodeRange1 = di.readInt();
        ulUnicodeRange2 = di.readInt();
        ulUnicodeRange3 = di.readInt();
        ulUnicodeRange4 = di.readInt();
        achVendorID = di.readInt();
        fsSelection = di.readShort();
        usFirstCharIndex = di.readUnsignedShort();
        usLastCharIndex = di.readUnsignedShort();
        sTypoAscender = di.readShort();
        sTypoDescender = di.readShort();
        sTypoLineGap = di.readShort();
        usWinAscent = di.readUnsignedShort();
        usWinDescent = di.readUnsignedShort();
        ulCodePageRange1 = di.readInt();
        ulCodePageRange2 = di.readInt();
        
        // OpenType 1.3
        if (version == 2) {
            sxHeight = di.readShort();
            sCapHeight = di.readShort();
            usDefaultChar = di.readUnsignedShort();
            usBreakChar = di.readUnsignedShort();
            usMaxContext = di.readUnsignedShort();
        }
    }

    public int getVersion() {
        return version;
    }

    public short getAvgCharWidth() {
        return xAvgCharWidth;
    }

    public int getWeightClass() {
        return usWeightClass;
    }

    public int getWidthClass() {
        return usWidthClass;
    }

    public short getLicenseType() {
        return fsType;
    }

    public short getSubscriptXSize() {
        return ySubscriptXSize;
    }

    public short getSubscriptYSize() {
        return ySubscriptYSize;
    }

    public short getSubscriptXOffset() {
        return ySubscriptXOffset;
    }

    public short getSubscriptYOffset() {
        return ySubscriptYOffset;
    }

    public short getSuperscriptXSize() {
        return ySuperscriptXSize;
    }

    public short getSuperscriptYSize() {
        return ySuperscriptYSize;
    }

    public short getSuperscriptXOffset() {
        return ySuperscriptXOffset;
    }

    public short getSuperscriptYOffset() {
        return ySuperscriptYOffset;
    }

    public short getStrikeoutSize() {
        return yStrikeoutSize;
    }

    public short getStrikeoutPosition() {
        return yStrikeoutPosition;
    }

    public short getFamilyClass() {
        return sFamilyClass;
    }

    public Panose getPanose() {
        return panose;
    }

    public int getUnicodeRange1() {
        return ulUnicodeRange1;
    }

    public int getUnicodeRange2() {
        return ulUnicodeRange2;
    }

    public int getUnicodeRange3() {
        return ulUnicodeRange3;
    }

    public int getUnicodeRange4() {
        return ulUnicodeRange4;
    }

    public int getVendorID() {
        return achVendorID;
    }

    public short getSelection() {
        return fsSelection;
    }

    public int getFirstCharIndex() {
        return usFirstCharIndex;
    }

    public int getLastCharIndex() {
        return usLastCharIndex;
    }

    public short getTypoAscender() {
        return sTypoAscender;
    }

    public short getTypoDescender() {
        return sTypoDescender;
    }

    public short getTypoLineGap() {
        return sTypoLineGap;
    }

    public int getWinAscent() {
        return usWinAscent;
    }

    public int getWinDescent() {
        return usWinDescent;
    }

    public int getCodePageRange1() {
        return ulCodePageRange1;
    }

    public int getCodePageRange2() {
        return ulCodePageRange2;
    }

    public short getXHeight() {
        return sxHeight;
    }
    
    public short getCapHeight() {
        return sCapHeight;
    }
    
    public int getDefaultChar() {
        return usDefaultChar;
    }
    
    public int getBreakChar() {
        return usBreakChar;
    }
    
    public int getMaxContext() {
        return usMaxContext;
    }

    public int getType() {
        return OS_2;
    }

    public String toString() {
        return new StringBuffer()
            .append("'OS/2' Table - OS/2 and Windows Metrics\n---------------------------------------")
            .append("\n  'OS/2' version:      ").append(version)
            .append("\n  xAvgCharWidth:       ").append(xAvgCharWidth)
            .append("\n  usWeightClass:       ").append(usWeightClass)
            .append("\n  usWidthClass:        ").append(usWidthClass)
            .append("\n  fsType:              0x").append(Integer.toHexString(fsType).toUpperCase())
            .append("\n  ySubscriptXSize:     ").append(ySubscriptXSize)
            .append("\n  ySubscriptYSize:     ").append(ySubscriptYSize)
            .append("\n  ySubscriptXOffset:   ").append(ySubscriptXOffset)
            .append("\n  ySubscriptYOffset:   ").append(ySubscriptYOffset)
            .append("\n  ySuperscriptXSize:   ").append(ySuperscriptXSize)
            .append("\n  ySuperscriptYSize:   ").append(ySuperscriptYSize)
            .append("\n  ySuperscriptXOffset: ").append(ySuperscriptXOffset)
            .append("\n  ySuperscriptYOffset: ").append(ySuperscriptYOffset)
            .append("\n  yStrikeoutSize:      ").append(yStrikeoutSize)
            .append("\n  yStrikeoutPosition:  ").append(yStrikeoutPosition)
            .append("\n  sFamilyClass:        ").append(sFamilyClass>>8)
            .append("    subclass = ").append(sFamilyClass&0xff)
            .append("\n  PANOSE:              ").append(panose.toString())
            .append("\n  Unicode Range 1( Bits 0 - 31 ): ").append(Integer.toHexString(ulUnicodeRange1).toUpperCase())
            .append("\n  Unicode Range 2( Bits 32- 63 ): ").append(Integer.toHexString(ulUnicodeRange2).toUpperCase())
            .append("\n  Unicode Range 3( Bits 64- 95 ): ").append(Integer.toHexString(ulUnicodeRange3).toUpperCase())
            .append("\n  Unicode Range 4( Bits 96-127 ): ").append(Integer.toHexString(ulUnicodeRange4).toUpperCase())
            .append("\n  achVendID:           '").append(getVendorIDAsString())
            .append("'\n  fsSelection:         0x").append(Integer.toHexString(fsSelection).toUpperCase())
            .append("\n  usFirstCharIndex:    0x").append(Integer.toHexString(usFirstCharIndex).toUpperCase())
            .append("\n  usLastCharIndex:     0x").append(Integer.toHexString(usLastCharIndex).toUpperCase())
            .append("\n  sTypoAscender:       ").append(sTypoAscender)
            .append("\n  sTypoDescender:      ").append(sTypoDescender)
            .append("\n  sTypoLineGap:        ").append(sTypoLineGap)
            .append("\n  usWinAscent:         ").append(usWinAscent)
            .append("\n  usWinDescent:        ").append(usWinDescent)
            .append("\n  CodePage Range 1( Bits 0 - 31 ): ").append(Integer.toHexString(ulCodePageRange1).toUpperCase())
            .append("\n  CodePage Range 2( Bits 32- 63 ): ").append(Integer.toHexString(ulCodePageRange2).toUpperCase())
            .toString();
    }
    
    private String getVendorIDAsString() {
        return new StringBuffer()
            .append((char)((achVendorID>>24)&0xff))
            .append((char)((achVendorID>>16)&0xff))
            .append((char)((achVendorID>>8)&0xff))
            .append((char)((achVendorID)&0xff))
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
