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
 * CPAL — Color Palette Table
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/cpal">Spec: Color Palette Table</a>
 */
public class CpalTable implements Table {

    static class ColorRecord {

        private final short _blue;
        private final short _green;
        private final short _red;
        private final short _alpha;

        ColorRecord(DataInput di) throws IOException {
            _blue = (short) di.readUnsignedByte();
            _green = (short) di.readUnsignedByte();
            _red = (short) di.readUnsignedByte();
            _alpha = (short) di.readUnsignedByte();
        }

        short getBlue() {
            return _blue;
        }

        short getGreen() {
            return _green;
        }

        short getRed() {
            return _red;
        }

        short getAlpha() {
            return _alpha;
        }
    }

    /**
     * Version 0 of {@link CpalTable}.
     */
    private static final int VERSION_0 = 0;

    /**
     * Version 1 of {@link CpalTable}.
     */
    private static final int VERSION_1 = 1;

    private int _version = VERSION_0;
    private int _numPalettesEntries;
    private int[] _colorRecordIndices;
    private final ArrayList<ColorRecord> _colorRecords = new ArrayList<>();

    @Override
    public void read(DataInput di, int length) throws IOException {
        _version = di.readUnsignedShort();
        _numPalettesEntries = di.readUnsignedShort();
        int numPalette = di.readUnsignedShort();
        int numColorRecords = di.readUnsignedShort();
        int offsetFirstColorRecord = di.readInt();

        int byteCount = 12;
        _colorRecordIndices = new int[numPalette];
        for (int i = 0; i < numPalette; ++i) {
            _colorRecordIndices[i] = di.readUnsignedShort();
            byteCount += 2;
        }
        
        int offsetPaletteTypeArray;
        int offsetPaletteLabelArray;
        int offsetPaletteEntryLabelArray;
        if (_version == VERSION_1) {
            offsetPaletteTypeArray = di.readInt();
            offsetPaletteLabelArray = di.readInt();
            offsetPaletteEntryLabelArray = di.readInt();
            byteCount += 12;
        } else {
            offsetPaletteTypeArray = -1;
            offsetPaletteLabelArray = -1;
            offsetPaletteEntryLabelArray = -1;
        }

        if (offsetFirstColorRecord > byteCount) {
            di.skipBytes(byteCount - offsetFirstColorRecord);
        }

        _colorRecords.ensureCapacity(numColorRecords);
        for (int i = 0; i < numColorRecords; ++i) {
            _colorRecords.add(new ColorRecord(di));
        }

        if (_version == VERSION_1) {
            // TODO find some sample version 1 content
        }
    }

    @Override
    public int getType() {
        return CPAL;
    }

    /**
     * Number of palette entries in each palette.
     */
    public int getNumPalettesEntries() {
        return _numPalettesEntries;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'CPAL' Table\n------------\nColor Record Indices\n");
        int i = 0;
        for (int index : _colorRecordIndices) {
            sb.append(String.format("%d: %d\n", i++, index));
        }
        sb.append("\nColor Records\n");
        i = 0;
        for (ColorRecord record : _colorRecords) {
            sb.append(String.format("%d: B: %3d, G: %3d, R: %3d, A: %3d\n",
                    i++, record.getBlue(), record.getGreen(), record.getRed(),
                    record.getAlpha()));
        }
        return sb.toString();
    }

}
