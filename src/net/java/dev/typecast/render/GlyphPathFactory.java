/*
 * $Id: GlyphPathFactory.java,v 1.1 2004-12-21 10:18:11 davidsch Exp $
 *
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004 David Schweinsberg
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

package net.java.dev.typecast.render;

import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;

import java.awt.Shape;

import net.java.dev.typecast.ot.Glyph;
import net.java.dev.typecast.ot.Point;

/**
 * A factory for generating Graphics2D paths from glyph outlines.
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: GlyphPathFactory.java,v 1.1 2004-12-21 10:18:11 davidsch Exp $
 */
public class GlyphPathFactory {
    
    /**
     * Build a {@link java.awt.geom.GeneralPath GeneralPath} from a
     * {@link net.java.dev.typecast.ot.Glyph Glyph}.  This glyph path can then
     * be transformed and rendered.
     */
    public static GeneralPath buildPath(Glyph glyph) {
        
        if (glyph == null) {
            return null;
        }

        GeneralPath glyphPath = new GeneralPath(GeneralPath.WIND_NON_ZERO);

        // Iterate through all of the points in the glyph.  Each time we find a
        // contour end point, add the point range to the path.
        int firstIndex = 0;
        int count = 0;
        for (int i = 0; i < glyph.getPointCount(); i++) {
            count++;
            if (glyph.getPoint(i).endOfContour) {
                addContourToPath(glyphPath, glyph, firstIndex, count);
                firstIndex = i + 1;
                count = 0;
            }
        }
        return glyphPath;
    }
    
    private static void addContourToPath(GeneralPath gp, Glyph glyph, int startIndex, int count) {
        int offset = 0;
        boolean connect = false;
        while (offset < count) {
            Shape s = null;
            Point point_minus1 = glyph.getPoint((offset==0) ? startIndex+count-1 : startIndex+(offset-1)%count);
            Point point = glyph.getPoint(startIndex + offset%count);
            Point point_plus1 = glyph.getPoint(startIndex + (offset+1)%count);
            Point point_plus2 = glyph.getPoint(startIndex + (offset+2)%count);
            if (point.onCurve && point_plus1.onCurve) {
                s = new Line2D.Float(point.x, -point.y, point_plus1.x, -point_plus1.y);
                offset++;
            } else if (point.onCurve && !point_plus1.onCurve && point_plus2.onCurve) {
                s = new QuadCurve2D.Float(
                    point.x,
                    -point.y,
                    point_plus1.x,
                    -point_plus1.y,
                    point_plus2.x,
                    -point_plus2.y);
                offset+=2;
            } else if (point.onCurve && !point_plus1.onCurve && !point_plus2.onCurve) {
                s = new QuadCurve2D.Float(
                    point.x,
                    -point.y,
                    point_plus1.x,
                    -point_plus1.y,
                    midValue(point_plus1.x, point_plus2.x),
                    -midValue(point_plus1.y, point_plus2.y));
                offset+=2;
            } else if (!point.onCurve && !point_plus1.onCurve) {
                s = new QuadCurve2D.Float(
                    midValue(point_minus1.x, point.x),
                    -midValue(point_minus1.y, point.y),
                    point.x,
                    -point.y,
                    midValue(point.x, point_plus1.x),
                    -midValue(point.y, point_plus1.y));
                offset++;
            } else if (!point.onCurve && point_plus1.onCurve) {
                s = new QuadCurve2D.Float(
                    midValue(point_minus1.x, point.x),
                    -midValue(point_minus1.y, point.y),
                    point.x,
                    -point.y,
                    point_plus1.x,
                    -point_plus1.y);
                offset++;
            } else {
                System.out.println("drawGlyph case not catered for!!");
                break;
            }
            gp.append(s, connect);
            connect = true;
        }
    }

    private static int midValue(int a, int b) {
        return a + (b - a)/2;
    }
}
