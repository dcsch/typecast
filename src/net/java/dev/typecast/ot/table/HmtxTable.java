/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.ot.table;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.IOException;

/**
 * @version $Id: HmtxTable.java,v 1.1.1.1 2004-12-05 23:14:45 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class HmtxTable implements Table {

    private DirectoryEntry de;
    private byte[] buf = null;
    private int[] hMetrics = null;
    private short[] leftSideBearing = null;

    protected HmtxTable(DirectoryEntry de, DataInput di) throws IOException {
        this.de = (DirectoryEntry) de.clone();
        buf = new byte[de.getLength()];
        di.readFully(buf);
    }

    public void init(int numberOfHMetrics, int lsbCount) {
        if (buf == null) {
            return;
        }
        hMetrics = new int[numberOfHMetrics];
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        for (int i = 0; i < numberOfHMetrics; i++) {
            hMetrics[i] = (int)(bais.read()<<24 | bais.read()<<16 | bais.read()<<8 | bais.read());
        }
        if (lsbCount > 0) {
            leftSideBearing = new short[lsbCount];
            for (int i = 0; i < lsbCount; i++) {
                leftSideBearing[i] = (short)(bais.read()<<8 | bais.read());
            }
        }
        buf = null;
    }

    public int getAdvanceWidth(int i) {
        if (hMetrics == null) {
            return 0;
        }
        if (i < hMetrics.length) {
            return hMetrics[i] >> 16;
        } else {
            return hMetrics[hMetrics.length - 1] >> 16;
        }
    }

    public short getLeftSideBearing(int i) {
        if (hMetrics == null) {
            return 0;
        }
        if (i < hMetrics.length) {
            return (short)(hMetrics[i] & 0xffff);
        } else {
            return leftSideBearing[i - hMetrics.length];
        }
    }

    public int getType() {
        return hmtx;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("'hmtx' Table - Horizontal Metrics\n---------------------------------\n");
        sb.append("Size = ").append(0).append(" bytes, ").append(hMetrics.length).append(" entries\n");
        for (int i = 0; i < hMetrics.length; i++) {
            sb.append("        ").append(i)
                .append(". advWid: ").append(getAdvanceWidth(i))
                .append(", LSdBear: ").append(getLeftSideBearing(i))
                .append("\n");
        }
        for (int i = 0; i < leftSideBearing.length; i++) {
            sb.append("        LSdBear ").append(i + hMetrics.length)
                .append(": ").append(leftSideBearing[i])
                .append("\n");
        }
        return sb.toString();
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
