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
 * @version $Id: KernSubtableFormat2.java,v 1.1.1.1 2004-12-05 23:14:48 davidsch Exp $
 */
public class KernSubtableFormat2 extends KernSubtable {

    private int rowWidth;
    private int leftClassTable;
    private int rightClassTable;
    private int array;

    /** Creates new KernSubtableFormat2 */
    protected KernSubtableFormat2(DataInput di) throws IOException {
        rowWidth = di.readUnsignedShort();
        leftClassTable = di.readUnsignedShort();
        rightClassTable = di.readUnsignedShort();
        array = di.readUnsignedShort();
    }

    public int getKerningPairCount() {
        return 0;
    }

    public KerningPair getKerningPair(int i) {
        return null;
    }

}
