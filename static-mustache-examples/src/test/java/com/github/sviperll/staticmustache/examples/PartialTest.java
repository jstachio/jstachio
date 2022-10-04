package com.github.sviperll.staticmustache.examples;

import static org.junit.Assert.*;

import org.junit.Test;

public class PartialTest {

    @Test
    public void testPartial() throws Exception {
        PartialExample pe = new PartialExample("Joe");
        var actual = PartialExampleRenderer.of(pe).renderString();

        assertEquals("""
                start partial parent
                Hello Joe!
                now from child
                INCLUDE start
                Hello Joe from INCLUDE!
                GRAND CHILD start
                PARAM message from partial-include.mustache
                GRAND CHILD end
                CHILD local message
                no replace
                INCLUDE end
                end partial parent""", actual);
    }

}
