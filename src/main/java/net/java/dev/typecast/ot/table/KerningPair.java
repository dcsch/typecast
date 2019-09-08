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
public class KerningPair {

    private int left;
    private int right;
    private short value;

    /** Creates new KerningPair */
    protected KerningPair(DataInput di) throws IOException {
        left = di.readUnsignedShort();
        right = di.readUnsignedShort();
        value = di.readShort();
    }

    public int getLeft() {
        return left;
    }

    public int getRight() {
        return right;
    }

    public short getValue() {
        return value;
    }

}
