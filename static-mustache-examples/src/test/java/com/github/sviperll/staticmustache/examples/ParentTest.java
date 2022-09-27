package com.github.sviperll.staticmustache.examples;

import static org.junit.Assert.*;

import org.junit.Test;

public class ParentTest {

    @Test
    public void testParent() throws Exception {
        Parent parent = new Parent("hello 1");
        String actual = ParentRenderer.of(parent).renderString();
        String expected = """
                before child
                GREETINGS FROM CHILD with:
                Surprise hello 1!
                after child""";
        assertEquals(expected, actual);
    }

    @Test
    public void testParent2() throws Exception {
        Parent2 parent2 = new Parent2("hello 2");
        String actual = Parent2Renderer.of(parent2).renderString();
        String expected = """
                before child
                GREETINGS FROM CHILD with:
                Oh you are being quiet!
                after child""";
        assertEquals(expected, actual);
    }

}
