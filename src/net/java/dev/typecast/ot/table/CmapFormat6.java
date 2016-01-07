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
 * TODO: To be implemented
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class CmapFormat6 extends CmapFormat {

    private short _firstCode;
    private short _entryCount;
    private short[] _glyphIdArray;

    protected CmapFormat6(DataInput di) throws IOException {
        super(di);
        _format = 6;
        
        // HACK: As this is not yet implemented, we need to skip over the bytes
        // we should be consuming
        di.skipBytes(_length - 6);
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
