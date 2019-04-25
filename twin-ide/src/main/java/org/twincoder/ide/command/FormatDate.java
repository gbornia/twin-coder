/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import java.text.SimpleDateFormat;
import java.util.Date;
import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.utils.DateUtils;

/**
 *
 * @author gbornia
 */
public class FormatDate extends Command implements Function {

    private static final SimpleDateFormat DEFAULT_FORMAT = new SimpleDateFormat("MM/dd/yyyy");
    
    @Override
    public boolean run(ProgramContext context) {
        Function args[] = getArguments();
        if (args != null && args.length > 1) {
            if (args[0] instanceof Variable && args[1] != this) {
                context.setVariableValue(((Variable)args[0]).getName(), getValue(context));
                return true;
            }
        }
        
        return false;
    }

    @Override
    public String getValue(ProgramContext context) {
        Function args[] = getArguments();
        
        String dateStr = "";
        if (args.length > 0 && context != null) {
            // In case there is a variable
            try {
               double days = Double.parseDouble(args[0].getValue(context));
               Date date = DateUtils.daysToDate(days);
               
               SimpleDateFormat format = DEFAULT_FORMAT;
               if (args.length > 1) {
                   String formatStr = args[1].getValue(context);
                   if (formatStr != null && !"".equals(formatStr)) {
                       try {
                           format = new SimpleDateFormat(formatStr);
                       } catch (Exception e) {
                           context.logError("FormatDate", "Invalid date format [" + formatStr + "], using default", e);
                       }
                   }
               }
               dateStr = format.format(date);
            } catch (NumberFormatException e) {
               context.logError("FormatDate", "Error with variable content " + args[0], e);
            }               
        }
        

        return dateStr;
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length == 0 || args.length > 2)
            throw new CommandArgumentException("The formatDate command needs one argument or two arguments");

    }
    
}
