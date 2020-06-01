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

package net.java.dev.typecast.ot.table;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import net.java.dev.typecast.io.BinUtils;
import net.java.dev.typecast.io.BinaryIO;
import net.java.dev.typecast.io.BinaryOutput;
import net.java.dev.typecast.io.Writable;
import net.java.dev.typecast.ot.Fixed;
import net.java.dev.typecast.ot.OTFont;

/**
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class TableDirectory {

    /**
     * Table Record entry.
     */
    public static class Entry {

        private final TableDirectory _directory;
        private final int _tag;
        private int _checksum;
        private int _offset;
        private int _length;
        private Table _table;
        
        long _checkSumPos;
        
        /** 
         * Creates a {@link TableDirectory.Entry}.
         */
        public Entry(TableDirectory directory, int tag) {
            _directory = directory;
            _tag = tag;
        }
        
        /**
         * Reads an {@link Entry} from the given input.
         */
        public static Entry readFrom(TableDirectory directory, DataInput di) throws IOException {
            int tag = di.readInt();
            
            Entry result = new Entry(directory, tag);
            result.read(di);
            return result;
        }
        
        private void read(DataInput di) throws IOException {
            _checksum = di.readInt();
            _offset = di.readInt();
            _length = di.readInt();
        }
        
        /**
         * The owning {@link TableDirectory}.
         */
        public TableDirectory getDirectory() {
            return _directory;
        }

        void updateOffset(int offset, int length) {
            _offset = offset;
            _length = length;
        }

        Writable write(BinaryIO out) throws IOException {
            out.writeInt(_tag);
            
            // Checksum computed later on.
            _checkSumPos = out.getPosition();
            out.writeInt(0);
            BinaryOutput offsetOut = out.reserve(2 * 4);
            
            return new Writable() {
                @Override
                public void write(BinaryOutput out) throws IOException {
                    BinUtils.padding4(out);
                    
                    long offset = out.getPosition();
                    ((Writable) getTable()).write(out);
                    long length = out.getPosition() - offset;
                    
                    updateOffset((int) offset, (int) length);
                    
                    offsetOut.writeInt(getOffset());
                    offsetOut.writeInt(getLength());
                    
                    offsetOut.close();
                    
                    BinUtils.padding4(out);
                }
            };
        }

        void updateChecksum(BinaryIO io) throws IOException {
            _checksum = computeChecksum(io, getOffset(), getLength());
            
            io.setPosition(_checkSumPos);
            io.writeInt(_checksum);
        }

        /**
         * CheckSum for this table.
         * 
         * <p>
         * Table checksums are the unsigned sum of the uint32 units of a given
         * table. In C, the following function can be used to determine a
         * checksum:
         * </p>
         * 
         * <pre>
         * uint32 CalcTableChecksum(uint32 *Table, uint32 Length) {
         *   uint32 Sum = 0L;
         *   uint32 *Endptr = Table+((Length+3) & ~3) / sizeof(uint32);
         *   while (Table < EndPtr)
         *     Sum += *Table++;
         *   return Sum;
         * }
         * </pre>
         */
        public int getChecksum() {
            return _checksum;
        }

        public int getLength() {
            return _length;
        }

        /**
         * Offset values in the Table Record are measured from the start of the font file.
         */
        public int getOffset() {
            return _offset;
        }

        /**
         * Table identifier.
         * 
         * @see Table#CFF
         */
        public int getTag() {
            return _tag;
        }

        String getTagAsString() {
            return TableDirectory.toStringTag(getTag());
        }

        @Override
        public String toString() {
            return 
                "    '" + getTagAsString() + "'\n" + 
                "    ------\n" + 
                "        Checksum = 0x" + Integer.toHexString(_checksum) + "\n" + 
                "        Offset   = 0x" + Integer.toHexString(_offset) + "\n" + 
                "        Length   = " + _length + "\n";
        }

        /**
         * The {@link Table} corresponding to this {@link Entry}.
         */
        public Table getTable() {
            return _table;
        }
        
        Table setTable(Table table) {
            Table before = _table;
            _table = table;
            getDirectory().cacheTable(getTag(), table);
            return before;
        }

        Table readTable(DataInputStream di) throws IOException {
            Table table = createTable();
            if (table != null) {
                table.read(di, getLength());
            }
            return table;
        }

        private Table createTable() {
            switch (getTag()) {
                case Table.CFF:
                    // TODO: Implemented but does not work.
                    // return new CffTable(di, getLength());
                    return null;
                case Table.cmap:
                    return new CmapTable();
                case Table.COLR:
                    return new ColrTable();
                case Table.CPAL:
                    return new CpalTable();
                case Table.cvt:
                    return  new CvtTable();
                case Table.DSIG:
                    return new DsigTable();
                case Table.EBDT:
                case Table.EBLC:
                case Table.EBSC:
                    // TODO: Not supported.
                    return null;
                case Table.fpgm:
                    return new FpgmTable();
                case Table.fvar:
                    // TODO: Not supported.
                    return null;
                case Table.gasp:
                    return new GaspTable();
                case Table.GDEF:
                    return new GdefTable();
                case Table.glyf: {
                    return new GlyfTable(getDirectory());
                }
                case Table.GPOS:
                    return new GposTable();
                case Table.GSUB:
                    return new GsubTable();
                case Table.hdmx:
                    return new HdmxTable(getDirectory());
                case Table.head:
                    return new HeadTable();
                case Table.hhea:
                    return new HheaTable();
                case Table.hmtx:
                    return new HmtxTable(getDirectory());
                case Table.JSTF:
                    // TODO: Not supported.
                    return null;
                case Table.kern:
                    return new KernTable();
                case Table.loca:
                    return new LocaTable(getDirectory());
                case Table.LTSH:
                    return new LtshTable();
                case Table.maxp:
                    return new MaxpTable();
                case Table.MMFX:
                case Table.MMSD:
                    // TODO: Not supported.
                    return null;
                case Table.name:
                    return new NameTable();
                case Table.OS_2:
                    return new Os2Table();
                case Table.PCLT:
                    return new PcltTable();
                case Table.post:
                    return new PostTable();
                case Table.prep:
                    return new PrepTable();
                case Table.sbix:
                    return new SbixTable(getDirectory());
                case Table.svg:
                    return new SVGTable();
                case Table.VDMX:
                    return new VdmxTable();
                case Table.vhea:
                    return new VheaTable();
                case Table.vmtx:
                    return new VmtxTable(getDirectory());
                default:
                    // Not supported.
                    return null;
            }
        }

        public Table initTable(DataInputStream di, int tablesOrigin) throws IOException {
            Table table = getTable();
            if (table == null) {
                seekTable(di, tablesOrigin, this);
                table = readTable(di);
                setTable(table);
            }
            return table;
        }

        private Entry seekTable(DataInputStream dis, int tablesOrigin, Entry entry) throws IOException {
            dis.reset();
            dis.skip(tablesOrigin + entry.getOffset());
            return entry;
        }

        /**
         * Writes this {@link Entry} to the given writer, including the
         * {@link Table#dump(Writer) table dump}.
         */
        public void dumpTo(Writer out) throws IOException {
            if (getTable() != null) {
                getTable().dump(out);
            }
        }
    }

    private final OTFont _font;
    
    private int _sfntVersion = TRUE_TYPE;
    private short _searchRange;
    private short _entrySelector;
    private short _rangeShift;
    
    private final ArrayList<Entry> _entries = new ArrayList<>();

    private HeadTable _head;

    private MaxpTable _maxp;

    private LocaTable _loca;

    private GlyfTable _glyf;

    private NameTable _name;

    private HmtxTable _hmtx;

    private GsubTable _gsub;

    private CmapTable _cmap;

    private PostTable _post;

    private Os2Table _os2;

    private HheaTable _hhea;

    private VheaTable _vhea;

    private SVGTable _svg;

    private HdmxTable _hdmx;

    private VdmxTable _vdmx;

    private KernTable _kern;

    private GaspTable _gasp;

    /**
     * Creates a {@link TableDirectory}.
     *
     * @param font See {@link #getFont()}.
     */
    public TableDirectory(OTFont font) {
        _font = font;
    }
    
    /**
     * The {@link OTFont} this directory belongs to.
     */
    public OTFont getFont() {
        return _font;
    }
    
    public void read(byte[] fontData) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(fontData));
        _sfntVersion = dis.readInt();
        short numTables = dis.readShort();
        _searchRange = dis.readShort();
        _entrySelector = dis.readShort();
        _rangeShift = dis.readShort();
        _entries.ensureCapacity(numTables);
        for (int i = 0; i < numTables; i++) {
            _entries.add(Entry.readFrom(this, dis));
        }
    }
    
    /**
     * Reads all supported table not read so far.
     */
    public void initTables(DataInputStream di, int tablesOrigin) throws IOException {
        // Initialize tables in reverse order of occurrence, since the format is
        // constructed so that information derived from former tables is stored
        // in later ones. While reading this information from latter tables is
        // required for reading former tables.
        for (int n = _entries.size() - 1; n >= 0; n--) {
            Entry entry = getEntry(n);
            entry.initTable(di, tablesOrigin);
        }
    }
    
    /**
     * Adds the given {@link Table} to this {@link TableDirectory}.
     *
     * @param table
     *        The {@link Table} to add.
     * @return The {@link Table} with the same {@link Table#getType()} that was
     *         overwritten.
     */
    public Table addTable(Table table) {
        int tag = table.getType();
        Entry entry = getEntryByTag(tag);
        if (entry == null) {
            entry = new Entry(this, tag);
            _entries.add(entry);
        }
        return entry.setTable(table);
    }
    
    /** 
     * Removes the {@link Table} with the given {@link Table#getType()}.
     */
    public Table removeTable(int tag) {
        return getEntryByTag(tag).setTable(null);
    }

    void cacheTable(int tag, Table table) {
        switch (tag) {
        case Table.head:
            _head = (HeadTable) table;
            break;
        case Table.maxp:
            _maxp = (MaxpTable) table;
            break;
        case Table.loca:
            _loca = (LocaTable) table;
            break;
        case Table.glyf:
            _glyf = (GlyfTable) table;
            break;
        case Table.name:
            _name = (NameTable) table;
            break;
        case Table.hmtx:
            _hmtx = (HmtxTable) table;
            break;
        case Table.GSUB:
            _gsub = (GsubTable) table;
            break;
        case Table.cmap:
            _cmap = (CmapTable) table;
            break;
        case Table.post:
            _post = (PostTable) table;
            break;
        case Table.OS_2:
            _os2 = (Os2Table) table;
            break;
        case Table.hhea:
            _hhea = (HheaTable) table;
            break;
        case Table.vhea:
            _vhea = (VheaTable) table;
            break;
        case Table.svg:
            _svg = (SVGTable) table;
            break;
        case Table.hdmx:
            _hdmx = (HdmxTable) table;
            break;
        case Table.VDMX:
            _vdmx = (VdmxTable) table;
            break;
        case Table.kern:
            _kern = (KernTable) table;
            break;
        case Table.gasp:
            _gasp = (GaspTable) table;
            break;
        }
    }

    /**
     * @see HeadTable
     */
    public HeadTable head() {
        return _head;
    }
    
    /**
     * @see MaxpTable
     */
    public MaxpTable maxp() {
        return _maxp;
    }
    
    /**
     * @see LocaTable
     */
    public LocaTable loca() {
        return _loca;
    }

    /**
     * @see GlyfTable
     */
    public GlyfTable glyf() {
        return _glyf;
    }
    
    /**
     * @see NameTable
     */
    public NameTable name() {
        return _name;
    }
    
    /**
     * @see HmtxTable
     */
    public HmtxTable hmtx() {
        return _hmtx;
    }
    
    /**
     * @see GsubTable
     */
    public GsubTable gsub() {
        return _gsub;
    }
    
    /**
     * @see CmapTable
     */
    public CmapTable cmap() {
        return _cmap;
    }
    
    /**
     * @see PostTable
     */
    public PostTable post() {
        return _post;
    }
    
    /**
     * @see Os2Table
     */
    public Os2Table os2() {
        return _os2;
    }
    
    /**
     * @see HheaTable
     */
    public HheaTable hhea() {
        return _hhea;
    }

    /**
     * @see VheaTable
     */
    public VheaTable vhea() {
        return _vhea;
    }
    
    /**
     * @see SVGTable
     */
    public SVGTable svg() {
        return _svg;
    }
    
    /**
     * @see HdmxTable
     */
    public HdmxTable hdmx() {
        return _hdmx;
    }
    
    /**
     * @see VdmxTable
     */
    public VdmxTable vdmx() {
        return _vdmx;
    }
    
    /**
     * @see KernTable
     */
    public KernTable kern() {
        return _kern;
    }
    
    /**
     * @see GaspTable
     */
    public GaspTable gasp() {
        return _gasp;
    }
    
    /**
     * Writes this {@link TableDirectory} and all of its {@link Entry entries}
     * to the given output.
     */
    public void write(BinaryIO io) throws IOException {
        long start = io.getPosition();
        
        // Table Record:
        // Entries in the Table Record must be sorted in ascending order by tag.
        List<Entry> entries = _entries.stream()
                .filter(e -> e.getTable() instanceof Writable)
                .sorted((e1, e2) -> Long.compare(0xFFFFFFFF & e1.getTag(), 0xFFFFFFFF & e2.getTag()))
                .collect(Collectors.toList());
        
        int numTables = entries.size();
        
        // Offset Table:
        io.writeInt(_sfntVersion);
        io.writeShort(numTables);
        io.writeShort(getSearchRange(numTables));
        io.writeShort(getEntrySelector(numTables));
        io.writeShort(getRangeShift(numTables));
        
        List<Writable> tableOuts = new ArrayList<>(numTables);
        for (Entry entry : entries) {
            tableOuts.add(entry.write(io));
        }
        
        for (Writable tableOut : tableOuts) {
            tableOut.write(io);
        }
        
        long length = io.getPosition() - start;

        for (Entry entry : entries) {
            entry.updateChecksum(io);
        }
        
        int checksumAdjustment = 0xB1B0AFBA - computeChecksum(io, start, length);
        head().updateChecksumAdjustment(io, checksumAdjustment);
    }
    
    static int computeChecksum(BinaryIO io, long start, long length) throws IOException {
        io.setPosition(start);
        
        int sum = 0;
        for (long n = 0, cnt = (length + 3) / 4; n < cnt; n++) {
            sum += io.readInt();
        }
        return sum;
    }

    /**
     * The {@link Table} entry with the given index.
     * 
     * @see #getNumTables()
     */
    public Entry getEntry(int index) {
        return _entries.get(index);
    }

    /**
     * The entry for the {@link Table} with the given {@link Table#getType()}.
     *
     * @param tag
     *        The {@link Table#getType() table type}.
     * @return The {@link Entry} for the given table type.
     */
    public Entry getEntryByTag(int tag) {
        for (int i = 0; i < getNumTables(); i++) {
            Entry entry = getEntry(i);
            if (entry.getTag() == tag) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Number of {@link Table} entries in this font.
     * 
     * @see #getEntry(int)
     */
    public int getNumTables() {
        return _entries.size();
    }

    /**
     * (Maximum power of 2 <= {@link #getNumTables()}) x 16.
     */
    public short getSearchRange() {
        return getSearchRange(getNumTables());
    }

    private short getSearchRange(int numTables) {
        int maxPow = 1;
        while (true) {
            int next = maxPow << 1;
            if (next > numTables) {
                break;
            }
            maxPow = next;
        }
        return (short) (maxPow * 16);
    }
    
    /**
     * Log2(maximum power of 2 <= {@link #getNumTables()}).
     */
    public short getEntrySelector() {
        return getEntrySelector(getNumTables());
    }

    private short getEntrySelector(int numTables) {
        int log = 0;
        int maxPow = 1;
        while (true) {
            int next = maxPow << 1;
            if (next > numTables) {
                break;
            }
            maxPow = next;
            log++;
        }

        return (short) log;
    }

    /**
     * NumTables x 16-{@link #getSearchRange()}.
     */
    public short getRangeShift() {
        return getRangeShift(getNumTables());
    }

    private short getRangeShift(int numTables) {
        return (short) (numTables * 16 - getSearchRange(numTables));
    }

    /**
     * Version of OpenType fonts that contain TrueType outlines.
     * 
     * @see #getVersion()
     */
    public static final int TRUE_TYPE = 0x00010000;

    /**
     * Version of OpenType fonts containing CFF data (version 1 or 2)
     * 
     * <p>
     * Value is 'OTTO, when re-interpreted as a Tag.
     * </p>
     * 
     * @see #getVersion()
     */
    public static final int OPEN_TYPE = 0x4F54544F;
    
    /**
     * uint32 sfntVersion {@link #TRUE_TYPE}  or {@link #OPEN_TYPE}
     * 
     * <p>
     * OpenType fonts that contain TrueType outlines should use the value of
     * {@link #TRUE_TYPE} for the sfntVersion. OpenType fonts containing CFF data
     * (version 1 or 2) should use {@link #OPEN_TYPE} for sfntVersion.
     * </p>
     * 
     * <p>
     * Note: The Apple specification for TrueType fonts allows for 'true' and
     * 'typ1' for sfnt version. These version tags should not be used for fonts
     * which contain OpenType tables.
     * </p>
     */
    public int getVersion() {
        return _sfntVersion;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder()
            .append("Offset Table\n------ -----")
            .append("\n    sfntVersion:   ").append(Fixed.floatValue(_sfntVersion))
            .append("\n    numTables:     ").append(getNumTables())
            .append("\n    searchRange:   ").append(_searchRange)
            .append("\n    entrySelector: ").append(_entrySelector)
            .append("\n    rangeShift:    ").append(_rangeShift)
            .append("\n\n");
        for (Entry entry : _entries) {
            sb.append(entry.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Converts a {@link Table} tag back to {@link String}.
     */
    public static String toStringTag(int tag) {
        return String.valueOf(
            (char) ((tag >> 24) & 0xff)) +
            (char) ((tag >> 16) & 0xff) +
            (char) ((tag >> 8) & 0xff) +
            (char) (tag & 0xff);
    }

    /**
     * Converts a {@link Table} tag {@link String} to an ID.
     */
    public static int fromStringTag(String tag) {
        assert tag.length() == 4;
        return 
            ((tag.charAt(0) & 0xFF) << 24) |
            ((tag.charAt(1) & 0xFF) << 16) |
            ((tag.charAt(2) & 0xFF) << 8) |
            (tag.charAt(3) & 0xff);
    }

    /**
     * Writes the {@link TableDirectory} and all {@link Table#dump(Writer) table
     * dumps} to the given {@link Writer}.
     */
    public void dumpTo(Writer out) throws IOException {
        out.write(toString());
        out.write("\n");
        
        for (Entry entry : _entries) {
            entry.dumpTo(out);
            out.write("\n");
        }
    }
}
