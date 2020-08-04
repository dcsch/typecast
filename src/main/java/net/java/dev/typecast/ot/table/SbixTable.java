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

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.java.dev.typecast.ot.Bits;

/**
 * sbix — Standard Bitmap Graphics Table
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/sbix">Spec: Standard Bitmap Graphics Table</a>
 */
public class SbixTable extends AbstractTable {

    public static class GlyphDataRecord {
        private final short _originOffsetX;
        private final short _originOffsetY;
        private final int _graphicType;
        private final byte[] _data;
        
        private static final int PNG = 0x706E6720;
        
        GlyphDataRecord(DataInput di, int dataLength) throws IOException {
            _originOffsetX = di.readShort();
            _originOffsetY = di.readShort();
            _graphicType = di.readInt();
            
            // Check the graphicType is valid
            if (_graphicType != PNG) {
                logger.error("Invalid graphicType: {}", _graphicType);
                _data = null;
                return;
            }

            _data = new byte[dataLength];
            try {
                di.readFully(_data);
            } catch (IOException e) {
                logger.error("Reading too much data");
            }
        }
        
        public int getGraphicType() {
            return _graphicType;
        }
        
        public byte[] getData() {
            return _data;
        }
    }
    
    public static class Strike {
        private final int _ppem;
        private final int _resolution;
        private final long[] _glyphDataOffset;
        private final GlyphDataRecord[] _glyphDataRecord;
        
        Strike(ByteArrayInputStream bais, int numGlyphs) throws IOException {
            DataInput di = new DataInputStream(bais);
            _ppem = di.readUnsignedShort();
            _resolution = di.readUnsignedShort();
            _glyphDataOffset = new long[numGlyphs + 1];
            for (int i = 0; i < numGlyphs + 1; ++i) {
                _glyphDataOffset[i] = di.readInt();
            }

            _glyphDataRecord = new GlyphDataRecord[numGlyphs];
            for (int i = 0; i < numGlyphs; ++i) {
                int dataLength = (int)(_glyphDataOffset[i + 1] - _glyphDataOffset[i]);
                if (dataLength == 0)
                    continue;
                bais.reset();
                logger.trace("Skip: {}", _glyphDataOffset[i]);
                bais.skip(_glyphDataOffset[i]);
                _glyphDataRecord[i] = new GlyphDataRecord(new DataInputStream(bais), dataLength);
            }
            logger.debug("Loaded Strike: ppem = {}, resolution = {}", _ppem, _resolution);
        }
        
        public GlyphDataRecord[] getGlyphDataRecords() {
            return _glyphDataRecord;
        }
        
        @Override
        public String toString() {
            return String.format("ppem: %d, resolution: %d", _ppem, _resolution);
        }
    }

    /**
     * Version 1 of {@link SbixTable}.
     */
    public static final int VERSION_1 = 1;

    private static final int FLAGS_BASE = 0x0001;
    
    private static final int FLAG_OUTLINE = 0x0002;

    private int _version = VERSION_1;
    private int _flags = FLAGS_BASE;
    private final ArrayList<Strike> _strikes = new ArrayList<>();

    private static final Logger logger = LoggerFactory.getLogger(SbixTable.class);

    /**
     * Creates a {@link SbixTable}.
     */
    public SbixTable(TableDirectory directory) {
        super(directory);
    }
    
    @Override
    public void read(DataInput di, int length) throws IOException {
        // Load entire table into a buffer, and create another input stream
        byte[] buf = new byte[length];
        di.readFully(buf);
        DataInput di2 = new DataInputStream(getByteArrayInputStreamForOffset(buf, 0));

        _version = di2.readUnsignedShort();
        _flags = di2.readUnsignedShort();
        int numStrikes = di2.readInt();
        int[] _strikeOffset = new int[numStrikes];
        for (int i = 0; i < numStrikes; ++i) {
            _strikeOffset[i] = di2.readInt();
        }
        
        int numGlyphs = maxp().getNumGlyphs();
        _strikes.ensureCapacity(numStrikes);
        for (int i = 0; i < numStrikes; ++i) {
            ByteArrayInputStream bais = getByteArrayInputStreamForOffset(buf, _strikeOffset[i]);
            _strikes.add(new Strike(bais, numGlyphs));
        }
    }

    private ByteArrayInputStream getByteArrayInputStreamForOffset(byte[] buf, int offset) {
        return new ByteArrayInputStream(
                buf, offset,
                buf.length - offset);
    }
    
    @Override
    public int getType() {
        return sbix;
    }
    
    /**
     * Table version number
     */
    public int getVersion() {
        return _version;
    }
    
    /**
     * Bit 0: Set to 1.
     * Bit 1: Draw outlines.
     * Bits 2 to 15: reserved (set to 0).
     */
    public int getFlags() {
        return _flags;
    }
    
    /**
     * Whether to draw the bitmap and the outline, only bitmaps otherwis.
     */
    public boolean getDrawBitmapAndOutline() {
        return Bits.isSet(_flags, FLAG_OUTLINE);
    }
    
    /**
     * Number of {@link Strike}s.
     * 
     * @see #getStrike(int)
     */
    public int getNumStrikes() {
        return _strikes.size();
    }

    /**
     * {@link Strike} with the given index.
     * 
     * @see #getNumStrikes()
     */
    public Strike getStrike(int index) {
        return _strikes.get(index);
    }

}
