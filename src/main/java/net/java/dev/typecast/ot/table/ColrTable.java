/*
 * Copyright (c) David Schweinsberg
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
import java.util.ArrayList;

/**
 * COLR — Color Table
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/colr">Spec: Color Table</a>
 */
public class ColrTable implements Table {

    static class BaseGlyphRecord {

        private final int _gid;
        private final int _firstLayerIndex;
        private final int _numLayers;

        BaseGlyphRecord(DataInput di) throws IOException {
            _gid = di.readUnsignedShort();
            _firstLayerIndex = di.readUnsignedShort();
            _numLayers = di.readUnsignedShort();
        }

        int getGid() {
            return _gid;
        }

        int getFirstLayerIndex() {
            return _firstLayerIndex;
        }

        int getNumLayers() {
            return _numLayers;
        }
    }

    static class LayerRecord {

        private final int _gid;
        private final int _paletteIndex;

        LayerRecord(DataInput di) throws IOException {
            _gid = di.readUnsignedShort();
            _paletteIndex = di.readUnsignedShort();
        }

        int getGid() {
            return _gid;
        }

        int getPaletteIndex() {
            return _paletteIndex;
        }
    }

    /**
     * First format version of {@link ColrTable}.
     */
    public static final int VERSION_0 = 0;

    private int _version = VERSION_0;
    private int _offsetBaseGlyphRecord;
    private int _offsetLayerRecord;
    private final ArrayList<BaseGlyphRecord> _baseGlyphRecords = new ArrayList<>();
    private final ArrayList<LayerRecord> _layerRecords = new ArrayList<>();

    @Override
    public void read(DataInput di, int length) throws IOException {
        _version = di.readUnsignedShort();
        int numBaseGlyphRecords = di.readUnsignedShort();
        _offsetBaseGlyphRecord = di.readInt();
        _offsetLayerRecord = di.readInt();
        int numLayerRecords = di.readUnsignedShort();

        int byteCount = 14;
        if (_offsetBaseGlyphRecord > byteCount) {
            di.skipBytes(byteCount - _offsetBaseGlyphRecord);
        }

        _baseGlyphRecords.ensureCapacity(numBaseGlyphRecords);
        for (int i = 0; i < numBaseGlyphRecords; ++i) {
            _baseGlyphRecords.add(new BaseGlyphRecord(di));
            byteCount += 6;
        }

        if (_offsetLayerRecord > byteCount) {
            di.skipBytes(byteCount - _offsetLayerRecord);
        }

        _layerRecords.ensureCapacity(numLayerRecords);
        for (int i = 0; i < numLayerRecords; ++i) {
            _layerRecords.add(new LayerRecord(di));
        }
    }
    
    /**
     * Table version number.
     */
    public int getVersion() {
        return _version;
    }

    @Override
    public int getType() {
        return COLR;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'COLR' Table\n");
        sb.append("------------\n");
        sb.append("    version = " + _version + "\n");
        sb.append("    Base Glyph Records\n");
        for (BaseGlyphRecord record : _baseGlyphRecords) {
            sb.append(String.format("%d : %d, %d\n", record.getGid(),
                    record.getFirstLayerIndex(), record.getNumLayers()));
        }
        sb.append("\n");
        sb.append("    Layer Records\n");
        for (LayerRecord record : _layerRecords) {
            sb.append(String.format("%d : %d\n", record.getGid(),
                    record.getPaletteIndex()));
        }
        return sb.toString();
    }

}
