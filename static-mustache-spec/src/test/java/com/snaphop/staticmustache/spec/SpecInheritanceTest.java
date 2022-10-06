package com.snaphop.staticmustache.spec;

import java.util.Collection;
import java.util.EnumSet;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.snaphop.staticmustache.spec.inheritance.InheritanceSpecTemplate;

@Ignore
@RunWith(Parameterized.class)
public class SpecInheritanceTest extends AbstractSpecTest<InheritanceSpecTemplate> {

    @Parameters(name = "{1}")
    public static Collection<Object[]> data() {
        return EnumSet.allOf(InheritanceSpecTemplate.class).stream().map(t -> new Object[] { t, t.title() }).toList();
    }

    private final InheritanceSpecTemplate specItem;

    public SpecInheritanceTest(InheritanceSpecTemplate specItem, String name) {
        this.specItem = specItem;
    }

    @Override
    InheritanceSpecTemplate specItem() {
        return this.specItem;
    }

    String render(InheritanceSpecTemplate specTemplate) {
        return specTemplate.render();
    }
}
