
@net.java.dev.hickory.prism.GeneratePrisms({ //
    @GeneratePrism(value = io.jstach.annotation.JStachRenderers.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStach.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStachBasePath.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.TemplateInterface.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStachPartialMapping.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStachPartial.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStachLambda.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStachFormatterTypes.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.JStachFlags.class, publicAccess = true), //

})
@org.eclipse.jdt.annotation.NonNullByDefault({DefaultLocation.TYPE_ARGUMENT})
package io.jstach.apt.prism;
import org.eclipse.jdt.annotation.DefaultLocation;

import net.java.dev.hickory.prism.GeneratePrism;