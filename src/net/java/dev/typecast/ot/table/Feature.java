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
 * @version $Id: Feature.java,v 1.1.1.1 2004-12-05 23:14:37 davidsch Exp $
 */
public class Feature {

    private int featureParams;
    private int lookupCount;
    private int[] lookupListIndex;

    /** Creates new Feature */
    protected Feature(RandomAccessFile raf, int offset) throws IOException {
        raf.seek(offset);
        featureParams = raf.readUnsignedShort();
        lookupCount = raf.readUnsignedShort();
        lookupListIndex = new int[lookupCount];
        for (int i = 0; i < lookupCount; i++) {
            lookupListIndex[i] = raf.readUnsignedShort();
        }
    }

    public int getLookupCount() {
        return lookupCount;
    }

    public int getLookupListIndex(int i) {
        return lookupListIndex[i];
    }

}
