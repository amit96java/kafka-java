package com.amit.learn_kafka_stream.processor;

import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MatchingPunctuator implements Punctuator {

    private static final Logger logger =
            LoggerFactory.getLogger(MatchingPunctuator.class);

    private final KeyValueStore<String, String> orderStateStore;
    private final KeyValueStore<String, Long> userStateStore;
    private final Duration expirationTime;

    public MatchingPunctuator(KeyValueStore<String, String> orderStateStore,
                              KeyValueStore<String, Long> userStateStore,
                             Duration expirationTime) {
        this.expirationTime = expirationTime;
        this.orderStateStore = orderStateStore;
        this.userStateStore = userStateStore;
    }

    @Override
    public void punctuate(long timestamp) {
        Instant now = Instant.ofEpochMilli(timestamp);
        Instant threshold = now.minus(expirationTime);
        logger.info("punctuator started...");
        try (KeyValueIterator<String, String> iterator = orderStateStore.all()) {
            while(iterator.hasNext()) {
                KeyValue<String, String> entry = iterator.next();
                logger.info("order store: key : "+entry.key+" value : "+entry.value);
            }
        }

        logger.info("fetching user details from state store...");
        try (KeyValueIterator<String, Long> iterator =  userStateStore.all()) {
            while(iterator.hasNext()) {
                KeyValue<String, Long> entry = iterator.next();
                logger.info("user store: key : "+entry.key+" value : "+entry.value);
            }
        }
    }
}
