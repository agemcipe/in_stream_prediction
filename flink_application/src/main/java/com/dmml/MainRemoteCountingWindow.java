package com.dmml;

import java.time.Duration;

import com.dmml.tuples.VariableStreamElement;
import com.dmml.window.BaseHttpRequestProcessAllWindowFunction;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class MainRemoteCountingWindow {

        public static void main(String[] args) throws Exception {

                final ParameterTool params = ParameterTool.fromArgs(args);

                final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

                env.getConfig().setGlobalJobParameters(params);

                // // TODO: make available through Parameter tool

                final long windowSize = params.getLong("windowSize");
                final long windowSlide = params.getLong("windowSlide");
                final boolean hasAnomaly = params.getBoolean("hasAnomalyColumn"); // TODO: Not working

                // parse input text file
                DataStream<VariableStreamElement> dataStream = env.readTextFile(params.get("inputFile"))
                                .map(new MapFunction<String, VariableStreamElement>() {
                                        @Override
                                        public VariableStreamElement map(String str) throws Exception {
                                                String[] temp = str.split(",");

                                                VariableStreamElement t = new VariableStreamElement(temp, true);
                                                return t;
                                        }
                                })
                                .assignTimestampsAndWatermarks(WatermarkStrategy
                                                .<VariableStreamElement>forBoundedOutOfOrderness(
                                                                Duration.ofSeconds(10))
                                                .withTimestampAssigner(
                                                                (event, timestamp) -> event.eventTime * 1000));

                // No keying done here
                dataStream.countWindowAll(windowSize, windowSlide)
                                .process(new BaseHttpRequestProcessAllWindowFunction())
                                .writeAsCsv(params.get("outputFile"));
                // dataStream.windowAll(SlidingEventTimeWindows.of(Time.seconds(windowSize),
                // Time.seconds(1)))
                // .process(new BaseHttpRequestProcessAllWindowFunction());

                env.execute(params.get("runName", "Main Remote Counting"));
        }
}
