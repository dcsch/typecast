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
 * @version $Id: CmapFormat4.java,v 1.1.1.1 2004-12-05 23:14:34 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class CmapFormat4 extends CmapFormat {

    private int segCountX2;
    private int searchRange;
    private int entrySelector;
    private int rangeShift;
    private int[] endCode;
    private int[] startCode;
    private int[] idDelta;
    private int[] idRangeOffset;
    private int[] glyphIdArray;
    private int segCount;

    protected CmapFormat4(DataInput di) throws IOException {
        super(di); // 6
        format = 4;
        segCountX2 = di.readUnsignedShort(); // +2 (8)
        segCount = segCountX2 / 2;
        endCode = new int[segCount];
        startCode = new int[segCount];
        idDelta = new int[segCount];
        idRangeOffset = new int[segCount];
        searchRange = di.readUnsignedShort(); // +2 (10)
        entrySelector = di.readUnsignedShort(); // +2 (12)
        rangeShift = di.readUnsignedShort(); // +2 (14)
        for (int i = 0; i < segCount; i++) {
            endCode[i] = di.readUnsignedShort();
        } // + 2*segCount (2*segCount + 14)
        di.readUnsignedShort(); // reservePad  +2 (2*segCount + 16)
        for (int i = 0; i < segCount; i++) {
            startCode[i] = di.readUnsignedShort();
        } // + 2*segCount (4*segCount + 16)
        for (int i = 0; i < segCount; i++) {
            idDelta[i] = di.readUnsignedShort();
        } // + 2*segCount (6*segCount + 16)
        for (int i = 0; i < segCount; i++) {
            idRangeOffset[i] = di.readUnsignedShort();
        } // + 2*segCount (8*segCount + 16)

        // Whatever remains of this header belongs in glyphIdArray
//        int count = (length - 24) / 2;
        int count = (length - (8*segCount + 16)) / 2;
        glyphIdArray = new int[count];
        for (int i = 0; i < count; i++) {
            glyphIdArray[i] = di.readUnsignedShort();
        } // + 2*count (8*segCount + 2*count + 18)
        
        // Are there any padding bytes we need to consume?
//        int leftover = length - (8*segCount + 2*count + 18);
//        if (leftover > 0) {
//            di.skipBytes(leftover);
//        }
    }

    public int mapCharCode(int charCode) {
        try {
            for (int i = 0; i < segCount; i++) {
                if (endCode[i] >= charCode) {
                    if (startCode[i] <= charCode) {
                        if (idRangeOffset[i] > 0) {
                            return glyphIdArray[idRangeOffset[i]/2 + (charCode - startCode[i]) - (segCount - i)];
                        } else {
                            return (idDelta[i] + charCode) % 65536;
                        }
                    } else {
                        break;
                    }
                }
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("error: Array out of bounds - " + e.getMessage());
        }
        return 0;
    }

    public String toString() {
        return new StringBuffer()
        .append(super.toString())
        .append(", segCountX2: ")
        .append(segCountX2)
        .append(", searchRange: ")
        .append(searchRange)
        .append(", entrySelector: ")
        .append(entrySelector)
        .append(", rangeShift: ")
        .append(rangeShift)
        .append(", endCode: ")
        .append(endCode)
        .append(", startCode: ")
        .append(endCode)
        .append(", idDelta: ")
        .append(idDelta)
        .append(", idRangeOffset: ")
        .append(idRangeOffset).toString();
    }
}
