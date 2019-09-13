/*
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

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import net.java.dev.typecast.ot.mac.ResourceHeader;
import net.java.dev.typecast.ot.mac.ResourceMap;
import net.java.dev.typecast.ot.mac.ResourceReference;
import net.java.dev.typecast.ot.mac.ResourceType;
import net.java.dev.typecast.ot.table.DirectoryEntry;
import net.java.dev.typecast.ot.table.TTCHeader;
import net.java.dev.typecast.ot.table.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class OTFontCollection {

    private String _pathName;
    private String _fileName;
    private TTCHeader _ttcHeader;
    private OTFont[] _fonts;
    private boolean _resourceFork = false;

    static final Logger logger = LoggerFactory.getLogger(OTFontCollection.class);

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

    /**
     * @param file The OpenType font file
     */
    protected void read(File file) throws IOException {
        _pathName = file.getPath();
        _fileName = file.getName();

        if (!file.exists()) {
            throw new IOException();
        }

        // Do we need to modify the path name to deal with font resources
        // in a Mac resource fork?
        if (file.length() == 0) {
            file = new File(file, "..namedfork/rsrc");
            if (!file.exists()) {
                throw new IOException();
            }
            _resourceFork = true;
        }

        DataInputStream dis = new DataInputStream(
            new BufferedInputStream(
                new FileInputStream(file), (int) file.length()));
        dis.mark((int) file.length());

        if (_resourceFork || _pathName.endsWith(".dfont")) {

            // This is a Macintosh font suitcase resource
            ResourceHeader resourceHeader = new ResourceHeader(dis);

            // Seek to the map offset and read the map
            dis.reset();
            dis.skip(resourceHeader.getMapOffset());
            ResourceMap map = new ResourceMap(dis);

            // Dump some info about the font suitcase
            for (int i = 0; i < map.getResourceTypeCount(); ++i) {
                logger.info(map.getResourceType(i).getTypeAsString());
            }

            ResourceType type = map.getResourceType("FOND");
            for (int i = 0; i < type.getCount(); ++i) {
                ResourceReference reference = type.getReference(i);
                logger.info(reference.getName());
            }

            // Get the 'sfnt' resources
            ResourceType resourceType = map.getResourceType("sfnt");

            // Load the font data
            _fonts = new OTFont[resourceType.getCount()];
            for (int i = 0; i < resourceType.getCount(); i++) {
                ResourceReference resourceReference = resourceType.getReference(i);
                int offset = resourceHeader.getDataOffset() +
                        resourceReference.getDataOffset() + 4;
                _fonts[i] = new TTFont(dis, offset /*, offset*/);
            }

        } else if (TTCHeader.isTTC(dis)) {

            // This is a TrueType font collection
            dis.reset();
            _ttcHeader = new TTCHeader(dis);
            _fonts = new OTFont[_ttcHeader.getDirectoryCount()];
            for (int i = 0; i < _ttcHeader.getDirectoryCount(); i++) {
                _fonts[i] = new TTFont(dis, _ttcHeader.getTableDirectory(i));
            }
        } else {

            // This is a standalone font file
            _fonts = new OTFont[1];
            _fonts[0] = new TTFont(dis, 0);

            // TODO T2Fonts
        }
        dis.close();
    }
}
