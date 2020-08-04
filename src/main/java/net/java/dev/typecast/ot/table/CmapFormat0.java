/*
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004-2016 David Schweinsberg
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


package net.java.dev.typecast.ot.table;

import java.io.DataInput;
import java.io.IOException;
import java.util.Arrays;

import net.java.dev.typecast.io.BinaryOutput;

/**
 * Format 0: Byte encoding table
 * 
 * <p>
 * Simple Macintosh cmap table, mapping only the ASCII character set to glyphs.
 * </p>
 * 
 * @see "https://docs.microsoft.com/en-us/typography/opentype/spec/cmap#format-0-byte-encoding-table"
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class CmapFormat0 extends CmapFormat {

    /**
     * uint16   
     * 
     * @see #getLength()
     */
    private final int _length;
    
    /**
     * uint16 
     * 
     * @see #getLanguage()
     */
    private final int _language;
    
    private final int[] _glyphIdArray;

    CmapFormat0(DataInput di) throws IOException {
        _length = di.readUnsignedShort();
        _language = di.readUnsignedShort();
        
        // Available mapping data is total length minus  format, length, and language field.
        int mappings = _length - 6;
        _glyphIdArray = new int[mappings];
        for (int i = 0; i < mappings; i++) {
            _glyphIdArray[i] = di.readUnsignedByte();
        }
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        long start = out.getPosition();
        out.writeShort(getFormat());
        BinaryOutput lengthOut = out.reserve(2);
        out.writeShort(getLanguage());
        for (int n = 0, cnt = _glyphIdArray.length; n < cnt; n++) {
            out.writeByte(_glyphIdArray[n]);
        }
        long length = out.getPosition() - start;
        
        lengthOut.writeShort((int) length);
        lengthOut.close();
    }

    @Override
    public int getFormat() {
        return 0;
    }

    @Override
    public int getLength() {
        return _length;
    }

    @Override
    public int getLanguage() {
        return _language;
    }

    @Override
    public int getRangeCount() {
        return 1;
    }
    
    @Override
    public Range getRange(int index) throws ArrayIndexOutOfBoundsException {
        if (index != 0) {
            throw new ArrayIndexOutOfBoundsException();
        }
        return new Range(0, 255);
    }

    @Override
    public int mapCharCode(int charCode) {
        if (0 <= charCode && charCode < 256) {
            return _glyphIdArray[charCode];
        } else {
            return 0;
        }
    }
    
    @Override
    public String toString() {
        return super.toString() +
            "    format:         " + getFormat() + "\n" +
            "    language:       " + getLanguage() + "\n" +
            "    glyphIdArray:   " + Arrays.toString(_glyphIdArray) + "\n";
    }
}
