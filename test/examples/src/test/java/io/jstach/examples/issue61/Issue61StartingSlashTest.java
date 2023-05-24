package io.jstach.examples.issue61;

import org.junit.Test;

import io.jstach.examples.reflect.TemplatesTest;
import io.jstach.jstachio.TemplateInfo;
import io.jstach.jstachio.spi.Templates;

public class Issue61StartingSlashTest {

	@Test
	public void testReflection() throws Exception {
		TemplateInfo refInfo = Templates.getInfoByReflection(Issue61Model.class);
		TemplateInfo staticInfo = Issue61ModelRenderer.of();
		System.out.println("" + refInfo);
		System.out.println("" + staticInfo);
		System.out.println("" + staticInfo.description());
		TemplatesTest.assertTemplateEquals(refInfo, staticInfo);

	}

}
