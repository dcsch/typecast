/*
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004-2015 David Schweinsberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.java.dev.typecast.t2;

/**
 *
 * @author dschweinsberg
 */
public class CffFont {
    
    private final Index _charStringsIndex;
    private final Dict _privateDict;
    private final Index _localSubrsIndex;
    private final Charset _charset;
    private final Charstring[] _charstrings;

    public CffFont(
            Index charStringsIndex,
            Dict privateDict,
            Index localSubrsIndex,
            Charset charset,
            Charstring[] charstrings) {
        _charStringsIndex = charStringsIndex;
        _privateDict = privateDict;
        _localSubrsIndex = localSubrsIndex;
        _charset = charset;
        _charstrings = charstrings;
    }

    public Index getCharStringsIndex() {
        return _charStringsIndex;
    }

    public Dict getPrivateDict() {
        return _privateDict;
    }

    public Index getLocalSubrsIndex() {
        return _localSubrsIndex;
    }

    public Charset getCharset() {
        return _charset;
    }

    public Charstring[] getCharstrings() {
        return _charstrings;
    }
}
