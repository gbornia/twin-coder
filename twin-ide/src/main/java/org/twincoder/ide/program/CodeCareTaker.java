/*
 *  TwinCode is an education project.
 *  You are free to use this code for educational purposes. Please improve the code.
 */
package org.twincoder.ide.program;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author gbornia
 */
public class CodeCareTaker {
  
    private final List<CodeMemento> list;
    private int position = -1;
    
    public CodeCareTaker() {
        list = new ArrayList<>();
    }
    
    public void add(CodeMemento memento) {
        
        if (memento != null) {
            // Clean subsequent positions
            while ((list.size() > 0) && (position < list.size() - 1)) {
                list.remove(list.size()-1);
            }

            CodeMemento current = getCurrent();
            if ((current == null) || (!current.equals(memento))){
                // Add memento to the top of the list
                list.add(memento);
                position = list.size() - 1;
            }
        }
    }
    
    public boolean hasPrevious() {
        return (position > 0);
    }
    
    public boolean hasNext() {
        return (position < list.size()-1);
    }
    
    public CodeMemento getCurrent() {
        if ((position >=0) && (position < list.size())) {
            return list.get(position);
        }
        return null;
    } 
    
    public CodeMemento getPrevious() {
        if (hasPrevious() && (position < list.size())) {
            position--;
            return list.get(position);           
        }
        return null;
    }
    
    public CodeMemento getNext() {
        if (hasNext()) {
            position++;
            return list.get(position);
        }
        return null;
    }
}
