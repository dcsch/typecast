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
import net.java.dev.typecast.ot.Disassembler;

/**
 * Font Program table
 * 
 * @see "https://docs.microsoft.com/en-us/typography/opentype/spec/fpgm"
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
class FpgmTable extends Program implements Table, Writable {

    protected FpgmTable(DataInput di, int length) throws IOException {
        readInstructions(di, length);
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        writeInstructionsContent(out);
    }

    @Override
    public int getType() {
        return fpgm;
    }

    public String toString() {
        return Disassembler.disassemble(getInstructions(), 0);
    }
    
}
