/*
 * $Id: GlyphPanel.java,v 1.2 2004-12-21 10:26:10 davidsch Exp $
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

package net.java.dev.typecast.apps.editor;

import java.awt.BorderLayout;
import java.awt.Color;

import java.util.Properties;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.java.dev.typecast.edit.GlyphEdit;
import net.java.dev.typecast.ot.Glyph;

/**
 *
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: GlyphPanel.java,v 1.2 2004-12-21 10:26:10 davidsch Exp $
 */
public class GlyphPanel extends JPanel {

    private static final long serialVersionUID = 1L;

    private Properties _properties;
    private GlyphEdit _glyphEdit = new GlyphEdit();
    private GlyphPanelToolBar _toolBar = new GlyphPanelToolBar();
    private GlyphPanelStatusBar _glyphPanelStatusBar =
            new GlyphPanelStatusBar();

    /** Creates new GlyphPanel */
    public GlyphPanel(Properties properties) {
        _properties = properties;
        setLayout(new BorderLayout());

        // Toolbar
        add(_toolBar, BorderLayout.NORTH);

        // Editor
        _glyphEdit.setBackground(Color.white);
        _glyphEdit.setScaleFactor(
                Float.valueOf(properties.getProperty("Zoom", "0.25")).floatValue());
        add(new JScrollPane(_glyphEdit), BorderLayout.CENTER);

        // Status bar
        _glyphPanelStatusBar.setGlyphEdit(_glyphEdit);
        add(_glyphPanelStatusBar, BorderLayout.SOUTH);
    }
    
    public GlyphEdit getGlyphEdit() {
        return _glyphEdit;
    }
    
    public void setProperties() {
        _properties.setProperty("Zoom", String.valueOf(_glyphEdit.getScaleFactor()));
    }
}
