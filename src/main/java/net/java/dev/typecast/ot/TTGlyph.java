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
package net.java.dev.typecast.ot;

import net.java.dev.typecast.ot.table.GlyfDescript;
import net.java.dev.typecast.ot.table.GlyphDescription;

/**
 * An individual TrueType glyph within a font.
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class TTGlyph extends Glyph {

    protected short _leftSideBearing;
    protected int _advanceWidth;
    private Point[] _points;

    /**
     * Construct a Glyph from a TrueType outline described by
     * a GlyphDescription.
     * @param gd The glyph description of the glyph.
     * @param lsb The Left Side Bearing.
     * @param advance The advance width.
     */
    public TTGlyph(GlyphDescription gd, short lsb, int advance) {
        _leftSideBearing = lsb;
        _advanceWidth = advance;
        describe(gd);
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

    /**
     * Resets the glyph to the TrueType table settings
     */
    public void reset() {
    }

    /**
     * @param factor a 16.16 fixed value
     */
    public void scale(int factor) {
        for (Point _point : _points) {
            //points[i].x = ( points[i].x * factor ) >> 6;
            //points[i].y = ( points[i].y * factor ) >> 6;
            _point.x = ((_point.x << 10) * factor) >> 26;
            _point.y = ((_point.y << 10) * factor) >> 26;
        }
        _leftSideBearing = (short)(( _leftSideBearing * factor) >> 6);
        _advanceWidth = (_advanceWidth * factor) >> 6;
    }

    /**
     * Set the points of a glyph from the GlyphDescription
     */
    private void describe(GlyphDescription gd) {
        int endPtIndex = 0;
        _points = new Point[gd.getPointCount() + 2];
        for (int i = 0; i < gd.getPointCount(); i++) {
            boolean endPt = gd.getEndPtOfContours(endPtIndex) == i;
            if (endPt) {
                endPtIndex++;
            }
            _points[i] = new Point(
                    gd.getXCoordinate(i),
                    gd.getYCoordinate(i),
                    (gd.getFlags(i) & GlyfDescript.onCurve) != 0,
                    endPt);
        }

        // Append the origin and advanceWidth points (n & n+1)
        _points[gd.getPointCount()] = new Point(0, 0, true, true);
        _points[gd.getPointCount()+1] = new Point(_advanceWidth, 0, true, true);
    }
}
