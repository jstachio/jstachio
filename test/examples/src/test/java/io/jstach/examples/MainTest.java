package io.jstach.examples;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

public class MainTest {

	PrintStream out = System.out;

	int[][] array = new int[][] { new int[] { 1, 2, 3, 4, 5 }, new int[] { 1, 2, 3, 4, 5 }, new int[] { 1, 2, 3, 4, 5 },
			new int[] { 1, 2, 3, 4, 5 }, new int[] { 1, 2, 3, 4, 5 } };

	List<User1.Item<String>> list1 = new ArrayList<User1.Item<String>>();

	{
		list1.add(new User1.Item<String>("abc"));
		list1.add(new User1.Item<String>("def"));
	}

	@Test
	public void testUser() throws Exception {

		PrintStream out = requireNonNull(System.out);
		if (out == null)
			throw new IllegalStateException();
		User1 user2 = new User1("Victor", 29, new String[] { "aaa", "bbb", "ccc" }, array, list1);

		RenderableHtmlUser1Adapter.of().execute(user2, out);
	}

	@Test
	public void testPage() throws Exception {
		UUID testId = UUID.nameUUIDFromBytes("test".getBytes());
		PageContainerRenderer.of()
				.execute(new PageContainer(new IdContainer(testId),
						new Blog(List.of(new Post("Maverick", new IdContainer(testId)),
								new Post("Ice Man", new IdContainer(testId)), new Post("Goose", new IdContainer(testId))

						))), out);
	}

	@Test
	public void testInline() throws Exception {
		InlineExample inline = new InlineExample("Adam");
		String actual = InlineExampleRenderer.of().execute(inline);
		assertEquals("Hello Adam!", actual);

	}

}
