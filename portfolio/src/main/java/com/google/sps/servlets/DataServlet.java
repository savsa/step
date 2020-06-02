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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.ArrayList;

@WebServlet("/comment")
public class DataServlet extends HttpServlet {

  private ArrayList<String> comments = new ArrayList<String>();

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    JSONObject jsonObject;
    try {
      jsonObject = new JSONObject();
    } catch (Exception e) {
      throw new RuntimeException("Failed to create JSON object.", e);
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
    if (comment == "" || comment == null) {
      jsonObject.put("error", "Bad request.");
      response.getWriter().println(toJson(jsonObject));
      return;
    }

    comments.add(comment);
    jsonObject.put("comments", comments);
    response.getWriter().println(toJson(jsonObject));
  }

  private String toJson(JSONObject jsonObject) {
    Gson gson = new Gson();
    return gson.toJson(jsonObject);
  }
}
