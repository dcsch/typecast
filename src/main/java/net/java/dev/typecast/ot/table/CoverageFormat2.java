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
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class CoverageFormat2 extends Coverage {

    private final int _rangeCount;
    private final RangeRecord[] _rangeRecords;

    /** Creates new CoverageFormat2 */
    CoverageFormat2(DataInput di) throws IOException {
        _rangeCount = di.readUnsignedShort();
        _rangeRecords = new RangeRecord[_rangeCount];
        for (int i = 0; i < _rangeCount; i++) {
            _rangeRecords[i] = new RangeRecord(di);
        }
    }

    @Override
    public int getFormat() {
        return 2;
    }

    @Override
    public int findGlyph(int glyphId) {
        for (int i = 0; i < _rangeCount; i++) {
            int n = _rangeRecords[i].getCoverageIndex(glyphId);
            if (n > -1) {
                return n;
            }
        }
        return -1;
    }

    @Override
    public int[] getGlyphIds() {
        List<Integer> ids = new ArrayList<>();
        for (RangeRecord record : _rangeRecords) {
            for (int i = record.getStart(); i < record.getEnd(); ++i) {
                ids.add(i);
            }
        }
        return ids.stream().mapToInt(i->i).toArray();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("                coverageFormat  = " + getFormat() + "\n");
        sb.append("                rangeCount      = " + _rangeCount + "\n");
        sb.append("                \n");
        
        int num = 1;
        for (RangeRecord record : _rangeRecords) {
            sb.append("                rangeRecord " + (num++) + "\n");
            sb.append("                ---------------\n");
            sb.append(record.toString());
            sb.append("                \n");
        }
        return sb.toString();
    }
    
}
