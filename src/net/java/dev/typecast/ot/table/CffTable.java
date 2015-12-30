/*
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004-2015 David Schweinsberg
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

package net.java.dev.typecast.ot.table;

import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.IOException;
import java.util.List;
import net.java.dev.typecast.t2.CffFont;
import net.java.dev.typecast.t2.Charset;
import net.java.dev.typecast.t2.CharsetFormat0;
import net.java.dev.typecast.t2.CharsetFormat1;
import net.java.dev.typecast.t2.CharsetFormat2;
import net.java.dev.typecast.t2.Charstring;
import net.java.dev.typecast.t2.CharstringType2;
import net.java.dev.typecast.t2.Dict;
import net.java.dev.typecast.t2.Index;
import net.java.dev.typecast.t2.NameIndex;
import net.java.dev.typecast.t2.StringIndex;
import net.java.dev.typecast.t2.TopDictIndex;

/**
 * Compact Font Format Table
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class CffTable implements Table {
    
    private DirectoryEntry _de;
    private int _major;
    private int _minor;
    private int _hdrSize;
    private int _offSize;
    private NameIndex _nameIndex;
    private TopDictIndex _topDictIndex;
    private StringIndex _stringIndex;
    private Index _globalSubrIndex;
    private CffFont[] _fonts;

    private byte[] _buf;

    /** Creates a new instance of CffTable
     * @param de
     * @param di
     * @throws java.io.IOException */
    protected CffTable(DirectoryEntry de, DataInput di) throws IOException {
        _de = (DirectoryEntry) de.clone();

        // Load entire table into a buffer, and create another input stream
        _buf = new byte[de.getLength()];
        di.readFully(_buf);
        DataInput di2 = getDataInputForOffset(0);

        // Header
        _major = di2.readUnsignedByte();
        _minor = di2.readUnsignedByte();
        _hdrSize = di2.readUnsignedByte();
        _offSize = di2.readUnsignedByte();
        
        // Name INDEX
        di2 = getDataInputForOffset(_hdrSize);
        _nameIndex = new NameIndex(di2);
        
        // Top DICT INDEX
        _topDictIndex = new TopDictIndex(di2);

        // String INDEX
        _stringIndex = new StringIndex(di2);
        
        // Global Subr INDEX
        _globalSubrIndex = new Index(di2);
        
        // TESTING
        Charstring gscs = new CharstringType2(
                0,
                "Global subrs",
                _globalSubrIndex.getData(),
                _globalSubrIndex.getOffset(0) - 1,
                _globalSubrIndex.getDataLength());
        System.out.println(gscs.toString());

        // Encodings go here -- but since this is an OpenType font will this
        // not always be a CIDFont?  In which case there are no encodings
        // within the CFF data.
        
        // Load each of the fonts
        _fonts = new CffFont[_topDictIndex.getCount()];
        for (int i = 0; i < _topDictIndex.getCount(); ++i) {

            // Charstrings INDEX
            // We load this before Charsets because we may need to know the number
            // of glyphs
            Integer charStringsOffset = (Integer) _topDictIndex.getTopDict(i).getValue(17);
            di2 = getDataInputForOffset(charStringsOffset);
            Index charStringsIndex = new Index(di2);
            int glyphCount = charStringsIndex.getCount();

            // Private DICT
            List<Integer> privateSizeAndOffset = (List<Integer>) _topDictIndex.getTopDict(i).getValue(18);
            di2 = getDataInputForOffset(privateSizeAndOffset.get(1));
            Dict privateDict = new Dict(di2, privateSizeAndOffset.get(0));
            
            // Local Subrs INDEX
            Index localSubrsIndex = null;
            Integer localSubrsOffset = (Integer) privateDict.getValue(19);
            if (localSubrsOffset != null) {
                di2 = getDataInputForOffset(privateSizeAndOffset.get(1) + localSubrsOffset);
                localSubrsIndex = new Index(di2);
            }
        
            // Charsets
            Charset charset = null;
            Integer charsetOffset = (Integer) _topDictIndex.getTopDict(i).getValue(15);
            di2 = getDataInputForOffset(charsetOffset);
            int format = di2.readUnsignedByte();
            switch (format) {
                case 0:
                    charset = new CharsetFormat0(di2, glyphCount);
                    break;
                case 1:
                    charset = new CharsetFormat1(di2, glyphCount);
                    break;
                case 2:
                    charset = new CharsetFormat2(di2, glyphCount);
                    break;
            }

            // Create the charstrings
            Charstring[] charstrings = new Charstring[glyphCount];
            for (int j = 0; j < glyphCount; ++j) {
                int offset = charStringsIndex.getOffset(j) - 1;
                int len = charStringsIndex.getOffset(j + 1) - offset - 1;
                charstrings[j] = new CharstringType2(
                        i,
                        _stringIndex.getString(charset.getSID(j)),
                        charStringsIndex.getData(),
                        offset,
                        len);
            }
            
            _fonts[i] = new CffFont(charStringsIndex, privateDict, localSubrsIndex, charset, charstrings);
        }
    }
    
    private DataInput getDataInputForOffset(int offset) {
        return new DataInputStream(new ByteArrayInputStream(
                _buf, offset,
                _de.getLength() - offset));
    }

    public NameIndex getNameIndex() {
        return _nameIndex;
    }
    
    public Index getGlobalSubrIndex() {
        return _globalSubrIndex;
    }

    public CffFont getFont(int fontIndex) {
        return _fonts[fontIndex];
    }

//    public Charset getCharset(int fontIndex) {
//        return _charsets[fontIndex];
//    }

    public Charstring getCharstring(int fontIndex, int gid) {
        return _fonts[fontIndex].getCharstrings()[gid];
    }
    
    public int getCharstringCount(int fontIndex) {
        return _fonts[fontIndex].getCharstrings().length;
    }
    
    @Override
    public int getType() {
        return CFF;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("'CFF' Table - Compact Font Format\n---------------------------------\n");
        sb.append("\nName INDEX\n");
        sb.append(_nameIndex.toString());
        sb.append("\nTop DICT INDEX\n");
        sb.append(_topDictIndex.toString());
        sb.append("\nString INDEX\n");
        sb.append(_stringIndex.toString());
        sb.append("\nGlobal Subr INDEX\n");
        sb.append(_globalSubrIndex.toString());
        for (int i = 0; i < _fonts.length; ++i) {
            sb.append("\nCharStrings INDEX ").append(i).append("\n");
            sb.append(_fonts[i].getCharStringsIndex().toString());
        }
        return sb.toString();
    }
    
    /**
     * Get a directory entry for this table.  This uniquely identifies the
     * table in collections where there may be more than one instance of a
     * particular table.
     * @return A directory entry
     */
    @Override
    public DirectoryEntry getDirectoryEntry() {
        return _de;
    }
}
