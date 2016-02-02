/*
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004-2007 David Schweinsberg
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

package net.java.dev.typecast.app.editor;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;

import net.java.dev.typecast.ot.mac.ResourceHeader;
import net.java.dev.typecast.ot.mac.ResourceMap;
import net.java.dev.typecast.ot.mac.ResourceType;

/**
 * A FilenameFilter implementation that includes font files based on their
 * extension and also by the presence of fonts in the resource fork.
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class MacOSFilenameFilter implements FilenameFilter {
    
    /** Creates a new instance of MacOSFilenameFilter */
    public MacOSFilenameFilter() {
    }

    public boolean accept(File dir, String name) {
        if (name.endsWith(".ttf")
            || name.endsWith(".ttc")
            || name.endsWith(".otf")
            || name.endsWith(".dfont")
            || name.endsWith(".suit")) {
            return true;
        } else if (name.indexOf('.') == -1) {
            
            // This filename has no extension, so we'll look into any
            // resource fork.  But first, if there is data in the data fork,
            // then we'll reject this as a font file
            File dataFork = new File(dir, name);
            if (dataFork.length() > 0) {
                return false;
            }
  
            // OK, go for the resource fork
            File file = new File(dataFork, "..namedfork/rsrc");
            if (file.exists() && file.length() > 0) {
                try {
                    DataInputStream dis = new DataInputStream(
                        new BufferedInputStream(
                            new FileInputStream(file), (int) file.length()));
                    dis.mark((int) file.length());

                    // Is this a Macintosh font suitcase resource?
                    ResourceHeader resourceHeader = new ResourceHeader(dis);

                    // Seek to the map offset and read the map
                    dis.reset();
                    dis.skip(resourceHeader.getMapOffset());
                    ResourceMap map = new ResourceMap(dis);

                    // Get any 'sfnt' resources
                    ResourceType resourceType = map.getResourceType("sfnt");
                    dis.close();
                    if (resourceType != null) {
                        return true;
                    }
                } catch (FileNotFoundException e) {
                    // ignore
                } catch (IOException e) {
                    // ignore
                }
            }
        }
        return false;
    }
}
