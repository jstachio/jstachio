package io.jstach.context;

import java.util.Iterator;
import java.util.List;

import org.junit.Test;


public class ContextIterableTest {

    @Test
    public void test() {
        List<String> list = List.of("a", "b", "c");
        
        int i = 0;
        for (Iterator<String> it = list.iterator(); it.hasNext(); i++) {
            var _item = it.next();
            boolean first = i == 0;
            boolean last = ! it.hasNext();
            
            System.out.println(_item + " " + i); 
        }
    }

}
