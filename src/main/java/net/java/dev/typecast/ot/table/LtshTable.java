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
 * LTSH - Linear Threshold Table
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/ltsh">Spec: Linear Threshold</a>
 */
public class LtshTable implements Table {

    /**
     * Version 0 of {@link LtshTable}.
     */
    public static final int VERSION_0 = 0;
    
    private int version = VERSION_0;
    private int[] yPels;
    
    @Override
    public void read(DataInput di, int length) throws IOException {
        version = di.readUnsignedShort();
        
        // Must be equal to value in maxp table.
        int numGlyphs = di.readUnsignedShort();
        
        yPels = new int[numGlyphs];
        for (int i = 0; i < numGlyphs; i++) {
            yPels[i] = di.readUnsignedByte();
        }
    }

    @Override
    public int getType() {
        return LTSH;
    }
    
    /**
     * Version number.
     */
    public int getVersion() {
        return version;
    }
    
    public int getNumGlyphs() {
        return yPels.length;
    }
    
    /**
     * The vertical pel height at which the glyph can be assumed to scale
     * linearly.
     * 
     * @param index
     *        The glyph index.
     */
    public int getYPel(int index) {
        return yPels[index];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'LTSH' Table - Linear Threshold Table\n-------------------------------------")
            .append("\n 'LTSH' Version:       ").append(getVersion())
            .append("\n Number of Glyphs:     ").append(getNumGlyphs())
            .append("\n\n   Glyph #   Threshold\n   -------   ---------\n");
        for (int i = 0; i < getNumGlyphs(); i++) {
            sb.append("   ").append(i).append(".        ").append(yPels[i])
                .append("\n");
        }
        return sb.toString();
    }

}
