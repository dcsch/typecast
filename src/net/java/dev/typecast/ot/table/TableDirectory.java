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
import net.java.dev.typecast.ot.Fixed;

/**
 * @version $Id: TableDirectory.java,v 1.1.1.1 2004-12-05 23:14:59 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class TableDirectory {

    private int version = 0;
    private short numTables = 0;
    private short searchRange = 0;
    private short entrySelector = 0;
    private short rangeShift = 0;
    private DirectoryEntry[] entries;

    public TableDirectory(DataInput di) throws IOException {
        version = di.readInt();
        numTables = di.readShort();
        searchRange = di.readShort();
        entrySelector = di.readShort();
        rangeShift = di.readShort();
        entries = new DirectoryEntry[numTables];
        for (int i = 0; i < numTables; i++) {
            entries[i] = new DirectoryEntry(di);
        }
/*
        // Sort them into file order (simple bubble sort)
        boolean modified = true;
        while (modified) {
            modified = false;
            for (int i = 0; i < numTables - 1; i++) {
                if (entries[i].getOffset() > entries[i+1].getOffset()) {
                    DirectoryEntry temp = entries[i];
                    entries[i] = entries[i+1];
                    entries[i+1] = temp;
                    modified = true;
                }
            }
        }
 */
    }

    public DirectoryEntry getEntry(int index) {
        return entries[index];
    }

    public DirectoryEntry getEntryByTag(int tag) {
        for (int i = 0; i < numTables; i++) {
            if (entries[i].getTag() == tag) {
                return entries[i];
            }
        }
        return null;
    }

    public short getEntrySelector() {
        return entrySelector;
    }

    public short getNumTables() {
        return numTables;
    }

    public short getRangeShift() {
        return rangeShift;
    }

    public short getSearchRange() {
        return searchRange;
    }

    public int getVersion() {
        return version;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer()
            .append("Offset Table\n------ -----")
            .append("\n  sfnt version:     ").append(Fixed.floatValue(version))
            .append("\n  numTables =       ").append(numTables)
            .append("\n  searchRange =     ").append(searchRange)
            .append("\n  entrySelector =   ").append(entrySelector)
            .append("\n  rangeShift =      ").append(rangeShift)
            .append("\n\n");
        for (int i = 0; i < numTables; i++) {
            sb.append(i).append(". ").append(entries[i].toString()).append("\n");
        }
        return sb.toString();
    }
}
