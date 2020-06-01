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
 * {@link BinaryFileInput} with a limit that cannot be written beyond.
 *
 * @author <a href="mailto:haui@haumacher.de">Bernhard Haumacher</a>
 */
public class LimitedBinaryFileOutput extends BinaryFileOutput {

    private final long _start;
    private final long _limit;

    /**
     * Creates a {@link LimitedBinaryFileOutput}.
     *
     * @param out
     *        The output to write to.
     * @param pos
     *        The position to start writing.
     * @param length
     *        The maximum number of bytes to allow writing.
     */
    public LimitedBinaryFileOutput(RandomAccessFile out, long pos, int length) {
        super(new LimitedBuffer(out, pos, length));
        
        _start = pos;
        _limit = pos + length;
    }
    
    long getLimit() {
        return _limit;
    }
    
    @Override
    public void setPosition(long pos) throws IOException {
        if (pos < _start) {
            throw new IOException("Before start of limited region.");
        }
        if (pos > _limit) {
            throw new IOException("After end of limited region.");
        }
        super.setPosition(pos);
    }

    static class LimitedBuffer extends Buffer {

        private long _start;
        private long _limit;
        private int _bufferLimit;

        /** 
         * Creates a {@link LimitedBuffer}.
         */
        public LimitedBuffer(RandomAccessFile f, long pos, int length) {
            super(f, pos, Math.min(4096, length));

            _start = pos;
            _limit = pos + length;
            _bufferLimit = length;
        }
        
        @Override
        public void setPosition(long pos) throws IOException {
            super.setPosition(pos);
            
            _bufferLimit = (int) (_limit - pos);
        }
        
        /**
         * Make sure that writing the given number of bytes does not exceed
         * limits.
         * 
         * @param length
         *        The number of bytes that are being written.
         * @throws IOException
         *         if limits are exceeded.
         */
        private void reserve(int length) throws IOException {
            if (getLength() + length > _bufferLimit) {
                throw error(length);
            }
        }

        @Override
        public void write(int b) throws IOException {
            reserve(1);
            super.write(b);
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            reserve(len);
            super.write(b, off, len);
        }
        
        private EOFException error(int len) {
            return new EOFException(
                    "Tried to write beyond the limits of this region [" + _start + ", " + _limit + "] starting at " + 
                            getPosition() + " writing " + len + " bytes.");
        }
    }
    
}
