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

import net.java.dev.typecast.io.BinaryOutput;
import net.java.dev.typecast.io.Writable;
import net.java.dev.typecast.ot.Fmt;

/**
 * hmtx — Horizontal Metrics Table
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/hmtx">Spec: Horizontal Metrics Table</a>
 */
public class HmtxTable extends AbstractTable implements Writable {

    private int[] _advanceWidth;
    private short[] _leftSideBearing;
    private int _length;

    /**
     * Creates a {@link HmtxTable}.
     */
    public HmtxTable(TableDirectory directory) {
        super(directory);
    }
    
    @Override
    public void read(DataInput di, int length) throws IOException {
        int numberOfHMetrics = hhea().getNumberOfHMetrics();
        _advanceWidth = new int[numberOfHMetrics];
        
        // Left side bearings for glyph IDs greater than or equal to
        // numberOfHMetrics.
        int numGlyphs = maxp().getNumGlyphs();
        _leftSideBearing = new short[numGlyphs];

        // Paired advance width and left side bearing values for each glyph.
        // Records are indexed by glyph ID.
        for (int glyphId = 0; glyphId < numberOfHMetrics; ++glyphId) {
            _advanceWidth[glyphId] = di.readUnsignedShort();
            _leftSideBearing[glyphId] = di.readShort();
        }
        
        for (int glyphId = numberOfHMetrics; glyphId < numGlyphs; ++glyphId) {
            _leftSideBearing[glyphId] = di.readShort();
        }
        
        _length = length;
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        for (int n = 0, cnt = _advanceWidth.length; n < cnt; n++) {
            out.writeShort(_advanceWidth[n]);
            out.writeShort(_leftSideBearing[n]);
        }
        for (int n = _advanceWidth.length, cnt = _leftSideBearing.length; n < cnt; n++) {
            out.writeShort(_leftSideBearing[n]);
        }
    }

    @Override
    public int getType() {
        return hmtx;
    }

    /**
     * uint16
     * 
     * Advance width, in font design units.
     * 
     * <p>
     * The baseline is an imaginary line that is used to ‘guide’ glyphs when
     * rendering text. It can be horizontal (e.g., Latin, Cyrillic, Arabic) or
     * vertical (e.g., Chinese, Japanese, Mongolian). Moreover, to render text,
     * a virtual point, located on the baseline, called the pen position or
     * origin, is used to locate glyphs.
     * </p>
     * 
     * <p>
     * The distance between two successive pen positions is glyph-specific and
     * is called the advance width. Note that its value is always positive, even
     * for right-to-left oriented scripts like Arabic. This introduces some
     * differences in the way text is rendered.
     * </p>
     * 
     * @param i
     *        The glyph index, see {@link GlyfTable#getNumGlyphs()}.
     * 
     * @see "https://www.freetype.org/freetype2/docs/glyphs/glyphs-3.html"
     */
    public int getAdvanceWidth(int i) {
        if (i < _advanceWidth.length) {
            return _advanceWidth[i];
        } else {
            return _advanceWidth[_advanceWidth.length - 1];
        }
    }

    /**
     * int16
     * 
     * Glyph left side bearing, in font design units.
     * 
     * @param i
     *        The glyph index, see {@link GlyfTable#getNumGlyphs()}.
     * 
     * @see "https://www.freetype.org/freetype2/docs/glyphs/glyphs-3.html"
     */
    public short getLeftSideBearing(int i) {
        return _leftSideBearing[i];
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'hmtx' Table - Horizontal Metrics\n");
        sb.append("---------------------------------\n");
        sb.append("        Size:   ").append(_length).append(" bytes\n");
        sb.append("        Length: ").append(_leftSideBearing.length).append(" entries\n");
        for (int i = 0; i < _leftSideBearing.length; i++) {
            sb.append("        ").append(Fmt.pad(6, i)).append(": ");
            sb.append("adv=").append(getAdvanceWidth(i));
            sb.append(", lsb=").append(getLeftSideBearing(i));
            sb.append("\n");
        }
        return sb.toString();
    }

}
