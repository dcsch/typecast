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

package net.java.dev.typecast.app.editor;

import java.awt.GridLayout;

import java.awt.event.MouseEvent;

import java.net.URL;

import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import javax.swing.event.MouseInputListener;

import net.java.dev.typecast.edit.GlyphEdit;

import net.java.dev.typecast.ot.Point;

/**
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class GlyphPanelStatusBar extends JPanel {

    private static final long serialVersionUID = 1L;

    private GlyphEdit _glyphEdit;
    private JLabel _cursorPos;
    private JLabel _selPos;
    private JLabel _selDelta;
    
    /** Creates new GlyphEditStatusBar */
    public GlyphPanelStatusBar() {
        setLayout(new GridLayout(1, 5));
        URL iconURL = ClassLoader.getSystemResource("images/cursor_16x16.gif");
        add(_cursorPos = new JLabel(
                "0, 0",
                new ImageIcon(iconURL),
                SwingConstants.LEFT));
        iconURL = ClassLoader.getSystemResource("images/point_selected_16.gif");
        add(_selPos = new JLabel(
                "---, ---",
                new ImageIcon(iconURL),
                SwingConstants.LEFT));
        iconURL = ClassLoader.getSystemResource(
                "images/point_and_cursor_16.gif");
        add(_selDelta = new JLabel(
                "---, ---",
                new ImageIcon(iconURL),
                SwingConstants.LEFT));
    }

    public GlyphEdit getGlyphEdit() {
        return _glyphEdit;
    }
    
    public void setGlyphEdit(GlyphEdit glyphEdit) {
        _glyphEdit = glyphEdit;

        // Create a MouseInputListener to track the location of the cursor
        // within the GlyphEdit window
        MouseInputListener mil = new MouseInputListener() {
            public void mouseClicked(MouseEvent e) { }
            public void mouseEntered(MouseEvent e) { }
            public void mouseExited(MouseEvent e) { }
            public void mousePressed(MouseEvent e) {
                setCursorStatus(e.getX(), e.getY());
                setSelectedStatus();
            }
            public void mouseReleased(MouseEvent e) { }
            public void mouseDragged(MouseEvent e) {
                setCursorStatus(e.getX(), e.getY());
                setSelectedStatus();
            }
            public void mouseMoved(MouseEvent e) {
                setCursorStatus(e.getX(), e.getY());
            }
        };
        glyphEdit.addMouseListener(mil);
        glyphEdit.addMouseMotionListener(mil);
    }
    
    private void setCursorStatus(int x, int y) {
        double f = _glyphEdit.getScaleFactor();
        int x1 = (int)((double) x / f - (double) _glyphEdit.getTranslateX());
        int y1 = -(int)((double) y / f - (double) _glyphEdit.getTranslateY());
        
        // Cursor position
        _cursorPos.setText(x1 + ", " + y1);
        
        // Difference between cursor and selected point
        Set s = _glyphEdit.getSelectedPoints();
        if (s.size() == 1) {
            Point p = (Point) s.iterator().next();
            _selDelta.setText((x1 - p.x) + ", " + (y1 - p.y));
        } else {
            _selDelta.setText("---, ---");
        }
    }
    
    private void setSelectedStatus() {
        Set s = _glyphEdit.getSelectedPoints();
        if (s.size() == 1) {
            Point p = (Point) s.iterator().next();
            _selPos.setText(p.x + ", " + p.y);
        } else {
            _selPos.setText("---, ---");
        }
    }
}
