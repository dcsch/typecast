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

import net.java.dev.typecast.io.BinaryOutput;
import net.java.dev.typecast.io.Writable;
import net.java.dev.typecast.ot.table.NameRecord.StringOut;

/**
 * name — Naming Table
 * 
 * <p>
 * The naming table allows multilingual strings to be associated with the
 * OpenType font file. These strings can represent copyright notices, font
 * names, family names, style names, and so on.
 * </p>
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * @see <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/name">Spec: Naming Table</a>
 */
public class NameTable implements Table, Writable {

    /**
     * Format 0 of {@link NameTable}.
     */
    public static final short FORMAT_0 = 0;

    /**
     * Format 1 of {@link NameTable}.
     */
    public static final short FORMAT_1 = 1;
    
    private short _format = FORMAT_1;
    private short _stringOffset;
    private NameRecord[] _records;

    @Override
    public void read(DataInput di, int length) throws IOException {
        _format = di.readShort();
        int count = di.readUnsignedShort();
        _stringOffset = di.readShort();
        _records = new NameRecord[count];
        
        // Load the records, which contain the encoding information and string
        // offsets
        for (int i = 0; i < count; i++) {
            _records[i] = new NameRecord(di);
        }
        
        // Load the string data into a buffer so the records can copy out the
        // bits they are interested in
        byte[] buffer = new byte[length - _stringOffset];
        di.readFully(buffer);
        
        // Now let the records get their hands on them
        for (int i = 0; i < count; i++) {
            _records[i].loadString(
                    new DataInputStream(new ByteArrayInputStream(buffer)));
        }
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        long start = out.getPosition();
        
        out.writeShort(_format);
        out.writeShort(_records.length);
        
        List<StringOut> stringOuts = new ArrayList<>(_records.length);
        try (BinaryOutput offsetOut = out.reserve(2)) {
            for (NameRecord record : _records) {
                stringOuts.add(record.write(out));
            }
            
            long storageStart = out.getPosition();
            long storageOffset = storageStart - start;
            
            offsetOut.writeShort((int) storageOffset);
            offsetOut.close();
            
            for (StringOut stringOut : stringOuts) {
                stringOut.write(storageStart);
            }
        }
    }
    
    @Override
    public int getType() {
        return name;
    }

    /**
     * uint16   Format selector (=0 or 1).
     */
    public short getFormat() {
        return _format;
    }

    /**
     * uint16   count   Number of name records.
     */
    public int getNumberOfNameRecords() {
        return _records.length;
    }
    
    /**
     * Offset16     stringOffset    Offset to start of string storage (from start of table).
     */
    public short getStringStorageOffset() {
        return _stringOffset;
    }

    public NameRecord getRecord(int i) {
        return _records[i];
    }

    public String getRecordString(short nameId) {

        // Search for the first instance of this name ID
        for (int i = 0; i < _records.length; i++) {
            if (_records[i].getNameId() == nameId) {
                return _records[i].getRecordString();
            }
        }
        return "";
    }

    @Override
    public String toString() {
        return "'name' Table - Naming Table\n--------------------------------" +
                "\n        'name' format:       " + _format +
                "\n        count:               " + getNumberOfNameRecords() +
                "\n        stringOffset:        " + _stringOffset +
                "\n        records:" +
                Arrays.asList(_records).stream().map(r -> "\n" + r.toString()).collect(Collectors.joining("\n")) + 
                "\n";
    }

}
