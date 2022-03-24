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

import com.github.sviperll.staticmustache.text.Layoutable;
import com.github.sviperll.staticmustache.text.Renderable;
import com.github.sviperll.staticmustache.text.Renderer;
import com.github.sviperll.staticmustache.text.formats.Html;
import com.github.sviperll.staticmustache.text.formats.PlainText;

public class Main {
    public static void main(String[] args) throws IOException {
        int [][] array = new int[][] {new int[] {1,2,3,4,5},new int[] {1,2,3,4,5},new int[] {1,2,3,4,5},new int[] {1,2,3,4,5},new int[] {1,2,3,4,5}};
        List<User1.Item<String>> list1 = new ArrayList<User1.Item<String>>();
        list1.add(new User1.Item<String>("abc"));
        list1.add(new User1.Item<String>("def"));

        PrintStream out = requireNonNull(System.out);
        User1 user2 = new User1("Victor", 29, new String[] {"aaa", "bbb", "ccc"}, array, list1);
        Renderable<PlainText> renderable2 = new RenderableTextUser1Adapter(user2);
        Renderer renderer3 = renderable2.createRenderer(out);
        renderer3.render();
        User1 user1 = new User1("Victor <asviraspossible@gmail.com>", 29, new String[] {}, array, list1);
        Renderable<Html> renderable1 = new RenderableHtmlUser1Adapter(user1);
        Renderer renderer1 = renderable1.createRenderer(out);
        renderer1.render();
        Settings settings = new Settings(renderable1, true);
        Renderable<Html> renderable3 = new RenderableSettingsAdapter(settings);
        Renderer renderer2 = renderable3.createRenderer(out);
        renderer2.render();

        List<User.Item<String>> list = new ArrayList<User.Item<String>>();
        list.add(new User.Item<String>("helmet"));
        list.add(new User.Item<String>("shower"));

        Html5Layout layout = new Html5Layout("John Doe page");
        Layoutable<Html> layoutable = new LayoutableHtml5LayoutAdapter(layout);
        User user = new User("John Doe", 21, new String[] {"Knowns nothing"}, list, new LayoutableLiLayoutAdapter(new LiLayout()));
        Renderable<Html> renderable = new RenderableHtmlUserAdapter(user);
        Renderer renderer = layoutable.createHeaderRenderer(out);
        renderer = renderer.andThen(renderable.createRenderer(out));
        renderer = renderer.andThen(layoutable.createFooterRenderer(out));
        renderer.render();

        Layouted layouted = new Layouted(renderable, layoutable);
        new RenderableLayoutedAdapter(layouted).createRenderer(out).render();
    }
}
