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

package net.java.dev.typecast.app.editor;


import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.HeadlessException;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.ToolTipManager;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import net.java.dev.typecast.edit.CharacterMap;
import net.java.dev.typecast.exchange.Exporter;
import net.java.dev.typecast.exchange.SVGExporter;
import net.java.dev.typecast.ot.OTFont;
import net.java.dev.typecast.ot.OTFontCollection;
import net.java.dev.typecast.ot.table.GlyphDescription;
import net.java.dev.typecast.ot.table.TableException;

/**
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class Main {

    private EditorMenu _menu;
    private JFrame _frame;
    private JTree _tree;
    private JSplitPane _splitPane;
    private DefaultTreeModel _treeModel;
    private ArrayList<OTFontCollection> _fontCollections = new ArrayList<>();
    private EditorPrefs _appPrefs = new EditorPrefs();
    private JTabbedPane _tabbedPane;
    private GlyphPanel _glyphPane;
    private Object _treeSelection;
    private ResourceBundle _rb;
    private OTFont _selectedFont = null;
    private TableTreeNode _selectedCollectionNode;
    
    /**
     * Typecast constructor.
     */
    public Main() {

        // Before loading Swing, set macOS-specific properties
        System.setProperty("apple.awt.application.name", "Typecast");
        System.setProperty("apple.laf.useScreenMenuBar", "true");

        // Show a splash screen whilst we load up
        Splash splash = new Splash();
        splash.setVisible(true);
        
        // TESTING: The following will be moved to a properties file
//        _modelViewPairs.add(new ModelViewPair(
//                GlyfDescript.class,
//                GlyphPanel.class));
//        _modelViewPairs.add(new ModelViewPair(
//                net.java.dev.typecast.ot.table.CmapFormat.class,
//                CharacterMap.class));

        try {
            // Set the L&F appropriate for the OS
            // (Mac automatically selects Aqua, but Windows goes for Metal)
            if (System.getProperty("os.name").startsWith("Windows")) {
                UIManager.setLookAndFeel(
                        "com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
            }

            // Load the user's application preferences
            _appPrefs.load(Preferences.userNodeForPackage(getClass()));

            // Load the resource bundle
            _rb = ResourceBundle.getBundle("sample/app/editor/Main");

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
            JScrollPane treePane = new JScrollPane(
                    JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                    JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
            treePane.getViewport().add(_tree);

            treePane.setBorder(null);

            // Listen for selection events from the tree
            TreeSelectionListener tsl = (TreeSelectionEvent e) -> {
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
            };
            _tree.addTreeSelectionListener(tsl);

            // Create a tabbed workspace
            _tabbedPane = new JTabbedPane();

            // Split the main frame
            _splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                treePane,
                _tabbedPane);
            _splitPane.setOneTouchExpandable(true);
            _splitPane.setDividerLocation(_appPrefs.getTreeWidth());
            _frame.getContentPane().add("Center", _splitPane);
            
            _splitPane.setBorder(null);
            
            // Create a menu bar
            _menu = new EditorMenu(this, _rb, _appPrefs);
            _frame.setJMenuBar(_menu.createMenuBar());

            _frame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        close();
                    }
                }
            );

            // We're built, so make the main frame visible and hide the splash
            _frame.pack();
            _frame.setVisible(true);
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException | HeadlessException e) {
            JOptionPane.showMessageDialog(
                null,
                e.toString(),
                "Exception",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            splash.setVisible(false);
        }
    }

    protected void loadFont(String pathName) {
        _frame.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            File file = new File(pathName);
            OTFontCollection fc = OTFontCollection.create(file);
            _fontCollections.add(fc);

            // Create the tree to put the information in
            TableTreeBuilder.addFontTree(_treeModel, fc);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(
                null,
                e.toString(),
                "I/O Exception",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(
                null,
                e.toString(),
                "Exception",
                JOptionPane.ERROR_MESSAGE);
        }
        _frame.setCursor(Cursor.getDefaultCursor());
     }

    public static void main(String[] args) {
        new Main();
    }

    /**
     * Display a file chooser and open the selected font file
     */
    protected void openFont() {
        String pathName = null;

        // Display a file chooser, depending on what OS we're running on
        if (System.getProperty("os.name").equals("Mac OS X")) {
            FileDialog fd = new FileDialog(_frame, "Open Font");
            fd.setFilenameFilter(new MacOSFilenameFilter());
            fd.setVisible(true);
            if (fd.getFile() != null) {
                pathName = fd.getDirectory() + fd.getFile();
            }
        } else {
            JFileChooser chooser = new JFileChooser();

            EditorFileFilter filter = new EditorFileFilter();
            filter.addExtension("ttf");
            filter.addExtension("ttc");
            filter.addExtension("otf");
            filter.addExtension("dfont");
            filter.setDescription("OpenType Fonts");

            chooser.setFileFilter(filter);

            if (chooser.showOpenDialog(_frame) == JFileChooser.APPROVE_OPTION) {
                pathName = chooser.getSelectedFile().getPath();
            }
        }
        
        if (pathName != null) {
            loadFont(pathName);
            _menu.addMru(pathName);
        }
    }

    /**
     * Close the currently selected font
     */
    protected void closeFont() {
        _fontCollections.remove(
                (OTFontCollection) _selectedCollectionNode.getUserObject());
        _treeModel.removeNodeFromParent(_selectedCollectionNode);
        selectElement(null, null);
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
                    try (FileOutputStream fos = new FileOutputStream(chooser.getSelectedFile().getPath())) {
                        Exporter exporter = new SVGExporter(_selectedFont);
                        exporter.export(fos);
                    }
                } catch (IOException | TableException e) {
                    JOptionPane.showMessageDialog(
                        null,
                        e.toString(),
                        "Exception",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    protected void showHelp() {
        JOptionPane.showMessageDialog(
            null,
            "Typecast currently has no help.",
            "Typecast Help",
            JOptionPane.INFORMATION_MESSAGE);
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
                _rb.getString("Typecast.copyright2") +
                "\n" +
                _rb.getString("Typecast.webHome"),
            _rb.getString("Typecast.about.title"),
            JOptionPane.INFORMATION_MESSAGE);
    }

    protected void showPreferences() {
        JOptionPane.showMessageDialog(
            null,
            "Typecast currently has no preferences page.",
            "Typecast Preferences",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    protected void close() {
        
        // Save the user's application preferences
        _appPrefs.setAppWindowPos(_frame.getLocation());
        _appPrefs.setAppWindowSize(_frame.getSize());
        _appPrefs.setTreeWidth(_splitPane.getDividerLocation());
        _appPrefs.save(Preferences.userNodeForPackage(getClass()));

        // End the application
        System.exit(0);
    }
    
    protected void changeGlyphView() {
        _glyphPane.getGlyphEdit().setPreview(_menu.isPreview());
        _glyphPane.getGlyphEdit().setDrawControlPoints(_menu.isShowPoints());
        _glyphPane.getGlyphEdit().setDrawHints(_menu.isShowHints());
        _glyphPane.getGlyphEdit().repaint();
    }
    
    private void selectElement(OTFont font, TableTreeNode tn) {
        
        // Note that this font is currently selected
        _selectedFont = font;

        Object obj = (tn != null) ? tn.getUserObject() : null;

        // Check that we actually have work to do
        if (_treeSelection == obj) {
            return;
        }

        // Configure the tabbed pane
        _tabbedPane.removeAll();

        // Add all the panes we're interested in
//        for (ModelViewPair p : _modelViewPairs) {
//            if (p._model.isInstance(obj)) {
//                Component view = p._view.newInstance();
//                if (view instanceof EditorView) {
//                    ((EditorView)view).setModel(font, obj);
//                }
//                _tabbedPane.add(view);
//            }
//        }

        // Then add the panes we're interested in
        if (obj instanceof GlyphDescription
                || obj instanceof net.java.dev.typecast.cff.Charstring) {
            _glyphPane = new GlyphPanel(_appPrefs);
            _glyphPane.setModel(font, obj);
            _tabbedPane.add(_glyphPane);
        }

        // Character maps
        if (obj instanceof net.java.dev.typecast.ot.table.CmapFormat) {
            CharacterMap cm = new CharacterMap();
            cm.setModel(_selectedFont, obj);
            _tabbedPane.add(cm);
        }

        // Bitmaps
        if (obj instanceof net.java.dev.typecast.ot.table.SbixTable.GlyphDataRecord) {
            BitmapPanel bitmapPanel = new BitmapPanel();
            bitmapPanel.setName("Bitmap");
            bitmapPanel.setModel(_selectedFont, obj);
            _tabbedPane.add(bitmapPanel);
        }

        // All selections get a "dump" pane
        if (obj != null) {
            DumpPanel textPane = new DumpPanel();
            textPane.setName("Dump");
            textPane.setModel(_selectedFont, obj);
            _tabbedPane.add(textPane);
        }

        _treeSelection = obj;
    }
}
