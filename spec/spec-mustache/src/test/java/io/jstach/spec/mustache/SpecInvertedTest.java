package io.jstach.spec.mustache;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import io.jstach.spec.mustache.spec.inverted.InvertedSpecTemplate;

@RunWith(Parameterized.class)
public class SpecInvertedTest extends AbstractSpecTest<InvertedSpecTemplate> {

	@Parameters(name = "{1}")
	public static Collection<Object[]> data() {
		return EnumSet.allOf(InvertedSpecTemplate.class).stream().map(t -> new Object[] { t, t.title() }).toList();
	}

	private final InvertedSpecTemplate specItem;

	public SpecInvertedTest(InvertedSpecTemplate specItem, String name) {
		this.specItem = specItem;
	}

	@Override
	InvertedSpecTemplate specItem() {
		return this.specItem;
	}

	String render(InvertedSpecTemplate specTemplate) {
		return specTemplate.render();
	}

}
