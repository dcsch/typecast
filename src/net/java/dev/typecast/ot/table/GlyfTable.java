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

/**
 * @version $Id: GlyfTable.java,v 1.1.1.1 2004-12-05 23:14:42 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class GlyfTable implements Table {

    private DirectoryEntry de;
    private byte[] buf = null;
    private GlyfDescript[] descript;

    protected GlyfTable(DirectoryEntry de, DataInput di) throws IOException {
        this.de = (DirectoryEntry) de.clone();
        buf = new byte[de.getLength()];
        di.readFully(buf);
/*
        TableMaxp t_maxp = (TableMaxp) td.getEntryByTag(maxp).getTable();
        TableLoca t_loca = (TableLoca) td.getEntryByTag(loca).getTable();
        descript = new TableGlyfDescript[t_maxp.getNumGlyphs()];
        for (int i = 0; i < t_maxp.getNumGlyphs(); i++) {
            raf.seek(tde.getOffset() + t_loca.getOffset(i));
            int len = t_loca.getOffset((short)(i + 1)) - t_loca.getOffset(i);
            if (len > 0) {
                short numberOfContours = raf.readShort();
                if (numberOfContours < 0) {
                    //          descript[i] = new TableGlyfCompositeDescript(this, raf);
                } else {
                    descript[i] = new TableGlyfSimpleDescript(this, numberOfContours, raf);
                }
            } else {
                descript[i] = null;
            }
        }

        for (int i = 0; i < t_maxp.getNumGlyphs(); i++) {
            raf.seek(tde.getOffset() + t_loca.getOffset(i));
            int len = t_loca.getOffset((short)(i + 1)) - t_loca.getOffset(i);
            if (len > 0) {
                short numberOfContours = raf.readShort();
                if (numberOfContours < 0) {
                    descript[i] = new TableGlyfCompositeDescript(this, raf);
                }
            }
        }
*/
    }

    public void init(int numGlyphs, LocaTable loca) throws IOException {
        if (buf == null) {
            return;
        }
        descript = new GlyfDescript[numGlyphs];
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        for (int i = 0; i < numGlyphs; i++) {
            int len = loca.getOffset((short)(i + 1)) - loca.getOffset(i);
            if (len > 0) {
                bais.reset();
                bais.skip(loca.getOffset(i));
                DataInputStream dis = new DataInputStream(bais);
                short numberOfContours = dis.readShort();
                if (numberOfContours >= 0) {
                    descript[i] = new GlyfSimpleDescript(this, numberOfContours, dis);
                }
            } else {
                descript[i] = null;
            }
        }

        for (int i = 0; i < numGlyphs; i++) {
            int len = loca.getOffset((short)(i + 1)) - loca.getOffset(i);
            if (len > 0) {
                bais.reset();
                bais.skip(loca.getOffset(i));
                DataInputStream dis = new DataInputStream(bais);
                short numberOfContours = dis.readShort();
                if (numberOfContours < 0) {
                    descript[i] = new GlyfCompositeDescript(this, dis);
                }
            }
        }
        buf = null;
    }

    public GlyfDescript getDescription(int i) {
        return descript[i];
    }

    public int getType() {
        return glyf;
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
