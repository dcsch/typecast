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
 * @version $Id: NameTable.java,v 1.1.1.1 2004-12-05 23:14:52 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class NameTable implements Table {

    private DirectoryEntry de;
    private short formatSelector;
    private short numberOfNameRecords;
    private short stringStorageOffset;
    private NameRecord[] records;

    protected NameTable(DirectoryEntry de, DataInput di) throws IOException {
        this.de = (DirectoryEntry) de.clone();
        formatSelector = di.readShort();
        numberOfNameRecords = di.readShort();
        stringStorageOffset = di.readShort();
        records = new NameRecord[numberOfNameRecords];
        
        // Load the records, which contain the encoding information and string offsets
        for (int i = 0; i < numberOfNameRecords; i++) {
            records[i] = new NameRecord(di);
        }
        
        // Now load the strings
        for (int i = 0; i < numberOfNameRecords; i++) {
            records[i].loadString(di);
        }
    }

    public short getNumberOfNameRecords() {
        return numberOfNameRecords;
    }

    public NameRecord getRecord(int i) {
        return records[i];
    }

    public String getRecordString(short nameId) {

        // Search for the first instance of this name ID
        for (int i = 0; i < numberOfNameRecords; i++) {
            if (records[i].getNameId() == nameId) {
                return records[i].getRecordString();
            }
        }
        return "";
    }

    public int getType() {
        return name;
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
