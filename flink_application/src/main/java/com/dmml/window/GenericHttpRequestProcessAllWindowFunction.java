package com.dmml.window;

import java.io.IOException;
import java.net.MalformedURLException;

import com.dmml.http.HttpPostRequest;
import com.dmml.http.HttpRequestBody;
import com.google.gson.JsonObject;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

// TODO: DELETE, purpose of this class was to see if generic Tuple works, Spoiler: it doesn't :(

public class GenericHttpRequestProcessAllWindowFunction
                extends ProcessAllWindowFunction<Tuple, Tuple3<String, String, String>, TimeWindow> {

        @Override
        public void process(Context context, Iterable<Tuple> input,
                        Collector<Tuple3<String, String, String>> out) throws MalformedURLException, IOException {

                int windowSize = 11;
                int numValues = 2;
                String url = "http://localhost:8000/predict";

                float[][] data = new float[windowSize][numValues];

                int i = 0;
                for (Tuple in : input) {
                        for (int j = 0; j < numValues; j++) {
                                data[i][j] = in.getField(j);
                        }
                        i++;
                        // TODO: check windowSize
                }

                JsonObject response = new HttpPostRequest(url, new HttpRequestBody(data).toString()).getResult();

                out.collect(new Tuple3<String, String, String>("Window: " + context.window(),
                                "count: " + i,
                                "Prediction: " + response.get("predicted").toString()));
        }

}
