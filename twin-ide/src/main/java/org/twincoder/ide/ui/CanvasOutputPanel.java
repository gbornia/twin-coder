package org.twincoder.ide.ui;

import org.twincoder.ide.program.ProgramOutputInterface;
import org.twincoder.ide.canvas.CanvasObject;
import org.twincoder.ide.program.InputListener;
import org.twincoder.ide.program.ThreadControl;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.twincoder.ide.canvas.CanvasUtils;
import org.twincoder.ide.program.Program;
import org.twincoder.ide.program.ProgramContext;

/**
 *
 * @author Gabriel
 */
public class CanvasOutputPanel extends javax.swing.JPanel implements ProgramOutputInterface  {

    private final ArrayList<String> textArray;
    private final ArrayList<CanvasObject> objectArray;
    private final Properties properties;

    private ProgramContext context;
    
    private boolean tick = false;
    
    private static Font consoleFont;
    
   
    // Input for read and readln
    private InputListener inputListener = null;
    private String inputString = null;
    private InputThread inputThread = null;
    private ThreadControl inputThreadControl = null;
    private boolean listening = false;
    
    // Ruler and other controls
    private boolean rulerVisible = false;
    public static final BasicStroke DASHED = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[]{2f,2f}, 0.0f);
    public static final BasicStroke SOLID = new BasicStroke(1.0f);
    
    public static final int REFRESH_MILLIS = 16;
    
    private final Timer timer = new Timer();
    private static long lastUpdate = 0;
    private static long latestRepaint = 0;
    
    /**
     * Creates new form ScreenOutput
     */
    public CanvasOutputPanel() {
        consoleFont = new Font("Courier New", Font.PLAIN, 12);
        textArray = new ArrayList();
        objectArray = new ArrayList();
        properties = new Properties();
        initComponents();
        timer.schedule(new RefreshTimer(this), REFRESH_MILLIS, REFRESH_MILLIS);
    }


    @Override
    public void disposeResources() {
        // Terminates all scheduled timers
        timer.cancel();

        if (inputThread != null) {
            inputThread.stopListening();
        }

        if (context != null) {
            context.logInfo("Output", "Resources have been finalized");
            context = null;
        }
    }

    @Override
    public int getWindowWidth() {
        return getWidth();
    }

    @Override
    public int getWindowHeight() {
        return getHeight();
    }

    class RefreshTimer extends TimerTask {
        JPanel panel;
        public RefreshTimer(JPanel panel) {
            this.panel = panel;
        }
        @Override
        public void run() {
           
            if (latestRepaint < lastUpdate - REFRESH_MILLIS) { 
                panel.repaint();
                latestRepaint = System.currentTimeMillis();
            }
        }
        
    }
    
    private void markForPaint() {
        lastUpdate = System.currentTimeMillis();
    }
    
    @Override
    public void addCanvasObject(CanvasObject object) {
        synchronized(objectArray) {
            objectArray.add(object);
            markForPaint();

        }
    }
    
    /**
     *
     */
    @Override
    public void clear() {
        synchronized(objectArray) {
            objectArray.clear();
        }
        textArray.clear();
        markForPaint();

    }

    @Override
    public void print(String text) {
        // Add it to the array
        int lastLine = textArray.size()-1;
        if (lastLine >= 0) {
            textArray.set(lastLine, textArray.get(lastLine) + text);
        } else {
            textArray.add(text);
        }
        repaint();
    }
 
    @Override
    public void println(String text) {
        print(text);
        textArray.add("");
        repaint();
    }    
    
    @Override
    public void startInput(InputListener inputListener, ThreadControl threadControl) {
        this.inputListener = inputListener;  
        this.inputThreadControl = threadControl;
        this.listening = true;
        inputString = "";
        
        // Start tick for visual view       
        inputThread = new InputThread();
        inputThread.start();
        
        try {
            // Do a quick sleep to make sure the thread will acquire control
            Thread.sleep(100);
        } catch (InterruptedException ex) {
            Logger.getLogger(CanvasOutputPanel.class.getName()).log(Level.SEVERE, "Error trying to sleep while starting to listen for input", ex);
        }
    }

    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);    
        Graphics2D g2 = (Graphics2D) g;
        
        // Draw entire component white
        g2.setColor(CanvasUtils.stringToColor(properties.getProperty("background.color"), Color.white));
        g2.fillRect(0, 0, getWidth(), getHeight());

        // Draw ruler
        if (rulerVisible) {
            g2.setStroke(DASHED);
            int rulerSize = 20;
            for (int i=0; i < getWidth()/rulerSize; i++) {
                g2.setColor(i%5==0?Color.GRAY:Color.LIGHT_GRAY);
                g2.drawLine(i*rulerSize, 0, i*rulerSize, getHeight());
            }
            for (int i=0; i < getHeight()/rulerSize; i++) {
                g2.setColor(i%5==0?Color.GRAY:Color.LIGHT_GRAY);
                g2.drawLine(0, i*rulerSize, getWidth(), i*rulerSize);
            }
        }
        g2.setStroke(SOLID);
        
        // Draw canvas objects
        synchronized(objectArray) {
            Iterator<CanvasObject> i = objectArray.iterator();
            while (i.hasNext()) {
                CanvasObject object = i.next();
                object.paint(g);
            }
        }

        // Draw console text
        int x = 22;
        int y = 18;
        g2.setColor(Color.black);
        g2.setFont(getConsoleFont());
        for (int i=0; i < textArray.size(); i ++) {
            String line = textArray.get(i);
            if (line != null) {
                if ((i == textArray.size()-1) && tick) {
                    g2.drawString(line + "_", x, y);
                } else {
                    g2.drawString(line, x, y);
                }
                y = y + 20;
            }
        }
    }

    public void setRulerVisible(boolean visible) {
        rulerVisible = visible;
        markForPaint();
        /*
        SwingUtilities.invokeLater(() -> {
            repaint();
        });  
        */
    }
    
    public boolean isRulerVisible() {
        return rulerVisible;
    }

    @Override
    public void setProperty(String property, String value) {
        properties.setProperty(property, value); 
    }

    @Override
    public void initialize(ProgramContext context) {
        this.context = context;
        this.requestFocus();
    }

    @Override
    public void setFocus() {
        requestFocus();
    }
    
    class InputThread extends Thread {
        
        private boolean stop = false;
         
        public void stopListening() {
            
            // Releases the control so it can proceed
            if (!stop) {
                System.out.println("Inpiut released control");
                inputThreadControl.release();       
            }
            stop = true;
        }
        
        
        @Override
        public void run() {
            // Blocl the entry
            if (inputThreadControl != null) {              
                try {
                    System.out.println("Input trying to acquire...");
                    // Get control so the rest of the program does not continue
                    inputThreadControl.acquire();
                   
                    // Continuously paint the tick that indicates it' listening for user input
                    while (!stop) {
                        tick = !tick;
                        markForPaint();
                        sleep(250);
                        requestFocus();
                    }
                    
                } catch (InterruptedException ex) {
                    Logger.getLogger(CanvasOutputPanel.class.getName()).log(Level.SEVERE, "Error with the input thread", ex);
                    stop = true;
                }            
            }
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

        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void formKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyTyped

        if (!listening) {
            return;
        }
        
        int key  = (int)evt.getKeyChar();
        switch (key) {
            case KeyEvent.VK_ENTER:
                // On enter add a new line
                println("");
                inputCompleted();
                break;
            case KeyEvent.VK_BACK_SPACE:
                // Clear the input string
                if (inputString != null && inputString.length() > 0) {
                    // Clear the input
                    inputString = inputString.substring(0, inputString.length()-1);
                    
                    // Clear the written text
                    int lastLine = textArray.size()-1;
                    String lastString = textArray.get(lastLine);
                    if (lastString.length() > 0) {
                        textArray.set(lastLine, lastString.substring(0, lastString.length()-1));
                    }
                    markForPaint();
                    /*
                    SwingUtilities.invokeLater(() -> {
                        repaint();
                    });
                    */
                }
                break;
            default:
                // Add the character also to the input
                inputString = (inputString!=null?inputString:"") + evt.getKeyChar();
                
                // Add the character to the last string (or new line if it was empty)
                print(evt.getKeyChar()+"");
        }
        
        
    }//GEN-LAST:event_formKeyTyped

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed
        if (context != null) {
            switch (evt.getKeyCode()) {
                case KeyEvent.VK_ENTER: 
                    context.setKeyPressed("enter");
                    break;
                case KeyEvent.VK_LEFT: 
                    context.setKeyPressed("left");
                    break;
                case KeyEvent.VK_RIGHT: 
                    context.setKeyPressed("right");
                    break;            
                case KeyEvent.VK_UP: 
                    context.setKeyPressed("up");
                    break;                  
                case KeyEvent.VK_DOWN: 
                    context.setKeyPressed("down");
                    break; 
                case KeyEvent.VK_SPACE: 
                    context.setKeyPressed("space");
                    break; 
                case KeyEvent.VK_ESCAPE: 
                    context.setKeyPressed("esc");
                    break; 
                case KeyEvent.VK_TAB: 
                    context.setKeyPressed("tab");
                    break; 
                case KeyEvent.VK_DELETE: 
                    context.setKeyPressed("delete");
                    break; 
                default:
                    if (Character.isLetterOrDigit(evt.getKeyCode())) {
                        context.setKeyPressed(evt.getKeyChar() + "");
                    }
            }
        }
        
    }//GEN-LAST:event_formKeyPressed



    /**
     * @return the consoleFont
     */
    public static Font getConsoleFont() {
        return consoleFont;
    }

    /**
     * @param aConsoleFont the consoleFont to set
     */
    public static void setConsoleFont(Font aConsoleFont) {
        consoleFont = aConsoleFont;
    }

        

    
    private void inputCompleted() {
         // Returns the result to the listner
        if (inputListener != null) {
            inputListener.inputReady(inputString);
        }
        
        // Stop ticking
        if (inputThread != null) {
            inputThread.stopListening();
            tick = false;
            markForPaint();
        }
        
        this.listening = false;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
