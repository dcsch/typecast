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
 * @version $Id: SingleSubstFormat1.java,v 1.1.1.1 2004-12-05 23:14:58 davidsch Exp $
 */
public class SingleSubstFormat1 extends SingleSubst {

    private int coverageOffset;
    private short deltaGlyphID;
    private Coverage coverage;

    /** Creates new SingleSubstFormat1 */
    protected SingleSubstFormat1(RandomAccessFile raf, int offset) throws IOException {
        coverageOffset = raf.readUnsignedShort();
        deltaGlyphID = raf.readShort();
        raf.seek(offset + coverageOffset);
        coverage = Coverage.read(raf);
    }

    public int getFormat() {
        return 1;
    }

    public int substitute(int glyphId) {
        int i = coverage.findGlyph(glyphId);
        if (i > -1) {
            return glyphId + deltaGlyphID;
        }
        return glyphId;
    }
    
    public String getTypeAsString() {
        return "SingleSubstFormat1";
    }
}

