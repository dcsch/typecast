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

import java.awt.*;
import java.awt.desktop.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import java.io.StreamTokenizer;
import java.io.StringReader;

import java.util.ResourceBundle;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;

import net.java.dev.typecast.ot.OTFontCollection;

/**
 * The application menu bar
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class EditorMenu {

    private Main _app;
    private ResourceBundle _rb;
    private EditorPrefs _prefs;
    private OTFontCollection _selectedFontCollection;
    private JMenuItem _closeMenuItem;
    private String _closeMenuString;
    private JCheckBoxMenuItem _previewMenuItem;
    private JCheckBoxMenuItem _showPointsMenuItem;
    private JCheckBoxMenuItem _showHintsMenuItem;
    private boolean _macPlatform;
    private int _primaryKeystrokeMask;
    private JMenu _mruMenu;
    
    /** Creates a new instance of EditorMenu */
    public EditorMenu(Main app, ResourceBundle rb, EditorPrefs prefs) {
        _app = app;
        _rb = rb;
        _prefs = prefs;
        _primaryKeystrokeMask =
                Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
        if (System.getProperty("os.name").equals("Mac OS X")) {
            _macPlatform = true;
            registerForMacOSXEvents();
        } else {
            _macPlatform = false;
        }
    }
    
    public OTFontCollection getSelectedFontCollection() {
        return _selectedFontCollection;
    }
    
    public void setSelectedFontCollection(OTFontCollection fc) {
        _selectedFontCollection = fc;
        if (_selectedFontCollection != null) {
            _closeMenuItem.setText(
                    _closeMenuString +
                    " \"" +
                    _selectedFontCollection.getFileName() +
                    "\"");
            _closeMenuItem.setEnabled(true);
        } else {
            _closeMenuItem.setText(_closeMenuString);
            _closeMenuItem.setEnabled(false);
        }
    }
    
    public boolean isPreview() {
        return _previewMenuItem.getState();
    }
    
    public boolean isShowPoints() {
        return _showPointsMenuItem.getState();
    }
    
    public boolean isShowHints() {
        return _showHintsMenuItem.getState();
    }
    
    private static void parseMenuString(String menuString, String[] tokens) {
        try {
            StreamTokenizer st = new StreamTokenizer(new StringReader(menuString));
            st.nextToken();
            if (st.sval != null) {
                tokens[0] = st.sval;
            }
            st.nextToken();
            if (st.sval != null) {
                tokens[1] = st.sval;
            }
            st.nextToken();
            if (st.sval != null) {
                tokens[2] = st.sval;
            }
        } catch (Exception e) {
        }
    }

    private static JMenuItem createMenuItem(
            Class<? extends JMenuItem> menuClass,
            String name,
            String mnemonic,
            String description,
            KeyStroke accelerator,
            boolean enabled,
            ActionListener al) {
        JMenuItem menuItem = null;
        try {
            menuItem = menuClass.newInstance();
            menuItem.setText(name);
            menuItem.setToolTipText(description);
            menuItem.setMnemonic(mnemonic.length() > 0 ? mnemonic.charAt(0) : 0);
            menuItem.getAccessibleContext().setAccessibleDescription(description);
            menuItem.setEnabled(enabled);
            if (accelerator != null) {
                menuItem.setAccelerator(accelerator);
            }
            if (al != null) {
                menuItem.addActionListener(al);
            }
        } catch (Exception e) {
        }
        return menuItem;
    }

    private static JMenuItem createMenuItem(
            String menuText,
            KeyStroke accelerator,
            boolean enabled,
            ActionListener al) {
        String[] tokens = new String[3];
        parseMenuString(menuText, tokens);
        return createMenuItem(
                JMenuItem.class,
                tokens[0],
                tokens[1],
                tokens[2],
                accelerator,
                enabled,
                al);
    }

    private static JCheckBoxMenuItem createCheckBoxMenuItem(
            String menuText,
            KeyStroke accelerator,
            ActionListener al) {
        String[] tokens = new String[3];
        parseMenuString(menuText, tokens);
        return (JCheckBoxMenuItem) createMenuItem(
                JCheckBoxMenuItem.class,
                tokens[0],
                tokens[1],
                tokens[2],
                accelerator,
                true,
                al);
    }

    private static JMenu createMenu(String menuText) {
        String[] tokens = new String[3];
        parseMenuString(menuText, tokens);
        return (JMenu) createMenuItem(JMenu.class, tokens[0], tokens[1], tokens[2], null, true, null);
    }

    public JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createViewMenu());
        //menuBar.add(createElementMenu());
        //menuBar.add(createPointsMenu());
        //menuBar.add(createMetricsMenu());
        if (_macPlatform) {
            menuBar.add(createWindowMenu());
        }
        menuBar.add(createHelpMenu());
        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu menu = createMenu(_rb.getString("Typecast.menu.file"));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.file.new"),
                KeyStroke.getKeyStroke(KeyEvent.VK_N, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.file.open"),
                KeyStroke.getKeyStroke(KeyEvent.VK_O, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _app.openFont();
                    }
                }));
        _mruMenu = createMenu(_rb.getString("Typecast.menu.file.openRecent"));
        menu.add(_mruMenu);

        // Generate a MRU list
        buildMRU();

        menu.add(new JSeparator());
        menu.add(_closeMenuItem = createMenuItem(
                _rb.getString("Typecast.menu.file.close"),
                null,
                false,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _app.closeFont();
                    }
                }));
        _closeMenuString = _closeMenuItem.getText();
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.file.save"),
                KeyStroke.getKeyStroke(KeyEvent.VK_S, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.file.saveAs"),
                KeyStroke.getKeyStroke(
                    KeyEvent.VK_S,
                    _primaryKeystrokeMask | KeyEvent.SHIFT_MASK),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.file.saveAll"),
                null,
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.file.revertToSaved"),
                null,
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.file.export"),
                null,
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _app.exportFont();
                    }
                }));
        if (!_macPlatform) {
            menu.add(new JSeparator());
            menu.add(createMenuItem(
                    _rb.getString("Typecast.menu.file.preferences"),
                    null,
                    true,
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                        }
                    }));
        }

        // Only add "Exit" to the menu if this isn't a Mac
        if (!_macPlatform) {
            menu.add(new JSeparator());
            menu.add(createMenuItem(
                    _rb.getString("Typecast.menu.file.exit"),
                    null,
                    true,
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            _app.close();
                        }
                    }));
        }
        return menu;
    }

    private JMenu createEditMenu() {
        JMenu menu = createMenu(_rb.getString("Typecast.menu.edit"));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.edit.undo"),
                KeyStroke.getKeyStroke(KeyEvent.VK_Z, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.edit.redo"),
                KeyStroke.getKeyStroke(KeyEvent.VK_Y, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        menu.add(new JSeparator());
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.edit.cut"),
                KeyStroke.getKeyStroke(KeyEvent.VK_X, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.edit.copy"),
                KeyStroke.getKeyStroke(KeyEvent.VK_C, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.edit.paste"),
                KeyStroke.getKeyStroke(KeyEvent.VK_V, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.edit.clear"),
                KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        //menu.add(createMenuItem(
        //    _rb.getString("Typecast.menu.edit.copyWidths"),
        //    null,
        //    new ActionListener() {
        //        public void actionPerformed(ActionEvent e) {
        //        }
        //    }));
        //menu.add(createMenuItem(
        //    _rb.getString("Typecast.menu.edit.copyReference"),
        //    null,
        //    new ActionListener() {
        //        public void actionPerformed(ActionEvent e) {
        //        }
        //    }));
        //menu.add(createMenuItem(
        //    _rb.getString("Typecast.menu.edit.unlinkReference"),
        //    null,
        //    new ActionListener() {
        //        public void actionPerformed(ActionEvent e) {
        //        }
        //    }));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.edit.selectAll"),
                KeyStroke.getKeyStroke(KeyEvent.VK_A, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        //menu.add(createMenuItem(
        //    _rb.getString("Typecast.menu.edit.duplicate"),
        //    KeyStroke.getKeyStroke(KeyEvent.VK_D, _primaryKeystrokeMask),
        //    new ActionListener() {
        //        public void actionPerformed(ActionEvent e) {
        //        }
        //    }));
        //menu.add(createMenuItem(
        //    _rb.getString("Typecast.menu.edit.clone"),
        //    null,
        //    new ActionListener() {
        //        public void actionPerformed(ActionEvent e) {
        //        }
        //    }));
        return menu;
    }
    
    private JMenu createViewMenu() {
        JMenu menu = createMenu(_rb.getString("Typecast.menu.view"));
        menu.add(_previewMenuItem = createCheckBoxMenuItem(
                _rb.getString("Typecast.menu.view.preview"),
                KeyStroke.getKeyStroke(KeyEvent.VK_L, _primaryKeystrokeMask),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _app.changeGlyphView();
                    }
                }));
        menu.add(_showPointsMenuItem = createCheckBoxMenuItem(
                _rb.getString("Typecast.menu.view.showPoints"),
                KeyStroke.getKeyStroke(KeyEvent.VK_P, _primaryKeystrokeMask),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _app.changeGlyphView();
                    }
                }));
        menu.add(_showHintsMenuItem = createCheckBoxMenuItem(
                _rb.getString("Typecast.menu.view.showHints"),
                KeyStroke.getKeyStroke(KeyEvent.VK_H, _primaryKeystrokeMask),
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _app.changeGlyphView();
                    }
                }));
        _showPointsMenuItem.setState(true);
        JMenu subMenu = createMenu(_rb.getString("Typecast.menu.view.magnification"));
        menu.add(subMenu);
        subMenu.add(createMenuItem(
                _rb.getString("Typecast.menu.view.magnification.fitInWindow"),
                KeyStroke.getKeyStroke(KeyEvent.VK_T, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        subMenu.add(new JSeparator());
        subMenu.add(createMenuItem(
                _rb.getString("Typecast.menu.view.magnification.00625"),
                KeyStroke.getKeyStroke(KeyEvent.VK_1, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        subMenu.add(createMenuItem(
                _rb.getString("Typecast.menu.view.magnification.01250"),
                KeyStroke.getKeyStroke(KeyEvent.VK_2, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        subMenu.add(createMenuItem(
                _rb.getString("Typecast.menu.view.magnification.02500"),
                KeyStroke.getKeyStroke(KeyEvent.VK_3, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        subMenu.add(createMenuItem(
                _rb.getString("Typecast.menu.view.magnification.05000"),
                KeyStroke.getKeyStroke(KeyEvent.VK_4, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        subMenu.add(createMenuItem(
                _rb.getString("Typecast.menu.view.magnification.10000"),
                KeyStroke.getKeyStroke(KeyEvent.VK_5, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        subMenu.add(createMenuItem(
                _rb.getString("Typecast.menu.view.magnification.20000"),
                KeyStroke.getKeyStroke(KeyEvent.VK_6, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                    }
                }));
        return menu;
    }
    
    private JMenu createElementMenu() {
        JMenu menu = createMenu(_rb.getString("Typecast.menu.element"));

        JMenuItem menuItem = menu.add(new JMenuItem("New"));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }
        );
        return menu;
    }
    
    private JMenu createPointsMenu() {
        JMenu menu = createMenu(_rb.getString("Typecast.menu.points"));

        JMenuItem menuItem = menu.add(new JMenuItem("New"));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }
        );
        return menu;
    }
    
    private JMenu createMetricsMenu() {
        JMenu menu = createMenu(_rb.getString("Typecast.menu.metrics"));

        JMenuItem menuItem = menu.add(new JMenuItem("New"));
        menuItem.addActionListener(
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }
        );
        return menu;
    }
    
    private JMenu createWindowMenu() {
        JMenu menu = createMenu(_rb.getString("Typecast.menu.window"));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.help.about"),
                null,
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _app.showAbout();
                    }
                }));
        return menu;
    }

    private JMenu createHelpMenu() {
        JMenu menu = createMenu(_rb.getString("Typecast.menu.help"));
        menu.add(createMenuItem(
                _rb.getString("Typecast.menu.help.typecast"),
                KeyStroke.getKeyStroke(KeyEvent.VK_SLASH, _primaryKeystrokeMask),
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _app.showHelp();
                    }
                }));
        if (!_macPlatform) {
            menu.add(new JSeparator());
            menu.add(createMenuItem(
                    _rb.getString("Typecast.menu.help.about"),
                    null,
                    true,
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            _app.showAbout();
                        }
                    }));
        }
        return menu;
    }
    
    private void buildMRU() {
        _mruMenu.removeAll();
        for (int i = 0; i < _prefs.getMRUCount(); ++i) {
            String mru = _prefs.getMRU(i);
            if (mru != null) {
                JMenuItem menuItem = _mruMenu.add(new JMenuItem(
                    mru,
                    KeyEvent.VK_0 + i));
                menuItem.getAccessibleContext().setAccessibleDescription(
                    "Recently used font");
                menuItem.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            _app.loadFont(e.getActionCommand());
                        }
                    }
                );
            }
        }
        if (_prefs.getMRUCount() == 0) {
            
            // Add a placeholder
            JMenuItem menuItem = _mruMenu.add(new JMenuItem("Recently used files"));
            menuItem.setEnabled(false);
        }
        _mruMenu.add(new JSeparator());
        _mruMenu.add(createMenuItem(
                _rb.getString("Typecast.menu.file.openRecent.clearMenu"),
                null,
                true,
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        _prefs.clearMRU();
                        buildMRU();
                    }
                }));
    }

    public void addMru(String mru) {
        _prefs.addMRU(mru);
        buildMRU();
    }

    // Generic registration with the macOS application menu
    public void registerForMacOSXEvents() {
        Desktop desktop = Desktop.getDesktop();
        desktop.setQuitHandler(new QuitHandler() {
            @Override
            public void handleQuitRequestWith(QuitEvent e, QuitResponse response) {
                _app.close();
            }
        });
        desktop.setAboutHandler(new AboutHandler() {
            @Override
            public void handleAbout(AboutEvent e) {
                _app.showAbout();
            }
        });
        desktop.setPreferencesHandler(new PreferencesHandler() {
            @Override
            public void handlePreferences(PreferencesEvent e) {
                _app.showPreferences();
            }
        });
    }
}
