package com.snaphop.staticmustache.spec;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.snaphop.staticmustache.spec.custom.ContextRenderer;
import com.snaphop.staticmustache.spec.custom.Custom;
import com.snaphop.staticmustache.spec.custom.Custom.Person;
import com.snaphop.staticmustache.spec.sections.SectionsSpecTemplate;

@RunWith(Parameterized.class)
public class SpecSectionsTest {

    @Parameters(name = "{1}")
    public static Collection<Object[]> data() {
        return EnumSet.allOf(SectionsSpecTemplate.class).stream().map(t -> new Object[] { t, t.title() }).toList();
    }

    private final SectionsSpecTemplate specItem;

    public SpecSectionsTest(SectionsSpecTemplate specItem, String name) {
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

    String render(SectionsSpecTemplate specTemplate) {
        return switch (specTemplate) {
        //case PARENT_CONTEXTS -> ParentContextsRender.of(new ParentContexts(Person.Joe)).renderString();
        case CONTEXT -> ContextRenderer.of(new Custom.Context(Person.Joe)).renderString();
        default -> specTemplate.render();
        };
    }
}
