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

/**
 * PCLT - PCL 5 Table
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/pclt">Spec: PCL 5 Table</a>
 */
public class PcltTable implements Table {

    /**
     * Version 1.0 of {@link PcltTable}.
     */
    public static final int VERSION_1_0 = 0x00010000;
    
    private int _version = VERSION_1_0;
    private long _fontNumber;
    private int _pitch;
    private int _xHeight;
    private int _style;
    private int _typeFamily;
    private int _capHeight;
    private int _symbolSet;
    private final char[] _typeface = new char[16];
    private final short[] _characterComplement = new short[8];
    private final char[] _fileName = new char[6];
    private short _strokeWeight;
    private short _widthType;
    private byte _serifStyle;

    @Override
    public void read(DataInput di, int length) throws IOException {
        _version = di.readInt();
        _fontNumber = di.readInt();
        _pitch = di.readUnsignedShort();
        _xHeight = di.readUnsignedShort();
        _style = di.readUnsignedShort();
        _typeFamily = di.readUnsignedShort();
        _capHeight = di.readUnsignedShort();
        _symbolSet = di.readUnsignedShort();
        for (int i = 0; i < 16; i++) {
            _typeface[i] = (char) di.readUnsignedByte();
        }
        for (int i = 0; i < 8; i++) {
            _characterComplement[i] = (short) di.readUnsignedByte();
        }
        for (int i = 0; i < 6; i++) {
            _fileName[i] = (char) di.readUnsignedByte();
        }
        _strokeWeight = (short) di.readUnsignedByte();
        _widthType = (short) di.readUnsignedByte();
        _serifStyle = di.readByte();
        
        // Reserved.
        di.readByte();
    }

    @Override
    public int getType() {
        return PCLT;
    }

    @Override
    public String toString() {
        return "'PCLT' Table - Printer Command Language Table\n---------------------------------------------" +
                "\n        version:             0x" + Integer.toHexString(_version).toUpperCase() +
                "\n        fontNumber:          " + _fontNumber + " (0x" + Long.toHexString(_fontNumber).toUpperCase() +
                ")\n        pitch:               " + _pitch +
                "\n        xHeight:             " + _xHeight +
                "\n        style:               0x" + _style +
                "\n        typeFamily:          0x" + (_typeFamily >> 12) +
                " " + (_typeFamily & 0xfff) +
                "\n        capHeight:           " + _capHeight +
                "\n        symbolSet:           " + _symbolSet +
                "\n        typeFace:            " + new String(_typeface) +
                "\n        characterComplement  0x" +
                Integer.toHexString(_characterComplement[0]).toUpperCase() +
                "\n        fileName:            " + new String(_fileName) +
                "\n        strokeWeight:        " + _strokeWeight +
                "\n        widthType:           " + _widthType +
                "\n        serifStyle:          " + _serifStyle;
    }

}
