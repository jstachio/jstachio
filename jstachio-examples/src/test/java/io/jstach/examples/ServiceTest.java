package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import io.jstach.JStachio;
import io.jstach.annotation.JStache;

public class ServiceTest {
    
    @Test
    public void testFindRenderer() throws IOException {
        String actual = JStachio.render(new InlineExample("Blah"));
        assertEquals("Hello Blah!", actual);
    }
    
    @JStache(template="Hello {{name}}")
    public record SomeView(String name) {}
    
//    @Test
//    public void testMapper() throws Exception {
//        //var e = Mappers.getMapper(MapStructExample.class);
//        var e = MapStructExample.of();
//    }

}
