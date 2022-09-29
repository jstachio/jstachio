package com.snaphop.staticmustache.spec;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.AssumptionViolatedException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.snaphop.staticmustache.spec.custom.Custom;
import com.snaphop.staticmustache.spec.custom.Custom.Person;
import com.snaphop.staticmustache.spec.custom.DottedNamesAmpersandInterpolationRenderer;
import com.snaphop.staticmustache.spec.custom.DottedNamesBasicInterpolationRenderer;
import com.snaphop.staticmustache.spec.custom.DottedNamesTripleMustacheInterpolationRenderer;
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
        String expected = specItem.expected();
        String json = specItem.json();
        String actual = render(specItem);
        boolean failed = true;
        try {
            assertEquals(json, expected, actual);
            failed = false;
        } finally {
            if (failed) {
                String message = String.format("""
                        Error.
                        <name>%s</name>
                        <template>
                        %s
                        </template>
                        <json>
                        %s
                        </json>
                        """, specItem.name(), "", json);

                System.out.println(message);
            }
        }
    }
    
    String render(InterpolationSpecTemplate specTemplate) {
        return switch(specTemplate) {
        case DOTTED_NAMES___BASIC_INTERPOLATION -> 
            DottedNamesBasicInterpolationRenderer.of(new Custom.DottedNamesBasicInterpolation(Person.Joe)).renderString();
        case DOTTED_NAMES___AMPERSAND_INTERPOLATION ->
           DottedNamesAmpersandInterpolationRenderer.of(new Custom.DottedNamesAmpersandInterpolation(Person.Joe)).renderString();
        case DOTTED_NAMES___TRIPLE_MUSTACHE_INTERPOLATION -> 
           DottedNamesTripleMustacheInterpolationRenderer.of(new Custom.DottedNamesTripleMustacheInterpolation(Person.Joe)).renderString();
        case DOTTED_NAMES___ARBITRARY_DEPTH ->
            throw new AssumptionViolatedException("Test not written yet");
        case DOTTED_NAMES___INITIAL_RESOLUTION ->
            throw new AssumptionViolatedException("Test not written yet");
        default -> specTemplate.render();
        };
    }
    
}
