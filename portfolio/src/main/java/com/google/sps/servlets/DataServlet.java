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
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gson.Gson;
import com.google.sps.data.Comment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/* Servlet that handles comment posting and fetching. */
@WebServlet("/comment")
public class DataServlet extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JSONObject jsonObject;
    try {
      jsonObject = new JSONObject();
    } catch (Exception e) {
      throw new RuntimeException("Failed to create JSON object.", e);
    }

    response.setContentType("application/json;");
    int numComments;
    try {
      numComments = Integer.parseInt(request.getParameter("num"));
    } catch (Exception e) {
      jsonObject.put("message", "Bad request.");
      jsonObject.put("status", "error");
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      return;
    }

    Query query = new Query("Comment").addSort("timestamp", SortDirection.DESCENDING);
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    PreparedQuery results = datastore.prepare(query);
    List<Entity> limitedResults = results.asList(FetchOptions.Builder.withLimit(numComments));

    ArrayList<Comment> comments = new ArrayList<>();
    for (Entity entity : limitedResults) {
      String text = (String)entity.getProperty("text");
      String email = (String)entity.getProperty("email");
      comments.add(new Comment(text, email));
    }

    UserService userService = UserServiceFactory.getUserService();
    String email = userService.isUserLoggedIn() ? userService.getCurrentUser().getEmail() : "";
    jsonObject.put("email", email);
    jsonObject.put("login_url", userService.createLoginURL("/"));

    jsonObject.put("comments", comments);
    response.getWriter().println(toJson(jsonObject));
  }

  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JSONObject jsonObject;
    try {
      jsonObject = new JSONObject();
    } catch (Exception e) {
      throw new RuntimeException("Failed to create JSON object.", e);
    }

    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      jsonObject.put("message", "Unauthorized.");
      jsonObject.put("status", "error");
      return;
    }

    response.setContentType("application/json;");
    String comment = request.getParameter("comment");
    if (comment == null || comment.isEmpty()) {
      jsonObject.put("message", "Bad request.");
      jsonObject.put("status", "error");
      response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
      response.getWriter().println(toJson(jsonObject));
      return;
    }

    Entity entity = new Entity("Comment");
    entity.setProperty("text", comment);
    entity.setProperty("email", userService.getCurrentUser().getEmail());
    entity.setProperty("timestamp", System.currentTimeMillis());

    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    datastore.put(entity);

    jsonObject.put("message", "Successfully created comment.");
    jsonObject.put("status", "success");
    response.getWriter().println(toJson(jsonObject));
  }

  private String toJson(JSONObject jsonObject) {
    return new Gson().toJson(jsonObject);
  }
}
