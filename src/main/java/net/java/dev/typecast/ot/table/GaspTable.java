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

import net.java.dev.typecast.io.BinaryOutput;
import net.java.dev.typecast.io.Writable;

/**
 * Grid-fitting and Scan-conversion Procedure Table
 * 
 * @see "https://docs.microsoft.com/en-us/typography/opentype/spec/gasp"
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class GaspTable implements Table, Writable {

    private int version;
    private GaspRange[] gaspRange;
    
    /**
     * Creates new GaspTable
     * 
     * @param length
     *        The total number of bytes.
     */
    public GaspTable(DataInput di, int length) throws IOException {
        version = di.readUnsignedShort();
        int numRanges = di.readUnsignedShort();
        gaspRange = new GaspRange[numRanges];
        for (int i = 0; i < numRanges; i++) {
            gaspRange[i] = new GaspRange(di);
        }
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        out.writeShort(version);
        int numRanges = getNumRanges();
        out.writeShort(numRanges);
        for (GaspRange range : gaspRange) {
            range.write(out);
        }
    }

    @Override
    public int getType() {
        return gasp;
    }
    
    /**
     * Number of {@link GaspRange}s in this table.
     */
    public int getNumRanges() {
        return gaspRange.length;
    }

    /**
     * {@link GaspRange} with given index.
     * 
     * @see #getNumRanges()
     */
    public GaspRange getGaspRange(int index) {
        return gaspRange[index];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'gasp' Table - Grid-fitting And Scan-conversion Procedure\n---------------------------------------------------------");
        sb.append("\n  'gasp' version:      ").append(version);
        sb.append("\n  numRanges:           ").append(getNumRanges());
        for (int i = 0; i < getNumRanges(); i++) {
            sb.append("\n\n  gasp Range ").append(i).append("\n");
            sb.append(gaspRange[i].toString());
        }
        return sb.toString();
    }

}
