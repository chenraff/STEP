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

import com.google.cloud.language.v1.Document;
import com.google.cloud.language.v1.LanguageServiceClient;
import com.google.cloud.language.v1.Sentiment;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.FetchOptions;
import java.io.IOException;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that returns arraylist json of comments */
@WebServlet("/data")
public class DataServlet extends HttpServlet {
    
    String COMMENTS_ENTITY = "Comment";
    String TIME_STAMP_PROPERTY = "timestamp";
    String COMMENTS_DATA_PROPERTY = "commentData";
    String SENTIMENT_SCORE_PROPERTY = "sentimentScore";
    String INDEX_URL = "/index.html";
    String TEXT_INPUT_PARAM = "text-input";
    String MAX_COMM_PARAM = "maxComments";
    String JSON_CONTENT = "application/json;";

    /* Returns json of stored comments list */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        Query query = new Query(COMMENTS_ENTITY).
                        addSort(TIME_STAMP_PROPERTY, SortDirection.ASCENDING);        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        /* Get maximum comments amount from url query string */
        int commentsMaxAmount = Integer.parseInt(request.getParameter(MAX_COMM_PARAM));
        FetchOptions fetchOption = FetchOptions.Builder
                    .withLimit(commentsMaxAmount);

        Map<String, Float> comments = new HashMap<>();
        for (Entity comment : results.asIterable(fetchOption)) {
            String commData = (String) comment.getProperty(COMMENTS_DATA_PROPERTY);
            float commScore = (float) comment.getProperty(SENTIMENT_SCORE_PROPERTY);
            comments.put(commData, commScore);
        }

        Gson gson = new Gson();
        String commentsJson = gson.toJson(comments);
        response.setContentType(JSON_CONTENT);
        response.getWriter().println(commentsJson);
    }

    /* Stores non-empty comment from the text input at the datastore comments entity */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String comment = request.getParameter(TEXT_INPUT_PARAM);
        long timestamp = System.currentTimeMillis();
        if (comment != null && !comment.trim().isEmpty()){
            float commentScore = sentimentScore(comment);
            Entity commentEntity = new Entity(COMMENTS_ENTITY);
            commentEntity.setProperty(COMMENTS_DATA_PROPERTY, comment);
            commentEntity.setProperty(SENTIMENT_SCORE_PROPERTY, commentScore);
            commentEntity.setProperty(TIME_STAMP_PROPERTY, timestamp);

            DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
            datastore.put(commentEntity);
        }
        response.sendRedirect(INDEX_URL);
    }
    
    /* Returns the sentiment score of given str */
    public float sentimentScore(String str) throws IOException {
        Document doc = Document.newBuilder()
                        .setContent(str)
                        .setType(Document.Type.PLAIN_TEXT)
                        .build();
        LanguageServiceClient languageService = LanguageServiceClient.create();
        Sentiment sentiment = languageService.analyzeSentiment(doc).getDocumentSentiment();
        float score = sentiment.getScore();
        languageService.close();
        return score;
    }
}
