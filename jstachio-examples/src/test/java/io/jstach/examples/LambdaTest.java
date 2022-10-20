package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.jstach.annotation.JStache;
import io.jstach.annotation.JStacheLambda;

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
    
    static final String lambdaList = """
            {{#list}}
            {{#lambda}}{{item}}{{/lambda}}
            {{/list}}
            """;
    
    @JStache(template=lambdaList)
    public record LambdaList(List<String> list) {
        
        @JStacheLambda
        public LambdaModel lambda(String input, String item) {
            return new LambdaModel(item);
        }
    }
    
    public record LambdaModel(String item) {}
    
    @Test
    public void testName() throws Exception {
        
    }

}
