/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import net.java.dev.typecast.ot.Fmt;

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

    /**
     * Ensures that the next write position in the given {@link BinaryOutput} is at a
     * 2 byte word position.
     */
    public static void padding2(BinaryOutput out) throws IOException {
        long length = out.getPosition();
        int offset = (int) (length % 2);
        if (offset > 0) {
            for (int n = offset; n < 2; n++) {
                out.write(0);
            }
        }
    }

    /** 
     * Writes a hex dump of the given byte array to the given {@link File}.
     */
    public static void hexDump(File file, byte[] buf) throws IOException {
        StringBuilder result = new StringBuilder();
        for (int n = 0, cnt = buf.length; n < cnt; n += 16) {
            result.append(Fmt.pad(10, n));
            
            result.append(":");
            for (int d = 0, limit = Math.min(16, cnt - n); d < limit; d++) {
                result.append(" ");
                if (d % 4 == 0) {
                    result.append(" ");
                }
                if (d == 8) {
                    result.append("  ");
                }
                result.append(Fmt.padHex(2, buf[n + d] & 0xFF));
            }
            result.append("\n");
        }
        
        try (FileOutputStream out = new FileOutputStream(file)) {
            try (Writer w = new OutputStreamWriter(out, "ASCII")) {
                w.write(result.toString());
            }
        }
    }
    
}
