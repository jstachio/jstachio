package com.snaphop.staticmustache.apt;

public interface CodeAppendable extends Appendable {
    
    public void print(String s);
    
    public void println();
    
    public boolean suppressesOutput();

    public void enableOutput();

    public void disableOutput();

}
