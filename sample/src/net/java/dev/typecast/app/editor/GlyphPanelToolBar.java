/*
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004-2016 David Schweinsberg
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

import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

/**
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class GlyphPanelToolBar extends JToolBar {

    private static final long serialVersionUID = 1L;

    /** Creates new GlyphPanelToolBar */
    public GlyphPanelToolBar() {
        URL iconURL = ClassLoader.getSystemResource("images/cursor_16x16.gif");
        JButton button = new JButton(new ImageIcon(iconURL));
        add(button);

        iconURL = ClassLoader.getSystemResource("images/crosshair_16x16.gif");
        button = new JButton(new ImageIcon(iconURL));
        add(button);

        iconURL = ClassLoader.getSystemResource("toolbarButtonGraphics/general/ZoomIn16.gif");
        button = new JButton(new ImageIcon(iconURL));
        add(button);

        iconURL = ClassLoader.getSystemResource("toolbarButtonGraphics/general/ZoomOut16.gif");
        button = new JButton(new ImageIcon(iconURL));
        add(button);
    }

}
