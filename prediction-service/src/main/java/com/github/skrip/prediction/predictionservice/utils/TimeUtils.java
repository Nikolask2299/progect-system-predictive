package com.github.skrip.prediction.predictionservice.utils;

import com.google.protobuf.Timestamp;

import java.time.Instant;

public class TimeUtils {

    public static Timestamp convertToTimestamp(Instant instant) {
       return Timestamp.newBuilder()
                .setSeconds(instant.getEpochSecond())
                .setNanos(instant.getNano())
                .build();
    }

    public static Instant convertToInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

}
