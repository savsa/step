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
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query.SortDirection;
import com.google.appengine.api.datastore.Query;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.ArrayList;

/* Servlet that handles comment posting and fetching. */
@WebServlet("/comment")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    int numComments = Integer.parseInt(request.getParameter("num"));
    JSONObject jsonObject;
    try {
      jsonObject = new JSONObject();
    } catch (Exception e) {
      throw new RuntimeException("Failed to create JSON object.", e);
    }

    Query query = new Query("Comment").addSort("timestamp", SortDirection.ASCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<Entity> limitedResults = results.asList(FetchOptions.Builder.withLimit(numComments));

    ArrayList<String> comments = new ArrayList<>();
    for (Entity entity : limitedResults) {
      String text = (String)entity.getProperty("text");
      comments.add(text);
    }

    jsonObject.put("comments", comments);
    response.setContentType("application/json;");
    response.getWriter().println(toJson(jsonObject));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    String comment = request.getParameter("comment");

    JSONObject jsonObject;
    try {
      jsonObject = new JSONObject();
    } catch (Exception e) {
      throw new RuntimeException("Failed to create JSON object.", e);
    }

    response.setContentType("application/json;");
    if (comment == null || comment.isEmpty()) {
      jsonObject.put("error", "Bad request.");
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(toJson(jsonObject));
      return;
    }

    Entity entity = new Entity("Comment");
    entity.setProperty("text", comment);
    entity.setProperty("timestamp", System.currentTimeMillis());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(entity);

    jsonObject.put("success", "Successfully created comment.");
    response.getWriter().println(toJson(jsonObject));
  }

  private String toJson(JSONObject jsonObject) {
    Gson gson = new Gson();
    return gson.toJson(jsonObject);
  }
}
