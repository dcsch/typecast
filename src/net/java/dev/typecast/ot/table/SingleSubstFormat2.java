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
 * @version $Id: SingleSubstFormat2.java,v 1.1.1.1 2004-12-05 23:14:59 davidsch Exp $
 */
public class SingleSubstFormat2 extends SingleSubst {

    private int coverageOffset;
    private int glyphCount;
    private int[] substitutes;
    private Coverage coverage;

    /** Creates new SingleSubstFormat2 */
    protected SingleSubstFormat2(RandomAccessFile raf, int offset) throws IOException {
        coverageOffset = raf.readUnsignedShort();
        glyphCount = raf.readUnsignedShort();
        substitutes = new int[glyphCount];
        for (int i = 0; i < glyphCount; i++) {
            substitutes[i] = raf.readUnsignedShort();
        }
        raf.seek(offset + coverageOffset);
        coverage = Coverage.read(raf);
    }

    public int getFormat() {
        return 2;
    }

    public int substitute(int glyphId) {
        int i = coverage.findGlyph(glyphId);
        if (i > -1) {
            return substitutes[i];
        }
        return glyphId;
    }

    public String getTypeAsString() {
        return "SingleSubstFormat2";
    }    
}

