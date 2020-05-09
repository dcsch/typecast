/*
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004-2007 David Schweinsberg
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
 * The Horizontal Device Metrics table for TrueType outlines.  This stores
 * integer advance widths scaled to specific pixel sizes.
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class HdmxTable implements Table {
    
    static class DeviceRecord {
        
        private short _pixelSize;
        private short _maxWidth;
        private short[] _widths;

        DeviceRecord(int numGlyphs, DataInput di) throws IOException {
            _pixelSize = di.readByte();
            _maxWidth = di.readByte();
            _widths = new short[numGlyphs];
            for (int i = 0; i < numGlyphs; ++i) {
                _widths[i] = di.readByte();
            }
        }

        short getPixelSize() {
            return _pixelSize;
        }
        
        short getMaxWidth() {
            return _maxWidth;
        }
        
        short[] getWidths() {
            return _widths;
        }
    }
    
    private int _version;
    private short _numRecords;
    private int _sizeDeviceRecords;
    private DeviceRecord[] _records;
    private int _length;

    /** Creates a new instance of HdmxTable */
    public HdmxTable(DataInput di, int length, MaxpTable maxp)
    throws IOException {
        _version = di.readUnsignedShort();
        _numRecords = di.readShort();
        _sizeDeviceRecords = di.readInt();
        _records = new DeviceRecord[_numRecords];
        
        // Read the device records
        for (int i = 0; i < _numRecords; ++i) {
            _records[i] = new DeviceRecord(maxp.getNumGlyphs(), di);
        }
        _length = length;
    }

    @Override
    public int getType() {
        return hdmx;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'hdmx' Table - Horizontal Device Metrics\n----------------------------------------\n");
        sb.append("Size = ").append(_length).append(" bytes\n")
            .append("\t'hdmx' version:         ").append(_version).append("\n")
            .append("\t# device records:       ").append(_numRecords).append("\n")
            .append("\tRecord length:          ").append(_sizeDeviceRecords).append("\n");
        for (int i = 0; i < _numRecords; ++i) {
            sb.append("\tDevRec ").append(i)
                .append(": ppem = ").append(_records[i].getPixelSize())
                .append(", maxWid = ").append(_records[i].getMaxWidth())
                .append("\n");
            for (int j = 0; j < _records[i].getWidths().length; ++j) {
                sb.append("    ").append(j).append(".   ")
                    .append(_records[i].getWidths()[j]).append("\n");
            }
            sb.append("\n\n");
        }
        return sb.toString();
    }

}
