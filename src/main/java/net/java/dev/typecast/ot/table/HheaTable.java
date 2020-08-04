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

    /**
     * Version 1.0 of {@link HheaTable}.
     */
    private static final int VERSION_1_0 = 0x00010000;
    
    private int _version = VERSION_1_0;
    private short _ascender;
    private short _descender;
    private short _lineGap;
    private short _advanceWidthMax;
    private short _minLeftSideBearing;
    private short _minRightSideBearing;
    private short _xMaxExtent;
    private short _caretSlopeRise;
    private short _caretSlopeRun;
    private short _caretOffset;
    private short _metricDataFormat;
    private int _numberOfHMetrics;

    @Override
    public void read(DataInput di, int length) throws IOException {
        _version = di.readInt();
        _ascender = di.readShort();
        _descender = di.readShort();
        _lineGap = di.readShort();
        _advanceWidthMax = di.readShort();
        _minLeftSideBearing = di.readShort();
        _minRightSideBearing = di.readShort();
        _xMaxExtent = di.readShort();
        _caretSlopeRise = di.readShort();
        _caretSlopeRun = di.readShort();
        _caretOffset = di.readShort();
        for (int i = 0; i < 4; i++) {
            di.readShort();
        }
        _metricDataFormat = di.readShort();
        _numberOfHMetrics = di.readUnsignedShort();
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        out.writeInt(_version);
        out.writeShort(_ascender);
        out.writeShort(_descender);
        out.writeShort(_lineGap);
        out.writeShort(_advanceWidthMax);
        out.writeShort(_minLeftSideBearing);
        out.writeShort(_minRightSideBearing);
        out.writeShort(_xMaxExtent);
        out.writeShort(_caretSlopeRise);
        out.writeShort(_caretSlopeRun);
        out.writeShort(_caretOffset);
        
        // Reserved.
        out.writeShort(0);
        out.writeShort(0);
        out.writeShort(0);
        out.writeShort(0);

        out.writeShort(_metricDataFormat);
        out.writeShort(_numberOfHMetrics);
    }

    @Override
    public int getType() {
        return hhea;
    }

    /**
     * Typographic ascent (Distance from baseline of highest ascender).
     */
    public short getAscender() {
        return _ascender;
    }
    
    /**
     * Typographic descent (Distance from baseline of lowest descender).
     */
    public short getDescender() {
        return _descender;
    }

    /**
     * Typographic line gap. Negative LineGap values are treated as zero in some
     * legacy platform implementations.
     */
    public short getLineGap() {
        return _lineGap;
    }

    /**
     * Maximum advance width value in 'hmtx' table.
     */
    public short getAdvanceWidthMax() {
        return _advanceWidthMax;
    }

    /**
     * Minimum left sidebearing value in 'hmtx' table.
     */
    public short getMinLeftSideBearing() {
        return _minLeftSideBearing;
    }

    /**
     * Minimum right sidebearing value; calculated as Min(aw - lsb - (xMax -
     * xMin)).
     */
    public short getMinRightSideBearing() {
        return _minRightSideBearing;
    }

    /**
     * Max(lsb + (xMax - xMin)).
     */
    public short getXMaxExtent() {
        return _xMaxExtent;
    }

    /**
     * Used to calculate the slope of the cursor (rise/run); 1 for vertical.
     */
    public short getCaretSlopeRise() {
        return _caretSlopeRise;
    }

    /**
     * 0 for vertical.
     */
    public short getCaretSlopeRun() {
        return _caretSlopeRun;
    }
    
    /**
     * The amount by which a slanted highlight on a glyph needs to be shifted to
     * produce the best appearance. Set to 0 for non-slanted fonts.
     */
    public short getCaretOffset() {
        return _caretOffset;
    }

    /**
     * 0 for current format.
     */
    public short getMetricDataFormat() {
        return _metricDataFormat;
    }

    /**
     * Number of hMetric entries in 'hmtx' table.
     */
    public int getNumberOfHMetrics() {
        return _numberOfHMetrics;
    }

    @Override
    public String toString() {
        return "'hhea' Table - Horizontal Header\n--------------------------------" +
                "\n        'hhea' version:       " + Fixed.floatValue(_version) +
                "\n        yAscender:            " + _ascender +
                "\n        yDescender:           " + _descender +
                "\n        yLineGap:             " + _lineGap +
                "\n        advanceWidthMax:      " + _advanceWidthMax +
                "\n        minLeftSideBearing:   " + _minLeftSideBearing +
                "\n        minRightSideBearing:  " + _minRightSideBearing +
                "\n        xMaxExtent:           " + _xMaxExtent +
                "\n        caretSlopeRise:       " + _caretSlopeRise +
                "\n        caretSlopeRun:        " + _caretSlopeRun +
                "\n        caretOffset:          " + _caretOffset +
                "\n        metricDataFormat:     " + _metricDataFormat +
                "\n        numberOfHMetrics:     " + _numberOfHMetrics;
    }

}
