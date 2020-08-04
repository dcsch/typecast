/*
 * Copyright (c) 2020 Business Operation Systems GmbH. All Rights Reserved.
 */
package net.java.dev.typecast.ot;

/**
 * Formatting utilities.
 *
 * @author <a href="mailto:bhu@top-logic.com">Bernhard Haumacher</a>
 */
public class Fmt {

    /**
     * Left aligned number in a field of the given number of digits.
     */
    public static String pad(int digits, int value) {
        return pad(digits, ' ', Integer.toString(value));
    }

    /**
     * Left aligned number in a field of the given number of digits.
     */
    public static String padHex(int digits, int value) {
        return pad(digits, '0', Integer.toHexString(value));
    }
    
    /**
     * Create a left aligned string by prepending it with the given padding
     * character up to the given number of total characters.
     *
     * @param length
     *        The total length of the resulting string.
     * @param paddingChar
     *        The character to pad with.
     * @param value
     *        The value to pad.
     * @return The padded value.
     */
    public static String pad(int length, char paddingChar, String value) {
        if (value.length() >= length) {
            return value;
        }
        StringBuilder buffer = new StringBuilder(length);
        for (int n = 0, cnt = length - value.length(); n < cnt; n++) {
            buffer.append(paddingChar);
        }
        buffer.append(value);
        return buffer.toString();
    }
    
}
