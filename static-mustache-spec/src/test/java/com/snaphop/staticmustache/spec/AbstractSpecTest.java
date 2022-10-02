package com.snaphop.staticmustache.spec;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public abstract class AbstractSpecTest<T extends SpecListing> {
    
    @Test
    public void testRender() throws Exception {
        var specItem = specItem();
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
