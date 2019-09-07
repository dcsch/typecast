/*
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

package net.java.dev.typecast.edit;

import java.awt.Cursor;
import java.awt.Point;

import java.util.Iterator;
import java.util.Set;

import net.java.dev.typecast.edit.GlyphEdit;

import net.java.dev.typecast.ot.Glyph;

/**
 * A simple point selection and manipulation tool.  Allows the user to select a
 * point with the cursor, to move that point by dragging, and to move the point
 * on- and off-curve by selecting the point with the control key pressed.
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class PointTool extends Tool {

    private GlyphEdit _glyphEdit;
    private Command _command;
    
    /** Creates new PointTool */
    public PointTool(GlyphEdit glyphEdit) {
        _glyphEdit = glyphEdit;
        
        // BUG: The crosshair cursor keeps coming up as a text cursor on my
        // Windows XP system :-(
        //_glyphEdit.setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        _glyphEdit.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
    }

    /**
     * Selects a point
     */
    public void pressed(Point p) {
        _glyphEdit.getSelectedPoints().clear();
        Glyph glyph = _glyphEdit.getGlyph();
        for (int i = 0; i < glyph.getPointCount(); i++) {
            net.java.dev.typecast.ot.Point gp = glyph.getPoint(i);
            double gpx = _glyphEdit.getScaleFactor() * (gp.x + _glyphEdit.getTranslateX());
            double gpy = _glyphEdit.getScaleFactor() * (-gp.y + _glyphEdit.getTranslateY());
            if (((gpx >= p.x - 2) && (gpx <= p.x + 2)) &&
                ((gpy >= p.y - 2) && (gpy <= p.y + 2))) {
                _glyphEdit.getSelectedPoints().add(gp);
            }
        }
        _glyphEdit.modified();
        _glyphEdit.repaint();
    }
    
    /**
     * Toggles the selected point between on-curve and off-curve
     */
    public void pressedControl(Point p) {
        Glyph glyph = _glyphEdit.getGlyph();
        for (int i = 0; i < glyph.getPointCount(); i++) {
            net.java.dev.typecast.ot.Point gp = glyph.getPoint(i);
            double gpx = _glyphEdit.getScaleFactor() * (gp.x + _glyphEdit.getTranslateX());
            double gpy = _glyphEdit.getScaleFactor() * (-gp.y + _glyphEdit.getTranslateY());
            if (((gpx >= p.x - 2) && (gpx <= p.x + 2)) &&
                ((gpy >= p.y - 2) && (gpy <= p.y + 2))) {
                gp.onCurve = !gp.onCurve;
            }
        }
        _glyphEdit.modified();
        _glyphEdit.repaint();
    }
    
    /**
     * Moves the selected points
     */
    public void dragged(Point p) {
        int x = (int)(p.x / _glyphEdit.getScaleFactor() - _glyphEdit.getTranslateX());
        int y = -(int)(p.y / _glyphEdit.getScaleFactor() - _glyphEdit.getTranslateY());
        Iterator iter = _glyphEdit.getSelectedPoints().iterator();
        while (iter.hasNext()) {
            net.java.dev.typecast.ot.Point gp = (net.java.dev.typecast.ot.Point) iter.next();
            gp.x = x;
            gp.y = y;
        }
        _glyphEdit.modified();
        _glyphEdit.repaint();
    }

    /**
     * nop
     */
    public void released(Point p) {
    }
}
