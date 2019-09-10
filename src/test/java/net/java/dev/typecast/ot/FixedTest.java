package net.java.dev.typecast.ot;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class FixedTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public FixedTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(FixedTest.class);
    }

    public void testSquareRoot() {
        // Values are 26.6 fixed numbers
        assertEquals(0x40, Fixed.squareRoot(0x40)); // 1.0
        assertEquals(0x80, Fixed.squareRoot(0x100)); // 4.0
        assertEquals(0xC0, Fixed.squareRoot(0x240)); // 9.0
        assertEquals(0x100, Fixed.squareRoot(0x400)); // 16.0
        assertEquals(0x80|0x20, Fixed.squareRoot(0x180|0x10)); // 6.25
    }

    public void testFloatValue() {
        assertEquals(0.0f, Fixed.floatValue(0x00000));
        assertEquals(0.5f, Fixed.floatValue(0x08000));
        assertEquals(1.0f, Fixed.floatValue(0x10000));
        assertEquals(1.25f, Fixed.floatValue(0x14000));
        assertEquals(1.5f, Fixed.floatValue(0x18000));
        assertEquals(1.75f, Fixed.floatValue(0x1c000));
        assertEquals(2.125f, Fixed.floatValue(0x22000));
        assertEquals(3.0625f, Fixed.floatValue(0x31000));
        assertEquals(4.03125f, Fixed.floatValue(0x40800));
        assertEquals(5.015625f, Fixed.floatValue(0x50400));
        assertEquals(6.0078125f, Fixed.floatValue(0x60200));
        assertEquals(7.00390625f, Fixed.floatValue(0x70100));
        assertEquals(8.001953125f, Fixed.floatValue(0x80080));
        assertEquals(9.0009765625f, Fixed.floatValue(0x90040));
        assertEquals(10.00048828125f, Fixed.floatValue(0xa0020));
        assertEquals(11.000244140625f, Fixed.floatValue(0xb0010));
        assertEquals(12.0001220703125f, Fixed.floatValue(0xc0008));
        assertEquals(13.00006103515625f, Fixed.floatValue(0xd0004));
        assertEquals(14.00003051757813f, Fixed.floatValue(0xe0002));
        assertEquals(15.00001525878907f, Fixed.floatValue(0xf0001));
    }
}
