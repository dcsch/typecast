/*
 * Typecast
 *
 * Copyright © 2004-2019 David Schweinsberg
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

/**
 * Base class for {@link Table} implementations.
 *
 * @author <a href="mailto:haui@haumacher.de">Bernhard Haumacher</a>
 */
public abstract class AbstractTable implements Table {

    private TableDirectory _directory;

    /** 
     * Creates a {@link AbstractTable}.
     *
     * @param directory
     */
    public AbstractTable(TableDirectory directory) {
        _directory = directory;
    }
    
    /**
     * The {@link TableDirectory} of the font owning this {@link Table}.
     */
    public TableDirectory getDirectory() {
        return _directory;
    }
    
    /**
     * The {@link HeadTable} of this font.
     */
    protected final HeadTable head() {
        return getDirectory().head();
    }
    
    /**
     * The {@link LocaTable} of this font.
     */
    protected final LocaTable loca() {
        return getDirectory().loca();
    }

    /**
     * The {@link MaxpTable} of this font.
     */
    protected final MaxpTable maxp() {
        return getDirectory().maxp();
    }
    
    /**
     * The {@link HheaTable} of this font.
     */
    protected final HheaTable hhea() {
        return getDirectory().hhea();
    }
    
    /**
     * The {@link VheaTable} of this font.
     */
    protected final VheaTable vhea() {
        return getDirectory().vhea();
    }
    
}
