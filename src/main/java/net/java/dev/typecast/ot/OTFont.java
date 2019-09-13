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

package net.java.dev.typecast.ot;

import java.io.DataInputStream;
import java.io.IOException;

import net.java.dev.typecast.ot.table.CmapTable;
import net.java.dev.typecast.ot.table.DirectoryEntry;
import net.java.dev.typecast.ot.table.HeadTable;
import net.java.dev.typecast.ot.table.HheaTable;
import net.java.dev.typecast.ot.table.HmtxTable;
import net.java.dev.typecast.ot.table.MaxpTable;
import net.java.dev.typecast.ot.table.NameTable;
import net.java.dev.typecast.ot.table.Os2Table;
import net.java.dev.typecast.ot.table.PostTable;
import net.java.dev.typecast.ot.table.Table;
import net.java.dev.typecast.ot.table.TableDirectory;
import net.java.dev.typecast.ot.table.VheaTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The TrueType font.
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class OTFont {

    private Os2Table _os2;
//    private CffTable _cff;
    private CmapTable _cmap;
    private HeadTable _head;
    private HheaTable _hhea;
    private HmtxTable _hmtx;
    private MaxpTable _maxp;
    private NameTable _name;
    private PostTable _post;
    private VheaTable _vhea;

    static final Logger logger = LoggerFactory.getLogger(OTFont.class);

    /**
     * @param dis OpenType/TrueType font file data.
     * @param directoryOffset The Table Directory offset within the file.  For a
     * regular TTF/OTF file this will be zero, but for a TTC (Font Collection)
     * the offset is retrieved from the TTC header.  For a Mac font resource,
     * offset is retrieved from the resource headers.
     * @param tablesOrigin The point the table offsets are calculated from.
     * Once again, in a regular TTF file, this will be zero.  In a TTC is is
     * also zero, but within a Mac resource, it is the beginning of the
     * individual font resource data.
     * @throws java.io.IOException
     */
    public OTFont(DataInputStream dis, int tablesOrigin) throws IOException {

        // Load the table directory
        dis.reset();
//        dis.skip(directoryOffset);
        TableDirectory tableDirectory = new TableDirectory(dis);

        // Load some prerequisite tables
        // (These are tables that are referenced by other tables, so we need to load
        // them first)
        seekTable(tableDirectory, dis, tablesOrigin, Table.head);
        _head = new HeadTable(dis);

        // 'hhea' is required by 'hmtx'
        seekTable(tableDirectory, dis, tablesOrigin, Table.hhea);
        _hhea = new HheaTable(dis);

        // 'maxp' is required by 'glyf', 'hmtx', 'loca', and 'vmtx'
        seekTable(tableDirectory, dis, tablesOrigin, Table.maxp);
        _maxp = new MaxpTable(dis);

        // 'vhea' is required by 'vmtx'
        int length = seekTable(tableDirectory, dis, tablesOrigin, Table.vhea);
        if (length > 0) {
            _vhea = new VheaTable(dis);
        }

        // 'post' is required by 'glyf'
        seekTable(tableDirectory, dis, tablesOrigin, Table.post);
        _post = new PostTable(dis);

        // Load all the other required tables
        seekTable(tableDirectory, dis, tablesOrigin, Table.cmap);
        _cmap = new CmapTable(dis);
        length = seekTable(tableDirectory, dis, tablesOrigin, Table.hmtx);
        _hmtx = new HmtxTable(dis, length, _hhea, _maxp);
        length = seekTable(tableDirectory, dis, tablesOrigin, Table.name);
        _name = new NameTable(dis, length);
        seekTable(tableDirectory, dis, tablesOrigin, Table.OS_2);
        _os2 = new Os2Table(dis);

        // If this is a TrueType outline, then we'll have at least the
        // 'glyf' table (along with the 'loca' table)
//        _glyf = (GlyfTable) getTable(Table.glyf);
    }

//    public Table getTable(int tableType) {
//        for (Table _table : _tables) {
//            if ((_table != null) && (_table.getType() == tableType)) {
//                return _table;
//            }
//        }
//        return null;
//    }

    public Os2Table getOS2Table() {
        return _os2;
    }
    
//    public CffTable getCffTable() {
//        return _cff;
//    }
    
    public CmapTable getCmapTable() {
        return _cmap;
    }
    
    public HeadTable getHeadTable() {
        return _head;
    }
    
    public HheaTable getHheaTable() {
        return _hhea;
    }
    
    public HmtxTable getHmtxTable() {
        return _hmtx;
    }
    
//    public LocaTable getLocaTable() {
//        return _loca;
//    }
    
    public MaxpTable getMaxpTable() {
        return _maxp;
    }

    public NameTable getNameTable() {
        return _name;
    }

    public PostTable getPostTable() {
        return _post;
    }

    public VheaTable getVheaTable() {
        return _vhea;
    }

    public int getAscent() {
        return _hhea.getAscender();
    }

    public int getDescent() {
        return _hhea.getDescender();
    }

    public int getNumGlyphs() {
        return _maxp.getNumGlyphs();
    }

//    // TODO What happens with the following when dealing with PostScript?
//    public Glyph getGlyph(int i) {
//        if (_glyf != null && _glyf.getDescription(i) != null) {
//            return new TTGlyph(
//                    _glyf.getDescription(i),
//                    _hmtx.getLeftSideBearing(i),
//                    _hmtx.getAdvanceWidth(i));
//        } else if (_cff != null && _cff.getFont(0).getCharstring(i) != null) {
//            return new T2Glyph(
//                    (CharstringType2) _cff.getFont(0).getCharstring(i),
//                    _hmtx.getLeftSideBearing(i),
//                    _hmtx.getAdvanceWidth(i));
//        } else {
//            return null;
//        }
//    }

    protected int seekTable(
            TableDirectory tableDirectory,
            DataInputStream dis,
            int tablesOrigin,
            int tag) throws IOException {
        dis.reset();
        DirectoryEntry entry = tableDirectory.getEntryByTag(tag);
        if (entry == null) {
            return 0;
        }
        dis.skip(tablesOrigin + entry.getOffset());
        return entry.getLength();
    }

//    protected void read(
//            DataInputStream dis,
//            int directoryOffset,
//            int tablesOrigin) throws IOException {
//    }

    public String toString() {
        return _head.toString();
    }
}
