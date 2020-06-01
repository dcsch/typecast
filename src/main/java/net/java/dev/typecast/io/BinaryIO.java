/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.io;

/**
 * Combination of {@link BinaryInput} and {@link BinaryOutput}.
 *
 * @author <a href="mailto:haui@haumacher.de">Bernhard Haumacher</a>
 */
public class BinaryIO {

    private BinaryInput _in;
    private BinaryOutput _out;

    /** 
     * Creates a {@link BinaryIO}.
     *
     * @param in See {@link #getIn()}.
     * @param out See {@link #getOut()}.
     */
    public BinaryIO(BinaryInput in, BinaryOutput out) {
        _in = in;
        _out = out;
    }

    /**
     * The underlying {@link BinaryInput}.
     */
    public BinaryInput getIn() {
        return _in;
    }
    
    /**
     * The underlying {@link BinaryOutput}.
     */
    public BinaryOutput getOut() {
        return _out;
    }
    
}
