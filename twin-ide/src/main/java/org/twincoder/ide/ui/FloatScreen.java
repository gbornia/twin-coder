/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author gbornia
 */
public class FloatScreen extends javax.swing.JFrame {

    private final String name;
    private final JPanel content;
    private final MainScreen mainScreen;
    private boolean reattached = false;
    /**
     * Creates new form FloatScreen
     * @param name
     * @param content
     * @param mainScreen
     * @param fullScreen
     */
    public FloatScreen(String name, JPanel content, MainScreen mainScreen, boolean fullScreen) {
        if (fullScreen) {
            setUndecorated(true);
            setExtendedState(JFrame.MAXIMIZED_BOTH); 
        } else {
            setSize(mainScreen.getWidth()-100, mainScreen.getHeight()-100);
            setPreferredSize(new Dimension(mainScreen.getWidth()-100, mainScreen.getHeight()-100));
            setLocation(mainScreen.getX()+50, mainScreen.getY()+50);
        }
        setTitle(name);
        initComponents();
        jPanelContent.add(content, BorderLayout.CENTER);
        jButtonBackToNormal.setVisible(fullScreen);
        jButtonFullScreen.setVisible(!fullScreen);

        this.name = name;
        this.content = content;
        this.mainScreen = mainScreen;
    }

    public String getComponentName() {
        return name;
    }
    
    public JPanel getContent() {
        return content;
    }
    
    public void dockToMain() {
        mainScreen.reatachWindow(this);
        reattached = true;
        closeWindow();
    }
    
    private void closeWindow() {
        if (!reattached) {
            mainScreen.close(content);
        }
        dispose();
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanelContent = new javax.swing.JPanel();
        jToolBar = new javax.swing.JToolBar();
        jButtonDock = new javax.swing.JButton();
        jButtonBackToNormal = new javax.swing.JButton();
        jButtonFullScreen = new javax.swing.JButton();
        jButtonHideToolbar = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jButtonClose = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/images/blocks.png")).getImage());
        setSize(new java.awt.Dimension(400, 300));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jPanelContent.setLayout(new java.awt.BorderLayout());
        getContentPane().add(jPanelContent, java.awt.BorderLayout.CENTER);

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);

        jButtonDock.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/application.png"))); // NOI18N
        jButtonDock.setText("Dock to Main Screen");
        jButtonDock.setFocusable(false);
        jButtonDock.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButtonDock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDockActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonDock);

        jButtonBackToNormal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/reduce.png"))); // NOI18N
        jButtonBackToNormal.setText("Back to Normal");
        jButtonBackToNormal.setToolTipText("");
        jButtonBackToNormal.setFocusable(false);
        jButtonBackToNormal.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButtonBackToNormal.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonBackToNormalActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonBackToNormal);

        jButtonFullScreen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/expand.png"))); // NOI18N
        jButtonFullScreen.setText("Full Screen");
        jButtonFullScreen.setToolTipText("");
        jButtonFullScreen.setFocusable(false);
        jButtonFullScreen.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButtonFullScreen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonFullScreenActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonFullScreen);

        jButtonHideToolbar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/screen.png"))); // NOI18N
        jButtonHideToolbar.setText("Hide Toolbar");
        jButtonHideToolbar.setToolTipText("");
        jButtonHideToolbar.setFocusable(false);
        jButtonHideToolbar.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButtonHideToolbar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonHideToolbarActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonHideToolbar);
        jToolBar.add(filler1);

        jButtonClose.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/close-button-rollover.png"))); // NOI18N
        jButtonClose.setText("Close");
        jButtonClose.setFocusable(false);
        jButtonClose.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jButtonClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCloseActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonClose);

        getContentPane().add(jToolBar, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonDockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDockActionPerformed
        dockToMain();
    }//GEN-LAST:event_jButtonDockActionPerformed

    private void jButtonCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCloseActionPerformed
        closeWindow();
    }//GEN-LAST:event_jButtonCloseActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        closeWindow();
    }//GEN-LAST:event_formWindowClosed

    private void jButtonFullScreenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonFullScreenActionPerformed
        java.awt.EventQueue.invokeLater(() -> {
            dockToMain();
            mainScreen.detachTab(content, true);
        });
    }//GEN-LAST:event_jButtonFullScreenActionPerformed

    private void jButtonBackToNormalActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonBackToNormalActionPerformed
        java.awt.EventQueue.invokeLater(() -> {
            dockToMain();
            mainScreen.detachTab(content, false);
        });
    }//GEN-LAST:event_jButtonBackToNormalActionPerformed

    private void jButtonHideToolbarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonHideToolbarActionPerformed
        jToolBar.setVisible(false);
    }//GEN-LAST:event_jButtonHideToolbarActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButtonBackToNormal;
    private javax.swing.JButton jButtonClose;
    private javax.swing.JButton jButtonDock;
    private javax.swing.JButton jButtonFullScreen;
    private javax.swing.JButton jButtonHideToolbar;
    private javax.swing.JPanel jPanelContent;
    private javax.swing.JToolBar jToolBar;
    // End of variables declaration//GEN-END:variables
}
