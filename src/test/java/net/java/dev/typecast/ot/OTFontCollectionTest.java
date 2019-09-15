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

package net.java.dev.typecast.ot;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class OTFontCollectionTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public OTFontCollectionTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(OTFontCollectionTest.class);
    }

    public void testLoadSingleFont() throws URISyntaxException, IOException {
        URL url = ClassLoader.getSystemResource("Lato-Regular.ttf");
        File file = new File(url.toURI());
        OTFontCollection fontCollection = new OTFontCollection(file);
        assertEquals(1, fontCollection.getFontCount());
        OTFont font = fontCollection.getFont(0);
        assertNotNull(font);
    }
}
