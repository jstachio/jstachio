package io.jstach.apt;

import java.util.Objects;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeMirror;

import org.eclipse.jdt.annotation.Nullable;

import io.jstach.apt.context.JavaLanguageModel;

public class ClassRef {
    
    private final String packageName;
    private final String simpleName;
    private final String binaryName;
    private final String canonicalName;
    
    private ClassRef(String packageName, String simpleName, String binaryName, String canonicalName) {
        super();
        this.packageName = packageName;
        this.simpleName = simpleName;
        this.binaryName = binaryName;
        this.canonicalName = canonicalName;
    }
    
    public static ClassRef ofBinaryName(String binaryName) {
        int i = binaryName.lastIndexOf(".");
        if (i == 0) {
            throw new IllegalArgumentException("malformed binaryName");
        }
        String className = i > 0 ? binaryName.substring(i + 1) : binaryName;
        String packageName = i > 0 ? binaryName.substring(0, i) : "";
        int j = className.lastIndexOf("$");
        String simpleName = i > 0 ? className.substring(j + 1) : className;
        String canonicalName = binaryName.replace("$", ".");
        return new ClassRef(packageName, simpleName, binaryName, canonicalName);
    }
    
    public static ClassRef of(String packageName, String className) {
        String binaryClassName = className.replace(".", "$");
        String binaryName = packageName.isEmpty() ? "" + binaryClassName : packageName + "." + binaryClassName;
        return ofBinaryName(binaryName);
    }
    
    public static ClassRef of(PackageElement packageElement, String className) {
        return of(packageElement.getQualifiedName().toString(), className);
    }
    
    public static ClassRef of(TypeMirror tm) {
        var e = ((DeclaredType) tm).asElement();
        var te = (TypeElement) e;
        return of(te);
    }
    
    public static ClassRef of(TypeElement te) {
        var elements = JavaLanguageModel.getInstance().getElements();
        PackageElement pe = elements.getPackageOf(te);
        assert pe != null;
        String packageName = pe.getQualifiedName().toString();
        String simpleName = te.getSimpleName().toString();
        String binaryName = elements.getBinaryName(te).toString();
        String qualifiedName = te.getQualifiedName().toString();
        return new ClassRef(packageName, simpleName, binaryName, qualifiedName);
    }
    
    public static ClassRef of(Class<?> c) {
        return ClassRef.ofBinaryName(c.getName());
    }
    
    
    @Override
    public int hashCode() {
        return Objects.hash(binaryName);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClassRef other = (ClassRef) obj;
        return Objects.equals(binaryName, other.binaryName);
    }

    public @Nullable String getCanonicalName() {
        return canonicalName;
    }
    
    public String requireCanonicalName() {
        return Objects.requireNonNull(canonicalName);
    }
    
    public String getBinaryName() {
        return binaryName;
    }
    
    public String getPackageName() {
        return packageName;
    }
    
    public String getSimpleName() {
        return simpleName;
    }
    
    public String getBinaryNameMinusPackage() {
        if (packageName.isEmpty()) {
            return binaryName;
        }
        //The length will include the "."
        return binaryName.substring(packageName.length() + 1);
    }
}
