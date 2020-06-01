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
import java.util.ArrayList;

/**
 * VDMX - Vertical Device Metrics Table for TrueType outlines.
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/vdmx">Spec: Vertical Device Metrics</a>
 */
public class VdmxTable implements Table {

    private static class RatioRange {
        
        private byte _bCharSet;
        private byte _xRatio;
        private byte _yStartRatio;
        private byte _yEndRatio;
        
        RatioRange(DataInput di) throws IOException {
            _bCharSet = di.readByte();
            _xRatio = di.readByte();
            _yStartRatio = di.readByte();
            _yEndRatio = di.readByte();
        }

        byte getBCharSet() {
            return _bCharSet;
        }
        
        byte getXRatio() {
            return _xRatio;
        }
        
        byte getYStartRatio() {
            return _yStartRatio;
        }
        
        byte getYEndRatio() {
            return _yEndRatio;
        }
    }
    
    private static class VTableRecord {
        
        private int _yPelHeight;
        private short _yMax;
        private short _yMin;
        
        VTableRecord(DataInput di) throws IOException {
            _yPelHeight = di.readUnsignedShort();
            _yMax = di.readShort();
            _yMin = di.readShort();
        }

        int getYPelHeight() {
            return _yPelHeight;
        }
        
        short getYMax() {
            return _yMax;
        }
        
        short getYMin() {
            return _yMin;
        }
    }
    
    private static class VDMXGroup {
        
        private int _recs;
        private int _startsz;
        private int _endsz;
        private VTableRecord[] _entry;
        
        VDMXGroup(DataInput di) throws IOException {
            _recs = di.readUnsignedShort();
            _startsz = di.readUnsignedByte();
            _endsz = di.readUnsignedByte();
            _entry = new VTableRecord[_recs];
            for (int i = 0; i < _recs; ++i) {
                _entry[i] = new VTableRecord(di);
            }
        }

        int getRecs() {
            return _recs;
        }
        
        int getStartSZ() {
            return _startsz;
        }
        
        int getEndSZ() {
            return _endsz;
        }
        
        VTableRecord[] getEntry() {
            return _entry;
        }
    }

    /**
     * Version 0 of {@link VdmxTable}, 
     */
    public static final int VERSION_0 = 0;
    
    /**
     * Version 1 of {@link VdmxTable}, 
     */
    public static final int VERSION_1 = 1;
    
    private int _version = VERSION_1;
    private final ArrayList<RatioRange> _ratRange = new ArrayList<>();
    private final ArrayList<VDMXGroup>  _groups = new ArrayList<>();
    
    @Override
    public void read(DataInput di, int length) throws IOException {
        _version = di.readUnsignedShort();
        int numRecs = di.readUnsignedShort();
        int numRatios = di.readUnsignedShort();
        _ratRange.ensureCapacity(numRatios);
        for (int i = 0; i < numRatios; ++i) {
            _ratRange.add(new RatioRange(di));
        }
        
        // TODO: Offsets are not used, why?
        int[] offset = new int[numRatios];
        for (int i = 0; i < numRatios; ++i) {
            offset[i] = di.readUnsignedShort();
        }

        _groups.ensureCapacity(numRecs);
        for (int i = 0; i < numRecs; ++i) {
            _groups.add(new VDMXGroup(di));
        }
    }
    
    @Override
    public int getType() {
        return VDMX;
    }
    
    /**
     * Version number.
     * 
     * @see #VERSION_0
     * @see #VERSION_1
     */
    public int getVersion() {
        return _version;
    }

    /** 
     * Number of ratio ranges and offsets.
     */
    public int getNumRatios() {
        return _ratRange.size();
    }

    /** 
     * Number of {@link VDMXGroup}s.
     * 
     * @see #getGroup(int)
     */
    public int getNumRecs() {
        return _groups.size();
    }

    /**
     * {@link VDMXGroup} with given index.
     * 
     * @see #getNumRecs()
     */
    public VDMXGroup getGroup(int index) {
        return _groups.get(index);
    }

    /**
     * {@link RatioRange} with the given index.
     * 
     * @see #getNumRatios()
     */
    public RatioRange getRatio(int index) {
        return _ratRange.get(index);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'VDMX' Table - Precomputed Vertical Device Metrics\n")
            .append("--------------------------------------------------\n")
            .append("  Version:                 ").append(_version).append("\n")
            .append("  Number of Hgt Records:   ").append(getNumRecs()).append("\n")
            .append("  Number of Ratio Records: ").append(getNumRatios()).append("\n");
        for (int i = 0; i < getNumRatios(); ++i) {
            RatioRange ratio = getRatio(i);
            sb.append("\n    Ratio Record #").append(i + 1).append("\n")
                .append("\tCharSetId     ").append(ratio.getBCharSet()).append("\n")
                .append("\txRatio        ").append(ratio.getXRatio()).append("\n")
                .append("\tyStartRatio   ").append(ratio.getYStartRatio()).append("\n")
                .append("\tyEndRatio     ").append(ratio.getYEndRatio()).append("\n");
        }
        sb.append("\n   VDMX Height Record Groups\n")
            .append("   -------------------------\n");
        for (int i = 0; i < getNumRecs(); ++i) {
            VDMXGroup group = getGroup(i);
            sb.append("   ").append(i + 1)
                .append(".   Number of Hgt Records  ").append(group.getRecs()).append("\n")
                .append("        Starting Y Pel Height  ").append(group.getStartSZ()).append("\n")
                .append("        Ending Y Pel Height    ").append(group.getEndSZ()).append("\n");
            for (int j = 0; j < group.getRecs(); ++j) {
                sb.append("\n            ").append(j + 1)
                    .append(". Pel Height= ").append(group.getEntry()[j].getYPelHeight()).append("\n")
                    .append("               yMax=       ").append(group.getEntry()[j].getYMax()).append("\n")
                    .append("               yMin=       ").append(group.getEntry()[j].getYMin()).append("\n");
            }
        }
        return sb.toString();
    }
    
}
