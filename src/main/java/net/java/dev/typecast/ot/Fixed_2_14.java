/*
 * Copyright (c) 2020 Business Operation Systems GmbH. All Rights Reserved.
 */
package net.java.dev.typecast.ot;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Utilities to convert 2.14 fixed floating point format.
 *
 * @author <a href="mailto:haui@haumacher.de">Bernhard Haumacher</a>
 */
public class Fixed_2_14 {

    /** 
     * Reads a value in fixed 2.14 floating point format.
     */
    public static double read(DataInput di) throws IOException {
        return toDouble(di.readShort());
    }

    private static double toDouble(int i) {
        return ((double) i) / 0x4000;
    }

    /** 
     * Writes a value in fixed 2.14 floating point format.
     */
    public static void write(DataOutput out, double value) throws IOException {
        out.writeShort(fromDouble(value));
    }

    private static short fromDouble(double value) {
        return (short) (value * 0x4000);
    }

}
