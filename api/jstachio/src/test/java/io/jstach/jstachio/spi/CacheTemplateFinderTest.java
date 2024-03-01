package io.jstach.jstachio.spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.concurrent.atomic.AtomicInteger;

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

	@Test
	public void testFindOrNull() {
		var finder = test.counting();
		var cached = JStachioTemplateFinder.cachedTemplateFinder(finder);
		cached.findOrNull(UnsupportedType.class);
		cached.findOrNull(UnsupportedType.class);
		cached.findOrNull(UnsupportedType.class);
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
				fail("should have failed");
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

}
