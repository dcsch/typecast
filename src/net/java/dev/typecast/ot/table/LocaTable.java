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
 * @version $Id: LocaTable.java,v 1.1.1.1 2004-12-05 23:14:50 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class LocaTable implements Table {

    private DirectoryEntry de;
    private byte[] buf = null;
    private int[] offsets = null;
    private short factor = 0;

    protected LocaTable(DirectoryEntry de, DataInput di) throws IOException {
        this.de = (DirectoryEntry) de.clone();
        buf = new byte[de.getLength()];
        di.readFully(buf);
    }

    public void init(int numGlyphs, boolean shortEntries) {
        if (buf == null) {
            return;
        }
        offsets = new int[numGlyphs + 1];
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        if (shortEntries) {
            factor = 2;
            for (int i = 0; i <= numGlyphs; i++) {
                offsets[i] = (int)(bais.read()<<8 | bais.read());
            }
        } else {
            factor = 1;
            for (int i = 0; i <= numGlyphs; i++) {
                offsets[i] = (int)(bais.read()<<24 | bais.read()<<16 | bais.read()<<8 | bais.read());
            }
        }
        buf = null;
    }
    
    public int getOffset(int i) {
        if (offsets == null) {
            return 0;
        }
        return offsets[i] * factor;
    }

    public int getType() {
        return loca;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("'loca' Table - Index To Location Table\n--------------------------------------\n")
            .append("Size = ").append(0).append(" bytes, ")
            .append(offsets.length).append(" entries\n");
        for (int i = 0; i < offsets.length; i++) {
            sb.append("        Idx ").append(i)
                .append(" -> glyfOff 0x").append(getOffset(i)).append("\n");
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
