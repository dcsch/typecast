/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.ot.table;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: LookupList.java,v 1.1.1.1 2004-12-05 23:14:50 davidsch Exp $
 */
public class LookupList {

    private int lookupCount;
    private int[] lookupOffsets;
    private Lookup[] lookups;

    /** Creates new LookupList */
    public LookupList(RandomAccessFile raf, int offset, LookupSubtableFactory factory)
    throws IOException {
        raf.seek(offset);
        lookupCount = raf.readUnsignedShort();
        lookupOffsets = new int[lookupCount];
        lookups = new Lookup[lookupCount];
        for (int i = 0; i < lookupCount; i++) {
            lookupOffsets[i] = raf.readUnsignedShort();
        }
        for (int i = 0; i < lookupCount; i++) {
            lookups[i] = new Lookup(factory, raf, offset + lookupOffsets[i]);
        }
    }

    public int getLookupCount() {
        return lookupCount;
    }
    
    public int getLookupOffset(int i) {
        return lookupOffsets[i];
    }
    
    public Lookup getLookup(int i) {
        return lookups[i];
    }

    public Lookup getLookup(Feature feature, int index) {
        if (feature.getLookupCount() > index) {
            int i = feature.getLookupListIndex(index);
            return lookups[i];
        }
        return null;
    }

}

