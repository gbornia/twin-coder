/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.program;

/**
 *
 * @author gbornia
 */
public class CodeMemento implements Comparable {
    // State
    private final String code;
    private final int caretPosition;
    
    public CodeMemento(String code, int caretPosition) {
        this.code = code;
        this.caretPosition = caretPosition;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the caretPosition
     */
    public int getCaretPosition() {
        return caretPosition;
    }
    
    @Override
    public boolean equals(Object another) {
        return (compareTo(another) == 0);
    }
    
    @Override
    public int compareTo(Object another) {
        int result = -1;
        if ((another != null) && (another instanceof CodeMemento)) {
            CodeMemento anotherMemento = (CodeMemento) another;
            if ((code != null) && (anotherMemento.getCode()!=null)) {
                result = code.compareTo(anotherMemento.getCode());
                if (result == 0) {
                    result = (new Integer(caretPosition)).compareTo(new Integer(anotherMemento.getCaretPosition()));
                }
            }
        }
        return result;
    }
}
