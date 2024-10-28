package com.rnd.flink;

import java.util.Optional;

import org.apache.beam.sdk.io.kafka.KafkaRecord;
import org.apache.beam.sdk.io.kafka.TimestampPolicy;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomFieldTimePolicy extends TimestampPolicy<byte[], byte[]>{
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomFieldTimePolicy.class);
    protected Instant currentWatermark;

    public CustomFieldTimePolicy(Optional<Instant> previousWatermark){
        currentWatermark = previousWatermark.orElse(BoundedWindow.TIMESTAMP_MIN_VALUE);
    }

    @Override
    public Instant getTimestampForRecord(PartitionContext ctx, KafkaRecord<byte[], byte[]> record) {
        String value = new String(record.getKV().getValue());
        Instant instant = new Instant();
        Long eventTime = getTimestamp(value);
        currentWatermark = instant.withMillis(eventTime);
        LOGGER.debug("record: {}", record.getKV().getValue());
        return currentWatermark;
    }

    @Override
    public Instant getWatermark(PartitionContext ctx) {
        return currentWatermark;
    }

    public Long getTimestamp(String data){
        String[] values = data.split(",");
        LOGGER.info("size: " + values.length);;
        return Long.valueOf(values[0].trim());
    }
    
}
