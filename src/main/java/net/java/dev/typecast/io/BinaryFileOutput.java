/*
 * Typecast
 *
 * Copyright © 2004-2019 David Schweinsberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.java.dev.typecast.io;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;

/**
 * {@link BinaryOutput} based on a {@link RandomAccessFile}.
 *
 * @author <a href="mailto:haui@haumacher.de">Bernhard Haumacher</a>
 */
public class BinaryFileOutput implements BinaryOutput {

    private int _handlesOpen = 0;

    private boolean _closed;
    
    private final Buffer _buffer;
    
    private final DataOutputStream _out;

    /**
     * Creates a {@link BinaryFileOutput}.
     *
     * @param f
     *        The file to write to.
     */
    public BinaryFileOutput(RandomAccessFile f) {
        this (f, 0);
    }
    
    /** 
     * Creates a {@link BinaryFileOutput}.
     *
     * @param f
     *        The file to write to.
     * @param pos
     *        The position to start writing.
     */
    public BinaryFileOutput(RandomAccessFile f, long pos) {
        this(new Buffer(f, pos));
    }
    
    /** 
     * Creates a {@link BinaryFileOutput}.
     */
    protected BinaryFileOutput(Buffer buffer) {
        _buffer = buffer;
        _out = new DataOutputStream(_buffer);
    }

    @Override
    public long getPosition() {
        return _buffer.getPosition();
    }
    
    @Override
    public void setPosition(long pos) throws IOException {
        _out.flush();
        _buffer.setPosition(pos);
    }
    
    @Override
    public void flush() throws IOException {
        _out.flush();
    }

    @Override
    public BinaryOutput reserve(int length) throws IOException {
        long start = getPosition();

        setPosition(start + length);
        
        _handlesOpen++;
        return new LimitedBinaryFileOutput(_buffer.getFile(), start, length) {
            @Override
            protected void doClose() throws IOException {
                BinaryFileOutput.this.onCloseHandle();
            }
        };
    }
    
    final void onCloseHandle() throws IOException {
        _handlesOpen--;
        if (_closed) {
            tryClose();
        }
    }

    @Override
    public void write(int b) throws IOException {
        _out.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        _out.write(b);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        _out.write(b, off, len);
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        _out.writeBoolean(v);
    }

    @Override
    public void writeByte(int v) throws IOException {
        _out.writeByte(v);
    }

    @Override
    public void writeShort(int v) throws IOException {
        _out.writeShort(v);
    }

    @Override
    public void writeChar(int v) throws IOException {
        _out.writeChar(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        _out.writeInt(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        _out.writeLong(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        _out.writeFloat(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        _out.writeDouble(v);
    }

    @Override
    public void writeBytes(String s) throws IOException {
        _out.writeBytes(s);
    }

    @Override
    public void writeChars(String s) throws IOException {
        _out.writeChars(s);
    }

    @Override
    public void writeUTF(String s) throws IOException {
        _out.writeUTF(s);
    }

    @Override
    public final void close() throws IOException {
        if (_closed) {
            return;
        }
        _closed = true;
        
        _out.close();
        
        tryClose();
    }

    private void tryClose() throws IOException {
        if (_handlesOpen == 0) {
            doClose();
        }
    }

    protected void doClose() throws IOException {
        _buffer.getFile().close();
    }

    static class Buffer extends OutputStream {
        static final int MAX_BUFFER = 512;
    
        private RandomAccessFile _f;
        
        private boolean _streamClosed = false;
    
        private long _pos;
    
        private int _length = 0;
    
        private final byte[] _buffer;

        /** 
         * Creates a {@link Buffer}.
         */
        public Buffer(RandomAccessFile f, long pos) {
            this(f, pos, 4096);
        }
        
        /** 
         * Creates a {@link BinaryFileOutput.Buffer}.
         */
        protected Buffer(RandomAccessFile f, long pos, int bufferSize) {
            _f = f;
            _pos = pos;
            _buffer = new byte[bufferSize];
        }
        
        /**
         * The underlying {@link RandomAccessFile}.
         */
        public RandomAccessFile getFile() {
            return _f;
        }

        /**
         * Updates {@link #getPosition()}
         * 
         * <p>
         * If any pending output is still buffered, it is flushed before
         * changing the target position.
         * </p>
         */
        public void setPosition(long pos) throws IOException {
            flush();
            _pos = pos;
        }

        /** 
         * The position in the underlying file where to write the next byte to.
         */
        public long getPosition() {
            return _pos + _length;
        }
        
        /**
         * The number of bytes currently in the buffer.
         */
        public int getLength() {
            return _length;
        }

        @Override
        public void write(int b) throws IOException {
            checkClosed();
            if (_length == _buffer.length) {
                flush();
            }
            _buffer[_length++] = (byte) b;
        }
    
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            checkClosed();
            
            if (len > MAX_BUFFER) {
                flush();
                writeDirect(b, off, len);
            } else {
                if (_length + len > _buffer.length) {
                    flush();
                }
                buffer(b, off, len);
            }
        }
    
        private void checkClosed() throws IOException {
            if (_streamClosed) {
                throw new IOException("Stream closed.");
            }
        }
    
        private void buffer(byte[] b, int off, int len) {
            System.arraycopy(b, off, _buffer, _length, len);
            _length += len;
        }
    
        @Override
        public void flush() throws IOException {
            if (_length == 0) {
                return;
            }
            writeDirect(_buffer, 0, _length);
            _length = 0;
        }
    
        @Override
        public final void close() throws IOException {
            super.close();
            
            _streamClosed = true;
        }
    
        private void writeDirect(byte[] buffer, int offset, int length) throws IOException {
            _f.seek(_pos);
            _f.write(buffer, offset, length);
            _pos += length;
        }
    }

}
