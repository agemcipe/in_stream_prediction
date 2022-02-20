package com.dmml.window;

import com.dmml.local.LocalOnnxModel;
import com.dmml.tuples.UnivariateTuple;

import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.streaming.api.functions.windowing.ProcessAllWindowFunction;
import org.apache.flink.streaming.api.windowing.windows.TimeWindow;
import org.apache.flink.util.Collector;

import ai.onnxruntime.OrtException;

public class LocalOnnxProcessAllWindowFunction
        extends ProcessAllWindowFunction<UnivariateTuple, Tuple3<String, String, String>, TimeWindow> {

    @Override
    public void process(Context context, Iterable<UnivariateTuple> input,
            Collector<Tuple3<String, String, String>> out) throws OrtException {

        long count = 0;
        int i = 0;

        float[][] modelInputs = new float[1][10];

        int predictedLabel = -1;

        for (UnivariateTuple in : input) {
            modelInputs[0][i] = in.f1;
            count++;
        }
        LocalOnnxModel myModel = new LocalOnnxModel();
        predictedLabel = myModel.predict(modelInputs);

        out.collect(new Tuple3<String, String, String>("Window: " + context.window(), "count: " + count,
                "Prediction: " + predictedLabel));
    }
}