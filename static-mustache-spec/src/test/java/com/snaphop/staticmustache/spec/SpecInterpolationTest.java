package com.snaphop.staticmustache.spec;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.snaphop.staticmustache.spec.interpolation.InterpolationSpecTemplate;

@RunWith(Parameterized.class)
public class SpecInterpolationTest {

    @Parameters(name = "{1}")
    public static Collection<Object[]> data() {
        return EnumSet.allOf(InterpolationSpecTemplate.class).stream().map(t -> new Object[] { t, t.title() }).toList();
    }
    
    private final InterpolationSpecTemplate specItem;
    
    public SpecInterpolationTest(InterpolationSpecTemplate specItem, String name) {
        this.specItem = specItem;
    }
    
    @Test
    public void testRender() throws Exception {
        String actual = specItem.render();
        String expected = specItem.expected();
        assertEquals(expected, actual);
    }
    
    SpecListing replaceIfNeeded(InterpolationSpecTemplate specTemplate) {
        return switch(specTemplate) {
        default -> specTemplate;
        };
    }
    
}
