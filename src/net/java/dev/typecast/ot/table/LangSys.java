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
 *
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: LangSys.java,v 1.1.1.1 2004-12-05 23:14:49 davidsch Exp $
 */
public class LangSys {

    private int lookupOrder;
    private int reqFeatureIndex;
    private int featureCount;
    private int[] featureIndex;
    
    /** Creates new LangSys */
    protected LangSys(DataInput di) throws IOException {
        lookupOrder = di.readUnsignedShort();
        reqFeatureIndex = di.readUnsignedShort();
        featureCount = di.readUnsignedShort();
        featureIndex = new int[featureCount];
        for (int i = 0; i < featureCount; i++) {
            featureIndex[i] = di.readUnsignedShort();
        }
    }
    
    public int getLookupOrder() {
        return lookupOrder;
    }
    
    public int getReqFeatureIndex() {
        return reqFeatureIndex;
    }
    
    public int getFeatureCount() {
        return featureCount;
    }
    
    public int getFeatureIndex(int i) {
        return featureIndex[i];
    }

    protected boolean isFeatureIndexed(int n) {
        for (int i = 0; i < featureCount; i++) {
            if (featureIndex[i] == n) {
                return true;
            }
        }
        return false;
    }

}

