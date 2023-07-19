package me.jangjunha.ftgo.common.protobuf;

import com.google.protobuf.Timestamp;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class TimestampUtils {
    public static OffsetDateTime fromTimestamp(Timestamp ts) {
        return Instant.ofEpochSecond(ts.getSeconds(), ts.getNanos()).atOffset(ZoneOffset.UTC);
    }

    public static Timestamp toTimestamp(OffsetDateTime dt) {
        Instant instant = dt.toInstant();
        return Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();
    }
}
