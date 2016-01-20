/*
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004-2016 David Schweinsberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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

/**
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class CmapFormat12 extends CmapFormat {

    private final int _length;
    private final int _language;
    private final int _nGroups;
    private final int[] _startCharCode;
    private final int[] _endCharCode;
    private final int[] _startGlyphId;

    protected CmapFormat12(DataInput di) throws IOException {
        di.readUnsignedShort(); // reserved
        _length = di.readInt();
        _language = di.readInt();
        _nGroups = di.readInt();
        _startCharCode = new int[_nGroups];
        _endCharCode = new int[_nGroups];
        _startGlyphId = new int[_nGroups];
        for (int i = 0; i < _nGroups; ++i) {
            _startCharCode[i] = di.readInt();
            _endCharCode[i] = di.readInt();
            _startGlyphId[i] = di.readInt();
        }
    }

    @Override
    public int getFormat() {
        return 12;
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
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Range getRange(int index) throws ArrayIndexOutOfBoundsException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int mapCharCode(int charCode) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
