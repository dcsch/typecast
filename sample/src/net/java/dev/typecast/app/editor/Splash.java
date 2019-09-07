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

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;

import java.net.URL;

import javax.swing.JWindow;

/**
 *
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class Splash extends JWindow {

    private static final long serialVersionUID = 1L;

    private Image image;
    
    /** Creates new Splash */
    public Splash() {
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        URL imageURL = ClassLoader.getSystemResource("images/Typecast.gif");
        image = Toolkit.getDefaultToolkit().getImage(imageURL);
        setSize(300, 480);
        setLocation(d.width/2 - 150, d.height/2 - 240);
    }

    public void paint(java.awt.Graphics graphics) {
        graphics.drawImage(image, 0, 0, this);
    }
}
