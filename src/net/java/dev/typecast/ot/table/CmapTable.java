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
import java.util.Arrays;

/**
 * @version $Id: CmapTable.java,v 1.1.1.1 2004-12-05 23:14:35 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class CmapTable implements Table {

    private DirectoryEntry de;
    private int version;
    private int numTables;
    private CmapIndexEntry[] entries;
//    private CmapFormat[] formats;

    protected CmapTable(DirectoryEntry de, DataInput di) throws IOException {
        this.de = (DirectoryEntry) de.clone();
        version = di.readUnsignedShort();
        numTables = di.readUnsignedShort();
        long bytesRead = 4;
        entries = new CmapIndexEntry[numTables];

        // Get each of the index entries
        for (int i = 0; i < numTables; i++) {
            entries[i] = new CmapIndexEntry(di);
            bytesRead += 8;
        }

        // Count the number of different encodings
//        int numEncodings = 0;
//        for (int i = 0; i < numTables; i++) {
//            numEncodings++;
//        }
//        formats = new CmapFormat[numEncodings];

        // Sort into their order of offset
        Arrays.sort(entries);

        // Get each of the tables
        int lastOffset = 0;
        CmapFormat lastFormat = null;
        for (int i = 0; i < numTables; i++) {
            if (entries[i].getOffset() == lastOffset) {

                // This is a multiple entry
                entries[i].setFormat(lastFormat);
                continue;
            } else if (entries[i].getOffset() > bytesRead) {
                di.skipBytes(entries[i].getOffset() - (int) bytesRead);
            } else if (entries[i].getOffset() != bytesRead) {
                
                // Something is amiss
                throw new IOException();
            }
            int formatType = di.readUnsignedShort();
            lastFormat = CmapFormat.create(formatType, di);
            lastOffset = entries[i].getOffset();
//            formats[i] = lastFormat;
            entries[i].setFormat(lastFormat);
            bytesRead += lastFormat.getLength();
        }
    }

    public int getVersion() {
        return version;
    }
    
    public int getNumTables() {
        return numTables;
    }
    
    public CmapIndexEntry getCmapIndexEntry(int i) {
        return entries[i];
    }
    
//    public CmapFormat getCmapFormat(int i) {
//        return formats[i];
//    }

    public CmapFormat getCmapFormat(short platformId, short encodingId) {

        // Find the requested format
        for (int i = 0; i < numTables; i++) {
            if (entries[i].getPlatformId() == platformId
                    && entries[i].getEncodingId() == encodingId) {
//                return formats[i];
                return entries[i].getFormat();
            }
        }
        return null;
    }

    public int getType() {
        return cmap;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer().append("cmap\n");

        // Get each of the index entries
        for (int i = 0; i < numTables; i++) {
            sb.append("\t").append(entries[i].toString()).append("\n");
        }

        // Get each of the tables
//        for (int i = 0; i < numTables; i++) {
//            sb.append("\t").append(formats[i].toString()).append("\n");
//        }
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
