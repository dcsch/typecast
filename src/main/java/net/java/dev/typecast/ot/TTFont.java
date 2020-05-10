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
        int length = seekTable(dis, tablesOrigin, Table.loca);
        if (length > 0) {
            LocaTable loca = new LocaTable(dis, length, this.getHeadTable(), this.getMaxpTable());
            
            // If this is a TrueType outline, then we'll have at least the
            // 'glyf' table (along with the 'loca' table)
            length = seekTable(dis, tablesOrigin, Table.glyf);
            _glyf = new GlyfTable(dis, length, this.getMaxpTable(), loca);
        }
        
        length = seekTable(dis, tablesOrigin, Table.svg);
        if (length > 0) {
            _svg = new SVGTable(dis);
        }

        length = seekTable(dis, tablesOrigin, Table.gasp);
        if (length > 0) {
            _gasp = new GaspTable(dis);
        }

        length = seekTable(dis, tablesOrigin, Table.kern);
        if (length > 0) {
            _kern = new KernTable(dis);
        }

        length = seekTable(dis, tablesOrigin, Table.hdmx);
        if (length > 0) {
            _hdmx = new HdmxTable(dis, length, this.getMaxpTable());
        }

        length = seekTable(dis, tablesOrigin, Table.VDMX);
        if (length > 0) {
            _vdmx = new VdmxTable(dis);
        }
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

    @Override
    public void dumpTo(Writer out) throws IOException {
        super.dumpTo(out);
        dump(out, getGlyfTable());
        dump(out, getSvgTable());
        dump(out, getGaspTable());
        dump(out, getKernTable());
        dump(out, getHdmxTable());
        dump(out, getVdmxTable());
    }
}
