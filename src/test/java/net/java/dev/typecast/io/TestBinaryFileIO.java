/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.io;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import junit.framework.TestCase;

/**
 * Test case for {@link BinaryFileOutput}.
 *
 * @author <a href="mailto:haui@haumacher.de">Bernhard Haumacher</a>
 */
public class TestBinaryFileIO extends TestCase {

    /** 
     * Tests writing to {@link BinaryFileOutput}.
     */
    public void testWrite() throws IOException {
        BinaryOutput inner;
        File testFile = new File("target/tmp/output.bin");
        try (BinaryFileOutput out = new BinaryFileOutput(new RandomAccessFile(testFile, "rw"))) {
            out.write(42);
            inner = out.reserve(5);
            out.write(13);
        }
        
        BinaryOutput inner2;
        try {
            inner.write(99);
            long position = inner.getPosition();
            try {
                inner.writeLong(0L);
                fail("Must fail.");
            } catch (IOException ex) {
                // Expected.
            }
            
            inner.setPosition(position);
            try {
                inner.writeUTF("öäü");
                fail("Must fail.");
            } catch (IOException ex) {
                // Expected.
            }

            inner.setPosition(position);
            try {
                inner.reserve(5);
                fail("Must fail.");
            } catch (IOException ex) {
                // Expected.
            }
            
            inner.setPosition(position);
            inner2 = inner.reserve(2);
            inner.writeChar(0xEE00);
        } finally {
            inner.close();
        }
        
        try {
            long pos = inner2.getPosition();
            try {
                inner2.writeInt(0);
                fail("Must fail.");
            } catch (IOException ex) {
                // Expected.
            }
            
            inner2.setPosition(pos);
            inner2.writeShort(0xCAFF);
        } finally {
            inner2.close();
        }
        
        try (DataInputStream in = new DataInputStream(new FileInputStream(testFile))) {
            assertEquals(42, in.read());
            assertEquals(99, in.read());
            assertEquals(0xCAFFEE00, in.readInt());
            assertEquals(13, in.read());
        }
    }
    
    /**
     * Tests {@link BinaryFileInput}.
     */
    public void testRead() throws IOException {
        File testFile = new File("target/tmp/input.bin");
        RandomAccessFile f = new RandomAccessFile(testFile, "rw");
        for (int n = 0, cnt = 40; n < cnt; n++) {
            f.write(n);
        }
        
        try (BinaryFileInput in = new BinaryFileInput(f)) {
            for (int n = 0, cnt = 10; n < cnt; n++) {
                in.readInt();
            }
        }
    }
    
}
