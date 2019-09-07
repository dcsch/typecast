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

package net.java.dev.typecast.edit;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.AbstractListModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import net.java.dev.typecast.app.framework.EditorView;
import net.java.dev.typecast.ot.OTFont;
import net.java.dev.typecast.ot.table.CmapFormat;
import net.java.dev.typecast.render.GlyphImageFactory;

/**
 * An editor for the character-to-glyph map, as represented in the CmapTable.
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class CharacterMap extends JScrollPane implements EditorView {

    private static final long serialVersionUID = 1L;
    
    private static final int CELL_WIDTH = 48;
    private static final int CELL_HEIGHT = 60;
    
    private AbstractListModel _listModel;
    private CmapFormat _cmapFormat;
    private OTFont _font;
    private AffineTransform _tx;
    private final Font _labelFont = new Font("SansSerif", Font.PLAIN, 10);
    
    private class Mapping {

        private final int _charCode;
        private final int _glyphCode;
        
        public Mapping(int charCode, int glyphCode) {
            _charCode = charCode;
            _glyphCode = glyphCode;
        }

        public int getCharCode() {
            return _charCode;
        }
        
        public int getGlyphCode() {
            return _glyphCode;
        }
        
        public Image getGlyphImage() {
            
            // NOTE: We're not caching the image as we can be dealing with
            // quite a lot of them
            return GlyphImageFactory.buildImage(
                    _font.getGlyph(_glyphCode),
                    _tx,
                    CELL_WIDTH,
                    CELL_HEIGHT - 10);
        }
    }
    
    private class CharListCellRenderer extends JComponent implements ListCellRenderer {

        private static final long serialVersionUID = 1L;
        
        private Mapping _mapping;
        private int _index;
        private boolean _isSelected;
        private final AffineTransform _imageTx =
                new AffineTransform(1.0, 0.0, 0.0, 1.0, 0.0, 0.0);
        
        /**
         * Renders each individual cell
         */
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2d = (Graphics2D) g;

            if (_isSelected) {
                g2d.setColor(Color.BLACK);
                g2d.fillRect(0, CELL_HEIGHT - 10, CELL_WIDTH, 10);
                g2d.setColor(Color.WHITE);
            } else {
                g2d.setColor(Color.WHITE);
                g2d.fillRect(0, CELL_HEIGHT - 10, CELL_WIDTH, 10);
                g2d.setColor(Color.BLACK);
            }
            
            // Draw the glyph
            g2d.drawImage(_mapping.getGlyphImage(), _imageTx, null);

            // Label this cell with the character code
            g2d.setFont(_labelFont);
            g2d.drawString(
                    String.format("%04X", _mapping.getCharCode()),
                    1,
                    CELL_HEIGHT - 1);
        }
        
        @Override
        public Component getListCellRendererComponent(
                JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            _mapping = (Mapping) value;
            _index = index;
            _isSelected = isSelected;
            setPreferredSize(new Dimension(CELL_WIDTH + 1, CELL_HEIGHT + 1));
            setToolTipText(String.format("Glyph ID: %d",
                    _mapping.getGlyphCode()));
            return this;
        }
    }
    
    /** Creates a new instance of CharacterMap */
    public CharacterMap() {
        super(
            JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
            JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        setName("Character Map");
    }

    @Override
    public void setModel(OTFont font, Object obj) {
        if (obj instanceof CmapFormat) {
            _font = font;
            _cmapFormat = (CmapFormat) obj;

            // Set up a list model to wrap the cmap
            _listModel = new AbstractListModel() {

                private static final long serialVersionUID = 1L;
                private final ArrayList<Mapping> _mappings = new ArrayList<>();

                {
                    for (int i = 0; i < _cmapFormat.getRangeCount(); ++i) {
                        CmapFormat.Range range = _cmapFormat.getRange(i);
                        for (int j = range.getStartCode(); j <= range.getEndCode(); ++j) {
                            _mappings.add(new Mapping(j, _cmapFormat.mapCharCode(j)));
                        }
                    }
                }

                @Override
                public Object getElementAt(int index) {
                    return _mappings.get(index);
                }

                @Override
                public int getSize() {
                    return _mappings.size();
                }
            };

            final JList list = new JList(_listModel);
            list.setBackground(Color.LIGHT_GRAY);
            list.setCellRenderer(new CharListCellRenderer());
            list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
            list.setVisibleRowCount(
                    _listModel.getSize() / 16 +
                    (_listModel.getSize() % 16 > 0 ? 1 : 0));
            setViewportView(list);

            // Create a mouse listener so we can listen to double-clicks
            MouseListener mouseListener = new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        int index = list.locationToIndex(e.getPoint());
                    }
                }
            };
            list.addMouseListener(mouseListener);

    //        int unitsPerEmBy2 = _font.getHeadTable().getUnitsPerEm() / 2;
    //        int translateX = 2 * unitsPerEmBy2;
    //        int translateY = 2 * unitsPerEmBy2;

            // How much should we scale the font to fit it into our tiny bitmap?
            double scaleFactor = 40.0 / _font.getHeadTable().getUnitsPerEm();

            _tx = new AffineTransform();
            _tx.translate(2, CELL_HEIGHT - 20);
            _tx.scale(scaleFactor, -scaleFactor);
        }
    }
}
