package net.java.dev.typecast.ot;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;
import net.java.dev.typecast.ot.table.HeadTable;

public class TTFontTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TTFontTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(TTFontTest.class);
    }

    public void testLoadSingleFont() throws URISyntaxException, IOException {
        URL url = ClassLoader.getSystemResource("Lato-Regular.ttf");
        File file = new File(url.toURI());
        DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(file), (int)file.length()));
        dis.mark((int)file.length());
        TTFont font = new TTFont(dis, 0);
        assertEquals(HeadTable.class, font.getHeadTable().getClass());
    }
}
