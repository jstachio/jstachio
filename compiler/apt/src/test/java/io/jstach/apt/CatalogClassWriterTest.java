package io.jstach.apt;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import io.jstach.apt.internal.ProcessingException;

public class CatalogClassWriterTest {

	@Test
	public void testWrite() throws IOException, ProcessingException {
		StringBuilder sb = new StringBuilder();
		CatalogClassWriter w = new CatalogClassWriter("com.company", "MyCatalog");
		w.addTemplateClasses(List.of("com.company.tmp.MyTemplate"));
		w.addTemplateClasses(List.of("com.company.tmp.MyTemplate"));
		w.addTemplateClasses(List.of("com.company.tmp.AnotherTemplate"));
		w.write(sb);

		String expected = """
				package com.company;

				/**
				 * Generated template catalog.
				 */
				public class MyCatalog implements io.jstach.jstachio.spi.TemplateProvider.GeneratedTemplateProvider {

				    /**
				     * Generated template catalog constructor for ServiceLoader.
				     */
				    public MyCatalog() {
				    }

				    @Override
				    public java.util.List<io.jstach.jstachio.Template<?>> provideTemplates(io.jstach.jstachio.TemplateConfig templateConfig) {
				        return java.util.List.of(//
				        new com.company.tmp.AnotherTemplate(templateConfig), //
				        new com.company.tmp.MyTemplate(templateConfig));
				    }
				}
				""";
		String actual = sb.toString();

		assertEquals(expected, actual);
	}

}
