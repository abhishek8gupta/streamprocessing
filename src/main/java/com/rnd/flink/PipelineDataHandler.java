package com.rnd.flink;

import java.util.Arrays;
import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.coders.KvCoder;
import org.apache.beam.sdk.coders.SerializableCoder;
import org.apache.beam.sdk.coders.StringUtf8Coder;
import org.apache.beam.sdk.io.kafka.KafkaIO;
import org.apache.beam.sdk.metrics.Counter;
import org.apache.beam.sdk.metrics.Metrics;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.Values;
import org.apache.beam.sdk.transforms.windowing.AfterProcessingTime;
import org.apache.beam.sdk.transforms.windowing.AfterWatermark;
import org.apache.beam.sdk.transforms.windowing.FixedWindows;
import org.apache.beam.sdk.transforms.windowing.Window;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.joda.time.Duration;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.GroupByKey;
import org.apache.beam.sdk.transforms.ParDo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PipelineDataHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(PipelineDataHandler.class);
    final static Counter counter = Metrics.counter("stats", "event-times");

    public static void main(String[] args) {
        DataPipelineOptions dataPipelineOptions = PipelineOptionsFactory.fromArgs(args).withValidation()
                .as(DataPipelineOptions.class);
        Pipeline pipeline = Pipeline.create(dataPipelineOptions);

        counter.inc();

        LOGGER.info("dataPipelineOptions.getBootstrapServers(): {}, {}", dataPipelineOptions.getBootstrapServers(),
                dataPipelineOptions.getInputTopic());

        try {

            PCollection<KV<String, InputData>> pCollection = pipeline.apply(KafkaIO.readBytes()
                    .withBootstrapServers(dataPipelineOptions.getBootstrapServers())
                    .withTopics(Arrays.asList(dataPipelineOptions.getInputTopic()))
                    .withKeyDeserializer(ByteArrayDeserializer.class)
                    .withValueDeserializer(ByteArrayDeserializer.class)
                    .withTimestampPolicyFactory((tp, previousWatermark) -> new CustomFieldTimePolicy(previousWatermark))
                    .withoutMetadata())
                    .apply(Values.<byte[]>create())
                    .apply(ParDo.of(new DoFn<byte[], KV<String, InputData>>() {
                        @ProcessElement
                        public void processElement(@Element byte[] data, ProcessContext c) {
                            try {
                                String dataStr = new String(data);
                                InputData inputData = getInputData(dataStr);
                                LOGGER.info("recieved : {}", inputData);
                                KV<String, InputData> kv = KV.of(inputData.getId(), inputData);
                                LOGGER.debug("KV: {}", kv);
                                c.output(kv);
                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }
                        }
                    }));

            PCollection<KV<String, Iterable<InputData>>> outputData = toEventWindows(pCollection);

            LOGGER.info("calling kafka writer");
            outputData.apply("writeToKafka", ParDo.of(new KakaWriter(dataPipelineOptions)));
            pipeline.run();
        } catch (Exception ex) {
            LOGGER.error("failed to deploy. error: {}", ex.getMessage(), ex);
        }
    }

    public static PCollection<KV<String, Iterable<InputData>>> toEventWindows(
            PCollection<KV<String, InputData>> inputs) {
        return inputs
                .apply("ApplySlidingWindows",
                        Window.<KV<String, InputData>>into(FixedWindows.of(Duration.standardSeconds(300)))
                                // .every(Duration.standardSeconds(60)))
                                .triggering(AfterWatermark.pastEndOfWindow()
                                        .withLateFirings(AfterProcessingTime.pastFirstElementInPane()
                                                .plusDelayOf(Duration.standardSeconds(1))))
                                .withAllowedLateness(Duration.standardSeconds(1)).discardingFiredPanes())
                .setCoder(KvCoder.of(StringUtf8Coder.of(), SerializableCoder.of(InputData.class)))
                .apply("GroupById", GroupByKey.create());
    }

    public static InputData getInputData(String data) {
        try {
            String[] fields = data.split(",");
            LOGGER.debug("data: {}, fields: {}", data, fields);

            if (fields.length != 6) {
                return null;
            }

            LOGGER.debug("fields length: {}", fields.length);

            long ts = Long.valueOf(fields[0]);
            String id = fields[1];
            String name = fields[2];
            int score = Integer.valueOf(fields[3]);
            int age = Integer.valueOf(fields[4]);
            String gender = fields[5];

            LOGGER.debug("ts: {}", ts);
            InputData inputData = new InputData.Builder(id)
                    .atTimestamp(ts)
                    .withName(name)
                    .withAge(age)
                    .withScore(score)
                    .withGender(gender)
                    .build();

            LOGGER.debug("inputdata: {}", inputData);

            return inputData;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

}
