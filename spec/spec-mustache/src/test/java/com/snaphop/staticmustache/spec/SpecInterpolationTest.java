package com.snaphop.staticmustache.spec;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.AssumptionViolatedException;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import io.jstach.spec.mustache.spec.custom.Custom;
import io.jstach.spec.mustache.spec.custom.DottedNamesAmpersandInterpolationRenderer;
import io.jstach.spec.mustache.spec.custom.DottedNamesBasicInterpolationRenderer;
import io.jstach.spec.mustache.spec.custom.DottedNamesTripleMustacheInterpolationRenderer;
import io.jstach.spec.mustache.spec.custom.Custom.Person;
import io.jstach.spec.mustache.spec.interpolation.InterpolationSpecTemplate;

@RunWith(Parameterized.class)
public class SpecInterpolationTest extends AbstractSpecTest<InterpolationSpecTemplate> {

	@Parameters(name = "{1}")
	public static Collection<Object[]> data() {
		return EnumSet.allOf(InterpolationSpecTemplate.class).stream().map(t -> new Object[] { t, t.title() }).toList();
	}

	private final InterpolationSpecTemplate specItem;

	public SpecInterpolationTest(InterpolationSpecTemplate specItem, String name) {
		this.specItem = specItem;
	}

	@Override
	InterpolationSpecTemplate specItem() {
		return this.specItem;
	}

	String render(InterpolationSpecTemplate specTemplate) {
		return switch (specTemplate) {
			case DOTTED_NAMES___BASIC_INTERPOLATION -> DottedNamesBasicInterpolationRenderer
					.of(new Custom.DottedNamesBasicInterpolation(Person.Joe)).renderString();
			case DOTTED_NAMES___AMPERSAND_INTERPOLATION -> DottedNamesAmpersandInterpolationRenderer
					.of(new Custom.DottedNamesAmpersandInterpolation(Person.Joe)).renderString();
			case DOTTED_NAMES___TRIPLE_MUSTACHE_INTERPOLATION -> DottedNamesTripleMustacheInterpolationRenderer
					.of(new Custom.DottedNamesTripleMustacheInterpolation(Person.Joe)).renderString();
			case DOTTED_NAMES___ARBITRARY_DEPTH -> throw new AssumptionViolatedException("Test not written yet");
			case DOTTED_NAMES___INITIAL_RESOLUTION -> throw new AssumptionViolatedException("Test not written yet");
			default -> specTemplate.render();
		};
	}

}
