package issue201;

import io.jstach.jstache.JStache;

import java.util.List;

@JStache(path = "templates/test.tpl")
record Order(String description, List<Item> items) {
    record Item(String name){}
    String other() {
      return "other";
    }
}
