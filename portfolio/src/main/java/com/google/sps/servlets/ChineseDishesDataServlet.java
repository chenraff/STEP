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
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.FetchOptions;
import java.io.IOException;
import com.google.gson.Gson;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/** Servlet that returns hashmap json of dishes and their votes */
@WebServlet("/dishes-data")
public class ChineseDishesDataServlet extends HttpServlet {
    
    String DISHES_ENTITY = "Dish";
    String DISH_NAME_PROPERTY = "DishName";
    String VOTE_AMOUNT_PROPERY = "VoteCnt";
    String INDEX_URL = "/index.html";
    String SELECT_INPUT_PARAM = "chinese-dishes-select";
    String JSON_CONTENT = "application/json;";

    /* Returns json of stored dish and votes hashmap */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        Query query = new Query(DISHES_ENTITY);        
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        PreparedQuery results = datastore.prepare(query);

        Map<String, Long> dishVotes = new HashMap<>();
        for (Entity dish : results.asIterable()) {
            String dishName = (String) dish.getProperty(DISH_NAME_PROPERTY);
            long voteAmount = (long) dish.getProperty(VOTE_AMOUNT_PROPERY);
            dishVotes.put(dishName, voteAmount);
        }

        Gson gson = new Gson();
        String dishJson = gson.toJson(dishVotes);
        response.setContentType(JSON_CONTENT);
        response.getWriter().println(dishJson);
    }

    /* Updates dish vote amount at the datastore dish entity */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long dishVoteCnt = 0;
        Entity dishEntity;

        String dishName = request.getParameter(SELECT_INPUT_PARAM);
        Filter dishNameFilter = new Query.FilterPredicate(DISH_NAME_PROPERTY, FilterOperator.EQUAL, dishName);
        Query dishQuery = new Query(DISHES_ENTITY).setFilter(dishNameFilter);
        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
        FetchOptions fetchOption = FetchOptions.Builder.withDefaults();
        List<Entity> dishResult = datastore.prepare(dishQuery).asList(fetchOption);

        /* The dish doesn't exist in the DB -> creates new entity */
        if (dishResult.isEmpty()) {
            dishEntity = new Entity(DISHES_ENTITY);
            dishEntity.setProperty(DISH_NAME_PROPERTY, dishName);
        }
        /* The dish exists already in the DB -> updates its votes amount */
        else {
            dishEntity = dishResult.get(0);
            dishVoteCnt = (long) dishEntity.getProperty(VOTE_AMOUNT_PROPERY);
        }
        dishEntity.setProperty(VOTE_AMOUNT_PROPERY, dishVoteCnt+1);

        datastore.put(dishEntity);
        response.sendRedirect(INDEX_URL);
    }
}
