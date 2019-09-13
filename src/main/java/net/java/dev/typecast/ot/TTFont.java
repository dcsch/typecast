package net.java.dev.typecast.ot;

import net.java.dev.typecast.ot.table.*;

import java.io.DataInputStream;
import java.io.IOException;

public class TTFont extends OTFont {

    private GlyfTable _glyf;
    private GaspTable _gasp;
    private KernTable _kern;
    private HdmxTable _hdmx;
    private VdmxTable _vdmx;

    /**
     * Constructor
     *
     * @param dis
     * @param tablesOrigin
     */
    public TTFont(DataInputStream dis, int tablesOrigin) throws IOException {
        super(dis, tablesOrigin);

        // Load the table directory
        dis.reset();
//        dis.skip(directoryOffset);
        TableDirectory tableDirectory = new TableDirectory(dis);

        // 'loca' is required by 'glyf'
        int length = seekTable(tableDirectory, dis, tablesOrigin, Table.loca);
        LocaTable loca = new LocaTable(dis, length, this.getHeadTable(), this.getMaxpTable());

        // If this is a TrueType outline, then we'll have at least the
        // 'glyf' table (along with the 'loca' table)
        length = seekTable(tableDirectory, dis, tablesOrigin, Table.glyf);
        _glyf = new GlyfTable(dis, length, this.getMaxpTable(), loca);

        length = seekTable(tableDirectory, dis, tablesOrigin, Table.gasp);
        if (length > 0) {
            _gasp = new GaspTable(dis);
        }

        length = seekTable(tableDirectory, dis, tablesOrigin, Table.kern);
        if (length > 0) {
            _kern = new KernTable(dis);
        }

        length = seekTable(tableDirectory, dis, tablesOrigin, Table.hdmx);
        if (length > 0) {
            _hdmx = new HdmxTable(dis, length, this.getMaxpTable());
        }

        length = seekTable(tableDirectory, dis, tablesOrigin, Table.VDMX);
        if (length > 0) {
            _vdmx = new VdmxTable(dis);
        }
    }

}
