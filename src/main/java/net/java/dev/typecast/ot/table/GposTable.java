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

package net.java.dev.typecast.ot.table;

import java.io.DataInput;
import java.io.IOException;

/**
 * TODO: To be implemented
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
class GposTable implements Table {

    protected GposTable(DataInput di) throws IOException {

        // GPOS Header
        int version = di.readInt();
        int scriptList = di.readInt();
        int featureList = di.readInt();
        int lookupList = di.readInt();
    }

    public String toString() {
        return "GPOS";
    }

}
