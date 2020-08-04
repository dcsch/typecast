/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.io;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

/**
 * {@link BinaryOutput} writing to a {@link RandomAccessFile}.
 *
 * @author <a href="mailto:haui@haumacher.de">Bernhard Haumacher</a>
 */
public class BinaryFileInput implements BinaryInput, AutoCloseable {
    
    private Buffer _buffer;
    private DataInputStream _in;

    /**
     * Creates a {@link BinaryFileInput}.
     *
     * @param f
     *        The file to read.
     */
    public BinaryFileInput(RandomAccessFile f) {
        this(f, 0L);
    }
    
    /**
     * Creates a {@link BinaryFileInput}.
     * 
     * @param f
     *        The file to read.
     * @param pos
     *        The position to start writing.
     */
    public BinaryFileInput(RandomAccessFile f, long pos) {
        _buffer = new Buffer(f, pos);
        _in = new DataInputStream(_buffer);
    }
    
    @Override
    public long getPosition() {
        return _buffer.getPosition();
    }
    
    @Override
    public void setPosition(long pos) throws IOException {
        _buffer.setPosition(pos);
    }
    
    @Override
    public void close() throws IOException {
        _in.close();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        _in.readFully(b);
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        _in.readFully(b, off, len);
    }

    @Override
    public int skipBytes(int n) throws IOException {
        return _in.skipBytes(n);
    }

    @Override
    public boolean readBoolean() throws IOException {
        return _in.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return _in.readByte();
    }

    @Override
    public int readUnsignedByte() throws IOException {
        return _in.readUnsignedByte();
    }

    @Override
    public short readShort() throws IOException {
        return _in.readShort();
    }

    @Override
    public int readUnsignedShort() throws IOException {
        return _in.readUnsignedShort();
    }

    @Override
    public char readChar() throws IOException {
        return _in.readChar();
    }

    @Override
    public int readInt() throws IOException {
        return _in.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return _in.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return _in.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return _in.readDouble();
    }

    @Override
    public String readLine() throws IOException {
        return _in.readLine();
    }

    @Override
    public String readUTF() throws IOException {
        return _in.readUTF();
    }
    
    static class Buffer extends InputStream {
        static final int MAX_BUFFER = 512;
    
        private RandomAccessFile _f;
        
        private boolean _streamClosed = false;
    
        private long _pos;
    
        private int _index = 0;
        private int _length = 0;
    
        private final byte[] _buffer = new byte[4096];

        /** 
         * Creates a {@link Buffer}.
         */
        public Buffer(RandomAccessFile f, long pos) {
            _f = f;
            _pos = pos;
        }
        
        /**
         * The underlying {@link RandomAccessFile}.
         */
        public RandomAccessFile getFile() {
            return _f;
        }

        /**
         * Updates {@link #getPosition()}
         */
        public void setPosition(long pos) {
            if (pos >= _pos && pos <= _pos + _length) {
                _index = (int) (pos - _pos);
            } else {
                _pos = pos;
                _length = 0;
                _index = 0;
            }
        }

        /** 
         * The position in the underlying file where to write the next byte to.
         */
        public long getPosition() {
            return _pos + _index;
        }

        @Override
        public int read() throws IOException {
            checkClosed();
            fetch();
            return _buffer[_index++] & 0xFF;
        }
        
        @Override
        public int read(byte[] b, int off, int len) throws IOException {
            checkClosed();
            if (len == 0) {
                return 0;
            }
            
            fetch();
            int direct = Math.min(avail(), len);
            System.arraycopy(_buffer, _index, b, off, direct);

            return direct;
        }

        private int avail() {
            return _length - _index;
        }

        private void fetch() throws IOException, EOFException {
            if (_index < _length) {
                return;
            }
            
            _pos = getPosition();
            _f.seek(_pos);
            
            int length;
            while (true) {
                length = _f.read(_buffer);
                if (length < 0) {
                    throw new EOFException();
                }
                if (length > 0) {
                    break;
                }
            }
            _index = 0;
            _length = length;
        }
    
        private void checkClosed() throws IOException {
            if (_streamClosed) {
                throw new IOException("Stream closed.");
            }
        }
    
        @Override
        public final void close() throws IOException {
            super.close();
            
            _streamClosed = true;
        }
    }

}
