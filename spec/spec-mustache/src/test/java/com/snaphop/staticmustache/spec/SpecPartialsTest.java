package com.snaphop.staticmustache.spec;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import io.jstach.spec.mustache.spec.partials.PartialsSpecTemplate;

@RunWith(Parameterized.class)
public class SpecPartialsTest extends AbstractSpecTest<PartialsSpecTemplate> {

	@Parameters(name = "{1}")
	public static Collection<Object[]> data() {
		return EnumSet.allOf(PartialsSpecTemplate.class).stream().map(t -> new Object[] { t, t.title() }).toList();
	}

	private final PartialsSpecTemplate specItem;

	public SpecPartialsTest(PartialsSpecTemplate specItem, String name) {
		this.specItem = specItem;
	}

	@Override
	PartialsSpecTemplate specItem() {
		return this.specItem;
	}

	String render(PartialsSpecTemplate specTemplate) {
		return specTemplate.render();
	}

}
