/*
 * $Id: GlyphEdit.java,v 1.1.1.1 2004-12-05 23:14:20 davidsch Exp $
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

import java.beans.*;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;
import java.awt.event.MouseEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.MouseInputListener;
import javax.swing.JPanel;
import javax.swing.Scrollable;
import net.java.dev.typecast.ot.Point;
import net.java.dev.typecast.ot.OTFont;
import net.java.dev.typecast.ot.Glyph;

/**
 *
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: GlyphEdit.java,v 1.1.1.1 2004-12-05 23:14:20 davidsch Exp $
 */
public class GlyphEdit extends JPanel implements Scrollable {

    private static final long serialVersionUID = 1L;

    private Glyph _glyph = null;
    private OTFont _font = null;
    private Tool _tool = null;

    private int _translateX = 0;
    private int _translateY = 0;
    private float _scaleFactor = 0.25f;

    private boolean _drawControlPoints = true;
    private boolean _preview = true;
    private Set<Point> _selectedPoints = new HashSet<Point>();

    //private static final String PROP_SAMPLE_PROPERTY = "SampleProperty";

    //private String sampleProperty;

    private PropertyChangeSupport _propertySupport;

    /** Creates new GlyphEdit */
    public GlyphEdit() {
        _propertySupport = new PropertyChangeSupport(this);

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

    public void addPropertyChangeListener (PropertyChangeListener listener) {
        _propertySupport.addPropertyChangeListener (listener);
    }

    public void removePropertyChangeListener (PropertyChangeListener listener) {
        _propertySupport.removePropertyChangeListener (listener);
    }

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

        int firstIndex = 0;
        int count = 0;
        int i;

        GeneralPath gp = new GeneralPath(GeneralPath.WIND_EVEN_ODD);
        for (i = 0; i < _glyph.getPointCount(); i++) {
            count++;
            if (_glyph.getPoint(i).endOfContour) {
//                drawContour(g2d, firstIndex, count);
                addContourToPath(gp, firstIndex, count);
                firstIndex = i + 1;
                count = 0;
            }
        }
        if (_preview) {
            g2d.fill(gp);
        } else {
            g2d.draw(gp);
        }

        if (_drawControlPoints) {

            g2d.setTransform(atOriginal);

            // Draw control points
            for (i = 0; i < _glyph.getPointCount(); i++) {
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

    private void addContourToPath(GeneralPath gp, int startIndex, int count) {
        int offset = 0;
        boolean connect = false;
        while (offset < count) {
            Shape s = null;
            Point point_minus1 = _glyph.getPoint((offset==0) ? startIndex+count-1 : startIndex+(offset-1)%count);
            Point point = _glyph.getPoint(startIndex + offset%count);
            Point point_plus1 = _glyph.getPoint(startIndex + (offset+1)%count);
            Point point_plus2 = _glyph.getPoint(startIndex + (offset+2)%count);
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

    private void drawContour(Graphics2D g2d, int startIndex, int count) {
        int offset = 0;
//        while (offset < (startIndex + count)) {
        while (offset < count) {
            Point point_minus1 = _glyph.getPoint((offset==0) ? startIndex+count-1 : startIndex+(offset-1)%count);
            Point point = _glyph.getPoint(startIndex + offset%count);
            Point point_plus1 = _glyph.getPoint(startIndex + (offset+1)%count);
            Point point_plus2 = _glyph.getPoint(startIndex + (offset+2)%count);
            if (point.onCurve && point_plus1.onCurve) {
                Line2D.Float line = new Line2D.Float(point.x, -point.y, point_plus1.x, -point_plus1.y);
                if (_preview) {
                    g2d.fill(line);
                } else {
                    g2d.draw(line);
                }
                offset++;
            } else if (point.onCurve && !point_plus1.onCurve && point_plus2.onCurve) {
                QuadCurve2D.Float curve = new QuadCurve2D.Float(
                    point.x,
                    -point.y,
                    point_plus1.x,
                    -point_plus1.y,
                    point_plus2.x,
                    -point_plus2.y);
                if (_preview) {
                    g2d.fill(curve);
                } else {
                    g2d.draw(curve);
                }
                offset+=2;
            } else if (point.onCurve && !point_plus1.onCurve && !point_plus2.onCurve) {
                QuadCurve2D.Float curve = new QuadCurve2D.Float(
                    point.x,
                    -point.y,
                    point_plus1.x,
                    -point_plus1.y,
                    midValue(point_plus1.x, point_plus2.x),
                    -midValue(point_plus1.y, point_plus2.y));
                if (_preview) {
                    g2d.fill(curve);
                } else {
                    g2d.draw(curve);
                }
                offset+=2;
            } else if (!point.onCurve && !point_plus1.onCurve) {
                QuadCurve2D.Float curve = new QuadCurve2D.Float(
                    midValue(point_minus1.x, point.x),
                    -midValue(point_minus1.y, point.y),
                    point.x,
                    -point.y,
                    midValue(point.x, point_plus1.x),
                    -midValue(point.y, point_plus1.y));
                if (_preview) {
                    g2d.fill(curve);
                } else {
                    g2d.draw(curve);
                }
                offset++;
            } else if (!point.onCurve && point_plus1.onCurve) {
                QuadCurve2D.Float curve = new QuadCurve2D.Float(
                    midValue(point_minus1.x, point.x),
                    -midValue(point_minus1.y, point.y),
                    point.x,
                    -point.y,
                    point_plus1.x,
                    -point_plus1.y);
                if (_preview) {
                    g2d.fill(curve);
                } else {
                    g2d.draw(curve);
                }
                offset++;
            } else {
                System.out.println("drawGlyph case not catered for!!");
                break;
            }
        }
    }
/*  
    private static int getCurveValue(long t, long p0, long p1, long p2) {
        return (int)((((0x40-t)*(0x40-t)*p0)>>12)
            + ((0x80*t*(0x40-t)*p1)>>18)
            + ((t*t*p2)>>12));
    }
*/
//    public int getGlyphIndex() {
//        return glyphIndex;
//    }

    private static int midValue(int a, int b) {
        return a + (b - a)/2;
    }

    public Glyph getGlyph() {
        return _glyph;
    }
    
    public void setGlyph(Glyph glyph) {
        this._glyph = glyph;
        
        // How much space does this glyph need?
//        xOrigin = 0x5000;
//        yOrigin = 0x7080;

        setPreferredSize(new Dimension(1024, 1024));
        setSize(new Dimension(1024, 1024));
        invalidate();
        repaint();
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

    public float getScaleFactor() {
        return _scaleFactor;
    }

    public void setScaleFactor(float factor) {
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
        this._font = font;
//        glyph = font.getGlyph(glyphIndex);
        _glyph = null;
//        repaint();
    }

    public Tool getTool() {
        return _tool;
    }
    
    public void setTool(Tool tool) {
        this._tool = tool;
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
