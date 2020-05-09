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
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class HmtxTable implements Table {

    private int[] _hMetrics;
    private short[] _leftSideBearing;
    private int _length;

    public HmtxTable(
            DataInput di,
            int length,
            HheaTable hhea,
            MaxpTable maxp) throws IOException {
        _hMetrics = new int[hhea.getNumberOfHMetrics()];
        for (int i = 0; i < hhea.getNumberOfHMetrics(); ++i) {
            _hMetrics[i] =
                    di.readUnsignedByte()<<24
                    | di.readUnsignedByte()<<16
                    | di.readUnsignedByte()<<8
                    | di.readUnsignedByte();
        }
        int lsbCount = maxp.getNumGlyphs() - hhea.getNumberOfHMetrics();
        _leftSideBearing = new short[lsbCount];
        for (int i = 0; i < lsbCount; ++i) {
            _leftSideBearing[i] = di.readShort();
        }
        _length = length;
    }

    @Override
    public int getType() {
        return hmtx;
    }

    public int getAdvanceWidth(int i) {
        if (_hMetrics == null) {
            return 0;
        }
        if (i < _hMetrics.length) {
            return _hMetrics[i] >> 16;
        } else {
            return _hMetrics[_hMetrics.length - 1] >> 16;
        }
    }

    public short getLeftSideBearing(int i) {
        if (_hMetrics == null) {
            return 0;
        }
        if (i < _hMetrics.length) {
            return (short)(_hMetrics[i] & 0xffff);
        } else {
            return _leftSideBearing[i - _hMetrics.length];
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'hmtx' Table - Horizontal Metrics\n---------------------------------\n");
        sb.append("Size = ").append(_length).append(" bytes, ")
            .append(_hMetrics.length).append(" entries\n");
        for (int i = 0; i < _hMetrics.length; i++) {
            sb.append("        ").append(i)
                .append(". advWid: ").append(getAdvanceWidth(i))
                .append(", LSdBear: ").append(getLeftSideBearing(i))
                .append("\n");
        }
        for (int i = 0; i < _leftSideBearing.length; i++) {
            sb.append("        LSdBear ").append(i + _hMetrics.length)
                .append(": ").append(_leftSideBearing[i])
                .append("\n");
        }
        return sb.toString();
    }

}
