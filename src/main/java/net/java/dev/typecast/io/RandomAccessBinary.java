/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.io;

import java.io.IOException;

/**
 * Interface for random access binary outputs and inputs.
 *
 * @author <a href="mailto:haui@haumacher.de">Bernhard Haumacher</a>
 */
public interface RandomAccessBinary {

    /**
     * The current byte offset to write to in the created output.
     */
    long getPosition();
    
    /**
     * Moves the output offset to a new position.
     *
     * @param pos The new value for {@link #getPosition()}.
     */
    void setPosition(long pos) throws IOException;

}
