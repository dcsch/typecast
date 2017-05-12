
package net.java.dev.typecast.ot.table;

import java.io.DataInput;
import java.io.IOException;

/**
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class ColrTable implements Table {

    public class BaseGlyphRecord {
        private final int _gid;
        private final int _firstLayerIndex;
        private final int _numLayers;
        
        protected BaseGlyphRecord(DataInput di) throws IOException {
            _gid = di.readUnsignedShort();
            _firstLayerIndex = di.readUnsignedShort();
            _numLayers = di.readUnsignedShort();
        }
    }
    
    public class LayerRecord {
        private final int _gid;
        private final int _paletteIndex;
        
        protected LayerRecord(DataInput di) throws IOException {
            _gid = di.readUnsignedShort();
            _paletteIndex = di.readUnsignedShort();
        }
    }
    
    private final DirectoryEntry _de;
    private final int _version;
    private final int _numBaseGlyphRecords;
    private final int _offsetBaseGlyphRecord;
    private final int _offsetLayerRecord;
    private final int _numLayerRecords;
    private final BaseGlyphRecord[] _baseGlyphRecords;
    private final LayerRecord[] _layerRecords;

    protected ColrTable(DirectoryEntry de, DataInput di) throws IOException {
        this._de = (DirectoryEntry) de.clone();
        _version = di.readUnsignedShort();
        _numBaseGlyphRecords = di.readUnsignedShort();
        _offsetBaseGlyphRecord = di.readInt();
        _offsetLayerRecord = di.readInt();
        _numLayerRecords = di.readUnsignedShort();
        
        _baseGlyphRecords = new BaseGlyphRecord[_numBaseGlyphRecords];
        for (int i = 0; i < _numBaseGlyphRecords; ++i) {
            _baseGlyphRecords[i] = new BaseGlyphRecord(di);
        }
        
        _layerRecords = new LayerRecord[_numLayerRecords];
        for (int i = 0; i < _numLayerRecords; ++i) {
            _layerRecords[i] = new LayerRecord(di);
        }
    }

    @Override
    public String toString() {
        return new StringBuffer()
            .append("'COLR' Table\n---------------------------------------")
            .toString();
    }

    @Override
    public int getType() {
        return COLR;
    }

    @Override
    public DirectoryEntry getDirectoryEntry() {
        return _de;
    }
}
