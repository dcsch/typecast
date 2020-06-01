
package net.java.dev.typecast.ot.table;

import java.io.DataInput;
import java.io.IOException;

/**
 * GDEF — Glyph Definition Table
 * 
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 * 
 * <a href="https://docs.microsoft.com/en-us/typography/opentype/spec/gdef">Spec: Glyph Definition Table</a>
 */
public class GdefTable implements Table {

    @Override
    public void read(DataInput di, int length) throws IOException {
        // TODO: Implement.
    }

    @Override
    public int getType() {
        return GDEF;
    }

}
