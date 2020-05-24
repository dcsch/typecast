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

/**
 * Index to Location table
 * 
 * <p>
 * The indexToLoc table stores the offsets to the locations of the glyphs in the
 * font, relative to the beginning of the glyphData table. In order to compute
 * the length of the last glyph element, there is an extra entry after the last
 * valid index.
 * </p>
 * 
 * <p>
 * By definition, index zero points to the “missing character”, which is the
 * character that appears if a character is not found in the font. The missing
 * character is commonly represented by a blank box or a space. If the font does
 * not contain an outline for the missing character, then the first and second
 * offsets should have the same value. This also applies to any other characters
 * without an outline, such as the space character. If a glyph has no outline,
 * then loca[n] = loca[n+1]. In the particular case of the last glyph(s),
 * loca[n] will be equal the length of the glyph data ('glyf') table. The
 * offsets must be in ascending order with loca[n] <= loca[n+1].
 * </p>
 * 
 * <p>
 * Most routines will look at the 'maxp' table to determine the number of glyphs
 * in the font, but the value in the 'loca' table must agree.
 * </p>
 * 
 * <p>
 * There are two versions of this table: a short version, and a long version.
 * The version is specified in the indexToLocFormat entry in the 'head' table.
 * </p>
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class LocaTable implements Table, Writable {

    private int[] _offsets;
    private int _length;

    private static final Logger logger = LoggerFactory.getLogger(LocaTable.class);
    private HeadTable _head;

    public LocaTable(
            DataInput di,
            int length,
            HeadTable head,
            MaxpTable maxp) throws IOException {
        _head = head;
        _offsets = new int[maxp.getNumGlyphs() + 1];
        boolean shortEntries = head.useShortEntries();
        if (shortEntries) {
            for (int i = 0; i <= maxp.getNumGlyphs(); i++) {
                _offsets[i] = 2 * di.readUnsignedShort();
            }
        } else {
            for (int i = 0; i <= maxp.getNumGlyphs(); i++) {
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
        _length = length;
    }
    
    void updateFormat() {
        for (int offset : _offsets) {
            if (offset > 2 * 0xFFFFFF || offset % 2 != 0) {
                _head.setShortEntries(false);
                return;
            }
        }
        _head.setShortEntries(true);
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        boolean shortEntries = _head.useShortEntries();
        if (shortEntries) {
            for (int offset : _offsets) {
                out.writeShort(offset);
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
        sb.append("'loca' Table - Index To Location Table\n--------------------------------------\n")
            .append("Size = ").append(_length).append(" bytes, ")
            .append(_offsets.length).append(" entries\n");
        for (int i = 0; i < _offsets.length; i++) {
            sb.append("        Idx ").append(i)
                .append(" -> glyfOff 0x").append(getOffset(i)).append("\n");
        }
        return sb.toString();
    }
    
}
