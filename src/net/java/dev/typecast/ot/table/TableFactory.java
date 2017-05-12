/*
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004-2016 David Schweinsberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.java.dev.typecast.ot.table;

import java.io.DataInputStream;
import java.io.IOException;
import net.java.dev.typecast.ot.OTFont;
import net.java.dev.typecast.ot.OTFontCollection;

/** 
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class TableFactory {

    public static Table create(
            OTFontCollection fc,
            OTFont font,
            DirectoryEntry de,
            DataInputStream dis) throws IOException {
        Table t = null;
        
        // First, if we have a font collection, look for the table there
        if (fc != null) {
            t = fc.getTable(de);
            if (t != null) {
                return t;
            }
        }
        
        // Create the table
        switch (de.getTag()) {
        case Table.BASE:
            t = new BaseTable(de, dis);
            break;
        case Table.CFF:
            t = new CffTable(de, dis);
            break;
        case Table.COLR:
            t = new ColrTable(de, dis);
            break;
        case Table.CPAL:
            t = new CpalTable(de, dis);
            break;
        case Table.DSIG:
            t = new DsigTable(de, dis);
            break;
        case Table.EBDT:
            break;
        case Table.EBLC:
            break;
        case Table.EBSC:
            break;
        case Table.GDEF:
            t = new GdefTable(de, dis);
            break;
        case Table.GPOS:
            t = new GposTable(de, dis);
            break;
        case Table.GSUB:
            t = new GsubTable(de, dis);
            break;
        case Table.JSTF:
            break;
        case Table.LTSH:
            t = new LtshTable(de, dis);
            break;
        case Table.MMFX:
            break;
        case Table.MMSD:
            break;
        case Table.OS_2:
            t = new Os2Table(de, dis);
            break;
        case Table.PCLT:
            t = new PcltTable(de, dis);
            break;
        case Table.VDMX:
            t = new VdmxTable(de, dis);
            break;
        case Table.cmap:
            t = new CmapTable(de, dis);
            break;
        case Table.cvt:
            t = new CvtTable(de, dis);
            break;
        case Table.fpgm:
            t = new FpgmTable(de, dis);
            break;
        case Table.fvar:
            break;
        case Table.gasp:
            t = new GaspTable(de, dis);
            break;
        case Table.glyf:
            t = new GlyfTable(de, dis, font.getMaxpTable(), font.getLocaTable());
            break;
        case Table.hdmx:
            t = new HdmxTable(de, dis, font.getMaxpTable());
            break;
        case Table.head:
            t = new HeadTable(de, dis);
            break;
        case Table.hhea:
            t = new HheaTable(de, dis);
            break;
        case Table.hmtx:
            t = new HmtxTable(de, dis, font.getHheaTable(), font.getMaxpTable());
            break;
        case Table.kern:
            t = new KernTable(de, dis);
            break;
        case Table.loca:
            t = new LocaTable(de, dis, font.getHeadTable(), font.getMaxpTable());
            break;
        case Table.maxp:
            t = new MaxpTable(de, dis);
            break;
        case Table.name:
            t = new NameTable(de, dis);
            break;
        case Table.prep:
            t = new PrepTable(de, dis);
            break;
        case Table.post:
            t = new PostTable(de, dis);
            break;
        case Table.sbix:
            t = new SbixTable(de, dis, font.getMaxpTable());
            break;
        case Table.vhea:
            t = new VheaTable(de, dis);
            break;
        case Table.vmtx:
            t = new VmtxTable(de, dis, font.getVheaTable(), font.getMaxpTable());
            break;
        }
        
        // If we have a font collection, add this table to it
        if ((fc != null) && (t != null)) {
            fc.addTable(t);
        }
        return t;
    }
}
