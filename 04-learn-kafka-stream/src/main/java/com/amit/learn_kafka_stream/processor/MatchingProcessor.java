package com.amit.learn_kafka_stream.processor;

import com.amit.learn_kafka_stream.dto.UserOrderDto;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class MatchingProcessor implements Processor<String, String, String, String> {
    private static final Logger logger =
            LoggerFactory.getLogger(MatchingProcessor.class);

    private final String userNameFilterStoreName;
    private final String userCountStoreName;
    private final String userListStoreName;

    private ProcessorContext<String, String> context;
    private KeyValueStore<String, String> userNameFilterStateStore;
    private KeyValueStore<String, Long> userCountStateStore;
    private KeyValueStore<String, List<UserOrderDto>> userListStateStore;

    public MatchingProcessor(String userNameFilterStoreName, String userCountStoreName, String userListStoreName) {
        this.userNameFilterStoreName = userNameFilterStoreName;
        this.userCountStoreName = userCountStoreName;
        this.userListStoreName = userListStoreName;
    }

    @Override
    public void init(ProcessorContext<String, String> context) {
        Processor.super.init(context);
        this.context = context;
        this.userNameFilterStateStore = context.getStateStore(userNameFilterStoreName);
        this.userCountStateStore =  context.getStateStore(userCountStoreName);
        this.userListStateStore = context.getStateStore(userListStoreName);
    }

    @Override
    public void process(Record<String, String> record) {
        String userName = record.key();
        String orderName = record.value();

        List<UserOrderDto> batch = userListStateStore.get("batch");
        if (batch == null) {
            logger.info("creating new batch list");
            batch = new ArrayList<>();
        }
        batch.add(new UserOrderDto(userName, orderName));
        userListStateStore.put("batch", batch);

        // 🔥 update user count (String -> Long store)
        Long currentCount = userCountStateStore.get(userName);
        if (currentCount == null) {
            currentCount = 0L;
        }
        userCountStateStore.put(userName, currentCount + 1);

        if (userName.startsWith("a")) {
            logger.info("adding in state store " + userName);
            userNameFilterStateStore.put(userName + "dlt", orderName);
        } else {
            logger.info("forwarding to another topic " + userName);
            context.forward(new Record<>(userName + ":forward", orderName, record.timestamp()));
        }

    }

    @Override
    public void close() {
        Processor.super.close();
    }
}
