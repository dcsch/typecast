/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.io;

import java.io.IOException;

/**
 * Utilities for writing to {@link BinaryOutput}.
 *
 * @author <a href="mailto:haui@haumacher.de">Bernhard Haumacher</a>
 */
public class BinUtils {

    /**
     * Ensures that the next write position in the given {@link BinaryOutput} is at a
     * 4 byte word position.
     */
    public static void padding4(BinaryOutput out) throws IOException {
        long length = out.getPosition();
        int offset = (int) (length % 4);
        if (offset > 0) {
            for (int n = offset; n < 4; n++) {
                out.write(0);
            }
        }
    }

}
