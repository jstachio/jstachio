package io.jstach.jstachio.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.nio.charset.Charset;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import org.eclipse.jdt.annotation.Nullable;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import io.jstach.jstachio.TemplateInfo;

@RunWith(Parameterized.class)
public class CacheTemplateFinderTest {

	private FinderTest test;

	public CacheTemplateFinderTest(FinderTest test) {
		this.test = test;
	}

	@SuppressWarnings("nullness") // wtf checker does not like assertNull
	@Test
	public void testFindOrNull() {
		var finder = test.counting();
		var cached = JStachioTemplateFinder.cachedTemplateFinder(finder);
		for (int i = 0; i < 3; i++) {
			var templateInfo = cached.findOrNull(UnsupportedType.class);
			if (test.expectMissing()) {
				assertNull(templateInfo);
			}
			else {
				assertNotNull(templateInfo);
			}
		}
		int actual = finder.count.get();
		if (test.expectCache()) {
			int expected = 1;
			assertEquals("should have cached", expected, actual);
		}
		else {
			int expected = 3;
			assertEquals(expected, actual);
		}
	}

	@Test
	public void testFindTemplate() {
		var finder = test.counting();
		var cached = JStachioTemplateFinder.cachedTemplateFinder(finder);
		for (int i = 0; i < 3; i++) {
			try {
				cached.findTemplate(UnsupportedType.class);
				if (test.expectMissing()) {
					fail("should have failed");
				}
			}
			catch (Exception e) {
				// e.printStackTrace();
				if (e instanceof TemplateNotFoundException te) {
					assertEquals(UnsupportedType.class, te.modelType());
				}
				else {
					if (test.expectCache()) {
						fail("Expected template not found exception");
					}
				}
			}
		}
		int actual = finder.count.get();
		if (test.expectCache()) {
			int expected = 1;
			assertEquals("should have cached", expected, actual);
		}
		else {
			int expected = 3;
			assertEquals(expected, actual);
		}
	}

	public enum FinderTest {

		ALWAYS_MISSING() {
			@Override
			protected JStachioTemplateFinder create() {
				return t -> {
					throw new TemplateNotFoundException(t);
				};
			}
		},
		ERROR_NO_CACHE() {
			@Override
			protected JStachioTemplateFinder create() {
				return t -> {
					throw new Exception("Some Bad Error");
				};
			}

			@Override
			public boolean expectCache() {
				return false;
			}

		},
		DEFAULT() {
			@Override
			protected JStachioTemplateFinder create() {
				return new DefaultTemplateFinder(s -> null);
			}

		},
		DEFAULT_REFLECTION_DISABLED() {

			@Override
			protected JStachioTemplateFinder create() {
				return new DefaultTemplateFinder(s -> {
					return switch (s) {
						case JStachioConfig.REFLECTION_TEMPLATE_DISABLE -> "true";
						default -> null;
					};
				});

			}

		},
		DEFAULT_SERVICE_LOADER_AND_REFLECTION_DISABLED() {

			@Override
			protected JStachioTemplateFinder create() {
				return new DefaultTemplateFinder(s -> {
					return switch (s) {
						case JStachioConfig.REFLECTION_TEMPLATE_DISABLE -> "true";
						case JStachioConfig.SERVICELOADER_TEMPLATE_DISABLE -> "true";
						default -> null;
					};
				});
			}

		},
		FOUND_TEMPLATE() {
			@Override
			protected JStachioTemplateFinder create() {
				return t -> {
					return FakeTemplateInfo.FAKE;
				};
			}

			@Override
			public boolean expectMissing() {
				return false;
			}
		};

		private FinderTest() {
		}

		public CountingTemplateFinder counting() {
			return CountingTemplateFinder.of(create());
		}

		protected abstract JStachioTemplateFinder create();

		public boolean expectCache() {
			return true;
		}

		public boolean expectMissing() {
			return true;
		}

	}

	@SuppressWarnings("null")
	@Parameters(name = "{0}")
	public static Object[] data() {
		return FinderTest.values();
	}

	record UnsupportedType() {
	}

	public static abstract class CountingTemplateFinder implements JStachioTemplateFinder {

		AtomicInteger count = new AtomicInteger();

		@Override
		public TemplateInfo findTemplate(Class<?> modelType) throws Exception {
			count.incrementAndGet();
			return doFind(modelType);
		}

		abstract TemplateInfo doFind(Class<?> modelType) throws Exception;

		public static CountingTemplateFinder of(JStachioTemplateFinder templateFinder) {
			return new CountingTemplateFinder() {
				@Override
				TemplateInfo doFind(Class<?> modelType) throws Exception {
					return templateFinder.findTemplate(modelType);
				}
			};
		}

	}

	enum FakeTemplateInfo implements TemplateInfo {

		FAKE;

		@Override
		public String templateName() {
			throw new UnsupportedOperationException();
		}

		@Override
		public String templatePath() {
			throw new UnsupportedOperationException();

		}

		@Override
		public Class<?> templateContentType() {
			throw new UnsupportedOperationException();

		}

		@Override
		public Charset templateCharset() {
			throw new UnsupportedOperationException();

		}

		@Override
		public String templateMediaType() {
			throw new UnsupportedOperationException();
		}

		@Override
		public Function<String, String> templateEscaper() {
			throw new UnsupportedOperationException();

		}

		@Override
		public Function<@Nullable Object, String> templateFormatter() {
			throw new UnsupportedOperationException();

		}

		@Override
		public boolean supportsType(Class<?> type) {
			throw new UnsupportedOperationException();

		}

		@Override
		public Class<?> modelClass() {
			throw new UnsupportedOperationException();

		}

	};

}
