package io.jstach.examples.reflect;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStacheConfig;
import io.jstach.jstache.JStacheName;
import io.jstach.jstachio.Template;
import io.jstach.jstachio.TemplateInfo;
import io.jstach.jstachio.escapers.PlainText;
import io.jstach.jstachio.spi.Templates;

public class TemplatesTest {

	@JStache(template = "Hello")
	public record UsePackage() {
	}

	@JStache
	public record UseClassName() {
	}

	@JStache(path = "use-resource")
	public record UseResource() {
	}

	@JStacheConfig(naming = @JStacheName(prefix = "asdfasdf"))
	public record Config() {

	}

	@Test
	public void testTemplateInfoReflection() throws Exception {
		TemplateInfo b = Templates.getInfoByReflection(UsePackage.class);
		Template<UsePackage> a = Templates.getTemplate(UsePackage.class);
		assertTemplateEquals(a, b);

		assertEquals(a.templateContentType(), PlainText.class);
		assertEquals("UsePackageBlah", a.getClass().getSimpleName());
	}

	@Test
	public void testUseClassName() throws Exception {
		TemplateInfo b = Templates.getInfoByReflection(UseClassName.class);
		Template<UseClassName> a = Templates.getTemplate(UseClassName.class);
		assertTemplateEquals(a, b);

		assertEquals(a.templateContentType(), PlainText.class);
		assertEquals("UseClassNameBlah", a.getClass().getSimpleName());
	}

	@Test
	public void testUseResource() throws Exception {
		TemplateInfo b = Templates.getInfoByReflection(UseResource.class);
		Template<UseResource> a = Templates.getTemplate(UseResource.class);
		assertTemplateEquals(a, b);
	}

	@Test
	public void testName() throws Exception {
		var jc = Config.class.getAnnotation(JStacheConfig.class);
		var naming = jc.naming();

		System.out.println(List.of(naming));

		System.out.println(Config.class.getAnnotation(JStacheName.class));
	}

	public static void assertTemplateEquals(TemplateInfo a, TemplateInfo b) {
		assertEquals("templateName", a.templateName(), b.templateName());
		assertEquals("templatePath", a.templatePath(), b.templatePath());
		assertEquals("contentType", a.templateContentType(), b.templateContentType());
		assertEquals("escaper", a.templateEscaper(), b.templateEscaper());
		assertEquals("formatter", a.templateFormatter(), b.templateFormatter());
	}

}
