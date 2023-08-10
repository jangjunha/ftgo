package me.jangjunha.ftgo.common.protobuf;

import com.google.protobuf.Timestamp;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TimestampUtilsTest {

    private static final Timestamp TS_EPOCH_0 = Timestamp.newBuilder().setSeconds(0).setNanos(0).build();
    private static final OffsetDateTime DT_EPOCH_0 = OffsetDateTime.parse("1970-01-01T00:00Z");

    private static final Timestamp TS_DEBUT = Timestamp.newBuilder().setSeconds(1673074883).setNanos(456789000).build();
    private static final OffsetDateTime DT_DEBUT = OffsetDateTime.parse("2023-01-07T16:01:23.456789+09:00");

    @Test
    void fromTimestamp() {
        assertTrue(DT_EPOCH_0.isEqual(TimestampUtils.fromTimestamp(TS_EPOCH_0)));
        assertTrue(DT_DEBUT.isEqual(TimestampUtils.fromTimestamp(TS_DEBUT)));
    }

    @Test
    void toTimestamp() {
        assertEquals(TS_EPOCH_0, TimestampUtils.toTimestamp(DT_EPOCH_0));
        assertEquals(TS_DEBUT, TimestampUtils.toTimestamp(DT_DEBUT));
    }
}
