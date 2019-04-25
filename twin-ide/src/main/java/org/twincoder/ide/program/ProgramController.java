/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.twincoder.ide.program;

import com.sun.speech.freetts.Voice;
import com.sun.speech.freetts.VoiceManager;
import java.util.ArrayList;
import java.util.Date;
import java.util.Observable;
import java.util.Observer;

/**
 *
 * @author gbornia
 */
public class ProgramController extends Observable implements Observer {
    private static ProgramController unique = null;
    
    private final ArrayList<Program> programs = new ArrayList();
    
    private Voice voice = null;
    
    /**
     * Gets a unique instance of the ProgramController
     * @return 
     */
    public static ProgramController getController() {
        if (unique == null) {
            unique = new ProgramController();
        }
        return unique;
    }
      
    /**
     * Starts the execution of a program
     * @param program
     * @param output 
     */
    public void run(Program program, ProgramOutputInterface output) {
        if (program != null) {
            // Add the program to the list
            programs.add(program);

            // Add this controller as an observer of what's happening in the program
            ProgramContext context = program.getContext();
            if (context != null) {
                context.addObserver(this);
            }
            
            // Run the program
            program.run(output);
        }
        
    }
    
    /**
     * Stops the execution of a program
     * @param program 
     */
    public void stop(Program program) {
        if (program != null && program.isRunning()) {
            program.stopProgram();
        }
    }
    
    /**
     * Verifies if a program is running
     * @param program
     * @return 
     */
    public boolean isRunning(Program program) {
        if (program != null) {
            return program.isRunning();
        }
        return false;
    }

    public int getProgramStatus(Program program) {
        if (program != null) {
            ProgramContext context = program.getContext();
            if (context != null) {
                return context.getProgramStatus();
            }
        }
        return -1;
    }
    
    public Date getProgramStarted(Program program) {
        if (program != null) {
            ProgramContext context = program.getContext();
            if (context != null && context.getProgramStarted() > 0) {
                return new Date(context.getProgramStarted());
            }
        }
        return null;
    }
    
    public Date getProgramEnded(Program program) {
        if (program != null) {
            ProgramContext context = program.getContext();
            if (context != null && context.getProgramEnded() > 0) {
                return new Date(context.getProgramEnded());
            }
        }
        return null;
    }
    /**
     * Count of programs in the controller
     * @return 
     */
    public int programCount() {
        return programs.size();
    }
    
    /**
     * Program at a particular index location
     * @param index
     * @return 
     */
    public Program getProgramAt(int index) {
        if (index > -1 && index < programs.size()) {
            return programs.get(index);
        }
        return null;
    }
    
    /**
     * Return the position of a given program in the list
     * @param program
     * @return 
     */
    public int getProgramIndex(Program program) {
        return programs.indexOf(program);
    }
    
    /**
     * Count of programs in the controller
     * @return 
     */
    public int getProgramMessagesCount(Program program) {
        if (program != null) {
            ProgramContext context = program.getContext();
            if (context != null && context.getLog() != null) {
                return context.getLog().size();
            }
        }
        return 0;
    }
    
    public int getProgramErrorCount(Program program) {
         if (program != null) {
            ProgramContext context = program.getContext();
            if (context != null ) {
                int[] logSummary = program.getContext().getLogSummary();
                if (logSummary != null) {
                     return logSummary[LogEntry.ERROR];
                }
            }
        }
        return 0;       
    }
    
    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        
        // Ignore variable updates
        if (!"Variable".equals(arg)) {
            notifyObservers(arg);
        }
    }
    
    
    public static final int PROGRAM_SUMMARY_TOTAL_PROGRAMS = 0;
    public static final int PROGRAM_SUMMARY_RUNNING_PROGRAMS = 1;
    public static final int PROGRAM_SUMMARY_TOTAL_LOG_MESSAGES = 2;
    public static final int PROGRAM_SUMMARY_ERROR_LOG_MESSAGES = 3;
    
    public int[] getProgramSummary() {
        int[] programSummary = new int[] {0,0,0,0};
        
        programSummary[PROGRAM_SUMMARY_TOTAL_PROGRAMS] = programs.size();
        
        for (int i=0; i < programs.size(); i++) {
            Program program = programs.get(i);
            if (program != null) {
                if (program.isRunning()) {
                    programSummary[PROGRAM_SUMMARY_RUNNING_PROGRAMS]++;
                }
                
               if (program.getContext() != null) {
                   int[] logSummary = program.getContext().getLogSummary();
                   if (logSummary != null) {
                        programSummary[PROGRAM_SUMMARY_ERROR_LOG_MESSAGES] = 
                                programSummary[PROGRAM_SUMMARY_ERROR_LOG_MESSAGES] + 
                                logSummary[LogEntry.ERROR];
                        
                        programSummary[PROGRAM_SUMMARY_TOTAL_LOG_MESSAGES] = 
                                programSummary[PROGRAM_SUMMARY_TOTAL_LOG_MESSAGES] + 
                                logSummary[LogEntry.ERROR] +
                                logSummary[LogEntry.WARNING] +
                                logSummary[LogEntry.INFO];
                       
                   }
                           
               }
                          
            }
        }
        
        return programSummary;
    }
    
    
    
    
    public Voice getVoice() {
        if (voice == null) {
            System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
            VoiceManager vm = VoiceManager.getInstance();
            voice = vm.getVoice("kevin16");
            voice.allocate();      
            
            //TODO: Add configuration for the voice 
            System.out.println("Name: " + voice.getName());
            System.out.println("Description: " + voice.getDescription());
            System.out.println("Organization: " + voice.getOrganization());
            System.out.println("Age: " + voice.getAge());
            System.out.println("Gender: " + voice.getGender());
            System.out.println("Rate: " + voice.getRate());
            System.out.println("Pitch: " + voice.getPitch());
            System.out.println("Style: " + voice.getStyle()); 
            System.out.println("Duration: " + voice.getDurationStretch());              
        }
        return voice;
    }
}
