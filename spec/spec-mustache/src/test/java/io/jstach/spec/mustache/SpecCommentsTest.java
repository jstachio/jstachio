package io.jstach.spec.mustache;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import io.jstach.spec.mustache.spec.comments.CommentsSpecTemplate;

@RunWith(Parameterized.class)
public class SpecCommentsTest extends AbstractSpecTest<CommentsSpecTemplate> {

	@Parameters(name = "{1}")
	public static Collection<Object[]> data() {
		return EnumSet.allOf(CommentsSpecTemplate.class).stream().map(t -> new Object[] { t, t.title() }).toList();
	}

	private final CommentsSpecTemplate specItem;

	public SpecCommentsTest(CommentsSpecTemplate specItem, String name) {
		this.specItem = specItem;
	}

	@Override
	CommentsSpecTemplate specItem() {
		return this.specItem;
	}

}
