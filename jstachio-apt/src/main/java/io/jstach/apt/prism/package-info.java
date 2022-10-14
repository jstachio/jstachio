
@net.java.dev.hickory.prism.GeneratePrisms({ //
    @GeneratePrism(value = io.jstach.annotation.GenerateRenderers.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.GenerateRenderer.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.TemplateBasePath.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.TemplateInterface.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.TemplateMapping.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.Template.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.TemplateLambda.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.TemplateFormatterTypes.class, publicAccess = true), //
    @GeneratePrism(value = io.jstach.annotation.TemplateCompilerFlags.class, publicAccess = true), //

})
@org.eclipse.jdt.annotation.NonNullByDefault({DefaultLocation.TYPE_ARGUMENT})
package io.jstach.apt.prism;
import org.eclipse.jdt.annotation.DefaultLocation;

import net.java.dev.hickory.prism.GeneratePrism;