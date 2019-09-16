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

package net.java.dev.typecast.render;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;

import java.awt.image.BufferedImage;

import net.java.dev.typecast.ot.Glyph;

/**
 * A factory for generating bitmaps from glyph outlines.
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
class GlyphImageFactory {
    
    /**
     * Build an {@link java.awt.Image Image} from a
     * {@link net.java.dev.typecast.ot.Glyph Glyph}.
     * @param glyph The glyph to render to an image.
     * @param at The transformation to apply to the glyph before rendering
     * @param width The width of the image to render into
     * @param height The height of the image to render into
     * @return 
     */
    public static BufferedImage buildImage(
            Glyph glyph,
            AffineTransform at,
            int width,
            int height) {
        
        if (glyph == null) {
            return null;
        }
        
        // We'll create a greyscale image to render to
        BufferedImage image = new BufferedImage(
                width,
                height,
                BufferedImage.TYPE_BYTE_GRAY);
        
        // Render the glyph to the image
        Graphics2D g = (Graphics2D) image.getGraphics();
        GeneralPath path = GlyphPathFactory.buildPath(glyph);
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        g.setTransform(at);
        g.setColor(Color.BLACK);
        g.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g.fill(path);
        
        return image;
    }
}
