/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/
 
package net.java.dev.typecast.ot.table;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import net.java.dev.typecast.ot.OTFontCollection;

/** 
 *
 * @version $Id: TableFactory.java,v 1.1.1.1 2004-12-05 23:15:00 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class TableFactory {

    public static Table create(OTFontCollection fc, DirectoryEntry de, DataInputStream dis) throws IOException {
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
            break;
        case Table.CFF:
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
            t = new GlyfTable(de, dis);
            break;
        case Table.hdmx:
            break;
        case Table.head:
            t = new HeadTable(de, dis);
            break;
        case Table.hhea:
            t = new HheaTable(de, dis);
            break;
        case Table.hmtx:
            t = new HmtxTable(de, dis);
            break;
        case Table.kern:
            t = new KernTable(de, dis);
            break;
        case Table.loca:
            t = new LocaTable(de, dis);
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
        case Table.vhea:
            break;
        case Table.vmtx:
            break;
        }
        
        // If we have a font collection, add this table to it
        if ((fc != null) && (t != null)) {
            fc.addTable(t);
        }
        return t;
    }
}
