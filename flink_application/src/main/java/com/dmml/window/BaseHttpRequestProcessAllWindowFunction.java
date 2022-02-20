package com.dmml.window;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.StreamSupport;

import com.dmml.http.HttpPostRequest;
import com.dmml.http.HttpRequestBody;
import com.dmml.tuples.VariableStreamElement;
import com.google.gson.JsonObject;

import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.GlobalWindow;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

// NOTE: This implementation does not work, because of Type mismatch during runtime by the Flink TypeChecker

public class BaseHttpRequestProcessAllWindowFunction
                extends ProcessAllWindowFunction<VariableStreamElement, Tuple3<String, String, String>, GlobalWindow> {

        @Override
        public void process(Context context, Iterable<VariableStreamElement> input,
                        Collector<Tuple3<String, String, String>> out) throws MalformedURLException, IOException {

                ParameterTool parameters = (ParameterTool) getRuntimeContext()
                                .getExecutionConfig()
                                .getGlobalJobParameters();

                final long windowSize = parameters.getLong("windowSize");
                final String url = parameters.get("url");

                // Check that we have as many elements in the window as expected

                float[][] data = new float[(int) windowSize][];

                int i = 0;
                for (VariableStreamElement in : input) {
                        if (i < data.length) {
                                data[i] = in.values;
                        }
                        i++;
                }

                String prediction;
                if (i == windowSize) {

                        try {
                                JsonObject response = new HttpPostRequest(url, new HttpRequestBody(data).toString())
                                                .getResult();
                                prediction = response.get("predicted").toString();
                        } catch (IOException e) {
                                e.printStackTrace();
                                prediction = "PREDICTION_FAILED";

                        }
                } else {
                        prediction = "Number of Elements in Windows Mismatched. Expected: " + windowSize + " Actual: "
                                        + i;
                }

                out.collect(new Tuple3<String, String, String>("Window: " + context.window(),
                                "count: " + i,
                                "Prediction: " + prediction));
        }

}
