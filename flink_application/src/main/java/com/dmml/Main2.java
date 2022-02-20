package com.dmml;

import java.time.Duration;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.api.java.tuple.Tuple;
import org.apache.flink.api.java.typeutils.TypeExtractor;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

public class Main2 {

        // TODO: Delete, this approach fails since it, due to runtime type checking :((
        public static void main(String[] args) throws Exception {

                final ParameterTool params = ParameterTool.fromArgs(args);

                final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
                env.getConfig().setGlobalJobParameters(params);

                // TODO: make available through Parameter tool
                final int numValues = params.getInt("numValues");
                final long windowSize = params.getLong("windowSize", 10); // windowSize in Seconds

                Tuple typeTuple = Tuple.newInstance(numValues);
                typeTuple.setField(Long.valueOf(1), 0); // timestamp
                typeTuple.setField(Integer.valueOf(1), numValues - 1); // is_anomaly

                for (int i = 1; i < numValues - 1; i++) {
                        typeTuple.setField(Float.valueOf(1), i);
                }

                TypeInformation<Tuple> typeInfo = TypeExtractor.getForObject(typeTuple);

                // parse input text file
                DataStream<Tuple> dataStream = env.readTextFile(params.get("inputFile"))
                                .map(new MapFunction<String, Tuple>() {
                                        @Override
                                        public Tuple map(String str) throws Exception {
                                                String[] temp = str.split(",");

                                                Tuple t = Tuple.newInstance(temp.length + 1);

                                                t.setField(Long.parseLong(temp[0]), 0);
                                                t.setField(Integer.parseInt(temp[temp.length - 1]), temp.length - 1);
                                                for (int i = 1; i < temp.length - 1; i++) {
                                                        t.setField(Float.parseFloat(temp[i]), i);
                                                }

                                                return t;
                                        }
                                }).returns(typeInfo)
                                .assignTimestampsAndWatermarks(WatermarkStrategy
                                                .<Tuple>forBoundedOutOfOrderness(
                                                                Duration.ofSeconds(10))
                                                .withTimestampAssigner(
                                                                (event, timestamp) -> event.getField(0)))
                                .returns(typeInfo);

                // No keying done here
                // dataStream.windowAll(SlidingEventTimeWindows.of(Time.seconds(10),
                // Time.seconds(1)))
                // .process(new HttpRequestProcessAllWindowFunction())
                // .writeAsCsv(params.get("outputFile"));

                // dataStream
                // .writeAsCsv(params.get("outputFile"));
                // dataStream.windowAll(SlidingEventTimeWindows.of(Time.hours(10),
                // Time.hours(1)))
                // .process(new RemoteOnnxProcessAllWindowFunction())
                // .writeAsCsv(params.get("outputFile"));

                env.execute("DataStream Remote");
        }
}
