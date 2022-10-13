package com.github.sviperll.staticmustache.examples;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Test;

public class LambdaTest {

    @Test
    public void testLambda() {
        // Funny story... Map.of does not consistent ordering
        // Map.of("Color", "Red", "Food", "Pizza", "Speed", "Fast")
        Map<String, String> m = new LinkedHashMap<>();
        m.put("Color", "Red");
        m.put("Food", "Pizza");
        m.put("Speed", "Fast");
        LambdaExample example = new LambdaExample("stuff", m);

        var actual = LambdaExampleRenderer.of(example).renderString();
        // We do not interpolate the results so the below is expected
        String expected = """
                <hello>Adam {{name}} </hello>: stuff

                Color : Red
                Food : Pizza
                Speed : Fast

                Key: Color, Value: Red
                Key: Food, Value: Pizza
                Key: Speed, Value: Fast

                                """;
        assertEquals(expected, actual);
    }

}
