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
 * @version $Id: SingleSubst.java,v 1.1.1.1 2004-12-05 23:14:58 davidsch Exp $
 */
public abstract class SingleSubst extends LookupSubtable {

    public abstract int getFormat();

    public abstract int substitute(int glyphId);
    
    public static SingleSubst read(RandomAccessFile raf, int offset) throws IOException {
        SingleSubst s = null;
        raf.seek(offset);
        int format = raf.readUnsignedShort();
        if (format == 1) {
            s = new SingleSubstFormat1(raf, offset);
        } else if (format == 2) {
            s = new SingleSubstFormat2(raf, offset);
        }
        return s;
    }

}

