/*
 * $Id: TableTreeCellRenderer.java,v 1.1 2004-12-15 14:07:41 davidsch Exp $
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

import java.awt.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: TableTreeCellRenderer.java,v 1.1 2004-12-15 14:07:41 davidsch Exp $
 */
public class TableTreeCellRenderer extends JLabel implements TreeCellRenderer {
    
    private static final long serialVersionUID = 1L;
    
    private boolean _selected;
    private Font font = new java.awt.Font("SansSerif", java.awt.Font.PLAIN, 12);
    
    public java.awt.Component getTreeCellRendererComponent(
        JTree tree,
        Object value,
        boolean selected,
        boolean expanded,
        boolean leaf,
        int row,
        boolean hasFocus) {

        String str = tree.convertValueToText(
            value,
            selected,
            expanded,
            leaf,
            row,
            hasFocus);
        setFont(font);
        setText(str);
        setToolTipText(str);
        if (leaf) {
            setIcon(null);
        } else {
            setIcon(null);
        }
        setForeground(
            selected ?
                SystemColor.textHighlightText :
                SystemColor.textText);
        _selected = selected;
        return this;
    }
    
    public void paint(Graphics g) {
        if (_selected) {
            g.setColor(SystemColor.textHighlight);
        } else if(getParent() != null) {
            g.setColor(getParent().getBackground());
        } else {
            g.setColor(getBackground());
        }
        Icon icon = getIcon();
        int offset = 0;
        if (icon != null && getText() != null) {
            offset = icon.getIconWidth() + getIconTextGap();
        }
        g.fillRect(offset, 0, getWidth() - 1 - offset, getHeight() - 1);
        super.paint(g);
    }
}
