package com.github.sviperll.staticmustache.examples;


public class PartialTest {

    // root context
    //@Generate
    record Person(String firstName, String lastName) {
        
    }
    
    record Field(String name, String value) {
        
    }
    @SuppressWarnings("unused")
    public void spec() {
        /*
         * partials are basically macro expansion
         * however they have access to the parent context
         */
        String partialName = "input.mustache";
        String partial = """
                <input name="{{name}}" value="{{value}}" />
                """;
        String templateName = "field.mustache";
        String template = """
                <form>
                {{> input }}
                </form>
                """;
        
        render(new Field(/* name */ "firstName",  /* value */ "lastName"), "field.mustache");
        
        /*
         * So basically {{> input }} would be verbatim replaced during compile time
         * with the partial string above to create what is below:
         */
        
        String expandedTemplate = """
                <form>
                <input name="{{name}}" value="{{value}}" />
                </form>
                """;
        
        /*
         * The above would be pretty simple to support as it is basically
         * just replace the partial call with the verbatim partial template.
         * The partial template would not need an annotated model.
         * 
         * The problem with mustache spec partials in practice is that
         * the context almost never matches up.
         * 
         * Thus handlebars allows the much more useful parameterized
         * partials.
         * 
         * Literals are put in double quotes otherwise it is a context path.
         */
        String personTemplateName = "person.mustache";

        String personTemplate = """
                <form>
                {{> input name="fieldFirstName" value=firstName }}
                {{> input name="fieldLastName" value=lastName }}
                </form>
                """;
        
        render(new Person("Adam", "Gent"));
        
        /*
         * We could do a preprocess model where we process the partial before it is parsed
         * regularly againt the java model.
         * 
         * We simply replace the variables like:
         */
        
        String paramExpandedTemplate = """
                <form>
                <input name="fieldFirstName" value="{{firstName}}" />
                <input name="fieldLastName" value="{{lastName}}" />
                </form>
                """;
        /*
         * In the above the {{name}} in the original partial was replaced
         * with the literal and the {{value}} replaced by variables.
         */
        /*
         * A minor problem with the above is that we will have to adjust
         * the Postion to give semi useful correct error messages
         * as the lines numbers will not match up.
         * 
         * A major problem is if the partial has nested contexts it
         * probably not work as expected.
         */
        
        String fieldListPartialName = "field-list.mustache";
        
        String fieldListPartialTemplate = """
                {{name}} - {{value}}
                {{#list}}
                <input name="{{name}}" value="{{value}}" />
                {{/list}}
                """;
        /*
         * In the above we do not want to replace the name and value 
         * in the list context. So we cannot just blindly search and replace
         * in the preprocess.
         * 
         * Thus to really support parameterized partials we would need them to have their own
         * sub context. That is they would need to be processed *mostly* like normal templates with
         * the parent being passed in I guess.
         * 
         * While it is probably possible that is confusing as fuck and this is mainly because
         * of having to know about the parent context.
         * 
         * Alternatives:
         * 
         * 1. Partials do not inherit the context and all parameters must be passed
         * or a default is inferred (e.g. null for declared types). This would be analagous to a
         * pure function call aka static method. However this is totally against the mustache spec
         * of partials and thus a different syntax should be used. A good alternative would be
         * handlebars helpers syntax. However resolving the parameters to pass to the generate the 
         * renderable would be nontrivial as it is objected based.
         * 
         * 2. Just expand the partial like the real mustache spec but this adds little value.
         * 
         * 3. Don't support partials and just decorate the model.
         * 
         */
        
    }
    
    private void render(Object...o) {
        
    }

}
