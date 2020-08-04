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
 * Object that can serialize itself to a {@link BinaryOutput}.
 *
 * @author <a href="mailto:haui@haumacher.de">Bernhard Haumacher</a>
 */
public interface Writable {

    /** 
     * Writes this instance to the given {@link BinaryOutput}.
     */
    void write(BinaryOutput out) throws IOException;

}
