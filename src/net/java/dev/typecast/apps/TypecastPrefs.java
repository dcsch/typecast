/*
 * $Id: TypecastPrefs.java,v 1.1 2004-12-09 23:41:29 davidsch Exp $
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

package net.java.dev.typecast.apps;

import java.util.prefs.Preferences;
import java.awt.Point;
import java.awt.Dimension;

/**
 * A class to handle all the various application preferences
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: TypecastPrefs.java,v 1.1 2004-12-09 23:41:29 davidsch Exp $
 */
public class TypecastPrefs {
    
    // Key strings
    private static final String APP_WINDOW_POS = "app_position";
    private static final String APP_WINDOW_SIZE = "app_size";
    private static final String TREE_WIDTH = "tree_width";
    
    // Default values
    private static final int APP_WINDOW_POS_X_DEFAULT = 0;
    private static final int APP_WINDOW_POS_Y_DEFAULT = 0;
    private static final int APP_WINDOW_SIZE_WIDTH_DEFAULT = 640;
    private static final int APP_WINDOW_SIZE_HEIGHT_DEFAULT = 480;
    private static final int TREE_WIDTH_DEFAULT = 200;
    
    private Point _appWindowPos;
    private Dimension _appWindowSize;
    private int _treeWidth;

    /** Creates a new instance of TypecastPrefs */
    public TypecastPrefs() {
    }
    
    public void load(Preferences prefs) {
        _appWindowPos = getPosition(
                prefs,
                APP_WINDOW_POS,
                new Point(APP_WINDOW_POS_X_DEFAULT, APP_WINDOW_POS_Y_DEFAULT));
        _appWindowSize = getSize(
                prefs,
                APP_WINDOW_SIZE,
                new Dimension(
                    APP_WINDOW_SIZE_WIDTH_DEFAULT,
                    APP_WINDOW_SIZE_HEIGHT_DEFAULT));
        _treeWidth = prefs.getInt(TREE_WIDTH, TREE_WIDTH_DEFAULT);
    }

    public void save(Preferences prefs) {
        putPosition(prefs, APP_WINDOW_POS, _appWindowPos);
        putSize(prefs, APP_WINDOW_SIZE, _appWindowSize);
        prefs.putInt(TREE_WIDTH, _treeWidth);
    }

    public Point getAppWindowPos() {
        return _appWindowPos;
    }
    
    public void setAppWindowPos(Point pos) {
        _appWindowPos = pos;
    }
    
    public Dimension getAppWindowSize() {
        return _appWindowSize;
    }

    public void setAppWindowSize(Dimension size) {
        _appWindowSize = size;
    }

    public int getTreeWidth() {
        return _treeWidth;
    }
    
    public void setTreeWidth(int width) {
        _treeWidth = width;
    }

    /**
     * Read a position string from preferences
     */
    public static Point getPosition(Preferences prefs, String keyName, Point defaultPos) {
        String position = prefs.get(
                keyName,
                defaultPos.x + "," + defaultPos.y);
        try {
            int i = position.indexOf(',');
            int x = Integer.parseInt(position.substring(0, i));
            int y = Integer.parseInt(position.substring(i + 1));
            return new Point(x, y);
        } catch(Exception e) {
            return defaultPos;
        }
    }

    public static void putPosition(Preferences prefs, String keyName, Point pos) {
        prefs.put(keyName, pos.x + "," + pos.y);
    }

    /**
     * Read a size string from preferences
     */
    public static Dimension getSize(Preferences prefs, String keyName, Dimension defaultSize) {
        String size = prefs.get(
                keyName,
                defaultSize.width + "x" + defaultSize.height);
        try {
            int i = size.indexOf('x');
            int w = Integer.parseInt(size.substring(0, i));
            int h = Integer.parseInt(size.substring(i + 1));
            return new Dimension(w, h);
        } catch(Exception e) {
            return defaultSize;
        }
    }

    public static void putSize(Preferences prefs, String keyName, Dimension size) {
        prefs.put(keyName, size.width + "x" + size.height);
    }
}
