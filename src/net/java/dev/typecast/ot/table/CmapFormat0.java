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

/**
 * Simple Macintosh cmap table, mapping only the ASCII character set to glyphs.
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class CmapFormat0 extends CmapFormat {

    private final int[] _glyphIdArray = new int[256];

    protected CmapFormat0(DataInput di) throws IOException {
        super(di);
        _format = 0;
        for (int i = 0; i < 256; i++) {
            _glyphIdArray[i] = di.readUnsignedByte();
        }
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
}
