/*
 * $Id: GlyphPanel.java,v 1.1 2004-12-15 14:07:40 davidsch Exp $
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

import net.java.dev.typecast.edit.GlyphEdit;
import net.java.dev.typecast.ot.Glyph;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Properties;
import javax.swing.JScrollPane;

/**
 *
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: GlyphPanel.java,v 1.1 2004-12-15 14:07:40 davidsch Exp $
 */
public class GlyphPanel extends javax.swing.JPanel {

    private static final long serialVersionUID = 1L;

    private Properties properties;
    private GlyphEdit glyphEdit = new GlyphEdit();
    private GlyphPanelToolBar toolBar = new GlyphPanelToolBar();
    private GlyphPanelStatusBar glyphPanelStatusBar = new GlyphPanelStatusBar();

    /** Creates new GlyphPanel */
    public GlyphPanel(Properties properties) {
        this.properties = properties;
        setLayout(new BorderLayout());

        // Toolbar
        add(toolBar, BorderLayout.NORTH);

        // Editor
        glyphEdit.setBackground(Color.white);
        glyphEdit.setScaleFactor(Float.valueOf(properties.getProperty("Zoom", "0.25")).floatValue());
        add(new JScrollPane(glyphEdit), BorderLayout.CENTER);

        // Status bar
        glyphPanelStatusBar.setGlyphEdit(glyphEdit);
        add(glyphPanelStatusBar, BorderLayout.SOUTH);
    }
    
    public void setFont(net.java.dev.typecast.ot.OTFont font) {
        glyphEdit.setFont(font);
    }
    
    public void setGlyph(Glyph glyph) {
        glyphEdit.setGlyph(glyph);
    }
    
    public void setProperties() {
        properties.setProperty("Zoom", String.valueOf(glyphEdit.getScaleFactor()));
    }
}
