/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.ui;

import org.twincoder.ide.compiler.CompilerCommand;
import java.awt.Color;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JTextPane;
import javax.swing.RepaintManager;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
/**
 *
 * @author Gabriel
 */
public class EditorHighlighter {
    
    public static void highlight(JTextPane editor) {
        long millis = System.currentTimeMillis();
        if (editor != null) {
            try {
                String docText = editor.getDocument().getText(0, editor.getDocument().getLength()).toLowerCase();
                //Replace to save http:// form being highlighted as a comment
                docText = docText.replaceAll("://",";!!");
                
                StyledDocument doc = (StyledDocument) editor.getDocument();
                SimpleAttributeSet atts = new SimpleAttributeSet();
                StyleConstants.setForeground(atts, Color.black); 
                StyleConstants.setFontFamily(atts, "Courier New");
                StyleConstants.setFontSize(atts, 12);
                doc.setCharacterAttributes(0, docText.length()+1, atts, true );
                RepaintManager.currentManager(editor).markCompletelyClean(editor);
                
                
                // Highlight reserved words
                StyleConstants.setForeground(atts, Color.blue); 
                StyleConstants.setBold(atts, true); 
                String[] reservedWords = CompilerCommand.getAvailableReservedWords();
                for (int i=0; reservedWords!=null && i < reservedWords.length; i++) {
                    int index = -1;
                    while (docText.indexOf(reservedWords[i], index + 1) > -1) {
                        index = docText.indexOf(reservedWords[i], index + 1);
                        doc.setCharacterAttributes( index, reservedWords[i].length(), atts, true );    
                        RepaintManager.currentManager(editor).markCompletelyClean(editor);
                    }
                }

                 
                // Highlight strings
                StyleConstants.setForeground(atts, Color.MAGENTA); 
                StyleConstants.setBold(atts, false); 
                highlightPattern(editor, "(\\\".*?\\\")", docText, doc, atts, false);
                RepaintManager.currentManager(editor).markCompletelyClean(editor);
               
                // Mark comments
                StyleConstants.setForeground(atts, Color.LIGHT_GRAY); 
                StyleConstants.setBold(atts, false); 
                highlightPattern(editor, "(\\/\\/.*)", docText, doc, atts, false);
                highlightPattern(editor, "(\\/\\*.*?\\*\\/)", docText, doc, atts, true);
                
                RepaintManager.currentManager(editor).markCompletelyDirty(editor);
            } catch (BadLocationException ex) {
                Logger.getLogger(EditorHighlighter.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private static void highlightPattern(JTextPane editor, String pattern, String docText, StyledDocument doc, SimpleAttributeSet atts, boolean ignoreNewLine) {
        Pattern pt = Pattern.compile(pattern, ignoreNewLine?Pattern.DOTALL:Pattern.MULTILINE);
        Matcher matcher = pt.matcher(docText);
        HashSet<String> set = new HashSet();
        
        // Find strings (differentiator)
        while (matcher.find()) {
            set.add(matcher.group(0));
        }  
        
        // Iterate over found strings
        Iterator<String> i = set.iterator();
        while (i.hasNext()) {           
            String comment = i.next();
            int index = -1;
            while (docText.indexOf(comment, index + 1) > -1) {
                index = docText.indexOf(comment, index + 1);
                doc.setCharacterAttributes( index, comment.length(), atts, true );    
                RepaintManager.currentManager(editor).markCompletelyClean(editor);
            }
        }       
    }

}

