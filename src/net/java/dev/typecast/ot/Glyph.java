/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- *
 * This software is published under the terms of the Apache Software License *
 * version 1.1, a copy of which has been included with this distribution in  *
 * the LICENSE file.                                                         *
 *****************************************************************************/

package net.java.dev.typecast.ot;

import net.java.dev.typecast.ot.table.GlyphDescription;
import net.java.dev.typecast.ot.table.GlyfDescript;

/**
 * An individual glyph within a font.
 * @version $Id: Glyph.java,v 1.1.1.1 2004-12-05 23:14:27 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class Glyph {

    protected short leftSideBearing;
    protected int advanceWidth;
    private Point[] points;

    public Glyph(GlyphDescription gd, short lsb, int advance) {
        leftSideBearing = lsb;
        advanceWidth = advance;
        describe(gd);
    }

    public int getAdvanceWidth() {
        return advanceWidth;
    }

    public short getLeftSideBearing() {
        return leftSideBearing;
    }

    public Point getPoint(int i) {
        return points[i];
    }

    public int getPointCount() {
        return points.length;
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
        for (int i = 0; i < points.length; i++) {
            //points[i].x = ( points[i].x * factor ) >> 6;
            //points[i].y = ( points[i].y * factor ) >> 6;
            points[i].x = ((points[i].x<<10) * factor) >> 26;
            points[i].y = ((points[i].y<<10) * factor) >> 26;
        }
        leftSideBearing = (short)(( leftSideBearing * factor) >> 6);
        advanceWidth = (advanceWidth * factor) >> 6;
    }

    /**
     * Set the points of a glyph from the GlyphDescription
     */
    private void describe(GlyphDescription gd) {
        int endPtIndex = 0;
        points = new Point[gd.getPointCount() + 2];
        for (int i = 0; i < gd.getPointCount(); i++) {
            boolean endPt = gd.getEndPtOfContours(endPtIndex) == i;
            if (endPt) {
                endPtIndex++;
            }
            points[i] = new Point(
                    gd.getXCoordinate(i),
                    gd.getYCoordinate(i),
                    (gd.getFlags(i) & GlyfDescript.onCurve) != 0,
                    endPt);
        }

        // Append the origin and advanceWidth points (n & n+1)
        points[gd.getPointCount()] = new Point(0, 0, true, true);
        points[gd.getPointCount()+1] = new Point(advanceWidth, 0, true, true);
    }
}
