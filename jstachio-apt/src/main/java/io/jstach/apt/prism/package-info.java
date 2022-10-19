
@net.java.dev.hickory.prism.GeneratePrisms({ //
    @GeneratePrism(value = io.jstach.annotation.JStaches.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStache.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStacheBasePath.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.TemplateInterface.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStachePartialMapping.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStachePartial.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStacheLambda.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStacheFormatterTypes.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStacheFlags.class, publicAccess = true), //

})
@org.eclipse.jdt.annotation.NonNullByDefault({DefaultLocation.TYPE_ARGUMENT})
package io.jstach.apt.prism;
import org.eclipse.jdt.annotation.DefaultLocation;

import net.java.dev.hickory.prism.GeneratePrism;