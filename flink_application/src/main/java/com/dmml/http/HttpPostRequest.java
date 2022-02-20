package com.dmml.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class HttpPostRequest {

   URL url;
   HttpURLConnection con;
   final private Gson gson = new Gson();

   public HttpPostRequest(String url, String requestBody) throws IOException, MalformedURLException {
      this.url = new URL(url);

      this.con = (HttpURLConnection) this.url.openConnection();
      this.con.setRequestMethod("POST");
      this.con.setRequestProperty("Content-Type", "application/json");
      this.con.setRequestProperty("Accept", "application/json");
      this.con.setDoOutput(true);
      this.con.setConnectTimeout(3000);
      this.con.setReadTimeout(3000);

      try (OutputStream os = con.getOutputStream()) {
         byte[] jsonInput = requestBody.getBytes("utf-8");
         os.write(jsonInput, 0, jsonInput.length);
      }
   }

   public JsonObject getResult() throws IOException {

      BufferedReader in = new BufferedReader(
            new InputStreamReader(this.con.getInputStream()));

      // turn response json string into jsonObject
      return gson.fromJson(in, JsonObject.class);
   }
}
