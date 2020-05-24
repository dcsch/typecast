/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.io;

import java.io.DataOutput;
import java.io.IOException;

/**
 * Binary output.
 *
 * @author <a href="mailto:haui@haumacher.de">Bernhard Haumacher</a>
 */
public interface BinaryOutput extends DataOutput, RandomAccessBinary, AutoCloseable {

    /**
     * Reserves the given number of bytes at the current position for filling
     * them later on.
     *
     * @param length
     *        The number of bytes to reserve.
     * @return A {@link DataOutput} to fill the reserved bytes later on. To the
     *         returned {@link DataOutput} no more than the reserved number of
     *         bytes must be written.
     */
    BinaryOutput reserve(int length) throws IOException;
    
    @Override 
    void close() throws IOException;
    
}
