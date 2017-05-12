
package net.java.dev.typecast.ot.table;

import java.io.DataInput;
import java.io.IOException;

/**
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class GdefTable implements Table {

    private DirectoryEntry _de;

    protected GdefTable(DirectoryEntry de, DataInput di) throws IOException {
        this._de = (DirectoryEntry) de.clone();
    }

    @Override
    public int getType() {
        return GDEF;
    }

    @Override
    public DirectoryEntry getDirectoryEntry() {
        return _de;
    }
}
