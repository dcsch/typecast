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

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import net.java.dev.typecast.io.BinUtils;
import net.java.dev.typecast.io.BinaryOutput;
import net.java.dev.typecast.io.Writable;

/**
 * Glyph Data
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * @see "https://docs.microsoft.com/en-us/typography/opentype/spec/glyf"
 */
public class GlyfTable extends AbstractTable implements Writable {

    private final ArrayList<GlyfDescript> _descript = new ArrayList<>();

    /**
     * Creates a {@link GlyfTable}.
     */
    public GlyfTable(TableDirectory directory) {
        super(directory);
    }
    
    @Override
    public void read(DataInput di, int length) throws IOException {
        int numGlyphs = maxp().getNumGlyphs();
        _descript.ensureCapacity(numGlyphs);
        
        // Buffer the whole table so we can randomly access it
        byte[] buf = new byte[length];
        di.readFully(buf);
        
        ByteArrayInputStream bais = new ByteArrayInputStream(buf);
        
        // Process all the simple glyphs
        LocaTable locaTable = loca();
        for (int i = 0; i < numGlyphs; i++) {
            int offset = locaTable.getOffset(i);
            
            int len = locaTable.getOffset(i + 1) - offset;
            if (len > 0) {
                bais.reset();
                bais.skip(offset);
                DataInputStream dis = new DataInputStream(bais);
                short numberOfContours = dis.readShort();
                if (numberOfContours >= 0) {
                    _descript.add(new GlyfSimpleDescript(this, i, numberOfContours, dis));
                } else {
                    _descript.add(null);
                }
            } else {
                _descript.add(null);
            }
        }

        // Now do all the composite glyphs
        for (int i = 0; i < numGlyphs; i++) {
            int offset = locaTable.getOffset(i);
            
            int len = locaTable.getOffset(i + 1) - offset;
            if (len > 0) {
                bais.reset();
                bais.skip(offset);
                DataInputStream dis = new DataInputStream(bais);
                short numberOfContours = dis.readShort();
                if (numberOfContours < 0) {
                    _descript.set(i, new GlyfCompositeDescript(this, i, dis));
                }
            }
        }
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        long start = out.getPosition();
        int glyphId = 0;
        LocaTable locaTable = loca();
        for (GlyfDescript glyph : _descript) {
            BinUtils.padding2(out);
            
            long glyphOffset = out.getPosition() - start;
            locaTable.setOffset(glyphId, (int) glyphOffset);
            
            if (glyph != null) {
                glyph.write(out);
            }
            
            glyphId++;
        }

        long endOffset = out.getPosition() - start;
        locaTable.setOffset(glyphId, (int) endOffset);
        
        locaTable.updateFormat();
    }

    @Override
    public int getType() {
        return glyf;
    }
    
    /**
     * Number of glyphs.
     * 
     * @see #getDescription(int)
     */
    public int getNumGlyphs() {
        return _descript.size();
    }

    /**
     * The glyph with the given index.
     * 
     * @see #getNumGlyphs()
     */
    public GlyfDescript getDescription(int i) {
        if (i < _descript.size()) {
            return _descript.get(i);
        } else {
            return null;
        }
    }
    
    @Override
    public String toString() {
        return 
            "'glyf' Table - Glyph Data\n" +
            "-------------------------\n" + 
            "    numGlyphs:        " + getNumGlyphs() + "\n";
    }
    
    @Override
    public void dump(Writer out) throws IOException {
        super.dump(out);
        out.write("\n");
        
        for (int n = 0, cnt = getNumGlyphs(); n< cnt; n++) {
            GlyfDescript glyph = getDescription(n);
            out.write("    Glyph ");
            out.write(Integer.toString(n));
            out.write("\n");
            out.write("    ----------\n");
            if (glyph == null) {
                out.write("        None.\n");
            } else {
                out.write(glyph.toString());
                out.write("\n");
            }
            out.write("\n");
        }
    }

}
