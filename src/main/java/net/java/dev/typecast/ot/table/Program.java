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

/**
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
abstract class Program {

    private short[] instructions;
    
    /**
     * uint16 instructionLength Total number of bytes for instructions. If
     * instructionLength is zero, no instructions are present for this glyph,
     * and this field is followed directly by the flags field.
     */
    public int getInstructionLength() {
        return instructions == null ? 0 : instructions.length;
    }
    
    /**
     * The instruction code at position n.
     * 
     * @see #getInstructionLength()
     */
    public short getInstruction(int n) {
        return instructions[n];
    }

    /**
     * uint8 instructions[{@link #getInstructionLength()}] Array of instruction
     * byte code for the glyph.
     */
    public short[] getInstructions() {
        return instructions;
    }

    /**
     * Reads the instructions array.
     * 
     * @see #getInstructions()
     */
    void readInstructions(DataInput di, int count) throws IOException {
        instructions = new short[count];
        for (int i = 0; i < count; i++) {
            instructions[i] = (short) di.readUnsignedByte();
        }
    }
    
    void writeInstructions(BinaryOutput out) throws IOException {
        out.writeShort(getInstructionLength());
        writeInstructionsContent(out);
    }

    void writeInstructionsContent(BinaryOutput out) throws IOException {
        for (int n = 0, cnt = getInstructionLength(); n < cnt; n++) {
            out.writeByte(getInstruction(n));
        }
    }
    
/*
    protected void readInstructions(ByteArrayInputStream bais, int count) {
        instructions = new short[count];
        for (int i = 0; i < count; i++) {
            instructions[i] = (short) bais.read();
        }
    }
*/
}
