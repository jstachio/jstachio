/*
 * Copyright (c) 2014, Victor Nazarov <asviraspossible@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation and/or
 *     other materials provided with the distribution.
 *
 *  3. Neither the name of the copyright holder nor the names of its contributors
 *     may be used to endorse or promote products derived from this software
 *     without specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 *  THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 *  IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *   LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 *  EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.sviperll.staticmustache.examples;

import static java.util.Objects.requireNonNull;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import io.jstach.text.Layoutable;
import io.jstach.text.formats.Html;

public class Main {
    public static void main(String[] args) throws IOException {
        int [][] array = new int[][] {new int[] {1,2,3,4,5},new int[] {1,2,3,4,5},new int[] {1,2,3,4,5},new int[] {1,2,3,4,5},new int[] {1,2,3,4,5}};
        List<User1.Item<String>> list1 = new ArrayList<User1.Item<String>>();
        list1.add(new User1.Item<String>("abc"));
        list1.add(new User1.Item<String>("def"));

        
        PrintStream out = requireNonNull(System.out);
        if (out == null) throw new IllegalStateException();
        User1 user2 = new User1("Victor", 29, new String[] {"aaa", "bbb", "ccc"}, array, list1);
        var renderable2 =  RenderableTextUser1Adapter.of(user2);
        renderable2.render(out);
        User1 user1 = new User1("Victor <asviraspossible@gmail.com>", 29, new String[] {}, array, list1);
        var renderable1 = RenderableHtmlUser1Adapter.of(user1);
        renderable1.render(out);

        Settings settings = new Settings(renderable1, true);
        var renderable3 = SettingsRenderer.of(settings);
        renderable3.render(out);

        List<User.Item<String>> list = new ArrayList<User.Item<String>>();
        list.add(new User.Item<String>("helmet"));
        list.add(new User.Item<String>("shower"));

        Html5Layout layout = new Html5Layout("John Doe page");
        Layoutable<Html> layoutable = new Html5LayoutLayoutable(layout);
        User user = new User("John Doe", 21, new String[] {"Knowns nothing"}, list, new LiLayoutLayoutable(new LiLayout()));
//        var renderable = new  UserRenderable(user);
//        Renderer renderer = layoutable.createHeaderRenderer(out);
//        renderer = renderer.andThen(renderable.createRenderer(out));
//        renderer = renderer.andThen(layoutable.createFooterRenderer(out));
//        renderer.render();
        
        var renderable = UserRenderer.of(user);
        renderable.withLayout(layoutable).render(out);

        Layouted layouted = new Layouted(UserRenderer.of(user), layoutable);
        LayoutedRenderer.of(layouted).render(out);
    }
}
