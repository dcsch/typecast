/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package net.java.dev.typecast.ot;

import java.io.DataInputStream;
import java.io.IOException;

import net.java.dev.typecast.ot.table.TTCHeader;
import net.java.dev.typecast.ot.table.TableDirectory;
import net.java.dev.typecast.ot.table.Table;
import net.java.dev.typecast.ot.table.Os2Table;
import net.java.dev.typecast.ot.table.CmapTable;
import net.java.dev.typecast.ot.table.GlyfTable;
import net.java.dev.typecast.ot.table.HeadTable;
import net.java.dev.typecast.ot.table.HheaTable;
import net.java.dev.typecast.ot.table.HmtxTable;
import net.java.dev.typecast.ot.table.LocaTable;
import net.java.dev.typecast.ot.table.MaxpTable;
import net.java.dev.typecast.ot.table.NameTable;
import net.java.dev.typecast.ot.table.PostTable;
import net.java.dev.typecast.ot.table.TableFactory;

/**
 * The TrueType font.
 * @version $Id: OTFont.java,v 1.1.1.1 2004-12-05 23:14:30 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class OTFont {

    private OTFontCollection fc;
    private TableDirectory tableDirectory = null;
    private Table[] tables;
    private Os2Table os2;
    private CmapTable cmap;
    private GlyfTable glyf;
    private HeadTable head;
    private HheaTable hhea;
    private HmtxTable hmtx;
    private LocaTable loca;
    private MaxpTable maxp;
    private NameTable name;
    private PostTable post;

    /**
     * Constructor
     */
    public OTFont(OTFontCollection fc) {
        this.fc = fc;
    }

    public Table getTable(int tableType) {
        for (int i = 0; i < tables.length; i++) {
            if ((tables[i] != null) && (tables[i].getType() == tableType)) {
                return tables[i];
            }
        }
        return null;
    }

    public Os2Table getOS2Table() {
        return os2;
    }
    
    public CmapTable getCmapTable() {
        return cmap;
    }
    
    public HeadTable getHeadTable() {
        return head;
    }
    
    public HheaTable getHheaTable() {
        return hhea;
    }
    
    public HmtxTable getHmtxTable() {
        return hmtx;
    }
    
    public LocaTable getLocaTable() {
        return loca;
    }
    
    public MaxpTable getMaxpTable() {
        return maxp;
    }

    public NameTable getNameTable() {
        return name;
    }

    public PostTable getPostTable() {
        return post;
    }

    public int getAscent() {
        return hhea.getAscender();
    }

    public int getDescent() {
        return hhea.getDescender();
    }

    public int getNumGlyphs() {
        return maxp.getNumGlyphs();
    }

    public Glyph getGlyph(int i) {
        return (glyf.getDescription(i) != null)
            ? new Glyph(
                glyf.getDescription(i),
                hmtx.getLeftSideBearing(i),
                hmtx.getAdvanceWidth(i))
            : null;
    }

    public TableDirectory getTableDirectory() {
        return tableDirectory;
    }

    /**
     * @param dis OpenType/TrueType font file data
     */
    protected void read(DataInputStream dis, int offset) throws IOException {
        
        // Load the table directory
        dis.reset();
        dis.skip(offset);
        tableDirectory = new TableDirectory(dis);
        tables = new Table[tableDirectory.getNumTables()];

        // Load each of the tables
        for (int i = 0; i < tableDirectory.getNumTables(); i++) {
            dis.reset();
            dis.skip(offset + tableDirectory.getEntry(i).getOffset());
            tables[i] = TableFactory.create(fc, tableDirectory.getEntry(i), dis);
        }

        // Get references to commonly used tables
        os2 = (Os2Table) getTable(Table.OS_2);
        cmap = (CmapTable) getTable(Table.cmap);
        glyf = (GlyfTable) getTable(Table.glyf);
        head = (HeadTable) getTable(Table.head);
        hhea = (HheaTable) getTable(Table.hhea);
        hmtx = (HmtxTable) getTable(Table.hmtx);
        loca = (LocaTable) getTable(Table.loca);
        maxp = (MaxpTable) getTable(Table.maxp);
        name = (NameTable) getTable(Table.name);
        post = (PostTable) getTable(Table.post);

        // Initialize the tables that require it
        hmtx.init(hhea.getNumberOfHMetrics(), maxp.getNumGlyphs() - hhea.getNumberOfHMetrics());
        loca.init(maxp.getNumGlyphs(), head.getIndexToLocFormat() == 0);
        glyf.init(maxp.getNumGlyphs(), loca);
    }

    public String toString() {
        if (tableDirectory != null) {
            return tableDirectory.toString();
        } else {
            return "Empty font";
        }
    }
}
