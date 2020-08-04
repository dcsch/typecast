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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.java.dev.typecast.io.BinaryOutput;
import net.java.dev.typecast.io.Writable;
import net.java.dev.typecast.ot.Fmt;

/**
 * loca — Index to Location Table
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/loca">Spec: Index to Location</a>
 */
public class LocaTable extends AbstractTable implements Writable {

    private int[] _offsets;

    private static final Logger logger = LoggerFactory.getLogger(LocaTable.class);

    /**
     * Creates a {@link LocaTable}.
     */
    public LocaTable(TableDirectory directory) {
        super(directory);
    }
    
    @Override
    public void read(DataInput di, int length) throws IOException {
        int numGlyphs = maxp().getNumGlyphs();
        _offsets = new int[numGlyphs + 1];
        boolean shortEntries = head().useShortEntries();
        if (shortEntries) {
            for (int i = 0; i <= numGlyphs; i++) {
                _offsets[i] = 2 * di.readUnsignedShort();
            }
        } else {
            for (int i = 0; i <= numGlyphs; i++) {
                _offsets[i] = di.readInt();
            }
        }
        
        // Check the validity of the offsets
        int lastOffset = 0;
        int index = 0;
        for (int offset : _offsets) {
            if (offset < lastOffset) {
                logger.error("Offset at index {} is bad ({} < {})", index, offset, lastOffset);
            }
            lastOffset = offset;
            ++index;
        }
    }
    
    void updateFormat() {
        HeadTable headTable = head();
        for (int offset : _offsets) {
            if (offset > 2 * 0xFFFF || offset % 2 != 0) {
                headTable.setShortEntries(false);
                return;
            }
        }
        headTable.setShortEntries(true);
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        boolean shortEntries = head().useShortEntries();
        if (shortEntries) {
            for (int offset : _offsets) {
                out.writeShort(offset / 2);
            }
        } else {
            for (int offset : _offsets) {
                out.writeInt(offset);
            }
        }
    }
    
    @Override
    public int getType() {
        return loca;
    }

    public int getOffset(int glyphId) {
        return _offsets[glyphId];
    }
    
    public void setOffset(int glyphId, int offset) {
        _offsets[glyphId] = offset;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'loca' Table - Index To Location Table\n");
        sb.append("--------------------------------------\n");
        sb.append("    entries = " + _offsets.length + "\n");
        for (int i = 0; i < _offsets.length; i++) {
            sb.append("    Index " + Fmt.pad(5, i) + " -> Offset " + Fmt.pad(7, getOffset(i)) + "\n");
        }
        return sb.toString();
    }
    
}
