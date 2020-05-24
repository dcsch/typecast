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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import net.java.dev.typecast.io.BinUtils;
import net.java.dev.typecast.io.BinaryIO;
import net.java.dev.typecast.io.BinaryOutput;
import net.java.dev.typecast.io.Writable;
import net.java.dev.typecast.ot.Fixed;
import net.java.dev.typecast.ot.Fmt;

/**
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class TableDirectory {

    /**
     * Table Record entry.
     */
    public class Entry {

        private int _tag;
        private int _checksum;
        private int _offset;
        private int _length;
        private Table _table;
        
        long _checkSumPos;

        Entry(DataInput di) throws IOException {
            _tag = di.readInt();
            _checksum = di.readInt();
            _offset = di.readInt();
            _length = di.readInt();
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
            return "'" + getTagAsString() +
                    "' - chksm = 0x" + Integer.toHexString(_checksum) +
                    ", off = 0x" + Integer.toHexString(_offset) +
                    ", len = " + _length;
        }

        /**
         * The {@link Table} corresponding to this {@link Entry}.
         */
        public Table getTable() {
            return _table;
        }
        
        /** 
         * @see #getTable()
         */
        public void setTable(Table table) {
            _table = table;
        }

        Table readTable(DataInputStream di) throws IOException {
            switch (getTag()) {
                case Table.CFF:
                    // TODO: Implemented but does not work.
                    // return new CffTable(di, getLength());
                    return null;
                case Table.cmap:
                    return new CmapTable(di, getLength());
                case Table.COLR:
                    return new ColrTable(di, getLength());
                case Table.CPAL:
                    return new CpalTable(di, getLength());
                case Table.cvt:
                    return new CvtTable(di, getLength());
                case Table.DSIG:
                    return new DsigTable(di, getLength());
                case Table.EBDT:
                case Table.EBLC:
                case Table.EBSC:
                    // TODO: Not supported.
                    return null;
                case Table.fpgm:
                    return new FpgmTable(di, getLength());
                case Table.fvar:
                    // TODO: Not supported.
                    return null;
                case Table.gasp:
                    return new GaspTable(di, getLength());
                case Table.GDEF:
                    return new GdefTable(di, getLength());
                case Table.glyf:
                    return new GlyfTable(di, getLength(), maxp(), loca());
                case Table.GPOS:
                    return new GposTable(di, getLength());
                case Table.GSUB:
                    return new GsubTable(di, getLength());
                case Table.hdmx:
                    return new HdmxTable(di, getLength(), maxp());
                case Table.head:
                    return new HeadTable(di, getLength());
                case Table.hhea:
                    return new HheaTable(di, getLength());
                case Table.hmtx:
                    return new HmtxTable(di, getLength(), hhea(), maxp());
                case Table.JSTF:
                    // TODO: Not supported.
                    return null;
                case Table.kern:
                    return new KernTable(di, getLength());
                case Table.loca:
                    return new LocaTable(di, getLength(), head(), maxp());
                case Table.LTSH:
                    return new LtshTable(di, getLength());
                case Table.maxp:
                    return new MaxpTable(di, getLength());
                case Table.MMFX:
                case Table.MMSD:
                    // TODO: Not supported.
                    return null;
                case Table.name:
                    return new NameTable(di, getLength());
                case Table.OS_2:
                    return new Os2Table(di, getLength());
                case Table.PCLT:
                    return new PcltTable(di, getLength());
                case Table.post:
                    return new PostTable(di, getLength());
                case Table.prep:
                    return new PrepTable(di, getLength());
                case Table.sbix:
                    return new SbixTable(di, getLength(), maxp());
                case Table.svg:
                    return new SVGTable(di, getLength());
                case Table.VDMX:
                    return new VdmxTable(di, getLength());
                case Table.vhea:
                    return new VheaTable(di, getLength());
                case Table.vmtx:
                    return new VmtxTable(di, getLength(), vhea(), maxp());
                default:
                    // Not supported.
                    return null;
            }
        }

        public Table initTable(DataInputStream di, int tablesOrigin) throws IOException {
            if (getTable() == null) {
                seekTable(di, tablesOrigin, this);
                Table table = readTable(di);
                setTable(table);
            }
            
            return getTable();
        }
    }

    private int _sfntVersion = TRUE_TYPE;
    private short _searchRange;
    private short _entrySelector;
    private short _rangeShift;
    private Entry[] _entries;

    public TableDirectory(byte[] fontData) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(fontData));
        _sfntVersion = dis.readInt();
        short numTables = dis.readShort();
        _searchRange = dis.readShort();
        _entrySelector = dis.readShort();
        _rangeShift = dis.readShort();
        _entries = new Entry[numTables];
        for (int i = 0; i < numTables; i++) {
            _entries[i] = new Entry(dis);
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
        for (int n = _entries.length - 1; n >= 0; n--) {
            Entry entry = _entries[n];
            entry.initTable(di, tablesOrigin);
        }
    }

    LocaTable loca() {
        return (LocaTable) getEntryByTag(Table.loca).getTable();
    }

    HheaTable hhea() {
        return (HheaTable) getEntryByTag(Table.hhea).getTable();
    }

    HeadTable head() {
        return (HeadTable) getEntryByTag(Table.head).getTable();
    }

    VheaTable vhea() {
        return (VheaTable) getEntryByTag(Table.vhea).getTable();
    }

    MaxpTable maxp() {
        return (MaxpTable) getEntryByTag(Table.maxp).getTable();
    }

    public void write(BinaryIO io) throws IOException {
        long start = io.getPosition();
        
        // Table Record:
        // Entries in the Table Record must be sorted in ascending order by tag.
        List<Entry> entries = Arrays.asList(_entries).stream()
                .filter(e -> e.getTable() instanceof Writable)
                .sorted((e1, e2) -> Long.compare(0xFFFFFFFF & e1.getTag(), 0xFFFFFFFF & e2.getTag()))
                .collect(Collectors.toList());
        
        int numTables = entries.size();
        
        // Offset Table:
        io.writeInt(_sfntVersion);
        io.writeShort(numTables);
        io.writeShort(getSearchRange());
        io.writeShort(getEntrySelector());
        io.writeShort(getRangeShift());
        
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

    public Entry getEntry(int index) {
        return _entries[index];
    }

    public Entry getEntryByTag(int tag) {
        for (int i = 0; i < getNumTables(); i++) {
            if (_entries[i].getTag() == tag) {
                return _entries[i];
            }
        }
        return null;
    }

    public int getNumTables() {
        return _entries.length;
    }

    /**
     * (Maximum power of 2 <= {@link #getNumTables()}) x 16.
     */
    public short getSearchRange() {
        int numTables = getNumTables();
        
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
        int numTables = getNumTables();
        
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
        return (short) (getNumTables() * 16 - getSearchRange());
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
    
    public String toString() {
        StringBuilder sb = new StringBuilder()
            .append("Offset Table\n------ -----")
            .append("\n  sfntVersion:   ").append(Fixed.floatValue(_sfntVersion))
            .append("\n  numTables:     ").append(getNumTables())
            .append("\n  searchRange:   ").append(_searchRange)
            .append("\n  entrySelector: ").append(_entrySelector)
            .append("\n  rangeShift:    ").append(_rangeShift)
            .append("\n\n");
        for (int i = 0; i < getNumTables(); i++) {
            sb.append("  ").append(Fmt.pad(2, i)).append(": ").append(_entries[i].toString()).append("\n");
        }
        return sb.toString();
    }

    private Entry seekTable(DataInputStream dis, int tablesOrigin, Entry entry) throws IOException {
        dis.reset();
        dis.skip(tablesOrigin + entry.getOffset());
        return entry;
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
}
