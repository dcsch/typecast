/*
 * $Id: Exporter.java,v 1.1.1.1 2004-12-05 23:14:20 davidsch Exp $
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

package net.java.dev.typecast.exchange;

import java.io.OutputStream;

import net.java.dev.typecast.ot.table.TableException;

/**
 *
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: Exporter.java,v 1.1.1.1 2004-12-05 23:14:20 davidsch Exp $
 */
public abstract class Exporter {
    
    public abstract void export(OutputStream os) throws TableException;
}
