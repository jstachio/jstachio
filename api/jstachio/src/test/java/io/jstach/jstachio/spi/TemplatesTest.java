package io.jstach.jstachio.spi;

import static org.junit.Assert.*;

import org.junit.Test;

import io.jstach.jstache.JStache;

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

}
