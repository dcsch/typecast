/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.ot.table;

import java.io.IOException;
import java.io.DataInput;

/**
 *
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: GposTable.java,v 1.1.1.1 2004-12-05 23:14:42 davidsch Exp $
 */
public class GposTable implements Table {

    private DirectoryEntry de;

    protected GposTable(DirectoryEntry de, DataInput di) throws IOException {
        this.de = (DirectoryEntry) de.clone();

        // GPOS Header
        int version = di.readInt();
        int scriptList = di.readInt();
        int featureList = di.readInt();
        int lookupList = di.readInt();
/*        
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

    /** Get the table type, as a table directory value.
     * @return The table type
     */
    public int getType() {
        return GPOS;
    }
    
    public String toString() {
        return "GPOS";
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
