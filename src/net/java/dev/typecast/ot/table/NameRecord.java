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
 * @version $Id: NameRecord.java,v 1.1.1.1 2004-12-05 23:14:52 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class NameRecord {

    private short platformId;
    private short encodingId;
    private short languageId;
    private short nameId;
    private short stringLength;
    private short stringOffset;
    private String record;

    protected NameRecord(DataInput di) throws IOException {
        platformId = di.readShort();
        encodingId = di.readShort();
        languageId = di.readShort();
        nameId = di.readShort();
        stringLength = di.readShort();
        stringOffset = di.readShort();
    }
    
    public short getEncodingId() {
        return encodingId;
    }
    
    public short getLanguageId() {
        return languageId;
    }
    
    public short getNameId() {
        return nameId;
    }
    
    public short getPlatformId() {
        return platformId;
    }

    public String getRecordString() {
        return record;
    }

    protected void loadString(DataInput di) throws IOException {
        StringBuffer sb = new StringBuffer();
        if (platformId == ID.platformUnicode) {
            
            // Unicode (big-endian)
            for (int i = 0; i < stringLength/2; i++) {
                sb.append(di.readChar());
            }
        } else if (platformId == ID.platformMacintosh) {

            // Macintosh encoding, ASCII
            for (int i = 0; i < stringLength; i++) {
                sb.append((char) di.readByte());
            }
        } else if (platformId == ID.platformISO) {
            
            // ISO encoding, ASCII
            for (int i = 0; i < stringLength; i++) {
                sb.append((char) di.readByte());
            }
        } else if (platformId == ID.platformMicrosoft) {
            
            // Microsoft encoding, Unicode
            char c;
            for (int i = 0; i < stringLength/2; i++) {
                c = di.readChar();
                sb.append(c);
            }
        }
        record = sb.toString();
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        
        sb.append("             Platform ID:       ").append(platformId)
            .append("\n             Specific ID:       ").append(encodingId)
            .append("\n             Language ID:       ").append(languageId)
            .append("\n             Name ID:           ").append(nameId)
            .append("\n             Length:            ").append(stringLength)
            .append("\n             Offset:            ").append(stringOffset)
            .append("\n\n").append(record);
        
        return sb.toString();
    }
}
