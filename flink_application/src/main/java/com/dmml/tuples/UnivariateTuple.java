package com.dmml.tuples;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.flink.api.java.tuple.Tuple3;

public class UnivariateTuple
        extends Tuple3<Long, Float, Integer> {

    public static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public UnivariateTuple(String datetimeString, Float value, Integer y) throws ParseException {
        super(format.parse(datetimeString).getTime(), value, y);

    }

    public UnivariateTuple() {
        // empty constructor required when subclassing Tuple
    }

}
