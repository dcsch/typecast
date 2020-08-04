/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.ot.table;

import java.io.DataInput;
import java.io.IOException;

import net.java.dev.typecast.io.BinaryOutput;
import net.java.dev.typecast.io.Writable;
import net.java.dev.typecast.ot.Fixed;

/**
 * Maximum Profile Table
 * 
 * @see "https://docs.microsoft.com/en-us/typography/opentype/spec/maxp"
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class MaxpTable implements Table, Writable {

    /**
     * Version 0.5 of {@link MaxpTable} (for CFF fonts).
     */
    public static final int VERSION_0_5 = 0x00005000;
    
    /**
     * Version 1.0 of {@link MaxpTable} (for TrueType fonts).
     */
    public static final int VERSION_1_0 = 0x00010000;
    
    private int _versionNumber = VERSION_1_0;
    private int _numGlyphs;
    private int _maxPoints;
    private int _maxContours;
    private int _maxCompositePoints;
    private int _maxCompositeContours;
    private int _maxZones;
    private int _maxTwilightPoints;
    private int _maxStorage;
    private int _maxFunctionDefs;
    private int _maxInstructionDefs;
    private int _maxStackElements;
    private int _maxSizeOfInstructions;
    private int _maxComponentElements;
    private int _maxComponentDepth;

    @Override
    public void read(DataInput di, int length) throws IOException {
        _versionNumber = di.readInt();
        
        if (_versionNumber == VERSION_0_5) {
            _numGlyphs = di.readUnsignedShort();
        } else if (_versionNumber == VERSION_1_0) {
            _numGlyphs = di.readUnsignedShort();
            _maxPoints = di.readUnsignedShort();
            _maxContours = di.readUnsignedShort();
            _maxCompositePoints = di.readUnsignedShort();
            _maxCompositeContours = di.readUnsignedShort();
            _maxZones = di.readUnsignedShort();
            _maxTwilightPoints = di.readUnsignedShort();
            _maxStorage = di.readUnsignedShort();
            _maxFunctionDefs = di.readUnsignedShort();
            _maxInstructionDefs = di.readUnsignedShort();
            _maxStackElements = di.readUnsignedShort();
            _maxSizeOfInstructions = di.readUnsignedShort();
            _maxComponentElements = di.readUnsignedShort();
            _maxComponentDepth = di.readUnsignedShort();
        }
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        out.writeInt(_versionNumber);
        out.writeShort(_numGlyphs);
        
        if (_versionNumber == VERSION_1_0) {
            out.writeShort(_maxPoints);
            out.writeShort(_maxContours);
            out.writeShort(_maxCompositePoints);
            out.writeShort(_maxCompositeContours);
            out.writeShort(_maxZones);
            out.writeShort(_maxTwilightPoints);
            out.writeShort(_maxStorage);
            out.writeShort(_maxFunctionDefs);
            out.writeShort(_maxInstructionDefs);
            out.writeShort(_maxStackElements);
            out.writeShort(_maxSizeOfInstructions);
            out.writeShort(_maxComponentElements);
            out.writeShort(_maxComponentDepth);
        }
    }

    @Override
    public int getType() {
        return maxp;
    }

    /**
     * CFF fonts use {@link #VERSION_0_5}, TrueType fonts use {@link #VERSION_1_0}.
     */
    public int getVersionNumber() {
        return _versionNumber;
    }

    /**
     * The number of glyphs in the font.
     */
    public int getNumGlyphs() {
        return _numGlyphs;
    }

    /**
     * Maximum points in a non-composite glyph.
     */
    public int getMaxPoints() {
        return _maxPoints;
    }

    /**
     * Maximum contours in a non-composite glyph.
     */
    public int getMaxContours() {
        return _maxContours;
    }

    /**
     * Maximum points in a composite glyph.
     */
    public int getMaxCompositePoints() {
        return _maxCompositePoints;
    }

    /**
     * Maximum contours in a composite glyph.
     */
    public int getMaxCompositeContours() {
        return _maxCompositeContours;
    }

    /**
     * 1 if instructions do not use the twilight zone (Z0), or 2 if instructions
     * do use Z0; should be set to 2 in most cases.
     */
    public int getMaxZones() {
        return _maxZones;
    }

    /**
     * Maximum points used in Z0.
     */
    public int getMaxTwilightPoints() {
        return _maxTwilightPoints;
    }

    /**
     * Number of Storage Area locations.
     */
    public int getMaxStorage() {
        return _maxStorage;
    }

    /**
     * Number of FDEFs, equal to the highest function number + 1.
     */
    public int getMaxFunctionDefs() {
        return _maxFunctionDefs;
    }

    /**
     * Number of IDEFs.
     */
    public int getMaxInstructionDefs() {
        return _maxInstructionDefs;
    }

    /**
     * Maximum stack depth across Font Program ('fpgm' table), CVT Program
     * ('prep' table) and all glyph instructions (in the 'glyf' table).
     */
    public int getMaxStackElements() {
        return _maxStackElements;
    }

    /**
     * Maximum byte count for glyph instructions.
     */
    public int getMaxSizeOfInstructions() {
        return _maxSizeOfInstructions;
    }

    /**
     * Maximum number of components referenced at “top level” for any composite glyph.
     */
    public int getMaxComponentElements() {
        return _maxComponentElements;
    }

    /**
     * Maximum levels of recursion; 1 for simple components.
     */
    public int getMaxComponentDepth() {
        return _maxComponentDepth;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'maxp' Table - Maximum Profile\n------------------------------")
            .append("\n        'maxp' version:         ").append(Fixed.floatValue(_versionNumber))
            .append("\n        numGlyphs:              ").append(_numGlyphs);
        if (_versionNumber == 0x00010000) {
            sb.append("\n        maxPoints:              ").append(_maxPoints)
                .append("\n        maxContours:            ").append(_maxContours)
                .append("\n        maxCompositePoints:     ").append(_maxCompositePoints)
                .append("\n        maxCompositeContours:   ").append(_maxCompositeContours)
                .append("\n        maxZones:               ").append(_maxZones)
                .append("\n        maxTwilightPoints:      ").append(_maxTwilightPoints)
                .append("\n        maxStorage:             ").append(_maxStorage)
                .append("\n        maxFunctionDefs:        ").append(_maxFunctionDefs)
                .append("\n        maxInstructionDefs:     ").append(_maxInstructionDefs)
                .append("\n        maxStackElements:       ").append(_maxStackElements)
                .append("\n        maxSizeOfInstructions:  ").append(_maxSizeOfInstructions)
                .append("\n        maxComponentElements:   ").append(_maxComponentElements)
                .append("\n        maxComponentDepth:      ").append(_maxComponentDepth);
        }
        sb.append("\n");
        return sb.toString();
    }

}
