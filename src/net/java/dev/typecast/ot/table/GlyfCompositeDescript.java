/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.ot.table;

import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Glyph description for composite glyphs.  Composite glyphs are made up of one
 * or more simple glyphs, usually with some sort of transformation applied to
 * each.
 *
 * @version $Id: GlyfCompositeDescript.java,v 1.1.1.1 2004-12-05 23:14:40 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public class GlyfCompositeDescript extends GlyfDescript {

    private ArrayList<GlyfCompositeComp> _components =
        new ArrayList<GlyfCompositeComp>();

    public GlyfCompositeDescript(GlyfTable parentTable, DataInput di)
    throws IOException {
        super(parentTable, (short) -1, di);
        
        // Get all of the composite components
        GlyfCompositeComp comp;
        int firstIndex = 0;
        int firstContour = 0;
        try {
        do {
            _components.add(comp = new GlyfCompositeComp(firstIndex, firstContour, di));
//            firstIndex += parentTable.getDescription(comp.getGlyphIndex()).getPointCount();
//            firstContour += parentTable.getDescription(comp.getGlyphIndex()).getContourCount();
        } while ((comp.getFlags() & GlyfCompositeComp.MORE_COMPONENTS) != 0);

        // Are there hinting intructions to read?
        if ((comp.getFlags() & GlyfCompositeComp.WE_HAVE_INSTRUCTIONS) != 0) {
            readInstructions(di, di.readShort());
        }
        } catch (IOException e) {
            throw e;
        } catch (Exception e) {
            int foo = 0;
        }
    }

    public int getEndPtOfContours(int i) {
        GlyfCompositeComp c = getCompositeCompEndPt(i);
        if (c != null) {
            GlyphDescription gd = parentTable.getDescription(c.getGlyphIndex());
            return gd.getEndPtOfContours(i - c.getFirstContour()) + c.getFirstIndex();
        }
        return 0;
    }

    public byte getFlags(int i) {
        GlyfCompositeComp c = getCompositeComp(i);
        if (c != null) {
            GlyphDescription gd = parentTable.getDescription(c.getGlyphIndex());
            return gd.getFlags(i - c.getFirstIndex());
        }
        return 0;
    }

    public short getXCoordinate(int i) {
        GlyfCompositeComp c = getCompositeComp(i);
        if (c != null) {
            GlyphDescription gd = parentTable.getDescription(c.getGlyphIndex());
            int n = i - c.getFirstIndex();
            int x = gd.getXCoordinate(n);
            int y = gd.getYCoordinate(n);
            short x1 = (short) c.scaleX(x, y);
            x1 += c.getXTranslate();
            return (short) x1;
        }
        return 0;
    }

    public short getYCoordinate(int i) {
        GlyfCompositeComp c = getCompositeComp(i);
        if (c != null) {
            GlyphDescription gd = parentTable.getDescription(c.getGlyphIndex());
            int n = i - c.getFirstIndex();
            int x = gd.getXCoordinate(n);
            int y = gd.getYCoordinate(n);
            short y1 = (short) c.scaleY(x, y);
            y1 += c.getYTranslate();
            return (short) y1;
        }
        return 0;
    }

    public boolean isComposite() {
        return true;
    }

    public int getPointCount() {
        GlyfCompositeComp c = _components.get(_components.size()-1);
        return c.getFirstIndex() + parentTable.getDescription(c.getGlyphIndex()).getPointCount();
    }

    public int getContourCount() {
        GlyfCompositeComp c = _components.get(_components.size()-1);
        return c.getFirstContour() + parentTable.getDescription(c.getGlyphIndex()).getContourCount();
    }

    public int getComponentIndex(int i) {
        return _components.get(i).getFirstIndex();
    }

    public int getComponentCount() {
        return _components.size();
    }

    public GlyfCompositeComp getComponent(int i) {
        return _components.get(i);
    }

    protected GlyfCompositeComp getCompositeComp(int i) {
        GlyfCompositeComp c;
        for (int n = 0; n < _components.size(); n++) {
            c = _components.get(n);
            GlyphDescription gd = parentTable.getDescription(c.getGlyphIndex());
            if (c.getFirstIndex() <= i && i < (c.getFirstIndex() + gd.getPointCount())) {
                return c;
            }
        }
        return null;
    }

    protected GlyfCompositeComp getCompositeCompEndPt(int i) {
        GlyfCompositeComp c;
        for (int j = 0; j < _components.size(); j++) {
            c = _components.get(j);
            GlyphDescription gd = parentTable.getDescription(c.getGlyphIndex());
            if (c.getFirstContour() <= i && i < (c.getFirstContour() + gd.getContourCount())) {
                return c;
            }
        }
        return null;
    }
}
