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
 * @version $Id: Script.java,v 1.1.1.1 2004-12-05 23:14:57 davidsch Exp $
 */
public class Script {

    private int defaultLangSysOffset;
    private int langSysCount;
    private LangSysRecord[] langSysRecords;
    private LangSys defaultLangSys;
    private LangSys[] langSys;
    
    /** Creates new ScriptTable */
    protected Script(RandomAccessFile raf, int offset) throws IOException {
        raf.seek(offset);
        defaultLangSysOffset = raf.readUnsignedShort();
        langSysCount = raf.readUnsignedShort();
        if (langSysCount > 0) {
            langSysRecords = new LangSysRecord[langSysCount];
            for (int i = 0; i < langSysCount; i++) {
                langSysRecords[i] = new LangSysRecord(raf);
            }
        }

        // Read the LangSys tables
        if (langSysCount > 0) {
            langSys = new LangSys[langSysCount];
            for (int i = 0; i < langSysCount; i++) {
                raf.seek(offset + langSysRecords[i].getOffset());
                langSys[i] = new LangSys(raf);
            }
        }
        if (defaultLangSysOffset > 0) {
            raf.seek(offset + defaultLangSysOffset);
            defaultLangSys = new LangSys(raf);
        }
    }

    public int getLangSysCount() {
        return langSysCount;
    }
    
    public LangSysRecord getLangSysRecord(int i) {
        return langSysRecords[i];
    }

    public LangSys getDefaultLangSys() {
        return defaultLangSys;
    }

    public LangSys getLangSys(int i) {
        return langSys[i];
    }
}

