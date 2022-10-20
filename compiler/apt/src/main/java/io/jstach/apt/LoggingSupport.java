package io.jstach.apt;


public interface LoggingSupport {
    
    public boolean isDebug();
    
    public void debug(CharSequence message);

}
