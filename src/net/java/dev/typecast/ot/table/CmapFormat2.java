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
 * @version $Id: CmapFormat2.java,v 1.1.1.1 2004-12-05 23:14:34 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class CmapFormat2 extends CmapFormat {

    private short[] subHeaderKeys = new short[256];
    private int[] subHeaders1;
    private int[] subHeaders2;
    private short[] glyphIndexArray;

    protected CmapFormat2(DataInput di) throws IOException {
        super(di);
        format = 2;
    }

    public int mapCharCode(int charCode) {
        return 0;
    }
}
