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
import java.io.DataInputStream;
import java.io.IOException;

import java.io.RandomAccessFile;

/**
 *
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: GsubTable.java,v 1.1.1.1 2004-12-05 23:14:43 davidsch Exp $
 */
public class GsubTable implements Table, LookupSubtableFactory {

    private DirectoryEntry de;
    private ScriptList scriptList;
    private FeatureList featureList;
    private LookupList lookupList;
    
    protected GsubTable(DirectoryEntry de, DataInput di) throws IOException {
        this.de = (DirectoryEntry) de.clone();
        
        // Load into a temporary buffer, and create another input stream
        byte[] buf = new byte[de.getLength()];
        di.readFully(buf);
        DataInput dis = new DataInputStream(new ByteArrayInputStream(buf));

        // GSUB Header
        int version = dis.readInt();
        int scriptListOffset = dis.readUnsignedShort();
        int featureListOffset = dis.readUnsignedShort();
        int lookupListOffset = dis.readUnsignedShort();

        // Script List
//        scriptList = new ScriptList(raf, de.getOffset() + scriptListOffset);

        // Feature List
//        featureList = new FeatureList(raf, de.getOffset() + featureListOffset);
        
        // Lookup List
//        lookupList = new LookupList(raf, de.getOffset() + lookupListOffset, this);
    }

    /**
     * 1 - Single - Replace one glyph with one glyph 
     * 2 - Multiple - Replace one glyph with more than one glyph 
     * 3 - Alternate - Replace one glyph with one of many glyphs 
     * 4 - Ligature - Replace multiple glyphs with one glyph 
     * 5 - Context - Replace one or more glyphs in context 
     * 6 - Chaining - Context Replace one or more glyphs in chained context
     */
    public LookupSubtable read(int type, RandomAccessFile raf, int offset)
    throws IOException {
        LookupSubtable s = null;
        switch (type) {
        case 1:
            s = SingleSubst.read(raf, offset);
            break;
        case 2:
//            s = MultipleSubst.read(raf, offset);
            break;
        case 3:
//            s = AlternateSubst.read(raf, offset);
            break;
        case 4:
            s = LigatureSubst.read(raf, offset);
            break;
        case 5:
//            s = ContextSubst.read(raf, offset);
            break;
        case 6:
//            s = ChainingSubst.read(raf, offset);
            break;
        }
        return s;
    }

    /** Get the table type, as a table directory value.
     * @return The table type
     */
    public int getType() {
        return GSUB;
    }

    public ScriptList getScriptList() {
        return scriptList;
    }

    public FeatureList getFeatureList() {
        return featureList;
    }

    public LookupList getLookupList() {
        return lookupList;
    }

    public String toString() {
        return "GSUB";
    }

    public static String lookupTypeAsString(int type) {
        switch (type) {
        case 1:
            return "Single";
        case 2:
            return "Multiple";
        case 3:
            return "Alternate";
        case 4:
            return "Ligature";
        case 5:
            return "Context";
        case 6:
            return "Chaining";
        }
        return "Unknown";
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
