package com.dmml;

import static org.junit.Assert.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;

import com.dmml.http.HttpPostRequest;
import com.dmml.http.HttpRequestBody;
import com.google.gson.JsonObject;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;

public class HttpPostRequestTest {

   private final int port = 8888;
   private final float predictedValue = 0.1f;

   private ClientAndServer mockServer;

   @Before
   public void startServer() {
      mockServer = startClientAndServer(port);

      mockServer
            .when(
                  request()
                        .withMethod("POST")
                        .withPath("/predict")
                        .withHeader("\"Content-type\", \"application/json\""))
            .respond(
                  response()
                        .withStatusCode(200)
                        .withHeaders(
                              new Header("Content-Type",
                                    "application/json; charset=utf-8"))
                        .withBody("{ predicted: " + predictedValue + " }"));
   }

   @After
   public void stopServer() {
      mockServer.stop();
   }

   @Test
   public void shouldAnswerWithTrue() throws MalformedURLException, IOException {
      // this expects to have the prediction server running

      float[][] data = new float[11][2]; // TODO extract windowSize
      for (float[] row : data) {
         Arrays.fill(row, (float) 0.0);
      }

      String requestBody = new HttpRequestBody(data).toString();
      JsonObject response = new HttpPostRequest(
            "http://localhost:" + port + "/predict", requestBody).getResult();

      String actualResponse = response.get("predicted").getAsString();
      assertEquals(predictedValue, Float.valueOf(actualResponse), 0.0001); // TODO allow for renaming

   }

}
