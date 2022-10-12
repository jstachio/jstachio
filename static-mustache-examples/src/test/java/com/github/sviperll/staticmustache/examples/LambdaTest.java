package com.github.sviperll.staticmustache.examples;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class LambdaTest {
    
    @Test
    public void testLambda() {
        LambdaExample example = new LambdaExample("stuff");
        var actual = LambdaExampleRenderer.of(example).renderString();
        // We do not interpolate the results so the below is expected
        assertEquals("<hello>Adam {{name}} </hello>", actual);
    }

}
