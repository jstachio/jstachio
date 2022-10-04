package com.snaphop.staticmustache.spec;

import static org.junit.Assert.assertEquals;
import static org.junit.Assume.assumeTrue;

import org.junit.Test;

public abstract class AbstractSpecTest<T extends SpecListing> {
    
    @Test
    public void testRender() throws Exception {
        var specItem = specItem();
        assumeTrue("Test is purposely disabled", specItem.enabled());
        String expected = specItem.expected();
        String actual = render(specItem);
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
    
    abstract String render(T item);
    
    abstract T specItem();

}
