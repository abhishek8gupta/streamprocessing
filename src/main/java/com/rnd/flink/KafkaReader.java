package com.rnd.flink;

import java.util.Map;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer;
import com.google.common.collect.ImmutableMap;

public class KafkaReader extends DoFn<byte[], String>{
    private final String topic;
    private final Map<String, Object> config;
    private transient KafkaConsumer<byte[], String> consumer = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaReader.class);
    
    public KafkaReader(DataPipelineOptions options){
        this.topic = options.getOutputTopic();
        this.config = ImmutableMap.<String, Object>of(
                "bootstrap.servers", options.getBootstrapServers(),
                "key.serializer", ByteArraySerializer.class.getName(),
                "value.serializer", ByteArraySerializer.class.getName()
        );

        consumer = new KafkaConsumer<>(config);
    }

    @ProcessElement
    public void processElement(ProcessContext pctx) throws Exception{
        ConsumerRecords<byte[], String> records = consumer.poll(5);
        for(ConsumerRecord<byte[], String> record : records){
            pctx.output(record.value());
        }
    }
}
