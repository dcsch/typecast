/*
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004 David Schweinsberg
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
 * Glyph Positioning Table
 * 
 * TODO: To be implemented
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
class GposTable implements Table {

    private int _version;
    private int _scriptList;
    private int _featureList;
    private int _lookupList;

    protected GposTable(DataInput di, int length) throws IOException {
        _version = di.readInt();
        _scriptList = di.readInt();
        _featureList = di.readInt();
        _lookupList = di.readInt();
        
        // TODO: Implement.
    }

    @Override
    public int getType() {
        return GPOS;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'GPOS' Table - Glyph Positioning Table\n");
        sb.append("--------------------------------------\n");
        sb.append("    version      = ").append(_version).append("\n");
        sb.append("    scriptList   = ").append(_scriptList).append("\n");
        sb.append("    featureList  = ").append(_featureList).append("\n");
        sb.append("    lookupList   = ").append(_lookupList).append("\n");
        return sb.toString();
    }

}
