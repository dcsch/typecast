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
 * OpenType font.
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public abstract class OTFont {

    private final TableDirectory _tableDirectory;
    
    /** 
     * Creates a {@link OTFont}.
     */
    public OTFont() {
        _tableDirectory = new TableDirectory(this);
    }
    
    /**
     * Creates a {@link OTFont} from the given binary font file data.
     * 
     * @see #read(byte[], int)
     */
    public OTFont(byte[] fontData, int tablesOrigin) throws IOException {
        this();
        read(fontData, tablesOrigin);
    }
    
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
    public void read(byte[] fontData, int tablesOrigin) throws IOException {
        _tableDirectory.read(fontData);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(fontData));
        dis.mark(fontData.length);
        dis.reset();

        // Load some prerequisite tables
        // (These are tables that are referenced by other tables, so we need to load
        // them first)
        initTable(dis, tablesOrigin, Table.head);

        // 'hhea' is required by 'hmtx'
        initTable(dis, tablesOrigin, Table.hhea);

        // 'maxp' is required by 'glyf', 'hmtx', 'loca', and 'vmtx'
        initTable(dis, tablesOrigin, Table.maxp);
        
        // 'vhea' is required by 'vmtx'
        initTable(dis, tablesOrigin, Table.vhea);

        // 'post' is required by 'glyf'
        initTable(dis, tablesOrigin, Table.post);

        // Load all the other required tables
        initTable(dis, tablesOrigin, Table.cmap);
        initTable(dis, tablesOrigin, Table.hmtx);
        initTable(dis, tablesOrigin, Table.name);
        initTable(dis, tablesOrigin, Table.OS_2);
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
    
    /**
     * Adds the given {@link Table} to this font.
     * 
     * <p>
     * If a {@link Table} with the same {@link Table#getType()} is already
     * present in this font, it is removed and returned.
     * </p>
     *
     * @param table
     * @return The {@link Table} with the same {@link Table#getType()} that was
     *         part of this font before.
     */
    public Table addTable(Table table) {
        return getTableDirectory().addTable(table);
    }
    
    public Table removeTable(int tag) {
        return getTableDirectory().removeTable(tag);
    }

    public Os2Table getOS2Table() {
        return getTableDirectory().os2();
    }
    
    public CmapTable getCmapTable() {
        return getTableDirectory().cmap();
    }
    
    public HeadTable getHeadTable() {
        return getTableDirectory().head();
    }
    
    public HheaTable getHheaTable() {
        return getTableDirectory().hhea();
    }
    
    public HmtxTable getHmtxTable() {
        return getTableDirectory().hmtx();
    }
    
    MaxpTable getMaxpTable() {
        return getTableDirectory().maxp();
    }

    public NameTable getNameTable() {
        return getTableDirectory().name();
    }

    public PostTable getPostTable() {
        return getTableDirectory().post();
    }

    public VheaTable getVheaTable() {
        return getTableDirectory().vhea();
    }

    public GsubTable getGsubTable() {
        return getTableDirectory().gsub();
    }

    public int getAscent() {
        return getHheaTable().getAscender();
    }

    public int getDescent() {
        return getHheaTable().getDescender();
    }

    public int getNumGlyphs() {
        return getMaxpTable().getNumGlyphs();
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

    @Override
    public String toString() {
        return getHeadTable().toString();
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
