package com.dmml.http;

import com.google.gson.Gson;

public class HttpRequestBody {

   public float[][] data;

   public HttpRequestBody(float[][] data) {
      this.data = data;
   }

   public String toString() {
      return new Gson().toJson(this).toString();
   }
}
