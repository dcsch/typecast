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
 * @version $Id: LigatureSubstFormat1.java,v 1.1.1.1 2004-12-05 23:14:49 davidsch Exp $
 */
public class LigatureSubstFormat1 extends LigatureSubst {

    private int coverageOffset;
    private int ligSetCount;
    private int[] ligatureSetOffsets;
    private Coverage coverage;
    private LigatureSet[] ligatureSets;

    /** Creates new LigatureSubstFormat1 */
    protected LigatureSubstFormat1(RandomAccessFile raf,int offset) throws IOException {
        coverageOffset = raf.readUnsignedShort();
        ligSetCount = raf.readUnsignedShort();
        ligatureSetOffsets = new int[ligSetCount];
        ligatureSets = new LigatureSet[ligSetCount];
        for (int i = 0; i < ligSetCount; i++) {
            ligatureSetOffsets[i] = raf.readUnsignedShort();
        }
        raf.seek(offset + coverageOffset);
        coverage = Coverage.read(raf);
        for (int i = 0; i < ligSetCount; i++) {
            ligatureSets[i] = new LigatureSet(raf, offset + ligatureSetOffsets[i]);
        }
    }

    public int getFormat() {
        return 1;
    }

    public String getTypeAsString() {
        return "LigatureSubstFormat1";
    }    
}
