/*
 * $Id: OTFontCollection.java,v 1.3 2004-12-15 14:09:44 davidsch Exp $
 *
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004 David Schweinsberg
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

import java.io.File;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import java.util.ArrayList;

import net.java.dev.typecast.ot.mac.ResourceHeader;
import net.java.dev.typecast.ot.mac.ResourceMap;
import net.java.dev.typecast.ot.mac.ResourceReference;
import net.java.dev.typecast.ot.mac.ResourceType;
import net.java.dev.typecast.ot.table.DirectoryEntry;
import net.java.dev.typecast.ot.table.Table;
import net.java.dev.typecast.ot.table.TTCHeader;

/**
 *
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: OTFontCollection.java,v 1.3 2004-12-15 14:09:44 davidsch Exp $
 */
public class OTFontCollection {

    private String _pathName;
    private String _fileName;
    private TTCHeader _ttcHeader;
    private OTFont[] _fonts;
    private ArrayList<Table> _tables = new ArrayList<Table>();

    /** Creates new FontCollection */
    protected OTFontCollection() {
    }

    /**
     * @param file The OpenType font file
     */
    public static OTFontCollection create(File file) throws IOException {
        OTFontCollection fc = new OTFontCollection();
        fc.read(file);
        return fc;
    }

    public String getPathName() {
        return _pathName;
    }

    public String getFileName() {
        return _fileName;
    }

    public OTFont getFont(int i) {
        return _fonts[i];
    }
    
    public int getFontCount() {
        return _fonts.length;
    }
    
    public TTCHeader getTtcHeader() {
        return _ttcHeader;
    }

    public Table getTable(DirectoryEntry de) {
        for (int i = 0; i < _tables.size(); i++) {
            Table table = (Table) _tables.get(i);
            if ((table.getDirectoryEntry().getTag() == de.getTag()) &&
                (table.getDirectoryEntry().getOffset() == de.getOffset())) {
                return table;
            }
        }
        return null;
    }

    public void addTable(Table table) {
        _tables.add(table);
    }

    /**
     * @param file The OpenType font file
     */
    protected void read(File file) throws IOException {
        _pathName = file.getPath();
        _fileName = file.getName();

        if (!file.exists()) {
            // TODO: Throw TTException
            return;
        }

        DataInputStream dis = new DataInputStream(
            new BufferedInputStream(
                new FileInputStream(file), (int) file.length()));
        dis.mark((int) file.length());

        if (_pathName.endsWith(".dfont")) {

            // This is a Macintosh font suitcase resource
            // Note that the resource fork must have been converted to a
            // data fork, which appears to be the standard case for
            // Mac OS X, thus the "dfont" extension.
            ResourceHeader resourceHeader = new ResourceHeader(dis);

            // Seek to the map offset and read the map
            dis.reset();
            dis.skip(resourceHeader.getMapOffset());
            ResourceMap map = new ResourceMap(dis);

            // Get the 'sfnt' resources
            ResourceType resourceType = map.getResourceType("sfnt");

            // Load the font data
            _fonts = new OTFont[resourceType.getCount()];
            for (int i = 0; i < resourceType.getCount(); i++) {
                ResourceReference resourceReference = resourceType.getReference(i);
                _fonts[i] = new OTFont(this);
                int offset = resourceHeader.getDataOffset() +
                        resourceReference.getDataOffset() + 4;
                _fonts[i].read(dis, offset, offset);
            }

        } else if (TTCHeader.isTTC(dis)) {

            // This is a TrueType font collection
            dis.reset();
            _ttcHeader = new TTCHeader(dis);
            _fonts = new OTFont[_ttcHeader.getDirectoryCount()];
            for (int i = 0; i < _ttcHeader.getDirectoryCount(); i++) {
                _fonts[i] = new OTFont(this);
                _fonts[i].read(dis, _ttcHeader.getTableDirectory(i), 0);
            }
        } else {

            // This is a standalone font file
            _fonts = new OTFont[1];
            _fonts[0] = new OTFont(this);
            _fonts[0].read(dis, 0, 0);
        }
        dis.close();
    }
}
