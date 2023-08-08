package io.jstach.jstachio.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import io.jstach.jstache.JStache;
import io.jstach.jstache.JStachePath;

public class TemplatesTest {

	@Test
	public void shouldFindAnnotatedParentInterface() {
		var stache = Templates.findJStacheOrNull(ModelNoJStache.class);
		assertNotNull(stache);
		assertEquals(stache.getKey(), InterfaceModel.class);
	}

	@Test
	public void shouldFindAnnotated() {
		var stache = Templates.findJStacheOrNull(ModelWithJStache.class);
		assertNotNull(stache);
		assertEquals(stache.getKey(), ModelWithJStache.class);
	}

	@Test
	public void shouldFindSuperClassBeforeInterface() {
		var stache = Templates.findJStache(ConcreteModel.class);
		assertNotNull(stache);
		assertEquals(stache.getKey(), AbstractModel.class);
	}

	@Test(expected = TemplateNotFoundException.class)
	public void shouldNotFindJStacheAndThrowTemplateNotFound() throws Exception {
		Templates.findJStache(NoJStache.class);
	}

	@Test
	public void shouldFindPathInfoWithMustacheSuffix() throws Exception {
		var info = Templates.getInfoByReflection(ModelWithJStache.class);
		String path = info.templatePath();
		assertEquals("io/jstach/jstachio/spi/ModelWithJStache.mustache", path);
	}

	@Test
	public void shouldFindPathInfoWithMustacheSuffixForSuper() throws Exception {
		var stache = Templates.findJStache(ConcreteModel.class);
		var superClass = stache.getKey();
		var info = Templates.getInfoByReflection(superClass);
		String path = info.templatePath();
		assertEquals("io/jstach/jstachio/spi/AbstractModel.mustache", path);
	}

	@Test
	public void shouldSuffixPathWithMustacheIfNoSuffix() throws Exception {
		var info = Templates.getInfoByReflection(ModelWithPath.class);
		String path = info.templatePath();
		assertEquals("stuff", path);
	}

	@Test
	public void shouldPrefixAndSuffixIfPathConfigGiven() throws Exception {
		var info = Templates.getInfoByReflection(ModelWithSuffixPrefix.class);
		String path = info.templatePath();
		assertEquals("prefix/io/jstach/jstachio/spi/ModelWithSuffixPrefix/suffix.mustache", path);
	}

	@JStache
	interface InterfaceModel {

		String message();

	}

	@JStache
	static class AbstractModel {

	}

	static class ConcreteModel extends AbstractModel implements InterfaceModel {

		@Override
		public String message() {
			return "";
		}

	}

	record NoJStache() {
	}

	record ModelNoJStache(String message) implements InterfaceModel {

	}

	@JStache
	record ModelWithJStache(String message) implements InterfaceModel {

	}

	@JStache(path = "stuff")
	record ModelWithPath(String message) implements InterfaceModel {

	}

	@JStachePath(prefix = "prefix/", suffix = "/suffix.mustache")
	@JStache
	record ModelWithSuffixPrefix(String message) implements InterfaceModel {

	}

}