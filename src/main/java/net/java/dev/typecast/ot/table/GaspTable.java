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
import java.util.ArrayList;

import net.java.dev.typecast.io.BinaryOutput;
import net.java.dev.typecast.io.Writable;

/**
 * gasp — Grid-fitting and Scan-conversion Procedure Table
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/gasp">Spec: Grid-fitting and Scan-conversion Procedure Table</a>
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class GaspTable implements Table, Writable {

    /**
     * Version 1 of {@link GaspTable}.
     */
    public static final int VERSION_1 = 1;

    private int version = VERSION_1;
    
    private final ArrayList<GaspRange> gaspRange = new ArrayList<>();
    
    @Override
    public void read(DataInput di, int length) throws IOException {
        version = di.readUnsignedShort();
        int numRanges = di.readUnsignedShort();
        gaspRange.ensureCapacity(numRanges);
        for (int i = 0; i < numRanges; i++) {
            gaspRange.add(new GaspRange(di));
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
     * Version number.
     */
    public int getVersion() {
        return version;
    }
    
    /**
     * Number of {@link GaspRange}s in this table.
     */
    public int getNumRanges() {
        return gaspRange.size();
    }

    /**
     * {@link GaspRange} with given index.
     * 
     * @see #getNumRanges()
     */
    public GaspRange getGaspRange(int index) {
        return gaspRange.get(index);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'gasp' Table - Grid-fitting And Scan-conversion Procedure\n---------------------------------------------------------");
        sb.append("\n  'gasp' version:      ").append(version);
        sb.append("\n  numRanges:           ").append(getNumRanges());
        for (int i = 0; i < getNumRanges(); i++) {
            sb.append("\n\n  gasp Range ").append(i).append("\n");
            sb.append(getGaspRange(i).toString());
        }
        return sb.toString();
    }

}
