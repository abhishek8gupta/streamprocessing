package com.rnd.flink;

import java.util.Iterator;
import java.util.Map;

import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.windowing.BoundedWindow;
import org.apache.beam.sdk.values.KV;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ser.std.ByteArraySerializer;
import com.google.common.collect.ImmutableBiMap;

public class KakaWriter extends DoFn<KV<String, Iterable<InputData>>, Void> {
    private String topic;
    private Map<String, Object> config;
    private transient KafkaProducer<byte[], byte[]> producer = null;
    private static final Logger LOGGER = LoggerFactory.getLogger(KakaWriter.class);

    public KakaWriter(DataPipelineOptions pipelineOptions){
        try {
            this.topic = pipelineOptions.getOutputTopic();
            this.config = ImmutableBiMap.<String, Object>of (
                "bootstrap.servers", pipelineOptions.getBootstrapServers(),
                "key.serializer", ByteArraySerializer.class.getName(),
                "value.serializer", ByteArraySerializer.class.getName()
            );
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @StartBundle
    public void startBundle() throws Exception {
        try {
            if(producer == null){
                producer = new KafkaProducer<>(config);
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    @Teardown
    public void teardown() throws Exception {
        producer.flush();
        producer.close();
    }

    @ProcessElement
    public void processElement(ProcessContext ctx, final BoundedWindow window) throws Exception {
        try {
            KV<String, Iterable<InputData>> data = ctx.element();
            LOGGER.info("sending to kafka: {}, {}", topic, data);
            Iterable<InputData> events = data.getValue();
            Iterator<InputData> eventIterator = events.iterator();
            while(eventIterator.hasNext()){
                send(data.getKey(), eventIterator.next(), window);
            }
        }catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public void send(String key, InputData inputData, final BoundedWindow window){
        LOGGER.info("{} : triggerring windows -------------> key: {}, value: {}", window.maxTimestamp(), key, inputData.getName());
        ProducerRecord<byte[], byte[]> record = new ProducerRecord<byte[], byte[]>(topic, 1, key.getBytes(), inputData.getName().getBytes());
        producer.send(record, new Callback() {
            @Override
            public void onCompletion(RecordMetadata rm, Exception exception){
                LOGGER.info("onCompletion: {}, {}", rm, exception);
            }
        });
    }
}
