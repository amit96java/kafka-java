package com.amit.learn_kafka_stream.processor;

import com.amit.learn_kafka_stream.dto.UserOrderDto;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.processor.Punctuator;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.KeyValueStore;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MatchingPunctuator implements Punctuator {

    private static final Logger logger =
            LoggerFactory.getLogger(MatchingPunctuator.class);

    private final KeyValueStore<String, String> userCountByOrderStateStore;
    private final KeyValueStore<String, Long> userNameFilterStateStore;
    private final KeyValueStore<String, List<UserOrderDto>> userListStateStore;
    private final Duration expirationTime;

    public MatchingPunctuator(KeyValueStore<String, String> userCountByOrderStateStore,
                              KeyValueStore<String, Long> userNameFilterStateStore,
                             KeyValueStore<String, List<UserOrderDto>> userListStateStore,
                             Duration expirationTime) {
        this.expirationTime = expirationTime;
        this.userCountByOrderStateStore = userCountByOrderStateStore;
        this.userNameFilterStateStore = userNameFilterStateStore;
        this.userListStateStore = userListStateStore;
    }

    @Override
    public void punctuate(long timestamp) {
        Instant now = Instant.ofEpochMilli(timestamp);
        Instant threshold = now.minus(expirationTime);
        logger.info(" ");
        logger.info("punctuator started...");
        logger.info("fetching user count by order details from state store...");
        try (KeyValueIterator<String, String> iterator = userCountByOrderStateStore.all()) {
            while (iterator.hasNext()) {
                KeyValue<String, String> entry = iterator.next();
                logger.info("order store: key : " + entry.key + " value : " + entry.value);
            }
        }

        logger.info("fetching filtered user by name details from state store...");
        try (KeyValueIterator<String, Long> iterator = userNameFilterStateStore.all()) {
            while (iterator.hasNext()) {
                KeyValue<String, Long> entry = iterator.next();
                logger.info("user store: key : " + entry.key + " value : " + entry.value);
            }
        }

        logger.info("fetching list of user from state store...");
        try (KeyValueIterator<String, List<UserOrderDto>> iterator = userListStateStore.all()) {
            while (iterator.hasNext()) {
                KeyValue<String, List<UserOrderDto>> entry = iterator.next();
                logger.info("user list store: key : " + entry.key + " value : " + entry.value);
            }
        }
    }
}
