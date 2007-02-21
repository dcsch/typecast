/*
 * $Id: CharstringType2.java,v 1.2 2007-02-21 12:26:22 davidsch Exp $
 *
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004-2007 David Schweinsberg
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

/**
 * CFF Type 2 Charstring
 * @version $Id: CharstringType2.java,v 1.2 2007-02-21 12:26:22 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class CharstringType2 extends Charstring {
    
    private static final String[] _oneByteOperators = {
        "-Reserved-",
        "hstem",
        "-Reserved-",
        "vstem",
        "vmoveto",
        "rlineto",
        "hlineto",
        "vlineto",
        "rrcurveto",
        "-Reserved-",
        "callsubr",
        "return",
        "escape",
        "-Reserved-",
        "endchar",
        "-Reserved-",
        "-Reserved-",
        "-Reserved-",
        "hstemhm",
        "hintmask",
        "cntrmask",
        "rmoveto",
        "hmoveto",
        "vstemhm",
        "rcurveline",
        "rlinecurve",
        "vvcurveto",
        "hhcurveto",
        "shortint",
        "callgsubr",
        "vhcurveto",
        "hvcurveto"
    };

    private static final String[] _twoByteOperators = {
        "-Reserved- (dotsection)",
        "-Reserved-",
        "-Reserved-",
        "and",
        "or",
        "not",
        "-Reserved-",
        "-Reserved-",
        "-Reserved-",
        "abs",
        "add",
        "sub",
        "div",
        "-Reserved-",
        "neg",
        "eq",
        "-Reserved-",
        "-Reserved-",
        "drop",
        "-Reserved-",
        "put",
        "get",
        "ifelse",
        "random",
        "mul",
        "-Reserved-",
        "sqrt",
        "dup",
        "exch",
        "index",
        "roll",
        "-Reserved-",
        "-Reserved-",
        "-Reserved-",
        "hflex",
        "flex",
        "hflex1",
        "flex1",
        "-Reserved-"
    };
    
    private int __index;
    private String _name;
    private int[] _data;
    private int _offset;
    private int _length;
    private int _index;

    /** Creates a new instance of CharstringType2 */
    protected CharstringType2(int index, String name, int[] data, int offset, int length) {
        _index = index;
        _name = name;
        _data = data;
        _offset = offset;
        _length = length;
    }
    
    public int getIndex() {
        return _index;
    }

    public String getName() {
        return _name;
    }
    
    private void disassemble(StringBuffer sb) {
        Number operand = null;
        while (isOperandAtIndex()) {
            operand = nextOperand();
            sb.append(operand).append(" ");
        }
        int operator = nextByte();
        String mnemonic;
        if (operator == 12) {
            operator = nextByte();
            
            // Check we're not exceeding the upper limit of our mnemonics
            if (operator > 38) {
                operator = 38;
            }
            mnemonic = _twoByteOperators[operator];
        } else {
            mnemonic = _oneByteOperators[operator];
        }
        sb.append(mnemonic);
    }
    
    public void resetIndex() {
        _index = _offset;
    }

    public boolean isOperandAtIndex() {
        int b0 = _data[_index];
        if ((32 <= b0 && b0 <= 255) || b0 == 28) {
            return true;
        }
        return false;
    }

    public Number nextOperand() {
        int b0 = _data[_index];
        if (32 <= b0 && b0 <= 246) {

            // 1 byte integer
            ++_index;
            return new Integer(b0 - 139);
        } else if (247 <= b0 && b0 <= 250) {

            // 2 byte integer
            int b1 = _data[_index + 1];
            _index += 2;
            return new Integer((b0 - 247) * 256 + b1 + 108);
        } else if (251 <= b0 && b0 <= 254) {

            // 2 byte integer
            int b1 = _data[_index + 1];
            _index += 2;
            return new Integer(-(b0 - 251) * 256 - b1 - 108);
        } else if (b0 == 28) {

            // 3 byte integer
            int b1 = _data[_index + 1];
            int b2 = _data[_index + 2];
            _index += 3;
            return new Integer(b1 << 8 | b2);
        } else if (b0 == 255) {

            // 16-bit signed integer with 16 bits of fraction
            int b1 = (byte) _data[_index + 1];
            int b2 = _data[_index + 2];
            int b3 = _data[_index + 3];
            int b4 = _data[_index + 4];
            _index += 5;
            return new Float((b1 << 8 | b2) + ((b3 << 8 | b4) / 65536.0));
        } else {
            return null;
        }
    }
    
    public int nextByte() {
        return _data[_index++];
    }
    
    public boolean moreBytes() {
        return _index < _offset + _length;
    }
    
    public String toString() {
        StringBuffer sb = new StringBuffer();
        resetIndex();
        while (moreBytes()) {
            disassemble(sb);
            sb.append("\n");
        }
        return sb.toString();
    }
}
