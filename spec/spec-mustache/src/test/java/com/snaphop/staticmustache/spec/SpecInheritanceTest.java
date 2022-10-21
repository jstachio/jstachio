package com.snaphop.staticmustache.spec;

import static org.junit.Assume.assumeTrue;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import io.jstach.spec.mustache.spec.inheritance.InheritanceSpecTemplate;

@RunWith(Parameterized.class)
public class SpecInheritanceTest extends AbstractSpecTest<InheritanceSpecTemplate> {

	@Parameters(name = "{1}")
	public static Collection<Object[]> data() {
		return EnumSet.allOf(InheritanceSpecTemplate.class).stream().map(t -> new Object[] { t, t.title() }).toList();
	}

	private final InheritanceSpecTemplate specItem;

	public SpecInheritanceTest(InheritanceSpecTemplate specItem, String name) {
		this.specItem = specItem;
	}

	@Override
	InheritanceSpecTemplate specItem() {
		return this.specItem;
	}

	@Override
	protected String adjustResult(InheritanceSpecTemplate specItem, String result, Result type) {
		return switch (specItem) {
			case INHERIT -> result.trim();
			case BLOCK_SCOPE -> ignoreTemplate("Do not correctly support block scope");
			case OVERRIDE_PARENT_WITH_NEWLINES -> result.trim();
			default -> result;
		};
	}

	private String ignoreTemplate(String message) {
		assumeTrue(message, false);
		return "FAIL";
	}

}
