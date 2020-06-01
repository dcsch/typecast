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
import net.java.dev.typecast.ot.Fmt;

/**
 * Control Value Table
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/cvt">Spec: Control Value Table</a>
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class CvtTable implements Table, Writable {

    private short[] values;

    @Override
    public void read(DataInput di, int length) throws IOException {
        int len = length / 2;
        values = new short[len];
        for (int i = 0; i < len; i++) {
            values[i] = di.readShort();
        }
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        for (short value : values) {
            out.writeShort(value);
        }
    }

    @Override
    public int getType() {
        return cvt;
    }

    public short[] getValues() {
        return values;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'cvt ' Table - Control Value Table\n");
        sb.append("----------------------------------\n");
        sb.append("    valueCnt = " + values.length + "\n");
        sb.append("    \n");
        
        sb.append("    Values\n");
        sb.append("    ------\n");
        for (int i = 0; i < values.length; i++) {
            sb.append("        " + Fmt.pad(3, i) + ": " + values[i] + "\n");
        }
        return sb.toString();
    }

}
