/*
 * $Id: Main.java,v 1.2 2004-12-21 10:25:55 davidsch Exp $
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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StreamTokenizer;

import java.net.URL;

import java.util.ArrayList;
import java.util.prefs.Preferences;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTree;
import javax.swing.ToolTipManager;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import javax.swing.table.AbstractTableModel;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import net.java.dev.typecast.edit.CharacterMap;

import net.java.dev.typecast.ot.table.DirectoryEntry;
import net.java.dev.typecast.ot.table.GlyfDescript;
import net.java.dev.typecast.ot.table.Table;

import net.java.dev.typecast.ot.OTFont;
import net.java.dev.typecast.ot.OTFontCollection;

import net.java.dev.typecast.exchange.Exporter;
import net.java.dev.typecast.exchange.SVGExporter;

/**
 * @author <a href="mailto:davidsch@dev.java.net">David Schweinsberg</a>
 * @version $Id: Main.java,v 1.2 2004-12-21 10:25:55 davidsch Exp $
 */
public class Main {

    private EditorMenu _menu;
    private JFrame _frame;
    private JTree _tree;
    private JSplitPane _splitPane;
    private DefaultTreeModel _treeModel;
    private JTextArea _dumpTextArea;
    private GlyphPanel _glyphPanel;
    private ArrayList<OTFontCollection> _fontCollections =
            new ArrayList<OTFontCollection>();
    private Properties _properties = new Properties();
    private EditorPrefs _appPrefs = new EditorPrefs();
    private JTabbedPane _tabbedPane;
    private JComponent _glyphPane;
    private JComponent _textPane;
    private JComponent _propertyPane;
    private Object _treeSelection;
    private ResourceBundle _rb;
    private OTFont _selectedFont = null;
    private TableTreeNode _selectedCollectionNode;
    
    /**
     * Typecast constructor.
     */
    public Main() {

        // Show a splash screen whilst we load up
        Splash splash = new Splash();
        splash.setVisible(true);

        try {
            // Load the user's application preferences
            _appPrefs.load(Preferences.userNodeForPackage(getClass()));

            // This is the old preferences store - to be removed
            try {
                _properties.load(new FileInputStream(
                    System.getProperty("user.home") +
                    System.getProperty("file.separator") +
                    "typecast.properties"));
            } catch (IOException e) {
            }

            // Load the resource bundle
            _rb = ResourceBundle.getBundle("net/java/dev/typecast/apps/editor/Main");

            _frame = new JFrame(
                    _rb.getString("Typecast.title") +
                    " " +
                    _rb.getString("Typecast.version"));
            _frame.setLocation(_appPrefs.getAppWindowPos());
            _frame.setPreferredSize(_appPrefs.getAppWindowSize());

            _treeModel = (DefaultTreeModel) TableTreeBuilder.createTypecastTreeModel();
            _tree = new JTree(_treeModel);
            _tree.setRootVisible(false);
            _tree.setShowsRootHandles(true);

            // Enable tool tips for the tree, without this tool tips will not
            // be picked up
            ToolTipManager.sharedInstance().registerComponent(_tree);

            // Make the tree use an instance of TableTreeCellRenderer for
            // drawing
            _tree.setCellRenderer(new TableTreeCellRenderer());

            // Put the Tree in a scroller
            JScrollPane treePane = new JScrollPane();
            treePane.getViewport().add(_tree);

            // Listen for selection events from the tree
            TreeSelectionListener tsl = new TreeSelectionListener() {
                public void valueChanged(TreeSelectionEvent e) {
                    TreePath selPath = e.getPath();
                    if(selPath != null) {

                        // Pick the font collection out of the path
                        if (selPath.getPathCount() >= 2) {
                            _selectedCollectionNode =
                                    (TableTreeNode) selPath.getPathComponent(1);
                            _menu.setSelectedFontCollection(
                                    (OTFontCollection)
                                    _selectedCollectionNode.getUserObject());
                        }

                        // Pick the selected font out of the path
                        OTFont font = null;
                        if (selPath.getPathCount() >= 3) {
                            TableTreeNode fontNode =
                                    (TableTreeNode) selPath.getPathComponent(2);
                            font = (OTFont) fontNode.getUserObject();
                        }

                        // Now get the actually selected node
                        TableTreeNode tn =
                                (TableTreeNode) selPath.getLastPathComponent();
                        selectElement(font, tn);
                    }
                }
            };
            _tree.addTreeSelectionListener(tsl);

            // Create a tabbed workspace
            _tabbedPane = new JTabbedPane();
            _glyphPane = createGlyphEditPane();
            _textPane = createTextPane();
            _propertyPane = createPropertyTable();
            configTabbedPane(null);

            // Split the main frame
            _splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                treePane,
                _tabbedPane);
            _splitPane.setOneTouchExpandable(true);
            _splitPane.setDividerLocation(_appPrefs.getTreeWidth());
            _frame.getContentPane().add("Center", _splitPane);

            // Create a menu bar
            _menu = new EditorMenu(this, _rb, _properties);
            _frame.setJMenuBar(_menu.createMenuBar());

            _frame.addWindowListener(
                new WindowAdapter() {
                    public void windowClosing(WindowEvent e) {
                        close();
                    }
                }
            );

            // We're built, so make the main frame visible and hide the splash
            _frame.pack();
            _frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                e.toString(),
                "Exception",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            splash.setVisible(false);
        }
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

            // Glyph outlines get the glyph pane
            if (selection instanceof GlyfDescript) {
                _tabbedPane.addTab("Outline", _glyphPane);
            }
            
            // Character maps
            if (selection instanceof net.java.dev.typecast.ot.table.CmapFormat) {
                _tabbedPane.addTab("Character Map", new CharacterMap(_selectedFont, (net.java.dev.typecast.ot.table.CmapFormat) selection));
            }
            
            // All selections get a "dump" pane
            if (selection != null) {
                _tabbedPane.add("Dump", _textPane);
//                tabbedPane.addTab("Properties", null, propertyPane, "Does little");
//                tabbedPane.setSelectedIndex(0);
            }
        }
        _treeSelection = selection;
    }
    
    protected void loadFont(String pathName) {
        try {
            File file = new File(pathName);
            OTFontCollection fc = OTFontCollection.create(file);
            _fontCollections.add(fc);

            // Create the tree to put the information in
            TableTreeBuilder.addFontTree(_treeModel, fc);
        }
        catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(
                null,
                e.toString(),
                "I/O Exception",
                JOptionPane.ERROR_MESSAGE);
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
        new Main();
    }

    /**
     * Display a file chooser and open the selected font file
     */
    protected void openFont() {
        JFileChooser chooser = new JFileChooser();

        EditorFileFilter filter = new EditorFileFilter();
        filter.addExtension("ttf");
        filter.addExtension("ttc");
        filter.addExtension("otf");
        filter.addExtension("dfont");
        filter.setDescription("OpenType Fonts");

        chooser.setFileFilter(filter);

        if (chooser.showOpenDialog(_frame) == JFileChooser.APPROVE_OPTION) {
            loadFont(chooser.getSelectedFile().getPath());
            _menu.addMru(chooser.getSelectedFile().getPath());
        }
    }

    /**
     * Close the currently selected font
     */
    protected void closeFont() {
        _fontCollections.remove(
                (OTFontCollection) _selectedCollectionNode.getUserObject());
        _treeModel.removeNodeFromParent(_selectedCollectionNode);
        configTabbedPane(null);
        _menu.setSelectedFontCollection(null);
    }

    /**
     * At this time the only format we export to is SVG
     */
    protected void exportFont() {
        if (_selectedFont != null) {
            JFileChooser chooser = new JFileChooser();

            EditorFileFilter filter = new EditorFileFilter();
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

    protected void showAbout() {
        JOptionPane.showMessageDialog(
            null,
            _rb.getString("Typecast.title") +
                " " +
                _rb.getString("Typecast.version") +
                " - " +
                _rb.getString("Typecast.shortDesc") +
                "\n" +
                _rb.getString("Typecast.copyright") +
                "\n" +
                _rb.getString("Typecast.webHome"),
            _rb.getString("Typecast.about.title"),
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    protected void close() {
        
        // Save the user's application preferences
        _appPrefs.setAppWindowPos(_frame.getLocation());
        _appPrefs.setAppWindowSize(_frame.getSize());
        _appPrefs.setTreeWidth(_splitPane.getDividerLocation());
        _appPrefs.save(Preferences.userNodeForPackage(getClass()));

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
    
    protected void changeGlyphView() {
        _glyphPanel.getGlyphEdit().setPreview(_menu.isPreview());
        _glyphPanel.getGlyphEdit().setDrawControlPoints(_menu.isShowPoints());
        _glyphPanel.getGlyphEdit().repaint();
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
                _glyphPanel.getGlyphEdit().setFont(font);
                _glyphPanel.getGlyphEdit().setGlyph(font.getGlyph(tn.getIndex()));
            }
        }
        configTabbedPane(obj);
    }
}
