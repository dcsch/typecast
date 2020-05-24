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
 * GaspRange Record
 * 
 * @see GaspTable#getGaspRange(int)
 * @see "https://docs.microsoft.com/en-us/typography/opentype/spec/gasp#gasp-table-formats"
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
class GaspRange implements Writable {

    private static final int GASP_GRIDFIT = 1;
    private static final int GASP_DOGRAY = 2;
    
    private int rangeMaxPPEM;
    private int rangeGaspBehavior;
    
    /** Creates new GaspRange */
    GaspRange(DataInput di) throws IOException {
        rangeMaxPPEM = di.readUnsignedShort();
        rangeGaspBehavior = di.readUnsignedShort();
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        out.writeShort(rangeMaxPPEM);
        out.writeShort(rangeGaspBehavior);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  rangeMaxPPEM:        ").append(rangeMaxPPEM)
            .append("\n  rangeGaspBehavior:   0x").append(rangeGaspBehavior);
        if ((rangeGaspBehavior & GASP_GRIDFIT) != 0) {
            sb.append("- GASP_GRIDFIT ");
        }
        if ((rangeGaspBehavior & GASP_DOGRAY) != 0) {
            sb.append("- GASP_DOGRAY");
        }
        return sb.toString();
    }
}
