package io.jstach.spi;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.ServiceLoader;

import io.jstach.Renderer;
import io.jstach.annotation.JStach;


class Renderers {

    private static final String IMPLEMENTATION_SUFFIX = "Renderer";

    private Renderers() {
    }


    public static <T> Renderer<T> getRenderer(Class<T> clazz) {
        try {
            List<ClassLoader> classLoaders = collectClassLoaders( clazz.getClassLoader() );

            return getRenderer( clazz, classLoaders );
        }
        catch ( ClassNotFoundException | NoSuchMethodException e ) {
            throw new RuntimeException( e );
        }
    }

    private static <T> Renderer<T> getRenderer(Class<T> mapperType, Iterable<ClassLoader> classLoaders)
            throws ClassNotFoundException, NoSuchMethodException {

        for ( ClassLoader classLoader : classLoaders ) {
            Renderer<T> mapper = doGetRenderer( mapperType, classLoader );
            if ( mapper != null ) {
                return mapper;
            }
        }

        throw new ClassNotFoundException("Cannot find implementation for " + mapperType.getName() );
    }

    @SuppressWarnings("unchecked")
    private static <T> Renderer<T> doGetRenderer(Class<T> clazz, ClassLoader classLoader) throws NoSuchMethodException {
        try {
            //TODO use annotation to resolve renderer name
            Class<?> implementation = (Class<?>) classLoader.loadClass( resolveName(clazz) );
            Constructor<?> constructor = implementation.getDeclaredConstructor();
            constructor.setAccessible( true );

            return (Renderer<T>) constructor.newInstance();
        }
        catch (ClassNotFoundException e) {
            return (Renderer<T>) getRendererFromServiceLoader( clazz, classLoader );
        }
        catch ( InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException( e );
        }
    }
    
    private static String resolveName(Class<?> c) {
        //c.getName().replace("$", "_") + IMPLEMENTATION_SUFFIX;
        var a = c.getAnnotation(JStach.class);
        String cname;
        if (a != null && ! ":auto".equals(a.adapterName())) {
            cname = a.adapterName();
        }
        else {
            cname = c.getSimpleName() + IMPLEMENTATION_SUFFIX;
        }
        String packageName = c.getPackageName();
        String fqn = packageName + (packageName.isEmpty() ? "" : ".")  + cname;
        return fqn;
    }

//    public static <T> Class< ? extends Renderer<?>> getRendererClass(Class<T> clazz) {
//        try {
//            List<ClassLoader> classLoaders = collectClassLoaders( clazz.getClassLoader() );
//
//            return getRendererClass( clazz, classLoaders );
//        }
//        catch ( ClassNotFoundException e ) {
//            throw new RuntimeException( e );
//        }
//    }

//    private static <T> Class<? extends Renderer<?>> getRendererClass(Class<T> mapperType, Iterable<ClassLoader> classLoaders)
//        throws ClassNotFoundException {
//
//        for ( ClassLoader classLoader : classLoaders ) {
//            Class<? extends Renderer<?>> mapperClass = doGetRendererClass( mapperType, classLoader );
//            if ( mapperClass != null ) {
//                return mapperClass;
//            }
//        }
//
//        throw new ClassNotFoundException( "Cannot find implementation for " + mapperType.getName() );
//    }

//    @SuppressWarnings("unchecked")
//    private static <T> Class<? extends Renderer<?>> doGetRendererClass(Class<T> clazz, ClassLoader classLoader) {
//        try {
//            return (Class<? extends Renderer<?>>) classLoader.loadClass( clazz.getName() + IMPLEMENTATION_SUFFIX );
//        }
//        catch ( ClassNotFoundException e ) {
//            T mapper = getRendererFromServiceLoader( clazz, classLoader );
//            if ( mapper != null ) {
//                return (Class<? extends Renderer<?>>) mapper.getClass();
//            }
//
//            return null;
//        }
//    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static <T> Renderer<?> getRendererFromServiceLoader(Class<T> clazz, ClassLoader classLoader) {
        ServiceLoader<Renderer> loader = ServiceLoader.load( Renderer.class, classLoader );

        for ( Renderer mapper : loader ) {
            if ( mapper != null && mapper.supportsType(clazz)) {
                return mapper;
            }
        }

        return null;
    }

    private static List<ClassLoader> collectClassLoaders(ClassLoader classLoader) {
        List<ClassLoader> classLoaders = new ArrayList<>( 3 );
        classLoaders.add( classLoader );

        if ( Thread.currentThread().getContextClassLoader() != null ) {
            classLoaders.add( Thread.currentThread().getContextClassLoader() );
        }

        classLoaders.add( Renderers.class.getClassLoader() );

        return classLoaders;
    }
}