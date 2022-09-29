package com.github.sviperll.staticmustache.apt.test;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.snaphop.staticmustache.apt.CodeNewLineSplitter;

public class SplitTest {

    @Test
    public void testSplit() {
        String s = "abaata";
        List<String> actual = CodeNewLineSplitter.split(s, "a");
        //assertEquals(4,actual.size());
        assertEquals("[a, ba, a, ta]", actual.toString());
    }

    @Test
    public void testEmpty() {
        String s = "";
        List<String> actual = CodeNewLineSplitter.split(s, "a");
        //assertEquals(4,actual.size());
        assertEquals("[]", actual.toString());
    }
    
    @Test
    public void testOnlyDelim1() {
        String s = "\\n";
        List<String> actual = CodeNewLineSplitter.split(s, "\\n");
        //assertEquals(4,actual.size());
        assertEquals("[\\n]", actual.toString());
    }
    
    @Test
    public void testOnlyDelim2() {
        String s = "abcabc";
        List<String> actual = CodeNewLineSplitter.split(s, "abc");
        //assertEquals(4,actual.size());
        assertEquals("[abc, abc]", actual.toString());
    }
    
    @Test
    public void testOnlyDelim3() {
        String s = "\\n\\n\\n";
        List<String> actual = CodeNewLineSplitter.split(s, "\\n");
        //assertEquals(4,actual.size());
        assertEquals(List.of("\\n", "\\n", "\\n"), actual);
    }
    
    @Test
    public void testReal() {
        String s = "<div>\\n\\n<div>\\n";
        List<String> actual = CodeNewLineSplitter.split(s, "\\n");
        //assertEquals(4,actual.size());
        assertEquals(List.of("<div>\\n", "\\n", "<div>\\n"), actual);
    }

    @Test
    public void testReal2() {
        String s = "<div>\\n\\n<div>";
        List<String> actual = CodeNewLineSplitter.split(s, "\\n");
        //assertEquals(4,actual.size());
        assertEquals(List.of("<div>\\n", "\\n", "<div>"), actual);
    }
    
    @Test
    public void testReal3() {
        String s = "<div>\\n\\n<div>\\n\\t  ";
        List<String> actual = CodeNewLineSplitter.split(s, "\\n");
        //assertEquals(4,actual.size());
        assertEquals(List.of("<div>\\n", "\\n", "<div>\\n", "\\t  "), actual);
    }

}
