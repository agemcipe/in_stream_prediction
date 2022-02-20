package com.dmml;

import static org.junit.Assert.assertEquals;

import com.dmml.http.HttpRequestBody;

import org.junit.Test;

public class HttpRequestBodyTest {

   @Test
   public void testToString() {

      float[][] data = { { (float) 0.0, (float) 0.0 }, { (float) 1.0, (float) 2.0 } };
      String requestBodyString = new HttpRequestBody(data).toString();

      String expString = "{\"data\":[[0.0,0.0],[1.0,2.0]]}";

      assertEquals(expString, requestBodyString);

   }
}
