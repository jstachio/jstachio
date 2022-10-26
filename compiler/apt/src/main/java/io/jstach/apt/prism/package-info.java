
@net.java.dev.hickory.prism.GeneratePrisms({ //
		@GeneratePrism(value = io.jstach.annotation.JStaches.class, publicAccess = true), //
		@GeneratePrism(value = io.jstach.annotation.JStache.class, publicAccess = true), //
		@GeneratePrism(value = io.jstach.annotation.JStacheContentType.class, publicAccess = true), //
		@GeneratePrism(value = io.jstach.annotation.JStachePath.class, publicAccess = true), //
		@GeneratePrism(value = io.jstach.annotation.JStacheInterfaces.class, publicAccess = true), //
		@GeneratePrism(value = io.jstach.annotation.JStachePartials.class, publicAccess = true), //
		@GeneratePrism(value = io.jstach.annotation.JStachePartial.class, publicAccess = true), //
		@GeneratePrism(value = io.jstach.annotation.JStacheLambda.class, publicAccess = true), //
		@GeneratePrism(value = io.jstach.annotation.JStacheLambda.Raw.class, publicAccess = true), //
		@GeneratePrism(value = io.jstach.annotation.JStacheFormatterTypes.class, publicAccess = true), //
		@GeneratePrism(value = io.jstach.annotation.JStacheFlags.class, publicAccess = true), //

})
@org.eclipse.jdt.annotation.NonNullByDefault({ DefaultLocation.TYPE_ARGUMENT })
package io.jstach.apt.prism;

import org.eclipse.jdt.annotation.DefaultLocation;

import net.java.dev.hickory.prism.GeneratePrism;