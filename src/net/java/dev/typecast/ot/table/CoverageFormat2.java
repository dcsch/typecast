/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.ot.table;

import java.io.*;

/**
 *
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: CoverageFormat2.java,v 1.1.1.1 2004-12-05 23:14:36 davidsch Exp $
 */
public class CoverageFormat2 extends Coverage {

    private int rangeCount;
    private RangeRecord[] rangeRecords;

    /** Creates new CoverageFormat2 */
    protected CoverageFormat2(RandomAccessFile raf) throws IOException {
        rangeCount = raf.readUnsignedShort();
        rangeRecords = new RangeRecord[rangeCount];
        for (int i = 0; i < rangeCount; i++) {
            rangeRecords[i] = new RangeRecord(raf);
        }
    }

    public int getFormat() {
        return 2;
    }

    public int findGlyph(int glyphId) {
        for (int i = 0; i < rangeCount; i++) {
            int n = rangeRecords[i].getCoverageIndex(glyphId);
            if (n > -1) {
                return n;
            }
        }
        return -1;
    }

}
