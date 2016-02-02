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
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public abstract class ClassDef {

    public abstract int getFormat();

    protected static ClassDef read(RandomAccessFile raf) throws IOException {
        ClassDef c = null;
        int format = raf.readUnsignedShort();
        if (format == 1) {
            c = new ClassDefFormat1(raf);
        } else if (format == 2) {
            c = new ClassDefFormat2(raf);
        }
        return c;
    }
}
