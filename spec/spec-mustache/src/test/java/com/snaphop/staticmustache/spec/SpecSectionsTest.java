package com.snaphop.staticmustache.spec;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import io.jstach.spec.mustache.spec.custom.ContextRenderer;
import io.jstach.spec.mustache.spec.custom.Custom;
import io.jstach.spec.mustache.spec.custom.Custom.Person;
import io.jstach.spec.mustache.spec.sections.SectionsSpecTemplate;

@RunWith(Parameterized.class)
public class SpecSectionsTest extends AbstractSpecTest<SectionsSpecTemplate> {

    @Parameters(name = "{1}")
    public static Collection<Object[]> data() {
        return EnumSet.allOf(SectionsSpecTemplate.class).stream().map(t -> new Object[] { t, t.title() }).toList();
    }

    private final SectionsSpecTemplate specItem;

    public SpecSectionsTest(SectionsSpecTemplate specItem, String name) {
        this.specItem = specItem;
    }

    @Override
    SectionsSpecTemplate specItem() {
        return this.specItem;
    }

    String render(SectionsSpecTemplate specTemplate) {
        return switch (specTemplate) {
        //case PARENT_CONTEXTS -> ParentContextsRender.of(new ParentContexts(Person.Joe)).renderString();
        case CONTEXT -> ContextRenderer.of(new Custom.Context(Person.Joe)).renderString();
        default -> specTemplate.render();
        };
    }
}
