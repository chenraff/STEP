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

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import java.io.IOException;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that returns arraylist json of comments */
@WebServlet("/data")
public class DataServlet extends HttpServlet {

    /* Returns json of stored comments list */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Query query = new Query("Comment").
                        addSort("timestamp", SortDirection.ASCENDING);        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        /* Get maximum comments amount from url query string */
        int commentsMaxAmount = Integer.parseInt(request.getParameter("maxComments"));
        FetchOptions fetchOption = FetchOptions.Builder
                    .withLimit(commentsMaxAmount);

        List<String> comments = new ArrayList<>();
        for (Entity comment : results.asIterable(fetchOption)) {
            String commData = (String) comment.getProperty("commentData");
            comments.add(commData);
        }

        Gson gson = new Gson();
        String commentsJson = gson.toJson(comments);
        response.setContentType("application/json;");
        response.getWriter().println(commentsJson);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String comment = request.getParameter("text-input");
        long timestamp = System.currentTimeMillis();
        if (comment != null && !comment.trim().isEmpty()){
            Entity commentEntity = new Entity("Comment");
            commentEntity.setProperty("commentData", comment);
            commentEntity.setProperty("timestamp", timestamp);

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(commentEntity);
        }
        response.sendRedirect("/index.html");
    }
}
