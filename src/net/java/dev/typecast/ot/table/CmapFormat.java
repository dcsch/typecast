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
 * @version $Id: CmapFormat.java,v 1.1.1.1 2004-12-05 23:14:34 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public abstract class CmapFormat {

    protected int format;
    protected int length;
    protected int version;

    protected CmapFormat(DataInput di) throws IOException {
        length = di.readUnsignedShort();
        version = di.readUnsignedShort();
    }

    protected static CmapFormat create(int format, DataInput di)
    throws IOException {
        switch(format) {
            case 0:
            return new CmapFormat0(di);
            case 2:
            return new CmapFormat2(di);
            case 4:
            return new CmapFormat4(di);
            case 6:
            return new CmapFormat6(di);
        }
        return null;
    }

    public int getFormat() {
        return format;
    }

    public int getLength() {
        return length;
    }

    public int getVersion() {
        return version;
    }

    public abstract int mapCharCode(int charCode);

    public String toString() {
        return new StringBuffer()
        .append("format: ")
        .append(format)
        .append(", length: ")
        .append(length)
        .append(", version: ")
        .append(version).toString();
    }
}
