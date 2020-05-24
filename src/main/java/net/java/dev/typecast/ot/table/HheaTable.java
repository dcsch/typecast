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
 * Horizontal Header Table
 * 
 * @see "https://docs.microsoft.com/en-us/typography/opentype/spec/hhea"
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class HheaTable implements Table, Writable {

    private int version;
    private short ascender;
    private short descender;
    private short lineGap;
    private short advanceWidthMax;
    private short minLeftSideBearing;
    private short minRightSideBearing;
    private short xMaxExtent;
    private short caretSlopeRise;
    private short caretSlopeRun;
    private short caretOffset;
    private short metricDataFormat;
    private int numberOfHMetrics;

    /**
     * Creates a {@link HheaTable} from the given input.
     *
     * @param di
     *        The input to read from.
     * @param length
     *        The total number of bytes.
     */
    public HheaTable(DataInput di, int length) throws IOException {
        version = di.readInt();
        ascender = di.readShort();
        descender = di.readShort();
        lineGap = di.readShort();
        advanceWidthMax = di.readShort();
        minLeftSideBearing = di.readShort();
        minRightSideBearing = di.readShort();
        xMaxExtent = di.readShort();
        caretSlopeRise = di.readShort();
        caretSlopeRun = di.readShort();
        caretOffset = di.readShort();
        for (int i = 0; i < 4; i++) {
            di.readShort();
        }
        metricDataFormat = di.readShort();
        numberOfHMetrics = di.readUnsignedShort();
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        out.writeInt(version);
        out.writeShort(ascender);
        out.writeShort(descender);
        out.writeShort(lineGap);
        out.writeShort(advanceWidthMax);
        out.writeShort(minLeftSideBearing);
        out.writeShort(minRightSideBearing);
        out.writeShort(xMaxExtent);
        out.writeShort(caretSlopeRise);
        out.writeShort(caretSlopeRun);
        out.writeShort(caretOffset);
        
        // Reserved.
        out.writeShort(0);
        out.writeShort(0);
        out.writeShort(0);
        out.writeShort(0);

        out.writeShort(metricDataFormat);
        out.writeShort(numberOfHMetrics);
    }

    @Override
    public int getType() {
        return hhea;
    }

    /**
     * Typographic ascent (Distance from baseline of highest ascender).
     */
    public short getAscender() {
        return ascender;
    }
    
    /**
     * Typographic descent (Distance from baseline of lowest descender).
     */
    public short getDescender() {
        return descender;
    }

    /**
     * Typographic line gap. Negative LineGap values are treated as zero in some
     * legacy platform implementations.
     */
    public short getLineGap() {
        return lineGap;
    }

    /**
     * Maximum advance width value in 'hmtx' table.
     */
    public short getAdvanceWidthMax() {
        return advanceWidthMax;
    }

    /**
     * Minimum left sidebearing value in 'hmtx' table.
     */
    public short getMinLeftSideBearing() {
        return minLeftSideBearing;
    }

    /**
     * Minimum right sidebearing value; calculated as Min(aw - lsb - (xMax -
     * xMin)).
     */
    public short getMinRightSideBearing() {
        return minRightSideBearing;
    }

    /**
     * Max(lsb + (xMax - xMin)).
     */
    public short getXMaxExtent() {
        return xMaxExtent;
    }

    /**
     * Used to calculate the slope of the cursor (rise/run); 1 for vertical.
     */
    public short getCaretSlopeRise() {
        return caretSlopeRise;
    }

    /**
     * 0 for vertical.
     */
    public short getCaretSlopeRun() {
        return caretSlopeRun;
    }
    
    /**
     * The amount by which a slanted highlight on a glyph needs to be shifted to
     * produce the best appearance. Set to 0 for non-slanted fonts.
     */
    public short getCaretOffset() {
        return caretOffset;
    }

    /**
     * 0 for current format.
     */
    public short getMetricDataFormat() {
        return metricDataFormat;
    }

    /**
     * Number of hMetric entries in 'hmtx' table.
     */
    public int getNumberOfHMetrics() {
        return numberOfHMetrics;
    }

    @Override
    public String toString() {
        return "'hhea' Table - Horizontal Header\n--------------------------------" +
                "\n        'hhea' version:       " + Fixed.floatValue(version) +
                "\n        yAscender:            " + ascender +
                "\n        yDescender:           " + descender +
                "\n        yLineGap:             " + lineGap +
                "\n        advanceWidthMax:      " + advanceWidthMax +
                "\n        minLeftSideBearing:   " + minLeftSideBearing +
                "\n        minRightSideBearing:  " + minRightSideBearing +
                "\n        xMaxExtent:           " + xMaxExtent +
                "\n        caretSlopeRise:       " + caretSlopeRise +
                "\n        caretSlopeRun:        " + caretSlopeRun +
                "\n        caretOffset:          " + caretOffset +
                "\n        metricDataFormat:     " + metricDataFormat +
                "\n        numberOfHMetrics:     " + numberOfHMetrics;
    }

}
