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
 * @version $Id: CmapIndexEntry.java,v 1.1.1.1 2004-12-05 23:14:35 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class CmapIndexEntry implements Comparable {

    private int platformId;
    private int encodingId;
    private int offset;
    private CmapFormat format;

    protected CmapIndexEntry(DataInput di) throws IOException {
        platformId = di.readUnsignedShort();
        encodingId = di.readUnsignedShort();
        offset = di.readInt();
    }

    public int getPlatformId() {
        return platformId;
    }

    public int getEncodingId() {
        return encodingId;
    }

    public int getOffset() {
        return offset;
    }

    public String getPlatformName() {
        switch (platformId) {
            case 1: return "Macintosh";
            case 3: return "Windows";
            default: return "";
        }
    }

    public String getEncodingName() {
        if (platformId == 3) {
            // Windows specific encodings
            switch (encodingId) {
                case 0: return "Symbol";
                case 1: return "Unicode";
                case 2: return "ShiftJIS";
                case 3: return "Big5";
                case 4: return "PRC";
                case 5: return "Wansung";
                case 6: return "Johab";
                default: return "";
            }
        }
        return "";
    }

    public CmapFormat getFormat() {
        return format;
    }
    
    public void setFormat(CmapFormat format) {
        this.format = format;
    }

    public String toString() {
        String platform;
        String encoding = "";

        switch (platformId) {
            case 1: platform = " (Macintosh)"; break;
            case 3: platform = " (Windows)"; break;
            default: platform = "";
        }
        if (platformId == 3) {
            // Windows specific encodings
            switch (encodingId) {
                case 0: encoding = " (Symbol)"; break;
                case 1: encoding = " (Unicode)"; break;
                case 2: encoding = " (ShiftJIS)"; break;
                case 3: encoding = " (Big5)"; break;
                case 4: encoding = " (PRC)"; break;
                case 5: encoding = " (Wansung)"; break;
                case 6: encoding = " (Johab)"; break;
                default: encoding = "";
            }
        }
        return new StringBuffer()
        .append( "platform id: " )
        .append( platformId )
//        .append( platform )
        .append(ID.getPlatformName((short) platformId))
        .append( ", encoding id: " )
        .append( encodingId )
//        .append( encoding )
        .append(ID.getEncodingName((short) platformId, (short) encodingId))
        .append( ", offset: " )
        .append( offset ).toString();
    }

    public int compareTo(java.lang.Object obj) {
        CmapIndexEntry entry = (CmapIndexEntry) obj;
        if (getOffset() < entry.getOffset()) {
            return -1;
        } else if (getOffset() > entry.getOffset()) {
            return 1;
        } else {
            return 0;
        }
    }
}
