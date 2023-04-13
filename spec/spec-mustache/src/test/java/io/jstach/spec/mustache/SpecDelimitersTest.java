package io.jstach.spec.mustache;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import io.jstach.spec.mustache.spec.delimiters.DelimitersSpecTemplate;

@RunWith(Parameterized.class)
public class SpecDelimitersTest extends AbstractSpecTest<DelimitersSpecTemplate> {

	@Parameters(name = "{1}")
	public static Collection<Object[]> data() {
		return EnumSet.allOf(DelimitersSpecTemplate.class).stream().map(t -> new Object[] { t, t.title() }).toList();
	}

	private final DelimitersSpecTemplate specItem;

	public SpecDelimitersTest(DelimitersSpecTemplate specItem, String name) {
		this.specItem = specItem;
	}

	@Override
	DelimitersSpecTemplate specItem() {
		return this.specItem;
	}

}
