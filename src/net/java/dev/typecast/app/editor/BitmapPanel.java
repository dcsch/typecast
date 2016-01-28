/*
 * Typecast - The Font Development Environment
 *
 * Copyright (c) 2004-2016 David Schweinsberg
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.java.dev.typecast.app.editor;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import net.java.dev.typecast.app.framework.EditorView;
import net.java.dev.typecast.ot.OTFont;
import net.java.dev.typecast.ot.table.SbixTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A basic bitmap view.
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public final class BitmapPanel extends JPanel implements EditorView {

    private BufferedImage _image;
    
    private static final long serialVersionUID = 1L;

    static final Logger logger = LoggerFactory.getLogger(BitmapPanel.class);

    public BitmapPanel() {
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(_image, 0, 0, this);
    }

    @Override
    public void setModel(OTFont font, Object obj) {
        SbixTable.GlyphDataRecord gdr = (SbixTable.GlyphDataRecord) obj;
        ByteArrayInputStream input = new ByteArrayInputStream(gdr.getData());
        try {
            _image = ImageIO.read(input);
        } catch (IOException e) {
            logger.error("Unable to load image data: " + e.toString());
        }
    }
}
