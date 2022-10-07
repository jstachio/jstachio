package com.snaphop.staticmustache.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import java.util.function.BiFunction;
import java.util.function.Function;

import org.junit.Test;

public abstract class AbstractSpecTest<T extends SpecListing> {
    
    @Test
    public void testRender() throws Exception {
        var specItem = specItem();
        testSpec(specItem, this::render, (r,t) -> this.adjustResult(specItem, r, t));

    }

    public static <T extends SpecListing> void testSpec(T specItem, 
            Function<T, String> renderFunction,
            BiFunction<String, Result, String> adjust) {
        assumeTrue("Test is purposely disabled", specItem.enabled());
        String expected =  adjust.apply(specItem.expected(), Result.EXPECTED);
        boolean failed = true;
        String actual = "EXCEPTION THROWN";
        try {
            actual = adjust.apply(renderFunction.apply(specItem), Result.ACTUAL);
            assertEquals(specItem.description(), expected, actual);
            failed = false;
        } finally {
            if (failed) {
                System.out.print(specItem.describe());
                System.out.println("<actual>" + actual + "</actual>\n");
            }
        }
    }
    
    public enum Result {
        EXPECTED,
        ACTUAL
    }
    
    protected String adjustResult(T specItem, String result, Result type) {
        return result;
    }

    
    public static <T extends SpecListing> void testSpec(T specItem, String actual) {
        testSpec(specItem, (t) -> actual, (s,r) -> s);
    }
    
    String render(T item) {
        return item.render();
    }
    
    abstract T specItem();

}
