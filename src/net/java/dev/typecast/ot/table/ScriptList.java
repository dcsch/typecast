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
 * @version $Id: ScriptList.java,v 1.1.1.1 2004-12-05 23:14:57 davidsch Exp $
 */
public class ScriptList {

    private int scriptCount = 0;
    private ScriptRecord[] scriptRecords;
    private Script[] scripts;
    
    /** Creates new ScriptList */
    protected ScriptList(RandomAccessFile raf, int offset) throws IOException {
        raf.seek(offset);
        scriptCount = raf.readUnsignedShort();
        scriptRecords = new ScriptRecord[scriptCount];
        scripts = new Script[scriptCount];
        for (int i = 0; i < scriptCount; i++) {
            scriptRecords[i] = new ScriptRecord(raf);
        }
        for (int i = 0; i < scriptCount; i++) {
            scripts[i] = new Script(raf, offset + scriptRecords[i].getOffset());
        }
    }

    public int getScriptCount() {
        return scriptCount;
    }
    
    public ScriptRecord getScriptRecord(int i) {
        return scriptRecords[i];
    }
    
    public Script getScript(int i) {
        return scripts[i];
    }
    
    public Script findScript(String tag) {
        if (tag.length() != 4) {
            return null;
        }
        int tagVal = (int)((tag.charAt(0)<<24)
            | (tag.charAt(1)<<16)
            | (tag.charAt(2)<<8)
            | tag.charAt(3));
        for (int i = 0; i < scriptCount; i++) {
            if (scriptRecords[i].getTag() == tagVal) {
                return scripts[i];
            }
        }
        return null;
    }

}

