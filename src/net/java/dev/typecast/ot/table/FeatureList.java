/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.ot.table;

import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: FeatureList.java,v 1.1.1.1 2004-12-05 23:14:38 davidsch Exp $
 */
public class FeatureList {

    private int featureCount;
    private FeatureRecord[] featureRecords;
    private Feature[] features;

    /** Creates new FeatureList */
    public FeatureList(RandomAccessFile raf, int offset) throws IOException {
        raf.seek(offset);
        featureCount = raf.readUnsignedShort();
        featureRecords = new FeatureRecord[featureCount];
        features = new Feature[featureCount];
        for (int i = 0; i < featureCount; i++) {
            featureRecords[i] = new FeatureRecord(raf);
        }
        for (int i = 0; i < featureCount; i++) {
            features[i] = new Feature(raf, offset + featureRecords[i].getOffset());
        }
    }

    public int getFeatureCount() {
        return featureCount;
    }
    
    public FeatureRecord getFeatureRecord(int i) {
        return featureRecords[i];
    }
    
    public Feature getFeature(int i) {
        return features[i];
    }

    public Feature findFeature(LangSys langSys, String tag) {
        if (tag.length() != 4) {
            return null;
        }
        int tagVal = (int)((tag.charAt(0)<<24)
            | (tag.charAt(1)<<16)
            | (tag.charAt(2)<<8)
            | tag.charAt(3));
        for (int i = 0; i < featureCount; i++) {
            if (featureRecords[i].getTag() == tagVal) {
                if (langSys.isFeatureIndexed(i)) {
                    return features[i];
                }
            }
        }
        return null;
    }

}
