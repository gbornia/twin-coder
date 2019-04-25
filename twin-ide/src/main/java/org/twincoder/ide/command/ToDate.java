/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.command;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.twincoder.ide.program.ProgramContext;
import org.twincoder.ide.utils.DateUtils;

/**
 *
 * @author gbornia
 */
public class ToDate extends Command implements Function {

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
        
        double days = 0;
        if (args.length > 0 && context != null) {
            // In case there is a variable
            String stringStr = args[0].getValue(context);
            try {
               
               SimpleDateFormat format = DEFAULT_FORMAT;
               if (args.length > 1) {
                   String formatStr = args[1].getValue(context);
                   if (formatStr != null && !"".equals(formatStr)) {
                       try {
                           format = new SimpleDateFormat(formatStr);
                       } catch (Exception e) {
                           context.logError("ToDate", "Invalid date format " + formatStr + ", using default", e);
                       }
                   }
               }
               
               if (stringStr != null && !"".equals(stringStr)) {
                    Date date = format.parse(stringStr);
                    if (date != null) {
                        days = DateUtils.dateToDays(date);
                    } else {
                       context.logWarning("ToDate", "Cannot convert date, check argument [" + stringStr + "]");
                    }
               } else {
                   context.logWarning("ToDate", "Cannot convert date, argument is empty");
               }
            } catch (NumberFormatException e) {
                context.logError("ToDate", "Error trying to convert date [" + stringStr + "]", e);
            } catch (ParseException ex) {
                context.logError("ToDate", "Error trying to convert date [" + stringStr+ "]", ex);
            }               
        }
        

        return days + "";
    }
    
    @Override
    public void validateArguments() throws CommandArgumentException {
        Function args[] = getArguments();
        if (args == null)
            throw new CommandArgumentException("Missing arguments");
        
        if (args.length == 0 || args.length > 2)
            throw new CommandArgumentException("The toDate command needs one argument or two arguments");

    }
    
}
