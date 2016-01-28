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

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import net.java.dev.typecast.cff.CffFont;
import net.java.dev.typecast.cff.Charstring;
import net.java.dev.typecast.cff.NameIndex;
import net.java.dev.typecast.ot.OTFont;
import net.java.dev.typecast.ot.OTFontCollection;
import net.java.dev.typecast.ot.table.CffTable;
import net.java.dev.typecast.ot.table.CmapIndexEntry;
import net.java.dev.typecast.ot.table.CmapTable;
import net.java.dev.typecast.ot.table.DirectoryEntry;
import net.java.dev.typecast.ot.table.Feature;
import net.java.dev.typecast.ot.table.GlyfCompositeComp;
import net.java.dev.typecast.ot.table.GlyfCompositeDescript;
import net.java.dev.typecast.ot.table.GlyfDescript;
import net.java.dev.typecast.ot.table.GlyfTable;
import net.java.dev.typecast.ot.table.GsubTable;
import net.java.dev.typecast.ot.table.ID;
import net.java.dev.typecast.ot.table.LangSys;
import net.java.dev.typecast.ot.table.Lookup;
import net.java.dev.typecast.ot.table.LookupSubtable;
import net.java.dev.typecast.ot.table.NameRecord;
import net.java.dev.typecast.ot.table.NameTable;
import net.java.dev.typecast.ot.table.PostTable;
import net.java.dev.typecast.ot.table.SbixTable;
import net.java.dev.typecast.ot.table.Script;
import net.java.dev.typecast.ot.table.Table;

/**
 * @author <a href="mailto:david.schweinsberg@gmail.com">David Schweinsberg</a>
 */
public class TableTreeBuilder {

    private static void addCmapTable(TableTreeNode parent, CmapTable ct) {
        
        int lastPlatformId = -1;
        int lastEncodingId = -1;
        TableTreeNode platformNode = null;
        TableTreeNode encodingNode = null;

        for (int i = 0; i < ct.getNumTables(); i++) {
            CmapIndexEntry cie = ct.getCmapIndexEntry(i);
            
            // Have we created the proper grouping?
            if (lastPlatformId != cie.getPlatformId()) {
                lastPlatformId = cie.getPlatformId();
                lastEncodingId = -1;
                String s = "Platform ID: " + cie.getPlatformId() + " (" +
                    ID.getPlatformName((short) cie.getPlatformId()) + ")";
                platformNode = createNode(s, null);
                parent.add(platformNode);
            }
            if (lastEncodingId != cie.getEncodingId()) {
                lastEncodingId = cie.getEncodingId();
                String s = "Encoding ID: " + cie.getEncodingId() + " (" +
                    ID.getEncodingName(
                        (short) cie.getPlatformId(),
                        (short) cie.getEncodingId()) + ")";
                encodingNode = createNode(s, cie.getFormat());
                platformNode.add(encodingNode);
            }
        }
    }

    private static void addNameTable(TableTreeNode parent, NameTable nt) {
        
        short lastPlatformId = -1;
        short lastEncodingId = -1;
        short lastLanguageId = -1;
        TableTreeNode platformNode = null;
        TableTreeNode encodingNode = null;
        TableTreeNode languageNode = null;

        for (int i = 0; i < nt.getNumberOfNameRecords(); i++) {
            NameRecord nr = nt.getRecord(i);
            
            // Have we created the proper grouping?
            if (lastPlatformId != nr.getPlatformId()) {
                lastPlatformId = nr.getPlatformId();
                lastEncodingId = -1;
                lastLanguageId = -1;
//                String s = "Platform ID: " + lastPlatformId;
                String s = "Platform ID: " + nr.getPlatformId() + " (" +
                    ID.getPlatformName(nr.getPlatformId()) + ")";
                platformNode = createNode(s, null);
                parent.add(platformNode);
            }
            if (lastEncodingId != nr.getEncodingId()) {
                lastEncodingId = nr.getEncodingId();
                lastLanguageId = -1;
//                String s = "Encoding ID: " + lastEncodingId;
                String s = "Encoding ID: " + nr.getEncodingId() + " (" +
                    ID.getEncodingName(nr.getPlatformId(), nr.getEncodingId()) + ")";
                encodingNode = createNode(s, null);
                platformNode.add(encodingNode);
            }
            if (lastLanguageId != nr.getLanguageId()) {
                lastLanguageId = nr.getLanguageId();
//                String s = "Language ID: " + lastLanguageId;
                String s = "Language ID: " + nr.getLanguageId() + " (" +
                    ID.getLanguageName(nr.getPlatformId(), nr.getLanguageId()) + ")";
                languageNode = createNode(s, null);
                encodingNode.add(languageNode);
            }
            String s = "" + nr.getNameId() + " (" + ID.getNameName(nr.getNameId()) + ")";
//            TypecastTreeNode node = createNode(Integer.toString(nr.getNameId()), nr);
            TableTreeNode node = createNode(s, nr);
            languageNode.add(node);
        }
    }

    private static void addFeatures(TableTreeNode parent, GsubTable gt, LangSys ls) {
        for (int i = 0; i < ls.getFeatureCount(); i++) {
            int index = ls.getFeatureIndex(i);
            String featureTag = gt.getFeatureList().getFeatureRecord(index).getTagAsString();
            Feature f = gt.getFeatureList().getFeature(index);
            TableTreeNode featureNode = new TableTreeNode(featureTag, f);
            parent.add(featureNode);
            
            // Add feature lookups
            for (int j = 0; j < f.getLookupCount(); j++) {
                Lookup l = gt.getLookupList().getLookup(f.getLookupListIndex(j));
                String type = GsubTable.lookupTypeAsString(l.getType());
                TableTreeNode lookupNode = new TableTreeNode(type, l);
                featureNode.add(lookupNode);
                
                // Add lookup subtables
                for (int k = 0; k < l.getSubtableCount(); k++) {
                    LookupSubtable lsub = l.getSubtable(k);
                    
                    // For some reason, lsub can be null
                    // TODO: find out why
                    if (lsub != null) {
                        TableTreeNode lsubNode = new TableTreeNode(
                            lsub.getTypeAsString(),
                            lsub);
                        lookupNode.add(lsubNode);
                    }
                }
            }
        }
    }

    private static void addGsubTable(TableTreeNode parent, GsubTable gt) {

        for (int i = 0; i < gt.getScriptList().getScriptCount(); i++) {
            String tag = gt.getScriptList().getScriptRecord(i).getTagAsString();
            Script s = gt.getScriptList().getScript(i);
            TableTreeNode scriptNode = new TableTreeNode(tag, s);
            parent.add(scriptNode);

            // Add the default LangSys node
            TableTreeNode langSysNode = new TableTreeNode(
                "default",
                s.getDefaultLangSys());
            scriptNode.add(langSysNode);
            addFeatures(langSysNode, gt, s.getDefaultLangSys());
            
            // Add any additional ones
            for (int j = 0; j < s.getLangSysCount(); j++) {
                String langSysTag = s.getLangSysRecord(j).getTagAsString();
                LangSys ls = s.getLangSys(j);
                langSysNode = new TableTreeNode(langSysTag, ls);
                scriptNode.add(langSysNode);
                addFeatures(langSysNode, gt, ls);
            }
        }
    }

    private static void addGlyfComposite(OTFont font, TableTreeNode parent, GlyfCompositeDescript gcd) {
        PostTable postTable = (PostTable) font.getTable(Table.post);
        for (int i = 0; i < gcd.getComponentCount(); i++) {
            GlyfCompositeComp gcc = gcd.getComponent(i);
            parent.add(new TableTreeNode(
                String.valueOf(gcc.getGlyphIndex()) + 
                ((postTable.getVersion() == 0x00020000) ?
                    (" " + postTable.getGlyphName(gcc.getGlyphIndex())) :
                    ""),
                gcc,
                i));
        }
    }

    private static void addGlyfTable(OTFont font, TableTreeNode parent, GlyfTable gt) {
        PostTable postTable = (PostTable) font.getTable(Table.post);
        for (int i = 0; i < font.getNumGlyphs(); i++) {
            GlyfDescript gd = gt.getDescription(i);
            TableTreeNode n = new TableTreeNode(
                String.valueOf(i) + 
                ((postTable.getVersion() == 0x00020000) ?
                    (" " + postTable.getGlyphName(i)) :
                    ""),
                gd,
                i);
            parent.add(n);
            if ((gd != null) &&  gd.isComposite()) {
                
                // We need to add the constituent glyphs
                addGlyfComposite(font, n, (GlyfCompositeDescript) gd);
            }
        }
    }
    
    private static void addCffFont(
            OTFont font,
            TableTreeNode parent,
            CffFont cf) {
        for (int i = 0; i < cf.getCharstringCount(); ++i) {
            Charstring cs = cf.getCharstring(i);
            TableTreeNode n = new TableTreeNode(
                String.valueOf(i) + " " + cs.getName(),
                cs,
                i);
            parent.add(n);
        }
    }
    
    private static void addCffTable(OTFont font, TableTreeNode parent, CffTable ct) {
        NameIndex ni = ct.getNameIndex();
        for (int i = 0; i < ni.getCount(); ++i) {
            TableTreeNode n = new TableTreeNode(
                ni.getName(i),
                ni,
                i);
            parent.add(n);
            addCffFont(font, n, ct.getFont(i));
        }
    }
    
    private static void addSbixStrike(OTFont font, TableTreeNode parent, SbixTable.Strike strike) {
        int i = 0;
        for (SbixTable.GlyphDataRecord gdr : strike.getGlyphDataRecords()) {
            TableTreeNode n = new TableTreeNode(
                String.valueOf(i),
                gdr,
                i++);
            parent.add(n);
        }
    }
    
    private static void addSbixTable(OTFont font, TableTreeNode parent, SbixTable sbix) {
        int i = 0;
        for (SbixTable.Strike strike : sbix.getStrikes()) {
            TableTreeNode n = new TableTreeNode(
                strike.toString(),
                strike,
                i++);
            parent.add(n);
            addSbixStrike(font, n, strike);
        }
    }
    
    private static void addTableDirectoryEntry(OTFont font, TableTreeNode parent, DirectoryEntry de) {
        TableTreeNode node = createNode(de.getTagAsString(), font.getTable(de.getTag()));
        parent.add(node);
        switch (de.getTag()) {
            case Table.name:
                addNameTable(node, (NameTable) font.getTable(Table.name));
                break;
            case Table.cmap:
                addCmapTable(node, (CmapTable) font.getTable(Table.cmap));
                break;
            case Table.glyf:
                addGlyfTable(font, node, (GlyfTable) font.getTable(Table.glyf));
                break;
            case Table.CFF:
                addCffTable(font, node, (CffTable) font.getTable(Table.CFF));
                break;
            case Table.GSUB:
                addGsubTable(node, (GsubTable) font.getTable(Table.GSUB));
                break;
            case Table.sbix:
                addSbixTable(font, node, (SbixTable) font.getTable(Table.sbix));
                break;
            default:
                break;
        }
    }

    public static void addFontTree(TreeModel treeModel, OTFontCollection fc) {
        
        TableTreeNode fcNode = createNode(fc.getPathName(), fc);
        ((TableTreeNode) treeModel.getRoot()).add(fcNode);

        // Add each font in this collection
        for (int i = 0; i < fc.getFontCount(); i++) {
            OTFont font = fc.getFont(i);
            TableTreeNode node = createNode(
                font.getNameTable().getRecordString(ID.nameFullFontName),
                font);
            fcNode.add(node);
            for (int j = 0; j < font.getTableDirectory().getNumTables(); j++) {
                DirectoryEntry de = font.getTableDirectory().getEntry(j);
                addTableDirectoryEntry(font, node, de);
            }
        }
        ((DefaultTreeModel) treeModel).reload();
    }

    public static TreeModel createTypecastTreeModel() {
        TableTreeNode node = createNode("Root", null);
        TreeModel treeModel = new DefaultTreeModel(node);
        return treeModel;
    }

    private static TableTreeNode createNode(String name, Object obj) {
        return new TableTreeNode(name, obj);
    }
}
