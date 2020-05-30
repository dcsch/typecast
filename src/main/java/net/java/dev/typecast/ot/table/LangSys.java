/*

 ============================================================================
                   The Apache Software License, Version 1.1
 ============================================================================

 Copyright (C) 1999-2003 The Apache Software Foundation. All rights reserved.

 Redistribution and use in source and binary forms, with or without modifica-
 tion, are permitted provided that the following conditions are met:

 1. Redistributions of  source code must  retain the above copyright  notice,
    this list of conditions and the following disclaimer.

 2. Redistributions in binary form must reproduce the above copyright notice,
    this list of conditions and the following disclaimer in the documentation
    and/or other materials provided with the distribution.

 3. The end-user documentation included with the redistribution, if any, must
    include  the following  acknowledgment:  "This product includes  software
    developed  by the  Apache Software Foundation  (http://www.apache.org/)."
    Alternately, this  acknowledgment may  appear in the software itself,  if
    and wherever such third-party acknowledgments normally appear.

 4. The names "Batik" and  "Apache Software Foundation" must  not  be
    used to  endorse or promote  products derived from  this software without
    prior written permission. For written permission, please contact
    apache@apache.org.

 5. Products  derived from this software may not  be called "Apache", nor may
    "Apache" appear  in their name,  without prior written permission  of the
    Apache Software Foundation.

 THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 FITNESS  FOR A PARTICULAR  PURPOSE ARE  DISCLAIMED.  IN NO  EVENT SHALL  THE
 APACHE SOFTWARE  FOUNDATION  OR ITS CONTRIBUTORS  BE LIABLE FOR  ANY DIRECT,
 INDIRECT, INCIDENTAL, SPECIAL,  EXEMPLARY, OR CONSEQUENTIAL  DAMAGES (INCLU-
 DING, BUT NOT LIMITED TO, PROCUREMENT  OF SUBSTITUTE GOODS OR SERVICES; LOSS
 OF USE, DATA, OR  PROFITS; OR BUSINESS  INTERRUPTION)  HOWEVER CAUSED AND ON
 ANY  THEORY OF LIABILITY,  WHETHER  IN CONTRACT,  STRICT LIABILITY,  OR TORT
 (INCLUDING  NEGLIGENCE OR  OTHERWISE) ARISING IN  ANY WAY OUT OF THE  USE OF
 THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

 This software  consists of voluntary contributions made  by many individuals
 on  behalf of the Apache Software  Foundation. For more  information on the
 Apache Software Foundation, please see <http://www.apache.org/>.

*/

package net.java.dev.typecast.ot.table;

import java.io.DataInput;
import java.io.IOException;

/**
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class LangSys {

    private int _lookupOrder;
    private int _reqFeatureIndex;
    private int _featureCount;
    private int[] _featureIndex;
    
    /** Creates new LangSys */
    LangSys(DataInput di) throws IOException {
        _lookupOrder = di.readUnsignedShort();
        _reqFeatureIndex = di.readUnsignedShort();
        _featureCount = di.readUnsignedShort();
        _featureIndex = new int[_featureCount];
        for (int i = 0; i < _featureCount; i++) {
            _featureIndex[i] = di.readUnsignedShort();
        }
    }
    
    public int getLookupOrder() {
        return _lookupOrder;
    }
    
    public int getReqFeatureIndex() {
        return _reqFeatureIndex;
    }
    
    public int getFeatureCount() {
        return _featureCount;
    }
    
    public int getFeatureIndex(int i) {
        return _featureIndex[i];
    }

    boolean isFeatureIndexed(int n) {
        for (int i = 0; i < _featureCount; i++) {
            if (_featureIndex[i] == n) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("                lookupOrder     = " + _lookupOrder + "\n");
        sb.append("                reqFeatureIndex = " + _reqFeatureIndex + "\n");
        sb.append("                featureCount    = " + _featureCount + "\n");
        sb.append("                featureIndex    = " + toString(_featureIndex) + "\n");
        sb.append("\n");
        return sb.toString();
    }

    static String toString(int[] featureIndex) {
        StringBuffer sb = new StringBuffer();
        for (int n = 0, cnt = featureIndex.length; n < cnt; n++) {
            if (n > 0) {
                sb.append(", ");
            }
            sb.append(featureIndex[n]);
        }
        return sb.toString();
    }
}

