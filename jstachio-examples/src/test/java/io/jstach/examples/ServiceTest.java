package io.jstach.examples;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import io.jstach.annotation.JStach;
import io.jstach.spi.JStachServices;

public class ServiceTest {
    
    @Test
    public void testFindRenderer() throws IOException {
        String actual = JStachServices.renderer(InlineExample.class).render(new InlineExample("Blah"));
        assertEquals("Hello Blah!", actual);
    }
    
    @JStach(template="Hello {{name}}")
    public record SomeView(String name) {}
    
//    @Test
//    public void testMapper() throws Exception {
//        //var e = Mappers.getMapper(MapStructExample.class);
//        var e = MapStructExample.of();
//    }

}
