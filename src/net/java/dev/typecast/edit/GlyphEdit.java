/*
 * $Id: GlyphEdit.java,v 1.4 2004-12-21 10:24:57 davidsch Exp $
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

package net.java.dev.typecast.edit;

//import java.beans.*;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

import java.awt.event.MouseEvent;

import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.Scrollable;

import javax.swing.event.MouseInputListener;

import net.java.dev.typecast.ot.Point;
import net.java.dev.typecast.ot.OTFont;
import net.java.dev.typecast.ot.Glyph;

import net.java.dev.typecast.render.GlyphPathFactory;

/**
 * The glyph editor.  The user will perform operatons on the glyph within this
 * window using a variety of tools derived from {@link Tool Tool}.
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: GlyphEdit.java,v 1.4 2004-12-21 10:24:57 davidsch Exp $
 */
public class GlyphEdit extends JPanel implements Scrollable {

    private static final long serialVersionUID = 1L;

    private Glyph _glyph = null;
    private OTFont _font = null;
    private Tool _tool = null;
    private GeneralPath _glyphPath;

    private int _translateX = 0;
    private int _translateY = 0;
    private double _scaleFactor = 0.25f;

    private boolean _drawControlPoints = true;
    private boolean _preview = false;
    private Set<Point> _selectedPoints = new HashSet<Point>();

    //private static final String PROP_SAMPLE_PROPERTY = "SampleProperty";

    //private String sampleProperty;

    //private PropertyChangeSupport _propertySupport;

    /** Creates new GlyphEdit */
    public GlyphEdit() {
//        _propertySupport = new PropertyChangeSupport(this);

        setName("ContourView");
        setLayout(null);
//        setPreferredSize(new Dimension(1024, 1024));

        _tool = new PointTool(this);

        MouseInputListener mil = new MouseInputListener() {
            public void mouseClicked(MouseEvent e) {
            }
            public void mouseEntered(MouseEvent e) { }
            public void mouseExited(MouseEvent e) { }
            public void mousePressed(MouseEvent e) {
                if (_tool != null) {
                    if (e.isControlDown()) {
                        _tool.pressedControl(e.getPoint());
                    } else {
                        _tool.pressed(e.getPoint());
                    }
                }
            }
            public void mouseReleased(MouseEvent e) {
                if (_tool != null) {
                    _tool.released(e.getPoint());
                }
            }
            public void mouseDragged(MouseEvent e) {
                if (_tool != null) {
                    _tool.dragged(e.getPoint());
                }
            }
            public void mouseMoved(MouseEvent e) { }
        };
        addMouseListener(mil);
        addMouseMotionListener(mil);
    }

    //public String getSampleProperty () {
    //    return sampleProperty;
    //}

    //public void setSampleProperty (String value) {
    //    String oldValue = sampleProperty;
    //    sampleProperty = value;
    //    _propertySupport.firePropertyChange (PROP_SAMPLE_PROPERTY, oldValue, sampleProperty);
    //}

//    public void addPropertyChangeListener (PropertyChangeListener listener) {
//        _propertySupport.addPropertyChangeListener (listener);
//    }
//
//    public void removePropertyChangeListener (PropertyChangeListener listener) {
//        _propertySupport.removePropertyChangeListener (listener);
//    }

    public void paint(Graphics graphics) {
        super.paint(graphics);

        if (_glyph == null) {
            return;
        }

        Graphics2D g2d = (Graphics2D) graphics;

        int unitsPerEmBy2 = _font.getHeadTable().getUnitsPerEm() / 2;
        _translateX = 2 * unitsPerEmBy2;
        _translateY = 2 * unitsPerEmBy2;
        
        AffineTransform at = g2d.getTransform();
        AffineTransform atOriginal = new AffineTransform(at);
        at.scale(_scaleFactor, _scaleFactor);
        at.translate(_translateX, _translateY);
        g2d.setTransform(at);

        // Draw grid
        g2d.setPaint(Color.gray);
        g2d.draw(new Line2D.Float(-unitsPerEmBy2, 0, unitsPerEmBy2, 0));
        g2d.draw(new Line2D.Float(0, -unitsPerEmBy2, 0, unitsPerEmBy2));

        // Draw guides
        g2d.setPaint(Color.lightGray);
        g2d.draw(new Line2D.Float(-unitsPerEmBy2, -_font.getAscent(), unitsPerEmBy2, -_font.getAscent()));
        g2d.draw(new Line2D.Float(-unitsPerEmBy2, -_font.getDescent(), unitsPerEmBy2, -_font.getDescent()));
        g2d.draw(new Line2D.Float(_glyph.getLeftSideBearing(), -unitsPerEmBy2, _glyph.getLeftSideBearing(), unitsPerEmBy2));
        g2d.draw(new Line2D.Float(_glyph.getAdvanceWidth(), -unitsPerEmBy2, _glyph.getAdvanceWidth(), unitsPerEmBy2));

        // Draw contours
        g2d.setPaint(Color.black);

        if (_glyphPath == null) {
            _glyphPath = GlyphPathFactory.buildPath(_glyph);
        }

        // Render the glyph path
        if (_preview) {
            g2d.fill(_glyphPath);
        } else {
            g2d.draw(_glyphPath);
        }

        if (_drawControlPoints) {

            g2d.setTransform(atOriginal);

            // Draw control points
            for (int i = 0; i < _glyph.getPointCount(); i++) {
                int x = (int) (_scaleFactor * (_glyph.getPoint(i).x + _translateX));
                int y = (int) (_scaleFactor * (-_glyph.getPoint(i).y + _translateY));

                // Set the point colour based on selection
                if (_selectedPoints.contains(_glyph.getPoint(i))) {
                    g2d.setPaint(Color.blue);
                } else {
                    g2d.setPaint(Color.black);
                }
                
                // Draw the point based on its type (on or off curve)
                if (_glyph.getPoint(i).onCurve) {
                    g2d.fill(new Rectangle2D.Float(x - 2, y - 2, 5, 5));
                } else {
                    g2d.draw(new Rectangle2D.Float(x - 2, y - 2, 5, 5));
                }
                g2d.drawString(Integer.toString(i), x + 4, y - 4);
            }
        }
    }    

    public Glyph getGlyph() {
        return _glyph;
    }
    
    public void setGlyph(Glyph glyph) {
        
        _glyph = glyph;
        
        // How much space does this glyph need?
//        xOrigin = 0x5000;
//        yOrigin = 0x7080;

        setPreferredSize(new Dimension(1024, 1024));
        setSize(new Dimension(1024, 1024));
        
        // We have a new glyph, so repaint
        _glyphPath = null;
        invalidate();
        repaint();
    }
    
    public void modified() {
        _glyphPath = null;
    }

    public int getTranslateX() {
        return _translateX;
    }

    public void setTranslateX(int x) {
        _translateX = x;
    }

    public int getTranslateY() {
        return _translateY;
    }

    public void setTranslateY(int y) {
        _translateY = y;
    }

    public double getScaleFactor() {
        return _scaleFactor;
    }

    public void setScaleFactor(double factor) {
        _scaleFactor = factor;
    }

    public boolean isDrawControlPoints() {
        return _drawControlPoints;
    }

    public void setDrawControlPoints(boolean b) {
        _drawControlPoints = b;
    }

    public boolean isPreview() {
        return _preview;
    }

    public void setPreview(boolean b) {
        _preview = b;
    }

    public Set<Point> getSelectedPoints() {
        return _selectedPoints;
    }

//    private int transform(int p, int scale, int translate) {
//        return ((p * scale) >> 6) + translate;
//    }

//    public Font getFont() {
//        return font;
//    }
    
    public void setFont(OTFont font) {
        _font = font;
//        glyph = font.getGlyph(glyphIndex);
        _glyph = null;
//        repaint();
        
        // Determine the default view scaling for this font
        short unitsPerEm = _font.getHeadTable().getUnitsPerEm();
        
        _scaleFactor = 512.0 / unitsPerEm;
    }

    public Tool getTool() {
        return _tool;
    }
    
    public void setTool(Tool tool) {
        _tool = tool;
    }

//    public void executeCommand(Command command) {
//    }
    
    public boolean getScrollableTracksViewportWidth() {
        return false;
    }
    
    public int getScrollableBlockIncrement(java.awt.Rectangle rectangle, int param, int param2) {
        return 10;
    }
    
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }
    
    public java.awt.Dimension getPreferredScrollableViewportSize() {
        return getPreferredSize();
//        return new Dimension(1024, 1024);
    }
    
    public int getScrollableUnitIncrement(java.awt.Rectangle rectangle, int param, int param2) {
        return 1;
    }
    
}
