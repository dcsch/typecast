/*****************************************************************************
 * Copyright (C) The Apache Software Foundation. All rights reserved.        *
 * ------------------------------------------------------------------------- * 
 * This software is published under the terms of the Apache Software License * 
 * version 1.1, a copy of which has been included with this distribution in  * 
 * the LICENSE file.                                                         * 
 *****************************************************************************/

package net.java.dev.typecast.ot.table;

/**
 * Specifies access to glyph description classes, simple and composite.
 * @version $Id: GlyphDescription.java,v 1.1.1.1 2004-12-05 23:14:42 davidsch Exp $
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 */
public interface GlyphDescription {
    public int getEndPtOfContours(int i);
    public byte getFlags(int i);
    public short getXCoordinate(int i);
    public short getYCoordinate(int i);
    public short getXMaximum();
    public short getXMinimum();
    public short getYMaximum();
    public short getYMinimum();
    public boolean isComposite();
    public int getPointCount();
    public int getContourCount();
    //  public int getComponentIndex(int c);
    //  public int getComponentCount();
}
