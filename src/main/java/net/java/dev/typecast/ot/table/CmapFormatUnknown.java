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

import net.java.dev.typecast.io.BinaryOutput;

/**
 * When we encounter a cmap format we don't understand, we can use this class
 * to hold the bare minimum information about it.
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class CmapFormatUnknown extends CmapFormat {
    
    private final int _format;
    private final int _length;
    private final int _language;
    private byte[] _data;
    
    /** Creates a new instance of CmapFormatUnknown
     * @param format
     * @param di
     * @throws java.io.IOException */
    CmapFormatUnknown(int format, DataInput di) throws IOException {
        _format = format;
        if (_format < 8) {
            _length = di.readUnsignedShort();
            _language = di.readUnsignedShort();
        
            // We don't know how to handle this data, so we'll just keep it.
            _data = new byte[_length - 6];
            di.readFully(_data);
        } else {
            _length = di.readInt();
            _language = di.readInt();

            // We don't know how to handle this data, so we'll just keep it.
            _data = new byte[_length - 10];
            di.readFully(_data);
        }
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        long start = out.getPosition();
        
        out.writeShort(getFormat());
        if (_format < 8) {
            try (BinaryOutput lengthOut = out.reserve(2)) {
                out.writeShort(getLanguage());
                out.write(_data);
                
                lengthOut.writeShort((int) (out.getPosition() - start));
            }
        } else {
            try  (BinaryOutput lengthOut = out.reserve(4)) {
                out.writeInt(getLanguage());
                out.write(_data);
                
                lengthOut.writeInt((int) (out.getPosition() - start));
            }
        }
    }

    @Override
    public int getFormat() {
        return _format;
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
        return 0;
    }
    
    @Override
    public Range getRange(int index) throws ArrayIndexOutOfBoundsException {
        throw new ArrayIndexOutOfBoundsException();
    }

    @Override
    public int mapCharCode(int charCode) {
        return 0;
    }
}
