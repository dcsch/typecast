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

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.java.dev.typecast.io.BinaryOutput;
import net.java.dev.typecast.io.Writable;

/**
 * Character to Glyph Index Mapping Table
 * 
 * @see "https://docs.microsoft.com/en-us/typography/opentype/spec/cmap"
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class CmapTable implements Table, Writable {

    /**
     * @see #getVersion()
     */
    public static final int VERSION = 0x0000;
    
    private int _version = VERSION;
    
    private ArrayList<CmapIndexEntry> _entries = new ArrayList<>();

    @Override
    public void read(DataInput di, int length) throws IOException {
        _version = di.readUnsignedShort();
        int numTables = di.readUnsignedShort();
        long bytesRead = 4;
        
        // Get each of the index entries
        
        // Note: The encoding record entries in the 'cmap' header must be sorted
        // first by platform ID, then by platform-specific encoding ID, and then
        // by the language field in the corresponding subtable. Each platform
        // ID, platform-specific encoding ID, and subtable language combination
        // may appear only once in the 'cmap' table.
        _entries.ensureCapacity(numTables);
        for (int i = 0; i < numTables; i++) {
            _entries.add(new CmapIndexEntry(di));
            bytesRead += 8;
        }

        // For reading, sort into their order of offset.
        List<CmapIndexEntry> entries = new ArrayList<CmapIndexEntry>(_entries);
        Collections.sort(entries);

        // Get each of the tables
        int lastOffset = -1;
        CmapFormat lastFormat = null;
        for (CmapIndexEntry entry : entries) {
            if (entry.getOffset() == lastOffset) {

                // This is a multiple entry
                entry.setFormat(lastFormat);
                continue;
            } else if (entry.getOffset() > bytesRead) {
                di.skipBytes(entry.getOffset() - (int) bytesRead);
            } else if (entry.getOffset() != bytesRead) {
                
                // Something is amiss
                throw new IOException();
            }
            int formatType = di.readUnsignedShort();
            lastFormat = CmapFormat.create(formatType, di);
            lastOffset = entry.getOffset();
            entry.setFormat(lastFormat);
            bytesRead += lastFormat.getLength();
        }
    }
    
    @Override
    public void write(BinaryOutput out) throws IOException {
        long start = out.getPosition();
        
        out.writeShort(getVersion());
        out.writeShort(getNumTables());
        
        // The encoding record entries in the 'cmap' header must be sorted first
        // by platform ID, then by platform-specific encoding ID, and then by
        // the language field in the corresponding subtable.
        Collections.sort(_entries, (e1, e2) -> {
            int platformCmp = Integer.compare(e1.getPlatformId(), e2.getPlatformId());
            if (platformCmp != 0) {
                return platformCmp;
            }
            
            int encodingCmp = Integer.compare(e1.getEncodingId(), e2.getEncodingId());
            if (encodingCmp != 0) {
                return encodingCmp;
            }
            
            int langCmp = Integer.compare(e1.getFormat().getLanguage(), e2.getFormat().getLanguage());
            return langCmp;
        });
        
        List<Writable> formatWriters = new ArrayList<>(_entries.size());
        for (CmapIndexEntry entry : _entries) {
            formatWriters.add(entry.writeEncodingRecord(out, start));
        }
        
        for (Writable formatWriter : formatWriters) {
            formatWriter.write(out);
        }
    }

    @Override
    public int getType() {
        return cmap;
    }

    /**
     * uint16 Table version number ({@link #VERSION}}).
     */
    public int getVersion() {
        return _version;
    }
    
    /**
     * uint16
     * 
     * Number of encoding tables that follow.
     */
    public int getNumTables() {
        return _entries.size();
    }
    
    public CmapIndexEntry getCmapIndexEntry(int i) {
        return _entries.get(i);
    }
    
    public CmapFormat getCmapFormat(short platformId, short encodingId) {
        // Find the requested format
        for (CmapIndexEntry entry : _entries) {
            if (entry.getPlatformId() == platformId && entry.getEncodingId() == encodingId) {
                return entry.getFormat();
            }
        }
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("cmap Table\n");
        sb.append("----------\n");
        sb.append("  numTables: " + getNumTables() + "\n");

        // Get each of the index entries
        for (CmapIndexEntry entry : _entries) {
            sb.append("\n").append(entry.toString());
        }

        // Get each of the tables
//        for (int i = 0; i < numTables; i++) {
//            sb.append("\t").append(formats[i].toString()).append("\n");
//        }
        
        sb.append("\n");
        return sb.toString();
    }

}
