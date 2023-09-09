package issue221;

import io.jstach.jstache.JStache;

@JStache(path = "issue221/issue221.mustache")
public record TestResourceModel(String message) {

}
