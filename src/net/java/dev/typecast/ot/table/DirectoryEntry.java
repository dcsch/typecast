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
 * @version $Id: DirectoryEntry.java,v 1.1.1.1 2004-12-05 23:14:37 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class DirectoryEntry implements Cloneable {

    private int tag;
    private int checksum;
    private int offset;
    private int length;
//    private Table table = null;

    protected DirectoryEntry(DataInput di) throws IOException {
        tag = di.readInt();
        checksum = di.readInt();
        offset = di.readInt();
        length = di.readInt();
    }
    
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    public int getChecksum() {
        return checksum;
    }

    public int getLength() {
        return length;
    }

    public int getOffset() {
        return offset;
    }

    public int getTag() {
        return tag;
    }

    public String getTagAsString() {
        return new StringBuffer()
            .append((char)((tag>>24)&0xff))
            .append((char)((tag>>16)&0xff))
            .append((char)((tag>>8)&0xff))
            .append((char)((tag)&0xff))
            .toString();
    }
    
    public String toString() {
        return new StringBuffer()
            .append("'").append(getTagAsString())
            .append("' - chksm = 0x").append(Integer.toHexString(checksum))
            .append(", off = 0x").append(Integer.toHexString(offset))
            .append(", len = ").append(length)
            .toString();
    }
}
