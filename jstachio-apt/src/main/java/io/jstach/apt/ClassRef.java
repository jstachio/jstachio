package io.jstach.apt;


public class ClassRef {
    
    private final String packageName;
    private final String simpleName;
    public ClassRef(String packageName, String simpleName) {
        super();
        this.packageName = packageName;
        this.simpleName = simpleName;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public String getSimpleName() {
        return simpleName;
    }
    
    public String getName() {
        return packageName + "." + simpleName;
    }
}
