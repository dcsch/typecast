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
import java.util.ArrayList;

/**
 * kern - Kerning
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/kern">Spec: Kerning</a>
 */
public class KernTable implements Table {
    
    /**
     * Version 0 of {@link KernTable}.
     */
    private static final int VERSION_0 = 0;
    
    private int _version = VERSION_0;
    private final ArrayList<KernSubtable> _tables = new ArrayList<>();

    @Override
    public void read(DataInput di, int length) throws IOException {
        _version = di.readUnsignedShort();
        int nTables = di.readUnsignedShort();
        _tables.ensureCapacity(nTables);
        for (int i = 0; i < nTables; i++) {
            _tables.add(KernSubtable.read(di));
        }
    }

    @Override
    public int getType() {
        return kern;
    }
    
    /**
     * Table version number.
     */
    public int getVersion() {
        return _version;
    }

    /**
     * Number of {@link KernSubtable}s.
     * 
     * @see #getSubtable(int)
     */
    public int getSubtableCount() {
        return _tables.size();
    }
    
    /**
     * {@link KernSubtable} with given index.
     * 
     * @see #getSubtableCount()
     */
    public KernSubtable getSubtable(int i) {
        return _tables.get(i);
    }

}
