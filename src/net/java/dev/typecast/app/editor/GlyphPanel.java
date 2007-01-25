/*
 * $Id: GlyphPanel.java,v 1.2 2007-01-25 08:40:27 davidsch Exp $
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

package net.java.dev.typecast.app.editor;

import java.awt.BorderLayout;
import java.awt.Color;

import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.java.dev.typecast.app.framework.EditorView;

import net.java.dev.typecast.edit.GlyphEdit;

import net.java.dev.typecast.ot.Glyph;
import net.java.dev.typecast.ot.OTFont;

import net.java.dev.typecast.ot.table.GlyphDescription;

import net.java.dev.typecast.app.editor.GlyphPanelToolBar;
import net.java.dev.typecast.app.editor.GlyphPanelStatusBar;

/**
 *
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: GlyphPanel.java,v 1.2 2007-01-25 08:40:27 davidsch Exp $
 */
public class GlyphPanel extends JPanel implements EditorView {

    private static final long serialVersionUID = 1L;

    private Properties _properties;
    private GlyphEdit _glyphEdit = new GlyphEdit();
    private GlyphPanelToolBar _toolBar = new GlyphPanelToolBar();
    private GlyphPanelStatusBar _glyphPanelStatusBar =
            new GlyphPanelStatusBar();

    /** Creates new GlyphPanel */
    public GlyphPanel(Properties properties) {
        _properties = properties;
        setName("Outline");
        setLayout(new BorderLayout());

        // Toolbar
        add(_toolBar, BorderLayout.NORTH);

        // Editor
        _glyphEdit.setBackground(Color.white);
        _glyphEdit.setScaleFactor(
                Float.valueOf(properties.getProperty("Zoom", "0.25")).floatValue());
        add(new JScrollPane(
                    _glyphEdit,
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS),
                BorderLayout.CENTER);

        // Status bar
        _glyphPanelStatusBar.setGlyphEdit(_glyphEdit);
        add(_glyphPanelStatusBar, BorderLayout.SOUTH);
    }

    /**
     * The GlyphPanel deals with GlyphDescriptions, so the Object parameter must
     * implement the GlyphDescription interface.
     */
    public void setModel(OTFont font, Object obj) {
        if (obj instanceof GlyphDescription) {
            _glyphEdit.setFont(font);
            GlyphDescription gd = (GlyphDescription) obj;
            _glyphEdit.setGlyph(new Glyph(
                    gd,
                    font.getHmtxTable().getLeftSideBearing(gd.getGlyphIndex()),
                    font.getHmtxTable().getAdvanceWidth(gd.getGlyphIndex())));
        }
    }
    
    public GlyphEdit getGlyphEdit() {
        return _glyphEdit;
    }
    
    public void setProperties() {
        _properties.setProperty("Zoom", String.valueOf(_glyphEdit.getScaleFactor()));
    }
}
