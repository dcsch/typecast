/*
 * $Id: Typecast.java,v 1.1.1.1 2004-12-05 23:14:17 davidsch Exp $
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

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;
import java.util.ResourceBundle;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import javax.swing.table.*;
import net.java.dev.typecast.ot.table.DirectoryEntry;
import net.java.dev.typecast.ot.table.GlyfDescript;
import net.java.dev.typecast.ot.table.Table;
import net.java.dev.typecast.ot.OTFont;
import net.java.dev.typecast.ot.OTFontCollection;
import net.java.dev.typecast.exchange.Exporter;
import net.java.dev.typecast.exchange.SVGExporter;

/**
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: Typecast.java,v 1.1.1.1 2004-12-05 23:14:17 davidsch Exp $
 */
public class Typecast {

    private JMenuBar _menuBar = null;
    private JFrame _frame;
    private JTree _tree;
    private DefaultTreeModel _treeModel;
    private JTextArea _dumpTextArea;
    private GlyphPanel _glyphPanel;
    private ArrayList<OTFontCollection> _fontCollections = new ArrayList<OTFontCollection>();
    private Properties _properties = new Properties();
    private JTabbedPane _tabbedPane;
    private JComponent _glyphPane;
    private JComponent _textPane;
    private JComponent _propertyPane;
    private Object _treeSelection;
    private ResourceBundle _rb;
    private OTFont _selectedFont = null;
    
    /**
     * FontGeek constructor comment.
     * @param title java.lang.String
     */
    public Typecast(String title) {

        Splash splash = new Splash();
        splash.setVisible(true);

        try {
            _properties.load(new FileInputStream(
                System.getProperty("user.home") +
                System.getProperty("file.separator") +
                "typecast.properties"));
        } catch (IOException e) {
        }

        // Load the resource bundle
        _rb = ResourceBundle.getBundle("net/java/dev/typecast/apps/Typecast");

        _frame = new JFrame(_rb.getString("Typecast.title"));

        _treeModel = (DefaultTreeModel) TableTreeBuilder.createTypecastTreeModel();
        _tree = new JTree(_treeModel);
//        _tree = new JTree((TreeModel)null);
        _tree.setRootVisible(false);
        _tree.setShowsRootHandles(true);

        // Enable tool tips for the tree, without this tool tips will not
        // be picked up
        ToolTipManager.sharedInstance().registerComponent(_tree);

        // Make the tree use an instance of DOMBrowserCellRenderer for
        // drawing
        _tree.setCellRenderer(new TableTreeCellRenderer());

        // Put the Tree in a scroller
        JScrollPane treePane = new JScrollPane();
        treePane.setPreferredSize(new Dimension(500, 500));
        treePane.getViewport().add(_tree);

        // Listen for selection events from the tree
        TreeSelectionListener tsl = new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                TreePath selPath = e.getPath();
                if(selPath != null) {
                    net.java.dev.typecast.ot.OTFont font = null;
                    if (selPath.getPathCount() >= 3) {
                        TableTreeNode fontNode = (TableTreeNode) selPath.getPathComponent(2);
                        font = (net.java.dev.typecast.ot.OTFont) fontNode.getUserObject();
                    }
                    TableTreeNode tn = (TableTreeNode) selPath.getLastPathComponent();
                    selectElement(font, tn);
                }
            }
        };
        _tree.addTreeSelectionListener(tsl);

        _tabbedPane = new JTabbedPane();
        _glyphPane = createGlyphEditPane();
        _textPane = createTextPane();
        _propertyPane = createPropertyTable();
        configTabbedPane(null);

        JSplitPane splitPane = new JSplitPane(
            JSplitPane.HORIZONTAL_SPLIT,
            treePane,
            _tabbedPane);
        splitPane.setOneTouchExpandable(true);
        splitPane.setDividerLocation(200);

        _frame.getContentPane().add("Center", splitPane);
        _frame.setJMenuBar(createMenuBar());

        _frame.addWindowListener(
            new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    close();
                }
            }
        );

        _frame.pack();
        _frame.setVisible(true);
        
        splash.setVisible(false);
    }

    private void parseMenuString(String menuString, String[] tokens) {
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

    private JMenuItem createMenuItem(
        Class menuClass,
        String name,
        String mnemonic,
        String description,
        KeyStroke accelerator,
        ActionListener al) {

        JMenuItem menuItem = null;
        try {
            menuItem = (JMenuItem) menuClass.newInstance();
            menuItem.setText(name);
            menuItem.setToolTipText(description);
            menuItem.setMnemonic(mnemonic.length() > 0 ? mnemonic.charAt(0) : 0);
            menuItem.getAccessibleContext().setAccessibleDescription(description);
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

    private JMenuItem createMenuItem(
        String menuText,
        KeyStroke accelerator,
        ActionListener al) {

        String[] tokens = new String[3];
        parseMenuString(menuText, tokens);
        return createMenuItem(JMenuItem.class, tokens[0], tokens[1], tokens[2], accelerator, al);
    }

    private JMenu createMenu(String menuText) {
        String[] tokens = new String[3];
        parseMenuString(menuText, tokens);
        return (JMenu) createMenuItem(JMenu.class, tokens[0], tokens[1], tokens[2], null, null);
    }

    private JMenuBar createMenuBar() {
        JMenu menu;
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(createFileMenu());
        menuBar.add(createEditMenu());
        menuBar.add(createViewMenu());
        //menuBar.add(createElementMenu());
        //menuBar.add(createPointsMenu());
        //menuBar.add(createMetricsMenu());
        menuBar.add(createHelpMenu());
        return menuBar;
    }

    private JMenu createFileMenu() {
        JMenu menu = createMenu(_rb.getString("Typecast.menu.file"));
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.file.new"),
            KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        menu.add(new JSeparator());
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.file.open"),
            KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    openFont();
                }
            }));
        menu.add(new JSeparator());
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.file.save"),
            KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.file.saveAs"),
            null,
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.file.export"),
            null,
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    exportFont();
                }
            }));
        menu.add(new JSeparator());
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.file.preferences"),
            null,
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        menu.add(new JSeparator());

        // Generate a MRU list
        boolean foundMru = false;
        for (int i = 0; i < 4; i++) {
            String mru = _properties.getProperty("MRU" + i);
            if (mru != null) {
                foundMru = true;
                JMenuItem menuItem = menu.add(new JMenuItem(
//                    String.valueOf(i) + " " + mru,
                    mru,
                    KeyEvent.VK_0 + i));
                menuItem.getAccessibleContext().setAccessibleDescription(
                    "Recently used font");
                menuItem.addActionListener(
                    new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            loadFont(e.getActionCommand());
                        }
                    }
                );
            }
        }
        if (!foundMru) {
            
            // Add a placeholder
            JMenuItem menuItem = menu.add(new JMenuItem("Recently used files"));
            menuItem.setEnabled(false);
        }

        menu.add(new JSeparator());

        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.file.exit"),
            null,
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    close();
                }
            }));

        return menu;
    }

    private JMenu createEditMenu() {
        JMenu menu = createMenu(_rb.getString("Typecast.menu.edit"));
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.edit.undo"),
            KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.edit.redo"),
            KeyStroke.getKeyStroke(KeyEvent.VK_Y, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        menu.add(new JSeparator());
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.edit.cut"),
            KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.edit.copy"),
            KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.edit.paste"),
            KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.edit.clear"),
            KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0),
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
            KeyStroke.getKeyStroke(KeyEvent.VK_A, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        //menu.add(createMenuItem(
        //    _rb.getString("Typecast.menu.edit.duplicate"),
        //    KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK),
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
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.view.preview"),
            KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.view.showPoints"),
            KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        JMenu subMenu = createMenu(_rb.getString("Typecast.menu.view.magnification"));
        menu.add(subMenu);
        subMenu.add(createMenuItem(
            _rb.getString("Typecast.menu.view.magnification.fitInWindow"),
            KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        subMenu.add(new JSeparator());
        subMenu.add(createMenuItem(
            _rb.getString("Typecast.menu.view.magnification.00625"),
            KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        subMenu.add(createMenuItem(
            _rb.getString("Typecast.menu.view.magnification.01250"),
            KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        subMenu.add(createMenuItem(
            _rb.getString("Typecast.menu.view.magnification.02500"),
            KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        subMenu.add(createMenuItem(
            _rb.getString("Typecast.menu.view.magnification.05000"),
            KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        subMenu.add(createMenuItem(
            _rb.getString("Typecast.menu.view.magnification.10000"),
            KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.CTRL_MASK),
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                }
            }));
        subMenu.add(createMenuItem(
            _rb.getString("Typecast.menu.view.magnification.20000"),
            KeyStroke.getKeyStroke(KeyEvent.VK_6, ActionEvent.CTRL_MASK),
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
    
    private JMenu createHelpMenu() {
        JMenu menu = createMenu(_rb.getString("Typecast.menu.help"));
        menu.add(createMenuItem(
            _rb.getString("Typecast.menu.help.about"),
            null,
            new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showAbout();
                }
            }));
        return menu;
    }
    
    private JComponent createTextPane() {
        _dumpTextArea = new JTextArea();
        _dumpTextArea.setEditable(false);
        _dumpTextArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        return new JScrollPane(_dumpTextArea);
    }
    
    private JTable createPropertyTable() {
        AbstractTableModel tableModel = new AbstractTableModel() {
            private static final long serialVersionUID = 1L;

            Object[][] rowData = {
                {"Mary", "Campione",
                "Snowboarding", new Integer(5), new Boolean(false)},
                {"Alison", "Huml",
                "Rowing", new Integer(3), new Boolean(true)},
                {"Kathy", "Walrath",
                "Chasing toddlers", new Integer(2), new Boolean(false)},
                {"Mark", "Andrews",
                "Speed reading", new Integer(20), new Boolean(true)},
                {"Angela", "Lih",
                "Teaching high school", new Integer(4), new Boolean(false)}
            };
            String[] columnNames = {"First Name",
                "Last Name",
                "Sport",
                "# of Years",
            "Vegetarian"};
        public String getColumnName(int col) { return columnNames[col].toString(); }
        public int getRowCount() { return rowData.length; }
        public int getColumnCount() { return columnNames.length; }
        public Object getValueAt(int row, int col) { return rowData[row][col]; }
        public boolean isCellEditable(int row, int col) { return false; }
        };

        return new JTable(tableModel);
    }

    private JComponent createGlyphEditPane() {
        _glyphPanel = new GlyphPanel(_properties);
        return _glyphPanel;
    }

    private void configTabbedPane(Object selection) {
        if (selection == null || (selection == null && _treeSelection == null)) {
            _tabbedPane.removeAll();
        } else if (_treeSelection == null || _treeSelection.getClass() != selection.getClass()) {
            _tabbedPane.removeAll();
            if (selection instanceof GlyfDescript) {
                _tabbedPane.addTab("Countours", _glyphPane);
            }
            if (selection != null) {
                _tabbedPane.add("Dump", _textPane);
//                tabbedPane.addTab("Properties", null, propertyPane, "Does little");
//                tabbedPane.setSelectedIndex(0);
            }
        }
        _treeSelection = selection;
    }
    
    private void loadFont(String pathName) {
        try {
            OTFontCollection fc = OTFontCollection.create(pathName);
            _fontCollections.add(fc);

            // Create the tree to put the information in
            TableTreeBuilder.addFontTree(_treeModel, fc);
        }
        catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                e.toString(),
                "Exception",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        new Typecast("Typecast");
    }

    private void addMru(String mru) {
        String oldMru;
        for (int i = 0; i < 4; i++) {
            oldMru = _properties.getProperty("MRU" + i);
            if (mru != null) {
                _properties.setProperty("MRU" + i, mru);
                mru = oldMru;
            } else {
                break;
            }
        }
    }
    
    private void openFont() {
        JFileChooser chooser = new JFileChooser();

        ExampleFileFilter filter = new ExampleFileFilter();
        filter.addExtension("ttf");
        filter.addExtension("ttc");
        filter.addExtension("otf");
        filter.addExtension("dfont");
        filter.setDescription("OpenType Fonts");

        chooser.setFileFilter(filter);

        if (chooser.showOpenDialog(_frame) == JFileChooser.APPROVE_OPTION) {
            loadFont(chooser.getSelectedFile().getPath());
            addMru(chooser.getSelectedFile().getPath());
        }
    }

    /**
     * At this time the only format we export to is SVG
     */
    private void exportFont() {
        if (_selectedFont != null) {
            JFileChooser chooser = new JFileChooser();

            ExampleFileFilter filter = new ExampleFileFilter();
            filter.addExtension("svg");
            filter.setDescription("Scalable Vector Graphics");

            chooser.setFileFilter(filter);

            if (chooser.showSaveDialog(_frame) == JFileChooser.APPROVE_OPTION) {
                try {
                    FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile().getPath());
                    Exporter exporter = new SVGExporter(_selectedFont);
                    exporter.export(fos);
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(
                        null,
                        e.toString(),
                        "Exception",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void showAbout() {
        JOptionPane.showMessageDialog(
            null,
            _rb.getString("Typecast.title") + " - " + _rb.getString("Typecast.shortDesc") + "\n" +
            _rb.getString("Typecast.copyright") + "\n" +
            _rb.getString("Typecast.webHome"),
            _rb.getString("Typecast.about.title"),
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void close() {
        
        // Save properties
        try {
            _glyphPanel.setProperties();

            _properties.store(new FileOutputStream(
                System.getProperty("user.home") +
                System.getProperty("file.separator") +
                "typecast.properties"), "Heading");
        } catch (IOException e) {
        }

        // End the application
        System.exit(0);
    }
    
    private void selectElement(OTFont font, TableTreeNode tn) {
        
        // Note that this font is currently selected
        _selectedFont = font;

        Object obj = tn.getUserObject();
        if (obj != null) {

            // TODO: Remove this hack
            // To overcome a problem with the tabbed pane, we're creating new
            // instances of the various panes each time.
            // This is a major pain in the ass.
            if (_treeSelection == null || _treeSelection.getClass() != obj.getClass()) {
                _textPane = createTextPane();
                if (obj instanceof GlyfDescript) {
                    _glyphPane = createGlyphEditPane();
                }
            }
            // End of hack
            
            _dumpTextArea.setText(obj.toString());

            if (obj instanceof GlyfDescript) {
                _glyphPanel.setFont(font);
                _glyphPanel.setGlyph(font.getGlyph(tn.getIndex()));
            }
        }
        configTabbedPane(obj);
    }
}
