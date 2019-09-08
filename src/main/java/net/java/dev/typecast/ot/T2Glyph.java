/*
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004-2016 David Schweinsberg
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
package net.java.dev.typecast.ot;

import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import net.java.dev.typecast.cff.CharstringType2;
import net.java.dev.typecast.cff.T2Interpreter;

/**
 * An individual Type 2 Charstring glyph within a font.
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class T2Glyph extends Glyph {
    protected short _leftSideBearing;
    protected int _advanceWidth;
    private final Point[] _points;
    private final Integer[] _hstems;
    private final Integer[] _vstems;

    /**
     * Construct a Glyph from a PostScript outline described by a Charstring.
     * @param cs The CharstringType2 describing the glyph.
     * @param lsb The Left Side Bearing.
     * @param advance The advance width.
     */
    public T2Glyph(
            CharstringType2 cs,
            short lsb,
            int advance) {
        _leftSideBearing = lsb;
        _advanceWidth = advance;
        T2Interpreter t2i = new T2Interpreter();
        _points = t2i.execute(cs);
        _hstems = t2i.getHStems();
        _vstems = t2i.getVStems();
    }

    @Override
    public int getAdvanceWidth() {
        return _advanceWidth;
    }

    @Override
    public short getLeftSideBearing() {
        return _leftSideBearing;
    }

    @Override
    public Point getPoint(int i) {
        return _points[i];
    }

    @Override
    public int getPointCount() {
        return _points.length;
    }

    public Integer[] getHStems() {
        return _hstems;
    }

    public Integer[] getVStems() {
        return _vstems;
    }
    
    public Rectangle2D getBounds() {
        Rectangle r = null;
        for (Point p : _points) {
            if (r == null) {
                r = new Rectangle(p.x, p.y, 0, 0);
            }
            r.add(new java.awt.Point(p.x, p.y));
        }
        return r != null ? r : new Rectangle(0, 0, 0, 0);
    }
}
