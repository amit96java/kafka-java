package com.amit.learn_kafka_stream.config;


import com.amit.learn_kafka_stream.processor.MatchingProcessor;
import com.amit.learn_kafka_stream.processor.MatchingPunctuator;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.PunctuationType;
import org.apache.kafka.streams.processor.StateStore;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.ProcessorSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.config.KafkaStreamsConfiguration;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Configuration
public class OrderCountStreamConfig {

    public String ORDER_COUNT_STORE_NAME = "order-count-store";
    public String USER_COUNT_STORE_NAME = "user-count-store";
    public String INPUT_TOPIC = "orders-topic";
    public String OUTPUT_TOPIC = "order-count-topic";
    public String applicationId = "order-count-app";


    @Bean
    public Properties kafkaStreamsProperties() {
        Properties properties = new Properties();
        properties.put(StreamsConfig.APPLICATION_ID_CONFIG, applicationId);
        properties.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

        return properties;
    }

    @Bean
    public Topology topology() {
        StreamsBuilder builder = new StreamsBuilder();

        StoreBuilder<KeyValueStore<String, String>> orderCountStore = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(ORDER_COUNT_STORE_NAME),
                Serdes.String(),
                Serdes.String()
        );

        StoreBuilder<KeyValueStore<String, Long>> userCountStore =
                Stores.keyValueStoreBuilder(
                        Stores.persistentKeyValueStore(USER_COUNT_STORE_NAME),
                        Serdes.String(),
                        Serdes.Long()
                );

        builder.addStateStore(orderCountStore);
        builder.addStateStore(userCountStore);

        builder.stream(INPUT_TOPIC, Consumed.with(Serdes.String(), Serdes.String()))
//                .selectKey((userName, orderName) -> userName+"-"+orderName)
                .process( createProcessorSupplier(), ORDER_COUNT_STORE_NAME, USER_COUNT_STORE_NAME)
                .to(OUTPUT_TOPIC, Produced.with(Serdes.String(), Serdes.String()));

        return builder.build();

    }

    private ProcessorSupplier<String, String, String, String> createProcessorSupplier() {
        return () -> new MatchingProcessor(ORDER_COUNT_STORE_NAME, USER_COUNT_STORE_NAME) {

            public void init(ProcessorContext<String, String> context) {
                super.init(context);

                // Schedule punctuator
                KeyValueStore<String, String> stateStore = context.getStateStore(ORDER_COUNT_STORE_NAME);
                KeyValueStore<String, Long> userStore =
                        context.getStateStore(USER_COUNT_STORE_NAME);

                context.schedule(
                        Duration.ofSeconds(8),
                        PunctuationType.WALL_CLOCK_TIME,
                        new MatchingPunctuator(stateStore, userStore,  Duration.ofSeconds(20))
                );
            }
        };
    }

    @Bean(initMethod = "start", destroyMethod = "close")
    public KafkaStreams kafkaStreams(Topology topology, Properties kafkaStreamsProperties) {
        KafkaStreams streams = new KafkaStreams(topology, kafkaStreamsProperties);

        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));

        return streams;
    }

}

