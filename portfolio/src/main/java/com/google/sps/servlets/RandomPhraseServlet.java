// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Servlet that returns random Chinese phrase */
@WebServlet("/random-phrase")
public class RandomPhraseServlet extends HttpServlet {
    
    String HTML_CONTENT = "text/html;";
    String ENCODING = "UTF-8";

    private List<String> phrases;

    /* Initializes phrases list with Chinese phrases */ 
    @Override
    public void init() {
        phrases = new ArrayList<>();
        phrases.add("一笑解千愁。(A smile dispels many worries)");
        phrases.add("笑一笑,十年少。(Happiness is the best cosmetic)");
        phrases.add("一步一个脚印。(Every step makes a footprint)");
        phrases.add("有缘千里来相会。(Fate brings people together from far apart)");
        phrases.add("失败是成功之母。(Failure is the mother of success)");
        phrases.add("水滴石穿，绳锯木断。(Water drops pierce stone; rope saws cut wood)");
        phrases.add("千里之行，始于足下。(A thousand-li journey starts with a footfall)");
    }

    /* Returns a random Chinese phrase */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String phrase = phrases.get((int) (Math.random() * phrases.size()));

        response.setContentType(HTML_CONTENT);
        response.setCharacterEncoding(ENCODING);
        response.getWriter().println(phrase);
    }
}
