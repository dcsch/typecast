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
import java.util.ArrayList;

/**
 * DSIG — Digital Signature Table
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/dsig">Spec: Digital Signature Table</a>
 */
public class DsigTable implements Table {

    private static final int VERSION_1 = 0x00000001;
    
    private int _version = VERSION_1;
    private int _flag;
    private final ArrayList<DsigEntry> _dsigEntry = new ArrayList<>();

    @Override
    public void read(DataInput di, int length) throws IOException {
        _version = di.readInt();
        int numSigs = di.readUnsignedShort();
        _flag = di.readUnsignedShort();
        _dsigEntry.ensureCapacity(numSigs);
        for (int i = 0; i < numSigs; i++) {
            _dsigEntry.add(new DsigEntry(di));
        }
        for (int i = 0; i < numSigs; i++) {
            _dsigEntry.get(i).setSignature(new SignatureBlock(di));
        }
    }
    
    /**
     * Version of the {@link DsigTable}.
     */
    public int getVersion() {
        return _version;
    }

    @Override
    public int getType() {
        return DSIG;
    }
    
    /**
     * Permission flags Bit 0: cannot be resigned, Bits 1-7: Reserved (Set to 0)
     */
    public int getFlags() {
        return _flag;
    }

    /** 
     * Number of signature blocks.
     * 
     * @see #getSignature(int)
     */
    public int getNumSigs() {
        return _dsigEntry.size();
    }

    /** 
     * The {@link SignatureBlock} with the given index.
     * 
     * @see #getNumSigs()
     */
    public SignatureBlock getSignature(int i) {
        return _dsigEntry.get(i).getSignature();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("DSIG table\n");
        sb.append("----------\n");
        sb.append("    version = " + getVersion() + "\n");
        sb.append("    flags   = " + getVersion() + "\n");
        for (int i = 0; i < getNumSigs(); i++) {
            sb.append(getSignature(i).toString());
        }
        return sb.toString();
    }
}
