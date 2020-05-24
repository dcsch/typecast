/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.io;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UTFDataFormatException;

/**
 * {@link BinaryOutput} writing to a {@link RandomAccessFile}.
 *
 * @author <a href="mailto:haui@haumacher.de">Bernhard Haumacher</a>
 */
public class BinaryFileIO implements BinaryIO {
    
    private long _pos;

    private RandomAccessFile _f;
    
    private int _handlesOpen = 0;

    private boolean _closed;
    
    /**
     * Creates a {@link BinaryFileIO}.
     *
     * @param out
     *        The file to write to.
     */
    public BinaryFileIO(RandomAccessFile out) {
        this(out, 0L);
    }
    
    /**
     * Creates a {@link BinaryFileIO}.
     * 
     * @param out
     *        The file to write to.
     * @param pos
     *        The position to start writing.
     */
    public BinaryFileIO(RandomAccessFile out, long pos) {
        _f = out;
        _pos = pos;
    }

    @Override
    public long getPosition() {
        return _pos;
    }
    
    @Override
    public void setPosition(long pos) {
        _pos = pos;
    }

    @Override
    public BinaryOutput reserve(int length) throws IOException {
        long start = _pos;
        
        seek(length);
        for (int n = 0; n < length; n++) {
            _f.write(0);
        }
        fetchPos();
        
        _handlesOpen++;
        return new LimitedBinaryFileIO(_f, start, length) {
            @Override
            protected void doClose() throws IOException {
                BinaryFileIO.this.onCloseHandle();
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
        seek(1);
        _f.write(b);
        fetchPos();
    }

    @Override
    public void write(byte[] b) throws IOException {
        seek(b.length);
        _f.write(b);
        fetchPos();
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        seek(len);
        _f.write(b, off, len);
        fetchPos();
    }

    @Override
    public void writeBoolean(boolean v) throws IOException {
        seek(1);
        _f.writeBoolean(v);
        fetchPos();
    }

    @Override
    public void writeByte(int v) throws IOException {
        seek(1);
        _f.writeByte(v);
        fetchPos();
    }

    @Override
    public void writeShort(int v) throws IOException {
        seek(2);
        _f.writeShort(v);
        fetchPos();
    }

    @Override
    public void writeChar(int v) throws IOException {
        seek(2);
        _f.writeChar(v);
        fetchPos();
    }

    @Override
    public void writeInt(int v) throws IOException {
        seek(4);
        _f.writeInt(v);
        fetchPos();
    }

    @Override
    public void writeLong(long v) throws IOException {
        seek(8);
        _f.writeLong(v);
        fetchPos();
    }

    @Override
    public void writeFloat(float v) throws IOException {
        seek(4);
        _f.writeFloat(v);
        fetchPos();
    }

    @Override
    public void writeDouble(double v) throws IOException {
        seek(8);
        _f.writeDouble(v);
        fetchPos();
    }

    @Override
    public void writeBytes(String s) throws IOException {
        seek(s.length());
        _f.writeBytes(s);
        fetchPos();
    }

    @Override
    public void writeChars(String s) throws IOException {
        seek(2 * s.length());
        _f.writeChars(s);
        fetchPos();
    }

    @Override
    public void writeUTF(String s) throws IOException {
        seek(2 + utfLength(s));
        _f.writeUTF(s);
        fetchPos();
    }

    private int utfLength(String s) throws UTFDataFormatException {
        int strlen = s.length();
        int result = 0;

        for (int n = 0; n < strlen; n++) {
            int c = s.charAt(n);
            if ((c >= 0x0001) && (c <= 0x007F)) {
                result++;
            } else if (c > 0x07FF) {
                result += 3;
            } else {
                result += 2;
            }
        }
        
        if (result > 65535) {
            throw new UTFDataFormatException(
                "Encoded string too long: " + result + " bytes, maximum is " + 65535);
        }
    
        return result;
    }

    /**
     * Updates {@link #getPosition()} from the actual position in the output file.
     */
    private void fetchPos() throws IOException {
        _pos = _f.getFilePointer();
    }
    
    /**
     * Updates the file pointer to the {@link #getPosition() last recorded position} for writing the
     * given number of bytes.
     *
     * @param reservedLength
     *        The number of bytes to immediately write.
     */
    protected void seek(int reservedLength) throws IOException {
        _f.seek(_pos);
    }

    @Override
    public final void close() throws IOException {
        if (_closed) {
            return;
        }
        _closed = true;
        
        tryClose();
    }

    private void tryClose() throws IOException {
        if (_handlesOpen == 0) {
            doClose();
        }
    }

    protected void doClose() throws IOException {
        _f.close();
    }

    @Override
    public void readFully(byte[] b) throws IOException {
        seek(b.length);
        _f.readFully(b);
        fetchPos();
    }

    @Override
    public void readFully(byte[] b, int off, int len) throws IOException {
        seek(len);
        _f.readFully(b, off, len);
        fetchPos();
    }

    @Override
    public int skipBytes(int n) throws IOException {
        seek(n);
        int result = _f.skipBytes(n);
        fetchPos();
        
        return result;
    }

    @Override
    public boolean readBoolean() throws IOException {
        seek(1);
        boolean result = _f.readBoolean();
        fetchPos();
        return result;
    }

    @Override
    public byte readByte() throws IOException {
        seek(1);
        byte result = _f.readByte();
        fetchPos();
        return result;
    }

    @Override
    public int readUnsignedByte() throws IOException {
        seek(1);
        int result = _f.readUnsignedByte();
        fetchPos();
        return result;
    }

    @Override
    public short readShort() throws IOException {
        seek(2);
        short result = _f.readShort();
        fetchPos();
        return result;
    }

    @Override
    public int readUnsignedShort() throws IOException {
        seek(2);
        int result = _f.readUnsignedShort();
        fetchPos();
        return result;
    }

    @Override
    public char readChar() throws IOException {
        seek(2);
        char result = _f.readChar();
        fetchPos();
        return result;
    }

    @Override
    public int readInt() throws IOException {
        seek(4);
        int result = _f.readInt();
        fetchPos();
        return result;
    }

    @Override
    public long readLong() throws IOException {
        seek(8);
        long result = _f.readLong();
        fetchPos();
        return result;
    }

    @Override
    public float readFloat() throws IOException {
        seek(4);
        float result = _f.readFloat();
        fetchPos();
        return result;
    }

    @Override
    public double readDouble() throws IOException {
        seek(8);
        double result = _f.readDouble();
        fetchPos();
        return result;
    }

    @Override
    public String readLine() throws IOException {
        seek(0);
        String result = _f.readLine();
        fetchPos();
        return result;
    }

    @Override
    public String readUTF() throws IOException {
        seek(2);
        String result = _f.readUTF();
        fetchPos();
        return result;
    }
    
}
