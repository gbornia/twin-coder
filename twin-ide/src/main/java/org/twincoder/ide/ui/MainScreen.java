package org.twincoder.ide.ui;

// Icons from: https://www.iconfinder.com/iconsets/fatcow

import java.awt.Color;
import org.twincoder.ide.utils.ResourceUtils;
import org.twincoder.ide.program.Program;
import org.twincoder.ide.program.ClipboardSupport;
import java.awt.Component;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import org.twincoder.ide.program.FileInterface;
import org.twincoder.ide.program.ProgramOutputInterface;
import org.twincoder.ide.program.ProjectProperties;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.twincoder.ide.program.ProgramController;
import org.twincoder.ide.program.ResourceDisposable;
import org.twincoder.ide.utils.TwinCoderUtils;

/**
 *
 * @author Gabriel
 */
public final class MainScreen extends javax.swing.JFrame implements Observer {

    private final ProjectProperties properties;
    private final Map<String, Component> openedFileComponents;
    private int count = 1;
    private final ImageIcon ICON_CODE = new javax.swing.ImageIcon(getClass().getResource("/images/code.png"));
    private final ImageIcon ICON_SCREEN = new javax.swing.ImageIcon(getClass().getResource("/images/screen.png"));
    private final ImageIcon ICON_TWIN = new javax.swing.ImageIcon(getClass().getResource("/images/blocks.png"));
    private final Set<FloatScreen> windows;
    private final Timer timer = new Timer();
    
    
    private ProgramListControl listControl = null;
    
    /**
     * Creates new form TwinMainScreen
     */
    public MainScreen() {
        initComponents();
        properties = ProjectProperties.getProjectProperties();
        openedFileComponents = new HashMap<>();
        windows = new HashSet<>();
        
        // Open menus and welcome message
        java.awt.EventQueue.invokeLater(() -> {
            openResource("/documentation/welcome.html");
            processAutoOpen();
            processMenuReopen();
        });

        // Initialize timer
        timer.schedule(new VerifyFocus(), 3000, 1000);
        timer.schedule(new MemoryChecker(), 3500, 3500);
        timer.schedule(new MemoryCleanup(), 43253, 43253);
        
        // Add observer to the ProgramController
        ProgramController.getController().addObserver(this);
        updateSummaryMessage();
    }

    public void processAutoOpen() {
        if (properties != null) {
            // Check and open last opened files
            if (properties.isFileAutoOpen()) {
                String[] files = properties.getOpenedFiles();
                if (files != null) {
                    for (int i=0; i < files.length; i++) {
                        File f = new File(files[i]);
                        openEditorFile(f);
                    }
                }
            }  
        }        
    }
    
    public void processMenuReopen() {
        // Clean
        jMenuReopen.removeAll();
        jMenuReopen.setEnabled(false);
        
        // Add submenus
        if (properties != null) {
             // Process the reopen menu
            String lastUsed[] = properties.getLastUsedFiles();
            if (lastUsed != null && lastUsed.length > 0) {
                jMenuReopen.setEnabled(true);
                
                for (int i=0; i < lastUsed.length; i ++) {
                    JMenuItem newMenu = new JMenuItem();
                    newMenu.setText(lastUsed[i]);
                    newMenu.addActionListener((java.awt.event.ActionEvent evt) -> {
                        try {
                            openEditorFile(new File(((JMenuItem)evt.getSource()).getText()));
                        } catch (Exception e) {
                            Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, "Error trying to reopen last used file", e);
                        }                        
                    });
                    jMenuReopen.add(newMenu);
                }
            }     
        }
    }
    
    public ImageIcon getIconForComponent(JPanel component) {
        ImageIcon icon = null;
        if (component instanceof CodeEditor) {
            icon = ICON_CODE;
        } else if (component instanceof ProgramOutputPanel) {
            icon = ICON_SCREEN;
        } else {
            icon = ICON_TWIN;
        }
        return icon;
    }
    
    public void addTab(String tabName, JPanel component) {
        jTabbedPane.add(tabName, component);
        ImageIcon icon = getIconForComponent(component);
        jTabbedPane.setTabComponentAt(jTabbedPane.indexOfComponent(component), new CloseableTabTitle(jTabbedPane, this, icon));
        jTabbedPane.setSelectedComponent(component);
        validateMenus();
    }
    
    public void setName(Component component, String name) {
        int index = jTabbedPane.indexOfComponent(component);
        if (index > -1) {
            jTabbedPane.setTitleAt(index, name);
        }
           
    }
    
    public void select(Component component) {
        jTabbedPane.setSelectedComponent(component);
    }
    
    private void processRemove(Component component) {
        if (component != null) {           
            // If it is a file, remove it from the list of opened files
            if (component instanceof FileInterface) {
                FileInterface fi = (FileInterface) component;
                File f = fi.getFile();
                if (f != null) {
                    properties.removeOpenFile(f.getAbsolutePath());
                    openedFileComponents.remove(f.getAbsolutePath());
                }
            }
            
            // If it is a running program, stop the program
            if (component instanceof ProgramOutputPanel) {            
                ProgramOutputPanel op = (ProgramOutputPanel) component;
                ProgramController.getController().stop(op.getProgram());
            }
            
            // If it is disposable, dispose resources
            if (component instanceof ResourceDisposable) {            
                ResourceDisposable rd = (ResourceDisposable) component;
                rd.disposeResources();
            }
            
            // Remove tab
            jTabbedPane.remove(component);
            
            // Clear the unique reference to the list control
            if (listControl == component) {
                listControl = null;
            }
        }
    }
    
        
    public void close(Component component) {
        if (component != null && component instanceof FileInterface) {
            FileInterface fi = (FileInterface) component;
            if (fi.isSaveNeeded()) {
                
                switch (JOptionPane.showConfirmDialog(this, 
                        "The file " + jTabbedPane.getTitleAt(jTabbedPane.indexOfComponent(component)) + 
                        " has changes, would you like to Save it?"))  {
                    case JOptionPane.YES_OPTION:
                        fi.save();
                        processRemove(component);
                        break;
                    case JOptionPane.NO_OPTION:
                        processRemove(component);
                        break;
                }
            } else {
                processRemove(component);
            }
        } else {
            processRemove(component);
        }
    }
    
    public void closeAll() {      
        Component[] components = jTabbedPane.getComponents();
        if (components != null) {
            for (Component component : components) {
                close(component);
            }
        }
    }
    
    public void openEditorFile(File f) {
        if (f != null && f.exists()) {
            if (openedFileComponents.containsKey(f.getAbsolutePath())) {
                // File was already opened, select tab
                jTabbedPane.setSelectedComponent(openedFileComponents.get(f.getAbsolutePath()));
            } else {
                // File needs to be opened
                CodeEditor ce = new CodeEditor(this);
                addTab(f.getName(), ce);
                ce.loadFile(f);
                properties.addOpenFile(f.getAbsolutePath());
                openedFileComponents.put(f.getAbsolutePath(), ce);
            }
        }
    }
    
    public void validateMenus() {
        Component component = jTabbedPane.getSelectedComponent();
        if (component != null && component instanceof FileInterface) {
            FileInterface fi = (FileInterface) component;
            jMenuItemSave.setEnabled(fi.isSaveNeeded());
            jMenuItemSaveAs.setEnabled(fi.isSaveAsAvailable());
        }       
        if (component != null) {
            // Enable/disable menus based on the type of component
            jMenuItemCompile.setEnabled(component instanceof CodeEditor);
            jMenuItemPlay.setEnabled(component instanceof CodeEditor);
            jMenuItemStop.setEnabled(false);
            jCheckBoxMenuItemProgramControl.setEnabled(component instanceof ProgramOutputPanel);
            jCheckBoxMenuItemRuler.setEnabled(component instanceof ProgramOutputPanel);
            
            // Special treatment for the CodeEditor
            if (component instanceof CodeEditor) {
                jMenuItemUndo.setEnabled(((CodeEditor)component).isUndoAvailable());
                jMenuItemRedo.setEnabled(((CodeEditor)component).isRedoAvailable());
                jMenuItemCopy.setEnabled(true);
                jMenuItemCut.setEnabled(true);
                jMenuItemPaste.setEnabled(true);
            } else {
                jMenuItemUndo.setEnabled(false);
                jMenuItemRedo.setEnabled(false);
                jMenuItemCopy.setEnabled(false);
                jMenuItemCut.setEnabled(false);
                jMenuItemPaste.setEnabled(false);
            }
            
            // Special treatment for the ProgramOutputPanel
            if (component instanceof ProgramOutputPanel) {
                ProgramOutputPanel op = (ProgramOutputPanel) component;
                jMenuItemStop.setEnabled(ProgramController.getController().isRunning(op.getProgram()));
                jCheckBoxMenuItemProgramControl.setSelected(op.isProgramControlVisible());
                jCheckBoxMenuItemRuler.setSelected(op.isRulerVisible());
            } 
        }       

        // Apply status to buttons
        jButtonSave.setEnabled(jMenuItemSave.isEnabled());
        jButtonSaveAll.setEnabled(jMenuItemSaveAll.isEnabled());
        jButtonUndo.setEnabled(jMenuItemUndo.isEnabled());
        jButtonRedo.setEnabled(jMenuItemRedo.isEnabled());
        jButtonCopy.setEnabled(jMenuItemCopy.isEnabled());
        jButtonPaste.setEnabled(jMenuItemPaste.isEnabled());
        jButtonCompile.setEnabled(jMenuItemCompile.isEnabled());
        jButtonPlay.setEnabled(jMenuItemPlay.isEnabled());
        jButtonStop.setEnabled(jMenuItemStop.isEnabled());
        jToggleButtonProgramControl.setEnabled(jCheckBoxMenuItemProgramControl.isEnabled());
        jToggleButtonProgramControl.setSelected(jCheckBoxMenuItemProgramControl.isSelected());
        jToggleButtonRuler.setEnabled(jCheckBoxMenuItemRuler.isEnabled());
        jToggleButtonRuler.setSelected(jCheckBoxMenuItemRuler.isSelected());
    }
    
    private JFileChooser getFileChooser() {
        
        JFileChooser fc = new JFileChooser();
        
        FileFilter twnFilter = new FileNameExtensionFilter("Twin Code", "twn");
        FileFilter txtFilter = new FileNameExtensionFilter("Text File", "txt");
        fc.addChoosableFileFilter(twnFilter);
        fc.addChoosableFileFilter(txtFilter);
        fc.setFileFilter(twnFilter);
        
        return fc;
    }
    
    public void openResource(String resource) {
        if (resource != null) {
            String content = ResourceUtils.getResource(resource);
            if (content != null) {
                if (resource.endsWith(".html") || resource.endsWith("htm")) {
                    Browser browser = new Browser(this);
                    String title = TwinCoderUtils.getStringInBetween(content, "<title>", "</title>");
                    addTab(title!=null?title:resource, browser);
                    browser.loadContent(content);
                } 
                else if (resource.endsWith(".twn")) {
                    CodeEditor editor = new CodeEditor(this);
                    addTab(resource, editor);
                    String title = TwinCoderUtils.getStringInBetween(content, "Example {", "}");
                    editor.loadContent(title!=null?title:resource, content);
                }
            }
        }
    }
    
    public void detachTab(JPanel component, boolean fullScreen) {
        String name = jTabbedPane.getTitleAt(jTabbedPane.indexOfComponent(component));
        // Remove tab
        jTabbedPane.remove(component);
        FloatScreen screen = new FloatScreen(name, component, this, fullScreen);
        ImageIcon icon = getIconForComponent(component);
        screen.setIconImage(icon.getImage());
        screen.setVisible(true);
        windows.add(screen);
    }
    
    public void reatachWindow(FloatScreen window) {
        if (window != null) {
            addTab(window.getComponentName(), window.getContent());
            windows.remove(window);
        }
    }
    
    public void checkFocus() {
        if (jTabbedPane.getSelectedComponent() instanceof ProgramOutputInterface) {
            ((ProgramOutputInterface)jTabbedPane.getSelectedComponent()).setFocus();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if ("Program".equals(arg) || "Log".equals(arg)) {
            updateSummaryMessage();
        }
    }
    
    private void updateSummaryMessage() {
        String messageStr = "";
        String errorStr = "";
        int errors = 0;
        
        int[] programSummary = ProgramController.getController().getProgramSummary();
        if (programSummary != null) {
            
            errors = programSummary[ProgramController.PROGRAM_SUMMARY_ERROR_LOG_MESSAGES];
            messageStr = 
                    "Programs: " + programSummary[ProgramController.PROGRAM_SUMMARY_TOTAL_PROGRAMS] +
                    " | Running: " + programSummary[ProgramController.PROGRAM_SUMMARY_RUNNING_PROGRAMS] +
                    " | Log Total: " + programSummary[ProgramController.PROGRAM_SUMMARY_TOTAL_LOG_MESSAGES] +
                    " | Errors: " + errors;              
            
            
            if (errors > 0) {
                errorStr = errors + (errors>1?" Errors":" Error");             
            }
        }
        
        jLabelMessage.setText(messageStr);
        jLabelErrors.setText(errorStr);
        jLabelErrors.setVisible(!"".equals(errorStr));
    }

    class VerifyFocus extends TimerTask {

        @Override
        public void run() {
            java.awt.EventQueue.invokeLater(() -> {
                validateMenus();
                checkFocus();
            });            
        }
        
    }
    
    class MemoryChecker extends TimerTask {
        @Override
        public void run() {
            Runtime rt = Runtime.getRuntime();
            long total = rt.totalMemory();
            long free = rt.freeMemory();  
            long used = total - free;
            jLabelStatus.setText("Memory: " + Math.round(used/(1024*1024)) + " Mb of " + Math.round(total/(1024*1024)) + "Mb (" + Math.round(((double)used/(double)total)*100) + "%)");   
        }
    } 
    
    class MemoryCleanup extends TimerTask {
        @Override
        public void run() {
            java.awt.EventQueue.invokeLater(() -> {
                jLabelStatus.setForeground(Color.red); 
            });

            Runtime.getRuntime().gc();
            
            java.awt.EventQueue.invokeLater(() -> {
                jLabelStatus.setForeground(Color.black);
            });            
        }
    }    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane = new javax.swing.JTabbedPane();
        jToolBar = new javax.swing.JToolBar();
        jButtonNew = new javax.swing.JButton();
        jButtonOpen = new javax.swing.JButton();
        jButtonSave = new javax.swing.JButton();
        jButtonSaveAll = new javax.swing.JButton();
        jSeparator9 = new javax.swing.JToolBar.Separator();
        jButtonUndo = new javax.swing.JButton();
        jButtonRedo = new javax.swing.JButton();
        jSeparator6 = new javax.swing.JToolBar.Separator();
        jButtonCopy = new javax.swing.JButton();
        jButtonPaste = new javax.swing.JButton();
        jSeparator7 = new javax.swing.JToolBar.Separator();
        jButtonCompile = new javax.swing.JButton();
        jButtonPlay = new javax.swing.JButton();
        jButtonStop = new javax.swing.JButton();
        jSeparator10 = new javax.swing.JToolBar.Separator();
        jToggleButtonProgramControl = new javax.swing.JToggleButton();
        jToggleButtonRuler = new javax.swing.JToggleButton();
        jSeparator11 = new javax.swing.JToolBar.Separator();
        jButtonDetach = new javax.swing.JButton();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        jLabelErrors = new javax.swing.JLabel();
        jPanelStatus = new javax.swing.JPanel();
        jLabelStatus = new javax.swing.JLabel();
        jLabelMessage = new javax.swing.JLabel();
        jMenuBar = new javax.swing.JMenuBar();
        jMenuFile = new javax.swing.JMenu();
        jMenuItemNew = new javax.swing.JMenuItem();
        jSeparator1 = new javax.swing.JPopupMenu.Separator();
        jMenuItemOpen = new javax.swing.JMenuItem();
        jMenuReopen = new javax.swing.JMenu();
        jMenuItemClose = new javax.swing.JMenuItem();
        jMenuItemCloseAll = new javax.swing.JMenuItem();
        jSeparator2 = new javax.swing.JPopupMenu.Separator();
        jMenuItemSave = new javax.swing.JMenuItem();
        jMenuItemSaveAs = new javax.swing.JMenuItem();
        jMenuItemSaveAll = new javax.swing.JMenuItem();
        jSeparator3 = new javax.swing.JPopupMenu.Separator();
        jMenuItemProperties = new javax.swing.JMenuItem();
        jSeparator4 = new javax.swing.JPopupMenu.Separator();
        jMenuItemExit = new javax.swing.JMenuItem();
        jMenuEdit = new javax.swing.JMenu();
        jMenuItemUndo = new javax.swing.JMenuItem();
        jMenuItemRedo = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        jMenuItemCut = new javax.swing.JMenuItem();
        jMenuItemCopy = new javax.swing.JMenuItem();
        jMenuItemPaste = new javax.swing.JMenuItem();
        jSeparator5 = new javax.swing.JPopupMenu.Separator();
        jMenuItemFind = new javax.swing.JMenuItem();
        jMenuView = new javax.swing.JMenu();
        jCheckBoxMenuItemProgramControl = new javax.swing.JCheckBoxMenuItem();
        jCheckBoxMenuItemRuler = new javax.swing.JCheckBoxMenuItem();
        jMenuProgram = new javax.swing.JMenu();
        jMenuItemCompile = new javax.swing.JMenuItem();
        jMenuItemPlay = new javax.swing.JMenuItem();
        jMenuItemStop = new javax.swing.JMenuItem();
        jSeparator12 = new javax.swing.JPopupMenu.Separator();
        jMenuItemProgramControl = new javax.swing.JMenuItem();
        jMenuWindow = new javax.swing.JMenu();
        jMenuItemDetach = new javax.swing.JMenuItem();
        jMenuItemReatachAll = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        setTitle("TwinCoder.org");
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/images/blocks.png")).getImage());
        setPreferredSize(new java.awt.Dimension(800, 600));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
            public void windowActivated(java.awt.event.WindowEvent evt) {
                formWindowActivated(evt);
            }
        });

        jTabbedPane.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPaneStateChanged(evt);
            }
        });
        getContentPane().add(jTabbedPane, java.awt.BorderLayout.CENTER);

        jToolBar.setFloatable(false);
        jToolBar.setRollover(true);

        jButtonNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new.png"))); // NOI18N
        jButtonNew.setToolTipText("New File (Ctrl+N)");
        jButtonNew.setFocusable(false);
        jButtonNew.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonNew.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonNew.setMinimumSize(new java.awt.Dimension(30, 30));
        jButtonNew.setPreferredSize(new java.awt.Dimension(30, 30));
        jButtonNew.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonNewActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonNew);

        jButtonOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open.png"))); // NOI18N
        jButtonOpen.setToolTipText("Open File (Ctrl+O)");
        jButtonOpen.setFocusable(false);
        jButtonOpen.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonOpen.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonOpen.setMinimumSize(new java.awt.Dimension(30, 30));
        jButtonOpen.setPreferredSize(new java.awt.Dimension(30, 30));
        jButtonOpen.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOpenActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonOpen);

        jButtonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.png"))); // NOI18N
        jButtonSave.setToolTipText("Save File (Ctrl+S)");
        jButtonSave.setFocusable(false);
        jButtonSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSave.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonSave.setMinimumSize(new java.awt.Dimension(30, 30));
        jButtonSave.setPreferredSize(new java.awt.Dimension(30, 30));
        jButtonSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonSave);

        jButtonSaveAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-all.png"))); // NOI18N
        jButtonSaveAll.setToolTipText("Save All");
        jButtonSaveAll.setFocusable(false);
        jButtonSaveAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonSaveAll.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonSaveAll.setMinimumSize(new java.awt.Dimension(30, 30));
        jButtonSaveAll.setPreferredSize(new java.awt.Dimension(30, 30));
        jButtonSaveAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonSaveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSaveAllActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonSaveAll);
        jToolBar.add(jSeparator9);

        jButtonUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/undo.png"))); // NOI18N
        jButtonUndo.setToolTipText("Undo (Ctrl+Z)");
        jButtonUndo.setFocusable(false);
        jButtonUndo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonUndo.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonUndo.setMinimumSize(new java.awt.Dimension(30, 30));
        jButtonUndo.setPreferredSize(new java.awt.Dimension(30, 30));
        jButtonUndo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonUndoActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonUndo);

        jButtonRedo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/redo.png"))); // NOI18N
        jButtonRedo.setToolTipText("Redo (Ctrl+Y)");
        jButtonRedo.setFocusable(false);
        jButtonRedo.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonRedo.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonRedo.setMinimumSize(new java.awt.Dimension(30, 30));
        jButtonRedo.setPreferredSize(new java.awt.Dimension(30, 30));
        jButtonRedo.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRedoActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonRedo);
        jToolBar.add(jSeparator6);

        jButtonCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/copy.png"))); // NOI18N
        jButtonCopy.setToolTipText("Copy (Ctrl+C)");
        jButtonCopy.setFocusable(false);
        jButtonCopy.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonCopy.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonCopy.setMinimumSize(new java.awt.Dimension(30, 30));
        jButtonCopy.setPreferredSize(new java.awt.Dimension(30, 30));
        jButtonCopy.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCopyActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonCopy);

        jButtonPaste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/paste.png"))); // NOI18N
        jButtonPaste.setToolTipText("Paste (Ctrl+V)");
        jButtonPaste.setFocusable(false);
        jButtonPaste.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPaste.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonPaste.setMinimumSize(new java.awt.Dimension(30, 30));
        jButtonPaste.setPreferredSize(new java.awt.Dimension(30, 30));
        jButtonPaste.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPasteActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonPaste);
        jToolBar.add(jSeparator7);

        jButtonCompile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/inspect.png"))); // NOI18N
        jButtonCompile.setToolTipText("Verify code (F5)");
        jButtonCompile.setFocusable(false);
        jButtonCompile.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonCompile.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonCompile.setMinimumSize(new java.awt.Dimension(30, 30));
        jButtonCompile.setPreferredSize(new java.awt.Dimension(30, 30));
        jButtonCompile.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonCompile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCompileActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonCompile);

        jButtonPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/play.png"))); // NOI18N
        jButtonPlay.setToolTipText("Run program (F9)");
        jButtonPlay.setFocusable(false);
        jButtonPlay.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonPlay.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonPlay.setMinimumSize(new java.awt.Dimension(30, 30));
        jButtonPlay.setPreferredSize(new java.awt.Dimension(30, 30));
        jButtonPlay.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPlayActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonPlay);

        jButtonStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/error.png"))); // NOI18N
        jButtonStop.setToolTipText("Run program (F9)");
        jButtonStop.setFocusable(false);
        jButtonStop.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonStop.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonStop.setMinimumSize(new java.awt.Dimension(30, 30));
        jButtonStop.setPreferredSize(new java.awt.Dimension(30, 30));
        jButtonStop.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonStop);
        jToolBar.add(jSeparator10);

        jToggleButtonProgramControl.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/verify.png"))); // NOI18N
        jToggleButtonProgramControl.setToolTipText("View Program Control");
        jToggleButtonProgramControl.setFocusable(false);
        jToggleButtonProgramControl.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonProgramControl.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonProgramControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonProgramControlActionPerformed(evt);
            }
        });
        jToolBar.add(jToggleButtonProgramControl);

        jToggleButtonRuler.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ruler.png"))); // NOI18N
        jToggleButtonRuler.setToolTipText("View Ruler");
        jToggleButtonRuler.setFocusable(false);
        jToggleButtonRuler.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButtonRuler.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButtonRuler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButtonRulerActionPerformed(evt);
            }
        });
        jToolBar.add(jToggleButtonRuler);
        jToolBar.add(jSeparator11);

        jButtonDetach.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/detach-window.png"))); // NOI18N
        jButtonDetach.setToolTipText("Undock as Window");
        jButtonDetach.setFocusable(false);
        jButtonDetach.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jButtonDetach.setMaximumSize(new java.awt.Dimension(30, 30));
        jButtonDetach.setMinimumSize(new java.awt.Dimension(30, 30));
        jButtonDetach.setPreferredSize(new java.awt.Dimension(30, 30));
        jButtonDetach.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jButtonDetach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDetachActionPerformed(evt);
            }
        });
        jToolBar.add(jButtonDetach);
        jToolBar.add(filler1);

        jLabelErrors.setBackground(new java.awt.Color(255, 0, 0));
        jLabelErrors.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabelErrors.setForeground(new java.awt.Color(255, 255, 255));
        jLabelErrors.setText("0 Errors");
        jLabelErrors.setToolTipText("Click to open Program Control");
        jLabelErrors.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 5, 5, 5));
        jLabelErrors.setOpaque(true);
        jLabelErrors.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelErrorsMouseClicked(evt);
            }
        });
        jToolBar.add(jLabelErrors);

        getContentPane().add(jToolBar, java.awt.BorderLayout.PAGE_START);

        jPanelStatus.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 5, 1, 5));
        jPanelStatus.setLayout(new java.awt.BorderLayout());
        jPanelStatus.add(jLabelStatus, java.awt.BorderLayout.WEST);

        jLabelMessage.setText("Message");
        jLabelMessage.setToolTipText("Click to open Program Control");
        jLabelMessage.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jLabelMessageMouseClicked(evt);
            }
        });
        jPanelStatus.add(jLabelMessage, java.awt.BorderLayout.EAST);

        getContentPane().add(jPanelStatus, java.awt.BorderLayout.SOUTH);

        jMenuFile.setText("File");
        jMenuFile.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenuFileMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        jMenuItemNew.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/new.png"))); // NOI18N
        jMenuItemNew.setText("New File");
        jMenuItemNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemNewActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemNew);
        jMenuFile.add(jSeparator1);

        jMenuItemOpen.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/open.png"))); // NOI18N
        jMenuItemOpen.setText("Open File...");
        jMenuItemOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemOpenActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemOpen);

        jMenuReopen.setText("Reopen");
        jMenuReopen.setEnabled(false);
        jMenuFile.add(jMenuReopen);

        jMenuItemClose.setText("Close");
        jMenuItemClose.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCloseActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemClose);

        jMenuItemCloseAll.setText("Close All");
        jMenuItemCloseAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCloseAllActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemCloseAll);
        jMenuFile.add(jSeparator2);

        jMenuItemSave.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save.png"))); // NOI18N
        jMenuItemSave.setText("Save");
        jMenuItemSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSave);

        jMenuItemSaveAs.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-as.png"))); // NOI18N
        jMenuItemSaveAs.setText("Save as...");
        jMenuItemSaveAs.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAsActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveAs);

        jMenuItemSaveAll.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/save-all.png"))); // NOI18N
        jMenuItemSaveAll.setText("Save all");
        jMenuItemSaveAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSaveAllActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemSaveAll);
        jMenuFile.add(jSeparator3);

        jMenuItemProperties.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/settings.png"))); // NOI18N
        jMenuItemProperties.setText("Properties...");
        jMenuItemProperties.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPropertiesActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemProperties);
        jMenuFile.add(jSeparator4);

        jMenuItemExit.setText("Exit");
        jMenuItemExit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        jMenuBar.add(jMenuFile);

        jMenuEdit.setText("Edit");
        jMenuEdit.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenuEditMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        jMenuItemUndo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Z, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemUndo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/undo.png"))); // NOI18N
        jMenuItemUndo.setText("Undo");
        jMenuItemUndo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemUndoActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemUndo);

        jMenuItemRedo.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_Y, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemRedo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/redo.png"))); // NOI18N
        jMenuItemRedo.setText("Redo");
        jMenuItemRedo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemRedoActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemRedo);
        jMenuEdit.add(jSeparator8);

        jMenuItemCut.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/cut.png"))); // NOI18N
        jMenuItemCut.setText("Cut");
        jMenuItemCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCutActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemCut);

        jMenuItemCopy.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/copy.png"))); // NOI18N
        jMenuItemCopy.setText("Copy");
        jMenuItemCopy.setToolTipText("");
        jMenuItemCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCopyActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemCopy);

        jMenuItemPaste.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemPaste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/paste.png"))); // NOI18N
        jMenuItemPaste.setText("Paste");
        jMenuItemPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPasteActionPerformed(evt);
            }
        });
        jMenuEdit.add(jMenuItemPaste);
        jMenuEdit.add(jSeparator5);

        jMenuItemFind.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F, java.awt.event.InputEvent.CTRL_MASK));
        jMenuItemFind.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/find.png"))); // NOI18N
        jMenuItemFind.setText("Find...");
        jMenuEdit.add(jMenuItemFind);

        jMenuBar.add(jMenuEdit);

        jMenuView.setText("View");

        jCheckBoxMenuItemProgramControl.setText("Program Control");
        jCheckBoxMenuItemProgramControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemProgramControlActionPerformed(evt);
            }
        });
        jMenuView.add(jCheckBoxMenuItemProgramControl);

        jCheckBoxMenuItemRuler.setText("Ruler");
        jCheckBoxMenuItemRuler.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuItemRulerActionPerformed(evt);
            }
        });
        jMenuView.add(jCheckBoxMenuItemRuler);

        jMenuBar.add(jMenuView);

        jMenuProgram.setText("Program");
        jMenuProgram.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenuProgramMenuSelected(evt);
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
        });

        jMenuItemCompile.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        jMenuItemCompile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/inspect.png"))); // NOI18N
        jMenuItemCompile.setText("Compile");
        jMenuItemCompile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemCompileActionPerformed(evt);
            }
        });
        jMenuProgram.add(jMenuItemCompile);

        jMenuItemPlay.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F9, 0));
        jMenuItemPlay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/play.png"))); // NOI18N
        jMenuItemPlay.setText("Run Program...");
        jMenuItemPlay.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemPlayActionPerformed(evt);
            }
        });
        jMenuProgram.add(jMenuItemPlay);

        jMenuItemStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/error.png"))); // NOI18N
        jMenuItemStop.setText("Stop Program");
        jMenuItemStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemStopActionPerformed(evt);
            }
        });
        jMenuProgram.add(jMenuItemStop);
        jMenuProgram.add(jSeparator12);

        jMenuItemProgramControl.setText("Program Control");
        jMenuItemProgramControl.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemProgramControlActionPerformed(evt);
            }
        });
        jMenuProgram.add(jMenuItemProgramControl);

        jMenuBar.add(jMenuProgram);

        jMenuWindow.setText("Window");

        jMenuItemDetach.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/detach-window.png"))); // NOI18N
        jMenuItemDetach.setText("Undock as Window");
        jMenuItemDetach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemDetachActionPerformed(evt);
            }
        });
        jMenuWindow.add(jMenuItemDetach);

        jMenuItemReatachAll.setText("Dock as Tabs");
        jMenuItemReatachAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemReatachAllActionPerformed(evt);
            }
        });
        jMenuWindow.add(jMenuItemReatachAll);

        jMenuBar.add(jMenuWindow);

        setJMenuBar(jMenuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItemOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemOpenActionPerformed
        JFileChooser fc = getFileChooser();
        if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            openEditorFile(fc.getSelectedFile());
            processMenuReopen();
        }
        validateMenus();
    }//GEN-LAST:event_jMenuItemOpenActionPerformed

    private void jMenuItemNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemNewActionPerformed
        CodeEditor editor = new CodeEditor(this);
        String name = "New " + (count++) + ".twn";
        editor.setFileName(name);
        addTab(name, editor);
        validateMenus();
    }//GEN-LAST:event_jMenuItemNewActionPerformed

    private void jMenuItemSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveActionPerformed
        Component component = jTabbedPane.getSelectedComponent();
        if (component != null && component instanceof CodeEditor) {
            FileInterface fi = (FileInterface) component;
            if (fi.getFile() != null) {
                if (fi.isSaveNeeded()) {
                    fi.save();
                }
            } else {
                // If no file then call Save As...
                jMenuItemSaveAsActionPerformed(evt);
            }
        }
        validateMenus();
    }//GEN-LAST:event_jMenuItemSaveActionPerformed

    private void jMenuItemSaveAsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAsActionPerformed
        Component component = jTabbedPane.getSelectedComponent();
        if (component != null && component instanceof FileInterface) {
            FileInterface fi = (FileInterface) component;
            
            if (fi.isSaveAsAvailable()) {
                JFileChooser fc = getFileChooser();
                
                // Select default file
                if (fi.getFile() != null) {
                    fc.setSelectedFile(fi.getFile());
                } else {
                    if (fi.getFileName() != null) {
                        fc.setSelectedFile(new File(fi.getFileName()));
                    }
                }
                
                if (fc.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File previous = fi.getFile();
                    if (fi.saveToFile(fc.getSelectedFile())) {
                        // Treat open file track
                        if (previous != null) {
                            properties.removeOpenFile(previous.getAbsolutePath());
                        }
                        properties.addOpenFile(fi.getFile().getAbsolutePath());
                        
                        // Process menus
                        validateMenus();
                        processMenuReopen();
                    }
                }
            }
        }
        validateMenus();
    }//GEN-LAST:event_jMenuItemSaveAsActionPerformed

    
    private void jMenuFileMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenuFileMenuSelected
        validateMenus();
    }//GEN-LAST:event_jMenuFileMenuSelected

    private void jMenuItemCloseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCloseActionPerformed
        close(jTabbedPane.getSelectedComponent());
    }//GEN-LAST:event_jMenuItemCloseActionPerformed

    private void jMenuItemCloseAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCloseAllActionPerformed
        closeAll();
    }//GEN-LAST:event_jMenuItemCloseAllActionPerformed

    private void jMenuItemExitActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemExitActionPerformed
        exitApplication();
    }//GEN-LAST:event_jMenuItemExitActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed

    }//GEN-LAST:event_formWindowClosed

    private void jMenuItemSaveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSaveAllActionPerformed
        java.awt.EventQueue.invokeLater(() -> {
            Component[] components = jTabbedPane.getComponents();
            if (components != null) {
                for (int i=0; i < components.length; i++) {
                    Component component = components[i];
                    if (component != null && component instanceof CodeEditor) {
                        FileInterface fi = (FileInterface) component;
                        if (fi.isSaveNeeded()) {
                            fi.save();
                        }
                    }
                }
            }
            validateMenus();
        });
    }//GEN-LAST:event_jMenuItemSaveAllActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        exitApplication();
    }//GEN-LAST:event_formWindowClosing

    private void jMenuItemCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCopyActionPerformed
        java.awt.EventQueue.invokeLater(() -> {
            if (jTabbedPane.getSelectedComponent() instanceof ClipboardSupport) {
                // Copy to clipboard
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable transferable = new StringSelection(
                        ((ClipboardSupport)jTabbedPane.getSelectedComponent()).getSelectedText()
                );
                clipboard.setContents(transferable, null);
            }
            validateMenus();
        });
    }//GEN-LAST:event_jMenuItemCopyActionPerformed

    private void jMenuItemPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPasteActionPerformed
        java.awt.EventQueue.invokeLater(() -> {
            if (jTabbedPane.getSelectedComponent() instanceof ClipboardSupport) {
                // Paste from clipboard
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                try {
                    ((ClipboardSupport)jTabbedPane.getSelectedComponent()).replaceSelectedText(
                            (String) clipboard.getData(DataFlavor.stringFlavor)
                    );
                } catch (UnsupportedFlavorException ex) {
                    Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, "Error trying to paste to editor (invalid type)", ex);
                } catch (IOException ex) {
                    Logger.getLogger(MainScreen.class.getName()).log(Level.SEVERE, "Error trying to paste to editor (I/O Error)", ex);
                }
            }
            validateMenus();
        });
    }//GEN-LAST:event_jMenuItemPasteActionPerformed

    private void jMenuItemCutActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCutActionPerformed
        java.awt.EventQueue.invokeLater(() -> {
            if (jTabbedPane.getSelectedComponent() instanceof ClipboardSupport) {
                // Copy to clipboard
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                Transferable transferable = new StringSelection(
                        ((ClipboardSupport)jTabbedPane.getSelectedComponent()).getSelectedText()
                );
                clipboard.setContents(transferable, null);

                // Clears (cut) the text
                ((ClipboardSupport)jTabbedPane.getSelectedComponent()).replaceSelectedText("");
            }
            validateMenus();
        });
    }//GEN-LAST:event_jMenuItemCutActionPerformed

    private void jButtonCompileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCompileActionPerformed
        jMenuItemCompileActionPerformed(evt);
    }//GEN-LAST:event_jButtonCompileActionPerformed

    private void jButtonPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPlayActionPerformed
        jMenuItemPlayActionPerformed(evt);
    }//GEN-LAST:event_jButtonPlayActionPerformed

    private void jButtonSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveActionPerformed
        jMenuItemSaveActionPerformed(evt);
    }//GEN-LAST:event_jButtonSaveActionPerformed

    private void jButtonSaveAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSaveAllActionPerformed
        jMenuItemSaveAllActionPerformed(evt);
    }//GEN-LAST:event_jButtonSaveAllActionPerformed

    private void jButtonOpenActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOpenActionPerformed
        jMenuItemOpenActionPerformed(evt);
    }//GEN-LAST:event_jButtonOpenActionPerformed

    private void jMenuEditMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenuEditMenuSelected
       validateMenus();
    }//GEN-LAST:event_jMenuEditMenuSelected

    private void jMenuProgramMenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenuProgramMenuSelected
        validateMenus();
    }//GEN-LAST:event_jMenuProgramMenuSelected

    private void jTabbedPaneStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPaneStateChanged
        validateMenus();
        checkFocus();
    }//GEN-LAST:event_jTabbedPaneStateChanged

    private void jMenuItemCompileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemCompileActionPerformed
        if (jTabbedPane.getSelectedComponent() instanceof CodeEditor) {
            ((CodeEditor) jTabbedPane.getSelectedComponent()).compile();
        }
        validateMenus();
    }//GEN-LAST:event_jMenuItemCompileActionPerformed

    private void jMenuItemPlayActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPlayActionPerformed
        if (jTabbedPane.getSelectedComponent() instanceof CodeEditor) {
            java.awt.EventQueue.invokeLater(() -> {
                Program program = ((CodeEditor) jTabbedPane.getSelectedComponent()).getCompiledProgram();
                if (program != null) {
                    
                    if (!program.isCompilerError()) {
                        ProgramOutputPanel output = new ProgramOutputPanel(program, jCheckBoxMenuItemProgramControl.isSelected());
                        this.addTab("Output", output);

                        ProgramController.getController().run(program, output);

                        validateMenus();
                        checkFocus();
                    } else {
                        JOptionPane.showMessageDialog(this, "Error compiling program. Not possible to run. Please check error messages.", "Error compiling program", JOptionPane.ERROR_MESSAGE);
                    }
                }        
            });
        }
    }//GEN-LAST:event_jMenuItemPlayActionPerformed

    private void jButtonCopyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCopyActionPerformed
        jMenuItemCopyActionPerformed(evt);
    }//GEN-LAST:event_jButtonCopyActionPerformed

    private void jButtonPasteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPasteActionPerformed
        jMenuItemPasteActionPerformed(evt);
    }//GEN-LAST:event_jButtonPasteActionPerformed

    private void jButtonNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonNewActionPerformed
        jMenuItemNewActionPerformed(evt);
    }//GEN-LAST:event_jButtonNewActionPerformed

    private void jMenuItemUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemUndoActionPerformed
        java.awt.EventQueue.invokeLater(() -> {
            if (jTabbedPane.getSelectedComponent() instanceof CodeEditor) {
                // Call undo
                ((CodeEditor)jTabbedPane.getSelectedComponent()).undo();
            }
        });
    }//GEN-LAST:event_jMenuItemUndoActionPerformed

    private void jMenuItemRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemRedoActionPerformed
        java.awt.EventQueue.invokeLater(() -> {
            if (jTabbedPane.getSelectedComponent() instanceof CodeEditor) {
                // Call undo
                ((CodeEditor)jTabbedPane.getSelectedComponent()).redo();
            }
        });
    }//GEN-LAST:event_jMenuItemRedoActionPerformed

    private void jButtonUndoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonUndoActionPerformed
        jMenuItemUndoActionPerformed(evt);
    }//GEN-LAST:event_jButtonUndoActionPerformed

    private void jButtonRedoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRedoActionPerformed
        jMenuItemRedoActionPerformed(evt);
    }//GEN-LAST:event_jButtonRedoActionPerformed

    private void jCheckBoxMenuItemProgramControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemProgramControlActionPerformed
        java.awt.EventQueue.invokeLater(() -> {
            if (jTabbedPane.getSelectedComponent() instanceof ProgramOutputPanel) {
                if (jCheckBoxMenuItemProgramControl.isSelected()) {
                    ((ProgramOutputPanel) jTabbedPane.getSelectedComponent()).showProgramControl();
                } else {
                    ((ProgramOutputPanel) jTabbedPane.getSelectedComponent()).hideProgramControl();
                }
                validateMenus();
            }
        });
    }//GEN-LAST:event_jCheckBoxMenuItemProgramControlActionPerformed

    private void jToggleButtonProgramControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonProgramControlActionPerformed
        jCheckBoxMenuItemProgramControl.setSelected(!jCheckBoxMenuItemProgramControl.isSelected());
        jCheckBoxMenuItemProgramControlActionPerformed(evt);
    }//GEN-LAST:event_jToggleButtonProgramControlActionPerformed

    private void jCheckBoxMenuItemRulerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuItemRulerActionPerformed
        java.awt.EventQueue.invokeLater(() -> {
            if (jTabbedPane.getSelectedComponent() instanceof ProgramOutputPanel) {
                ((ProgramOutputPanel) jTabbedPane.getSelectedComponent()).setRulerVisible(jCheckBoxMenuItemRuler.isSelected());
                validateMenus();
            }
        });
    }//GEN-LAST:event_jCheckBoxMenuItemRulerActionPerformed

    private void jToggleButtonRulerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButtonRulerActionPerformed
        jCheckBoxMenuItemRuler.setSelected(!jCheckBoxMenuItemRuler.isSelected());
        jCheckBoxMenuItemRulerActionPerformed(evt);
    }//GEN-LAST:event_jToggleButtonRulerActionPerformed

    private void jButtonDetachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDetachActionPerformed
        jMenuItemDetachActionPerformed(evt);
    }//GEN-LAST:event_jButtonDetachActionPerformed

    private void jMenuItemDetachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemDetachActionPerformed
        if (jTabbedPane.getSelectedComponent() != null && jTabbedPane.getSelectedComponent() instanceof JPanel) {
            java.awt.EventQueue.invokeLater(() -> {
                JPanel component = (JPanel) jTabbedPane.getSelectedComponent();
                detachTab(component, false);
            });
        }
    }//GEN-LAST:event_jMenuItemDetachActionPerformed

    private void jMenuItemReatachAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemReatachAllActionPerformed
        if (windows.size() > 0) {         
            FloatScreen[] windowArray = windows.toArray(new FloatScreen[]{});
            for (FloatScreen window : windowArray) {
                if (window != null) {
                    window.dockToMain();
                }
            }
        }
    }//GEN-LAST:event_jMenuItemReatachAllActionPerformed

    private void formWindowActivated(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowActivated
        checkFocus();
    }//GEN-LAST:event_formWindowActivated

    private void jButtonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopActionPerformed
        jMenuItemStopActionPerformed(evt);
    }//GEN-LAST:event_jButtonStopActionPerformed

    private void jMenuItemStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemStopActionPerformed
        if (jTabbedPane.getSelectedComponent() instanceof ProgramOutputPanel) {
            java.awt.EventQueue.invokeLater(() -> {
                ProgramOutputPanel op = (ProgramOutputPanel) jTabbedPane.getSelectedComponent();
                ProgramController.getController().stop(op.getProgram());
            });
        }

    }//GEN-LAST:event_jMenuItemStopActionPerformed

    private void jMenuItemProgramControlActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemProgramControlActionPerformed
       if (listControl == null) {
           listControl = new ProgramListControl();
           addTab("Program Control", listControl);
       } else {
           if (jTabbedPane.indexOfComponent(listControl) > -1) {
               jTabbedPane.setSelectedComponent(listControl);
           } else {
               addTab("Program Control", listControl);
           }
       }
    
    }//GEN-LAST:event_jMenuItemProgramControlActionPerformed

    private void jLabelMessageMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelMessageMouseClicked
        jMenuItemProgramControlActionPerformed(null);
    }//GEN-LAST:event_jLabelMessageMouseClicked

    private void jLabelErrorsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jLabelErrorsMouseClicked
        jMenuItemProgramControlActionPerformed(null);
    }//GEN-LAST:event_jLabelErrorsMouseClicked

    private void jMenuItemPropertiesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemPropertiesActionPerformed
        PropertiesScreen ps = new PropertiesScreen();
        ps.setVisible(true);
    }//GEN-LAST:event_jMenuItemPropertiesActionPerformed

    public void exitApplication() {
        boolean needConfirmation = false;
        Component[] components = jTabbedPane.getComponents();
        if (components != null) {
            for (int i=0; i < components.length; i++) {
                if (components[i] instanceof FileInterface) {
                    FileInterface fi = (FileInterface) components[i];
                    if (fi.isSaveNeeded()) {
                        needConfirmation = true;
                    }
                }
            }
        }
        
        if (needConfirmation) {
            if (JOptionPane.showOptionDialog(
                    this, 
                    "Oops! There are unsaved changes, do you really want to exit?",
                    "Goodbye?", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.QUESTION_MESSAGE, 
                    null,
                    new String[]{"No, I want to save my changes","Yes, I'm OK losing my last changes"},
                    "No, I want to save my changes") == 1) {
                processExitApplication();
            }
        } else {
            processExitApplication();
        }
    }
    
    public void processExitApplication() {
        properties.save();
        dispose();
        System.exit(0);
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainScreen.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MainScreen().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.Box.Filler filler1;
    private javax.swing.JButton jButtonCompile;
    private javax.swing.JButton jButtonCopy;
    private javax.swing.JButton jButtonDetach;
    private javax.swing.JButton jButtonNew;
    private javax.swing.JButton jButtonOpen;
    private javax.swing.JButton jButtonPaste;
    private javax.swing.JButton jButtonPlay;
    private javax.swing.JButton jButtonRedo;
    private javax.swing.JButton jButtonSave;
    private javax.swing.JButton jButtonSaveAll;
    private javax.swing.JButton jButtonStop;
    private javax.swing.JButton jButtonUndo;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemProgramControl;
    private javax.swing.JCheckBoxMenuItem jCheckBoxMenuItemRuler;
    private javax.swing.JLabel jLabelErrors;
    private javax.swing.JLabel jLabelMessage;
    private javax.swing.JLabel jLabelStatus;
    private javax.swing.JMenuBar jMenuBar;
    private javax.swing.JMenu jMenuEdit;
    private javax.swing.JMenu jMenuFile;
    private javax.swing.JMenuItem jMenuItemClose;
    private javax.swing.JMenuItem jMenuItemCloseAll;
    private javax.swing.JMenuItem jMenuItemCompile;
    private javax.swing.JMenuItem jMenuItemCopy;
    private javax.swing.JMenuItem jMenuItemCut;
    private javax.swing.JMenuItem jMenuItemDetach;
    private javax.swing.JMenuItem jMenuItemExit;
    private javax.swing.JMenuItem jMenuItemFind;
    private javax.swing.JMenuItem jMenuItemNew;
    private javax.swing.JMenuItem jMenuItemOpen;
    private javax.swing.JMenuItem jMenuItemPaste;
    private javax.swing.JMenuItem jMenuItemPlay;
    private javax.swing.JMenuItem jMenuItemProgramControl;
    private javax.swing.JMenuItem jMenuItemProperties;
    private javax.swing.JMenuItem jMenuItemReatachAll;
    private javax.swing.JMenuItem jMenuItemRedo;
    private javax.swing.JMenuItem jMenuItemSave;
    private javax.swing.JMenuItem jMenuItemSaveAll;
    private javax.swing.JMenuItem jMenuItemSaveAs;
    private javax.swing.JMenuItem jMenuItemStop;
    private javax.swing.JMenuItem jMenuItemUndo;
    private javax.swing.JMenu jMenuProgram;
    private javax.swing.JMenu jMenuReopen;
    private javax.swing.JMenu jMenuView;
    private javax.swing.JMenu jMenuWindow;
    private javax.swing.JPanel jPanelStatus;
    private javax.swing.JPopupMenu.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator10;
    private javax.swing.JToolBar.Separator jSeparator11;
    private javax.swing.JPopupMenu.Separator jSeparator12;
    private javax.swing.JPopupMenu.Separator jSeparator2;
    private javax.swing.JPopupMenu.Separator jSeparator3;
    private javax.swing.JPopupMenu.Separator jSeparator4;
    private javax.swing.JPopupMenu.Separator jSeparator5;
    private javax.swing.JToolBar.Separator jSeparator6;
    private javax.swing.JToolBar.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JToolBar.Separator jSeparator9;
    private javax.swing.JTabbedPane jTabbedPane;
    private javax.swing.JToggleButton jToggleButtonProgramControl;
    private javax.swing.JToggleButton jToggleButtonRuler;
    private javax.swing.JToolBar jToolBar;
    // End of variables declaration//GEN-END:variables
}
