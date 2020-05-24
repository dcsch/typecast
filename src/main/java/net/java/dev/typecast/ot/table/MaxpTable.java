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

    private static final int VERSION_1_0 = 0x00010000;
    private static final int VERSION_0_5 = 0x00005000;
    private int versionNumber;
    private int numGlyphs;
    private int maxPoints;
    private int maxContours;
    private int maxCompositePoints;
    private int maxCompositeContours;
    private int maxZones;
    private int maxTwilightPoints;
    private int maxStorage;
    private int maxFunctionDefs;
    private int maxInstructionDefs;
    private int maxStackElements;
    private int maxSizeOfInstructions;
    private int maxComponentElements;
    private int maxComponentDepth;

    /**
     * Creates a {@link MaxpTable} from the given input.
     *
     * @param di
     *        The input to read from.
     * @param length
     *        Total number of bytes.
     */
    public MaxpTable(DataInput di, int length) throws IOException {
        versionNumber = di.readInt();
        
        if (versionNumber == VERSION_0_5) {
            numGlyphs = di.readUnsignedShort();
        } else if (versionNumber == VERSION_1_0) {
            numGlyphs = di.readUnsignedShort();
            maxPoints = di.readUnsignedShort();
            maxContours = di.readUnsignedShort();
            maxCompositePoints = di.readUnsignedShort();
            maxCompositeContours = di.readUnsignedShort();
            maxZones = di.readUnsignedShort();
            maxTwilightPoints = di.readUnsignedShort();
            maxStorage = di.readUnsignedShort();
            maxFunctionDefs = di.readUnsignedShort();
            maxInstructionDefs = di.readUnsignedShort();
            maxStackElements = di.readUnsignedShort();
            maxSizeOfInstructions = di.readUnsignedShort();
            maxComponentElements = di.readUnsignedShort();
            maxComponentDepth = di.readUnsignedShort();
        }
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        out.writeInt(versionNumber);
        out.writeShort(numGlyphs);
        
        if (versionNumber == VERSION_1_0) {
            out.writeShort(maxPoints);
            out.writeShort(maxContours);
            out.writeShort(maxCompositePoints);
            out.writeShort(maxCompositeContours);
            out.writeShort(maxZones);
            out.writeShort(maxTwilightPoints);
            out.writeShort(maxStorage);
            out.writeShort(maxFunctionDefs);
            out.writeShort(maxInstructionDefs);
            out.writeShort(maxStackElements);
            out.writeShort(maxSizeOfInstructions);
            out.writeShort(maxComponentElements);
            out.writeShort(maxComponentDepth);
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
        return versionNumber;
    }

    /**
     * The number of glyphs in the font.
     */
    public int getNumGlyphs() {
        return numGlyphs;
    }

    /**
     * Maximum points in a non-composite glyph.
     */
    public int getMaxPoints() {
        return maxPoints;
    }

    /**
     * Maximum contours in a non-composite glyph.
     */
    public int getMaxContours() {
        return maxContours;
    }

    /**
     * Maximum points in a composite glyph.
     */
    public int getMaxCompositePoints() {
        return maxCompositePoints;
    }

    /**
     * Maximum contours in a composite glyph.
     */
    public int getMaxCompositeContours() {
        return maxCompositeContours;
    }

    /**
     * 1 if instructions do not use the twilight zone (Z0), or 2 if instructions
     * do use Z0; should be set to 2 in most cases.
     */
    public int getMaxZones() {
        return maxZones;
    }

    /**
     * Maximum points used in Z0.
     */
    public int getMaxTwilightPoints() {
        return maxTwilightPoints;
    }

    /**
     * Number of Storage Area locations.
     */
    public int getMaxStorage() {
        return maxStorage;
    }

    /**
     * Number of FDEFs, equal to the highest function number + 1.
     */
    public int getMaxFunctionDefs() {
        return maxFunctionDefs;
    }

    /**
     * Number of IDEFs.
     */
    public int getMaxInstructionDefs() {
        return maxInstructionDefs;
    }

    /**
     * Maximum stack depth across Font Program ('fpgm' table), CVT Program
     * ('prep' table) and all glyph instructions (in the 'glyf' table).
     */
    public int getMaxStackElements() {
        return maxStackElements;
    }

    /**
     * Maximum byte count for glyph instructions.
     */
    public int getMaxSizeOfInstructions() {
        return maxSizeOfInstructions;
    }

    /**
     * Maximum number of components referenced at “top level” for any composite glyph.
     */
    public int getMaxComponentElements() {
        return maxComponentElements;
    }

    /**
     * Maximum levels of recursion; 1 for simple components.
     */
    public int getMaxComponentDepth() {
        return maxComponentDepth;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'maxp' Table - Maximum Profile\n------------------------------")
            .append("\n        'maxp' version:         ").append(Fixed.floatValue(versionNumber))
            .append("\n        numGlyphs:              ").append(numGlyphs);
        if (versionNumber == 0x00010000) {
            sb.append("\n        maxPoints:              ").append(maxPoints)
                .append("\n        maxContours:            ").append(maxContours)
                .append("\n        maxCompositePoints:     ").append(maxCompositePoints)
                .append("\n        maxCompositeContours:   ").append(maxCompositeContours)
                .append("\n        maxZones:               ").append(maxZones)
                .append("\n        maxTwilightPoints:      ").append(maxTwilightPoints)
                .append("\n        maxStorage:             ").append(maxStorage)
                .append("\n        maxFunctionDefs:        ").append(maxFunctionDefs)
                .append("\n        maxInstructionDefs:     ").append(maxInstructionDefs)
                .append("\n        maxStackElements:       ").append(maxStackElements)
                .append("\n        maxSizeOfInstructions:  ").append(maxSizeOfInstructions)
                .append("\n        maxComponentElements:   ").append(maxComponentElements)
                .append("\n        maxComponentDepth:      ").append(maxComponentDepth);
        } else {
            sb.append("\n");
        }
        return sb.toString();
    }

}
