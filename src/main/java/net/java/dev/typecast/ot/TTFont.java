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

import net.java.dev.typecast.ot.table.GaspTable;
import net.java.dev.typecast.ot.table.GlyfTable;
import net.java.dev.typecast.ot.table.HdmxTable;
import net.java.dev.typecast.ot.table.HmtxTable;
import net.java.dev.typecast.ot.table.KernTable;
import net.java.dev.typecast.ot.table.LocaTable;
import net.java.dev.typecast.ot.table.SVGTable;
import net.java.dev.typecast.ot.table.Table;
import net.java.dev.typecast.ot.table.VdmxTable;

/**
 * TrueType Font.
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class TTFont extends OTFont {
   
    /** 
     * Creates a {@link TTFont}.
     */
    public TTFont() {
        super();
    }

    /**
     * Creates a {@link TTFont} from the given binary font file data.
     * 
     * @see #read(byte[], int)
     */
    public TTFont(byte[] fontData, int tablesOrigin) throws IOException {
        super(fontData, tablesOrigin);
    }

    @Override
    public void read(byte[] fontData, int tablesOrigin) throws IOException {
        super.read(fontData, tablesOrigin);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(fontData));
        dis.mark(fontData.length);
        dis.reset();

        // 'loca' is required by 'glyf'
        LocaTable loca = (LocaTable) initTable(dis, tablesOrigin, Table.loca);
        if (loca != null) {
            // If this is a TrueType outline, then we'll have at least the
            // 'glyf' table (along with the 'loca' table)
            initTable(dis, tablesOrigin, Table.glyf);
        }
        
        initTable(dis, tablesOrigin, Table.svg);
        initTable(dis, tablesOrigin, Table.gasp);
        initTable(dis, tablesOrigin, Table.kern);
        initTable(dis, tablesOrigin, Table.hdmx);
        initTable(dis, tablesOrigin, Table.VDMX);
        
        getTableDirectory().initTables(dis, tablesOrigin);
    }

    public GlyfTable getGlyfTable() {
        return getTableDirectory().glyf();
    }
    
    /**
     * Optional {@link SVGTable}.
     */
    public SVGTable getSvgTable() {
        return getTableDirectory().svg();
    }

    public GaspTable getGaspTable() {
        return getTableDirectory().gasp();
    }

    public KernTable getKernTable() {
        return getTableDirectory().kern();
    }

    public HdmxTable getHdmxTable() {
        return getTableDirectory().hdmx();
    }

    public VdmxTable getVdmxTable() {
        return getTableDirectory().vdmx();
    }

    public Glyph getGlyph(int i) {
        HmtxTable hmtxTable = getHmtxTable();
        return new TTGlyph(getGlyfTable().getDescription(i), 
            hmtxTable.getLeftSideBearing(i), hmtxTable.getAdvanceWidth(i));
    }

}
