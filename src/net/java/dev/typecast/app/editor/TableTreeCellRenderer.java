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

import java.awt.Font;
import java.awt.Graphics;
import java.awt.SystemColor;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;

import javax.swing.tree.TreeCellRenderer;

/**
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
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
