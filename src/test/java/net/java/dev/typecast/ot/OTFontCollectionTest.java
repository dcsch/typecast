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
        OTFontCollection fontCollection = OTFontCollection.create(file);
        assertEquals(1, fontCollection.getFontCount());
    }
}
