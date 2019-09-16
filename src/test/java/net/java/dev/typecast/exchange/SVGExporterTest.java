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

package net.java.dev.typecast.exchange;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.java.dev.typecast.ot.TTFont;
import net.java.dev.typecast.ot.table.TableException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;

public class SVGExporterTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public SVGExporterTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(SVGExporterTest.class);
    }

    public void testExportFont() throws URISyntaxException, IOException, TableException {
        URL url = ClassLoader.getSystemResource("Lato-Regular.ttf");
        File file = new File(url.toURI());
        byte[] fontData = Files.readAllBytes(file.toPath());
        TTFont font = new TTFont(fontData, 0);
        SVGExporter exporter = new SVGExporter(font, 32, 32, "abc", true, false);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        exporter.export(baos);
        String svgString = baos.toString();
        System.out.println(svgString);
    }
}
