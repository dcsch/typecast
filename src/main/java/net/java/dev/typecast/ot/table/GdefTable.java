
package net.java.dev.typecast.ot.table;

import java.io.DataInput;
import java.io.IOException;

/**
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
class GdefTable implements Table {

    protected GdefTable(DataInput di, int i) throws IOException {
    }

    @Override
    public int getType() {
        return GDEF;
    }

}
