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
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class KernTable implements Table {
    
    private int version;
    private int nTables;
    private KernSubtable[] tables;

    /** Creates new KernTable */
    public KernTable(DataInput di) throws IOException {
        version = di.readUnsignedShort();
        nTables = di.readUnsignedShort();
        tables = new KernSubtable[nTables];
        for (int i = 0; i < nTables; i++) {
            tables[i] = KernSubtable.read(di);
        }
    }

    @Override
    public int getType() {
        return kern;
    }

    public int getSubtableCount() {
        return nTables;
    }
    
    public KernSubtable getSubtable(int i) {
        return tables[i];
    }

}
