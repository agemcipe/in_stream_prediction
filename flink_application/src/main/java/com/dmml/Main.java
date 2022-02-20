package com.dmml;

import java.time.Duration;

import com.dmml.local.LocalOnnxModel;
import com.dmml.tuples.UnivariateTuple;
import com.dmml.window.LocalOnnxProcessAllWindowFunction;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.windowing.assigners.SlidingEventTimeWindows;
import org.apache.flink.streaming.api.windowing.time.Time;

public class Main {

        public static void main(String[] args) throws Exception {

                final ParameterTool params = ParameterTool.fromArgs(args);

                final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                env.getConfig().setGlobalJobParameters(params);

                // parse input text file
                DataStream<UnivariateTuple> dataStream = env.readTextFile(params.get("inputFile"))
                                .map(new MapFunction<String, UnivariateTuple>() {
                                        @Override
                                        public UnivariateTuple map(String str) throws Exception {
                                                String[] temp = str.split(",");
                                                System.out.println();
                                                return new UnivariateTuple(temp[0],
                                                                Float.parseFloat(temp[1]),
                                                                Integer.parseInt(temp[2]));
                                        }
                                })
                                .assignTimestampsAndWatermarks(WatermarkStrategy
                                                .<UnivariateTuple>forBoundedOutOfOrderness(
                                                                Duration.ofHours(8))
                                                .withTimestampAssigner(
                                                                (event, timestamp) -> event.f0));

                // No keying done here
                // dataStream.windowAll(SlidingEventTimeWindows.of(Time.hours(10),
                // Time.hours(1)))
                // .process(new LocalOnnxProcessAllWindowFunction())
                // .writeAsCsv(params.get("outputFile"));

                dataStream.windowAll(SlidingEventTimeWindows.of(Time.hours(10),
                                Time.hours(1)))
                                .process(new LocalOnnxProcessAllWindowFunction())
                                .writeAsCsv(params.get("outputFile"));

                env.execute("DataStream Remote");
        }
}
