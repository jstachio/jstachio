package com.github.sviperll.staticmustache.examples;

import static java.util.Objects.requireNonNull;
import static org.junit.Assert.assertEquals;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import com.github.sviperll.staticmustache.text.RenderFunction;

public class JMustacheCompareTest {

    int[][] array = new int[][] { new int[] { 1, 2, 3, 4, 5 }, new int[] { 1, 2, 3, 4, 5 }, new int[] { 1, 2, 3, 4, 5 },
            new int[] { 1, 2, 3, 4, 5 }, new int[] { 1, 2, 3, 4, 5 } };
    List<User1.Item<String>> list1 = new ArrayList<User1.Item<String>>();
    {
        list1.add(new User1.Item<String>("abc"));
        list1.add(new User1.Item<String>("def"));
    }

    @Test
    public void testUserJMustache() throws Exception {

        JMustacheRenderService.setEnabled(true);
        try {
            PrintStream out = requireNonNull(System.out);
            if (out == null)
                throw new IllegalStateException();
            User1 user2 = new User1("Victor", 29, new String[] { "aaa", "bbb", "ccc" }, array, list1);

            RenderableHtmlUser1Adapter.of(user2).render(out);
        } finally {
            JMustacheRenderService.setEnabled(false);
        }
    }
    
    @Test
    public void testPage() throws Exception {
        UUID testId = UUID.nameUUIDFromBytes("test".getBytes());
        var page = new PageContainer(new IdContainer(testId),
                new Blog(List.of(new Post("Maverick", new IdContainer(testId)),
                        new Post("Ice Man", new IdContainer(testId)), new Post("Goose", new IdContainer(testId))

                )));
        RenderFunction render = PageContainerRenderer.of(page);
        String sm = normalize(render.renderString());
        String jm = normalize(jmustacheRender(page));
        System.out.println(sm);
        assertEquals(jm, sm);
    }
    
    private String normalize(String out) {
        return out.replaceAll("\\n+", "\n");
    }
    private String jmustacheRender(PageContainer page) {
        try {
            JMustacheRenderService.setEnabled(true);
            RenderFunction render = PageContainerRenderer.of(page);
            return render.renderString();
        } finally {
            JMustacheRenderService.setEnabled(false);
        }
    }


}
