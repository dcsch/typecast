/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.io;

import java.io.EOFException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * {@link BinaryFileIO} with a limit that cannot be written beyond.
 *
 * @author <a href="mailto:haui@haumacher.de">Bernhard Haumacher</a>
 */
public class LimitedBinaryFileIO extends BinaryFileIO {

    private long _start;
    private long _limit;

    /**
     * Creates a {@link LimitedBinaryFileIO}.
     *
     * @param out
     *        The output to write to.
     * @param pos
     *        The position to start writing.
     * @param length
     *        The maximum number of bytes to allow writing.
     */
    public LimitedBinaryFileIO(RandomAccessFile out, long pos, int length) {
        super(out, pos);
        
        _start = pos;
        _limit = pos + length;
    }
    
    @Override
    public void setPosition(long pos) {
        if (pos < _start) {
            throw new IllegalArgumentException("Before start of limited region.");
        }
        if (pos > _limit) {
            throw new IllegalArgumentException("After end of limited region.");
        }
        super.setPosition(pos);
    }

    @Override
    protected void seek(int reservedLength) throws IOException {
        if (getPosition() + reservedLength > _limit) {
            throw new EOFException(
                "Tried to write beyond the limits of this region [" + _start + ", " + _limit + "] starting at " + 
                    getPosition() +" writing " + reservedLength + " bytes.");
        }
        super.seek(reservedLength);
    }

}
