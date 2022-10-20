package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import io.jstach.JStachio;
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
    
    /*
     * { "items" : [ 1, 2, 3], "lambda" : function(input, item) {...no analog in current spec..} }
     */
    static final String template = """
            {{#items}}{{#lambda}}{{item}} is {{stripe}}{{/lambda}}{{/items}}""";

    @JStache(template=template)
    public record Items(List<Integer> items) {
        @JStacheLambda
        public LambdaModel lambda(Integer item) {
            return new LambdaModel(item, item % 2 == 0 ? "even" : "odd");
        }
    }
    /*
     * In jstachio if you return an object it is then pushed on the stack
     * and the contents of the of the lambda block are used as a sort of inline partial.
     * 
     * This is in large part because we cannot handle dynamic templates and also
     * because I think it is the correct usage is as the caller knows how it wants to render
     * things and avoids the whole delimeter nightmare.
     */
    public record LambdaModel(Integer item, String stripe) {}
    
    @Test
    public void testName() throws Exception {
        String expected = "5 is odd";
        String actual = JStachio.render(new Items(List.of(5)));
        assertEquals(expected, actual);
    }

}
