/*
 * Typecast
 *
 * Copyright © 2004-2019 David Schweinsberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.java.dev.typecast.ot;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Writer;

import net.java.dev.typecast.io.BinaryIO;
import net.java.dev.typecast.ot.table.CmapTable;
import net.java.dev.typecast.ot.table.GsubTable;
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

/**
 * The TrueType font.
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public abstract class OTFont {

    private Os2Table _os2;
    private CmapTable _cmap;
    private HeadTable _head;
    private HheaTable _hhea;
    private HmtxTable _hmtx;
    private MaxpTable _maxp;
    private NameTable _name;
    private PostTable _post;
    private VheaTable _vhea;
    private GsubTable _gsub;
    private TableDirectory _tableDirectory;

    /**
     * @param fontData OpenType/TrueType font file data.
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
    OTFont(byte[] fontData, int tablesOrigin) throws IOException {
        _tableDirectory = new TableDirectory(fontData);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(fontData));
        dis.mark(fontData.length);
        dis.reset();

        // Load some prerequisite tables
        // (These are tables that are referenced by other tables, so we need to load
        // them first)
        _head = (HeadTable) initTable(dis, tablesOrigin, Table.head);

        // 'hhea' is required by 'hmtx'
        _hhea = (HheaTable) initTable(dis, tablesOrigin, Table.hhea);

        // 'maxp' is required by 'glyf', 'hmtx', 'loca', and 'vmtx'
        _maxp = (MaxpTable) initTable(dis, tablesOrigin, Table.maxp);
        
        // 'vhea' is required by 'vmtx'
        _vhea = (VheaTable) initTable(dis, tablesOrigin, Table.vhea);

        // 'post' is required by 'glyf'
        _post = (PostTable) initTable(dis, tablesOrigin, Table.post);

        // Load all the other required tables
        _cmap = (CmapTable) initTable(dis, tablesOrigin, Table.cmap);
        _hmtx = (HmtxTable) initTable(dis, tablesOrigin, Table.hmtx);
        _name = (NameTable) initTable(dis, tablesOrigin, Table.name);
        _os2 = (Os2Table) initTable(dis, tablesOrigin, Table.OS_2);
    }
    
    public void write(BinaryIO out) throws IOException {
        _tableDirectory.write(out);
    }
    
    /**
     * {@link TableDirectory} with all font tables.
     */
    public TableDirectory getTableDirectory() {
        return _tableDirectory;
    }

    public Os2Table getOS2Table() {
        return _os2;
    }
    
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
    
    MaxpTable getMaxpTable() {
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

    public GsubTable getGsubTable() {
        return _gsub;
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

    public abstract Glyph getGlyph(int i);

    protected Table initTable(DataInputStream dis, int tablesOrigin, int tag) throws IOException {
        TableDirectory directory = getTableDirectory();
        TableDirectory.Entry entry = directory.getEntryByTag(tag);
        if (entry == null) {
            return null;
        }
        
        return entry.initTable(dis, tablesOrigin);
    }

    public String toString() {
        return _head.toString();
    }
    
    /**
     * Dumps information of all tables to the given {@link Writer}.
     */
    public void dumpTo(Writer out) throws IOException {
        getTableDirectory().dumpTo(out);
    }

    /** 
     * Writes the toString() representation of the given table to the given {@link Writer}.
     */
    protected static void dump(Writer out, Table table) throws IOException {
        if (table != null) {
            table.dump(out);
            out.write("\n");
            out.write("\n");
        }
    }
    
    
}
