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
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: LangSysRecord.java,v 1.1.1.1 2004-12-05 23:14:49 davidsch Exp $
 */
public class LangSysRecord {

    private int tag;
    private int offset;
    
    /** Creates new LangSysRecord */
    public LangSysRecord(DataInput di) throws IOException {
        tag = di.readInt();
        offset = di.readUnsignedShort();
    }

    public int getTag() {
        return tag;
    }

    public int getOffset() {
        return offset;
    }

    public String getTagAsString() {
        return new StringBuffer()
            .append((char)((tag>>24)&0xff))
            .append((char)((tag>>16)&0xff))
            .append((char)((tag>>8)&0xff))
            .append((char)((tag)&0xff))
            .toString();
    }
}
