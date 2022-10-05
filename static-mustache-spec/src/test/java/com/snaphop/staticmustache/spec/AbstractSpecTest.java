package com.snaphop.staticmustache.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import java.util.function.Function;

import org.junit.Test;

public abstract class AbstractSpecTest<T extends SpecListing> {
    
    @Test
    public void testRender() throws Exception {
        var specItem = specItem();
        testSpec(specItem, this::render);

    }

    public static <T extends SpecListing> void testSpec(T specItem, Function<T, String> renderFunction) {
        assumeTrue("Test is purposely disabled", specItem.enabled());
        String expected = specItem.expected();
        String actual = renderFunction.apply(specItem);
        boolean failed = true;
        try {
            assertEquals(specItem.description(), expected, actual);
            failed = false;
        } finally {
            if (failed) {
                System.out.print(specItem.describe());
                System.out.println("<actual>" + actual + "</actual>\n");
            }
        }
    }
    
    public static <T extends SpecListing> void testSpec(T specItem, String actual) {
        testSpec(specItem, (t) -> actual);
    }
    
    abstract String render(T item);
    
    abstract T specItem();

}
