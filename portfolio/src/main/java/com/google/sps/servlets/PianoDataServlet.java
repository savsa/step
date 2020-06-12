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

import com.google.sps.data.Piano;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/* Returns piano location data (lat, lng) as a JSON array. */
@WebServlet("/piano-data")
public class PianoDataServlet extends HttpServlet {

  private ArrayList<Piano> pianos;

  @Override
  public void init() {
    pianos = new ArrayList<>();

    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/piano-data.csv"));
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");

      double lat;
      double lng;
      String title;
      String info;
      try {
        lat = Double.parseDouble(cells[0]);
        lng = Double.parseDouble(cells[1]);
        title = cells[2];
        info = cells[3];
      } catch (Exception e) {
        throw new RuntimeException("Failed to read CSV file.", e);
      }
      pianos.add(new Piano(lat, lng, title, info));
    }
    scanner.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.getWriter().println(new Gson().toJson(pianos));
  }
}