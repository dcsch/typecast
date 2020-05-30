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
import net.java.dev.typecast.ot.table.KernTable;
import net.java.dev.typecast.ot.table.LocaTable;
import net.java.dev.typecast.ot.table.SVGTable;
import net.java.dev.typecast.ot.table.Table;
import net.java.dev.typecast.ot.table.VdmxTable;

public class TTFont extends OTFont {

    private GlyfTable _glyf;
    private GaspTable _gasp;
    private KernTable _kern;
    private HdmxTable _hdmx;
    private VdmxTable _vdmx;
    private SVGTable _svg;

    /**
     * Constructor
     *
     * @param fontData
     * @param tablesOrigin
     */
    public TTFont(byte[] fontData, int tablesOrigin) throws IOException {
        super(fontData, tablesOrigin);

        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(fontData));
        dis.mark(fontData.length);
        dis.reset();

        // 'loca' is required by 'glyf'
        LocaTable loca = (LocaTable) initTable(dis, tablesOrigin, Table.loca);
        if (loca != null) {
            // If this is a TrueType outline, then we'll have at least the
            // 'glyf' table (along with the 'loca' table)
            _glyf = (GlyfTable) initTable(dis, tablesOrigin, Table.glyf);
        }
        
        _svg = (SVGTable) initTable(dis, tablesOrigin, Table.svg);
        _gasp = (GaspTable) initTable(dis, tablesOrigin, Table.gasp);
        _kern = (KernTable) initTable(dis, tablesOrigin, Table.kern);
        _hdmx = (HdmxTable) initTable(dis, tablesOrigin, Table.hdmx);
        _vdmx = (VdmxTable) initTable(dis, tablesOrigin, Table.VDMX);
        
        getTableDirectory().initTables(dis, tablesOrigin);
    }

    public GlyfTable getGlyfTable() {
        return _glyf;
    }
    
    /**
     * Optional {@link SVGTable}.
     */
    public SVGTable getSvgTable() {
        return _svg;
    }

    public GaspTable getGaspTable() {
        return _gasp;
    }

    public KernTable getKernTable() {
        return _kern;
    }

    public HdmxTable getHdmxTable() {
        return _hdmx;
    }

    public VdmxTable getVdmxTable() {
        return _vdmx;
    }

    public Glyph getGlyph(int i) {
        return new TTGlyph(
                _glyf.getDescription(i),
                getHmtxTable().getLeftSideBearing(i),
                getHmtxTable().getAdvanceWidth(i));
    }

}
